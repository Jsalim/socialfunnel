package services;

import interceptors.DefaultInterceptor;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.format.DateTimeFormat;

import constants.FilterTypes;
import constants.Permissions;
import constants.RoleName;
import exceptions.InvalidParameterException;

import play.data.Form;
import play.data.validation.ValidationError;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import util.MyUtil;

import models.AccountKey;
import models.App;
import models.Brand;
import models.FacebookAccountInfo;
import models.Invitation;
import models.StreamFilter;
import models.TwitterAccountInfo;
import models.Agent;
import models.UserBrandRole;
import models.UserRole;

public class BrandService {

	private static BrandService instance;

	private static final UserService userService = UserService.getInstance();
	//	private static final EmailService emailService = EmailService.getInstance(); 
	private final HashSet<String> brandSubdomains = new HashSet<String>();

	public BrandService() {}

	public static BrandService getInstance(){
		if(instance == null){
			instance = new BrandService();
		}
		return instance;
	}

	public void addBrandSubdomains(HashSet<String> brandAddresses){
		brandSubdomains.addAll(brandAddresses);
	}

	/**
	 * This method should only be called by {@link DefaultInterceptor}
	 * @see {@link DefaultInterceptor}
	 * */
	public boolean checkBrandSubdomain (String subDomain){
		EntityManager em = JPA.em();
		if (subDomain != null) {
			if(subDomain.equals("test")){
				brandSubdomains.add(subDomain);
				return true;
			}
			if(brandSubdomains.contains(subDomain)){
				return true;
			}else{

				Query query = em.createNativeQuery("SELECT b.* FROM Brand b WHERE nameAddress = :nameAddress AND b.active = :bool", Brand.class);
				query.setParameter("nameAddress", subDomain);
				query.setParameter("bool", true);

				Brand brand = null;
				try {
					brand = (Brand) query.getSingleResult();
				}catch(Exception e){
					Logger.error("Cant findo a brand: " + e.getMessage());
				}
				
				if(brand != null){ //TODO implement validation for forum and knowledge base apps active  
					Logger.debug("adding new brand: " + subDomain);
					brandSubdomains.add(subDomain);
					return true;
				}
			}
		}
		return false;
	}

	public Brand findById(Long id) {
		return JPA.em().find(Brand.class, id);
	}


