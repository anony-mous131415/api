
package io.revx.core.model.targetting;

import java.util.List;

public class RTBChannel {
  public boolean selectAllChannels;
  public boolean selectAnyChannels;

  public List<Aggregator> includeChannels;
  public List<Aggregator> excludeChannels;
  public List<Favourite> favourites;
}
