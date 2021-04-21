/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.es.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.utils.ServiceUtils;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.BaseModel;
import io.revx.core.model.BaseModelWithModifiedTime;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.ElasticResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Repository
public class CustomESRepositoryImpl {

	private static Logger logger = LogManager.getLogger(CustomESRepositoryImpl.class);
	private static int DEFAULT_RESULT_SIZE = 15;
	private static String DEFAULT_SORT_FIELD = "modifiedTime";

	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	private static ObjectMapper mapper = new ObjectMapper();

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.FIND_ID)
	public List<BaseModelWithModifiedTime> findById(String index, String id) {
		logger.debug(" find By Id : {}  , index {}  ", id, index);
		PageRequest pr = PageRequest.of(0, DEFAULT_RESULT_SIZE, Sort.by(DEFAULT_SORT_FIELD));
		BoolQueryBuilder boolquery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", id));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(boolquery).withIndices(index).withPageable(pr)
				.build();

		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		List<BaseModelWithModifiedTime> result = new ArrayList<BaseModelWithModifiedTime>();
		for (SearchHit hit : response.getHits()) {
			BaseModelWithModifiedTime pojo = mapper.convertValue(hit.getSourceAsMap(), BaseModelWithModifiedTime.class);
			result.add(pojo);
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + result.size());
		return result;

	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.FIND_ID)
	public <T> List<T> findById(String index, String id, Class<T> resultClass) {
		PageRequest pr = PageRequest.of(0, DEFAULT_RESULT_SIZE, Sort.by(DEFAULT_SORT_FIELD));
		BoolQueryBuilder boolquery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", id));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(boolquery).withIndices(index).withPageable(pr)
				.build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		logger.debug("We are here For printing");
		List<T> result = new ArrayList<T>();
		for (SearchHit hit : response.getHits()) {
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			result.add(pojo);
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + result.size());
		return result;

	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.FIND_ID)
	public <T> List<T> findByIdList(String index, List<Long> ids, Class<T> resultClass) {
		PageRequest pr = PageRequest.of(0, 200, Sort.by(DEFAULT_SORT_FIELD));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(ids)).withIndices(index)
				.withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			logger.info("resp :" + resp);
			return resp;
		});
		logger.debug("We are here For printing");
		List<T> result = new ArrayList<>();
		for (SearchHit hit : response.getHits()) {
			logger.info("hit Data :" + hit.getSourceAsMap());
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			result.add(pojo);
		}
		logger.info("result size :" + result.size());
		return result;

	}

	public ElasticResponse searchByNameLike(int startPage, int pageSize, String index, ElasticSearchTerm searchTerm) {
		logger.debug(" pageNumber {} , pageSize {} ", startPage, pageSize);
		return searchByNameLike(startPage, pageSize, index, searchTerm, null, Direction.DESC, DEFAULT_SORT_FIELD);
	}

	public ElasticResponse searchByNameLike(String index, ElasticSearchTerm searchTerm, String name) {
		return searchByNameLike(0, DEFAULT_RESULT_SIZE, index, searchTerm, name);
	}

	public ElasticResponse searchByNameLike(int startPage, int pageSize, String index, ElasticSearchTerm searchTerm,
			String name) {
		return searchByNameLike(startPage, pageSize, index, searchTerm, name, Direction.DESC, DEFAULT_SORT_FIELD);
	}

	public ElasticResponse searchByNameLike(int startPage, int pageSize, String index, ElasticSearchTerm searchTerm,
			String name, String sort, Direction sortDirection) {
		return searchByNameLike(startPage, pageSize, index, searchTerm, null, sortDirection, sort);
	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.SEARCH + "-name")
	public ElasticResponse searchByNameLike(int startPage, int pageSize, String index, ElasticSearchTerm searchTerm,
			String name, Direction direction, String... sortBy) {
		ElasticResponse eResp = new ElasticResponse();
		logger.debug(" pageNumber {} , pageSize {} , searchTerm {} ", startPage, pageSize, searchTerm);
		PageRequest pr = PageRequest.of(startPage, pageSize, Sort.by(direction, sortBy));
		if (StringUtils.isNotBlank(name))
			searchTerm.setSerachInIdOrName(name);
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(searchTerm, index))
				.withIndices(index).withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		// logger.debug("We are here For printing response :" + response.toString());
		List<StatusBaseObject> result = new ArrayList<StatusBaseObject>();
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			// logger.debug("SearchHit : " + hit.toString());
			StatusBaseObject pojo = mapper.convertValue(hit.getSourceAsMap(), StatusBaseObject.class);
			result.add(pojo);
			logger.info("hit Data {} , pojo  {} ", hit.getSourceAsMap(), pojo);
		}
		logger.info("result size :" + result.size());
		eResp.setData(result);
		return eResp;
	}

	public <T> EResponse<T> searchAsList(int startPage, int pageSize, String index, ElasticSearchTerm searchTerm,
			Class<T> resultClass, String sort, Direction dir) {
		List<T> listData = new ArrayList<>();
		EResponse<T> eResp = new EResponse<>();
		logger.debug(" pageNumber {} , pageSize {} , searchTerm {} ", startPage, pageSize, searchTerm);
		PageRequest pr = PageRequest.of(startPage, pageSize, Sort.by(dir, sort));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(searchTerm, index))
				.withIndices(index).withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		// logger.debug("We are here For printing response :" + response.toString());
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			// logger.debug("SearchHit : {} : {}", hit.toString(), hit.getId());
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			try {
				listData.add(pojo);
			} catch (Exception e) {

			}
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + listData.size());
		eResp.setData(listData);
		return eResp;
	}

	public List<StatusBaseObject> getAllStatusBasedEntity(String index) {
		return getAllStatusBasedEntity(index, 0, DEFAULT_RESULT_SIZE);
	}

	public List<StatusBaseObject> getAllStatusBasedEntity(String index, int pageNumber, int pageSize) {
		return getAllStatusBasedEntity(index, pageNumber, pageSize, Direction.DESC, DEFAULT_SORT_FIELD);
	}

	public List<StatusBaseObject> getAllStatusBasedEntity(String index, int pageNumber, int pageSize,
			Direction direction, String... sortBy) {
		PageRequest pr = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery()).withIndices(index)
				.withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		logger.debug("We are here For printing");
		List<StatusBaseObject> result = new ArrayList<StatusBaseObject>();
		for (SearchHit hit : response.getHits()) {
			StatusBaseObject pojo = mapper.convertValue(hit.getSourceAsMap(), StatusBaseObject.class);
			result.add(pojo);
			logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + result.size());
		return result;
	}

	public <T> List<T> searchAsList(String index, ElasticSearchTerm searchTerm, Class<T> resultClass) {
		List<T> listData = new ArrayList<>();
		ElasticResponse eResp = new ElasticResponse();
		PageRequest pr = PageRequest.of(0, DEFAULT_RESULT_SIZE, Sort.by(Direction.DESC, DEFAULT_SORT_FIELD));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(searchTerm, index))
				.withIndices(index).withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		// logger.debug("We are here For printing response :" + response.toString());
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			// logger.debug("SearchHit : {} : {}", hit.toString(), hit.getId());
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			try {
				listData.add(pojo);
			} catch (Exception e) {

			}
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + listData.size());
		return listData;
	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.FIND_DETAIL_BY_ID)
	public <T> T findDetailById(String index, String id, Class<T> resultClass) {
		PageRequest pr = PageRequest.of(0, DEFAULT_RESULT_SIZE, Sort.by(DEFAULT_SORT_FIELD));
		BoolQueryBuilder boolquery = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", id));
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(boolquery).withIndices(index).withPageable(pr)
				.build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});
		logger.debug("We are here For printing index {} ", index);
		List<T> result = new ArrayList<T>();
		for (SearchHit hit : response.getHits()) {
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			result.add(pojo);
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info("result size :" + result.size());
		return (result != null && result.size() > 0) ? result.get(0) : null;
	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.SEARCH)
	public <T> EResponse<T> searchAll(String index, ElasticSearchTerm searchTerm, Class<T> resultClass) {
		logger.info(" Quering Elastic Search for index : {} : searchTerm : {} ", index, searchTerm);
		EResponse<T> eResp = new EResponse<>();
		List<T> data = new ArrayList<>();

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		queryBuilder.withQuery(getQueryBuilder(searchTerm, index)).withIndices(index);

		if (CollectionUtils.isNotEmpty(searchTerm.getSortList())) {
			for (String sortKey : searchTerm.getSortList()) {
				SortBuilder<?> sortBuilder = null;
				sortBuilder = ServiceUtils.getSortBuilder(sortKey);
				queryBuilder.withSort(sortBuilder);
			}
		}

		PageRequest pr = PageRequest.of(searchTerm.getPageNumber() > 0 ? searchTerm.getPageNumber() - 1 : 0,
				searchTerm.getPageSize());
		SearchQuery query = queryBuilder.withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});

		logger.info("Total records present for search without pagenation: {} ", response.getHits().totalHits);
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			try {
				data.add(pojo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("Queried Elastic Search for {} : {}. Got size :  ", index, searchTerm, data.size());
		logger.debug("result data : {}", data);
		eResp.setData(data);
		return eResp;

	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.SEARCH)
	public <T> Map<Long, T> search(String index, ElasticSearchTerm searchTerm, Class<T> resultClass) {
		logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
		Map<Long, T> mapData = new HashMap<Long, T>();
		ElasticResponse eResp = new ElasticResponse();

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		queryBuilder.withQuery(getQueryBuilder(searchTerm, index)).withIndices(index);

		if (CollectionUtils.isNotEmpty(searchTerm.getSortList())) {
			for (String sortKey : searchTerm.getSortList()) {
				SortBuilder<?> sortBuilder = null;
				sortBuilder = ServiceUtils.getSortBuilder(sortKey);
				queryBuilder.withSort(sortBuilder);
			}
		}

		PageRequest pr = PageRequest.of(searchTerm.getPageNumber(), searchTerm.getPageSize());
		SearchQuery query = queryBuilder.withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});

		// logger.debug("We are here For printing response :" + response.toString());
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			// logger.debug("SearchHit : {} : {}", hit.toString(), hit.getId());
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			try {
				mapData.put(Long.parseLong(hit.getId()), pojo);
			} catch (Exception e) {

			}
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
		logger.info("result size :" + mapData.size());
		return mapData;

	}

	/*
	 * @LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC +
	 * GraphiteConstants.SEARCH) public <T> Map<Long, T> search(String index,
	 * ElasticSearchTerm searchTerm, Class<T> resultClass) {
	 * logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
	 * Map<Long, T> mapData = new HashMap<Long, T>(); ElasticResponse eResp = new
	 * ElasticResponse(); PageRequest pr = PageRequest.of(0, 9999); SearchQuery
	 * query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(searchTerm,
	 * index)) .withIndices(index).withPageable(pr).build(); SearchResponse response
	 * = elasticsearchTemplate.query(query, resp -> { return resp; });
	 * 
	 * // logger.debug("We are here For printing response :" + response.toString());
	 * eResp.setTotalNoOfRecords(response.getHits().totalHits); for (SearchHit hit :
	 * response.getHits()) { // logger.debug("SearchHit : {} : {}", hit.toString(),
	 * hit.getId()); T pojo = mapper.convertValue(hit.getSourceAsMap(),
	 * resultClass); try { mapData.put(Long.parseLong(hit.getId()), pojo); } catch
	 * (Exception e) {
	 * 
	 * } // logger.info("hit Data :" + hit.getSourceAsMap()); }
	 * logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
	 * logger.info("result size :" + mapData.size()); return mapData;
	 * 
	 * }
	 */

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.SEARCH)
	public <T> Map<Long, T> searchByNameExact(String index, ElasticSearchTerm searchTerm, Set<String> names,
			Class<T> resultClass) {
		logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
		Map<Long, T> mapData = new HashMap<Long, T>();
		ElasticResponse eResp = new ElasticResponse();
		PageRequest pr = PageRequest.of(0, DEFAULT_RESULT_SIZE * 100);
		SearchQuery query = new NativeSearchQueryBuilder().withQuery(getQueryBuilder(searchTerm, names, index))
				.withIndices(index).withPageable(pr).build();
		SearchResponse response = elasticsearchTemplate.query(query, resp -> {
			return resp;
		});

		// logger.debug("We are here For printing response :" + response.toString());
		eResp.setTotalNoOfRecords(response.getHits().totalHits);
		for (SearchHit hit : response.getHits()) {
			// logger.debug("SearchHit : {} : {}", hit.toString(), hit.getId());
			T pojo = mapper.convertValue(hit.getSourceAsMap(), resultClass);
			try {
				mapData.put(Long.parseLong(hit.getId()), pojo);
			} catch (Exception e) {

			}
			// logger.info("hit Data :" + hit.getSourceAsMap());
		}
		logger.info(" Quering Elastic Search for {} : {} ", index, searchTerm);
		logger.info("result size :" + mapData.size());
		return mapData;

	}

	@LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.ELASTIC + GraphiteConstants.SEARCH)
	public <T> Map<Long, T> searchAllByExactNames(String index, ElasticSearchTerm searchTerm, Set<String> names,
			Class<T> resultClass) {
		Map<Long, T> mapData = new HashMap<>();

		SearchResponse scrollResp = elasticsearchTemplate.getClient().
				prepareSearch(index)
				.setScroll(new TimeValue(60000))
				.setSize(5000)
				.setTypes(index)
				.setQuery(getQueryBuilder(searchTerm, names, index))
				.execute().actionGet();

		try {
			do {
				for (SearchHit hit : scrollResp.getHits().getHits()) {
					T pojo = mapper.readValue(hit.getSourceAsString(), resultClass);
					mapData.put(Long.parseLong(hit.getId()), pojo);
				}
				scrollResp = elasticsearchTemplate.getClient().prepareSearchScroll(scrollResp.getScrollId())
						.setScroll(new TimeValue(60000))
						.execute()
						.actionGet();
			} while (scrollResp.getHits().getHits().length != 0);
		} catch (IOException e) {
			logger.error("Exception while executing query {}", e.getMessage());
		}
		return mapData;

	}

	private static QueryBuilder getQueryBuilder(List<Long> ids) {
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (CollectionUtils.isNotEmpty(ids) && !ids.isEmpty())
			query = query.must(QueryBuilders.termsQuery("id", ids.toArray()));
		return query;

	}

	private static QueryBuilder getQueryBuilder(ElasticSearchTerm searchTerm, String index) {
		return getQueryBuilder(searchTerm, null, index);
	}

	private static QueryBuilder getQueryBuilder(ElasticSearchTerm searchTerm, Set<String> names, String index) {
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (CollectionUtils.isNotEmpty(searchTerm.getLicensies()))
			query = query.must(QueryBuilders.termsQuery("licensee".equalsIgnoreCase(index) ? "id" : "licenseeId",
					searchTerm.getLicensies().toArray()));
		if (CollectionUtils.isNotEmpty(searchTerm.getAdvertisers()))
			query = query.must(QueryBuilders.termsQuery("advertiser".equalsIgnoreCase(index) ? "id" : "advertiserId",
					searchTerm.getAdvertisers().toArray()));
		if (CollectionUtils.isNotEmpty(searchTerm.getCampaigns()))
			query = query.must(QueryBuilders.termsQuery("campaign".equalsIgnoreCase(index) ? "id" : "campaignId",
					searchTerm.getCampaigns().toArray()));

		if (CollectionUtils.isNotEmpty(searchTerm.getStrategies()))
			query = query.must(QueryBuilders.termsQuery("strategy".equalsIgnoreCase(index) ? "id" : "strategyId",
					searchTerm.getStrategies().toArray()));
		
		if (CollectionUtils.isNotEmpty(searchTerm.getAggregators()))
			query = query.must(QueryBuilders.termsQuery("aggregator".equalsIgnoreCase(index) ? "id" : null,
					searchTerm.getAggregators().toArray()));

		if (CollectionUtils.isNotEmpty(names))
			query = query.must(QueryBuilders.termsQuery("name.keyword", names.toArray()));
		if (searchTerm.getFilters() != null) {
			for (Entry<String, Set<String>> keyValue : searchTerm.getFilters().entrySet()) {
				logger.debug("  keyValue {} , {} , {} ", keyValue.getKey(), keyValue.getValue(), index);
				if (TablesEntity.isValidFilter(index, keyValue.getKey())
						&& CollectionUtils.isNotEmpty(keyValue.getValue())) {
					logger.debug("  isValidFilter  ");
					query = query.must(QueryBuilders.termsQuery(keyValue.getKey(), keyValue.getValue().toArray()));
				} else {
					logger.debug("Not A   ValidFilter  ");

				}
			}
		}
		if (StringUtils.isNotBlank(searchTerm.getSerachInIdOrName())) {
			QueryBuilder qb = makeQueryString(searchTerm.getSerachInIdOrName());
			query = query.must(qb);
		}

		return query;

	}

	private static QueryBuilder makeQueryString(String serachInIdOrName) {
		if (StringUtils.isNumeric(serachInIdOrName)) {
			int idsArray[] = { Integer.parseInt(serachInIdOrName) };
			return QueryBuilders.termsQuery("id", idsArray);
		}
		return QueryBuilders.queryStringQuery("*" + serachInIdOrName + "*").defaultField("name")
				.defaultOperator(Operator.AND);
	}

	/**
	 * Save to elasticSearch.
	 *
	 * @param baseModel   the base model
	 * @param tableEntity the table entity
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
	public String save(BaseModel baseModel, TablesEntity tableEntity) {
		String id = null;
		try {
			if (!elasticsearchTemplate.indexExists(tableEntity.getElasticIndex()))
				elasticsearchTemplate.createIndex(tableEntity.getElasticIndex());

			IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(baseModel.getId()))
					.withIndexName(tableEntity.getElasticIndex()).withType(tableEntity.getElasticIndex())
					.withSource(mapper.writeValueAsString(baseModel)).build();

			id = elasticsearchTemplate.index(indexQuery);
			logger.debug("Elastic search :: data saving ..... saved to elastic for index : {} id : {}, baseModel {} ",
					tableEntity.getElasticIndex(), id, baseModel);
			elasticsearchTemplate.refresh(tableEntity.getElasticIndex());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while saving " + baseModel.getName() + " to elastic search.");
		}
		return id;
	}
}
