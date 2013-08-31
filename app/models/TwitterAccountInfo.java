package models;

import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.Logger;

/**
 * A representation of a twitter account persisted for each brand
 * @see {@link Brand#twitterAccounts} 
 * */
@Entity
public class TwitterAccountInfo extends AbstractAccountInfo {

	/**
	 * The twitter user screen name
	 * */
	private String screenName;

	/**
	 * The twitter user account tokenSecret
	 * */
	String tokenSecret;

	public TwitterAccountInfo() {}
	
	public TwitterAccountInfo(String oauthToken, String tokenSecret, String screenName, long id) throws NoSuchAlgorithmException {
		super();
		super.setId(id);
		super.setOauthToken(oauthToken);
		this.tokenSecret = tokenSecret;
		this.screenName = screenName;
	}


	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = this.screenName.equals( ((TwitterAccountInfo)obj).getScreenName() );
		return equals;
	}

	@Override
	public int hashCode() {
		return this.screenName.hashCode()*31;
	}
}
