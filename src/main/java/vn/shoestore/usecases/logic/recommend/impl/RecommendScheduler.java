package vn.shoestore.usecases.logic.recommend.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class RecommendScheduler {

  private static final String PROJECT_ROOT = System.getProperty("user.dir");

  private static final String COMPUTE_PREF_SCRIPT =
      PROJECT_ROOT + File.separator + "ai" + File.separator + "scripts" + File.separator + "compute_user_preferences.py";

  private static final String GENERATE_REC_SCRIPT =
      PROJECT_ROOT + File.separator + "ai" + File.separator + "scripts" + File.separator + "generate_recommendations.py";

  // Chạy lúc 02:00 hằng ngày
  @Scheduled(cron = "0 0 2 * * *")
  public void runDailyJobs() {
    log.info("AI scheduler start at {}", LocalDateTime.now());
    runScript(COMPUTE_PREF_SCRIPT);
    runScript(GENERATE_REC_SCRIPT);
    log.info("AI scheduler done at {}", LocalDateTime.now());
  }

  private void runScript(String scriptPath) {
    try {
      ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
      pb.directory(new File(PROJECT_ROOT));
      pb.redirectErrorStream(true);
      Process process = pb.start();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("[AI-Job] {}", line);
        }
      }
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        log.error("Script {} exited with code {}", scriptPath, exitCode);
      } else {
        log.info("Script {} completed successfully", scriptPath);
      }
    } catch (Exception e) {
      log.error("Failed to run script {}", scriptPath, e);
    }
  }
}


