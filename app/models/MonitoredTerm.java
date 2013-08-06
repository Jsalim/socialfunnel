package models;

import java.util.HashSet;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import constants.MediaChannels;

/**
 * version: 1 </br>
 * original project: cwservice
 * */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"term", "channel"}))
public class MonitoredTerm{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String term;
	
	@Column (nullable = false)
	@Enumerated(EnumType.STRING)
	private MediaChannels channel; // Social Network

	@ManyToMany(targetEntity=Brand.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "monitoredterm_brand", joinColumns = {
	@JoinColumn(name = "monitoredterm_id") }, inverseJoinColumns = { @JoinColumn(name = "brand_id") })
	private Set<Brand> brands = new HashSet<Brand>();

	public MonitoredTerm() {
	}	
	public MonitoredTerm(String term, Brand brand, MediaChannels channel) {
		this.term = term;
		this.brands.add(brand);
		this.channel = channel;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Set<Brand> getBrands() {
		return brands;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEquals = this.term.equals(((MonitoredTerm) obj).getTerm()) && ( this.channel == ((MonitoredTerm) obj).getChannel() );
		return isEquals;
	}
	
	@Override
	public int hashCode() {
		return this.term.hashCode()*31;
	}
	
	public MediaChannels getChannel() {
		return channel;
	}
	
	public void setChannel(MediaChannels channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return this.term + " " + this.brands + " " + this.channel;
	}
	public void setBrands(Set<Brand> brands) {
		this.brands = brands;
	}
}
