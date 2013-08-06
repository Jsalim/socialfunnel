package models;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import play.db.jpa.JPA;
import constants.Permissions;

@Entity(name = "roles")
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	/*
	 * Role name
	 */
	public String name;

	/*
	 * Permission set given to the role
	 */
	@ElementCollection(targetClass = Permissions.class)
	@CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permissions_id")
	public Set<Permissions> permissions;

	public static UserRole findByName(String name) {
		UserRole role = (UserRole) JPA.em().createNativeQuery("SELECT * FROM roles r WHERE name = :nome", UserRole.class).setParameter("nome", name)
				.getSingleResult();
		return role;
	}

}
