/*
 * @author: ranjan-pritesh 16th Dec 2019
 * @modified: Ashish 25th Mar 2020
 */
package io.revx.api.mysql.entity.creative;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.creative.CreativeType;

@Entity
@Table(name = "Creative")
public class CreativeEntity implements BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cr_id", nullable = false)
	private Long id;

	@Column(name = "cr_name")
	private String name;

	@Column(name = "cr_url_path")
	private String urlPath;

	@Column(name = "cr_content", columnDefinition = "longtext")
	private String content;

	@Column(name = "cr_width")
	private Integer width;

	@Column(name = "cr_height")
	private Integer height;

	@Column(name = "cr_allow_empty_lp", columnDefinition = "BIT", length = 1)
	private Boolean aelp;

	@Column(name = "cr_created_at")
	private Long creationDate;

	@Column(name = "cr_created_by")
	private Long createdBy;

	@Column(name = "cr_modified_by")
	private Long modifiedBy;

	@Column(name = "cr_modified_on")
	private Long modifiedOn;

	@Column(name = "cr_creative_type", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
	private CreativeType type;


	@Column(name = "cr_status", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
	private CreativeStatus status;

	@Column(name = "cr_cdn_id")
	private Long cdnId;

	@Column(name = "cr_media_buyer_id", nullable = false)
	private Long advertiserId;

	@Column(name = "cr_licensee_id", nullable = false)
	private Long licenseeId;

	@Column(name = "cr_click_destination", nullable = false)
	private Long clickDestination;

	@Column(name = "cr_is_dco", columnDefinition = "BIT", length = 1)
	private Boolean isDco;

	@Column(name = "cr_video_attributes_id", nullable = true)
	private Long videoAttributesId;

	@Column(name = "cr_version", nullable = true)
	private Long version;

	@Column(name = "cr_is_mraid", columnDefinition = "BIT", length = 1)
	private Boolean isMraid;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "cr_asset_id", nullable = true)
	private CreativeAssetEntity creativeAsset;

	@Column(name = "cr_dco_attributes_id", nullable = false)
	private Long dcoAttributesId;

	@Column(name = "cr_is_refactor", columnDefinition = "BIT", length = 1)
	private Boolean isRefactored;

	@Column(name = "cr_attribute_id")
	private Long attributesId;

	@Column(name = "cr_is_template_based", nullable = false)
	private Boolean isTemplateBased;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Boolean getAelp() {
		return aelp;
	}

	public void setAelp(Boolean aelp) {
		this.aelp = aelp;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public CreativeType getType() {
		return type;
	}

	public void setType(CreativeType type) {
		this.type = type;
	}

	public CreativeStatus getStatus() {
		return status;
	}

	public void setStatus(CreativeStatus status) {
		this.status = status;
	}

	public Long getCdnId() {
		return cdnId;
	}

	public void setCdnId(Long cdnId) {
		this.cdnId = cdnId;
	}

	public Long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getClickDestination() {
		return clickDestination;
	}

	public void setClickDestination(Long clickDestination) {
		this.clickDestination = clickDestination;
	}

	public Boolean getIsDco() {
		return isDco;
	}

	public void setIsDco(Boolean isDco) {
		this.isDco = isDco;
	}

	public Long getVideoAttributesId() {
		return videoAttributesId;
	}

	public void setVideoAttributesId(Long videoAttributesId) {
		this.videoAttributesId = videoAttributesId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Boolean getIsMraid() {
		return isMraid;
	}

	public void setIsMraid(Boolean isMraid) {
		this.isMraid = isMraid;
	}

	public Boolean getIsRefactored() {
		return isRefactored;
	}

	public void setIsRefactored(Boolean isRefactored) {
		this.isRefactored = isRefactored;
	}

	public Long getDcoAttributesId() {
		return dcoAttributesId;
	}

	public void setDcoAttributesId(Long dcoAttributesId) {
		this.dcoAttributesId = dcoAttributesId;
	}

	public Long getAttributesId() {
		return attributesId;
	}

	public void setAttributesId(Long attributesId) {
		this.attributesId = attributesId;
	}

	public CreativeAssetEntity getCreativeAsset() {
		return creativeAsset;
	}

	public void setCreativeAsset(CreativeAssetEntity creativeAsset) {
		this.creativeAsset = creativeAsset;
	}

	public Boolean getTemplateBased() {
		return isTemplateBased;
	}

	public void setTemplateBased(Boolean templateBased) {
		isTemplateBased = templateBased;
	}

	@Override
	public String toString() {
		return "CreativeEntity{" +
				"id=" + id +
				", name='" + name + '\'' +
				", urlPath='" + urlPath + '\'' +
				", content='" + content + '\'' +
				", width=" + width +
				", height=" + height +
				", aelp=" + aelp +
				", creationDate=" + creationDate +
				", createdBy=" + createdBy +
				", modifiedBy=" + modifiedBy +
				", modifiedOn=" + modifiedOn +
				", type=" + type +
				", status=" + status +
				", cdnId=" + cdnId +
				", advertiserId=" + advertiserId +
				", licenseeId=" + licenseeId +
				", clickDestination=" + clickDestination +
				", isDco=" + isDco +
				", videoAttributesId=" + videoAttributesId +
				", version=" + version +
				", isMraid=" + isMraid +
				", creativeAsset=" + creativeAsset +
				", dcoAttributesId=" + dcoAttributesId +
				", isRefactored=" + isRefactored +
				", attributesId=" + attributesId +
				", isTemplateBased=" + isTemplateBased +
				'}';
	}


}
