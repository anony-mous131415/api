package io.revx.core.model.creative;

import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.StatusTimeModel;

import java.util.List;

public class CreativeDTO extends StatusTimeModel implements BaseEntity {

  private static final long serialVersionUID = 1L;

  private Size size;
  private String content;
  private ClickDestination clickDestination;
  private CreativeType type;
  private FileType contentType;
  private BaseModel advertiser;
  private String previewUrl;
  private String urlPath;
  private List<VideoAttributes> videoAttributes;
  private NativeAssetPojo nativeAsset;
  private String errorMsg;
  private VideoUploadType videoUploadType;
  private VastCreative vastCreative;
  private String thirdPartyAdTag;
  private boolean dcoAd;
  private boolean nativeAd;
  private boolean isRefactored;
  private DcoAttributesDTO dcoAttributes;
  private CreativePerformanceData performanceData;
  private String originalFileName;
  private Long advertiserId;
  private String dynamicItemList;
  private boolean isTemplateBased;

  public CreativeDTO() {}

  public CreativeDTO(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  @Override
  public Long getId() {
    return super.getId();
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ClickDestination getClickDestination() {
    return clickDestination;
  }

  public void setClickDestination(ClickDestination clickDestination) {
    this.clickDestination = clickDestination;
  }

  public CreativeType getType() {
    return type;
  }

  public FileType getContentType() {
    return contentType;
  }

  public void setContentType(FileType contentType) {
    this.contentType = contentType;
  }

  public void setType(CreativeType type) {
    this.type = type;
  }

  public BaseModel getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(BaseModel advertiser) {
    this.advertiser = advertiser;
    if(advertiser != null) {
    	setAdvertiserId(advertiser.getId());
    }
  }

  public String getPreviewUrl() {
    return previewUrl;
  }

  public void setPreviewUrl(String previewUrl) {
    this.previewUrl = previewUrl;
  }

  public String getUrlPath() {
    return urlPath;
  }

  public void setUrlPath(String urlPath) {
    this.urlPath = urlPath;
  }

  public List<VideoAttributes> getVideoAttributes() {
    return videoAttributes;
  }

  public void setVideoAttributes(List<VideoAttributes> videoAttributes) {
    this.videoAttributes = videoAttributes;
  }

  public NativeAssetPojo getNativeAsset() {
    return nativeAsset;
  }

  public void setNativeAsset(NativeAssetPojo nativeAsset) {
    this.nativeAsset = nativeAsset;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public VideoUploadType getVideoUploadType() {
    return videoUploadType;
  }

  public void setVideoUploadType(VideoUploadType videoUploadType) {
    this.videoUploadType = videoUploadType;
  }

  public VastCreative getVastCreative() {
    return vastCreative;
  }

  public void setVastCreative(VastCreative vastCreative) {
    this.vastCreative = vastCreative;
  }

  public String getThirdPartyAdTag() {
    return thirdPartyAdTag;
  }

  public void setThirdPartyAdTag(String thirdPartyAdTag) {
    this.thirdPartyAdTag = thirdPartyAdTag;
  }

  public boolean isDcoAd() {
    return dcoAd;
  }

  public void setDcoAd(boolean dcoAd) {
    this.dcoAd = dcoAd;
  }

  public boolean isNativeAd() {
    return nativeAd;
  }

  public void setNativeAd(boolean nativeAd) {
    this.nativeAd = nativeAd;
  }

  public DcoAttributesDTO getDcoAttributes() {
    return dcoAttributes;
  }

  public void setDcoAttributes(DcoAttributesDTO dcoAttributes) {
    this.dcoAttributes = dcoAttributes;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }


  public boolean isRefactored() {
    return isRefactored;
  }

  public void setRefactored(boolean isRefactored) {
    this.isRefactored = isRefactored;
  }

  public CreativePerformanceData getPerformanceData() {
    return performanceData;
  }

  public void setPerformanceData(CreativePerformanceData performanceData) {
    this.performanceData = performanceData;
  }

  public String getOriginalFileName() {
    return originalFileName;
  }

  public void setOriginalFileName(String originalFileName) {
    this.originalFileName = originalFileName;
  }

  public Long getAdvertiserId() {
	return advertiserId;
}

  public void setAdvertiserId(Long advertiserId) {
	this.advertiserId = advertiserId;
}

  public String getDynamicItemList() {
    return dynamicItemList;
  }

  public void setDynamicItemList(String dynamicItemList) {
    this.dynamicItemList = dynamicItemList;
  }

  public boolean isTemplateBased() {
    return isTemplateBased;
  }

  public void setTemplateBased(boolean templateBased) {
    isTemplateBased = templateBased;
  }

  @Override
  public String toString() {
    return "CreativeDTO{" +
            "size=" + size +
            ", content='" + content + '\'' +
            ", clickDestination=" + clickDestination +
            ", type=" + type +
            ", contentType=" + contentType +
            ", advertiser=" + advertiser +
            ", previewUrl='" + previewUrl + '\'' +
            ", urlPath='" + urlPath + '\'' +
            ", videoAttributes=" + videoAttributes +
            ", nativeAsset=" + nativeAsset +
            ", errorMsg='" + errorMsg + '\'' +
            ", videoUploadType=" + videoUploadType +
            ", vastCreative=" + vastCreative +
            ", thirdPartyAdTag='" + thirdPartyAdTag + '\'' +
            ", dcoAd=" + dcoAd +
            ", nativeAd=" + nativeAd +
            ", isRefactored=" + isRefactored +
            ", dcoAttributes=" + dcoAttributes +
            ", performanceData=" + performanceData +
            ", originalFileName='" + originalFileName + '\'' +
            ", advertiserId=" + advertiserId +
            ", dynamicItemList='" + dynamicItemList + '\'' +
            ", isTemplateBased=" + isTemplateBased +
            '}';
  }


}
