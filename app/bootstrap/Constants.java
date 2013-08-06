package bootstrap;

import play.Play;

public class Constants {

	public static final String passwordHashkey = Play.application().configuration().getString("password.hashkey");

}
