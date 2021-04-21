/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.ns.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.FileUploadService;
import io.revx.api.service.creative.CreativeService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.Constants;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.FileModel;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.response.ApiListResponse;

@RestController
public class FileUploadController {

  private static Logger logger = LogManager.getLogger(FileUploadController.class);

  @Autowired
  CreativeService creativeService;

  @Autowired
  FileUploadService fileUploadService;
  
  /**
   * Upload files.
   *
   * @param uploadingFiles the uploading files
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.UPLOAD)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.UPLOAD)
  @PostMapping(path = ApiConstant.upload + ApiConstant.creative, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiListResponse<CreativeFiles>> uploadFiles(
      @RequestPart("file") MultipartFile[] uploadingFiles) throws Exception {
    logger.info("Uploading creative Files........ no Of files : {}", uploadingFiles.length);
    ApiListResponse<CreativeFiles> response = creativeService.upload(uploadingFiles);
    return ResponseEntity.ok().body(response);
  }

  
  
  
  
  /**
   * Upload audience file.
   *
   * @param uploadingFiles the uploading files
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.UPLOAD)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.UPLOAD)
  @PostMapping(path = ApiConstant.AUDIENCE_UPLOAD, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiListResponse<FileModel>> uploadAudienceFile(
      @RequestPart("file") MultipartFile[] uploadingFiles) throws Exception {
    logger.info("Uploading audience Files........ no Of files : {}", uploadingFiles.length);
    
    if(uploadingFiles.length > 1) {
      throw new ValidationException(ErrorCode.BAD_REQUEST,
          new Object[] {Constants.MSG_MULTIPLE_FILE_IN_UPLOAD});
    }
    
    ApiListResponse<FileModel> response = fileUploadService.uploadAudienceFile(uploadingFiles);
    return ResponseEntity.ok().body(response);
  }

}
