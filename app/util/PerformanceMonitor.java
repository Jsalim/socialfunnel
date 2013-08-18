package util;
import java.io.File;
import java.lang.management.*;

public class PerformanceMonitor { 

	private static Runtime runtime = Runtime.getRuntime();

	public static String uptime(){
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long uptime = rb.getUptime();
		return "uptime: " + uptime / 1000 / 60 + " min";
	}

	public static String processLoad(){
		return "process load: " + ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getProcessCpuLoad() * 100 + "%";
	}

	public static String availableProcessors(){
		return "available processors: "+ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getAvailableProcessors() + "";
	}

	public static String systemLoad(){
		return "system load: " + ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getSystemCpuLoad() * 100 + "%";
	}

	public static String maxMemory(){
		long maxMemory = runtime.maxMemory();
		return "max memory: " + maxMemory / 1024 / 1024 + " MB";
	}

	public static String allocatedMemory(){
		long allocatedMemory = runtime.totalMemory();
		return "total memory:" + allocatedMemory / 1024 / 1024 + " MB";
	}

	public static String freeMemory(){
		long allocatedMemory = runtime.freeMemory();
		return "free memory: " + allocatedMemory / 1024 / 1024 + " MB";
	}

	public static String totalFreeMemory(){
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		return "total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024 / 1024 + " MB";
	}

	public static String totalSpace(){
		File[] roots = File.listRoots();
		if (roots.length > 0){
			return "total space: " + roots[0].getTotalSpace() / 1024 / 1024 + " MB";
		}else{
			return "No root =(";
		}
	}

	public static String freeSpace(){
		File[] roots = File.listRoots();
		if (roots.length > 0){
			return "free space: " + roots[0].getFreeSpace() / 1024 / 1024 + " MB";
		}else{
			return "No root =(";
		}
	}

	public static String usableSpace(){
		File[] roots = File.listRoots();
		if (roots.length > 0){
			return "unusable space: " + roots[0].getUsableSpace() / 1024 / 1024 + " MB";
		}else{
			return "No root =(";
		}
	}
}