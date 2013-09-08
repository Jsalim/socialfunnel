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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.solr.analysis.BrazilianStemFilterFactory;
import org.apache.solr.analysis.EdgeNGramFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.bridge.builtin.EnumBridge;

import play.db.jpa.JPA;

import constants.MediaChannels;



/**
 * A representation of a ticket.
 * */
@Indexed
@AnalyzerDefs({
	@AnalyzerDef(name = "pt_BR",
			tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), 
			filters = { 
				@TokenFilterDef(factory = BrazilianStemFilterFactory.class),
				@TokenFilterDef(factory = LowerCaseFilterFactory.class),
				@TokenFilterDef(factory = EdgeNGramFilterFactory.class, 
				params = { 
					@Parameter(name = "maxGramSize", value = "100"), 
					@Parameter(name = "minGramSize", value = "3") 
				}),
				@TokenFilterDef(factory = StopFilterFactory.class, params = { 
					@Parameter(name = "words", value = "res/stopwordsbr.txt"), 
					@Parameter(name = "ignoreCase", value = "true") 
				}) 
			})
})
@Entity
public class Ticket {

	@Id
	@DocumentId
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
	@FieldBridge( impl = EnumBridge.class )
	@Field(index = Index.NO, store = Store.YES, analyze = Analyze.NO)
	private MediaChannels channel;

	@Column(nullable = false)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.MINUTE)
	private Date createdAt;

	@Column(nullable = true)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.MINUTE)
	private Date updatedAt;

	@Lob
	@Column(nullable = false)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = "pt_BR"))
	private String description;
	
	@Lob
	@Column(nullable = false)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = "pt_BR"))
	private String subject;
	
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.NO)
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

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date lastUpdated) {
		this.updatedAt = lastUpdated;
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

	/**
	 * This method should be executed every time a new ticket is create in order for it to calculate the ticket's Number. 
	 * */
	public void setTicketNumber() {
		if(ticketNumber <= 0 && brand != null && id > 0){
			JPA.em().createNativeQuery("UPDATE ticket as t SET t.ticketNumber = (SELECT count(*) FROM (SELECT * FROM ticket tki where tki.id <= :id ) as tk where brand_id = :brand) where t.id = :sameid ").setParameter("id", this.id).setParameter("brand", brand.getId()).setParameter("sameid", this.id) .executeUpdate();
			JPA.em().refresh(this);
		}
	}
	
	@Column(nullable = false)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = "pt_BR"))
	public String contactName;
	
	@Column(nullable = true)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = "pt_BR"))
	public String assignedAgentName;
	
	@Column(nullable = true)
	@Field(index = Index.YES, store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = "pt_BR"))
	public String createdByAgentName;
	
	@PreUpdate
	private void preUpdate(){
		this.updatedAt = new Date();
		
		if(contact != null){
			contactName = contact.getName();
		}
		
		if(assignedTo != null){
			assignedAgentName = assignedTo.getName();
		}
		
		if(createdBy != null){
			createdByAgentName = createdBy.getName();
		}
	}
	
	@PrePersist
	private void prePersist() {
		this.createdAt = new Date();
		
		if(contact != null){
			contactName = contact.getName();
		}
		
		if(assignedTo != null){
			assignedAgentName = assignedTo.getName();
		}
		
		if(createdBy != null){
			createdByAgentName = createdBy.getName();
		}
	}
	
}
