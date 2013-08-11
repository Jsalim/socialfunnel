import java.util.HashSet;

import javax.persistence.EntityManager;

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
		UserRole admin = em.find(UserRole.class, 1l);
		UserRole agent = em.find(UserRole.class, 2l);
		em.getTransaction().begin();
		if (admin == null) {
			admin = new UserRole();
			admin.permissions = new HashSet<Permissions>();
			admin.permissions.add(Permissions.ALL);
			admin.name = RoleName.ADMIN;
			em.persist(admin);
		}
		if (agent == null) {
			agent = new UserRole();
			agent.name = RoleName.AGENT;
			agent.permissions = new HashSet<Permissions>();
			agent.permissions.add(Permissions.VIEW_INTERACTIONS);
			agent.permissions.add(Permissions.VIEW_MONITORED_TERMS);
			agent.permissions.add(Permissions.VIEW_MEDIA_CHANNELS);
			agent.permissions.add(Permissions.VIEW_STREAM);
			agent.permissions.add(Permissions.ADD_INTERACTION);
			agent.permissions.add(Permissions.ADD_MONITORED_TERM);
			agent.permissions.add(Permissions.ADD_DYNAMIC_FORM);
			em.persist(agent);
		}
		em.getTransaction().commit();
		em.close();
	}
}
