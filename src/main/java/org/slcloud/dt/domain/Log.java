package org.slcloud.dt.domain;

import java.util.Date;

public class Log {
	
	private long task_id;
	private String message;
	private Date time;
	private int type;
	
	public Log() {
		super();
	}
	
	
	
	public Log(long task_id, String message) {
		super();
		this.task_id = task_id;
		this.message = message;
		this.time = new Date();
		this.type = 0;
	}

	public Log(long task_id, String message, int type) {
		super();
		this.task_id = task_id;
		this.message = message;
		this.type = type;
	}



	public Log(long task_id, String message, Date time, int type) {
		super();
		this.task_id = task_id;
		this.message = message;
		this.time = time;
		this.type = type;
	}
	public long getTask_id() {
		return task_id;
	}
	public void setTask_id(long task_id) {
		this.task_id = task_id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
