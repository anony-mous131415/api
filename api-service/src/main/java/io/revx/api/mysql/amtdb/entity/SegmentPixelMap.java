package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistent class for the SegmentPixelMap Table
 */

@Entity
@Table(name = "SegmentPixelMap")
public class SegmentPixelMap implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(name = "segment_id", nullable = false)
	private Long segmentId;

	@Column(name = "pixel_id", nullable = false)
	private Long pixelId;

	@Column(name = "rule_expression", length = 512)
	private String ruleExpression;

  /*
   * @OneToMany(mappedBy = "segmentPixelExpressionId", fetch = FetchType.EAGER)
   * 
   * @OrderBy(value = "id") //@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
   * //@JoinColumn(name = "rc_segment_pixel_id") private List<RuleComponent> ruleComponents;
   */
	public SegmentPixelMap() {

	}

	public SegmentPixelMap(Long segmentId, Long pixelId) {
		super();
		this.segmentId = segmentId;
		this.pixelId = pixelId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Long segmentId) {
		this.segmentId = segmentId;
	}

	public Long getPixelId() {
		return pixelId;
	}

	public void setPixelId(Long pixelId) {
		this.pixelId = pixelId;
	}

	public String getRuleExpression() {
		return ruleExpression;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
	}

}
