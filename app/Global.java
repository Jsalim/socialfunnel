import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import jobs.JobScheduler;

import bootstrap.Constants;
import bootstrap.DS;

import constants.AppTypes;
import constants.Permissions;
import constants.RoleName;
import constants.States;

import models.App;
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
		checkAndCreateDefaultApps();
		
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

	private void checkAndCreateDefaultApps() {
		EntityManager em = JPA.em("default");
		
		List<App> formbuilderList = em.createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "formbuilder").getResultList();
		List<App> knoledgeBaseList = em.createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "knoledgeBase").getResultList();
		List<App> socialnetworksList = em.createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "socialnetworks").getResultList();
		List<App> helpDeskList = em.createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "helpDesk").getResultList();
		List<App> reportList = em.createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "report").getResultList();
		
		em.getTransaction().begin();
		if(formbuilderList.size() < 1){
			App formbuilder = new App();
			formbuilder.setName("formbuilder"); formbuilder.getAppTypes().add(AppTypes.EMBEDDABLE_APP);
			em.persist(formbuilder);
		}
		
		if(knoledgeBaseList.size() < 1){
			App knoledgeBase = new App();
			knoledgeBase.setName("knoledgeBase"); knoledgeBase.getAppTypes().add(AppTypes.FACEBOOK_APP);
			em.persist(knoledgeBase);
		}
		
		if(socialnetworksList.size() < 1){
			App socialnetworks = new App();
			socialnetworks.setName("socialnetworks"); socialnetworks.getAppTypes().add(AppTypes.TAB_APP);
			em.persist(socialnetworks);
		}
		
		if(helpDeskList.size() < 1){
			App helpDesk = new App();
			helpDesk.setName("helpDesk"); helpDesk.getAppTypes().add(AppTypes.TAB_APP);
			em.persist(helpDesk);
		}
		
		if(reportList.size() < 1){
			App report = new App();
			report.setName("report"); report.getAppTypes().add(AppTypes.TAB_APP);
			em.persist(report);
		}
		em.getTransaction().commit();
		em.close();
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
