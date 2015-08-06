package org.slcloud.dt.domain;

public class ColumnMap {

	private long id;
	private String s_column;
	private String t_column;
	private int type;
	private int t_index;
	
	public ColumnMap() {
		super();
	}
	public ColumnMap(long id,String s_column, String t_column,int type,int t_index) {
		super();
		this.id = id;
		this.s_column = s_column;
		this.t_column = t_column;
		this.type = type;
		this.t_index = t_index;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getS_column() {
		return s_column;
	}
	public void setS_column(String s_column) {
		this.s_column = s_column;
	}
	public String getT_column() {
		return t_column;
	}
	public void setT_column(String t_column) {
		this.t_column = t_column;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getT_index() {
		return t_index;
	}
	public void setT_index(int t_index) {
		this.t_index = t_index;
	}
}
