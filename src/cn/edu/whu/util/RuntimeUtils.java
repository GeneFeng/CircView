package cn.edu.whu.util;

public class RuntimeUtils {
	public static long getAvailableMemory() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		return freeMemory + (maxMemory - allocatedMemory);
	}
}
