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
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import play.db.jpa.JPA;

import constants.MediaChannels;

/**
 * A representation of a ticket.
 * */
@Entity
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JoinColumn(nullable = false, name = "brand_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Brand brand;

	@JoinColumn(name = "created_by", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent createdBy;

	@JoinColumn(name = "assigned_to", nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent assignedTo;

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
	
	@Lob
	@Column(nullable = false)
	private String subject;
	
	private long ticketNumber;
	
	@ManyToOne
	private Contact contact;
	
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

	@PrePersist
	private void setCreatedAt() {
		this.createdAt = new Date();
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public long getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber() {
		if(ticketNumber <= 0 && brand != null && id > 0){
			JPA.em().createNativeQuery("UPDATE ticket as t SET t.ticketNumber = (SELECT count(*) FROM (SELECT * FROM ticket tki where tki.id <= :id ) as tk where brand_id = :brand) where t.id = :sameid ").setParameter("id", this.id).setParameter("brand", brand.getId()).setParameter("sameid", this.id) .executeUpdate();
			JPA.em().refresh(this);
		}
	}

}
