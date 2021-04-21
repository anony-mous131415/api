package io.revx.core.model.advertiser;

import io.revx.core.model.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LookalikeDTO {

	public int enableFacebook;

	public Integer targetSize;
	
	public BaseModel targetCountry;
	
	public BaseModel baseAudience;
	
}
