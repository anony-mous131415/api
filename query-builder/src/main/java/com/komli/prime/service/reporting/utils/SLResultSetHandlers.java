package com.komli.prime.service.reporting.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

public class SLResultSetHandlers {

	public static ResultSetHandler<Integer> COUNTROWSHANDLER = new ResultSetHandler<Integer>() {
		public Integer handle(ResultSet rs) throws SQLException {
			Integer rows = 0;
			while (rs.next()) {
				rows = rs.getInt(1);
			}
			return rows;
		}
	};
	
	public static ResultSetHandler<List<Integer>> LISTINTEGERHANDLER = new ResultSetHandler<List<Integer>>() {
		public List<Integer> handle(ResultSet rs) throws SQLException {
			List<Integer> list = new ArrayList<Integer>();
			while (rs.next()) {
				list.add(rs.getInt(1));
			}
			return list;
		}
	};
	
	public static ResultSetHandler<List<Map<String, Object>>> MAPHANDLER= new MapListHandler();

}