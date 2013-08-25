package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Agent;
import models.Brand;
import models.Contact;
import models.Ticket;

import org.codehaus.jackson.node.ObjectNode;

import exceptions.NoUUIDException;
import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.Secured;
import interceptors.UserSessionInterceptor;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import services.BrandService;
import services.HelpDeskService;
import services.UserService;
import util.UserSession;

/**
 * Actions related to the dashboard as hole. Note, action related to specific entities such as 
 * Users and Brands should have there own controllers.
 * */
@Security.Authenticated(Secured.class)
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class HelpDesk extends Controller{

	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance();
	private static final BrandService brandService = BrandService.getInstance();
	private static final HelpDeskService helpDeskService = HelpDeskService.getInstance(); 

	@With(AjaxAuthCheckInterceptor.class)
	public static Result tickets() {
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.helpdesk.tickets.render(userSession, userSession.getBrand()));
			}else{ // else, if there is no parameter and no brand in cache
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Parametro \"brand\" esperado.");
				return badRequest(result);
			}

		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}

	@With(AjaxAuthCheckInterceptor.class)
	public static Result contacts() {
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.helpdesk.contacts.render(userSession, userSession.getBrand()));
			}else{ // else, if there is no parameter and no brand in cache
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Parametro \"brand\" esperado.");
				return badRequest(result);
			}

		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}

	//	public static Result ticket() {
	//		try {
	//			UserSession userSession = userService.getUserSession(session());
	//			// the brand address name passed by the frontend
	//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
	//				return ok(views.html.dashboard.helpdesk.ticket.render(userSession, userSession.getBrand()));
	//			}else{ // else, if there is no parameter and no brand in cache
	//				ObjectNode result = Json.newObject();
	//				result.put("success", false);
	//				result.put("error", "Parametro \"brand\" esperado.");
	//				return badRequest(result);
	//			}
	//
	//		} catch (NoUUIDException e) {
	//			e.printStackTrace();
	//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
	//			return internalServerError(e.getJson());
	//		}
	//	}
	@With(AjaxAuthCheckInterceptor.class)
	public static Result createTicket(){
		try {
			UserSession userSession = userService.getUserSession(session());


			DynamicForm params = form().bindFromRequest();
			String subject = params.get("subject");
			String description = params.get("description");
			String contact = params.get("contact");
			String assignedAgent = params.get("assignedAgent");
			String reporterAgent = params.get("reporterAgent");
			String tags = params.get("tags");
			String type = params.get("type");
			String priority = params.get("priority");
			String channel = params.get("channel");

			Form<Ticket> ticketForm = helpDeskService.validateTicketForm(subject, description, contact, assignedAgent, reporterAgent, tags, type, priority, channel);

			Logger.info(subject + " " +description + " " +contact + " " +assignedAgent + " " +reporterAgent + " " +tags + " " +type + " " +priority + " " +channel);

			if(ticketForm !=null && ticketForm.hasErrors()){
				ObjectNode resp = Json.newObject();
				resp.put("success", false);
				return badRequest(resp);
			}else if(ticketForm !=null){
				ObjectNode resp = Json.newObject();
				resp.put("success", true);
				return ok(resp);
			}else{
				ObjectNode resp = Json.newObject();
				resp.put("success", false);
				return internalServerError(resp);
			}
		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}
	
	@Transactional
	@With(AjaxAuthCheckInterceptor.class)
	public static Result reports(){
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.helpdesk.reports.render(userSession, userSession.getBrand()));
			}else{ // else, if there is no parameter and no brand in cache
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Parametro \"brand\" esperado.");
				return badRequest(result);
			}
		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}

	@Transactional
	@With(AjaxAuthCheckInterceptor.class)
	public static Result testTicket(){
		
		try {
			UserSession userSession = userService.getUserSession(session());
			Brand brand = brandService.findById(userSession.getBrand().getId());
			Ticket ticket = new Ticket();
			Contact contact = null;
			
			List<Contact> contacts = JPA.em().createQuery("SELECT c FROM Contact c", Contact.class).setMaxResults(1).getResultList();
			if(contacts.size() == 0){
				contact = new Contact();
				contact.setName("João Aleixo");
				contact.setBrand(brand);
				contact.setEmails("misawsneto@gmail.com");
				contact.setGravatarLink(contact.getEmails());
				JPA.em().persist(contact);
			}else{
				contact = contacts.get(0);
			}
			
			ticket.setSubject("Isso é um tema");
			ticket.setDescription("Isso é a primeira descrição");
			ticket.setContact(contact);
			ticket.setBrand(brand);
			
			JPA.em().persist(ticket);
			
			ticket.setTicketNumber();
			
			Logger.info("" + ticket.getTicketNumber());
			
			return ok();
			
		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}
}
