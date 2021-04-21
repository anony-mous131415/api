package io.revx.api.service.pixel;

import java.nio.file.AccessDeniedException;
import java.util.List;
import io.revx.api.enums.Status;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.core.enums.DataSourceType;
import io.revx.core.exception.ApiException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.pixel.PixelAdvDTO;


public interface DataPixelService {

	/**
	 * 
	 * @param pixel
	 *            DataPixelDTO object containing pixel details
	 * @return `
	 * @throws AccessDeniedException
	 */
	public DataPixelDTO createDataPixel(DataPixelDTO pixel);
	
	/**
	 * Save the pixel new details for given partner pixel Returns <tt>true</tt>
	 * if the edit is successful
	 * 
	 * @param pixel
	 *            DataPixelDTO object containing edited info
	 * @return <tt>true</tt> if the edit is successful
	 * @throws AccessDeniedException
	 */
	public DataPixelDTO saveDataPixel(DataPixelDTO pixel);

	/**
	 * Modify the segment status <tt>true</tt> if the edit is successful
	 * 
	 * @param pixelId
	 *            Segment Id of the segment to be edited
	 * @param new updated status
	 * @return <tt>true</tt> if the edit is successful
	 * @throws AccessDeniedException
	 */
	public DataPixelDTO modifyDataPixelStatus(Integer pixelId, Status status);

	/**
	 * Fetches pixel for the given partner
	 * 
	 * @param pixelId
	 *            DataPixelDTO Id
	 * @return
	 * @throws ApiException 
	 * @throws AccessDeniedException
	 */
	public DataPixelDTO getDataPixel(Long pixelId) throws ApiException;

	/**
	 * Fetches pixels for the given partner
	 * 
	 * @return
	 * @throws AccessDeniedException
	 */
	public List<DataPixelDTO> getAllDataPixelsForLicensee();

	/**
	 * 
	 * @return
	 */
	public List<PixelAdvDTO> getAllDataPixels();

	/**
	 * Fetches pixels with pagination support for the given partner
	 * 
	 * @param startIndex
	 * @param pageSize
	 * @return
	 * @throws AccessDeniedException
	 */
	public List<PixelAdvDTO> getAllDataPixels(int startIndex,	int pageSize);

	/**
	 * 
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	public List<DataPixelDTO> getAllDataPixelsForLicensee(int startIndex, int pageSize);

	/**
	 * 
	 * @param pixelId
	 * @return
	 */
	//public Audiences getAudiences(Integer pixelId);

	public Long createAdvertiserToPixel(BaseModel advertiser, DataSourceType sourceType);

	public AdvertiserToPixelEntity createAndGetAdvertiserToPixel(BaseModel advertiser, DataSourceType sourceType) ;

    public AdvertiserToPixelEntity getAdvertiserToPixel(Long pixelId);
}
