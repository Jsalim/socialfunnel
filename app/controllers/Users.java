package controllers;

import static play.data.Form.form;
//import interceptors.AjaxAuthCheckInterceptor;
//import interceptors.DefaultInterceptor;
//import interceptors.UserSessionInterceptor;

import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Agent;
import models.AgentNotification;
import models.Brand;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import constants.UserStatus;
import exceptions.InvalidParameterException;

import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Validation;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.UserService;
import services.BrandService;
import util.MyUtil;
import util.UserSession;

/**
 * Actions related to the user entity 
 * @author marcio
 * */
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class Users extends Controller {

	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance(); 
	/** singleton instance of {@link BrandService}  */
	private static final BrandService brandService = BrandService.getInstance();

	/**
	 * Renders register form
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result register() {
		DynamicForm params = form().bindFromRequest();
		String invitation = params.get("invitation");
		String name = params.get("name");
		String email = params.get("email");
		String username = params.get("username");
		String password = params.get("password");

		Form<Agent> agentForm = userService.validateAgentForm(name, email, username, password);

		Agent user = new Agent(name, email, username, password);

		if(agentForm.hasErrors()){
			InvalidParameterException paramException = new InvalidParameterException(Json.toJson(form().errorsAsJson()).toString());
			
			return badRequest(views.html.home.signup.render(agentForm, user, null, null));
		}else{
			user.setStatus(UserStatus.STATUS_ACTIVE);

			boolean saved = false;

			if(invitation != null && !invitation.equals("")){
				saved = userService.processInvitation(user, invitation);
			}else{
				saved = userService.create(user);
			}

			if(saved) {
				flash("registered", "true");
				return redirect(routes.Application.login());
			}else{
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Convite inválido.");
				badRequest(result);
				return badRequest(views.html.home.signup.render(null, null, null, null));
			}
		}
//		return ok();
	}
	
	@Transactional
	@With(AjaxAuthCheckInterceptor.class)
	public static Result ajaxEdit(){
		UserSession userSession = userService.getUserSession(session());
		DynamicForm params = form().bindFromRequest();
		
		String name = params.get("name");
		String email = params.get("email");
		String username = params.get("username");
		String phone = params.get("phone");
		String details = params.get("details");
		String password = params.get("password");
		
		if(password == null || password.trim().isEmpty()){
			password = "Misa99zz"; // dumb password for validation. 
		}

		Form<Agent> agentForm = userService.validateAgentForm(name, email, username, password);

		Agent user = userSession.getUser();
		
		user.setEmail(email);
		user.setDetails(details);
		user.setName(name);
		user.setUsername(username);
		user.setPhone(phone);
		if(password != null || !password.trim().isEmpty()){
			user.setPassword(password);
		}
		
		if(agentForm.hasErrors()){
			InvalidParameterException paramException = new InvalidParameterException(Json.toJson(form().errorsAsJson()).toString());
			
			return badRequest(views.html.home.signup.render(agentForm, user, null, null));
		}else{
//				user.setStatus(UserStatus.STATUS_ACTIVE);
			boolean saved = false;

			if(saved) {
				flash("registered", "true");
				return redirect(routes.Application.login());
			}else{
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Convite inválido.");
				badRequest(result);
				return badRequest(views.html.home.signup.render(null, null, null, null));
			}
		}
	}

	/**
	 * Renders register form
	 * @return
	 */
	@play.db.jpa.Transactional
	public static Result simpleRegister() {
		DynamicForm params = form().bindFromRequest();
		
		String invitation = params.get("invitation");

		String brandName = params.get("brandname");
		String brandAddress = params.get("brandaddress");
		String brandPhone = params.get("brandphone");

		String name = params.get("name");
		String email = params.get("email");
		String username = params.get("username");
		String password = params.get("password");

		Form<Brand> brandForm = brandService.validateBrandForm(brandName, brandAddress, brandPhone);

		Form<Agent> agentForm = userService.validateAgentForm(name, email, username, password);

		Agent user = new Agent(name, email, username, password);
		
		Brand brand = new Brand(brandName, brandAddress, brandPhone);

		if(brandForm.hasErrors()){
			Logger.error("ERROR");
			return badRequest(views.html.home.signup.render(agentForm, user, brandForm, brand));
		}
		
		if(agentForm.hasErrors() || brandForm.hasErrors()){
			return badRequest(views.html.home.signup.render(agentForm, user, brandForm, brand));
		}else{
			user.setStatus(UserStatus.STATUS_ACTIVE);

			boolean saved = false;

			if(invitation != null && !invitation.equals("")){
				saved = userService.processInvitation(user, invitation);
			}else{
				saved = userService.create(user);
			}

			if(saved) {
				Agent agent = userService.findByUsername(user.getUsername());
				try{
					brand = brandService.createBrand(brandName, null, brandAddress, agent, null, null, null);
				}catch (Exception e){
					e.printStackTrace();
					userService.delete(user);
					return badRequest(views.html.home.signup.render(null, null, null, null));
				}
				flash("registered", "true");
				return redirect(routes.Application.login());
			}else{
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Convite inválido.");
				badRequest(result);
				return badRequest(views.html.home.signup.render(null, null, null, null));
			}
		}
//		return ok();
	}

	/**
	 * Checks if username is available this method is usually called in a
	 * ajax fashion and returns  
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = true)
	public static Result checkUsername() {
		Map<String, String[]> params = request().queryString();
		ObjectNode json = Json.newObject();
		response().setContentType("application/json");
		if (params != null && params.get("username") != null) {
			String username = params.get("username")[0];

			if (!userService.exists(username)) {
				json.put("success", true);
				return ok(json);
			} else {
				json.put("success", false);
				json.put("error", "username já está em uso");
				return ok(json);
			}
		} else {
			json.put("success", false);
			json.put("error", "Nenhum nome fornecido");
			return badRequest(json);
		}
	}

	/**
	 * Checks for notifications for the user
	 * endpoint: /users/notifications - GET
	 */
	@Transactional (readOnly = true)
	//	@With(AjaxAuthCheckInterceptor.class)
	public static Result getNotifications(){
		UserSession userSession = userService.getUserSession(session());
		
		if(userSession.isValidSession() == false){
			String uuid = session("uuid");
			Cache.remove(uuid + "_user");
			session().clear();
			return unauthorized();
		}

		ObjectNode result = Json.newObject();
		ArrayList<String> errors = new ArrayList<String>();

		String[] limit = request().queryString().get("limit");
		String[] unseen = request().queryString().get("unseen");
		// if parameter limit & unseen are sent together
		if((limit != null && limit.length > 0 && !limit[0].equals("")) && (unseen != null && unseen.length > 0 && !unseen[0].equals(""))){
			errors.add("Parametros \"limit\" e \"unseen\" não podem ser usados em conjunto");
			// if parameter limit is sent
		}else if(limit != null && limit.length > 0 && !limit[0].equals("")){
			try {// try to see if parameter limit is a number
				int myLimit = Integer.parseInt(limit[0]);
				List<AgentNotification> notifications = userService.getAgentNotification(userSession, myLimit, 0);
				List<AgentNotification> unseenNotis = userService.getUnseenNotifications(userSession);
				result.put("success", true);
				result.put("lastNotification", Json.toJson(notifications));
				result.put("unseenNotification", Json.toJson(unseenNotis));
				return ok(result);
			} catch (Exception e) {
				errors.add("Parametro limit: " + limit[0] + " inválido");
			}
			// if parameter unseen is sent
		}else if(unseen != null && unseen.length > 0 && !unseen[0].equals("")){
			// if parameter unseen is true
			if(unseen[0].equals("true")){
				List<AgentNotification> unseenNotis = userService.getUnseenNotifications(userSession);
				result.put("success", true);
				result.put("unseenNotification", Json.toJson(unseenNotis));
				return ok(result);
			}else if(!unseen[0].equals("false")){ // if parameter unseen is not false
				errors.add("Parametro unseen: " + unseen[0] + " inválido");
			}
		}else{
			List<AgentNotification> notifications = userService.getAgentNotification(userSession, 10, 0);
			List<AgentNotification> unseenNotis = userService.getUnseenNotifications(userSession);
			result.put("success", true);
			result.put("lastNotification", Json.toJson(notifications));
			result.put("unseenNotification", Json.toJson(unseenNotis));
			return ok(result);
		}

		InvalidParameterException paramException = new InvalidParameterException(errors.toString()); 
		return badRequest(paramException.getJson());
	}

	/**
	 * Update users notification
	 * endpoint: /users/notifications - GET
	 */
	@Transactional
	//	@With(AjaxAuthCheckInterceptor.class)
	public static Result updateNotifications(){
		Long notificationId = 1l;
		UserSession userSession = userService.getUserSession(session());
		boolean success = userService.updateUserNotifications(userSession, notificationId);
		return ok(Json.toJson(success));
	}
}