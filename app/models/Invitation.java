package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.joda.time.DateTime;

@Entity
public class Invitation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique=true)
	private String hash;
	
	@ManyToOne(cascade = CascadeType.ALL, optional=true)
	@JoinColumn(name = "brand_id")
	private Brand brand;

	private String name;

	private String email;

	private Date expires;
	
	private Date createdAt;
	@Column(nullable = false)
	private boolean active = true;
	
	public Invitation() {}
	
	public Invitation(String hash, Brand brand, String name, String email) {
		this.hash = hash;
		this.brand = brand;
		this.name = name;
		this.email = email;
		this.expires = DateTime.now().plusDays(2).toDate();
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getExpires() {
		return expires;
	}

	public Date getCreatedAt() {
		return createdAt;
	}
	
	@PrePersist
	private void prePersist(){
		if(this.createdAt == null){
			this.createdAt = new Date();
		} 
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return email.equals(((Invitation)obj).getEmail());
	}
	
	@Override
	public int hashCode() {
		return this.email.hashCode() * 31;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

}
