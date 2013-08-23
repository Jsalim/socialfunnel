package services;

import models.Ticket;
import play.data.Form;

public class HelpDeskService {

	private static HelpDeskService instance = null;
	
	public static HelpDeskService getInstance() {
		if(instance == null){
			instance = new HelpDeskService();
		}
		return instance;
	}

	public Form<Ticket> validateTicketForm(String subject, String description,
			String contact, String assignedAgent, String reporterAgent,
			String tags, String type, String priority, String channel) {
		// TODO Auto-generated method stub
		return null;
	}
}
