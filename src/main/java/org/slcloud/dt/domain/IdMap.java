package org.slcloud.dt.domain;

public class IdMap {

	private long id;
	private String s_id;
	private String t_id;
	
	public IdMap() {
		super();
	}
	public IdMap(long id, String s_id, String t_id) {
		super();
		this.id = id;
		this.s_id = s_id;
		this.t_id = t_id;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getS_id() {
		return s_id;
	}
	public void setS_id(String s_id) {
		this.s_id = s_id;
	}
	public String getT_id() {
		return t_id;
	}
	public void setT_id(String t_id) {
		this.t_id = t_id;
	}
}
