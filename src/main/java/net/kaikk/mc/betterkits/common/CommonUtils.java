package net.kaikk.mc.betterkits.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class CommonUtils {
	public static UUID toUUID(byte[] bytes) {
		if (bytes.length != 16) {
			throw new IllegalArgumentException();
		}
		int i = 0;
		long msl = 0;
		for (; i < 8; i++) {
			msl = (msl << 8) | (bytes[i] & 0xFF);
		}
		long lsl = 0;
		for (; i < 16; i++) {
			lsl = (lsl << 8) | (bytes[i] & 0xFF);
		}
		return new UUID(msl, lsl);
	}

	public static String mergeStringArrayFromIndex(String[] arrayString, int i) {
		StringBuilder sb = new StringBuilder();

		for(;i<arrayString.length;i++){
			sb.append(arrayString[i]);
			sb.append(' ');
		}

		if (sb.length()!=0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

	public static short getUtcYear() {
		return (short) Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.YEAR);
	}
	
	public static int epoch() {
		return (int) (System.currentTimeMillis()/1000);
	}
	

	public static int ipv4ToInt(String address) {
		String[] p = address.split("[.]");
	    return (Integer.parseInt(p[0])<<24) | (Integer.parseInt(p[1])<<16) | (Integer.parseInt(p[2])<<8) | (Integer.parseInt(p[3]));
	}

	public static String shortStackTrace() {
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		for(int i = 2; i<5 && i<ste.length; i++) {
			String className;
			try {
				className = Class.forName(ste[i].getClassName()).getSimpleName();
			} catch (ClassNotFoundException e) {
				className = ste[i].getClassName();
			}
			
			sb.append(className + "." + ste[i].getMethodName() +
		            (ste[i].isNativeMethod() ? "(Native Method)" :
		                (ste[i].getFileName() != null && ste[i].getLineNumber() >= 0 ?
		                 "(" + ste[i].getFileName() + ":" + ste[i].getLineNumber() + ")" :
		                 (ste[i].getFileName() != null ?  "("+ste[i].getFileName()+")" : "(Unknown Source)"))));
			sb.append(" <- ");
		}
		return sb.toString();
	}	
	
	public static void extractResource(String resourcePath, File destination, boolean replace) throws IOException {
		if (replace || !destination.exists()) {
			destination.mkdirs();
			Files.copy(getResourceAsStream(resourcePath), destination.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public static InputStream getResourceAsStream(String resourcePath) {
		return CommonUtils.class.getResourceAsStream(resourcePath);
	}
}