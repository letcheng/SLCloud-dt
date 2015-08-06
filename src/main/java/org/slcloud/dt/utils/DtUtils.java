package org.slcloud.dt.utils;



import java.sql.Driver;


import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class DtUtils {

	public final static int MYSQL_DRIVER = 1;
	public final static int OARCLE_DRIVER = 2;

	public static JdbcTemplate getJdbcTemplate(Class<? extends Driver> driverClass,String uri,String username,String password) {

		return new JdbcTemplate(new SimpleDriverDataSource(BeanUtils.instantiateClass(driverClass), uri, username, password));
	}
	
	public static JdbcTemplate getConfJdbcTemplate(){
		return DtUtils.getJdbcTemplate(DtUtils.getDriverClass(1), DtUtils.getUri(1, "localhost", 3306, "dt"), "root", "njsslj");
	}

	public static String getUri(int type,String host,int port,String instance){
		switch (type) {
		case 1:
			return "jdbc:mysql://"+host+":"+port+"/"+instance;
		case 2:
			return "jdbc:oracle:thin:@"+host+":"+port+":"+instance;
		default:
			return null;
		}
	}
	
	public static Class<? extends Driver> getDriverClass(int type){
		switch (type) {
		case 1:
			return com.mysql.jdbc.Driver.class;
		case 2:
			return oracle.jdbc.OracleDriver.class;
		default:
			return null;
		}
	}
}
