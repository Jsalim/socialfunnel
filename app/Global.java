import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import ch.qos.logback.classic.sift.AppenderFactory;

import jobs.JobScheduler;

import bootstrap.Constants;
import bootstrap.DS;

import constants.AppNames;
import constants.AppTypes;
import constants.Permissions;
import constants.RoleName;
import constants.States;
import exceptions.NoSutchAppException;

import models.App;
import models.UserRole;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.libs.F.Promise;
import play.libs.WS;
import services.AppService;

public class Global extends GlobalSettings {
	
	private static final AppService appService = AppService.getInstance();
	
	@Override
	public void onStart(Application arg0) {
		try {
			checkAndCreateDefaultApps();
		} catch (NoSutchAppException e) {
			e.printStackTrace();
			System.exit(1);
		}
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

	/**
	 *	GET		/dashboard/apps						controllers.Dashboard.apps()
	 * 	GET		/dashboard/interactions				controllers.Dashboard.interactions()
	 *	GET		/dashboard/helpdesk					controllers.Dashboard.helpdesk()
	 *	GET		/dashboard/reports					controllers.Dashboard.reports()
	 * @throws NoSutchAppException 
	 *
	 * */
	private void checkAndCreateDefaultApps() throws NoSutchAppException {
		EntityManager em = JPA.em("default");
		
		String qurey = "SELECT an.* FROM App an WHERE an.name like :name";

		em.getTransaction().begin();
		App app = null;
		
		if(em.createNativeQuery(qurey).setParameter("name", AppNames.FORMBUILDER.toString()).getResultList().size() < 1){
			app = appService.create(AppNames.FORMBUILDER);
			em.persist(app);
		}
		
		if(em.createNativeQuery(qurey).setParameter("name", AppNames.KNOWLEDGEBASE.toString()).getResultList().size() < 1){
			app = appService.create(AppNames.KNOWLEDGEBASE);
			em.persist(app);
		}
		
		if(em.createNativeQuery(qurey).setParameter("name", AppNames.SOCIALNETWORKS.toString()).getResultList().size() < 1){
			app = appService.create(AppNames.SOCIALNETWORKS);
			em.persist(app);
		}
		
		if(em.createNativeQuery(qurey).setParameter("name", AppNames.HELPDESK.toString()).getResultList().size() < 1){
			app = appService.create(AppNames.HELPDESK);
			em.persist(app);
		}
		
		if(em.createNativeQuery(qurey).setParameter("name", AppNames.REPORTS.toString()).getResultList().size() < 1){
			app = appService.create(AppNames.REPORTS);
			em.persist(app);
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
