/*
 * @author: ranjan-pritesh @date: 16th Dec 2019
 * @modified: Ashish date - 25/03/2020
 * 
 */
package io.revx.api.mysql.entity.creative;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CreativeAsset")
public class CreativeAssetEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ca_id", nullable = false)
	private Long id;

	@Column(name = "ca_data", unique = false, nullable = false, length = 100000, columnDefinition="blob")
	private byte[] assetData;

	@Column(name = "ca_width")
	private Integer width;

	@Column(name = "ca_height")
	private Integer height;

	@Column(name = "ca_advertiser_id")
	private Long advertiserId;

	@Column(name = "ca_licensee_id")
	private Long licenseeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public byte[] getAssetData() {
		return assetData;
	}

	public void setAssetData(byte[] assetData) {
		this.assetData = assetData;
	}

}
