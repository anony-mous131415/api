/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.core.model.creative;

import java.io.Serializable;


public enum CampaignType implements Serializable {

  UA, RT;
  
  
  public static CampaignType getCampaignType(String str)
  {
    if(str.equals(CampaignType.UA.toString()))
      return CampaignType.UA;
    
    if(str.equals(CampaignType.RT.toString()))
      return CampaignType.RT;
    
    return null;
    
  }

}
