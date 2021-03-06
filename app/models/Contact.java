package models;

import javax.persistence.*;

import util.MyUtil;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; 

	@ManyToOne
	private Brand brand;
	@Column(unique = false)
	private String name;
	@Column(unique = false)
	private String emails;
	@Column(unique = false)
	private String facebookId;
	@Column(unique = false)
	private String facebookName;
	@Column(unique = false)
	private String twitterId;
	@Column(unique = false)
	private String twitterName;
	@Column(unique = false)
	private String imgLink;
	
	public Contact() {}
	
	public Brand getBrand() {
		return brand;
	}
	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getFacebookName() {
		return facebookName;
	}
	public void setFacebookName(String facebookName) {
		this.facebookName = facebookName;
	}
	public String getTwitterId() {
		return twitterId;
	}
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}
	public String getTwitterName() {
		return twitterName;
	}
	public void setTwitterName(String twitterName) {
		this.twitterName = twitterName;
	}
	
	public void mergeContact(Contact contact){
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String getImgLink() {
		return imgLink;
	}
	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	
	public void setGravatarLink(String email) {
		String gravatarUrl = "http://www.gravatar.com/avatar/";
		gravatarUrl = gravatarUrl + encryptMd5(email.trim().toLowerCase());
		if(MyUtil.httpResourceExists(gravatarUrl)){
			this.imgLink = gravatarUrl;
		}else{
			this.imgLink = null;
		}
	}
	
	public static String encryptMd5(String string) {
		try {
			return MyUtil.stringToMD5(string);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
