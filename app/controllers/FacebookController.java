package controllers;

import static play.data.Form.form;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import models.FacebookAccountInfo;

import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookGraphException;
import com.restfb.types.Page;

import constants.FacebookAccountTypes;

import exceptions.NoUUIDException;

import play.Logger;
import play.cache.Cache;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.FacebookService;
import services.UserService;
import util.UserSession;
/**
 * This class contains actions for interaction with facebook. Different from twitter, the authentication is handled by the FB.login JS API.
 * This eliminates the need for the oauth dance. The main method is {@link FacebookController#pageAccounts()}. Similar to twitter this method
 * returns a json object with newly added FacebookAccountInfo objects.  
 * */
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class FacebookController extends Controller{

	private static final FacebookService facebookService = FacebookService.getInstance();

	private static final UserService userService = UserService.getInstance();
	
	/**
	 * Different from the TwitterController, the FacebookController only has the accounts action.
	 * Since facebook uses the JS SDK for authenticating the user we do not need a handler nor a callback
	 * @throws NoSuchAlgorithmException 
	 * 
	 * @see {@link TwitterController}
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	public static Result pageAccounts() throws NoSuchAlgorithmException{
		try {
			DynamicForm params = form().bindFromRequest();
			String accessToken = params.get("accessToken");
			String userID = params.get("userID");
			String expiresIn = params.get("expiresIn");
			String signedRequest = params.get("signedRequest");
			String status = params.get("status");
			
			UserSession userSession;
			userSession = userService.getUserSession(session());

			HashSet<FacebookAccountInfo> facebookAccounts = (HashSet<FacebookAccountInfo>) Cache.get(userSession.getUUID() + "_facebook_accounts");
			if(facebookAccounts == null){
				facebookAccounts = new HashSet<FacebookAccountInfo>();
			}
			
			ObjectNode result = Json.newObject();

			JsonNode validUser = null;
			if(signedRequest !=null){
				validUser = facebookService.getUserFromSignedRequest(signedRequest);
			}
			
			if(validUser != null){
				String newAccessToken = facebookService.extendAccessToken(accessToken);
				JsonNode user = facebookService.getUserProfile(newAccessToken);
				JsonNode pages = facebookService.getUserPages(newAccessToken);
				//			JsonNode groups = facebookService.getGroups(accessToken);
				facebookAccounts.add(new FacebookAccountInfo(user.get("userID").asLong(), user.get("userName").asText(), newAccessToken, FacebookAccountTypes.FACEBOOK_USER));
				Iterator<JsonNode> iterator = pages.get("data").iterator();
				while (iterator.hasNext()) {
					JsonNode page = iterator.next();
					facebookAccounts.add(new FacebookAccountInfo(page.get("id").asLong(), page.get("name").asText(), page.get("perms").asText(), page.get("accessToken").asText(), FacebookAccountTypes.FACEBOOK_PAGE));
				}
			}

//			ArrayNode usersArray = result.putArray("users");
			ArrayNode pagesArray = result.putArray("pages");

			for(FacebookAccountInfo accountInfo: facebookAccounts){
				FacebookClient client = new DefaultFacebookClient(accountInfo.getOauthToken());
//				if(accountInfo.getAccountType() == FacebookAccountTypes.FACEBOOK_USER){
//					ObjectNode userObject = Json.newObject();
//					userObject.put("userID", accountInfo.getId());
//					userObject.put("userName", accountInfo.getName());
//					int friendsCount = facebookService.getFriendsCount(accountInfo.getOauthToken());
//					userObject.put("friendsCount", friendsCount);
//					userObject.put("profileImage", "http://graph.facebook.com/" + accountInfo.getId() + "/picture?width=50&height=50");
//
//					usersArray.add(userObject);
//				}
				if(accountInfo.getAccountType() == FacebookAccountTypes.FACEBOOK_PAGE){
					ObjectNode pageObject = Json.newObject();
					pageObject.put("id", accountInfo.getId());
					pageObject.put("name", accountInfo.getName());
					// gathering page info from Facebook, for like count and any other info that doesn't require a connection
					com.restfb.types.Page pageInfo = client.fetchObject(accountInfo.getId().toString(), com.restfb.types.Page.class);
					pageObject.put("likes", pageInfo.getLikes());
					pageObject.put("profileImage", "http://graph.facebook.com/" + accountInfo.getId() + "/picture?width=50&height=50");

					pagesArray.add(pageObject);
				}
			}

			Cache.set(userSession.getUUID() + "_facebook_accounts", facebookAccounts, 1000 * 60 * 60);
			
			result.put("success", true);
			response().setContentType("application/json");
			
			return ok(result);

		}catch (FacebookException e) {
			e.printStackTrace();
			ObjectNode result = Json.newObject();
			result.put("success", false);
			if(e instanceof FacebookGraphException){
				result.put("error", ((FacebookGraphException)e).getErrorMessage());
				return status(((FacebookGraphException)e).getHttpStatusCode() ,result);
			}else{
				result.put("error", e.getMessage());
				return internalServerError(result);
			}
		} catch (NoUUIDException e) {
			e.printStackTrace();
			e.setErrorMessage("Erro interno! Não foi possivel identificar sua sessão para: " + request().uri());
			return internalServerError(e.getJson());
		}
	}

	public static Result handler(){
		return null;
	}

}
