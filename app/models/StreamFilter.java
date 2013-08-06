package models;

import java.util.ArrayList;
import java.util.Date;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import constants.FilterTypes;

@Entity
public class StreamFilter {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false) 
	private String filterName;
	
	@Enumerated(EnumType.STRING)
	private FilterTypes filterType; 
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "brand_id", nullable = false)
	public Brand mybrand;
	
	@Column(nullable = false)
	private String searchExpression = "";

	@Column(nullable = false)
	private Date beginning;
	@Column(nullable = false)
	private Date ending;
	
	@Lob
	private String jsonProfiles = "{}";
	@Lob
	private String jsonMessageTypes = "{}";
	@Lob
	private String jsonChannels = "{}";

	@Lob
	private String jsonTerms = "{}";
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Brand getBrand() {
		return mybrand;
	}
	public void setBrand(Brand brand) {
		this.mybrand = brand;
	}
	public String getSearchExpression() {
		return searchExpression;
	}
	public void setSearchExpression(String searchExpression) {
		this.searchExpression = searchExpression;
	}
	public Date getBeginning() {
		return beginning;
	}
	public void setBeginning(Date beginning) {
		this.beginning = beginning;
	}
	public Date getEnding() {
		return ending;
	}
	public void setEnding(Date ending) {
		this.ending = ending;
	}
	public String getJsonProfiles() {
		return jsonProfiles;
	}
	public void setJsonProfiles(String jsonProfiles) {
		this.jsonProfiles = jsonProfiles;
	}
	public String getJsonMessageTypes() {
		return jsonMessageTypes;
	}
	public void setJsonMessageTypes(String jsonMessageTypes) {
		this.jsonMessageTypes = jsonMessageTypes;
	}
	public String getJsonChannels() {
		return jsonChannels;
	}
	public void setJsonChannels(String jsonChannels) {
		this.jsonChannels = jsonChannels;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public Brand getMybrand() {
		return mybrand;
	}
	public void setMybrand(Brand mybrand) {
		this.mybrand = mybrand;
	}
	public FilterTypes getFilterType() {
		return filterType;
	}
	public void setFilterType(FilterTypes filterType) {
		this.filterType = filterType;
	}
	public String getJsonTerms() {
		return jsonTerms;
	}
	public void setJsonTerms(String jsonTerms) {
		this.jsonTerms = jsonTerms;
	}
	
}
