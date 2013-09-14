package services;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.BasicQuery;

import com.mongodb.MongoException;

import bootstrap.DS;
import bootstrap.MongoConfig;

import constants.Permissions;
import constants.RoleName;

import models.AgentNotification;
import models.Brand;
import models.Invitation;
import models.PasswordReset;
import models.Agent;
import models.UserBrandRole;
import models.UserRole;
import play.Configuration;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.Http.Session;
import util.MyUtil;
import util.UserSession;

import static play.data.Form.form;
import play.data.Form;
import play.data.validation.ValidationError;
/**
 * @author marcio
 * 
 *         This class holds all installation and authentication business logic
 * */
public final class UserService {
	/** static instance of the class called by the {@link #getInstance()} */
	private static UserService instance = null;

	//	private static final EmailService emailService = EmailService.getInstance();

	/**
	 * Empty constructor to guarantee that this constructor can only be called
	 * from within this class
	 * */
	private UserService() {
		// Empty constructor
	}

	/**
	 * Static method for recovering the {@link UserAuthService} singleton
	 * instance without having to instantiate an object HTTP requests
	 * 
	 * @return The singleton instance.
	 */
	public static UserService getInstance() {
		if (instance == null) {
			instance = new UserService();
		}

		return instance;
	}

	/**
	 * Find the user by name in the DB.
	 * 
	 * @param username
	 *            the users's username
	 * @return The user Model object
	 * */
	public Agent findByUsername(String username) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT u.* FROM Agent u WHERE username = :username", Agent.class);
		query.setParameter("username", username);

