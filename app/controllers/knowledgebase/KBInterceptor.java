package controllers.knowledgebase;

import models.Brand;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;
import services.BrandService;
import services.UserService;
import util.UserSession;
import bootstrap.Constants;

public class KBInterceptor  extends Action.Simple{

	private static final BrandService brandService = BrandService.getInstance();
	private static final UserService userService = UserService.getInstance();
	/**
	 * This method is run before executing the context action.
	 * @param ctx 
	 * @return 
	 * */
	public Result before(final Context ctx){
		
		UserSession userSession = userService.getUserSession(ctx.session());
		Brand kbBrand = userSession.getKbBrand();

		String host = ctx.request().host();
		final String[] hostParts = host.split("\\.");
		
		boolean isBrand = false;
		final String subDomain = hostParts.length > 0 ? hostParts[0].toLowerCase().trim() : null;

		Logger.warn("KB HOST: " + host + ctx.request().path());

		if(hostParts.length <=2 || (hostParts.length == 3 && Constants.reservedSubdomains.contains(subDomain) )){
			if(subDomain.equals("admin")){
				return ok("admin");
			}
			return null;
		}
		
		if((subDomain != null && !subDomain.isEmpty()) && ( kbBrand == null || (kbBrand != null && !kbBrand.getSubdomain().equals(subDomain))) ){
			try {
				if(ctx.args.get("currentEntityManager") == null){ // 
					isBrand = JPA.withTransaction(new F.Function0<Boolean>() {
						@Override
						public Boolean apply() throws Throwable {
							return brandService.checkBrandSubdomain(subDomain, ctx.session());
						}
					});
				}else{
					isBrand = brandService.checkBrandSubdomain(subDomain, ctx.session());
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		if(isBrand){
			Logger.debug("Knowledge Base from brand: " + subDomain);
			return null;
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
		Logger.debug("KBInterceptor.before:\n------------------------------ begining action " + ctx.request().path() + " ------------------------------\n");
		Result result;
	
		// execute this method before the action is run
		result = this.before(ctx);
		if(result == null){
			result = delegate.call(ctx);
		}
		// execute this method after the action is run
		this.after(ctx);

		Logger.debug("KBInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
		return result;
	}

}
