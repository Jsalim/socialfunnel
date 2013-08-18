import java.util.HashSet;

import javax.persistence.EntityManager;

import jobs.JobScheduler;

import bootstrap.DS;

import constants.Permissions;
import constants.RoleName;
import constants.States;

import models.UserRole;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.libs.F.Promise;
import play.libs.WS;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application arg0) {
		checkAndCreateBasicRoles();
		
		DS.initStore();
		
//		JobScheduler.getInstance().startTestJob();
		
		loadStates();
	}

	private void loadStates() {
		try{
			Promise<WS.Response> result = WS.url(Play.application().configuration().getString("das.address")).head();
			Logger.info(result.get().getStatus() + "");
		}catch (Exception e) {
			States.DAS_ON = false;
		}
	}
	
	private void checkAndCreateBasicRoles() {
		EntityManager em = JPA.em("default");
		UserRole adminRole = em.find(UserRole.class, 1l);
		UserRole agentRole = em.find(UserRole.class, 2l);
		em.getTransaction().begin();
		if (adminRole == null) {
			adminRole = new UserRole();
			adminRole.permissions = new HashSet<Permissions>();
			adminRole.permissions.add(Permissions.ALL);
			adminRole.name = RoleName.ADMIN;
			em.persist(adminRole);
		}
		if (agentRole == null) {
			agentRole = new UserRole();
			agentRole.name = RoleName.AGENT;
			agentRole.permissions = new HashSet<Permissions>();
			agentRole.permissions.add(Permissions.VIEW_INTERACTIONS);
			agentRole.permissions.add(Permissions.VIEW_MONITORED_TERMS);
			agentRole.permissions.add(Permissions.VIEW_MEDIA_CHANNELS);
			agentRole.permissions.add(Permissions.VIEW_STREAM);
			agentRole.permissions.add(Permissions.ADD_INTERACTION);
			agentRole.permissions.add(Permissions.ADD_MONITORED_TERM);
			agentRole.permissions.add(Permissions.ADD_DYNAMIC_FORM);
			em.persist(agentRole);
		}
		em.getTransaction().commit();
		em.close();
	}
}
