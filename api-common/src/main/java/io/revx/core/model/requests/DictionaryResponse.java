package io.revx.core.model.requests;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.model.BaseModel;

public class DictionaryResponse {

  protected long totalNoOfRecords;
  protected List<BaseModel> data;

  public DictionaryResponse() {
    super();

  }

  public DictionaryResponse(long totalNoOfRecords, List<BaseModel> data) {
    super();
    this.totalNoOfRecords = totalNoOfRecords;
    this.data = data;
  }

  public DictionaryResponse(ElasticResponse eResp) {
    if (eResp != null) {
      this.totalNoOfRecords = eResp.getTotalNoOfRecords();
      this.data = new ArrayList<>();
      this.data.addAll(eResp.getData());
    }
  }

  public long getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(long totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<BaseModel> getData() {
    return data;
  }

  public void setData(List<BaseModel> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DictionaryResponse [totalNoOfRecords=");
    builder.append(totalNoOfRecords);
    builder.append(", data=");
    builder.append(data);
    builder.append("]");
    return builder.toString();
  }

}
