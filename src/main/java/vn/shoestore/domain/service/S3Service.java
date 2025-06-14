package vn.shoestore.domain.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.shoestore.shared.utils.FileUtils;

import java.io.*;
import java.net.URLConnection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class S3Service {
  @Getter private final String stableBucket;
  private final AmazonS3 client;

  public S3Service(AmazonS3 client, @Value("${vn.shoe-store.minio.bucket}") String stableBucket) {
    this.client = client;
    this.stableBucket = stableBucket;
  }

  public String generateFileUrl(String bucket, String key, Date expired) {
    if (expired == null) {
      expired = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));
    }

    return client.generatePresignedUrl("", bucket + "/" + key, expired).toString();
  }

  public String generateFileUrl(String key, Date expired) {
    return generateFileUrl(stableBucket, key, expired);
  }

  public String generateFileUrl(String key) {
    return generateFileUrl(key, null);
  }

  public String generateFileUrlExist(String bucket, String key, Date expired) {
    if (!objectExists(bucket, key)) {
      return key;
    }
    return generateFileUrl(bucket, key, expired);
  }

  public String uploadFile(String key, MultipartFile file) throws IOException {
    return uploadFile(stableBucket, key, file);
  }

  public InputStreamResource downloadFile(String key) {
    return downloadFile(stableBucket, key);
  }

  public String uploadFile(String bucket, String key, MultipartFile file) throws IOException {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(file.getSize());
    objectMetadata.setContentType(file.getContentType());
    var putObjectRequest =
        new PutObjectRequest("", bucket + "/" + key, file.getInputStream(), objectMetadata);
    client.putObject(putObjectRequest);
    return client.getUrl(bucket, key).toString();
  }

  public String uploadFileByFile(String bucket, String fileName, InputStream inputStream)
      throws IOException {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    String contentType = URLConnection.guessContentTypeFromStream(inputStream);
    if (contentType == null) {
      contentType = Mimetypes.getInstance().getMimetype(fileName);
    }
    objectMetadata.setContentType(contentType);
    var putObjectRequest =
        new PutObjectRequest("", bucket + "/" + fileName, inputStream, objectMetadata);
    client.putObject(putObjectRequest);
    return client.getUrl(bucket, fileName).toString();
  }

  public void deleteFile(String key) {
    client.deleteObject(stableBucket, key);
  }

  public Boolean deleteFiles(String bucketName, String keyPath) {
    try {
      for (S3ObjectSummary file : client.listObjects(bucketName, keyPath).getObjectSummaries()) {
        client.deleteObject(bucketName, file.getKey());
      }
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private InputStreamResource downloadFile(String bucket, String key) {
    try {
      if (!objectExists(bucket, key)) {
        return null;
      }
      S3Object s3Object = client.getObject(bucket, key);
      return new InputStreamResource(s3Object.getObjectContent());
    } catch (AmazonServiceException e) {
      throw new RuntimeException("Error downloading file from S3", e);
    }
  }

  private boolean objectExists(String bucket, String key) {
    try {
      S3Object object = client.getObject(bucket, key);
      object.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String generatePresignedUrl(
      String bucketName, String objectKey, long expirationInMinutes) {
    Date expiration = new Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += expirationInMinutes * 60 * 1000;
    expiration.setTime(expTimeMillis);

    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

    return client.generatePresignedUrl(generatePresignedUrlRequest).toString();
  }

  public File downloadFileFromS3(
      String s3Key, String folderName, String folderChildName, String keyFolder) {
    try {
      S3Object s3Object = client.getObject(new GetObjectRequest(stableBucket, s3Key));
      InputStream inputStream = s3Object.getObjectContent();
      String key = s3Key.substring(s3Key.lastIndexOf("/") + 1, s3Key.length());

      String tempDir = System.getProperty("java.io.tmpdir");

      FileUtils.createFolder(folderName);
      File folderDir = new File(tempDir + File.separator + folderName);

      if (folderChildName != null) {
        FileUtils.createFolder(folderName + File.separator + folderChildName);
        folderDir =
            new File(tempDir + File.separator + folderName + File.separator + folderChildName);
        if (keyFolder != null) {
          FileUtils.createFolder(
              folderName + File.separator + folderChildName + File.separator + keyFolder);
          folderDir =
              new File(
                  tempDir
                      + File.separator
                      + folderName
                      + File.separator
                      + folderChildName
                      + File.separator
                      + keyFolder);
        }
      }

      // Create a temporary file
      // File tempDir = new File(System.getProperty("java.io.tmpdir"));
      File tempFile = new File(folderDir, key);

      // Write the input stream to the temporary file
      try (OutputStream outputStream = new FileOutputStream(tempFile)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, length);
        }
      }

      return tempFile;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
