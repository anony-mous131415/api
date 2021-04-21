package io.revx.api.mysql.crmdb.entity;

import io.revx.core.enums.CrmStatus;

public interface IStatus extends IDto{

	public static final String STATUS_FIELD = "status";
	
	public CrmStatus getStatus();

}
