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

import constants.AppNames;
import constants.AppTypes;

@Entity
public class App {
	
	@Id
	@Column(length = 40)
	private String id;
	
	@Column(nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private AppNames name;

	@Column(nullable = true)
	private String tabAction;
	
	@ElementCollection(targetClass = AppTypes.class)
	@CollectionTable(name = "app_apptypes", joinColumns = @JoinColumn(name = "app_id"))
	@Column(name = "apptypes", nullable = false, length = 40)
	@Enumerated(EnumType.STRING)
	private Set<AppTypes> appTypes = new HashSet<AppTypes>();

	public String getId() {
		return id;
	}

	public AppNames getName() {
		return name;
	}

	public void setName(AppNames name) {
		this.id = name.toString();
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
