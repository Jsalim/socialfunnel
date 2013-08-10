package bootstrap;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.SSLSocketFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoConfig {



	public class MongoFactory {
		public Mongo buildMongo (String replicaSet, boolean slaveOk, int writeNumber , int connectionsPerHost, boolean useSSL) throws UnknownHostException{

			ServerAddress addr = new ServerAddress();
			List<ServerAddress> addresses = new ArrayList<ServerAddress>();
			int port =0;
			String host = new String();
			if ( replicaSet == null )
				throw new UnknownHostException("Please provide hostname");
			replicaSet = replicaSet.trim();
			if ( replicaSet.length() == 0 )
				throw new UnknownHostException("Please provide hostname");
			StringTokenizer tokens = new StringTokenizer(replicaSet, ",");
			while(tokens.hasMoreTokens()){
				String token = tokens.nextToken();
				int idx = token.indexOf( ":" );
				if ( idx > 0 ){               
					port = Integer.parseInt( token.substring( idx + 1 ) );
					host = token.substring( 0 , idx ).trim();
				}
				addr = new ServerAddress(host.trim(), port);
				addresses.add(addr);
			}

		//  mongo options
			MongoClientOptions.Builder mongoOptions = MongoClientOptions.builder();
			if (useSSL){
				 mongoOptions.socketFactory(SSLSocketFactory.getDefault());
			}
			
			WriteConcern writeConcern = new WriteConcern(1, 0, false, false);
			
			mongoOptions.connectionsPerHost(connectionsPerHost);
			mongoOptions.writeConcern(writeConcern);
			mongoOptions.maxWaitTime(5000);
			mongoOptions.connectTimeout(5000);
			mongoOptions.autoConnectRetry(true);
			mongoOptions.socketKeepAlive(true);

			Mongo mongo = new MongoClient(addresses, mongoOptions.build());
			if(slaveOk){
				mongo.setReadPreference(ReadPreference.SECONDARY);
			}
			return mongo;
		}
	}
}
