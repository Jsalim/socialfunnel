package constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds all static constants and configuration properties as well as the log wrapper for printing logs
 * when the method start is called this object will try to load all properties from the /resources/application.conf file.
 * It is a final class because it should no be extended.
 * </br>
 * </br> 
 * <b>THIS CLASS SHOULD BE THE FIRST OBJECT TO BE INSTANTIATED IN THE APPLICATION STARTUP STACK IN {@link Application}</b>
 * @see /resources/application.conf file 
 * */
public final class GlobalConfig {

	// ---- data store configuration
	/** the default data store port 27017
	 * @see {@link DBConfig} */
	public static final String DS_DEFAULT_PORT = "27017";
	/** the default data store database 
	 * @see {@link DBConfig} */
	public static final String DS_DEFAULT_DBNAME = "serp_db";
	/** data store auto connect retry = true */
	public static final boolean DS_AUTO_CONNECT_RETRY = true;
	/** data store connections per host 10 */
	public static final int DS_CONNECTIONS_PER_HOST = 5;
	/** sets an auto connect retry time of 15 milliseconds*/
	public static final long DS_MAX_AUTO_CONNECT_RETRY_TIME = 15l * 1000;
	/** tells driver to wait form mongo to acknowledge the writes */
	public static final boolean DS_FSYNC = false;
	/** tells the server to continue writing a batch operation even if an error occours */
	public static final boolean DS_CONTINUE_ON_ERROR = true;
	// ---- end of data store configuration
	/** The default pool to monitor the cluster
	 * @see <a href="http://www.mongodb.org/display/DOCS/Notes+on+Pooling+for+Mongo+Drivers">Notes on Pooling for Mongo Drivers<a> 
	 * */
	public static final long DEFAULT_CLUSTER_POOL_TIME = 1l * 1000;
	/**  maximum time a machine can hold a lock before been overwritten */
	public static final long DEFAULT_CLUSTER_LOCK_TIMEOUT = 1l * 1000;
	// ---- end of cluster poll setting
	
}
