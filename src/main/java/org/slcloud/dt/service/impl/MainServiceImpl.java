package org.slcloud.dt.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slcloud.dt.job.MainJob;
import org.slcloud.dt.service.MainService;
import org.slcloud.dt.utils.DtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class MainServiceImpl implements MainService
{

	Scheduler scheduler;

	JdbcTemplate jt_conf;
	JobDetail job;
	Trigger trigger;

	@Autowired
	public MainServiceImpl(SchedulerFactoryBean schedulerFactory){
		scheduler =  schedulerFactory.getScheduler();
		jt_conf = DtUtils.getConfJdbcTemplate();
		try {
			jt_conf.query("SELECT t.id,t.interval FROM dt_task t WHERE t.is_disabled = 0", 
					new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					job = JobBuilder.newJob(MainJob.class)
							.withIdentity("job"+rs.getInt("id"), "hhu") 
							.usingJobData("task_id",rs.getInt("id"))
							.build();

					trigger = TriggerBuilder.newTrigger()
							.withIdentity("trigger"+rs.getLong("id"), "hhu")
							.startNow()
							.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(60*rs.getInt("interval"))
							.repeatForever())
							.build(); 
					try {
						scheduler.scheduleJob(job, trigger);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		schedulerFactory.start();
	}
}
