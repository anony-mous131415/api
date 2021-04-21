package io.revx.api.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import io.revx.core.enums.AppSettingsKey;
import io.revx.core.enums.AppSettingsType;

import java.util.List;

@Entity
@Table(name = "AppSettings")
public class AppSettingsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "as_id")
	private Long id;

	@Column(name = "as_licensee_id")
	private Long licenseeId;

	@Column(name = "as_advertiser_id")
	private Long advertiserId;

	@Column(name = "as_key", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
	private AppSettingsKey key;

	@Column(name = "as_value")
	private String value;

	@Column(name = "as_type", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
	private AppSettingsType type;

	@Column(name = "as_is_active")
	private boolean active;

	@OneToMany(mappedBy = "appSettingsEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AppSettingsPropertyEntity> appSettingsPropertyEntities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public AppSettingsKey getKey() {
		return key;
	}

	public void setKey(AppSettingsKey key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AppSettingsType getType() {
		return type;
	}

	public void setType(AppSettingsType type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<AppSettingsPropertyEntity> getAppSettingsPropertyEntities() {
		return appSettingsPropertyEntities;
	}

	public void setAppSettingsPropertyEntities(List<AppSettingsPropertyEntity> appSettingsPropertyEntities) {
		this.appSettingsPropertyEntities = appSettingsPropertyEntities;
	}

	@Override
	public String toString() {
		return "AppSettingsEntity{" +
				"id=" + id +
				", licenseeId=" + licenseeId +
				", advertiserId=" + advertiserId +
				", key='" + key + '\'' +
				", value='" + value + '\'' +
				", type=" + type +
				", isActive=" + active +
				", appSettingsPropertyEntities=" + appSettingsPropertyEntities +
				'}';
	}
}
