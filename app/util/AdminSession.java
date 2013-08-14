package util;

import models.Agent;
import models.admin.SysAdmin;

public class AdminSession {

	Long adminId;
	
	private String UUID;

	private SysAdmin admin;
	
	public SysAdmin getUser() {
		return admin;
	}

	public void setUser(SysAdmin admin) {
		this.admin = admin;
		this.adminId = this.admin.getId();
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uuid) {
		this.UUID = uuid;
	}

	public Long getAgentId() {
		return adminId;
	}

	public void setId(Agent user) {
		if(user != null){
			this.adminId = user.getId();
		}
	}
}
