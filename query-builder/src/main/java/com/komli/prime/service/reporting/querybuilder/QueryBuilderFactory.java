package com.komli.prime.service.reporting.querybuilder;

import com.komli.prime.service.reporting.pojo.InputParameters;
import com.komli.prime.service.reporting.querybuilder.api.Query;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.VerticaQueryBuilder;

public class QueryBuilderFactory {
	public static Query getQuery(InputParameters inputObj, String sessionId){
		return new VerticaQueryBuilder(inputObj, sessionId);
	}
}
