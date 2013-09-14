package controllers;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import models.TwitterAccountInfo;

import interceptors.AjaxAuthCheckInterceptor;
import interceptors.DefaultInterceptor;
import interceptors.UserSessionInterceptor;

import com.typesafe.config.ConfigFactory;

import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import services.UserService;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import util.TokenRedirectUtil;
import util.UserSession;
/**
 * This class contains actions for interaction with twitter. The 2 main actions are {@link TwitterController#handler} and {@link TwitterController#callback(String, String)}. 
 * These 2 methods handle the twitter oauth dance.
 * */
@With({DefaultInterceptor.class, UserSessionInterceptor.class})
public class TwitterController extends Controller{

	private static final UserService userService = UserService.getInstance();
	// get key, secret and callbackurl from the application.conf file
	private static String key = ConfigFactory.load().getString("twapp.key");
	private static String secret = ConfigFactory.load().getString("twapp.secret");
	private static String callbackUrl = ConfigFactory.load().getString("twapp.callback");

	/**
	 * Once the user authenticates with twitter, twitter will send a request with the oauth_token and oauth_verifier.
	 * The {uuid}_twitter_request_token created in the {@link TwitterController#handler()} contains authentication 
	 * info on the request and it is recovered in order to validate and store the access token. The twitterAccounts
	 * stored in cache may be used to hold multiple twitter accounts and is bound to the uuid.
	 * @throws NoSuchAlgorithmException 
	 * */
	public static Result callback(String oauth_token, String oauth_verifier) throws NoSuchAlgorithmException{

		if(oauth_token != null && oauth_verifier != null){
			try {
				// Create a new TwitterFactory and get a new twitter cliente
				Twitter twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(key, secret); // set the OAuthConsumer token key and secret from the application.conf

				UserSession userSession = userService.getUserSession(session()); //get the userSession
				//The TokenRedirectUtil Stores the Twitter token set by the TwitterController.handler() and the redirect url send to he same action. 
				TokenRedirectUtil tokenRedirectUtil = (TokenRedirectUtil) Cache.get(userSession.getUUID() + "_twitter_request_token");
				Cache.remove(userSession.getUUID() + "_twitter_request_token"); // Remove from cache 
				// Create an accessToken from the request token and the oauth_verifier
				AccessToken accessToken = twitter.getOAuthAccessToken(tokenRedirectUtil.getRequestToken(), oauth_verifier);
//				twitter.setOAuthAccessToken(accessToken); // set the twitter accessToken
				// get the twitter accounts from cache
				HashSet<TwitterAccountInfo> twitterAccounts = (HashSet<TwitterAccountInfo>) Cache.get(userSession.getUUID() + "_twitter_accounts");
				if(twitterAccounts == null){// if twitter accounts where not set
					twitterAccounts = new HashSet<TwitterAccountInfo>();// init a twitterAccount hashset
				}
				// create a twitterAccountInfo object from the data in hand
				TwitterAccountInfo twitterAccountInfo = new TwitterAccountInfo(accessToken.getToken(), accessToken.getTokenSecret(), accessToken.getScreenName(), accessToken.getUserId());
				twitterAccounts.add(twitterAccountInfo); // add the twitter account info to the hashset
				Cache.set(userSession.getUUID() + "_twitter_accounts", twitterAccounts, 1000 * 60 * 60); // store twitter accounts for 60 min.
				// if there is a redirect url, redirect to it
				// @see the instalation process in /dashboard/gate ... add a twitter account. The twitter button passes a redirect parameter to a killwindow action
				// Since the window is a popup (@see newbrandwizard.scala.html -> jquery popup window) this action, which is run on the pop, will redirect to the killwindow.scala.html
				String redirect = tokenRedirectUtil.getRedirect();
				if(redirect != null){
					return redirect(redirect);
				}
				
				return ok();
				// TODO latertwitter.setOAuthAccessToken(accessToken);
			} catch (TwitterException e) {
				e.printStackTrace();
				ObjectNode result = Json.newObject();
				result.put("success", false);
				result.put("error", e.getMessage());
				return status(e.getStatusCode(), result);
			}
		}else{
			return badRequest("O twitter não enviou os parametros de autenticação corretos");
		}
	}
	/**
	 * This action calls twitter and asks for the user oauth_token and oauth_verifier. The &force_login passed in
	 * the request tells twitter to force the user to give his/her credential. This action checks for a redirect
	 * parameter which is stored in cache bound by the users UUID ({uuid}_twitter_request_token).   
	 * */
	public static Result handler(){
		try {
			// Create a new TwitterFactory and get a new twitter cliente
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(key, secret); // set the OAuthConsumer token key and secret from the application.conf
			// Get the callback url and start a requestToken
			RequestToken twitterRequestToken = twitter.getOAuthRequestToken(callbackUrl);
			// Check for a redirect parameter on the query string.
			String param[] = request().queryString().get("redirect");
			String redirect = (param != null && param.length > 0) ? param[0] : null; 
			// Create a TokenRedirectUtil object with the requestToken and redirect url 
			TokenRedirectUtil tokenRedirectUtil = new TokenRedirectUtil(twitterRequestToken, redirect);
			UserSession userSession = userService.getUserSession(session());// get a userSessionObject
			// store the tokenRedirectUtil for 5 min. Plenty of time for twitter user to authenticate and twitter to redirect calling the callback action 
			Cache.set(userSession.getUUID() + "_twitter_request_token", tokenRedirectUtil, 1000 * 60 * 5); 
			// The force login parameter tells twitter to force the user to authenticate even though he may already be 
			return redirect(twitterRequestToken.getAuthorizationURL() + "&force_login=true");
		} catch (TwitterException e) {
			e.printStackTrace();
			ObjectNode result = Json.newObject();
			result.put("success", false);
			result.put("error", e.getMessage());
			return status(e.getStatusCode(), result);
		}
	} 
	/**
	 * This action gets all TwitterAccounts info bound to the user's UUID {uuid}_twitter_request_token
	 * and returns as a json object.
	 * */
	@With(AjaxAuthCheckInterceptor.class)
	public static Result accounts(){
		try {
			UserSession userSession = userService.getUserSession(session()); //get the current userSession
			// Create a new TwitterFactory and get a new twitter cliente
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(key, secret); // set the OAuthConsumer token key and secret from the application.conf
			
			ObjectNode result = Json.newObject();// create an empty result json object
			// get the twitter accounts from cache
			HashSet<TwitterAccountInfo> twitterAccounts = (HashSet<TwitterAccountInfo>) Cache.get(userSession.getUUID() + "_twitter_accounts");
			if(twitterAccounts == null){// if twitter accounts where not set
				twitterAccounts = new HashSet<TwitterAccountInfo>();// init a twitterAccount hashset
			}
			
			ArrayNode arrayNode = result.putArray("users"); // create an array object called user in the result json
			for(TwitterAccountInfo twitterAccount: twitterAccounts){ // iterate all twitterAccounts
				ObjectNode row = Json.newObject();// create a new object to be added to the json array
				// init a twitter4j client by adding the accessToke and tokenSecret stored in twitterAccounts in cache
				twitter.setOAuthAccessToken(new AccessToken(twitterAccount.getOauthToken(), twitterAccount.getTokenSecret()));
				// Call the verify credentials which returns a twitter user object this should no return a 
				User user = twitter.verifyCredentials();
				
				row.put("screenName", twitterAccount.getScreenName());
				row.put("followers", user.getFollowersCount() + "");
				row.put("profileImage", user.getProfileImageURL());
				row.put("id", user.getId());

				arrayNode.add(row);
			}
			response().setContentType("application/json");
			result.put("success", true);
			return ok(result);
		} catch (TwitterException e) {
			e.printStackTrace();
			ObjectNode result = Json.newObject();
			result.put("success", false);
			result.put("error", e.getMessage());
			return status(e.getStatusCode(), result);
		}
	}

}
