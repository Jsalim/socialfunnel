package bootstrap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

import play.Logger;

import com.mongodb.MongoException;

/*Data Store configuration*/
public class DS {

	public static MongoOperations mop = null;

	public static void initStore(){
		ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfig.class);
		mop = (MongoOperations)ctx.getBean("mongoTemplate");
		
		// try to connect to server. If using replicas and connection fails print the exception stack and exit application
		Logger.info("MongoDB servers: " + MongoConfig.testCon() );
	}

}
