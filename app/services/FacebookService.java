package services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.User;
import com.typesafe.config.ConfigFactory;

public class FacebookService {

	private static FacebookService instance;

	private static String key = ConfigFactory.load().getString("fbapp.key");
	private static String secret = ConfigFactory.load().getString("fbapp.secret");
	private static String callbackUrl = ConfigFactory.load().getString("fbapp.callback");

	private FacebookService(){}

	public static FacebookService getInstance(){
		if(instance == null){
			instance = new FacebookService();
		}
		
		return instance;
	}

	public JsonNode validateSignedRequest(String signedRequest) {
		try {
			//it is important to enable url-safe mode for Base64 encoder 
			Base64 base64 = new Base64();

			//split request into signature and data
			String[] splitedSignedRequest = signedRequest.split("\\.", 2);

			//parse signature
			String sig = new String(base64.decode(splitedSignedRequest[0].getBytes("UTF-8")));

			//parse data and convert to json object
			String json = new String(base64.decode(splitedSignedRequest[1].getBytes("UTF-8")));
			JsonNode data = Json.parse(json);

			if(!hmacSHA256(splitedSignedRequest[1], secret).equals(sig)) {
				Logger.error("WTF?? Failed facebook signature check!");
				return null;
			}

			return data;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	//HmacSHA256 implementation 
	private String hmacSHA256(String data, String secret) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKey);
		byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
		return new String(hmacData);
	}

	public JsonNode getUserFromSignedRequest(String signedRequest){
		if(signedRequest != null && !signedRequest.isEmpty()){
			JsonNode sr = validateSignedRequest(signedRequest);

			if(sr.has("user_id")){
				return sr;
			}
		}
		return null;
	}

	//https://graph.facebook.com/oauth/access_token?client_id=APP_ID&client_secret=APP_SECRET&grant_type=fb_exchange_token&fb_exchange_token=EXISTING_ACCESS_TOKEN
	public String extendAccessToken(String accessToken) {
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken);

		//		JsonObject newToken = facebookClient.fetchObject("/oauth/access_token", JsonObject.class , 
		//				Parameter.with("client_id", key),
		//				Parameter.with("client_secret", secret), 
		//				Parameter.with("grant_type","fb_exchange_token"),
		//				Parameter.with("fb_exchange_token", accessToken)
		//				);
		AccessToken token = facebookClient.obtainExtendedAccessToken(key, secret, accessToken);

		//		return newToken.getString("oauth_token");

		return token.getAccessToken();
	}

	public JsonNode getUserPages(String accessToken) {
		FacebookClient client = new DefaultFacebookClient(accessToken);
		JsonObject pages = client.fetchObject("/me/accounts", JsonObject.class, Parameter.with("type", "page"));

		JsonArray jsonArray = pages.getJsonArray("data");

		ObjectNode result = Json.newObject();

		ArrayNode fbPages = result.putArray("data");
		//		graph.facebook.com/506026989448001/picture?width=34&height=34
		for(int i=0; i < jsonArray.length(); i++){
			ObjectNode row = Json.newObject();

			row.put("id", jsonArray.getJsonObject(i).getString("id"));
			row.put("perms", jsonArray.getJsonObject(i).getJsonArray("perms").toString());
			row.put("name", jsonArray.getJsonObject(i).getString("name"));
			row.put("accessToken", jsonArray.getJsonObject(i).getString("access_token"));
			row.put("profileImage", "http://graph.facebook.com/" + jsonArray.getJsonObject(i).getString("id") + "/picture?width=50&height=50");

			fbPages.add(row);
		}

		return result;
	}

	public JsonNode getUserProfile(String accessToken) {
		FacebookClient client = new DefaultFacebookClient(accessToken);

		User user = client.fetchObject("me", User.class);

		ObjectNode object = Json.newObject();

		object.put("userID", user.getId());
		object.put("userName", user.getName());

		return object;
	}

	public int getFriendsCount(String accessToken){
		FacebookClient client = new DefaultFacebookClient(accessToken);
		int friendsCount = client.fetchConnection("me/friends", User.class).getData().size();
		return friendsCount; 
	}

	public JsonNode getGroups(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

}
