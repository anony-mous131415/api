/*
 * @author: ranjan-pritesh
 * 
 * @date:2 jan 2020
 */
package io.revx.api.service.creative;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Global;
import io.humble.video.MediaDescriptor.Type;
import io.revx.api.config.ApplicationProperties;
import io.revx.core.constant.Constants;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.VideoProperties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.revx.api.utility.Util.validateFilenameInDir;

/**
 * The Class CreativeUtil.
 */
@Component
public class CreativeFileDetailsUtil {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CreativeFileDetailsUtil.class);


  /** The properties. */
  @Autowired
  ApplicationProperties properties;

  /**
   * Sets the file details.
   *
   * @param f the f
   * @param fileUploaded the file uploaded
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void fetchFileDetails(File f, CreativeFiles fileUploaded) throws IOException {
    // zip
    if (fileUploaded.getContentType() != null
        && fileUploaded.getContentType().split("/")[1].equals("zip"))
      fetchZipFileDetails(f, fileUploaded);

    // image file
    if (fileUploaded.getContentType() != null
        && fileUploaded.getContentType().split("/")[0].equals("image"))
      fetchImageFileDetails(f, fileUploaded, fileUploaded.getContentType());

    // video
    if (fileUploaded.getContentType() != null
        && fileUploaded.getContentType().split("/")[0].equals("video"))
      fetchVideoFileDetails(f, fileUploaded);

  }



  /**
   * Fetch video file details.
   *
   * @param f the f
   * @param fileUploaded the file uploaded
   */
  private void fetchVideoFileDetails(File f, CreativeFiles fileUploaded) {
    Demuxer demuxer = Demuxer.make();
    try {
      demuxer.open(f.getPath(), null, false, true, null, null);
    } catch (InterruptedException e) {
      logger.error("Error reading video metadata: " + f.getAbsolutePath(), e);
    } catch (IOException e) {
      logger.error("Error reading video metadata: " + f.getAbsolutePath(), e);
    }

    VideoProperties video = new VideoProperties();
    if (demuxer.getDuration() != Global.NO_PTS) {
      double duration = (float) demuxer.getDuration() / 1000.0 / 1000.0;
      long formattedDuration = Math.round(duration * 100) / 100;
      video.setDurationInSec(formattedDuration);
    }

    if (demuxer.getBitRate() > 0) {
      int bitrate = demuxer.getBitRate() / 1000;
      if (bitrate > Integer.valueOf(properties.getCreativeVideoBitrateLimitInKbps()))
        fileUploaded.setErrorMsg(Constants.INVALID_VIDEO_BITRATE);
      video.setBitRate(bitrate + "kb/s");
    }

    fileUploaded.setVideoAttribute(video);

    try {
      for (int i = 0; i < demuxer.getNumStreams(); i++) {
        DemuxerStream stream = demuxer.getStream(i);
        Decoder coder = stream.getDecoder();
        if (coder.getCodecType() == Type.MEDIA_VIDEO) {
          fileUploaded.setHeight(coder.getHeight());
          fileUploaded.setWidth(coder.getWidth());
        }
      }
      demuxer.close();
    } catch (InterruptedException e) {
      logger.error("Error reading video metadata: " + f.getAbsolutePath(), e);
    } catch (IOException e) {
      logger.error("Error reading video metadata: " + f.getAbsolutePath(), e);
    }

  }



  /**
   * Fetch image file details.
   *
   * @param f the f
   * @param fileUploaded the file uploaded
   * @param contentType the content type
   */
  private void fetchImageFileDetails(File f, CreativeFiles fileUploaded, String contentType) {

    if (StringUtils.isBlank(contentType))
      return;

    Iterator<ImageReader> iter = ImageIO.getImageReadersByMIMEType(contentType);
    while (iter.hasNext()) {
      ImageReader reader = iter.next();
      try {
        ImageInputStream stream = new FileImageInputStream(f);
        reader.setInput(stream);
        int width = reader.getWidth(reader.getMinIndex());
        int height = reader.getHeight(reader.getMinIndex());
        fileUploaded.setHeight(height);
        fileUploaded.setWidth(width);
      } catch (IOException e) {
        logger.debug("Error/Exception reading image metadata for file : {}. Fallback to another method. Exception is : " + f.getAbsolutePath(), e.getMessage());
        try {
          BufferedImage bimg = ImageIO.read(f);
          fileUploaded.setHeight(bimg.getWidth());
          fileUploaded.setWidth(bimg.getHeight());
        }catch (Exception e1) {
          logger.error("Error reading image metadata in fallback: " + f.getAbsolutePath(), e1);
        }
      } finally {
        reader.dispose();
      }
    }
  }



  /**
   * Fetch zip file details.
   *
   * @param f the f
   * @param fileUploaded the file uploaded
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void fetchZipFileDetails(File f, CreativeFiles fileUploaded) throws IOException {

    ZipFile zipFile = new ZipFile(f.getPath());
    boolean hasHtml = false;
    String destn = properties.getUnzipDirectoryForCreative();
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    File entryDestination = null;
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      entryDestination = new File(destn, entry.getName());
      String fileName = validateFilenameInDir(entryDestination,destn);
      if (entry.isDirectory()) {
        new File(fileName).mkdirs();
      } else if (entry.getName().contains(".html")) {
        hasHtml = true;
        entryDestination.getParentFile().mkdirs();
        InputStream in = zipFile.getInputStream(entry);
        OutputStream out = new FileOutputStream(entryDestination);
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        out.close();
        break;
      }
    }
    if (!hasHtml)
      fileUploaded.setErrorMsg(Constants.NO_HTML_IN_ZIP);

    zipFile.close();
  }



  /**
   * Readable file size.
   *
   * @param size the size
   * @return the string
   */
  public String readableFileSize(long size) {
    if (size <= 0)
      return "0";
    final String[] units = new String[] {"B", "kB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
        + units[digitGroups];
  }


}
