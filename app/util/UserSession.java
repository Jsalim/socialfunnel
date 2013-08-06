package util;

import java.util.ArrayList;
import java.util.List;

import models.AgentNotification;
import models.Brand;
import models.Agent;

public class UserSession {

	Agent user;
	String UUID;
	Long id;
	Brand brand;
	List<AgentNotification> unSeenNotis = new ArrayList<AgentNotification>();
	List<AgentNotification> lastNotis = new ArrayList<AgentNotification>();
	
	public Agent getUser() {
		return user;
	}

	public void setUser(Agent user) {
		this.user = user;
		this.id = this.user.getId();
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uuid) {
		this.UUID = uuid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Agent user) {
		if(user != null){
			this.id = user.getId();
		}
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public List<AgentNotification> getUnSeenNotis() {
		return unSeenNotis;
	}

	public void setUnSeenNotis(List<AgentNotification> unSeenNotis) {
		this.unSeenNotis = unSeenNotis;
	}

}
