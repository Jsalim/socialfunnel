package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@IdClass(AccountKey.class)
public abstract class AbstractAccountInfo {

	public AbstractAccountInfo() {}

	@Id
	private Long id;

	/**
	 * The user <b>extended</b> user token, page token or user-group token 
	 * */
	@Id
	private String oauthToken;	//A JSON string that can be used when making requests to the Graph API. This is also known as a user access token.

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
