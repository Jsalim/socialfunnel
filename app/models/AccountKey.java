package models;

import java.io.Serializable;

public class AccountKey implements Serializable{
	private Long id;

	/**
	 * The user <b>extended</b> user token, page token or user-group token 
	 * */
	private String oauthToken;
	
	public AccountKey() {}

	public AccountKey(Long id, String oauthToken) {
		super();
		this.id = id;
		this.oauthToken = oauthToken;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return id == ((AccountKey)obj).getId() && oauthToken.equals( ((AccountKey)obj).getOauthToken()) ;
	}
	
	@Override
	public int hashCode() {
		return oauthToken.hashCode() * 31;
	}
}