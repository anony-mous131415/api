package io.revx.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"application.properties"})
public class ApplicationProperties {

  @Value("${fileDownloadDomain:http://download.atomex.net}")
  private String fileDownloadDomain;

  @Value("${downloadFilePath:/atom/data}")
  private String downloadFilePath;

  @Value("${defaultSort:modifiedTime-}")
  private String defaultSort;

  @Value("${api.ehcache.time-to-live-in-minutes:4320}")
  private Long ehcacheTTL;

  @Value("${slmServiceUrl}")
  private String slmServiceUrl;

  @Value("${smartTagOriginDirectory}")
  private String smartTagOriginDirectory;

  @Value("${smartTagScriptDirectory}")
  private String smartTagScriptDirectory;

  @Value("${timePeriodForSuccessRate:51840000}")
  private Long timePeriodForSuccessRate;

  @Value("${imageTrackerCodeTemplate}")
  private String imageTrackerCodeTemplate;

  @Value("${jsTrackerCodeTemplate}")
  private String jsTrackerCodeTemplate;

  @Value("${appTrackerCodeTemplate}")
  private String appTrackerCodeTemplate;

  @Value("${trackerAppurl}")
  private String trackerAppurl;

  @Value("${dmpUri:gcpbeta-mdmp.mtraction.com/segment/_sapi}")
  private String dmpUri;

  @Value("${dmpSyncUri:mdmp-api.mtraction.com/segment/syncsegment}")
  private String dmpSyncUri;
  
  @Value("${dmpToken:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9}")
  private String dmpToken;

  @Value("${dmpAid:1}")
  private String dmpAid;

  @Value("${creativeVideoBitrateLimitInKbps:512}")
  private String creativeVideoBitrateLimitInKbps;

  @Value("${temporaryCreativeDirectory:'/atom/origin/cr_temp/'}")
  private String temporaryCreativeDirectory;

  @Value("${CreativeUrlPrepend:http://origin.atomex.net/creatives/}")
  private String creativeUrlPrepend;

  @Value("${CreativeUrlPrependTemp:http://origin.atomex.net/}")
  private String creativeUrlPrependTemp;

  @Value("${temporaryCreativeDirectoryPath:/atom/origin/cr_temp/}")
  private String temporaryCreativeDirectoryPath;

  @Value("${LogoUrlPrependTemp:https://cdn.atomex.net/}")
  private String logoUrlPrependTemp;

  @Value("${supportedVideoCreativeSize:320x480,480x320,1024x768,768x1024}")
  private String supportedCreativeSize;

  @Value("${supportedNativeAspectRatio:16:9,9:16,1:1}")
  private String supportedNativeAspectRatio;

  @Value("${supportedDcoNativeImageSize:1200x627,1280x720,1200x800}")
  private String supportedDcoNativeImageSize;


  @Value("${UnzipDirectoryForCreative:/atom/origin/unzip/}")
  private String unzipDirectoryForCreative; 
  
  @Value("${CreativeDirectoryPath:/atom/origin/}")
  private String creativeDirectoryPath;
  
  @Value("${creativesDirectory:creatives/}")
  private String creativesDirectory;
  
  @Value("${temporaryAudienceDirectoryPath:/atom/crm/}")
  private String audienceDirectoryPath;
  
  @Value("${compressedDirectoryPath:/atom/crm/compressed/}")
  private String compressedDirectoryPath; 
  
  @Value("${s3host:revx-segments.s3.amazonaws.com}")
  private String s3host; 
  
  @Value("${s3SegmentBucketUrlPath:s3://revx-segments/}")
  private String s3SegmentBucketUrlPath;

  @Value("${creative.performance.table:creative_main}")
  private String creativePerformanceTable;

  @Value("${spring.cloud.gcp.credentials.location:/atom/gcp/Revx-5572eed4dbe6.json}")
  private String gcsCredentialsLocation;

  @Value("${gcp.project-id:revx-259410}")
  private String gccProjectId;

  @Value("${reporting.source.dataSet:prod}")
  private String reportingSourceDataSet;

  @Value("${reporting.temporary.datasetName:revx_reports}")
  private String reportingDestinationDataSet;

  @Value("${reporting.temporary.table.timetolive:1800}")
  private String tempTableTimeToLive;

  @Value("${reporting.export.bucket.location:gs://revx-advanced-reporting-poc/advancedReports/}")
  private String reportingExportBucket;

