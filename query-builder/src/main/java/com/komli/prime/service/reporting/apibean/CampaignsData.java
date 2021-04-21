package com.komli.prime.service.reporting.apibean;

import java.sql.ResultSet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CampaignsData {
	public Campaign campaign;
	public Language language;
	public long impressions;
	public long clicks;
	public long conversions;
	public Money mediaCost;
	public Percentage margin;
	public Percentage ctr;
	public Percentage cvr;
	
	public CampaignsData(){}

	public CampaignsData(int campaign_id, String campaign_name, long creationDate,
			int lang_id, String lang_name, long impressions, long clicks, long conversions, float mediaCost,
			int cost_curr_id, String cost_curr_name, float margin, float cvr,
			float ctr) {
		
			this.campaign = new Campaign(campaign_id, campaign_name, creationDate);
			this.language = new Language(lang_id, lang_name);
			this.impressions = impressions;
			this.clicks = clicks;
			this.conversions = conversions;
			this.mediaCost = new Money(mediaCost, cost_curr_id, cost_curr_name);
			this.margin = new Percentage(margin);
			this.ctr = new Percentage(ctr);
			this.cvr = new Percentage(cvr);			
	}
	
	public CampaignsData(ResultSet resultSet){
		
	}
	
}
