package interceptors;

import java.util.Map;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import services.UserService;
/**
 * This interceptor show be executed in the context of every request.  
 * */
public class DefaultInterceptor extends Action.Simple{
	
	/**
	 * This method is run before executing the context action.
	 * @param ctx 
	 * */
	public void before(Context ctx){
	}
	/**
	 * After executing the context action this method is called. 
	 * */
	public void after(Context ctx){
	}
	
	@Override
	public Result call(Context ctx) throws Throwable {
		Logger.debug("DefaultInterceptor.before:\n------------------------------ begining action " + ctx.request().path() + " ------------------------------\n");
		Result result;
		// execute this method before the action is run
		this.before(ctx);
		// action is run here
		Map<String, String[]> param = ctx.request().queryString();
		if(param != null && param.get("tab") != null){
			ctx.flash().remove("tab");
			ctx.flash().put("tab", param.get("tab")[0]);
		}
		
		result = delegate.call(ctx);
		// execute this method after the action is run
		this.after(ctx);

		Logger.debug("DefaultInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
		return result;
	}
	
}
