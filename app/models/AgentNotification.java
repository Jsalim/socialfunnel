package models;

import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import constants.NotificationTypes;
@Entity
public class AgentNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String title; // max 255 chars
	
	@Lob
	@Column(nullable = false)
	private String message; // long text 
	
	@Lob 
	@Column(length=512, nullable = true)
	private String link; //max 512 chars
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationTypes notificationType;

	@Column(nullable = false) 
	private Date createdAt;
	
	@Column(nullable = false)
	private Date updatedAt;
	
	@Column(nullable = false)
	private Boolean hasSeen = false;
	
	/** The mappedBy property defines which attribute in the UserBrandRole table is mapping the relationship
	 *  @see UserBrandRole#brand
	 * */
	@ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name="agent_id")
	private Agent agent;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public NotificationTypes getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationTypes notificationType) {
		this.notificationType = notificationType;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.id == ((AgentNotification)obj).getId();
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Long.toString(this.id).hashCode() * 31;
	}
	
	@PrePersist
	private void setCreatedAt(){
		this.createdAt = new Date();
	}

	@PreUpdate
	private void setUpdatedAt(){
		this.updatedAt = new Date();
	}

	public Boolean getHasSeen() {
		return hasSeen;
	}

	public void setHasSeen(Boolean hasSeen) {
		this.hasSeen = hasSeen;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
}
