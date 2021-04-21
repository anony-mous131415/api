package com.komli.prime.service.reporting.apibean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Money {
	public double cost;
	public Currency currency;
	
	public Money(){}
	
	public Money(double cost, int id, String name){
		this.cost = cost;
		this.currency = new Currency(id,name);
	}
	
}
