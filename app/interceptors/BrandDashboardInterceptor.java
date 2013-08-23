package interceptors;

import java.util.List;

import models.Brand;

import org.codehaus.jackson.node.ObjectNode;

import exceptions.NoUUIDException;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.BrandService;
import services.UserService;
import util.UserSession;

/**
 * This class is responsible for checking if the user has permissions to see the specified brand dashboard and for redirecting him otherwise 
 * */
public class BrandDashboardInterceptor extends Action.Simple{
	
	private static final UserService userService = UserService.getInstance();
	private static final BrandService brandService = BrandService.getInstance();
	
	public Result before(Context ctx) {
		
		Logger.info(ctx.request().host());
		
		Logger.debug("BrandDashboardInterceptor.before:\n------------------------------ begining action " + ctx.request().path() + " ------------------------------\n");
		try {
			UserSession userSession = userService.getUserSession(ctx.session());
			// the brand address name passed by the frontend
			String[] brandAddress = ctx.request().queryString().get("brand");
			if(brandAddress != null && brandAddress.length > 0 && !brandAddress[0].equals("")){
				// get the brand by name address
				Brand brand = brandService.findByNameAddress(brandAddress[0]);
				if(brand != null){ // if a brand matching the given parameter is found
					List<Brand> userBrands = brandService.getUserBrands(userSession.getUser());
					for(Brand myBrand: userBrands){ // Check if user has permissions to access this brand
						if(brand.getId() == myBrand.getId()){
							userSession.setBrand(brand); // set the brand to cache
//							userService.updateExistingUserSession(userSession); // update user session object in cache
							userService.updateUsersNotificationInCache(userSession);
							return null;
						}
					}
					// if user does not have a UserBrandRole matching the brand
					ObjectNode result = Json.newObject();
					result.put("success", false);
					result.put("error", "Access denied for this user.");
					return unauthorized(result);

				}else{ // if no brand is found for the given parameter
					ObjectNode result = Json.newObject();
					result.put("success", false);
					result.put("error", "No brand found for: " + brandAddress[0]);
					return badRequest(result);
				}
			}else if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
				userService.updateUsersNotificationInCache(userSession);
				return null; // OK - No error was thrown and the user is authorized to view this dashboard
				
			}else{ // else, if there is no parameter and no brand in cache
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", "Parametro \"brand\" esperado.");
				return badRequest(result);
			}

		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + ctx.request().uri());
			return internalServerError(e.getJson());
		}
	}

	@Override
	public Result call(Context ctx) throws Throwable {
		// TODO Auto-generated method stub
		Result result = this.before(ctx);
		if(result == null){
			Logger.debug("BrandDashboardInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return delegate.call(ctx);
		}else{
			Logger.debug("BrandDashboardInterceptor.after:\n------------------------------ ending action " + ctx.request().path() + " ------------------------------\n");
			return result;
		}
	}

}
