package vn.shoestore.application.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.shoestore.application.response.BaseResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file/")
public interface IFileController {

  @PostMapping(value = "upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<BaseResponse<List<String>>> upload(@RequestPart("files[]") MultipartFile[] files)
      throws IOException;
}
