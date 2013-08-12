package interceptors;

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
 * This interceptor checks the status of the UserSession. Important in debugging session related errors
 * */
public class UserSessionInterceptor extends Action.Simple{
	
	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance(); 
	/**
	 * This method is run before executing the context action.
	 * @param ctx 
	 * @throws NoUUIDException 
	 * */
	public void before(Context ctx) throws NoUUIDException{
		Session session = ctx.session();
		UserSession userSession = userService.getUserSession(session);
		userService.validateDistributedSession(userSession);
		Logger.debug("UserSessionInterceptor.before: USER SESSION UUID - "+ userSession.getUUID() + "\n");
	}
	/**
	 * After executing the context action this method is called. 
	 * */
	public void after(Context ctx){
		
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		Result result;
		// execute this method before the action is run
		this.before(ctx);
		// action is run here
		result = delegate.call(ctx);
		// execute this method after the action is run
		this.after(ctx);

		return result;
	}
	
}
