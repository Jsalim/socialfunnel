package controllers.landing;

import models.PasswordReset;

import org.codehaus.jackson.node.ObjectNode;

import play.*;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;
import static play.data.Form.form;

import services.UserService;
//import services.UserService;
import util.MyUtil;

public class Home extends Controller {
	
	private static final UserService userService= UserService.getInstance();

	public static Result index() {
		return ok(views.html.home.index.render());
	}

	public static Result signup() {
		String[] invitation = request().queryString().get("invitation");
		
		if(invitation != null && invitation[0] != null && !invitation[0].equals("")){
			flash("invitation", invitation[0]);
		}
		
		return ok(views.html.home.signup.render(null, null, null, null));
	}
//
	public static Result signin() {
		return ok(views.html.home.signin.render(null, false));
	}
//
//	public static Result blog() {
//		return ok(views.html.home.blog.render());
//	}
//
//	public static Result pricing() {
//		return ok(views.html.home.pricing.render());
//	}
//
//	public static Result comingsoon() {
//		return ok(views.html.home.comingsoon.render());
//	}
//
//	public static Result features() {
//		return ok(views.html.home.features.render());
//	}
//
//	public static Result portfolio() {
//		return ok(views.html.home.portfolio.render());
//	}
//
//	public static Result contact() {
//		return ok(views.html.home.contact.render());
//	}
//
//	public static Result aboutus() {
//		return ok(views.html.home.aboutus.render());
//	}
//
//	public static Result faq() {
//		return ok(views.html.home.faq.render());
//	}
//
	@Transactional(readOnly = true)
	public static Result reset() {
		String[] resetCode = request().queryString().get("hash");
		if(resetCode != null && resetCode.length > 0 && !resetCode[0].equals("")){
			if(userService.checkResetPassword((resetCode[0]))){
//				return ok(views.html.home.newpassword.render(resetCode[0]));
				return ok("pimba");
			}else{
				return badRequest("Código Inválido");
			}
		}
		return ok(views.html.home.reset.render());
	}
	
	@Transactional
	public static Result resetPassword(){
		DynamicForm dynamicForm = form().bindFromRequest();

		String passwordconfirm = dynamicForm.get("passwordconfirm");
		String password = dynamicForm.get("password");
		String hash = dynamicForm.get("hash");
		
		if(password.equals(passwordconfirm)){
			if(userService.resetPassword(hash, password)){
				return redirect(controllers.landing.routes.Home.signin().url());
			}else{
				return badRequest("Código Inválido");
			}
		}else{
			return badRequest("Senhas diferentes");
		}
		
	}
	
	@Transactional
	public static Result doReset(){
		DynamicForm dynamicForm = form().bindFromRequest();
		String email = dynamicForm.get("email");

		ObjectNode result = Json.newObject();

		if(email != null){
			if(MyUtil.isEmailAddr(email)){
				// persist password reset
				if(userService.createResetPassword(email)){
					result.put("success", true);
					return ok(result);
				}else{
					result.put("success", false);
					result.put("error", "Email not register");
					return ok(result);
				}
			}
		}
		
		result.put("success", false);
		result.put("error", "invalid parameter " + "email: " + email);
		return badRequest(result);
	}
//
//	public static Result blogpost() {
//		return ok(views.html.home.blogpost.render());
//	}
//	
//	public static Result unbounce() {
//		return ok(views.html.home.unbounce.render());
//	}
}
