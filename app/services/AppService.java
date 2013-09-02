package services;

import java.util.HashSet;
import java.util.Set;

import models.App;
import play.db.jpa.JPA;
import constants.AppNames;
import constants.AppTypes;
import exceptions.NoSutchAppException;

public class AppService {
	
	private static AppService instance = null;
	
	public static AppService getInstance() {
		if(instance == null){
			instance = new AppService();
		}
		return instance;
	}
	
	private AppService() {
		// TODO Auto-generated constructor stub
	}

	public Set<App> getDefaultApps(){
		Set<App> defaultApps = new HashSet<App>();

		App formbuilder = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", AppNames.FORMBUILDER.toString()).getSingleResult();
		App knoledgeBase = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", AppNames.KNOWLEDGEBASE.toString()).getSingleResult();
		App socialnetworks = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", AppNames.SOCIALNETWORKS.toString()).getSingleResult();
		App helpDesk = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", AppNames.HELPDESK.toString()).getSingleResult();
		App report = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", AppNames.REPORTS.toString()).getSingleResult();

		defaultApps.add(formbuilder);
		defaultApps.add(knoledgeBase);
		defaultApps.add(socialnetworks);
		defaultApps.add(helpDesk);
		defaultApps.add(report);

		return defaultApps;
	}
	
	public App create(AppNames appName) throws NoSutchAppException{
		App app = new App();
		if(appName == AppNames.FORMBUILDER){
			app.setName(AppNames.FORMBUILDER); 
			app.getAppTypes().add(AppTypes.EMBEDDABLE_APP);
			app.getAppTypes().add(AppTypes.TAB_APP);
			return app;
		}
		
		if(appName == AppNames.KNOWLEDGEBASE){
			app.setName(AppNames.KNOWLEDGEBASE); 
			app.getAppTypes().add(AppTypes.FACEBOOK_APP);
			app.getAppTypes().add(AppTypes.TAB_APP);
			return app;
		}
		
		if(appName == AppNames.SOCIALNETWORKS){
			app.setName(AppNames.SOCIALNETWORKS); 
			app.getAppTypes().add(AppTypes.TAB_APP);
			return app;
		}
		
		if(appName == AppNames.HELPDESK){
			app.setName(AppNames.HELPDESK); 
			app.getAppTypes().add(AppTypes.TAB_APP);
			return app;
		}
		
		if(appName == AppNames.REPORTS){
			app.setName(AppNames.REPORTS); 
			app.getAppTypes().add(AppTypes.TAB_APP);
			return app;
		}
		
		throw new NoSutchAppException();
	}
}
