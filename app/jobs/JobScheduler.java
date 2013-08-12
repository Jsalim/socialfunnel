package jobs;

import java.util.concurrent.TimeUnit;

import akka.actor.Cancellable;

import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

public class JobScheduler {

	private static JobScheduler instance = null;

	private Cancellable testJob;

	private JobScheduler(){}

	public static JobScheduler getInstance(){
		if (instance == null){
			instance = new JobScheduler();
		}
		return instance;
	}

	public Cancellable startTestJob(){
		if(this.testJob == null){
			this.testJob = Akka.system().scheduler().schedule(
				Duration.create(0, TimeUnit.MILLISECONDS),   // initial delay 
				Duration.create(10, TimeUnit.SECONDS),        // run job every 5 minutes
				new Runnable(){
					public void run(){
						Logger.info("TestJob runs every\n\n");
					}
				}, 
				Akka.system().dispatcher()
			);
		}
		return this.testJob; 
	}

}
