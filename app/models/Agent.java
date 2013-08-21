package models;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import constants.UserStatus;

import play.Logger;
import play.data.validation.Constraints.Email;
import util.MyUtil;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class Agent {
	
	public Agent() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Email
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, length = 30, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private Date createdAt;

	@Column(nullable = true)
	private Date updatedAt;

	@Column(nullable = true)
	private Date lastLogin;

	@Column(nullable = false)
	private int loginCounter = 0;
	
	@Column(nullable = true)
	private String phone;
	
	@Lob
	private String details;

	/** The mappedBy property defines which attribute in the UserBrandRole table is mapping the relationship
	 *  @see UserBrandRole#user
	 *  */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
	Set<UserBrandRole> userBrandRoles;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status = UserStatus.STATUS_INACTIVE;

	@Column(nullable = true)
	private String imgLink;

	public Agent(String name, String email, String username, String password) {
		this.name = name;
		this.email = email;
		this.username = username;
		setPassword(password);
		this.setGravatarLink(email);
	}
	/**
	 * If a non-default controller is present there is a need for a
	 * default constructor to be explicitly written.in order for 
	 * the Play!framework Form to work 
	 * @param phone2 
	 * @param password2 
	 * @param username2 
	 * @param email2 
	 * @param name2 
	 * */
	public Agent(String name2, String email2, String username2, String password2, String phone2){
		this.name = name2;
		this.email = email2;
		this.username = username2;
		this.phone = phone2;
		setPassword(password);
	}
	
	private void setGravatarLink(String email) {
		String gravatarUrl = "http://www.gravatar.com/avatar/";
		gravatarUrl = gravatarUrl + encryptMd5(email.trim().toLowerCase());
		if(MyUtil.httpResourceExists(gravatarUrl)){
			this.imgLink = gravatarUrl;
		}else{
			this.imgLink = null;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = encryptMd5(password);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean validatePassword(String password) {
		String encPass = encryptMd5(password);
		return this.password.equals(encPass);
	}

	public static String encryptMd5(String string) {
		try {
			return MyUtil.stringToMD5(string);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@PrePersist
	private void setCreatedAt(){
		this.createdAt = new Date();
	}

	@PreUpdate
	private void setUpdatedAt(){
		this.updatedAt = new Date();
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public int getLoginCounter() {
		return loginCounter;
	}

	public void setLoginCounter(int loginCounter) {
		this.loginCounter = loginCounter;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Set<UserBrandRole> getUserBrandRoles() {
		return userBrandRoles;
	}

	public UserStatus getStatus() {
		return status;
	}
	
	public void setStatus(UserStatus status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return username;
	}

	@Override
	public boolean equals(Object obj) {
		return username.equals(((Agent)obj).getUsername());
	}

	@Override
	public int hashCode() {
		return username.hashCode() * 31;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
}
