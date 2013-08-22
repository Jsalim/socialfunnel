package controllers;

import org.codehaus.jackson.node.ObjectNode;

import exceptions.NoUUIDException;
import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.Secured;
import interceptors.UserSessionInterceptor;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import services.BrandService;
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
			return ok();
		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}
}
