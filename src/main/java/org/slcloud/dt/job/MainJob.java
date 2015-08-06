package org.slcloud.dt.job;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import org.slcloud.dt.domain.ColumnMap;
import org.slcloud.dt.domain.IdMap;
import org.slcloud.dt.domain.Log;
import org.slcloud.dt.service.LogService;
import org.slcloud.dt.service.impl.LogServiceImpl;
import org.slcloud.dt.utils.DtUtils;

public class MainJob implements Job{

	long s_source_id;
	long t_source_id;
	String s_table;
	String t_table;
	String s_timecolumn;
	String t_timecolumn;
	String s_idcolumn;
	String t_idcolumn;
	String condition;
	Date create_time;

	JdbcTemplate jt_conf = null;
	JdbcTemplate jt_source = null;
	JdbcTemplate jt_target = null;

	List<IdMap> idMaps = new ArrayList<IdMap>(); //映射信息
	List<ColumnMap> columnMaps= new ArrayList<ColumnMap>();//列映射信息

	List<Map<String, Object>> list;// 中间结果集
	
	LogService logService = null;

	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		final int task_id = context.getMergedJobDataMap().getInt("task_id"); //取得任务信息
		jt_conf = DtUtils.getConfJdbcTemplate(); //获得配置信息的数据源
		logService = new LogServiceImpl(jt_conf);

		
		//查询任务信息
		try {
			jt_conf.query("SELECT t.s_source_id,t.t_source_id,t.s_table,t.t_table,t.s_timecolumn,t.t_timecolumn,s_idcolumn,t_idcolumn,t.create_time,t.condition FROM dt_task t WHERE t.id = ?", 
					new Object[]{task_id},
					new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					s_source_id = rs.getLong("s_source_id");
					t_source_id = rs.getLong("t_source_id");
					s_table = rs.getString("s_table");
					t_table = rs.getString("t_table");
					s_timecolumn = rs.getString("s_timecolumn");
					t_timecolumn = rs.getString("t_timecolumn");
					s_idcolumn = rs.getString("s_idcolumn");
					t_idcolumn = rs.getString("t_idcolumn");
					condition = rs.getString("condition");
					create_time = rs.getDate("create_time");
				}
			});
		} catch (DataAccessException e) {
			logService.insert(new Log(task_id, "查询任务信息失败",1));
			
			return;
		}
		
		//查询 "源" 数据源。
		try {
			jt_conf.query("SELECT t.db_type,t.db_host,t.db_port,t.db_instance,t.db_username,t.db_pwd FROM dt_source t WHERE t.id = ?", 
					new Object[]{s_source_id},
					new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					jt_source = DtUtils.getJdbcTemplate(DtUtils.getDriverClass(rs.getInt("db_type")),
							DtUtils.getUri(rs.getInt("db_type"), rs.getString("db_host"), rs.getInt("db_port"), rs.getString("db_instance")), 
							rs.getString("db_username"), 
							rs.getString("db_pwd"));
				}
			});
		} catch (DataAccessException e) {
			logService.insert(new Log(task_id, "查询源库信息失败",1));
			
			return;
		}
		

		//查询 "目标" 数据源。
		try {
			jt_conf.query("SELECT t.db_type,t.db_host,t.db_port,t.db_instance,t.db_username,t.db_pwd FROM dt_source t WHERE t.id = ?", 
					new Object[]{t_source_id},
					new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					jt_target = DtUtils.getJdbcTemplate(DtUtils.getDriverClass(rs.getInt("db_type")),
							DtUtils.getUri(rs.getInt("db_type"), rs.getString("db_host"), rs.getInt("db_port"), rs.getString("db_instance")), 
							rs.getString("db_username"), 
							rs.getString("db_pwd"));
				}
			});
		} catch (DataAccessException e) {
			logService.insert(new Log(task_id, "查询目标库信息失败",1));
			
			return;
		}


		//查询站码 id 映射信息
		try {
			jt_conf.query("SELECT t.id,t.s_id,t.t_id FROM dt_id_map t WHERE t.task_id = ?", 
					new Object[]{task_id},
					new RowCallbackHandler() {
				public void processRow(ResultSet rs) throws SQLException {
					idMaps.add(new IdMap(rs.getLong("id"), rs.getString("s_id"), rs.getString("t_id")));
				}
			});
		} catch (DataAccessException e) {
			logService.insert(new Log(task_id, "查询站码信息失败",1));
			
			return;
		}


		//开始对每个站点数据进行汇集
		for (IdMap idMap : idMaps) {
			
			//开始查询列映射信息
			columnMaps.clear();
			try {
				//查询列映射信息
				jt_conf.query("SELECT t.id,t.s_column,t.t_column,t.type FROM dt_column_map_task t WHERE t.task_id = ?", 
						new Object[]{task_id},
						new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						columnMaps.add(new ColumnMap(rs.getLong("id"),rs.getString("s_column"), rs.getString("t_column"),rs.getInt("type"),0));
					}
				});

				/*	
				for(int i=0;i<columnMaps.size();i++){
					final int tmp = i;
					jt_conf.query("SELECT t.s_column FROM dt_column_map t WHERE t.map_id = ? AND t.map_column_id = ?", 
							new Object[]{idMap.getId(),columnMaps.get(tmp).getId()},
							new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							columnMaps.get(tmp).setS_column(rs.getString("s_column"));
						}
					});
				}*/
			} catch (DataAccessException e) {
				logService.insert(new Log(task_id, "查询列映射信息失败",1));
				
				e.printStackTrace();
				return;
			}
			
			//查询"目标"数据源的最近一条记录的时间
			Date t_last_time = null;
			try {
				t_last_time = jt_target.queryForObject("SELECT MAX(t."+t_timecolumn+") FROM "+t_table+" t WHERE t."+t_idcolumn+"='"+idMap.getT_id()+"'",
							Date.class);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			t_last_time = t_last_time==null? create_time:t_last_time;

			String sql_column_string = "";
			for (ColumnMap columnMap : columnMaps) {
				if(sql_column_string.length()==0){
					sql_column_string += columnMap.getS_column();
				}else{
					sql_column_string += ","+columnMap.getS_column();
				}
			}
			
			//查询源数据集
			try {
				
				String sql = "SELECT "+sql_column_string+" FROM "+s_table+" t WHERE t."+s_idcolumn+"='"+idMap.getS_id()+"' AND t.tm > to_date('"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t_last_time)+"','yyyy-mm-dd hh24:mi:ss')";
				if(condition!=null&&!condition.equals("")){
					sql += "AND "+condition;
				}
				list = jt_source.queryForList(sql);
			} catch (DataAccessException e) {
				logService.insert(new Log(task_id, "查询"+idMap.getS_id()+"源数据集失败",1));
				
				e.printStackTrace();
				return;
			}

			final int count = list.size();
			if(count>0){
				
				for (Map<String, Object> map : list) {
					map.put(s_idcolumn,idMap.getT_id());
				}
				
				//开始批量插入
				sql_column_string = "";
				String sql_column_target = "";
				for (int i=0;i<columnMaps.size();i++) {
					columnMaps.get(i).setT_index(i+1);
					if(sql_column_string.length()==0){
						sql_column_string += columnMaps.get(i).getT_column();
						sql_column_target += "?";
					}else{
						sql_column_string += ","+columnMaps.get(i).getT_column();
						sql_column_target += ",?";
					}
				}
				try {
					int result[] = jt_target.batchUpdate("INSERT INTO "+t_table+"("+sql_column_string+") VALUES("+sql_column_target+")", new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							for(int j=0;j<columnMaps.size();j++){
								ColumnMap columnMap = columnMaps.get(j);
								switch (columnMap.getType()) {
								case 3: //时间型
									ps.setTimestamp(columnMap.getT_index(), (java.sql.Timestamp) list.get(i).get(columnMap.getS_column()));
									break;
								case 2: //整型
									ps.setInt(columnMap.getT_index(), (Integer) list.get(i).get(columnMap.getS_column()));
									break;
								case 1: //浮点型
									ps.setBigDecimal(columnMap.getT_index(), (BigDecimal) list.get(i).get(columnMap.getS_column()));
									break;
								default://字符串
									ps.setString(columnMap.getT_index(), (String) list.get(i).get(columnMap.getS_column()));
									break;
								}
							}
						}
						public int getBatchSize() {
							return count;
						}
					});
					logService.insert(new Log(task_id, idMap.getS_id()+",共"+result.length+"条数据"));
				} catch (Exception e) {
					logService.insert(new Log(task_id, "写入数据失败",1));
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
