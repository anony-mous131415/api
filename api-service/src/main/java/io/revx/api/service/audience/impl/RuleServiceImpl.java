package io.revx.api.service.audience.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.audience.pojo.MetaRuleDto;
import io.revx.api.audience.pojo.MetaRulesDto;
import io.revx.api.audience.pojo.RuleFilterDto;
import io.revx.api.audience.pojo.RuleFiltersDto;
import io.revx.api.audience.pojo.RuleOperatorDto;
import io.revx.api.audience.pojo.RuleOperatorsDto;
import io.revx.api.audience.pojo.RuleValueDto;
import io.revx.api.mysql.amtdb.entity.RuleFilter;
import io.revx.api.mysql.amtdb.entity.RuleOperator;
import io.revx.api.mysql.amtdb.entity.RuleValue;
import io.revx.api.mysql.amtdb.repo.RuleFilterRepository;
import io.revx.api.mysql.amtdb.repo.RuleOperatorRepository;
import io.revx.api.utility.Util;
import io.revx.core.response.ApiResponseObject;

@Component
public class RuleServiceImpl{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RuleServiceImpl.class);

	@Autowired
	RuleFilterRepository ruleFilterDao;
	
	@Autowired
	RuleOperatorRepository ruleOperatorDao;

	public ApiResponseObject<MetaRulesDto> getAllRules() {
		MetaRulesDto metaRules = new MetaRulesDto();
		List<RuleFilter> ruleFilters = ruleFilterDao.findAll();
		LOGGER.debug("Found {} entries in rule filter table.", ruleFilters.size());
		for(RuleFilter ruleFilter : ruleFilters) {
			MetaRuleDto metaRule = new MetaRuleDto();
			metaRule.setId(ruleFilter.getId());
			metaRule.setName(ruleFilter.getFilterName());
			metaRule.setDisplayName(ruleFilter.getFilterDisplayName());
			metaRule.setFilterType(ruleFilter.getRuleFilterType());
			metaRule.setValueType(ruleFilter.getRuleValueType());
			metaRule.setFbxName(ruleFilter.getFbxFilterName());
			
			Set<RuleOperatorDto> ruleOperators = new HashSet<RuleOperatorDto>();
			for(RuleOperator ruleOperatorDto : ruleFilter.getRuleOperatorDto()) {
				RuleOperatorDto ruleOperator = Util.getVoFromTuple(ruleOperatorDto);
				ruleOperators.add(ruleOperator);
			}
			LOGGER.debug("Found {} operators for the filter {}", ruleFilter
					.getRuleOperatorDto().size(), ruleFilter.getFilterName());
			metaRule.setRuleOperators(ruleOperators);
			Set<RuleValueDto> ruleValues = new HashSet<RuleValueDto>();
			for(RuleValue ruleValue : ruleFilter.getRuleValueDto()) {
				RuleValueDto ruleValueDto = Util.getVoFromTuple(ruleValue);
				ruleValues.add(ruleValueDto);
			}
			LOGGER.debug("Found {} values for the filter {}", ruleFilter
					.getRuleValueDto().size(), ruleFilter.getFilterName());
			metaRule.setRuleValues(ruleValues);
			
			metaRules.addMetaRule(metaRule);
		}
		ApiResponseObject<MetaRulesDto> resp = new ApiResponseObject<>();
	    resp.setRespObject(metaRules);
		return resp;
	}

	public RuleFiltersDto getFilters() {
		RuleFiltersDto filters = new RuleFiltersDto();
		List<RuleFilter> filter = ruleFilterDao.findAll();
		for(RuleFilter dto : filter) {
			RuleFilterDto filterDto = Util.getVoFromTuple(dto);
			filters.addFilter(filterDto);
		}
		return filters;
	}

	public RuleOperatorsDto getOperators() {
		RuleOperatorsDto operators = new RuleOperatorsDto();
		List<RuleOperator> operator = ruleOperatorDao.findAll();
		for(RuleOperator dto : operator) {
			RuleOperatorDto operatorDto = Util.getVoFromTuple(dto);
			operators.addOperator(operatorDto);
		}
		return operators;
	}

}
