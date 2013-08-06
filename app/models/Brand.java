package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
/**
 * version: 1 </br>
 * original project: cwservice
 * */
@Entity
public class Brand implements Comparable<Brand>{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(nullable = false)
	private String name;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Agent owner;

	@Column(nullable = false)
	private Date createdAt;

	@Column(nullable = true)
	private Date updatedAt;
	
	@Column(unique = true, nullable = false)
	private String nameAddress;
	@Column(nullable = false)
	private boolean active = true;
	
	/** The mappedBy property defines which attribute in the UserBrandRole table is mapping the relationship
	 *  @see UserBrandRole#brand
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "brand", fetch = FetchType.EAGER)
	private Set<UserBrandRole> userBrandRoles = new HashSet<UserBrandRole>();
	
	@ManyToMany(targetEntity=TwitterAccountInfo.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "brand_twitteraccountinfo", joinColumns = {
	@JoinColumn(name = "brand_id") }, inverseJoinColumns = {@JoinColumn(nullable=false, name="account_id"),
			@JoinColumn(nullable=false, name="account_oauthtoken")})
	private Set<TwitterAccountInfo> twitterAccounts = new HashSet<TwitterAccountInfo>();
	
	@ManyToMany(targetEntity=FacebookAccountInfo.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "brand_facebookaccountinfo", joinColumns = {
	@JoinColumn(name = "brand_id") }, inverseJoinColumns = {@JoinColumn(nullable=false, name="account_id"),
			@JoinColumn(nullable=false, name="account_oauthtoken")})
	private Set<FacebookAccountInfo> facebookAccounts = new HashSet<FacebookAccountInfo>();
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "mybrand", fetch = FetchType.EAGER)
	private Set<StreamFilter> streamFilters = new HashSet<StreamFilter>();
	
	@Column(length = 1000, nullable = true)
	private String description;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Agent getOwner() {
		return owner;
	}

	public void setOwner(Agent owner) {
		this.owner = owner;
	}
	
	@PrePersist
	private void setCreatedAt(){
		this.createdAt = new Date();
	}
	
	@PreUpdate
	private void setUpdatedAt(){
		this.updatedAt = new Date();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Brand)obj).getId();
	}
	
	@Override
	public int hashCode() {
		return Long.toString(this.id).hashCode() * 31;
	}

	public Set<UserBrandRole> getUserBrandRoles() {
		return userBrandRoles;
	}

	public void setUserBrandRoles(Set<UserBrandRole> userBrandRoles) {
		this.userBrandRoles = userBrandRoles;
	}

	public Set<TwitterAccountInfo> getTwitterAccounts() {
		return twitterAccounts;
	}

	public void setTwitterAccounts(Set<TwitterAccountInfo> twitterAccounts) {
		this.twitterAccounts = twitterAccounts;
	}

	public Set<FacebookAccountInfo> getFacebookAccounts() {
		return facebookAccounts;
	}

	public void setFacebookAccounts(Set<FacebookAccountInfo> facebookAccounts) {
		this.facebookAccounts = facebookAccounts;
	}

	public String getNameAddress() {
		return nameAddress;
	}

	public void setNameAddress(String nameAddress) {
		this.nameAddress = nameAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public int compareTo(Brand o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<StreamFilter> getStreamFilters() {
		return streamFilters;
	}

	public void setStreamFilters(Set<StreamFilter> streamFilters) {
		this.streamFilters = streamFilters;
	}

}
