package controllers;

import static play.data.Form.form;
import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Brand;
import models.FacebookAccountInfo;
import models.Invitation;
import models.StreamFilter;
import models.TwitterAccountInfo;
import models.Agent;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.JsonObject;

import constants.FilterTypes;

import exceptions.InvalidParameterException;
import play.Logger;
import play.data.validation.ValidationError;
import play.cache.Cache;
import play.data.DynamicForm;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.With;
import services.BrandService;
import services.UserService;
import util.MyUtil;
import util.UserSession;

/**
 * Brand related actions
 * */
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class Brands extends Controller{

	/** singleton instance of {@link UserService}  */
	private final static UserService userService = UserService.getInstance();
	/** singleton instance of {@link BrandService}  */
	private final static BrandService brandService = BrandService.getInstance();

	/**
	 * Renders the brand creation wizard multi-step form
	 * 
	 * @see /dashboard/newbrandwizard.scala.html
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	public static Result newBrandWizard(){
		UserSession userSession = userService.getUserSession(session());
		return ok(views.html.dashboard.newbrandwizard.render(userSession));
	}

	/**
	 * Every time a user tries to create a new brand by clicking on the new brand button on the workspace (gate) view, clear
	 * the cache and remove the facebook and twitter accounts from previews instalation.
	 * @see /dashboard/newbrandwizard.scala.html
	 * @see TwitterController
	 * @see FacebookController
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	public static Result clearInstallationAccounts(){
		UserSession userSession = userService.getUserSession(session());
		Cache.remove(userSession.getUUID() + "_twitter_accounts");
		Cache.remove(userSession.getUUID() + "_facebook_accounts");
		ObjectNode result = Json.newObject();
		result.put("success", true);
		return ok(result);
	}

	/**
	 * Creates a new brand from the parameters send through the form.
	 * @see /dashboard/newbrandwizard.scala.html
	 * */
	@Transactional
	@With(AjaxAuthCheckInterceptor.class)
	public static Result createBrand(){
		UserSession userSession = userService.getUserSession(session());
		// initialize variables
		Set<FacebookAccountInfo> facebookAccounts = new HashSet<FacebookAccountInfo>(); // empty list of facebookAccounts
		Set<TwitterAccountInfo> twitterAccounts = new HashSet<TwitterAccountInfo>();// empty list of twitterAccounts
		Set<Invitation> invitations = new HashSet<Invitation>();

		// get facebook and twitter accounts from cache
		Set<FacebookAccountInfo> cacheFacebookAccounts = (Set<FacebookAccountInfo>) Cache.get(userSession.getUUID() + "_facebook_accounts");
		Set<TwitterAccountInfo> cacheTwitterAccounts = (Set<TwitterAccountInfo>) Cache.get(userSession.getUUID() + "_twitter_accounts");

		// if the list is empty, create an empty list.
		cacheFacebookAccounts = cacheFacebookAccounts != null ? cacheFacebookAccounts : new HashSet<FacebookAccountInfo>();
		cacheTwitterAccounts = cacheTwitterAccounts != null ? cacheTwitterAccounts : new HashSet<TwitterAccountInfo>(); 

		// get form data as map 
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		// get parameters
		String[] brandName = params.get("brandName");
		String[] brandDescription = params.get("brandDescription");
		String[] nameAddress = params.get("brandNameAddress");
		String[] names = params.get("name[]");
		String[] emails = params.get("email[]");
		String[] facebookAccountIds = params.get("facebook[]");
		String[] twitterAccountIds = params.get("twitter[]");

		String myBrandName = null;
		String myNameAddress = null;
		String myBrandDescription = "";
		// set brand name
		if(brandName != null && brandName.length > 0){
			myBrandName = brandName[0].length() >= 2 ? brandName[0] : null;
		}else{
			List<ValidationError> list = new ArrayList<ValidationError>();
			list.add(new ValidationError("noname", "Invalid address"));
			form().errors().put("noname",list);
		}

		if(brandDescription != null && brandDescription.length > 0){
			myBrandDescription = brandDescription[0].length() >= 2 ? brandDescription[0] : null;
		}
		//			else{
		//				List<ValidationError> list = new ArrayList<ValidationError>();
		//				list.add(new ValidationError("nodesc", "Invalid description"));
		//				form().errors().put("noname",list);
		//			}

		if(nameAddress != null && nameAddress.length > 0){
			myNameAddress = nameAddress[0].length() >= 2 ? nameAddress[0] : null;
		}else{
			List<ValidationError> list = new ArrayList<ValidationError>();
			list.add(new ValidationError("noaddress", "Invalid name"));
			form().errors().put("noaddress",list);
		}

		// get facebook accounts that are in cache and have been marked in the new wizard 
		if(facebookAccountIds != null && facebookAccountIds.length > 0){
			for(String account : facebookAccountIds){
				long accountId = Long.parseLong(account.trim());
				for(FacebookAccountInfo accountInfo: cacheFacebookAccounts){
					if(accountInfo.getId() == accountId){
						facebookAccounts.add(accountInfo);
					}
				}
			}
		}
		// get twitter accounts that are in cache and have been marked in the new wizard
		if(twitterAccountIds != null && twitterAccountIds.length > 0){
			for(String account : twitterAccountIds){
				long accountId = Long.parseLong(account.trim());
				for(TwitterAccountInfo accountInfo: cacheTwitterAccounts){
					if(accountInfo.getId() == accountId){
						twitterAccounts.add(accountInfo);
					}
				}
			}
		}

		if(		(twitterAccountIds == null && facebookAccountIds == null) ||
				(twitterAccountIds != null && twitterAccountIds.length == 0) &&
				(facebookAccountIds != null && facebookAccountIds.length == 0) ){

			List<ValidationError> list = new ArrayList<ValidationError>();
			list.add(new ValidationError("noaccounts", "No accounts associated"));
			form().errors().put("noaccounts",list);
		}

		// get names and emails for team 
		if(names != null && names.length > 0){
			for(int i = 0; i < names.length ; i++){
				String name = names[i];
				String email = emails[i];
				if(!MyUtil.isEmailAddr(email)){
					List<ValidationError> list = new ArrayList<ValidationError>();
					list.add(new ValidationError("email", "Invalid email"));
					form().errors().put("email",list);
				}
				invitations.add(new Invitation(null, null, name, email));
			}
		}

		ObjectNode result = Json.newObject();

		if(form().hasErrors()){
			result.put("success", false);
			result.put("error", "Invalid parameters");
			return badRequest(result);
		}

		Agent owner = userService.findById(userSession.getUser().getId());

		Brand brand = brandService.createBrand(myBrandName, myBrandDescription,  myNameAddress, owner, facebookAccounts, twitterAccounts, invitations);

		if(brand != null){
			return ok(views.html.dashboard.brandbox.render(brand));
		}else{
			result.put("success", false);
			result.put("error", "Error while creating a new brand");
			return internalServerError(result);
		}
	}

	/**
	 * Check if brand name has already been taken
	 * */
	@Transactional(readOnly = true)
	public static Result brandNameAddressCheck(){
		DynamicForm dynamicForm = form().bindFromRequest();
		String input = dynamicForm.get("input");

		ObjectNode result = Json.newObject();

		if(input != null || input.trim().equals("")){
			if(input.length() >= 2 && brandService.isNameAddressAvailable(input)){
				if(MyUtil.isHost(input)){
					result.put("success", true);
					result.put("available", true);
					return ok(result);
				}else{
					result.put("success", false);
					result.put("error", "Invalid hostname.");
					return ok(result);
				}
			}else{
				result.put("success", false);
				result.put("available", false);
				result.put("error", "Usuário inválido.");
				return ok(result);
			}
		}
		result.put("success", false);
		result.put("error", "Parameter \"input\" not identified.");
		return badRequest(result);
	}

	@With(AjaxAuthCheckInterceptor.class)
	@Transactional
	public static Result readBrand(String id){
		return null;
	}

	/**
	 * This action calls the deleteBrand service. The brand is not actually deleted but deactivated
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	@Transactional
	public static Result removeBrand(Long id) {
		ObjectNode result = Json.newObject();
		if(id != null){

			Long brandId = id;
			if(brandService.deleteBrand(brandId)){
				result.put("success", true);
				return ok(result);
			}else{
				result.put("success", false);
				result.put("error", "Error while removing brand");
				return ok(result);
			}
		}else{
			result.put("success", false);
			result.put("error", "Missing parameter id");
			return badRequest(result);
		}
	}

	/**
	 * Endpoint for image/file upload. 
	 * we use the file-uploader plugin
	 * @see <a href="https://github.com/valums/file-uploader/blob/master/docs/options-fineuploaderbasic.md">fine-uploader</a> 
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	@Transactional
	public static Result imageUpload() {
		UserSession userSession;
		userSession = userService.getUserSession(session());
		ObjectNode result = Json.newObject();

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart picture = body.getFile("file");


		String[] actionSrc = request().queryString().get("actionSrc");
		String[] uuid = request().queryString().get("uuid");

		if(uuid.length < 1 || uuid[0] == null || uuid[0].equals("")){
			result.put("success", false);
			result.put("error", "Missing parameter uuid");
			return badRequest(result);
		}

		if(actionSrc.length < 1 || actionSrc[0] == null || actionSrc[0].equals("")){
			result.put("success", false);
			result.put("error", "Missing parameter actionSrc");
			return badRequest(result);
		}


		if (picture != null) {

			String fileName = picture.getFilename();
			String contentType = picture.getContentType(); 
			File file = picture.getFile();

			//				Logger.debug(fileName);

			String fileNamePrefix = uuid[0];
			fileName = fileNamePrefix + "_" + fileName;
			fileName = fileName.replaceAll(" ", "_");

			if(actionSrc[0].equals("brandsimages")){
				file.renameTo(new File("public/brandsimages", fileName));
			}

			result.put("success", true);
			result.put("fileUUID", uuid[0]);
			result.put("fileName", fileName);
			return ok(result);
		} else {
			flash("error", "Missing file");
			return redirect(routes.Application.index());    
		}
	}

//	/**
//	 * Endpoint for image/file upload. 
//	 * we use the file-uploader plugin
//	 * @see <a href="https://github.com/valums/file-uploader/blob/master/docs/options-fineuploaderbasic.md">fine-uploader</a> 
//	 * */
//	//	@With(AjaxAuthCheckInterceptor.class)
//	//	public static Result file(){
//	//		
//	//	}
//
	@With(AjaxAuthCheckInterceptor.class)
	@Transactional
	public static Result addFilter(){

//		DynamicForm data = form().bindFromRequest();
		JsonNode data = request().body().asJson();
		UserSession userSession;
		
		try { // NoUUIDException 
			userSession = userService.getUserSession(session());
			Brand brand = userSession.getBrand();


			// non-optional parameters
			String paramBeginning = data.get("beginning") != null ? data.get("beginning").asText() : null;
			String paramEnding = data.get("ending") != null ? data.get("ending").asText() : null;
			String paramFilterType = data.get("filterType") != null ? data.get("filterType").asText() : null;
			
			// optional parametersl 
			String paramSearchExpression = data.get("searchExpression") != null ? data.get("searchExpression").asText() : null;
			String paramFilterName = data.get("filterName") != null ? data.get("filterName").asText() : null;
			String paramJsonProfiles = data.get("jsonProfiles") != null ? data.get("jsonProfiles").toString() : null;
			String paramJsonMessages = data.get("jsonMessageTypes") != null ? data.get("jsonMessageTypes").toString() : null;
			String paramJsonChannels = data.get("jsonChannels") != null ? data.get("jsonChannels").toString() : null;
			String paramJsonTerms = data.get("jsonTerms") != null ? data.get("jsonTerms").toString() : null;
			
		 	StreamFilter streamFilter = brandService.createFilter(brand, paramBeginning, paramEnding, paramFilterType, 
					paramSearchExpression, paramFilterName, paramJsonProfiles, 
					paramJsonMessages, paramJsonChannels, paramJsonTerms);
			
			ObjectNode result = Json.newObject();
			result.put("success", true);
			result.put("filterId", streamFilter.getId());
			
			return ok(result);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			return badRequest(e.getJson());
		}
	}

	@Transactional
	@With(AjaxAuthCheckInterceptor.class)
	public static Result getFilter(){
		UserSession userSession;
		userSession = userService.getUserSession(session());
		Brand brand = userSession.getBrand();

		StreamFilter[] filters = (StreamFilter[]) brand.getStreamFilters().toArray();

		ObjectNode result = Json.newObject();
		result.put("success", true);
		result.put("filters", Json.toJson(filters));
		
		return ok(result);
	}
}