  @Value("${reportDownloadPath:https://storage.googleapis.com/revx-advanced-reporting-poc/advancedReports}")
  private String reportDownloadPath;

  @Value("${FallBackUrlStatic:https://cdn.atomex.net/fallback/FBAndroid.html?package=<fill_bundle_id>&dl=<fill_single_encoded_deeplink_with_utm>&fb=<fill_singleencoded_fallbacklink_with_utm>}")
  private String fallBackUrlStatic;

  @Value("${FallBackUrlDynamic:https://cdn.atomex.net/fallback/FBAndroid.html?package=<fill_bundle_id_of_the_app_here>&dl=__ENCODED_PAGE_LINK__&fb=<fill_singleencoded_fallbacklink_with_utm>}")
  private String fallBackUrlDynamic;
  @Value("${videocreative.host.protocol:http}")
  private String videoCreativeHostProtocol;

  @Value("${FallBackUrl:https://cdn.atomex.net/fallback/FBAndroid.html?package=<fill bundle id>&dl=<fill singleencoded Deeplink with utm>&fb=<fill singleencoded Fallbacklink with utm>}")
  private String fallBackUrl;

  @Value("${skad.campaign.count:1}")
  private String skadCampaignCount;

  @Value("${skad.strategy.count:100}")
  private String skadStrategyCount;

  @Value("${skad.settings.file.location:/atom/origin/skad/settings.json}")
  private String skadSettingsFileLocation;

  @Value("${allowAudienceTargetingOnSkadStrategy:false}")
  private String allowAudienceTargetingForSkad;

  @Value("${AppsFlyerPartnerPullKey_Value}")
  private String partnerPullKey_Value;

  @Value("${AppsFlyerPartnerPullKey_Key:{{PARTNER_PULL_KEY}}")
  private String partnerPullKey_Key;

  public String getDmpSyncUri() {
    return dmpSyncUri;
  }

  public void setDmpSyncUri(String dmpSyncUri) {
    this.dmpSyncUri = dmpSyncUri;
  }

  public String getDmpAid() {
    return dmpAid;
  }

  public void setDmpAid(String dmpAid) {
    this.dmpAid = dmpAid;
  }

  public String getDmpUri() {
    return dmpUri;
  }

  public void setDmpUri(String dmpUri) {
    this.dmpUri = dmpUri;
  }

  public String getDmpToken() {
    return dmpToken;
  }

  public void setDmpToken(String dmpToken) {
    this.dmpToken = dmpToken;
  }
  
  public String getS3SegmentBucketUrlPath() {
    return s3SegmentBucketUrlPath;
  }

  public void setS3SegmentBucketUrlPath(String s3SegmentBucketUrlPath) {
    this.s3SegmentBucketUrlPath = s3SegmentBucketUrlPath;
  }

  public String getS3host() {
    return s3host;
  }

  public void setS3host(String s3host) {
    this.s3host = s3host;
  }

  public String getAudienceDirectoryPath() {
    return audienceDirectoryPath;
  }

  public void setAudienceDirectoryPath(String audienceDirectoryPath) {
    this.audienceDirectoryPath = audienceDirectoryPath;
  }

  public String getCompressedDirectoryPath() {
    return compressedDirectoryPath;
  }

  public void setCompressedDirectoryPath(String compressedDirectoryPath) {
    this.compressedDirectoryPath = compressedDirectoryPath;
  }

  public Long getEhcacheTTLInMillis() {
    return ehcacheTTL * 60000;
  }

  public void setEhcacheTTLInMillis(Long ehcacheTTL) {
    this.ehcacheTTL = ehcacheTTL;
  }

  public void setFileDownloadDomain(String fileDownloadDomain) {
    this.fileDownloadDomain = fileDownloadDomain;
  }

  public void setDownloadFilePath(String downloadFilePath) {
    this.downloadFilePath = downloadFilePath;
  }

  public void setDefaultSort(String defaultSort) {
    this.defaultSort = defaultSort;
  }

  public String getFileDownloadDomain() {
    return fileDownloadDomain;
  }

  public String getDefaultSort() {
    return defaultSort;
  }

  public String getDownloadFilePath() {
    return downloadFilePath;
  }

  public String getSlmServiceUrl() {
    return slmServiceUrl;
  }

  public void setSlmServiceUrl(String slmServiceUrl) {
    this.slmServiceUrl = slmServiceUrl;
  }

  public String getSmartTagOriginDirectory() {
    return smartTagOriginDirectory;
  }

