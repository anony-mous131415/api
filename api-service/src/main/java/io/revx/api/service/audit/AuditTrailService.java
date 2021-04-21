package io.revx.api.service.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.revx.api.audit.AuditDetails;
import io.revx.api.audit.AuditMarker;
import io.revx.api.mysql.entity.UserInfoEntity;
import io.revx.api.mysql.entity.audit.AuditChange;
import io.revx.api.mysql.entity.audit.AuditLog;
import io.revx.api.mysql.repo.UserRepository;
import io.revx.api.mysql.repo.audit.AuditChangeRepository;
import io.revx.api.mysql.repo.audit.AuditLogRepository;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.response.ApiResponseObject;

@Component
public class AuditTrailService {
	private static Logger logger = LogManager.getLogger(AuditTrailService.class);

	@Autowired
	AuditLogRepository auditLogRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	StrategyRepository strategyRepository;

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	AuditChangeRepository auditChangeRepository;

	@Autowired
	EntityESService elasticSearch;

	public ApiResponseObject<List<AuditMarker>> getLog(long id, long startTime, long endTime, DashBoardEntity entity)
			throws ValidationException {
		List<AuditLog> logs = new ArrayList<AuditLog>();
		HashMap<Long, String> entityDetails = new HashMap<Long, String>();

		logs = fillEntityDetails(id, startTime, endTime, entity, logs, entityDetails);

		List<AuditMarker> auditMarkerList = new ArrayList<>();
		if (logs != null && !logs.isEmpty()) {
			auditMarkerList = populateAuditMarkerList(logs, entityDetails);
		}

		ApiResponseObject<List<AuditMarker>> resp = new ApiResponseObject<>();
		resp.setRespObject(auditMarkerList);
		return resp;
	}

	public ApiResponseObject<List<AuditDetails>> getAuditDetails(long id, long startTime, long endTime,
			DashBoardEntity entity) throws ValidationException {
		List<AuditLog> logs = new ArrayList<AuditLog>();
		HashMap<Long, String> entityDetails = new HashMap<Long, String>();

		logs = fillEntityDetails(id, startTime, endTime, entity, logs, entityDetails);

		List<AuditDetails> auditDetails = new ArrayList<>();
		if (logs != null && !logs.isEmpty()) {
			auditDetails = populateAuditDetails(logs, entityDetails);
		}

		ApiResponseObject<List<AuditDetails>> resp = new ApiResponseObject<>();
		resp.setRespObject(auditDetails);
		return resp;
	}

