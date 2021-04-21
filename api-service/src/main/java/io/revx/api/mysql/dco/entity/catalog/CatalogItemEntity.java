package io.revx.api.mysql.dco.entity.catalog;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CatalogItem")
public class CatalogItemEntity implements  Serializable {
	
	private static final long serialVersionUID = 6434804357029960010L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ci_id", nullable = false)
	private Long  id; 	
	
	@Column(name = "ci_title", length = 250)
	private String title;
	
	@Column(name = "ci_description")
	@Type(type="text")
	private String description;
	
	@Column(name = "ci_adv_id", nullable = false)
	private Long advertiserId;
	
    @Column(name = "ci_feed_id", nullable = false)
	private Long feedId;
	
	@Column(name = "ci_sku", length = 50)
	private String stockKeepingUnit;
	
	@Column(name = "ci_business_type", length = 20)
	private String businessType;
	
	@Column(name = "ci_page_link", length = 1024)
	private String pageLink;
	
	@Column(name = "ci_default_image", length = 1024)
	private String defaultImage;
	
	@Column(name = "ci_additional_images")
	@Type(type="text")
	private String additionalImages;
	
	@Column(name = "ci_availability", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean availability;
	
	@Column(name = "ci_stock_quantity")
	private Long stockQuantity;
	
	@Column(name = "ci_original_price")
	private BigDecimal originalPrice;
	
	@Column(name = "ci_sale_price")
	private BigDecimal salePrice;
	
	@Column(name = "ci_offer_start_date")
	private Long offerStartDate;
	
	@Column(name = "ci_offer_end_date")
	private Long offerEndDate;
	
	@Column(name = "ci_discount")
	private BigDecimal discount;
	
	@Column(name = "ci_brand", length = 100)
	private String brand;
	
	@Column(name = "ci_gender", length = 20)
	private String gender;
	
	@Column(name = "ci_age_group", length = 50)
	private String ageGroup;
	
	@Column(name = "ci_color", length = 50)
	private String color;
	
	@Column(name = "ci_size", length = 20)
	private String size;
	
	@Column(name = "ci_material", length = 50)
	private String material;
	
	@Column(name = "ci_pattern", length = 50)
	private String pattern;
	
	@Column(name = "ci_source", length = 100)
	private String source;
	
	@Column(name = "ci_source_code", length = 50)
	private String sourceCode;
	
	@Column(name = "ci_destination", length = 100)
	private String destination;
	
	@Column(name = "ci_destination_code", length = 50)
	private String destinationCode;
	
	@Column(name = "ci_start_date")
	private Long startDate;
	
	@Column(name = "ci_end_date")
	private Long endDate;
	
	@Column(name = "ci_duration")
	private Integer duration;
	
	@Column(name = "ci_class", length = 50)
	private String classStr;
	
	@Column(name = "ci_data_state", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean dateState;

    @Column(name = "ci_offer_name")
    private String offerName;

    @Column(name = "ci_offer_desc")
    private String offerDesc;

    @Column(name = "ci_offer_amount")
    private BigDecimal offerAmount;

    @Column(name = "ci_offer_percent")
    private BigDecimal offerPercent;

    @Column(name = "ci_offer_coupon_code")
    private String offerCouponCode;

    @Column(name = "ci_best_price")
    private BigDecimal bestPrice;

    @Column(name = "ci_coupon_valid", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean couponValid;

    @Column(name = "ci_min_order_value")
    private BigDecimal minOrderValue;

    @Column(name = "ci_android_tablet_click_url")
    private String andTabletClickUrl;

    @Column(name = "ci_android_phone_click_url")
    private String andPhoneClickUrl;

    @Column(name = "ci_ios_tablet_click_url")
    private String iosTabletClickUrl;

    @Column(name = "ci_ios_phone_click_url")
    private String iosPhoneClickUrl;

    @Column(name = "ci_windows_tablet_click_url")
    private String winTabletClickUrl;

    @Column(name = "ci_windows_phone_click_url")
    private String winPhoneClickUrl;

    @Column(name = "ci_third_party_click_url")
    private String thirdPartyClickUrl;
    
    @Column(name = "ci_category_id", length = 50)
    private String categoryId;

    @Column(name = "ci_category_name", length = 250)
    private String categoryName;

    @Column(name = "ci_sub_category_id", length = 50)
    private String subCategoryId;

    @Column(name = "ci_sub_category_name", length = 250)
    private String subCategoryName;

    @Column(name = "ci_content_title", length = 250)
    private String contentTitle;

    @Column(name = "ci_content_description")
    @Type(type="text")
    private String contentDescription;

    @Column(name = "ci_publish_date")
    private Long publishDate;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    
    public String getStockKeepingUnit() {
      return stockKeepingUnit;
    }

    public void setStockKeepingUnit(String stockKeepingUnit) {
      this.stockKeepingUnit = stockKeepingUnit;
    }

    public String getBusinessType() {
      return businessType;
    }

    public void setBusinessType(String businessType) {
      this.businessType = businessType;
    }

    public String getPageLink() {
      return pageLink;
    }

    public void setPageLink(String pageLink) {
      this.pageLink = pageLink;
    }

    public String getDefaultImage() {
      return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
      this.defaultImage = defaultImage;
    }

    public String getAdditionalImages() {
      return additionalImages;
    }

    public void setAdditionalImages(String additionalImages) {
      this.additionalImages = additionalImages;
    }

    public Boolean getAvailability() {
      return availability;
    }

    public void setAvailability(Boolean availability) {
      this.availability = availability;
    }

    public Long getAdvertiserId() {
      return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
      this.advertiserId = advertiserId;
    }

    public Long getFeedId() {
      return feedId;
    }

    public void setFeedId(Long feedId) {
      this.feedId = feedId;
    }

    public Long getStockQuantity() {
      return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
      this.stockQuantity = stockQuantity;
    }

    public BigDecimal getOriginalPrice() {
      return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
      this.originalPrice = originalPrice;
    }

    public BigDecimal getSalePrice() {
      return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
      this.salePrice = salePrice;
    }

    public Long getOfferStartDate() {
      return offerStartDate;
    }

    public void setOfferStartDate(Long offerStartDate) {
      this.offerStartDate = offerStartDate;
    }

    public Long getOfferEndDate() {
      return offerEndDate;
    }

    public void setOfferEndDate(Long offerEndDate) {
      this.offerEndDate = offerEndDate;
    }

    public BigDecimal getDiscount() {
      return discount;
    }

    public void setDiscount(BigDecimal discount) {
      this.discount = discount;
    }

    public String getBrand() {
      return brand;
    }

    public void setBrand(String brand) {
      this.brand = brand;
    }

    public String getGender() {
      return gender;
    }

    public void setGender(String gender) {
      this.gender = gender;
    }

    public String getAgeGroup() {
      return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
      this.ageGroup = ageGroup;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }

    public String getSize() {
      return size;
    }

    public void setSize(String size) {
      this.size = size;
    }

    public String getMaterial() {
      return material;
    }

    public void setMaterial(String material) {
      this.material = material;
    }

    public String getPattern() {
      return pattern;
    }

    public void setPattern(String pattern) {
      this.pattern = pattern;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getSourceCode() {
      return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
      this.sourceCode = sourceCode;
    }

    public String getDestination() {
      return destination;
    }

    public void setDestination(String destination) {
      this.destination = destination;
    }

    public String getDestinationCode() {
      return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
      this.destinationCode = destinationCode;
    }

    public Long getStartDate() {
      return startDate;
    }

    public void setStartDate(Long startDate) {
      this.startDate = startDate;
    }

    public Long getEndDate() {
      return endDate;
    }

    public void setEndDate(Long endDate) {
      this.endDate = endDate;
    }

    public Integer getDuration() {
      return duration;
    }

    public void setDuration(Integer duration) {
      this.duration = duration;
    }

    public String getClassStr() {
      return classStr;
    }

    public void setClassStr(String classStr) {
      this.classStr = classStr;
    }

    public Boolean getDateState() {
      return dateState;
    }

    public void setDateState(Boolean dateState) {
      this.dateState = dateState;
    }

    public String getOfferName() {
      return offerName;
    }

    public void setOfferName(String offerName) {
      this.offerName = offerName;
    }

    public String getOfferDesc() {
      return offerDesc;
    }

    public void setOfferDesc(String offerDesc) {
      this.offerDesc = offerDesc;
    }

    public BigDecimal getOfferAmount() {
      return offerAmount;
    }

    public void setOfferAmount(BigDecimal offerAmount) {
      this.offerAmount = offerAmount;
    }

    public BigDecimal getOfferPercent() {
      return offerPercent;
    }

    public void setOfferPercent(BigDecimal offerPercent) {
      this.offerPercent = offerPercent;
    }

    public String getOfferCouponCode() {
      return offerCouponCode;
    }

    public void setOfferCouponCode(String offerCouponCode) {
      this.offerCouponCode = offerCouponCode;
    }

    public BigDecimal getBestPrice() {
      return bestPrice;
    }

    public void setBestPrice(BigDecimal bestPrice) {
      this.bestPrice = bestPrice;
    }

    public Boolean getCouponValid() {
      return couponValid;
    }

    public void setCouponValid(Boolean couponValid) {
      this.couponValid = couponValid;
    }

    public BigDecimal getMinOrderValue() {
      return minOrderValue;
    }

    public void setMinOrderValue(BigDecimal minOrderValue) {
      this.minOrderValue = minOrderValue;
    }

    public String getAndTabletClickUrl() {
      return andTabletClickUrl;
    }

    public void setAndTabletClickUrl(String andTabletClickUrl) {
      this.andTabletClickUrl = andTabletClickUrl;
    }

    public String getAndPhoneClickUrl() {
      return andPhoneClickUrl;
    }

    public void setAndPhoneClickUrl(String andPhoneClickUrl) {
      this.andPhoneClickUrl = andPhoneClickUrl;
    }

    public String getIosTabletClickUrl() {
      return iosTabletClickUrl;
    }

    public void setIosTabletClickUrl(String iosTabletClickUrl) {
      this.iosTabletClickUrl = iosTabletClickUrl;
    }

    public String getIosPhoneClickUrl() {
      return iosPhoneClickUrl;
    }

    public void setIosPhoneClickUrl(String iosPhoneClickUrl) {
      this.iosPhoneClickUrl = iosPhoneClickUrl;
    }

    public String getWinTabletClickUrl() {
      return winTabletClickUrl;
    }

    public void setWinTabletClickUrl(String winTabletClickUrl) {
      this.winTabletClickUrl = winTabletClickUrl;
    }

    public String getWinPhoneClickUrl() {
      return winPhoneClickUrl;
    }

    public void setWinPhoneClickUrl(String winPhoneClickUrl) {
      this.winPhoneClickUrl = winPhoneClickUrl;
    }

    public String getThirdPartyClickUrl() {
      return thirdPartyClickUrl;
    }

    public void setThirdPartyClickUrl(String thirdPartyClickUrl) {
      this.thirdPartyClickUrl = thirdPartyClickUrl;
    }

    public String getCategoryId() {
      return categoryId;
    }

    public void setCategoryId(String categoryId) {
      this.categoryId = categoryId;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
    }

    public String getSubCategoryId() {
      return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
      this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
      return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
      this.subCategoryName = subCategoryName;
    }

    public String getContentTitle() {
      return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
      this.contentTitle = contentTitle;
    }

    public String getContentDescription() {
      return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
      this.contentDescription = contentDescription;
    }

    public Long getPublishDate() {
      return publishDate;
    }

    public void setPublishDate(Long publishDate) {
      this.publishDate = publishDate;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("CatalogItemEntity [id=").append(id).append(", title=").append(title)
          .append(", description=").append(description).append(", advertiserId=")
          .append(advertiserId).append(", feedId=").append(feedId).append(", stockKeepingUnit=")
          .append(stockKeepingUnit).append(", businessType=").append(businessType)
          .append(", pageLink=").append(pageLink).append(", defaultImage=").append(defaultImage)
          .append(", additionalImages=").append(additionalImages).append(", availability=")
          .append(availability).append(", stockQuantity=").append(stockQuantity)
          .append(", originalPrice=").append(originalPrice).append(", salePrice=").append(salePrice)
          .append(", offerStartDate=").append(offerStartDate).append(", offerEndDate=")
          .append(offerEndDate).append(", discount=").append(discount).append(", brand=")
          .append(brand).append(", gender=").append(gender).append(", ageGroup=").append(ageGroup)
          .append(", color=").append(color).append(", size=").append(size).append(", material=")
          .append(material).append(", pattern=").append(pattern).append(", source=").append(source)
          .append(", sourceCode=").append(sourceCode).append(", destination=").append(destination)
          .append(", destinationCode=").append(destinationCode).append(", startDate=")
          .append(startDate).append(", endDate=").append(endDate).append(", duration=")
          .append(duration).append(", classStr=").append(classStr).append(", dateState=")
          .append(dateState).append(", offerName=").append(offerName).append(", offerDesc=")
          .append(offerDesc).append(", offerAmount=").append(offerAmount).append(", offerPercent=")
          .append(offerPercent).append(", offerCouponCode=").append(offerCouponCode)
          .append(", bestPrice=").append(bestPrice).append(", couponValid=").append(couponValid)
          .append(", minOrderValue=").append(minOrderValue).append(", andTabletClickUrl=")
          .append(andTabletClickUrl).append(", andPhoneClickUrl=").append(andPhoneClickUrl)
          .append(", iosTabletClickUrl=").append(iosTabletClickUrl).append(", iosPhoneClickUrl=")
          .append(iosPhoneClickUrl).append(", winTabletClickUrl=").append(winTabletClickUrl)
          .append(", winPhoneClickUrl=").append(winPhoneClickUrl).append(", thirdPartyClickUrl=")
          .append(thirdPartyClickUrl).append(", categoryId=").append(categoryId)
          .append(", categoryName=").append(categoryName).append(", subCategoryId=")
          .append(subCategoryId).append(", subCategoryName=").append(subCategoryName)
          .append(", contentTitle=").append(contentTitle).append(", contentDescription=")
          .append(contentDescription).append(", publishDate=").append(publishDate).append("]");
      return builder.toString();
    }


}
