package vn.shoestore.usecases.file.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import vn.shoestore.domain.service.S3Service;
import vn.shoestore.shared.anotation.UseCase;
import vn.shoestore.usecases.file.IUploadFileUseCase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class UploadFileUseCaseImpl implements IUploadFileUseCase {
  private final S3Service service;
  private static final String FILE_TOPIC = "file_topic/";

  @Value("${vn.shoe-store.minio.bucket}")
  private String bucket;

  @Override
  public List<String> uploadFile(MultipartFile[] multipartFiles) throws IOException {
    List<String> results = new ArrayList<>();
    for (MultipartFile file : multipartFiles) {
      String timestamp =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
      String originalFilename = file.getOriginalFilename();
      String fileExtension = "";
      if (originalFilename != null) {
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex >= 0 && extensionIndex < originalFilename.length() - 1) {
          fileExtension = originalFilename.substring(extensionIndex + 1);
        }
      }

      String key = FILE_TOPIC + timestamp + "." + fileExtension;
      results.add(service.uploadFile(bucket, key, file));
    }

    return results;
  }
}