  public void setSmartTagOriginDirectory(String smartTagOriginDirectory) {
    this.smartTagOriginDirectory = smartTagOriginDirectory;
  }

  public String getSmartTagScriptDirectory() {
    return smartTagScriptDirectory;
  }

  public void setSmartTagScriptDirectory(String smartTagScriptDirectory) {
    this.smartTagScriptDirectory = smartTagScriptDirectory;
  }

  public Long getTimePeriodForSuccessRate() {
    return timePeriodForSuccessRate;
  }

  public void setTimePeriodForSuccessRate(Long timePeriodForSuccessRate) {
    this.timePeriodForSuccessRate = timePeriodForSuccessRate;
  }

  public Long getEhcacheTTL() {
    return ehcacheTTL;
  }

  public void setEhcacheTTL(Long ehcacheTTL) {
    this.ehcacheTTL = ehcacheTTL;
  }

  public String getImageTrackerCodeTemplate() {
    return imageTrackerCodeTemplate;
  }

  public void setImageTrackerCodeTemplate(String imageTrackerCodeTemplate) {
    this.imageTrackerCodeTemplate = imageTrackerCodeTemplate;
  }

  public String getJsTrackerCodeTemplate() {
    return jsTrackerCodeTemplate;
  }

  public void setJsTrackerCodeTemplate(String jsTrackerCodeTemplate) {
    this.jsTrackerCodeTemplate = jsTrackerCodeTemplate;
  }

  public String getAppTrackerCodeTemplate() {
    return appTrackerCodeTemplate;
  }

  public void setAppTrackerCodeTemplate(String appTrackerCodeTemplate) {
    this.appTrackerCodeTemplate = appTrackerCodeTemplate;
  }

  public String getTrackerAppurl() {
    return trackerAppurl;
  }

  public void setTrackerAppurl(String trackerAppurl) {
    this.trackerAppurl = trackerAppurl;
  }

  public String getTemporaryCreativeDirectory() {
    return temporaryCreativeDirectory;
  }

  public void setTemporaryCreativeDirectory(String temporaryCreativeDirectory) {
    this.temporaryCreativeDirectory = temporaryCreativeDirectory;
  }

  public String getCreativeUrlPrepend() {
    return creativeUrlPrepend;
  }

  public void setCreativeUrlPrepend(String creativeUrlPrepend) {
    this.creativeUrlPrepend = creativeUrlPrepend;
  }

  public String getCreativeUrlPrependTemp() {
    return creativeUrlPrependTemp;
  }

  public void setCreativeUrlPrependTemp(String creativeUrlPrependTemp) {
    this.creativeUrlPrependTemp = creativeUrlPrependTemp;
  }

  public String getTemporaryCreativeDirectoryPath() {
    return temporaryCreativeDirectoryPath;
  }

  public void setTemporaryCreativeDirectoryPath(String temporaryCreativeDirectoryPath) {
    this.temporaryCreativeDirectoryPath = temporaryCreativeDirectoryPath;
  }

  public String getSupportedCreativeSize() {
    return supportedCreativeSize;
  }

  public void setSupportedCreativeSize(String supportedCreativeSize) {
    this.supportedCreativeSize = supportedCreativeSize;
  }

  public String getSupportedNativeAspectRatio() {
    return supportedNativeAspectRatio;
  }

  public void setSupportedNativeAspectRatio(String supportedNativeAspectRatio) {
    this.supportedNativeAspectRatio = supportedNativeAspectRatio;
  }


  public int getDefaultPlacementTargetingId() {
    // TODO Auto-generated method stub
    return 0;
  }
  public String getSupportedDcoNativeImageSize() {
    return supportedDcoNativeImageSize;
  }

  public void setSupportedDcoNativeImageSize(String supportedDcoNativeImageSize) {
    this.supportedDcoNativeImageSize = supportedDcoNativeImageSize;
  }

  public String getCreativeVideoBitrateLimitInKbps() {
    return creativeVideoBitrateLimitInKbps;
  }

  public void setCreativeVideoBitrateLimitInKbps(String creativeVideoBitrateLimitInKbps) {
    this.creativeVideoBitrateLimitInKbps = creativeVideoBitrateLimitInKbps;
  }

  public String getUnzipDirectoryForCreative() {
    return unzipDirectoryForCreative;
  }

  public void setUnzipDirectoryForCreative(String unzipDirectoryForCreative) {
    this.unzipDirectoryForCreative = unzipDirectoryForCreative;
  }

