package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;

public class Channel implements Serializable{
	private String username;
	private String name;
	private String description;
	private List<Topic> topics;
	
	public Channel(String name, String description, List<Topic> topics) {
		super();
		this.name = name;
		this.description = description;
		this.topics = topics;
	}
	
	public Channel(String name, String description) {
		super();
		this.name = name;
		this.description = description;
		this.topics = new ArrayList<>();
	}
	
	public Channel() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}
	
	
	
}
