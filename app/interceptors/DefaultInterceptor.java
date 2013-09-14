package interceptors;

import java.util.Map;

import bootstrap.Constants;

import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.BrandService;
/**
 * This interceptor show be executed in the context of every request.
 * 
 *  @see
    <a href="https://github.com/playframework/playframework/blob/master/framework/src/play-java-jpa/src/main/java/play/db/jpa/JPA.java">
 		https://github.com/playframework/playframework/blob/master/framework/src/play-java-jpa/src/main/java/play/db/jpa/JPA.java
    </a>
 * */
public class DefaultInterceptor extends Action.Simple{

	private static final BrandService brandService = BrandService.getInstance();

	/**
	 * This method is run before executing the context action.
	 * @param ctx 
	 * @return 
	 * */
	public Result before(Context ctx){

		String host = ctx.request().host();
		final String[] hostParts = host.split("\\.");
		
		boolean isBrand = false;
		final String subDomain = hostParts.length > 0 ? hostParts[0].toLowerCase().trim() : null;

		Logger.error("HOST: " + host + ctx.request().path());

		if(hostParts.length <=2 || (hostParts.length == 3 && Constants.reservedSubdomains.contains(subDomain) )){
			if(subDomain.equals("admin")){
				return ok("admin");
			}
			return null;
		}
		
		if((subDomain != null && !subDomain.isEmpty()) ){
			try {
				if(ctx.args.get("currentEntityManager") == null){ // 
					isBrand = JPA.withTransaction(new F.Function0<Boolean>() {
						@Override
						public Boolean apply() throws Throwable {
							return brandService.checkBrandSubdomain(subDomain);
						}
					});
				}else{
					isBrand = brandService.checkBrandSubdomain(subDomain);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		if(isBrand && (ctx.request().path().trim().equals("/") || ctx.request().path().trim().equals(""))){
			return redirect(controllers.knowledgebase.routes.KnowledgeBase.home().url());
		}else{
			Logger.error("No brand found! " + host + ctx.request().path());
			return notFound();
		}
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
	
		// action is run here
		Map<String, String[]> param = ctx.request().queryString();
		if(param != null && param.get("tab") != null){
			ctx.flash().remove("tab");
			ctx.flash().put("tab", param.get("tab")[0]);
		}
		
		// execute this method before the action is run
		result = this.before(ctx);
		if(result == null){
			result = delegate.call(ctx);
		}
		// execute this method after the action is run
		this.after(ctx);

		Logger.debug("DefaultInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
		return result;
	}

}
