package com.komli.prime.service.reporting.apibean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Campaign {
	public int id;
	public String name;
	public long timestampCreation;
	public Campaign() {}

	public Campaign(int id, String name, long timestampCreation) {
		this.id = id;
		this.name = name;
		this.timestampCreation = timestampCreation;
	}
}
