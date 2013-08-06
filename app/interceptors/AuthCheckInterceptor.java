package interceptors;

import controllers.Application;
import controllers.routes;
import controllers.landing.Home;
import exceptions.NoUUIDException;
import play.Logger;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import services.UserService;
import util.UserSession;
/**
 * Only used for non-ajax authentication check This class contains a before method that checks if the user us authenticated.
 * It is similar to the {@link AjaxAuthCheckInterceptor} but instead of returning a 401 (Unauthorized STATUS) and a Json Message
 * it simply redirects the user to the the login page
 * */
public class AuthCheckInterceptor extends Action.Simple{
	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance(); 

	public Result before(Context ctx) throws NoUUIDException{

		Session session = ctx.session();
		UserSession userSession = userService.getUserSession(session);
		if(session.get("username") == null || userSession.getUser() == null){
			Logger.debug("AuthCheckInterceptor.before: Redirecting user to the application login page");
			return redirect(controllers.landing.routes.Home.signin().url());
		}else if(session.get("username") != null && userSession.getUser() != null){
			Logger.debug("BrandAssociationCheckInterceptor.before: Checking if the user: " + userSession.getUser().getUsername() + "[id: " + userSession.getUser().getId() +  "] " + "is associated with any brand");
			//				return Application.login();
		}

		return null;
	}

	@Override
	public Result call(Context ctx) throws Throwable {
		Logger.debug("AuthCheckInterceptor.before:\n------------------------------ begining action " + ctx.request().path() + " ------------------------------\n");
		
		Result result = this.before(ctx);
		if(result == null){
			Logger.debug("AuthCheckInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return delegate.call(ctx);
		}else{
			Logger.debug("AuthCheckInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return result;
		}
	}

}
