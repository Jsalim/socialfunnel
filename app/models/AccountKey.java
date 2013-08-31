package models;

import java.io.Serializable;

public class AccountKey implements Serializable{
	private Long id;

	/**
	 * The user <b>extended</b> user token, page token or user-group token 
	 * */
	private String authhash;
	
	public AccountKey() {}

	public AccountKey(Long id, String authhash) {
		super();
		this.id = id;
		this.authhash = authhash;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthHash() {
		return authhash;
	}

	public void setAuthHash(String authHash) {
		this.authhash = authHash;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return id == ((AccountKey)obj).getId() && authhash.equals( ((AccountKey)obj).getAuthHash()) ;
	}
	
	@Override
	public int hashCode() {
		return authhash.hashCode() * 31;
	}
}