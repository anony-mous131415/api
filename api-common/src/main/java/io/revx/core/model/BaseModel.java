package io.revx.core.model;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseModel implements Serializable, Comparable<BaseModel> {

	protected static final long serialVersionUID = 1L;

	public BaseModel() {
		super();
	}

	public BaseModel(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long id;
	public String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void populateIdAndName(BaseModel model) {
		if (model != null) {
			this.id = model.getId();
			this.name = model.getName();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseModel other = (BaseModel) obj;
		if (id.longValue() != other.id.longValue())
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(BaseModel model) {
		return StringUtils.compare((this == null) ? null : this.name, (model == null) ? null : model.name);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseModel [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
