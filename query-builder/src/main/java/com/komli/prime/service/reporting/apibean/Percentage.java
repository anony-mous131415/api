package com.komli.prime.service.reporting.apibean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Percentage {
	public float value;
	public float minvalue;
	public float maxvalue;
	
	public Percentage(){}
	public Percentage(float value){
		this.value = value;
		this.minvalue = 0;
		this.maxvalue = 100;
	}
}
