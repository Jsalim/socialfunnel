package controllers;

import static play.data.Form.form;
import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Agent;

import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
//import services.BrandService;
import services.UserService;
import util.MyUtil;
import util.UserSession;

//import com.dataanalytics.DataAnalyticsClient;
//import com.dataanalytics.DataAnalyticsFactory;
//import com.dataanalytics.exceptions.DataAnalyticsException;
//import com.dataanalytics.helper.Parameter;
import com.restfb.types.Post;

import exceptions.NoUUIDException;

/**
 * This controller holds simple actions related to the application but not
 * related to a specific section.
 * */
@With({ DefaultInterceptor.class, UserSessionInterceptor.class })
public class Application extends Controller {

	/** singleton instance of {@link UserService} */
	private static final UserService userService = UserService.getInstance();
//	private static final BrandService brandService = BrandService.getInstance();

	public static Result index() {
		return redirect("/home");
	}

	/**
	 * Login class handler for system login form
	 */
	public static class Login {
		public String username;
		public String password;

		/**
		 * This method is called internally when the request is binded by the
		 * form helper ex. Form.form(Login.class).bindFromRequest(); This method
		 * should return null if no error is found. If an error occurs this
		 * method should return a string description of the error.
		 * */
		public String validate() {
			
			String message = Messages.get("user.login.failed");
			if (username != null && password != null) {
				Agent user = userService.findByUsername(username);

				if (user != null) {
					if (user.validatePassword(password)) {
						try {

							UserSession userSession = userService.getUserSession(session());
							userSession.setUser(user);
							userService.updateLastLogin(userSession);
							userService.incrementLoginCounter(userSession);
							userService.updateExistingUserSession(userSession);

						} catch (NoUUIDException e) {
							e.printStackTrace();
						}
						return null;
					}
				}
			}

			return message;
		}
	}

	/**
	 * Renders system login form
	 * 
	 * @return login view form
	 */
	public static Result login() {
		String uuid = session("uuid");
		UserSession userSession = (UserSession) Cache.get(uuid + "_user");
		Agent user = userSession.getUser();
		if (user == null) {
			return ok(views.html.home.signin.render(null, false));
		} else {
			return redirect(routes.Dashboard.index());
		}
	}

	/**
	 * Logs out of the system and renders login form
	 * 
	 * @return login view form
	 */
	public static Result logout() {
		String uuid = session("uuid");
		Cache.remove(uuid + "_user");
		session().clear();
//		return redirect(routes.Dashboard.index());
		return ok("logout");
	}

	/**
	 * Validates login form using Login.class template
	 * 
	 * @return dashboard view if login is successful, or back to login form if
	 *         not
	 */
	@Transactional
	public static Result authenticate() {
		// Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

		DynamicForm params = form().bindFromRequest();

		Form<Login> loginForm = Form.form(Login.class).bind(params.data());
		
		if (loginForm.hasErrors()) {
			flash("loginError", "error");
			return badRequest(views.html.home.signin.render(loginForm.field("username").value(), false));
		} else {
			session("username", loginForm.get().username);
			return redirect(routes.Dashboard.index());
//			return ok("OK");
		}
	}

//	public static Result killwindow() {
//		return ok(views.html.killwindow.render());
//	}

	/**
	 * Normalizes a string and replaces special chars, white spaces and
	 * ponctuation with url valid chars
	 * */
	public static Result toSlug() {
		DynamicForm dynamicForm = form().bindFromRequest();
		String input = dynamicForm.get("input");
		String output = "";

		if (input != null) {
			output = MyUtil.toSlug(input);
			ObjectNode result = Json.newObject();
			result.put("success", true);
			result.put("output", output);
			return ok(result);
		} else {
			ObjectNode result = Json.newObject();
			result.put("success", false);
			return ok(result);
		}
	}

//	public static Result doTest() {
//		String address = Play.application().configuration().getString("das.address");
//		try {
//			DataAnalyticsClient dac = DataAnalyticsFactory.getInstance(address, "");
//			@SuppressWarnings("unchecked")
//			List<com.restfb.types.Post> posts = (List<Post>) dac.requestList("search", com.restfb.types.Post.class, "GET", Parameter.with("q", "dilma"));
//
//		} catch (MalformedURLException | DataAnalyticsException e) {
//			e.printStackTrace();
//		}
//
//		return ok();
//	}
}
