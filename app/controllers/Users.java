package controllers;

import static play.data.Form.form;
//import interceptors.AjaxAuthCheckInterceptor;
//import interceptors.DefaultInterceptor;
//import interceptors.UserSessionInterceptor;

import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Agent;
import models.AgentNotification;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import constants.UserStatus;
import exceptions.InvalidParameterException;
import exceptions.NoUUIDException;

import play.Logger;
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

		Form<Agent> agentForm = new Form<Agent>(Agent.class);
		
		if(name == null){
			agentForm.errors().put("name", Arrays.asList(new ValidationError("name", "Nome inválido")));
		}else if(name.length() < 3 || name.length() >= 50){
			agentForm.errors().put("name", Arrays.asList(new ValidationError("name", "Nome deve conter 3 a 50 caracteres.")));
		}

		if(email == null || MyUtil.isEmailAddr(email) == false){
			agentForm.errors().put("email", Arrays.asList(new ValidationError("email", "Email inválido.")));
		}else if(userService.exists(email)){
			agentForm.errors().put("email", Arrays.asList(new ValidationError("email", "Email já está em uso")));
		}

		if(username == null){
			agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username inválido.")));
		}else if(username.length() < 3 || username.length() >= 30){
			agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username deve conter 3 a 30 caracteres.")));
		}else if(userService.exists(username)){
			agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username já está em uso.")));
		}

		if(password == null){
			agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha inválido.")));
		}else if(password.length() < 6){
			agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha deve ter no mínimo 6 caracteres.")));
		}else{
			boolean upperFound = false, hasNumber = false;
			for (char c : password.toCharArray()) {
				if (Character.isUpperCase(c)) {
					upperFound = true;
					break;
				}
			}
			for (char c : password.toCharArray()) {
				if (Character.isDigit(c)){
					hasNumber = true;
				}
			}

			if(upperFound && hasNumber){
				agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha deve conter ao menos 1 caracter maiúsculo e 1 dígito.")));
			}
		}

		Agent user = new Agent(name, email, username, password);

		if(agentForm.hasErrors()){
			InvalidParameterException paramException = new InvalidParameterException(Json.toJson(form().errorsAsJson()).toString());
			
			for(Map.Entry<String, List<ValidationError>> entry:  agentForm.errors().entrySet()){
				Logger.info(entry.getKey() + " " +entry.getValue());
			}
			
			return badRequest(views.html.home.signup.render(agentForm, user));
		}else{
			user.setStatus(UserStatus.STATUS_ACTIVE);

			boolean saved = false;

			if(invitation != null && !invitation.equals("")){
				saved = userService.processInvitation(user, invitation);
			}else{
				saved = userService.save(user);
			}

			if(saved) {
				flash("registered", "true");
				return redirect(routes.Application.login());
			}else{
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Convite inválido.");
				badRequest(result);
				return badRequest(views.html.home.signup.render(null, null));
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
		try {
			UserSession userSession = userService.getUserSession(session());

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

		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}

	/**
	 * Update users notification
	 * endpoint: /users/notifications - GET
	 */
	@Transactional
	//	@With(AjaxAuthCheckInterceptor.class)
	public static Result updateNotifications(){
		try {
			Long notificationId = 1l;
			UserSession userSession = userService.getUserSession(session());
			boolean success = userService.updateUserNotifications(userSession, notificationId);
			return ok(Json.toJson(success));
		} 
		catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}
}