		Agent user = null;
		try {
			user = (Agent) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Find the user by name in the DB.
	 * 
	 * @param username
	 *            the users's username
	 * @return The user Model object
	 * */
	public Agent findByEmail(String email) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT u.* FROM Agent u WHERE email = :email", Agent.class);
		query.setParameter("email", email);

		Agent user = null;
		try {
			user = (Agent) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public Form<Agent> validateAgentForm(String name, String email, String username, String password){

		Form<Agent> agentForm = new Form<Agent>(Agent.class);

		if(agentForm != null){
			if(name == null){
				agentForm.errors().put("name", Arrays.asList(new ValidationError("name", "Nome inválido")));
			}else if(name.length() < 3 || name.length() >= 50){
				agentForm.errors().put("name", Arrays.asList(new ValidationError("name", "Nome deve conter 3 a 50 caracteres.")));
			}

			if(email == null || MyUtil.isEmailAddr(email) == false){
				agentForm.errors().put("email", Arrays.asList(new ValidationError("email", "Email inválido.")));
			}else if(exists(email)){
				agentForm.errors().put("email", Arrays.asList(new ValidationError("email", "Email já está em uso")));
			}

			if(username == null){
				agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username inválido.")));
			}else if(username.length() < 3 || username.length() >= 30){
				agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username deve conter 3 a 30 caracteres.")));
			}else if(exists(username)){
				agentForm.errors().put("username", Arrays.asList(new ValidationError("username", "Username já está em uso.")));
			}

			if(password == null){
				agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha inválido.")));
			}else if(password.length() < 6){
				agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha deve ter no mínimo 6 caracteres.")));
			}else{
				boolean upperFound = false, hasNumber = false;
				for (char c : password.toCharArray()) {
					if (Character.isUpperCase(c)) {
						upperFound = true;
						break;
					}
				}
				for (char c : password.toCharArray()) {
					if (Character.isDigit(c)){
						hasNumber = true;
					}
				}

				if(upperFound && hasNumber){
					agentForm.errors().put("password", Arrays.asList(new ValidationError("password", "Senha deve conter ao menos 1 caracter maiúsculo e 1 dígito.")));
				}
			}
			return agentForm;
		}else {
			return null;
		}
	}

	/**
	 * Save a user no database
	 * 
	 * @param user
	 *            The user Object built by binding the HTTP action parameters
	 *            from the play framework
	 * @return true if the user is persisted or false if JPA EM throws an
	 *         exception.
	 * */
	public boolean create(Agent user) {
		try {
			JPA.em().persist(user);
		} catch (PersistenceException e) {
			return false;
		}
		return true;
	}

	/**
	 * Save a user no database
	 * 
	 * @param user
	 *            The user Object built by binding the HTTP action parameters
	 *            from the play framework
	 * @return true if the user is persisted or false if JPA EM throws an
	 *         exception.
	 * */
	public boolean delete(Agent user) {
		try {
			JPA.em().remove(user);
		} catch (PersistenceException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the user exists on database.
	 * 
	 * @param usernameOrEmail
	 *            String representing a username or email
	 * @return true if there is at least 1 username or email is persisted
	 * */
	public boolean exists(String usernameOrEmail) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT COUNT(id) as count FROM Agent u WHERE username = :uoe OR email = :uoe");
		query.setParameter("uoe", usernameOrEmail);

		int count = 0;
		try {
			count = ((BigInteger) query.getSingleResult()).intValue();
		} catch (NoResultException e) {
			// TODO: Logar busca de usuário inexistente
		}
		return count > 0;
	}

	/**
	 * Get existing user session or creates a new one if doesn't exist
	 * 
	 * @param session
	 * @return the user session
	 * @throws NoUUIDException
	 */
	public UserSession getUserSession(Session session){
		String uuid = session.get("uuid");
		// if the session object has no uuid, create one
		if (uuid == null) {
			uuid = java.util.UUID.randomUUID().toString();
			session.put("uuid", uuid);
			Logger.debug("CREATING NEW UUID: " + uuid);
		}
		// try to find a user session on cache
		UserSession userSession = (UserSession) Cache.get(uuid + "_user");
		// if session is not found create
		if (userSession == null) {
			userSession = createNewUserSession(uuid);
		}
		return userSession;
	}

	/**
	 * Update existing user session.
	 * <br>
	 * RECOMMENDATION: THIS METHOD SHOULD BE USE WITH CAUTION. DB ACCESS OVERHEAD ALERT!! 
	 * TRY USING THIS METHOD ONLY WHEN NECESSERY (EX. ON TOTAL PAGE RELOAD, ... {@link Dashboard} ACTIONS
	 * 
	 * @param userSession
	 *            the user session object to be udpate.
	 * @return the updated user session object
	 * */
	public UserSession updateExistingUserSession(UserSession userSession) {
		Cache.set(userSession.getUUID() + "_user", userSession, Play.application().configuration().getInt("dasboard.cache.expiration"));
		return (UserSession) Cache.get(userSession.getUUID() + "_user");
	}

	public UserSession updateUsersNotificationInCache(UserSession userSession) {
		List<AgentNotification> agentNotifications = getUnseenNotifications(userSession);
		userSession.setUnSeenNotis(agentNotifications);
		return updateExistingUserSession(userSession);
	}

	/**
	 * Create a new user session
	 * 
	 * @param uuid
	 * @return the user session that was just created
	 * */
	public UserSession createNewUserSession(String uuid) {
		UserSession userSession = new UserSession();
		userSession.setUUID(uuid);
		Cache.set(userSession.getUUID() + "_user", userSession, Play.application().configuration().getInt("dasboard.cache.expiration"));
		Logger.debug("Creating New UserSession: New user session chached in memory for " + Configuration.root().getInt("dasboard.cache.expiration")
				+ "s - " + uuid);
		return userSession;
	}

	public void updateLastLogin(UserSession userSession) {
		userSession.getUser().setLastLogin(new Date());
	}

	public void incrementLoginCounter(UserSession userSession) {
		userSession.getUser().setLoginCounter(userSession.getUser().getLoginCounter() + 1);

	}

	public Agent findById(Long id) {
		return JPA.em().find(Agent.class, id);
	}

	public boolean createResetPassword(String email) {
		Agent user = findByEmail(email);

		try {
			if (user != null) {
				Query query = JPA.em().createNativeQuery("SELECT pr.* FROM PasswordReset pr WHERE pr.user_id = :id ", PasswordReset.class);
				query.setParameter("id", user.getId());
				PasswordReset passwordReset;
				try {
					passwordReset = (PasswordReset) query.getSingleResult();
					passwordReset.setExpires(DateTime.now().plusDays(2).toDate());
				} catch (NoResultException e) {
					passwordReset = new PasswordReset(email, user, MyUtil.stringToMD5(email + new Date() + new Random().nextInt(Integer.MAX_VALUE)));
				}

				//				emailService.resetEmail(passwordReset);

				JPA.em().persist(passwordReset);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkResetPassword(String hash) {
		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
		query.setParameter("hash", hash);
		List<PasswordReset> list = null;
		try {
			list = query.getResultList();
		} catch (NoResultException e) {
			return false;
		}
		if (list != null && list.size() == 1) {
			Logger.debug(list.get(0).getUser().getName());
			return true;
		} else {
			return false;
		}
	}

	public PasswordReset getResetPassword(String hash) {
		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
		query.setParameter("hash", hash);
		List<PasswordReset> list = null;
		try {
			list = query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public boolean resetPassword(String hash, String password) {
		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
		query.setParameter("hash", hash);
		List<PasswordReset> list = null;
		try {
			list = query.getResultList();
		} catch (NoResultException e) {
			return false;
		}
		if (list != null && list.size() == 1) {
			Agent user = list.get(0).getUser();
			user.setPassword(password);
			JPA.em().persist(user);
			return true;
		} else {
			return false;
		}
	}

	public boolean processInvitation(Agent user, String hash) {
		try {

			JPA.em().persist(user);

			Query query = JPA.em().createQuery("SELECT i FROM Invitation i WHERE i.hash = :hash AND i.active = true");
			query.setParameter("hash", hash);

			List<Invitation> list = null;
			list = query.getResultList();

			if (list != null && list.size() == 1) {

				Invitation invitation = list.get(0);
				Brand brand = invitation.getBrand();

				UserBrandRole userBrandRole = new UserBrandRole();
				userBrandRole.brand = brand;
				userBrandRole.user = user;
				//				userBrandRole.role = UserRole.findByName("AGENT");
				userBrandRole.role = UserRole.findByName(RoleName.AGENT);
				userBrandRole.active = true;

				JPA.em().persist(userBrandRole);
				return true;

			} else {
				return false;
			}

		} catch (PersistenceException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<AgentNotification> getUnseenNotifications(UserSession userSession) {
		Agent agent = userSession.getUser();
		if(agent != null){
			Query query = JPA.em().createNativeQuery("SELECT an.* FROM AgentNotification an WHERE (an.hasSeen = :hasSeen and an.agent_id = :agent)", AgentNotification.class);
			query.setParameter("hasSeen", false).setParameter("agent", agent.getId());
			List<AgentNotification> notifications = query.getResultList();
			return notifications;
		}else{
			return new ArrayList<AgentNotification>();
		}
	}

	public List<AgentNotification> getAgentNotification(UserSession userSession, int limit, long offset){
		if(limit >= 0 && offset >= 0){
			Agent agent = userSession.getUser();
			if(agent != null){
				Query query = JPA.em().createNativeQuery("SELECT an.* FROM AgentNotification an WHERE an.agent_id = :agent order by an.createdAt DESC LIMIT :limit OFFSET :offset", AgentNotification.class);
				query.setParameter("agent", agent.getId());
				query.setParameter("limit", limit);
				query.setParameter("offset", offset);
				List<AgentNotification> notifications = query.getResultList();
				return notifications;
			}else{
				return new ArrayList<AgentNotification>();
			}
		}else{
			return new ArrayList<AgentNotification>();
		}
	}

	public boolean updateUserNotifications(
			UserSession userSession, Long notificationId) {
		// TODO Auto-generated method stub
		return true;
	}
	/**
	 * This method is responsible for querying the mongodb for for sessions that are stored at login time.
	 * If another browser opens a session by logging in to the system then the current session in the current
	 * browser is invalidated.    
	 * */
	public void validateDistributedSession(UserSession userSession) {
		try{
			Date date = new Date();
			if(MongoConfig.isOnline()){  // if mongodb is online
				if(userSession.getAgentId() != null){ // if user is logged in.
					//DS.mop.remove(new BasicQuery("{ agentId : " + userSession.getAgentId() + " ,  UUID: {$ne: \"" + userSession.getUUID() + "\"}}"), UserSession.class);
					UserSession us = DS.mop.findOne(new BasicQuery("{ agentId : " + userSession.getAgentId() + "}"), UserSession.class); // find users session for the current user
					if(us != null && us.getLastRequest().after(userSession.getLastRequest()) && !us.getUUID().equals(userSession.getUUID())){
						// if a session in mongodb is found and that session has a {lastRequest} date greater than the current session's {lastRequest} and the its UUID in different then the current 
						userSession.setValidSession(false);// invalidate the current session
						updateExistingUserSession(userSession); // update in cache userSession
					}else{// save in mongoDB only if its a valid distributed sessuion;
						userSession.setLastRequest(date); // update last request
						DS.mop.save(userSession); // save session in mongodb
					}
				}
			}else{
				Logger.warn("Not using mongodb cache."); // mongodb is offline
			}
		}catch(RuntimeException e){
			e.printStackTrace();
			MongoConfig.setOnline(false);
		}
	}

	public void updateDistributedSession(UserSession userSession) {
		try{
			Date date = new Date();
			if(MongoConfig.isOnline()){
				userSession.setLastRequest(date);
				DS.mop.save(userSession);
			}else{
				Logger.warn("Not using mongodb cache.");
			}
		}catch(RuntimeException e){
			e.printStackTrace();
			MongoConfig.setOnline(false);
		}
	}

}
