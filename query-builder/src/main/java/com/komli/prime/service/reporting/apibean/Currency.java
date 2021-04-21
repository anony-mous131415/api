package com.komli.prime.service.reporting.apibean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Currency {
	public int id;
	public String name;
	public Currency(){}
	public Currency(int id, String name){
		this.id = id;
		this.name = name;
	}
}
