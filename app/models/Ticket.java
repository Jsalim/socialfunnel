package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import constants.MediaChannels;

@Entity
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JoinColumn(nullable = false, name = "brand_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Brand brand;

	@JoinColumn(name = "created_by", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent createdBy;

	@JoinColumn(name = "assigned_to", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent assignedTo;

//	@JoinColumn(name = "account_used", nullable = false)
	@JoinColumns ( {@JoinColumn(nullable=false, name="account_id"),
		@JoinColumn(nullable=false, name="account_oauthtoken")})
	@ManyToOne(fetch = FetchType.LAZY)
	private AbstractAccountInfo account;

	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private MediaChannels channel;

	@Column(nullable = false)
	private Date createdAt;

	@Column(nullable = true)
	private Date lastUpdated;

	@Lob
	@Column(nullable = false)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Agent getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Agent assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Agent getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Agent createdBy) {
		this.createdBy = createdBy;
	}

	public AbstractAccountInfo getAccount() {
		return account;
	}

	public void setAccount(AbstractAccountInfo account) {
		this.account = account;
	}

	public MediaChannels getChannel() {
		return channel;
	}

	public void setChannel(MediaChannels channel) {
		this.channel = channel;
	}

}
