package util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import models.AgentNotification;
import models.Brand;
import models.Agent;

@Document
public class UserSession {

	@Id
	private String id;
	
	Long agentId;
	
	private String UUID;

	private Date lastRequest = new Date();
	
	@Transient
	private Agent user;
	@Transient
	private Brand brand;
	@Transient
	private List<AgentNotification> unSeenNotis = new ArrayList<AgentNotification>();
	@Transient
	private List<AgentNotification> lastNotis = new ArrayList<AgentNotification>();
	
	public Agent getUser() {
		return user;
	}

	public void setUser(Agent user) {
		this.user = user;
		this.agentId = this.user.getId();
		this.id = this.agentId+"";
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uuid) {
		this.UUID = uuid;
	}

	public Long getId() {
		return agentId;
	}

	public void setId(Agent user) {
		if(user != null){
			this.agentId = user.getId();
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

	public Date getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}

}
