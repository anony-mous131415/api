package io.revx.api.service;

import io.revx.api.enums.SlicexEntity;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.SlicexChartResponse;
import io.revx.core.model.requests.SlicexListResponse;
import io.revx.core.model.requests.SlicexRequest;

public interface ISlicexService {

  public SlicexChartResponse getSlicexChartData(SlicexRequest slicexRequest);

  public SlicexListResponse getSlicexGridData(SlicexRequest slicexRequest, String sort,
       SlicexEntity entity);

  public FileDownloadResponse getSlicexGridDataForExport(SlicexRequest slicexRequest, String sort,
      SlicexEntity entity);
}


