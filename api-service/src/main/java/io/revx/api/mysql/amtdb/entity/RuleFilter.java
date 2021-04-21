package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import io.revx.api.audience.pojo.RuleFilterType;
import io.revx.api.audience.pojo.RuleValueType;

/**
 * Persistant class for RuleFilters table
 */

@Entity
@Table(name = "RuleFilter")
public class RuleFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "rf_id", nullable = false)
	private Long id;

	@Column(name = "rf_filter_name", nullable = false)
	private String filterName;

	@Column(name = "rf_display_name", nullable = false)
	private String filterDisplayName;
	
	@Column(name = "rf_filter_type", nullable = false)
	private String ruleFilterType;

	@Column(name = "rf_value_type")
	private String ruleValueType;
	
	@Column(name = "rf_fbx_filter_name")
	private String fbxFilterName;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "FilterOperatorMap", joinColumns = { @JoinColumn(name = "fom_filter_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "fom_operator_id", nullable = false) })
	private Set<RuleOperator> ruleOperatorDto;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "filterId")
	private Set<RuleValue> ruleValueDto;

	public RuleFilter() {
		super();
	}

	public RuleFilter(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
	public String getFilterDisplayName() {
		return filterDisplayName;
	}

	public void setFilterDisplayName(String filterDisplayName) {
		this.filterDisplayName = filterDisplayName;
	}

	public RuleFilterType getRuleFilterType() {
		return RuleFilterType.getRuleFilterType(ruleFilterType);
	}

	public void setRuleFilterType(RuleFilterType ruleFilterType) {
		this.ruleFilterType = ruleFilterType.name();
	}

	public RuleValueType getRuleValueType() {
		return RuleValueType.getRuleValueType(ruleValueType);
	}

	public void setRuleValueType(RuleValueType ruleValueType) {
		this.ruleValueType = ruleValueType.name();
	}

	public String getFbxFilterName() {
		return fbxFilterName;
	}

	public void setFbxFilterName(String fbxFilterName) {
		this.fbxFilterName = fbxFilterName;
	}

	public Set<RuleOperator> getRuleOperatorDto() {
		return ruleOperatorDto;
	}

	public void setRuleOperatorDto(Set<RuleOperator> ruleOperatorDto) {
		this.ruleOperatorDto = ruleOperatorDto;
	}

	public Set<RuleValue> getRuleValueDto() {
		return ruleValueDto;
	}

	public void setRuleValueDto(Set<RuleValue> ruleValueDto) {
		this.ruleValueDto = ruleValueDto;
	}

}
