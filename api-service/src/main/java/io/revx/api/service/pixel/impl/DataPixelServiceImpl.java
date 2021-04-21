package io.revx.api.service.pixel.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;
import io.revx.api.mysql.amtdb.repo.DataPixelsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.pixel.DataPixelService;
import io.revx.api.service.pixel.PixelUtils;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.pixel.PixelAdvDTO;
import io.revx.core.utils.HashUtil;

@Component
public class DataPixelServiceImpl implements DataPixelService{

  private static Logger logger = LogManager.getLogger(DataPixelServiceImpl.class);
  
  @Autowired
  DataPixelsRepository dataPixelRepository;
  
  @Autowired
  PixelUtils pixelUtils;
  
  /** The login user details service. */
  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  
  @Autowired
  AdvertiserToPixelRepository advertiserToPixelRepository;
  
  @Override
  public DataPixelDTO createDataPixel(DataPixelDTO pixel) {
    logger.debug("Inside createDataPixel to create pixel for : {}", pixel);
    DataPixelsEntity dataPixelsEntity = new DataPixelsEntity();
    pixelUtils.updateDbPixel(dataPixelsEntity, pixel);
    dataPixelsEntity.setStatus(Status.ACTIVE);
    DataSourceType dst = pixel.getSourceType();
    dataPixelsEntity.setSourceType(dst == null ? DataSourceType.PIXEL_LOG : dst);
    dataPixelsEntity.setLiceseeId(loginUserDetailsService.getLicenseeId());
    dataPixelRepository.save(dataPixelsEntity);
    
    logger.debug("Inside createDataPixel. Create dataPixelsEntity : {}. Updating hash of that pixel.", dataPixelsEntity);
    // Create a hash of pixelId and update it in the database entry
    dataPixelsEntity.setHash(HashUtil.getMD5Hash(String.valueOf(dataPixelsEntity.getId())));
    dataPixelRepository.save(dataPixelsEntity);
    logger.debug("Inside createDataPixel. Updated hash for dataPixelsEntity : {}", dataPixelsEntity);
    DataPixelDTO savedPixel = pixelUtils.getVoFromTuple(dataPixelsEntity);
    
    /*
     * try { int responseCode = pixelCreatorClient.updatePixels(savedPixel.getId()); if
     * (HttpStatus.OK.value() == responseCode)
     * LOGGER.info("Successfully informed upixel about pixel Id: " + savedPixel.getId()); else
     * LOGGER.error("Response code: " + responseCode + " while informed upixel about pixel Id: " +
     * savedPixel.getId()); } catch (Exception e) {
     * LOGGER.error("PixelCreatorClient responded with error " +
     * "while sending notification for new pixel ID: " + savedPixel.getId(), e); }
     */
    return savedPixel;
}

  @Override
  public DataPixelDTO saveDataPixel(DataPixelDTO pixel) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DataPixelDTO modifyDataPixelStatus(Integer pixelId, Status status) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public DataPixelDTO getDataPixel(Long id) throws ApiException {
    logger.info("Inside getDataPixel method to get pixel details for id : "+id);
    if(id == null)
      throw new ApiException("Pixel id is null/emtpy");
    
    Optional<DataPixelsEntity> dataPixelsEntity = dataPixelRepository.findById(id);
    if(!dataPixelsEntity.isPresent())
      throw new ApiException("Pixel id is invalid");
    DataPixelDTO dataPixelDTO = pixelUtils.getVoFromTuple(dataPixelsEntity.get());
    return dataPixelDTO;
  }

  @Override
  public List<DataPixelDTO> getAllDataPixelsForLicensee() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PixelAdvDTO> getAllDataPixels() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PixelAdvDTO> getAllDataPixels(int startIndex, int pageSize) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<DataPixelDTO> getAllDataPixelsForLicensee(int startIndex, int pageSize) {
    // TODO Auto-generated method stub
    return null;
  }

  @Transactional
  @Override
  public Long createAdvertiserToPixel(BaseModel advertiser, DataSourceType sourceType) {
    logger.debug("Inside createAdvertiserToPixel to return pixel id. advertiser : {}, sourceType : {}", advertiser, sourceType);
    boolean astPixel = DataSourceType.PIXEL_LOG.equals(sourceType);
    AdvertiserToPixelEntity advertiserToPixel;
    if (astPixel) {
        advertiserToPixel = advertiserToPixelRepository.findByStatusAndAdvertiserId(Status.ACTIVE, advertiser.getId());
        if (advertiserToPixel != null) {
            return advertiserToPixel.getPixelId();
        }
    }
    
    DataPixelDTO pixel = pixelUtils.newPixelInstance(advertiser);
    pixel.setSourceType(sourceType);
    pixel = createDataPixel(pixel);

    // create advertiser pixel map here
    advertiserToPixel = new AdvertiserToPixelEntity();

    advertiserToPixel.setAdvertiserId(advertiser.getId());
    advertiserToPixel.setPixelId(pixel.getId());
    if (astPixel) {
        advertiserToPixel.setStatus(Status.ACTIVE);
        advertiserToPixel.setIsAutoUpdate(1L);
    } else {
        advertiserToPixel.setStatus(Status.INACTIVE);
        advertiserToPixel.setIsAutoUpdate(0L);

    }
    advertiserToPixel.setDateCreated(new Timestamp(System.currentTimeMillis()));
    
    advertiserToPixelRepository.save(advertiserToPixel);
    
    logger.debug("Create new record in advertiser pixel table with advertiser {} and pixel {}", advertiser,
        pixel.getId());
    return pixel.getId();
  }

  @Override
  public AdvertiserToPixelEntity createAndGetAdvertiserToPixel(BaseModel advertiser, DataSourceType sourceType) {
    boolean astPixel = DataSourceType.PIXEL_LOG.equals(sourceType);
    AdvertiserToPixelEntity advertiserToPixel;
    if (astPixel) {
        advertiserToPixel = advertiserToPixelRepository.findByStatusAndAdvertiserId(Status.ACTIVE, advertiser.getId());
        if (advertiserToPixel != null) {
            return advertiserToPixel;
        }
    }
    // Create a tag AKA Slm Pixel and attach it to the segment
    DataPixelDTO pixel = pixelUtils.newPixelInstance(advertiser);
    pixel.setSourceType(sourceType);
    pixel = createDataPixel(pixel);

    createDataPixel(pixel);
    
    // create advertiser pixel map here
    advertiserToPixel = new AdvertiserToPixelEntity();

    advertiserToPixel.setAdvertiserId(advertiser.getId());
    advertiserToPixel.setPixelId(pixel.getId());
    if (astPixel) {
        advertiserToPixel.setStatus(Status.ACTIVE);
        advertiserToPixel.setIsAutoUpdate(1L);
    } else {
        advertiserToPixel.setStatus(Status.INACTIVE);
        advertiserToPixel.setIsAutoUpdate(0L);

    }
    advertiserToPixelRepository.save(advertiserToPixel);
    
    logger.debug("Create new record in advertiser pixel table with advertiser {} and pixel {}", advertiser,
        pixel.getId());
    return advertiserToPixel;
  }
  
  @Override
  public AdvertiserToPixelEntity getAdvertiserToPixel(Long pixelId) {
    logger.debug("Inside getAdvertiserToPixel to get mapping for pixel id {}", pixelId);
    
    return advertiserToPixelRepository.findByPixelId(pixelId);
   
  }
}
