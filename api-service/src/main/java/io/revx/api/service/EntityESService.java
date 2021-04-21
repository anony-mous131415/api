package io.revx.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.pojo.TablesEntity;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.BaseModelWithModifiedTime;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.City;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.State;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.StatusTimeModel;
import io.revx.core.model.Strategy;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.ElasticResponse;
import io.revx.core.model.requests.MenuCrubResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.querybuilder.enums.Filter;

import static io.revx.api.utility.Util.replaceSpecialCharactersWithSpace;

@Component
@SuppressWarnings("unchecked")
public class EntityESService {

	private static Logger logger = LogManager.getLogger(EntityESService.class);

	@Autowired
	CustomESRepositoryImpl customESRepositoryImpl;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.MENU)
	public List<MenuCrubResponse> getMenuCrubResponse() {
		TablesEntity[] menuCrubsEntity = { TablesEntity.ADVERTISER, TablesEntity.APP_AUDIENCE,
				TablesEntity.WEB_AUDIENCE, TablesEntity.DMP_AUDIENCE, TablesEntity.CAMPAIGN, TablesEntity.CREATIVE,
				TablesEntity.STRATEGY };
		List<MenuCrubResponse> resp = new ArrayList<>();
		for (TablesEntity entity : menuCrubsEntity) {
			List<StatusBaseObject> list = (List<StatusBaseObject>) customESRepositoryImpl
					.searchAsList(entity.getElasticIndex(), getElasticFilter(entity), StatusBaseObject.class);

			if (entity == TablesEntity.APP_AUDIENCE) {
				resp.add(new MenuCrubResponse("app_audience", list));
			} else if (entity == TablesEntity.WEB_AUDIENCE) {
				resp.add(new MenuCrubResponse("web_audience", list));
			} else if (entity == TablesEntity.DMP_AUDIENCE) {
				resp.add(new MenuCrubResponse("dmp_audience", list));
			} else {
				resp.add(new MenuCrubResponse(entity.getElasticIndex(), list));
			}
		}
		return resp;
	}

	public List<BaseModel> searchList(TablesEntity entity) {
		List<BaseModel> list = (List<BaseModel>) customESRepositoryImpl.searchAsList(entity.getElasticIndex(),
				getElasticFilter(entity), entity.getElasticPojoClass());
		return list;
	}

	public DictionaryResponse getDictionaryData(TablesEntity tableEntity, Integer pageNumber, Integer pageSize,
			SearchRequest request, String sort) {
		Direction direction = Direction.ASC;
		if (pageNumber > 0)
			pageNumber--;
		ElasticSearchTerm est = getElasticFilter(tableEntity);
		if (request != null && request.getFilters() != null)
			updateElasticSearchTerm(est, request.getFilters());

		if (StringUtils.isNotBlank(sort)) {
			direction = getESDirectionToSort(sort);
			sort = getESKeyWordToSort(sort);
		}

		logger.debug(" pageNumber {} , pageSize {} , est {} ", pageNumber, pageSize, est);
		ElasticResponse eResp = customESRepositoryImpl.searchByNameLike(pageNumber, pageSize,
				tableEntity.getElasticIndex(), est, null, sort, direction);
		return new DictionaryResponse(eResp);
	}

	private String getESKeyWordToSort(String sort) {
		String sortBy = sort.substring(0, sort.length() - 1);

		if ( !sortBy.equals("name") )
			return new StringBuilder(sort.substring(0, sort.length() - 1)).toString();
		else
			return new StringBuilder(sort.substring(0, sort.length() - 1)).append(".keyword").toString();
	}

	private Direction getESDirectionToSort(String sort) {
		Character dir = sort.charAt(sort.length() - 1);
		if (dir.equals('-'))
			return Direction.DESC;
		else
			return Direction.ASC;
	}

	public EResponse<?> getDetailDictionaryData(TablesEntity tableEntity, Integer pageNumber, Integer pageSize,
			SearchRequest request, String sort) {
		Direction direction = Direction.ASC;
		if (pageNumber > 0)
			pageNumber--;
		ElasticSearchTerm est = getElasticFilter(tableEntity);

		if (StringUtils.isNotBlank(sort)) {
			direction = getESDirectionToSort(sort);
			sort = getESKeyWordToSort(sort);
		}

		if (request != null && request.getFilters() != null)
			updateElasticSearchTerm(est, request.getFilters());
		logger.debug(" pageNumber {} , pageSize {} , est {} ", pageNumber, pageSize, est);
		EResponse<?> resp = customESRepositoryImpl.searchAsList(pageNumber, pageSize, tableEntity.getElasticIndex(),
				est, tableEntity.getElasticPojoClass(), sort, direction);
		return resp;
	}

	public MenuCrubResponse searchByName(TablesEntity tableEntity, String filter, List<DashboardFilters> filters) {
		ElasticSearchTerm est = getElasticFilter(tableEntity);
		if (filters != null) {
			updateElasticSearchTerm(est, filters);
		}

		ElasticResponse eResp = customESRepositoryImpl.searchByNameLike(tableEntity.getElasticIndex(), est, filter);
		return new MenuCrubResponse(tableEntity.getElasticIndex(), eResp.getData());
	}

	public ElasticResponse searchByGivenFilter(TablesEntity tableEntity, List<DashboardFilters> filters) {
		ElasticSearchTerm est = loginUserDetailsService.getElasticSearchTerm();
		updateElasticSearchTerm(est, filters);

		ElasticResponse eResp = customESRepositoryImpl.searchByNameLike(tableEntity.getElasticIndex(), est, null);
		return eResp;
	}

	public BaseModel searchById(TablesEntity tableEntity, long id) {
		logger.debug(" searchById : {} for id {}  ", tableEntity, id);
		List<BaseModelWithModifiedTime> list = customESRepositoryImpl.findById(tableEntity.getElasticIndex(),
				String.valueOf(id));
		if (list != null && list.size() > 0) {
			return new BaseModel(list.get(0).getId(), list.get(0).getName());
		}
		return null;
	}

	public <T> T searchPojoById(TablesEntity tableEntity, long id) {
		logger.debug(" searchPojoById : {} for id {}  ", tableEntity, id);
		List<T> list = (List<T>) customESRepositoryImpl.findById(tableEntity.getElasticIndex(), String.valueOf(id),
				tableEntity.getElasticPojoClass());
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<?> searchPojoByIdList(TablesEntity tableEntity, List<Long> ids) {
		logger.debug(" searchPojoByIdList : {} for id {}  ", tableEntity, java.util.Arrays.asList(ids));
		List<?> list = customESRepositoryImpl.findByIdList(tableEntity.getElasticIndex(), ids,
				tableEntity.getElasticPojoClass());
		return list;
	}

	public ParentBasedObject searchDetailById(TablesEntity tableEntity, long id) {
		ParentBasedObject pbo = null;
		StatusTimeModel stm = (StatusTimeModel) customESRepositoryImpl.findDetailById(tableEntity.getElasticIndex(),
				String.valueOf(id), tableEntity.getElasticPojoClass());
		if (stm != null) {
			pbo = new ParentBasedObject(stm);
			Long parentId = getParentId(stm);
			int max = 0;
			while (tableEntity.getParentTableEntity() != null && parentId != null && max <= 5) {
				max++;
				tableEntity = tableEntity.getParentTableEntity();
				stm = (StatusTimeModel) customESRepositoryImpl.findDetailById(tableEntity.getElasticIndex(),
						String.valueOf(parentId), tableEntity.getElasticPojoClass());
				parentId = getParentId(stm);
				ParentBasedObject tmp = pbo;
				System.out.println(max + ":: " + stm + " :: " + parentId);
				if (stm != null) {
					while (tmp.getParent() != null) {
						tmp = tmp.getParent();
					}
					tmp.setParent(new ParentBasedObject(stm));
					logger.debug("tmp >>>>>>>>>  {}", tmp);
				}
			}
		}
		return pbo;
	}

	private static Long getParentId(StatusTimeModel stm) {
		if (stm instanceof Strategy) {
			return ((Strategy) stm).getCampaignId();
		} else if (stm instanceof CampaignESDTO) {
			return ((CampaignESDTO) stm).getAdvertiserId();
		} else if (stm instanceof Advertiser) {
			return ((Advertiser) stm).getLicenseeId();
		} else if (stm instanceof State) {
			return ((State) stm).getCountryId();
		} else if (stm instanceof City) {
			return ((City) stm).getStateId();
		}
		return null;
	}

	public Map<Long, ?> search(TablesEntity tableEntity, ElasticSearchTerm searchTerm) {
		if (tableEntity != null) {
			Map<Long, ?> idModelMap = customESRepositoryImpl.search(tableEntity.getElasticIndex(), searchTerm,
					tableEntity.getElasticPojoClass());
			return idModelMap;
		}
		return null;
	}

	public <T> EResponse<T> searchAll(TablesEntity tableEntity, ElasticSearchTerm searchTerm) {
		if (tableEntity != null) {
			EResponse<T> eResponse = (EResponse<T>) customESRepositoryImpl.searchAll(tableEntity.getElasticIndex(),
					searchTerm, tableEntity.getElasticPojoClass());
			return eResponse;
		}
		return null;
	}

	/**
	 * Returns all the id and name mapping of the documents that match the provides filters
	 * of an index
	 */
	public <T> EResponse<T> searchAll(TablesEntity tableEntity, List<DashboardFilters> filters) {
		ElasticSearchTerm est = loginUserDetailsService.getElasticSearchTerm();
		updateElasticSearchTerm(est, filters);
		if (tableEntity != null) {
			return (EResponse<T>) customESRepositoryImpl.searchAll(tableEntity.getElasticIndex(),
					est, tableEntity.getElasticPojoClass());
		}
		return null;
	}

	public Map<Long, ?> searchByNameExactMatch(TablesEntity tableEntity, Set<String> names) {
		if (tableEntity != null) {
			Map<Long, ?> idModelMap = customESRepositoryImpl.searchByNameExact(tableEntity.getElasticIndex(),
					getElasticFilter(tableEntity), names, tableEntity.getElasticPojoClass());
		}
		return null;
	}

	public Map<Long, ?> searchAllByNameExactMatch(TablesEntity tableEntity, Set<String> names) {
		if (tableEntity != null) {
			return customESRepositoryImpl.searchAllByExactNames(tableEntity.getElasticIndex(),
					getElasticFilter(tableEntity), names, tableEntity.getElasticPojoClass());
		}
		return null;
	}

	private void updateElasticSearchTerm(ElasticSearchTerm est, List<DashboardFilters> filters) {
		ElasticSearchTerm tempESt = new ElasticSearchTerm();
		for (DashboardFilters filterComponent : filters) {
			try {
				Filter filter = Filter.fromString(filterComponent.getColumn());
				if (filter != null && StringUtils.isNoneBlank(filterComponent.getValue())) {
						switch (filter) {
					case ADVERTISER_ID:
						tempESt.setAdvertisers(Long.parseLong(filterComponent.getValue()));
						break;
					case CAMPAIGN_ID:
						tempESt.setCampaigns(Long.parseLong(filterComponent.getValue()));
						break;
					case STRATEGY_ID:
						tempESt.setStrategies(Long.parseLong(filterComponent.getValue()));
						break;
					case AGGREGATOR_ID:
						tempESt.setAggregators(filterComponent.getValue());
						break;
					case ID:
						tempESt.setSerachInIdOrName(filterComponent.getValue());
					case NAME:
						String searchTerm = replaceSpecialCharactersWithSpace(filterComponent.getValue());
						tempESt.setSerachInIdOrName(searchTerm);
						break;
					default:
						tempESt.setFilters(filterComponent.getColumn(), filterComponent.getValue());
						break;
					}
				} else {
					// Here Filter is Not defined
					tempESt.setFilters(filterComponent.getColumn(), filterComponent.getValue());
				}
			} catch (Exception e) {

			}
		}

		if (tempESt.getAdvertisers() != null && tempESt.getAdvertisers().size() > 0) {
			est.setAdvertisers(tempESt.getAdvertisers());
		}
		if (tempESt.getCampaigns() != null && tempESt.getCampaigns().size() > 0) {
			est.setCampaigns(tempESt.getCampaigns());
		}
		if (tempESt.getStrategies() != null && tempESt.getStrategies().size() > 0) {
			est.setStrategies(tempESt.getStrategies());
		}
		if (tempESt.getAggregators() != null && tempESt.getAggregators().size() > 0) {
			est.setAggregators(tempESt.getAggregators());
		}
		if (StringUtils.isNotBlank(tempESt.getSerachInIdOrName())) {
			est.setSerachInIdOrName(tempESt.getSerachInIdOrName());
		}
		if (tempESt.getFilters() != null) {
			est.setFilters(tempESt.getFilters());
		}

	}

	private ElasticSearchTerm getElasticFilter(TablesEntity entity) {
		ElasticSearchTerm elasticSearchTerm = null;

		if (entity.isLoginFilter()) {
			elasticSearchTerm = loginUserDetailsService.getElasticSearchTerm();

			Map<String, Set<String>> filters = elasticSearchTerm.getFilters();
			if (filters.isEmpty()) {
				filters = new HashMap<String, Set<String>>();
			}

			if (entity == TablesEntity.APP_AUDIENCE) {
				String appAudience[] = { "mobile_app" };
				Set<String> app = new HashSet<>(Arrays.asList(appAudience));
				filters.put("user_data_type", app);
			} else if (entity == TablesEntity.WEB_AUDIENCE) {
				String webAudience[] = { "web_browsing" };
				Set<String> web = new HashSet<>(Arrays.asList(webAudience));
				filters.put("user_data_type", web);
			} else if (entity == TablesEntity.DMP_AUDIENCE) {
				String dmpAudience[] = { "dmp" };
				Set<String> dmp = new HashSet<>(Arrays.asList(dmpAudience));
				filters.put("user_data_type", dmp);
			}
			elasticSearchTerm.setFilters(filters);
			return elasticSearchTerm;
		}
		return new ElasticSearchTerm();
	}

	public String save(BaseModel baseModel, TablesEntity tableEntity) throws JsonProcessingException {
		return customESRepositoryImpl.save(baseModel, tableEntity);
	}
}