	public Brand createBrand(String myBrandName, String myBrandDescirption, String myNameAddress, Agent owner, Set<FacebookAccountInfo> facebookAccounts, Set<TwitterAccountInfo> twitterAccounts, Set<Invitation> invitations){

		Brand brand = new Brand();
		brand.setName(myBrandName);
		brand.setDescription(myBrandDescirption);
		brand.setNameAddress(myNameAddress);
		brand.setOwner(owner);

		Set<FacebookAccountInfo> fbaAccounts = new HashSet<FacebookAccountInfo>();
		Set<TwitterAccountInfo> twAccounts = new HashSet<TwitterAccountInfo>();

		// remove accounts that are already in db from list, retrieve it from the database and add the managed instance back on the list
		if(facebookAccounts != null){
			for(FacebookAccountInfo accountInfo: facebookAccounts){
				FacebookAccountInfo facebookAccountInfo = JPA.em().find(FacebookAccountInfo.class, new AccountKey(accountInfo.getId(), accountInfo.getOauthToken()));
				if(facebookAccountInfo != null){
					fbaAccounts.add(facebookAccountInfo);
				}else{
					fbaAccounts.add(accountInfo);
				}
			}
		}
		// remove accounts that are already in db from list, retrieve it from the database and add the managed instance back on the list
		if(twitterAccounts != null){
			for(TwitterAccountInfo accountInfo: twitterAccounts){
				TwitterAccountInfo twitterAccountInfo = JPA.em().find(TwitterAccountInfo.class, new AccountKey(accountInfo.getId(), accountInfo.getOauthToken()));
				if(twitterAccountInfo != null){
					twAccounts.add(twitterAccountInfo);
				}else{
					twAccounts.add(accountInfo);
				}
			}
		}

		brand.setFacebookAccounts(fbaAccounts);
		brand.setTwitterAccounts(twAccounts);

		UserBrandRole userBrandRole = new UserBrandRole();
		userBrandRole.brand = brand;
		userBrandRole.user = owner;
		userBrandRole.role = UserRole.findByName(RoleName.ADMIN);

		Set<UserBrandRole> userBrandRoles = new HashSet<UserBrandRole>();
		userBrandRoles.add(userBrandRole);

		brand.setUserBrandRoles(userBrandRoles);
		
		// add default apps
		
		App formbuilder = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "formbuilder").getSingleResult();
		App knoledgeBase = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "knoledgeBase").getSingleResult();
		App socialnetworks = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "socialnetworks").getSingleResult();
		App helpDesk = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "helpDesk").getSingleResult();
		App report = (App) JPA.em().createNativeQuery("SELECT a.* from App a where a.name like :name", App.class).setParameter("name", "report").getSingleResult();

		brand.getApps().add(formbuilder);
		brand.getApps().add(knoledgeBase);
		brand.getApps().add(socialnetworks);
		brand.getApps().add(helpDesk);
		brand.getApps().add(report);
		
		JPA.em().persist(brand);

		addOrInviteUserToBrand(invitations, brand);

		return brand;
	}

	public Form<Brand> validateBrandForm(String brandName, String brandAddress, String brandPhone){

		Form<Brand> brandForm = new Form<Brand>(Brand.class);

		if(brandForm != null){
			if(brandName == null){
				brandForm.errors().put("brandname", Arrays.asList(new ValidationError("brandname", "Nome inválido")));
			}else if(brandName.length() < 2 || brandName.length() > 50){
				brandForm.errors().put("brandname", Arrays.asList(new ValidationError("brandname", "Nome deve conter 2 a 50 caracteres.")));
			}

			if(brandAddress == null || MyUtil.isHost(brandAddress) == false){
				brandForm.errors().put("brandaddress", Arrays.asList(new ValidationError("brandaddress", "Subdomínio inválido.")));
			}else if(!isNameAddressAvailable(brandAddress)){
				brandForm.errors().put("brandaddress", Arrays.asList(new ValidationError("brandaddress", "Subdomínio já está em uso.")));
			}

			if(brandPhone == null){
				brandForm.errors().put("brandphone", Arrays.asList(new ValidationError("brandphone", "Telefone inválido.")));
			}else if(brandPhone.length() < 8 || brandPhone.length() > 20){
				brandForm.errors().put("brandphone", Arrays.asList(new ValidationError("brandphone", "Telefone deve conter 8 a 20 caracteres.")));
			}

			return brandForm;
		}else {
			return null;
		}
	}

	private void addOrInviteUserToBrand(Set<Invitation> invitations, Brand brand){
		//		try {
		//			if(invitations != null)
		//				for (Invitation invitation: invitations){
		//					invitation.setBrand(brand);
		//					invitation.setHash(MyUtil.stringToMD5(invitation.getEmail() + new Date().getTime() + new Random().nextInt(Integer.MAX_VALUE)));
		//	
		//					JPA.em().persist(invitation);
		//				}
		//			
		//				emailService.batchInvitationEmails(invitations);
		//			}
		//		} catch (NoSuchAlgorithmException e) {
		//			e.printStackTrace();
		//		}
	}

	//Available
	public boolean isNameAddressAvailable(String input){

		if(input != null){
			input = input.trim().toLowerCase();

			Query query = JPA.em().createQuery("SELECT count(*) FROM Brand b WHERE nameAddress = :input");
			query.setParameter("input", input);

			Long count = 0l;
			try {
				count = (Long) query.getSingleResult();
				if(count == 0){
					//				Logger.debug(input + " " + count);
					return true;
				}
			} catch (NoResultException e) {
				Logger.debug(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}else{
			return false;
		}
	}
	
	public Brand getBrandByNameAddress(String address) {
		EntityManager em = JPA.em();
		
		if(em.getTransaction().isActive()){
			em.getTransaction().begin();
		}

		Query query = em.createNativeQuery("SELECT b.* FROM Brand b WHERE nameAddress = :nameAddress AND b.active = :bool", Brand.class);
		query.setParameter("nameAddress", address);
		query.setParameter("bool", true);

		Brand brand = null;
		try {
			brand = (Brand) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brand;
	}

	public List<Brand> getUserBrands(Agent user) {

		Query query = JPA.em().createQuery("SELECT b FROM UserBrandRole ubr JOIN ubr.user u JOIN ubr.brand b WHERE u.id = :user AND ubr.active = :bool ORDER BY b.createdAt desc");
		query.setParameter("user", user.getId());
		query.setParameter("bool", true);
		List<Brand> brands;
		try {
			brands = (List<Brand>) query.getResultList();

			return brands;
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean deleteBrand(Long brandId) {

		try {

			Query query = JPA.em().createNativeQuery("UPDATE UserBrandRole ubr set ubr.active = :bool WHERE brand_id = :brand", UserBrandRole.class);
			query.setParameter("brand", brandId);
			query.setParameter("bool", false);
			query.executeUpdate();

			Query query2 = JPA.em().createNativeQuery("UPDATE Invitation i set i.active = :bool WHERE brand_id = :brand", Invitation.class);
			query2.setParameter("brand", brandId);
			query2.setParameter("bool", false);
			query2.executeUpdate();

			Brand brand = JPA.em().find(Brand.class, brandId);
			brand.setActive(false);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Brand findByNameAddress(String nameAddress) {
		EntityManager em = JPA.em();

		Query query = em.createNativeQuery("SELECT b.* FROM Brand b WHERE nameAddress = :nameAddress", Brand.class);
		query.setParameter("nameAddress", nameAddress);

		Brand brand = null;
		try {
			brand = (Brand) query.getSingleResult();
		} catch (NoResultException e) {
			Logger.debug(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return brand;
	}

	public boolean checkUserPermission(Agent user, Brand brand, Permissions perm) {
		boolean r = false;


		return r;
	}

	public StreamFilter createFilter(Brand brand, String paramBeginning, String paramEnding, 
			String paramFilterType, String paramSearchExpression, String paramFilterName, 
			String paramJsonProfiles, String paramJsonMessages, 
			String paramJsonChannels, String paramJsonTerms) throws InvalidParameterException{

		ArrayList<String> errors = new ArrayList<String>();

		brand = JPA.em().find(Brand.class, brand.getId());

		StreamFilter filter = new StreamFilter();

		// check parameters
		if( paramBeginning == null || 
				paramEnding == null ||
				paramFilterType == null
				){
			String errorParameters = "[" + 
					(paramBeginning == null ? "beginning, " : "") + 
					(paramEnding == null ? "ending, " : "") + 
					(paramFilterType == null ? "filterType, " : "") + "]";
			errors.add("Parametro(s) inválidos: " + errorParameters);
		}else{
			try{
				filter.setBeginning(DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(paramBeginning).toDate());
				filter.setEnding(DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(paramEnding).toDate());
			}catch(Exception e){
				errors.add("Data inválida");
			}

			if(paramFilterName == null || paramFilterName.length() < 2){
				errors.add("Parametro(s) inválidos. " + "paramFilterName");
			}else{
				filter.setFilterName(paramFilterName);
			}

			ObjectMapper mapper = new ObjectMapper();

			try{
				filter.setSearchExpression(paramSearchExpression);
				if(paramJsonChannels != null){
					filter.setJsonChannels(mapper.readTree(paramJsonChannels).toString());
				}
				if(paramJsonMessages != null){
					filter.setJsonMessageTypes(mapper.readTree(paramJsonMessages).toString());
				}
				if(paramJsonProfiles != null){
					filter.setJsonProfiles(mapper.readTree(paramJsonProfiles).toString());
				}
				if(paramJsonTerms != null){
					filter.setJsonTerms(mapper.readTree(paramJsonTerms).toString());
				}
			}catch (Exception e){
				errors.add("Parametro(s) inválidos: Erro na leitura dos filtros");
			}

			if(paramFilterType.equals(FilterTypes.PROFILE.toString())){
				filter.setFilterType(FilterTypes.PROFILE);
			}else if(paramFilterType.equals(FilterTypes.PUBLIC.toString())){
				filter.setFilterType(FilterTypes.PUBLIC);
			}else{
				errors.add("Parametro(s) inválidos: Tipo de filtro não encontrado");
			}

		}//end of parameter validation

		if(errors.size() > 0){
			throw new InvalidParameterException(Json.toJson(errors).toString());
		}else{

			filter.setBrand(brand);
			brand.getStreamFilters().add(filter);
			JPA.em().persist(brand);

			return filter;
		}
	}
}