	private List<AuditMarker> populateAuditMarkerList(List<AuditLog> Al, Map<Long, String> entityDetails) {

		List<Long> logIds = new ArrayList<Long>();
		List<Integer> userIds = new ArrayList<Integer>();
		for (AuditLog log : Al) {
			logIds.add(log.getId());
			log.setMsg("");
			userIds.add((int) log.getUserId());
		}

		List<UserInfoEntity> userInfoList = userRepository.findAllById(userIds);
		Map<Integer, String> userDetails = new HashMap<Integer, String>();
		for (UserInfoEntity entity : userInfoList) {
			userDetails.put(entity.getId(), entity.getUsername());
		}

		List<AuditChange> auditChangeList = auditChangeRepository.findByAuditLogId(logIds);

		List<AuditMarker> auditMarkerList = new ArrayList<AuditMarker>();
		for (AuditLog log : Al) {
			long logId = log.getId();
			StringBuffer sb = new StringBuffer();
			for (AuditChange auditChange : auditChangeList) {
				if (auditChange.getAuditLogId() == logId) {
					sb.append(auditChange.getFieldName() + ",");
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			log.setMsg(sb.toString());
			AuditMarker marker = new AuditMarker();
			marker.setId(log.getId());
			marker.setType(log.getType());
			marker.setTimestamp(log.getTimestamp());
			marker.setEntity_id(log.getEntityId());
			marker.setEntity_type(log.getEntityType());
			marker.setEntity_name(entityDetails.get(log.getEntityId()));
			marker.setMessage(log.getMsg());
			if (log.getUserId() != -1) {
				marker.setUser_name(userDetails.get((int) log.getUserId()));
			} else {
				marker.setUser_name("-1");
			}

			auditMarkerList.add(marker);
		}

		return auditMarkerList;

	}

	private List<AuditDetails> populateAuditDetails(List<AuditLog> Al, Map<Long, String> entityDetails) {
		List<Long> logIds = new ArrayList<Long>();
		List<Integer> userIds = new ArrayList<Integer>();
		for (AuditLog log : Al) {
			logIds.add(log.getId());
			userIds.add((int) log.getUserId());
		}

		List<UserInfoEntity> userInfoList = userRepository.findAllById(userIds);
		Map<Integer, String> userDetails = new HashMap<Integer, String>();
		for (UserInfoEntity entity : userInfoList) {
			userDetails.put(entity.getId(), entity.getUsername());
		}

		List<AuditChange> auditChangeList = auditChangeRepository.findByAuditLogId(logIds);

		List<AuditDetails> auditDetailsList = new ArrayList<>();

		for (AuditLog log : Al) {
			long logId = log.getId();
			Set<AuditChange> auditChangeSet = new HashSet<AuditChange>();
			for (AuditChange auditChange : auditChangeList) {
				if (auditChange.getAuditLogId() == logId) {
					auditChangeSet.add(auditChange);
				}
			}

			AuditDetails auditDetail = new AuditDetails();
			auditDetail.setId(log.getId());
			auditDetail.setType(log.getType());
			auditDetail.setTimestamp(log.getTimestamp());
			auditDetail.setEntity_id(log.getEntityId());
			auditDetail.setEntity_type(log.getEntityType());
			auditDetail.setEntity_name(entityDetails.get(log.getEntityId()));
			auditDetail.setMessage(log.getMsg());
			if (log.getUserId() != -1) {
				auditDetail.setUser_name(userDetails.get((int) log.getUserId()));
			} else {
				auditDetail.setUser_name("-1");
			}
			auditDetail.setChanges(auditChangeSet);
			auditDetailsList.add(auditDetail);
		}
		return auditDetailsList;

	}

	/**
	 * Method to get the audit logs and populate entity details based on entity,
	 * start and end time
	 * 
	 * @param id
	 * @param startTime
	 * @param endTime
	 * @param entity
	 * @param auditLogs
	 * @param entityDetails
	 * @return
	 * @throws ValidationException
	 */
	private List<AuditLog> fillEntityDetails(long id, long startTime, long endTime, DashBoardEntity entity,
			List<AuditLog> auditLogs, HashMap<Long, String> entityDetails) throws ValidationException {

		if (entity.equals(DashBoardEntity.STRATEGY)) {
			auditLogs = auditLogRepository.findAuditLogByStrategyId(id, startTime, endTime);
			BaseModel model = getEntityDetailsFromId(TablesEntity.STRATEGY, id);
			if (model == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Invalid Strategy Id. " });
			}
			entityDetails.put(model.getId(), model.getName());

		} else if (entity.equals(DashBoardEntity.CAMPAIGN)) {
			BaseModel model = getEntityDetailsFromId(TablesEntity.CAMPAIGN, id);
			if (model == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Invalid Campaign Id. " });
			}
			entityDetails.put(model.getId(), model.getName());
			List<Long> strategyIds = strategyRepository.findStrategyIdsFromCampaingId(id);
			List<BaseModel> entities = getEntityDetailsFromIdList(TablesEntity.STRATEGY, strategyIds);
			if (entities != null) {
				for (BaseModel bm : entities) {
					entityDetails.put(bm.getId(), bm.getName());
				}
			}
			if (strategyIds == null || strategyIds.size() == 0) {
				auditLogs = auditLogRepository.findAuditLogByCampaignId(id, startTime, endTime);
			} else {
				auditLogs = auditLogRepository.findAuditLogByCampaignIdAndStrategyIds(id, strategyIds, startTime,
						endTime);
			}
		} else {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] { "Entity in query params should be CAMPAIGN or STRATEGY. " });
		}

		return auditLogs;
	}

	/**
	 * Function to get (id, name) pair from elastic search for an given entity and
	 * id
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	private BaseModel getEntityDetailsFromId(TablesEntity entity, long id) {
		return elasticSearch.searchPojoById(entity, id);
	}

	/**
	 * Function to get (id, name) pair from elastic search for an given entity and
	 * list of ids
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	private List<BaseModel> getEntityDetailsFromIdList(TablesEntity entity, List<Long> ids) {
		if (ids == null || ids.size() == 0) {
			return null;
		}

		List<?> list = elasticSearch.searchPojoByIdList(entity, ids);
		if (list != null && !list.isEmpty()) {
			return (List<BaseModel>) list;
		}

		return null;
	}

}