  public String getCreativeDirectoryPath() {
    return creativeDirectoryPath;
  }

  public void setCreativeDirectoryPath(String creativeDirectoryPath) {
    this.creativeDirectoryPath = creativeDirectoryPath;
  }

  public String getCreativesDirectory() {
    return creativesDirectory;
  }

  public void setCreativesDirectory(String creativesDirectory) {
    this.creativesDirectory = creativesDirectory;
  }

  public String getCreativePerformanceTable() {
    return creativePerformanceTable;
  }

  public void setCreativePerformanceTable(String creativePerformanceTable) {
    this.creativePerformanceTable = creativePerformanceTable;
  }

  public String getGcsCredentialsLocation() {
    return gcsCredentialsLocation;
  }

  public void setGcsCredentialsLocation(String gcsCredentialsLocation) {
    this.gcsCredentialsLocation = gcsCredentialsLocation;
  }

  public String getGccProjectId() {
    return gccProjectId;
  }

  public void setGccProjectId(String gccProjectId) {
    this.gccProjectId = gccProjectId;
  }

  public String getReportingSourceDataSet() {
    return reportingSourceDataSet;
  }

  public void setReportingSourceDataSet(String reportingSourceDataSet) {
    this.reportingSourceDataSet = reportingSourceDataSet;
  }

  public String getReportingDestinationDataSet() {
    return reportingDestinationDataSet;
  }

  public void setReportingDestinationDataSet(String reportingDestinationDataSet) {
    this.reportingDestinationDataSet = reportingDestinationDataSet;
  }

  public String getTempTableTimeToLive() {
    return tempTableTimeToLive;
  }

  public void setTempTableTimeToLive(String tempTableTimeToLive) {
    this.tempTableTimeToLive = tempTableTimeToLive;
  }

  public String getReportingExportBucket() {
    return reportingExportBucket;
  }

  public void setReportingExportBucket(String reportingExportBucket) {
    this.reportingExportBucket = reportingExportBucket;
  }

  public String getReportDownloadPath() {
    return reportDownloadPath;
  }

  public void setReportDownloadPath(String reportDownloadPath) {
    this.reportDownloadPath = reportDownloadPath;
  }


  public String getFallBackUrlStatic() {
    return fallBackUrlStatic;
  }

  public void setFallBackUrlStatic(String fbStatic) {
    this.fallBackUrlStatic = fbStatic;
  }

  //REVX-515 : dynamic s2s fallback
  public String getFallBackUrlDynamic() {
    return fallBackUrlDynamic;
  }

  //REVX-515 : dynamic s2s fallback
  public void setFallBackUrlDynamic(String fbDynamic) {
    this.fallBackUrlDynamic = fbDynamic;
  }

  public String getVideoCreativeHostProtocol() {
    return videoCreativeHostProtocol;
  }

  public void setVideoCreativeHostProtocol(String videoCreativeHostProtocol) {
    this.videoCreativeHostProtocol = videoCreativeHostProtocol;
  }

  public String getPartnerPullKey_Value() {
    return partnerPullKey_Value;
  }

  public void setPartnerPullKey_Value(String partnerPullKey_Value) {
    this.partnerPullKey_Value = partnerPullKey_Value;
  }

  public String getPartnerPullKey_Key() {
    return partnerPullKey_Key;
  }

  public void setPartnerPullKey_Key(String partnerPullKey_Key) {
    this.partnerPullKey_Key = partnerPullKey_Key;
  }

  public String getSkadCampaignCount() {
    return skadCampaignCount;
  }

  public void setSkadCampaignCount(String skadCampaignCount) {
    this.skadCampaignCount = skadCampaignCount;
  }

  public String getSkadStrategyCount() {
    return skadStrategyCount;
  }

  public void setSkadStrategyCount(String skadStrategyCount) {
    this.skadStrategyCount = skadStrategyCount;
  }

  public String getSkadSettingsFileLocation() {
    return skadSettingsFileLocation;
  }

  public void setSkadSettingsFileLocation(String skadSettingsFileLocation) {
    this.skadSettingsFileLocation = skadSettingsFileLocation;
  }

  public String getAllowAudienceTargetingForSkad() {
    return allowAudienceTargetingForSkad;
  }

  public void setAllowAudienceTargetingForSkad(String allowAudienceTargetingForSkad) {
    this.allowAudienceTargetingForSkad = allowAudienceTargetingForSkad;
  }

