package io.revx.api.service.pixel;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.controller.audience.AudienceController;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.core.model.BaseModel;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.pixel.PixelAdvDTO;

@Component
public class PixelUtils {


  @Autowired
  AdvertiserToPixelRepository advertiserToPixelRepository;
  
  private static Logger logger = LogManager.getLogger(AudienceController.class);
  

  public DataPixelDTO newPixelInstance(BaseModel advertiser) {
    DataPixelDTO pixel = new DataPixelDTO();
    String pxlName =
        advertiser.getName() + "_Pixel_" + Long.toString(System.currentTimeMillis() / 1000);
    String description = pxlName + "_description";
    pixel.setName(pxlName);
    pixel.setDescription(description);
    pixel.setActive(true);
    logger.debug("Inside newPixelInstance. Returning DataPixelDTO : {}",pixel);
    return pixel;
  }
  
  /**
   * updates the dbPixel object with fields which are user editable (As this
   * method is being used by create and save both hence fields which are not
   * editable should not be changed here)
   * 
   * @param dataPixelsEntity
   * @param dataPixelDTO
   */
  public void updateDbPixel(DataPixelsEntity dataPixelsEntity, DataPixelDTO dataPixelDTO) {
      dataPixelsEntity.setDescription(dataPixelDTO.getDescription());
      dataPixelsEntity.setName(dataPixelDTO.getName());
      // Pixel Status can't be chaned through save/create
      // dataPixelsEntity.setStatus(dataPixelDTO.getStatus());
      dataPixelsEntity.setPartners(dataPixelDTO.getPartners());
  }

  public PixelAdvDTO getVoFromTuple(DataPixelsEntity dataPixelsEntity) {
    PixelAdvDTO pixelAdvDTO = new PixelAdvDTO();
    pixelAdvDTO.setDescription(dataPixelsEntity.getDescription());
    pixelAdvDTO.setId(dataPixelsEntity.getId());
    pixelAdvDTO.setName(dataPixelsEntity.getName());
    pixelAdvDTO.setActive(dataPixelsEntity.getStatus().equals(Status.ACTIVE) ? true : false);
    pixelAdvDTO.setPartners(dataPixelsEntity.getPartners());
    pixelAdvDTO.setHash(dataPixelsEntity.getHash());
    pixelAdvDTO.setLicenseeId(dataPixelsEntity.getLiceseeId());
    pixelAdvDTO.setSourceType(dataPixelsEntity.getSourceType());
    
    // Segment Ids
    List<Long> segmentIds = new ArrayList<Long>();
    /*if (dataPixelsEntity.getSegments() != null)
        for (SegmentDto dto : dataPixelsEntity.getSegments()) {
            segmentIds.add(dto.getId());
        }*/
    pixelAdvDTO.setSegmentIds(segmentIds);
    return pixelAdvDTO;
}
}
