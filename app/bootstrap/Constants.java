package bootstrap;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import play.Play;

public class Constants {

	public static final String passwordHashkey = Play.application().configuration().getString("password.hashkey");
	public static final ArrayList<String> reservedSubdomains = new ArrayList<String>();
	
	static {
		reservedSubdomains.add("www");
		reservedSubdomains.add("api");
		reservedSubdomains.add("support");
		reservedSubdomains.add("ajuda");
		reservedSubdomains.add("admin");
	}
}
