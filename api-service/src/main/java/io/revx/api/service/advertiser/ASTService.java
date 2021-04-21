/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */

package io.revx.api.service.advertiser;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.Status;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.pixel.PixelUtils;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.constant.Constants;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ValidationException;

@Component
public class ASTService {

  private static final Logger logger = LoggerFactory.getLogger(ASTService.class);

  /** The advertiser to pixel repo. */
  @Autowired
  AdvertiserToPixelRepository advertiserToPixelRepo;

  /** The advertiser repo. */
  @Autowired
  AdvertiserRepository advertiserRepo;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  ApplicationProperties applicationProperties;

  @Autowired
  PixelUtils pixelUtils;
  
  @Autowired
  DataPixelServiceImpl dataPixelService;
  
  @Autowired
  EntityESService elasticSearch;

  /**
   * This method generate and upload ast.js into cdn
   * 
   * @throws ApiException
   * @throws IOException
   */
  public Boolean generateAndUploadAstTagToCdn(Long advertiserId) throws ApiException, IOException {
    String astTemplateStr;
    String pixelHash = "djfgvsaydawdjjsakdbiasjhdfutasv";
    File astScriptsDirectory = null;
    Boolean result = false;

    AdvertiserToPixelEntity advertiserToPixel = dataPixelService.createAndGetAdvertiserToPixel(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserId), DataSourceType.PIXEL_LOG);

    if (advertiserToPixel != null && advertiserToPixel.getStatus().equals(Status.ACTIVE)
        && (advertiserToPixel.getIsAutoUpdate() & 0x1) != 0) {

      File versionFile =
          new File(new StringBuilder(applicationProperties.getSmartTagOriginDirectory())
              .append("/templates/js/version").toString());
      String version = FileUtils.readFileToString(versionFile);
      version = version.replace("\n", "");
      File astTemplateFile =
          new File(new StringBuilder(applicationProperties.getSmartTagOriginDirectory())
              .append("/templates/js/template_ast_").append(version).append(".js").toString());

      astTemplateStr = FileUtils.readFileToString(astTemplateFile);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      String astTagString = astTemplateStr
          .replace(Constants.PIXEL_TMPL, Long.toString(advertiserToPixel.getPixelId()))
          .replace(Constants.PIXEL_HASH_TMPL, pixelHash).replace(Constants.VERSION_TMPL, version)
          .replace(Constants.DATE_TMPL, sdf.format(new Date()));


      logger.trace("Atom Smart tag for Advertiser id : {} and tagString : {}", advertiserId,
          astTagString);

      astScriptsDirectory = new File(
          applicationProperties.getSmartTagScriptDirectory() + File.separator + advertiserId);
      astScriptsDirectory.mkdirs();
      logger.trace("created directory advertiser {} specific ast scripts directory path: {}",
          advertiserId, astScriptsDirectory.getAbsolutePath());

      File destFile = new File(astScriptsDirectory.getAbsolutePath() + "/ast.js");
      FileUtils.writeStringToFile(destFile, astTagString);

    } else {
      logger.warn("Advertiser {} Pixel {} Inactive or isUpdate is false", advertiserToPixel.getId(),
          advertiserToPixel.getPixelId());
    }

    if (astScriptsDirectory != null)
      result = true;

    return result;

  }


  /**
   * This method generate and update ast for all advertisers.
   * 
   * @throws IOException
   * @throws ApiException
   */
  public Boolean generateAndUploadAst() throws ApiException, IOException {
    List<AdvertiserEntity> advertiserList = advertiserRepo
        .findAllByLicenseeId(loginUserDetailsService.getUserInfo().getSelectedLicensee().getId());
    if (advertiserList == null) {
      throw new ValidationException("advetiser list is null");
    }
    for (AdvertiserEntity advertiser : advertiserList) {
      List<AdvertiserToPixelEntity> advertiserToPixelList =
          advertiserToPixelRepo.findAllByAdvertiserId(advertiser.getId());

      if (advertiserToPixelList != null && advertiserToPixelList.size() > 0) {
        for (AdvertiserToPixelEntity advertiserToPixel : advertiserToPixelList) {
          generateAndUploadAstTagToCdn(advertiserToPixel.getAdvertiserId());
        }
      }

    }
    return true;

  }

}


