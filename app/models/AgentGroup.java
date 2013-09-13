package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class AgentGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	/**
	 * 255 characters only
	 * */
	@Column(nullable = false)
	private String name;
	
	@Lob
	private String description;
	
	@ManyToOne
	private Brand brand;
	
	@ManyToMany
	@JoinTable(name = "agentgroup_agent", joinColumns = {
	@JoinColumn(name = "agentgroup_id") }, inverseJoinColumns = { @JoinColumn(name = "agent_id") })
	private Set<Agent> agents = new HashSet<Agent>();
	
	private Date createdAt;
	
	private Date updatedAt;
	
}
