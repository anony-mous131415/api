package io.revx.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.controller.audience.AudienceController;
import io.revx.api.utility.Util;
import io.revx.core.constant.Constants;
import io.revx.core.enums.CompressionType;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.FileModel;
import io.revx.core.model.creative.FileType;
import io.revx.core.response.ApiListResponse;

@Component
public class FileUploadService {

  private static Logger logger = LogManager.getLogger(AudienceController.class);

  @Autowired
  ApplicationProperties properties;

  
  public ApiListResponse<FileModel> uploadAudienceFile(MultipartFile[] uploadingFiles)
      throws IOException, ValidationException {

    if (uploadingFiles == null || uploadingFiles.length == 0)
      throw new ValidationException(ErrorCode.BAD_REQUEST,
          new Object[] {Constants.MSG_NO_FILE_IN_UPLOAD});

    ApiListResponse<FileModel> response = new ApiListResponse<>();
    List<FileModel> uploadedFiles = new ArrayList<>();
    
    String finalDirectoryPath = properties.getAudienceDirectoryPath();
    
    for (MultipartFile f : uploadingFiles) {
      logger.debug("filename : === > {}", f.getOriginalFilename());
      Util.createDirectory(finalDirectoryPath);
      String pathName = finalDirectoryPath  + f.getOriginalFilename();
      //Util.createFile(pathName);
      File file = new File(pathName);
      f.transferTo(file);
      logger.debug("File is saved to {} ", pathName);
      FileModel fileModel = populateFileInfo(file);
      validate(fileModel);

      if (!fileModel.getErrorMsg().equals(Constants.Creative_Success)) {
        throw new ValidationException(ErrorCode.BAD_REQUEST,
            new Object[] {fileModel.getErrorMsg()});
      }
      
      String finalFilePath = finalDirectoryPath+Util.getmd5Hex(fileModel.getLocalFileLocation());
      Util.deleteFile(finalFilePath);
      Path temp = Files.move 
          (Paths.get(fileModel.getLocalFileLocation()),  
          Paths.get(finalFilePath)); 
    
          if(temp != null) { 
            logger.debug("File renamed and moved successfully"); 
          } 
          else{ 
            logger.debug("Failed to move the file"); 
          } 
          fileModel.setLocalFileLocation(finalFilePath);
      uploadedFiles.add(fileModel);
    }
    logger.debug("Final list of files are : {} ", uploadedFiles);
    response.setData(uploadedFiles);
    response.setTotalNoOfRecords(uploadedFiles.size());
    return response;
  }
  

  private FileModel populateFileInfo(File f) throws IOException {
    FileModel fileUploaded = new FileModel();
    fileUploaded.setName(f.getName());
    /*
     * URL file Location is required if download option is given on UI. We can set it : http://atom.crm.net/<file location>
     */
    fileUploaded.setLocalFileLocation(f.getAbsolutePath());
    fileUploaded.setSize(Util.readableFileSize(f.length()));
    fileUploaded.setOriginFileName(f.getName());
    fileUploaded.setFileType(FileType.getFileType(Files.probeContentType(f.toPath())));
    logger.debug("File type  :   {}", fileUploaded.getFileType());
    logger.debug("File uploaded : {} ", fileUploaded);
    return fileUploaded;
  }

  private void validate(FileModel fileModel) {
    /*
     * Only single file/record should be present inside compressed file. Can make configurable if required.
     */
    Boolean oneFilePerRecord = true;
    String compressedDirectoryPath = properties.getCompressedDirectoryPath();
    
    if(fileModel.getFileType().equals(FileType.TEXT) || fileModel.getFileType().equals(FileType.CSV)) {
      fileModel.setCompressionType(CompressionType.NONE);
      fileModel.setErrorMsg(Constants.MSG_SUCCESS);
      return;
    }
    
    if(fileModel.getFileType().equals(FileType.GZIP)) {
      fileModel.setCompressionType(CompressionType.GZIP);
      fileModel.setErrorMsg(Constants.MSG_SUCCESS);
      return;
    }
    
    if(fileModel.getFileType().equals(FileType.ZIP)) {
      int childRecords = Util.getChildRecordsOfZipFile(fileModel.getLocalFileLocation(), compressedDirectoryPath);
      if(childRecords <= 0)
        fileModel.setErrorMsg(Constants.MSG_NO_FILE_IN_ZIP);
      if(oneFilePerRecord && childRecords != 1)
        fileModel.setErrorMsg(Constants.MSG_MULTIPLE_FILE_IN_ZIP);
      
      fileModel.setCompressionType(CompressionType.ZIP);
      fileModel.setErrorMsg(Constants.MSG_SUCCESS);
      return;
    }
      
    fileModel.setErrorMsg(Constants.INVALID_FILE_TYPE);
  }
  
}
