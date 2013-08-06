package controllers;

import java.util.List;

import models.Brand;
import models.UserBrandRole;

import org.codehaus.jackson.node.ObjectNode;

import exceptions.NoUUIDException;
import interceptors.AjaxAuthCheckInterceptor;
import interceptors.AuthCheckInterceptor;
import interceptors.BrandDashboardInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.Secured;
import interceptors.UserSessionInterceptor;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.*;
//import services.BrandService;
import services.UserService;
import util.UserSession;

/**
 * Actions related to the dashboard as hole. Note, action related to specific entities such as 
 * Users and Brands should have there own controllers.
 * */
@Security.Authenticated(Secured.class)
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class Dashboard extends Controller {
	/** singleton instance of {@link UserService}  */
	private static final UserService userService = UserService.getInstance();
//	private static final BrandService brandService = BrandService.getInstance();

	/**
	 * Renders the a brand specific Dashboard. The {@link Brand#getNameAddress()} method returns a unique brand address name. A instance of
	 * a brand address name should be passed by parameter by the front-end.
	 * 
	 * @see /dashboard/index.scala.html
	 * */
	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
	@With({AuthCheckInterceptor.class})
	public static Result index() {
		try {
			UserSession userSession = userService.getUserSession(session());
			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.index.render(userSession, userSession.getBrand()));
				return ok(views.html.dashboard.index.render());
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}

		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result interactions() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.interactionspanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result campaigns() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.campaignspanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result helpdesk() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession]
//				return ok(views.html.dashboard.helpdeskpanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result leads() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.leadspanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result report() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.reportpanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
////				ObjectNode result = Json.newObject();
////				result.put("success", false);
////				result.put("error", "Parametro \"brand\" esperado.");
////				return badRequest(result);
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//
//	@Transactional(readOnly = true)
//	@With({AuthCheckInterceptor.class, BrandDashboardInterceptor.class})
//	public static Result settings() {
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			// the brand address name passed by the frontend
//			if(userSession.getBrand() != null){ // if a there is a brand on the UserSession
//				return ok(views.html.dashboard.settingspanel.render(userSession, userSession.getBrand()));
//			}else{ // else, if there is no parameter and no brand in cache
//				return redirect(controllers.landing.routes.Home.signin().url());
//			}
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//
//	/**
//	 * Renders the gate view.
//	 * @see /dashboard/gate.scala.html
//	 * 
//	 * */
//	@Transactional(readOnly = true)
//	@With(AuthCheckInterceptor.class)
//	public static Result gate(){
//		String[] newbrand = request().queryString().get("newbrand");
//		
//		Boolean addBrand = false;
//		
//		if(newbrand != null && newbrand.length > 0 && newbrand[0] != null && newbrand[0].equals("true")){
//			addBrand = true;
//		}
//		
//		try {
//			UserSession userSession = userService.getUserSession(session());
//			List<Brand> userBrands = brandService.getUserBrands(userSession.getUser());
//			
//			if(userBrands.size() == 1 && addBrand == false){
//				userSession.setBrand(userBrands.get(0));
//				userService.updateExistingUserSession(userSession);
//				return redirect(routes.Dashboard.index());
//			}
//			
//			return ok(views.html.dashboard.gate.render(userBrands, userSession, addBrand));
//		} catch (NoUUIDException e) {
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}
//
//	/**
//	 * This view can only be called by an authenticated user and the user must have a UserBrandRole associated with that brand
//	 * /dashboard/index?id=
//	 *  
//	 *  @see {@link UserBrandRole}
//	 *  @see /dashboard/configurations.scala.html
//	 * */
//	@With(AjaxAuthCheckInterceptor.class)
//	@Transactional
//	public static Result configurations(){
//		UserSession userSession;
//		try {
//			userSession = userService.getUserSession(session());
//			String[] id = request().queryString().get("id");
//
//			List<Brand> brands = brandService.getUserBrands(userSession.getUser());
//
//			if(id != null && id.length > 0 && !id[0].equals("")){
//				for(Brand brand : brands){
//					if(brand.getId() == Long.parseLong(id[0])){
//						return ok(views.html.dashboard.configurations.render(userSession, brand));
//					}
//				}
//				ObjectNode result = Json.newObject();
//				result.put("success", false);
//				result.put("error", "Usuário não tem relação com a marca: " + id[0]);
//				return unauthorized(result);
//			}else{
//				ObjectNode result = Json.newObject();
//				result.put("success", false);
//				result.put("error", "Parametro \"id\" não foi identificado.");
//				return badRequest(result);
//			}
//		}  catch (NoUUIDException e) { 
//			e.printStackTrace();
//			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
//			return internalServerError(e.getJson());
//		}
//	}

}
