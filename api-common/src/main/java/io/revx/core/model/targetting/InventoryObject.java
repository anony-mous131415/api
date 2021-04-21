package io.revx.core.model.targetting;

import java.io.Serializable;
import io.revx.core.enums.InventoryType;

public class InventoryObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String inventoryTargetingExpression;
	
	private InventoryType inventoryType;

	public String getInventoryTargetingExpression() {
		return inventoryTargetingExpression;
	}

	public void setInventoryTargetingExpression(String inventoryTargetingExpression) {
		this.inventoryTargetingExpression = inventoryTargetingExpression;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}

	public InventoryObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
