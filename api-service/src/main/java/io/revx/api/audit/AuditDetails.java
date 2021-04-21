package io.revx.api.audit;

import java.util.HashSet;
import java.util.Set;

import io.revx.api.mysql.entity.audit.AuditChange;

public class AuditDetails {

	private Long id;
	private Long timestamp;
	private String message;
	private Long type;
	private String user_name;
	private String entity_type;
	private Long entity_id;
	private String entity_name;
	private Set<AuditChange> changes = new HashSet<AuditChange>();
	
	public AuditDetails() {
		super();
	}

	public AuditDetails(Long id, Long timestamp, String message, Long type, String user_name, String entity_type,
			Long entity_id, String entity_name, Set<AuditChange> changes) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.message = message;
		this.type = type;
		this.user_name = user_name;
		this.entity_type = entity_type;
		this.entity_id = entity_id;
		this.entity_name = entity_name;
		this.changes = changes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public Long getEntity_id() {
		return entity_id;
	}

	public void setEntity_id(Long entity_id) {
		this.entity_id = entity_id;
	}

	public String getEntity_name() {
		return entity_name;
	}

	public void setEntity_name(String entity_name) {
		this.entity_name = entity_name;
	}

	public Set<AuditChange> getChanges() {
		return changes;
	}

	public void setChanges(Set<AuditChange> changes) {
		this.changes = changes;
	}

	@Override
	public String toString() {
		return "AuditDetails [id=" + id + ", timestamp=" + timestamp + ", message=" + message + ", type=" + type
				+ ", user_name=" + user_name + ", entity_type=" + entity_type + ", entity_id=" + entity_id
				+ ", entity_name=" + entity_name + ", changes=" + changes + "]";
	}

	
}
