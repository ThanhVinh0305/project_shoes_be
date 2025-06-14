package vn.shoestore.shared.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
  public static void createFolder(String folderName) throws IOException {
    String tempDir = System.getProperty("java.io.tmpdir");

    File folder = new File(tempDir + File.separator + folderName);
    if (!folder.exists()) {
      boolean created = folder.mkdir();
      if (created) {
        System.out.println("Folder created successfully: " + folder.getAbsolutePath());
      } else {
        System.out.println("Failed to create folder at: " + folder.getAbsolutePath());
      }
    } else {
      System.out.println("Folder already exists: " + folder.getAbsolutePath());
    }
  }
}
