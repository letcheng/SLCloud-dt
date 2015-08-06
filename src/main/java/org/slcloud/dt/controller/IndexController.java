package org.slcloud.dt.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slcloud.dt.utils.DtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@Autowired
	private SchedulerFactoryBean schedulerFactory;


	@RequestMapping(value="/source")
	public ModelAndView source(){
		ModelAndView mv = new ModelAndView("source");
		final List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		JdbcTemplate jt_conf = DtUtils.getConfJdbcTemplate();
		jt_conf.query("SELECT t.id,t.display_name,t.db_type,t.db_host,t.db_port,t.db_instance,t.db_username,t.db_pwd FROM dt_source t", 
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("id", rs.getLong("id"));
				map.put("display_name", rs.getString("display_name"));
				map.put("db_type", rs.getString("db_type"));
				map.put("db_host", rs.getString("db_host"));
				map.put("db_port", rs.getString("db_port"));
				map.put("db_instance", rs.getString("db_instance"));
				map.put("db_username", rs.getString("db_username"));
				map.put("db_pwd", rs.getString("db_pwd"));
				data.add(map);
			}
		});
		mv.addObject("data", data);
		return mv;
	}
	
	@RequestMapping(value="/pauseJob")
	public String pauseJob(@RequestParam("id")Integer id) throws SchedulerException{
		if(id!=0){
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = new TriggerKey("trigger"+id, "hhu");
			scheduler.pauseTrigger(triggerKey);
		}
		return "redirect:/";
	}
	
	@RequestMapping(value="/resumeJob")
	public String resumeJob(@RequestParam("id")Integer id) throws SchedulerException{
		if(id!=0){
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = new TriggerKey("trigger"+id, "hhu");
			scheduler.resumeTrigger(triggerKey);
		}
		return "redirect:/";
	}

	@RequestMapping(value="/")
	public ModelAndView index() throws SchedulerException{
		ModelAndView mv = new ModelAndView("index");
		final List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		final Scheduler scheduler = schedulerFactory.getScheduler();
		JdbcTemplate jt_conf = DtUtils.getConfJdbcTemplate();
		jt_conf.query("SELECT t1.id,t1.s_table,t1.t_table,t2.display_name AS s_name,t3.display_name AS t_name FROM dt_task t1,dt_source t2,dt_source t3 WHERE t1.s_source_id = t2.id AND t1.t_source_id = t3.id", 
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,Object> map = new HashMap<String, Object>();
				TriggerKey triggerKey = new TriggerKey("trigger"+rs.getInt("id"), "hhu");
				try {
					map.put("state", scheduler.getTriggerState(triggerKey));
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
				map.put("id", rs.getLong("id"));
				map.put("s_name", rs.getString("s_name"));
				map.put("t_name", rs.getString("t_name"));
				map.put("s_table", rs.getString("s_table"));
				map.put("t_table", rs.getString("t_table"));
				data.add(map);
			}
		});
		mv.addObject("data", data);
		return mv;
	}
}
