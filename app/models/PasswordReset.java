package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.joda.time.DateTime;

import play.Logger;

@Entity
public class PasswordReset {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(unique = true, nullable = false)
	private String email;

	@JoinColumn(nullable = false, name = "user_id")
	@OneToOne(fetch=FetchType.EAGER)
	private Agent user;

	@Column(nullable = false)
	private Date expires;

	@Column(unique = true, nullable = false)
	private String hash;

	public PasswordReset() {}
	
	public PasswordReset(String email, Agent user, Date expires, String hash) {
		super();
		this.email = email;
		this.user = user;
		this.expires = expires;
		this.hash = hash;
	}

	public PasswordReset(String email, Agent user, String hash) {
		super();
		this.email = email;
		this.user = user;
		this.hash = hash;
		this.expires = DateTime.now().plusDays(2).toDate();
		Logger.debug(this.expires + "");
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Agent getUser() {
		return user;
	}
	public void setUser(Agent user) {
		this.user = user;
	}
	public Date getExpires() {
		return expires;
	}
	public void setExpires(Date expires) {
		this.expires = expires;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

	public long getId() {
		return id;
	}
	
}
