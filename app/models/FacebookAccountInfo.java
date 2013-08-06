package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Transient;

import play.Logger;

import constants.FacebookAccountTypes;

/**
 * A representation of a facebook account persisted for each brand
 * @see {@link Brand#facebookAccounts} 
 * */
@Entity
public class FacebookAccountInfo extends AbstractAccountInfo {
	
	public FacebookAccountInfo() {}

	/**
	 * Add a user account
	 * */
	public FacebookAccountInfo(long id, String name, String oauthToken, FacebookAccountTypes accountType) {
		super();
		super.setId(id);
		super.setOauthToken(oauthToken);
		this.name = name;
		this.accountType = accountType;
	}
	/**
	 * Add a page account
	 * */
	public FacebookAccountInfo(long id, String name, String perms, String oauthToken, FacebookAccountTypes accountType) {
		super();
		super.setId(id);
		super.setOauthToken(oauthToken);
		this.name = name;
		this.accountType = accountType;
		this.perms = perms;
	}

	/**
	 * The full name of the user, page or group
	 * */
	private String name;
	/**
	 * The user Json String return in the signed_request
	 * */
	private String user;	//A JSON object containing the locale string, country string and the age object. See the Age Object table for actual min and max values.

	/**
	 * The string representation of all the permissions for pages
	 * */
	private String perms;
	
	/**
	 * The facebook account types may be {@link FacebookAccountInfo#FACEBOOK_USER}, {@link FacebookAccountInfo#FACEBOOK_PAGE} or {@link FacebookAccountInfo#FACEBOOK_GROUP}
	 * */
	@Enumerated(EnumType.STRING)
	private FacebookAccountTypes accountType;

	@Override
	public String toString() {
		String ret =
		"\n\nid: " + super.getId() +
		"\n\nuser: " + user +
		"\n\nname: " + name +
		"\n\noauthToken: " + super.getOauthToken();
				
		return ret;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FacebookAccountTypes getAccountType() {
		return accountType;
	}

	public void setAccountType(FacebookAccountTypes accountType) {
		this.accountType = accountType;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = this.getId().longValue() == ((FacebookAccountInfo)obj).getId().longValue();
		return equals;
	}
	
	@Override
	public int hashCode() {
		return Long.toString(this.getId()).hashCode()*31;
	}
}
