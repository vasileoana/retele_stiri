package common;

import java.io.Serializable;

public class Topic implements Serializable{
	private String name;
	private String text;
	
	public Topic(String name, String text) {
		super();
		this.name = name;
		this.text = text;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
