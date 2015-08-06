package org.slcloud.dt.service.impl;

import java.sql.Types;

import org.slcloud.dt.domain.Log;
import org.slcloud.dt.service.LogService;
import org.springframework.jdbc.core.JdbcTemplate;

public class LogServiceImpl implements LogService {

	JdbcTemplate jt_conf = null; //获得配置信息的数据源
	
	public LogServiceImpl(JdbcTemplate jt) {
		jt_conf = jt;
	}
	
	@Override
	public void insert(Log log) {
		jt_conf.update("INSERT INTO dt_log (task_id,time,message,type) VALUES (?,?,?,?)", 
				new Object[]{log.getTask_id(),log.getTime(),log.getMessage(),log.getType()},
				new int[]{Types.INTEGER,Types.TIMESTAMP,Types.VARCHAR,Types.INTEGER});
	}
}
