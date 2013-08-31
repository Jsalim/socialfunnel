package models;

import java.security.NoSuchAlgorithmException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import util.MyUtil;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@IdClass(AccountKey.class)
public abstract class AbstractAccountInfo {

	public AbstractAccountInfo() {}

	@Id
	@Column(name="account_id")
	private Long id;

	@Id
	@Column(length=40, name="account_authhash")
	private String authhash;
	
	/**
	 * The user <b>extended</b> user token, page token or user-group token 
	 * */
	private String oauthToken;	//A JSON string that can be used when making requests to the Graph API. This is also known as a user access token.

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) throws NoSuchAlgorithmException {
		this.authhash = MyUtil.stringToMD5(oauthToken);
		this.oauthToken = oauthToken;
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

	public void setAuthHash(String authhash) {
		this.authhash = authhash;
	}

}
