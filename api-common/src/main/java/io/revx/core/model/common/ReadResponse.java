package io.revx.core.model.common;

import java.util.List;

public class ReadResponse<T> {

  public Integer total;

  public Integer pageNumber;

  public Integer pageSize = 0;
  
  public List<T> result;

  public ReadResponse(List<T> result, Integer total, Integer pageNumber, Integer pageSize) {
    this.total = total;
    this.result = result;
    this.pageNumber = pageNumber;
    if (pageNumber != null && pageSize != null)
      this.pageSize =
          total >= pageNumber * pageSize ? pageSize : total - (pageNumber - 1) * pageSize;
    if (this.pageSize < 0)
      this.pageSize = 0;
  }

  /**
   * page number = 1 and page size = size of result list
   * 
   * @param result
   * @param total
   */
  public ReadResponse(List<T> result, Integer total) {
    this.total = total;
    this.result = result;
    this.pageNumber = 1;
    if (result != null)
      this.pageSize = result.size();
  }
}
