package services.admin;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import models.Agent;
import models.AgentNotification;
import models.Brand;
import models.Invitation;
import models.PasswordReset;
import models.UserBrandRole;
import models.UserRole;
import models.admin.SysAdmin;

import org.joda.time.DateTime;

import constants.RoleName;
import play.Configuration;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.JPA;
import play.mvc.Http.Session;
import util.AdminSession;
import util.MyUtil;
import util.UserSession;

public class AdminService {

	private static AdminService instance = null;
	
	private AdminService(){}
	
	public static AdminService getInstance(){
		if(instance == null){
			instance = new AdminService();
		}
		return instance;
	}

	 /**
	 * Find the admin by name in the DB.
	 * 
	 * @param username
	 *            the admins's username
	 * @return The admin Model object
	 * */
	public SysAdmin findByUsername(String username) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT u.* FROM SysAdmin u WHERE username = :username", SysAdmin.class);
		query.setParameter("username", username);

		SysAdmin admin = null;
		try {
			admin = (SysAdmin) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}

	/**
	 * Find the admin by name in the DB.
	 * 
	 * @param username
	 *            the admins's username
	 * @return The admin Model object
	 * */
	public SysAdmin findByEmail(String email) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT u.* FROM SysAdmin u WHERE email = :email", SysAdmin.class);
		query.setParameter("email", email);

		SysAdmin admin = null;
		try {
			admin = (SysAdmin) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return admin;
	}

	/**
	 * Save a admin no database
	 * 
	 * @param admin
	 *            The admin Object built by binding the HTTP action parameters
	 *            from the play framework
	 * @return true if the admin is persisted or false if JPA EM throws an
	 *         exception.
	 * */
	public boolean create(SysAdmin admin) {
		try {
			JPA.em().persist(admin);
		} catch (PersistenceException e) {
			return false;
		}
		return true;
	}

	/**
	 * Save a admin no database
	 * 
	 * @param admin
	 *            The admin Object built by binding the HTTP action parameters
	 *            from the play framework
	 * @return true if the admin is persisted or false if JPA EM throws an
	 *         exception.
	 * */
	public boolean delete(SysAdmin admin) {
		try {
			JPA.em().remove(admin);
		} catch (PersistenceException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the admin exists on database.
	 * 
	 * @param usernameOrEmail
	 *            String representing a username or email
	 * @return true if there is at least 1 username or email is persisted
	 * */
	public boolean exists(String usernameOrEmail) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT COUNT(id) as count FROM SysAdmin u WHERE username = :uoe OR email = :uoe");
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
	 * Get existing admin session or creates a new one if doesn't exist
	 * 
	 * @param session
	 * @return the admin session
	 * @throws NoUUIDException
	 */
	public AdminSession getAdminSession(Session session) {
		String uuid = session.get("uuid2");
		// if the session object has no uuid, create one
		if (uuid == null) {
			uuid = java.util.UUID.randomUUID().toString();
			session.put("uuid2", uuid);
		}
		// try to find a admin session on cache
		AdminSession adminSession = (AdminSession) Cache.get(uuid + "_admin");
		// if session is not found create
		if (adminSession == null) {
			adminSession = createNewAdminSession(uuid);
		}
		return adminSession;
	}

	/**
	 * Update existing admin session.
	 * <br>
	 * RECOMMENDATION: THIS METHOD SHOULD BE USE WITH CAUTION. DB ACCESS OVERHEAD ALERT!! 
	 * TRY USING THIS METHOD ONLY WHEN NECESSERY (EX. ON TOTAL PAGE RELOAD, ... {@link Dashboard} ACTIONS
	 * 
	 * @param adminSession
	 *            the admin session object to be udpate.
	 * @return the updated admin session object
	 * */
	public AdminSession updateExistingAdminSession(AdminSession adminSession) {
		Cache.set(adminSession.getUUID() + "_admin", adminSession, Play.application().configuration().getInt("dasboard.cache.expiration"));
		return (AdminSession) Cache.get(adminSession.getUUID() + "_admin");
	}

	/**
	 * Create a new admin session
	 * 
	 * @param uuid
	 * @return the admin session that was just created
	 * */
	public AdminSession createNewAdminSession(String uuid) {
		AdminSession adminSession = new AdminSession();
		adminSession.setUUID(uuid);
		Cache.set(adminSession.getUUID() + "_admin", adminSession, Play.application().configuration().getInt("dasboard.cache.expiration"));
		Logger.debug("AdminSession.createNewAdminSession: New admin session chached in memory for " + Configuration.root().getInt("dasboard.cache.expiration")
				+ "s - " + uuid);
		return adminSession;
	}

	public void updateLastLogin(AdminSession adminSession) {
		adminSession.getUser().setLastLogin(new Date());
	}

	public void incrementLoginCounter(AdminSession adminSession) {
		adminSession.getUser().setLoginCounter(adminSession.getUser().getLoginCounter() + 1);

	}

	public SysAdmin findById(Long id) {
		return JPA.em().find(SysAdmin.class, id);
	}

//	public boolean createResetPassword(String email) {
//		SysAdmin user = findByEmail(email);
//
//		try {
//			if (user != null) {
//				Query query = JPA.em().createNativeQuery("SELECT pr.* FROM PasswordReset pr WHERE pr.user_id = :id ", PasswordReset.class);
//				query.setParameter("id", user.getId());
//				PasswordReset passwordReset;
//				try {
//					passwordReset = (PasswordReset) query.getSingleResult();
//					passwordReset.setExpires(DateTime.now().plusDays(2).toDate());
//				} catch (NoResultException e) {
//					passwordReset = new PasswordReset(email, user, MyUtil.stringToMD5(email + new Date() + new Random().nextInt(Integer.MAX_VALUE)));
//				}
//
//				//				emailService.resetEmail(passwordReset);
//
//				JPA.em().persist(passwordReset);
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

//	public boolean checkResetPassword(String hash) {
//		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
//		query.setParameter("hash", hash);
//		List<PasswordReset> list = null;
//		try {
//			list = query.getResultList();
//		} catch (NoResultException e) {
//			return false;
//		}
//		if (list != null && list.size() == 1) {
//			Logger.debug(list.get(0).getUser().getName());
//			return true;
//		} else {
//			return false;
//		}
//	}

//	public PasswordReset getResetPassword(String hash) {
//		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
//		query.setParameter("hash", hash);
//		List<PasswordReset> list = null;
//		try {
//			list = query.getResultList();
//		} catch (NoResultException e) {
//			return null;
//		}
//		if (list != null && list.size() == 1) {
//			return list.get(0);
//		} else {
//			return null;
//		}
//	}

//	public boolean resetPassword(String hash, String password) {
//		Query query = JPA.em().createQuery("SELECT p FROM PasswordReset p WHERE p.hash = :hash ");
//		query.setParameter("hash", hash);
//		List<PasswordReset> list = null;
//		try {
//			list = query.getResultList();
//		} catch (NoResultException e) {
//			return false;
//		}
//		if (list != null && list.size() == 1) {
//			Agent user = list.get(0).getUser();
//			user.setPassword(password);
//			JPA.em().persist(user);
//			return true;
//		} else {
//			return false;
//		}
//	}

}
