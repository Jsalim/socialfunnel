package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * The relationship-entity-table between a {@link Agent} and a {@link Brand}
 * */
@Entity
public class UserBrandRole {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	/**
	 * A description of the role (probably never gonna be used)
	 * */
	public String description;

	/**
	 * The {@link Agent} part of the relationship table
	 * */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	public Agent user;
	/**
	 * The {@link Brand} part of the relationship table
	 * */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "brand_id")
	public Brand brand;
	
	@Column(nullable = false)
	public boolean active = true;

	/**
	 * The {@link UserRole} for the brand
	 * */
	@OneToOne(fetch = FetchType.EAGER)
	public UserRole role;

}
