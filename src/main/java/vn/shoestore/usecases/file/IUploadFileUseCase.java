package vn.shoestore.usecases.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUploadFileUseCase {
  List<String> uploadFile(MultipartFile[] multipartFiles) throws IOException;
}
