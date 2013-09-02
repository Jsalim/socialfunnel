package constants;

/** 
 * Permissions for each app
 * 
 * <strong>LAST 2 DIGITS REPRESENT THE PERMISISON. THE REST REPRESENTS THE APP</strong>
 * 
 * */
public enum Permissions {
	// Admin
	ALL(0),
	
	// read permissions 
	VIEW_USERS(101),
	VIEW_INTERACTIONS(102),
	VIEW_STREAM(103),
	VIEW_MEDIA_CHANNELS(104),
	VIEW_MONITORED_TERMS(105),
	
	// write permissions
	ADD_USER(201),
	ADD_INTERACTION(202),
	ADD_MONITORED_TERM(203),
	ADD_MEDIA_CHANNEL(205),
	ADD_DYNAMIC_FORM(206),
	
	// billing related permissions
	BILLING_PAID(301),
	BILLING_MISSING(302),
	BILLING_ONHOLD(303),
	BILLING_TRIAL(304);
	
	private int id;
	private Permissions(int val) {
		this.id = val;
	}
}
