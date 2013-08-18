package controllers;

import interceptors.DefaultInterceptor;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import util.PerformanceMonitor;

/**
 * This controller holds the main system api and may be further expended
 * */
@With(DefaultInterceptor.class)
public class Api extends Controller{

	public static Result sysInfos(){
		
		String report = "" + 
		PerformanceMonitor.uptime() + "\n" +
		PerformanceMonitor.availableProcessors() + "\n" +
		PerformanceMonitor.processLoad() + "\n" +
		PerformanceMonitor.systemLoad() + "\n" +
		PerformanceMonitor.maxMemory() + "\n" +
		PerformanceMonitor.allocatedMemory() + "\n" +
		PerformanceMonitor.freeMemory() + "\n" +
		PerformanceMonitor.totalFreeMemory() + "\n" +
		PerformanceMonitor.totalSpace() + "\n" +
		PerformanceMonitor.freeSpace() + "\n" +
		PerformanceMonitor.usableSpace() + "\n";
		
		return ok(report);
	}
}
