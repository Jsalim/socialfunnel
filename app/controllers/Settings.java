package controllers;

import java.util.List;

import models.Agent;

import org.codehaus.jackson.node.ObjectNode;

import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.Secured;
import interceptors.UserSessionInterceptor;
import exceptions.NoUUIDException;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import services.BrandService;
import services.UserService;
import util.UserSession;


@Security.Authenticated(Secured.class)
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class Settings extends Controller{

	private final static UserService userService = UserService.getInstance();
	private final static BrandService brandService = BrandService.getInstance();

	@With(AjaxAuthCheckInterceptor.class)
	public static Result generalSettings(){
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.settings.general.render(userSession, userSession.getBrand()));
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
	public static Result editProfile(){
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.settings.editprofile.render(userSession, userSession.getBrand()));
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
	public static Result groups(){
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				List<Agent> agents = brandService.getAllAgents(userSession.getBrand());
				return ok(views.html.dashboard.settings.groups.render(userSession, userSession.getBrand(), agents));
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
	public static Result apps(){
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				return ok(views.html.dashboard.settings.applications.render(userSession, userSession.getBrand()));
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
}
