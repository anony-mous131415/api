package io.revx.core.model.reporting;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DurationModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "start_timestamp")
	private Long start_timestamp;

	@JsonProperty(value = "end_timestamp")
	private Long end_timestamp;

	public DurationModel() {
		super();
	}

	public DurationModel(Long startTimeStamp, Long endTimeStamp) {
		super();
		this.start_timestamp = startTimeStamp;
		this.end_timestamp = endTimeStamp;
	}

	public Long getStart_timestamp() {
		return start_timestamp;
	}

	public void setStart_timestamp(Long start_timestamp) {
		this.start_timestamp = start_timestamp;
	}

	public Long getEnd_timestamp() {
		return end_timestamp;
	}

	public void setEnd_timestamp(Long end_timestamp) {
		this.end_timestamp = end_timestamp;
	}

	@Override
	public String toString() {
		return "DurationModel [start_timestamp=" + start_timestamp + ", end_timestamp=" + end_timestamp + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end_timestamp == null) ? 0 : end_timestamp.hashCode());
		result = prime * result + ((start_timestamp == null) ? 0 : start_timestamp.hashCode());
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
		DurationModel other = (DurationModel) obj;
		if (end_timestamp == null) {
			if (other.end_timestamp != null)
				return false;
		} else if (!end_timestamp.equals(other.end_timestamp))
			return false;
		if (start_timestamp == null) {
			if (other.start_timestamp != null)
				return false;
		} else if (!start_timestamp.equals(other.start_timestamp))
			return false;
		return true;
	}

}
