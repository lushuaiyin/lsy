package com.example.one.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBean {

	private String type;
	private String mess;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	
	
	@Override
	public String toString() {
		return "MessageBean [type=" + type + ", mess=" + mess + "]";
	}
}
