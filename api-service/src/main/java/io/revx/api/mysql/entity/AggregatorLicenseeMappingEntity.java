package io.revx.api.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AggregatorLicenseeMapping")
public class AggregatorLicenseeMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alm_id")
	private int id;

	@Column(name = "alm_ag_id")
	private int aggregatorId;

	@Column(name = "alm_licensee_id")
	private int licenseeId;

	@Column(name = "alm_type")
	private Boolean isWhiteListed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAggregatorId() {
		return aggregatorId;
	}

	public void setAggregatorId(int aggregatorId) {
		this.aggregatorId = aggregatorId;
	}

	public int getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(int licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Boolean getIsWhiteListed() {
		return isWhiteListed;
	}

	public void setIsWhiteListed(Boolean isWhiteListed) {
		this.isWhiteListed = isWhiteListed;
	}

	@Override
	public String toString() {
		return "AggregatorLicenseeMappingEntity [id=" + id + ", aggregatorId=" + aggregatorId + ", licenseeId="
				+ licenseeId + ", isWhiteListed=" + isWhiteListed + "]";
	}

}
