package com.hanxin.electrictool.entity;

import java.io.Serializable;

import android.R.integer;

public class ElectricData implements Serializable {
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String begin_time,result,user;
	public int id =0;
	
	public ElectricData() {
		super();

	}

	public ElectricData(String begin_time, String result,String user) {
		super();
		this.begin_time = begin_time;
		this.result = result;
		this.user =user;
	}
	

	public ElectricData(String begin_time, String result, String user,int id) {
		super();
		this.begin_time = begin_time;
		this.result = result;
		this.id = id;
		this.user =user;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}



	
}
