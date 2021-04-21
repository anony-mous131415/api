package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistent class for the RuleComponent table
 */

@Entity
@Table(name = "RuleComponent")
public class RuleComponent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rc_id", nullable = false)
	private Integer id;

	@Column(name = "rc_segment_pixel_id", nullable = false)
	private Long segmentPixelExpressionId;

    @Column(name = "rc_filter_id", nullable = false)
    private Long ruleFilterId;

    @Column(name = "rc_operator_id", nullable = false)
    private Long ruleOperatorId;
  
  /*
   * @ManyToOne(fetch = FetchType.EAGER)
   * 
   * @JoinColumn(name = "rc_filter_id") private RuleFilter ruleFilterDto;
   * 
   * @ManyToOne(fetch = FetchType.EAGER)
   * 
   * @JoinColumn(name = "rc_operator_id") private RuleOperator ruleOperatorDto;
   */

	@Column(name = "rc_value", nullable = false)
	private String ruleValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getSegmentPixelExpressionId() {
		return segmentPixelExpressionId;
	}

	public void setSegmentPixelExpressionId(Long segmentToPixelExpressionId) {
		this.segmentPixelExpressionId = segmentToPixelExpressionId;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

  public Long getRuleFilterId() {
    return ruleFilterId;
  }

  public void setRuleFilterId(Long ruleFilterId) {
    this.ruleFilterId = ruleFilterId;
  }

  public Long getRuleOperatorId() {
    return ruleOperatorId;
  }

  public void setRuleOperatorId(Long ruleOperatorId) {
    this.ruleOperatorId = ruleOperatorId;
  }
	
}
