package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import constants.AppTypes;

@Entity
public class App {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = true)
	private String tabAction;
	
	@ElementCollection(targetClass = AppTypes.class)
	@CollectionTable(name = "app_types", joinColumns = @JoinColumn(name = "app_id"))
	@Column(name = "apptypes_id", nullable = false, length = 40)
	@Enumerated(EnumType.STRING)
	private Set<AppTypes> appTypes = new HashSet<AppTypes>();

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTabAction() {
		return tabAction;
	}

	public void setTabAction(String tabAction) {
		this.tabAction = tabAction;
	}

	public Set<AppTypes> getAppTypes() {
		return appTypes;
	}

}
