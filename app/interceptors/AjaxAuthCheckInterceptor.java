package interceptors;

import org.codehaus.jackson.node.ObjectNode;

import exceptions.NoUUIDException;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import services.UserService;
import util.UserSession;

/**
 * Only used for ajax authentication check This class contains a before method that checks if the user us authenticated.
 * It is similar to the {@link AuthCheckInterceptor} but instead of redirecting the user, if he or she is not connected,
 * it returns a 401 (Unauthorized STATUS) and a Json Message. Thus, redirection must be handled on the frontend
 * */
public class AjaxAuthCheckInterceptor extends Action.Simple{
	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance(); 

	public Result before(Context ctx) throws NoUUIDException{
//		Cache-Control: no-cache, no-store, must-revalidate
//		Pragma: no-cache
//		Expires: 0
		ctx.response().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		ctx.response().setHeader("Pragma", "no-cache");
		ctx.response().setHeader("Expires", "0");

		Session session = ctx.session();
		UserSession userSession = userService.getUserSession(session);
		if(session.get("username") == null || userSession.getUser() == null){
			Logger.debug("AjaxAuthCheckInterceptor.before: Could not authenticate user. Sending json feedback.");
			ObjectNode result = Json.newObject();
			result.put("success", false);
			result.put("error", "Unauthorized!");
			return unauthorized(result);
		}else if(session.get("username") != null && userSession.getUser() != null){
			Logger.debug("BrandAssociationCheckInterceptor.before: Checking if the user: " + userSession.getUser().getUsername() + "[id: " + userSession.getUser().getId() +  "] " + "is associated with any brand");
			//				return Application.login();
		}

		return null;
	}

	@Override
	public Result call(Context ctx) throws Throwable {
		Logger.debug("AjaxAuthCheckInterceptor.before:\n------------------------------ begining action " + ctx.request().path() + " ------------------------------\n");
		Result result = this.before(ctx);
		if(result == null){ // if result is null continue processing the request
			Logger.debug("AjaxAuthCheckInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return delegate.call(ctx);
		}else{// if not, intercept the request and return the error message
			Logger.debug("AjaxAuthCheckInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return result;
		}
	}

}
