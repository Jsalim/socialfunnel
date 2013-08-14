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

import constants.MediaChannels;

/**
 * A representation of a ticket comment for a ticket instance
 * @see {@link Ticket} and {@link Agent} 
 * */
@Entity
public class TicketComment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** A ticket id reference*/
	@JoinColumn(nullable = false, name = "ticket_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Ticket ticket;

	/** ticket comment represented as a reply */
	@JoinColumn(nullable = true, name = "reply_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TicketComment replyTo;

	@JoinColumn(nullable = true, name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Agent from;

	/** The channel medium */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MediaChannels channel;
	
	@Column(nullable = false)
	private Date createdDate;

	@Lob
	@Column(nullable = false)
	private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public TicketComment getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(TicketComment replyTo) {
		this.replyTo = replyTo;
	}

	public Agent getFrom() {
		return from;
	}

	public void setFrom(Agent from) {
		this.from = from;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MediaChannels getChannel() {
		return channel;
	}

	public void setChannel(MediaChannels channel) {
		this.channel = channel;
	}

}
