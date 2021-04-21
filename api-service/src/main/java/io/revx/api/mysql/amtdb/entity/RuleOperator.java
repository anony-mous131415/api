package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Persistent class for the table RuleOperators
 */

@Entity
@Table(name = "RuleOperator")
public class RuleOperator implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ro_id", nullable = false)
	private Long id;

	@Column(name = "ro_operator_name", nullable = false)
	private String operatorName;

	@Column(name = "ro_display_name", nullable = false)
	private String operatorDisplayName;
	
	@Column(name = "ro_fbx_operator_name")
	private String fbxOperatorName;
	
	public RuleOperator() {
		super();
	}

	public RuleOperator(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorDisplayName() {
		return operatorDisplayName;
	}

	public void setOperatorDisplayName(String operatorDisplayName) {
		this.operatorDisplayName = operatorDisplayName;
	}

	public String getFbxOperatorName() {
		return fbxOperatorName;
	}

	public void setFbxOperatorName(String fbxOperatorName) {
		this.fbxOperatorName = fbxOperatorName;
	}
	
}