  public String getLogoUrlPrependTemp() {
    return logoUrlPrependTemp;
  }

  public void setLogoUrlPrependTemp(String logoUrlPrependTemp) {
    this.logoUrlPrependTemp = logoUrlPrependTemp;
  }

  @Override
  public String toString() {
    return "ApplicationProperties{" +
            "fileDownloadDomain='" + fileDownloadDomain + '\'' +
            ", downloadFilePath='" + downloadFilePath + '\'' +
            ", defaultSort='" + defaultSort + '\'' +
            ", ehcacheTTL=" + ehcacheTTL +
            ", slmServiceUrl='" + slmServiceUrl + '\'' +
            ", smartTagOriginDirectory='" + smartTagOriginDirectory + '\'' +
            ", smartTagScriptDirectory='" + smartTagScriptDirectory + '\'' +
            ", timePeriodForSuccessRate=" + timePeriodForSuccessRate +
            ", imageTrackerCodeTemplate='" + imageTrackerCodeTemplate + '\'' +
            ", jsTrackerCodeTemplate='" + jsTrackerCodeTemplate + '\'' +
            ", appTrackerCodeTemplate='" + appTrackerCodeTemplate + '\'' +
            ", trackerAppurl='" + trackerAppurl + '\'' +
            ", dmpUri='" + dmpUri + '\'' +
            ", dmpSyncUri='" + dmpSyncUri + '\'' +
            ", dmpToken='" + dmpToken + '\'' +
            ", dmpAid='" + dmpAid + '\'' +
            ", creativeVideoBitrateLimitInKbps='" + creativeVideoBitrateLimitInKbps + '\'' +
            ", temporaryCreativeDirectory='" + temporaryCreativeDirectory + '\'' +
            ", creativeUrlPrepend='" + creativeUrlPrepend + '\'' +
            ", creativeUrlPrependTemp='" + creativeUrlPrependTemp + '\'' +
            ", temporaryCreativeDirectoryPath='" + temporaryCreativeDirectoryPath + '\'' +
            ", logoUrlPrependTemp='" + logoUrlPrependTemp + '\'' +
            ", supportedCreativeSize='" + supportedCreativeSize + '\'' +
            ", supportedNativeAspectRatio='" + supportedNativeAspectRatio + '\'' +
            ", supportedDcoNativeImageSize='" + supportedDcoNativeImageSize + '\'' +
            ", unzipDirectoryForCreative='" + unzipDirectoryForCreative + '\'' +
            ", creativeDirectoryPath='" + creativeDirectoryPath + '\'' +
            ", creativesDirectory='" + creativesDirectory + '\'' +
            ", audienceDirectoryPath='" + audienceDirectoryPath + '\'' +
            ", compressedDirectoryPath='" + compressedDirectoryPath + '\'' +
            ", s3host='" + s3host + '\'' +
            ", s3SegmentBucketUrlPath='" + s3SegmentBucketUrlPath + '\'' +
            ", creativePerformanceTable='" + creativePerformanceTable + '\'' +
            ", gcsCredentialsLocation='" + gcsCredentialsLocation + '\'' +
            ", gccProjectId='" + gccProjectId + '\'' +
            ", reportingSourceDataSet='" + reportingSourceDataSet + '\'' +
            ", reportingDestinationDataSet='" + reportingDestinationDataSet + '\'' +
            ", tempTableTimeToLive='" + tempTableTimeToLive + '\'' +
            ", reportingExportBucket='" + reportingExportBucket + '\'' +
            ", reportDownloadPath='" + reportDownloadPath + '\'' +
            ", fallBackUrlStatic='" + fallBackUrlStatic + '\'' +
            ", fallBackUrlDynamic='" + fallBackUrlDynamic + '\'' +
            ", videoCreativeHostProtocol='" + videoCreativeHostProtocol + '\'' +
            ", fallBackUrl='" + fallBackUrl + '\'' +
            ", skadCampaignCount='" + skadCampaignCount + '\'' +
            ", skadStrategyCount='" + skadStrategyCount + '\'' +
            ", skadSettingsFileLocation='" + skadSettingsFileLocation + '\'' +
            ", allowAudienceTargetingForSkad='" + allowAudienceTargetingForSkad + '\'' +
            ", partnerPullKey_Value='" + partnerPullKey_Value + '\'' +
            ", partnerPullKey_Key='" + partnerPullKey_Key + '\'' +
            '}';
  }
}
