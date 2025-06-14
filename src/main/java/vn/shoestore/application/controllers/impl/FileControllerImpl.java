package vn.shoestore.application.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.shoestore.application.controllers.IFileController;
import vn.shoestore.application.response.BaseResponse;
import vn.shoestore.shared.factory.ResponseFactory;
import vn.shoestore.usecases.file.IUploadFileUseCase;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileControllerImpl implements IFileController {
  private final IUploadFileUseCase iUploadFileUseCase;

  @Override
  public ResponseEntity<BaseResponse<List<String>>> upload(MultipartFile[] files)
      throws IOException {
    return ResponseFactory.success(iUploadFileUseCase.uploadFile(files));
  }
}
