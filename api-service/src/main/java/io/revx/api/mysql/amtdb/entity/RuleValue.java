package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "RuleValue", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"rv_filter_id", "rv_value" }) })
public class RuleValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rv_id", nullable = false)
	private Long id;

	@Column(name = "rv_filter_id", nullable = false)
	private Long filterId;

	@Column(name = "rv_value", length = 100, nullable = false)
	private String value;

	@Column(name = "rv_display_value", length = 100, nullable = false)
	private String displayValue;
	
	public RuleValue() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

}
