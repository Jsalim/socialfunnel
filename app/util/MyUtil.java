package util;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;

public class MyUtil {

	public static String getShortName(String string, int size){
		try{
			string = string.substring(0, size - 1) + "...";
		}catch (RuntimeException e){
			// this is a good thing
			Logger.info("String is smaller than limit");
			return string;
		}
		return string;
	}

	public static boolean httpResourceExists(String url){
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");

			boolean status = (con.getResponseCode() == HttpURLConnection.HTTP_OK);

			con.disconnect();

			Logger.debug(status + " " + url);

			return status;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String stringToMD5(String string) throws NoSuchAlgorithmException{
		if(string != null){
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				return hex (md.digest(string.getBytes("CP1252")));
			} catch (NoSuchAlgorithmException e) {
				return null;
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}else{
			return null;
		}
	}

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i]
					& 0xFF) | 0x100).substring(1,3));        
		}
		return sb.toString();
	}

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String toSlug(String input) {
		String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.ENGLISH);
	}

	private static final Pattern emailPattern = Pattern.compile("^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

	private static final Pattern fqdnPattern = Pattern.compile("(?=^.{1,254}$)(^(?:(?!\\d+\\.|-)[a-zA-Z0-9_\\-]{1,63}(?<!-)\\.?)+(?:[a-zA-Z]{2,})$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern hostPattern = Pattern.compile("^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$", Pattern.CASE_INSENSITIVE);

	private static final Pattern urlPattern = Pattern.compile("[^(http\\:\\/\\/[a-zA-Z0-9_\\-]+(?:\\.[a-zA-Z0-9_\\-]+)*\\.[a-zA-Z]{2,4}(?:\\/[a-zA-Z0-9_]+)*(?:\\/[a-zA-Z0-9_]+\\.[a-zA-Z]{2,4}(?:\\?[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)?)?(?:\\&[a-zA-Z0-9_]+\\=[a-zA-Z0-9_]+)*)$]", Pattern.CASE_INSENSITIVE);


	public static boolean isInteger(String intVal){
		try { 
			int i = new Integer(intVal);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isBoolean(String boolVal){
		try {
			boolean b = new Boolean(boolVal);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isFloat(String floatVal){
		try { 
			float f = new Float(floatVal);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isLong(String logVal){
		try {
			long l = new Long(logVal);
			return true;
		} catch(Exception e) {
		}
		return false;
	}

	public static boolean isFQDN(String fqdnVal){
		try {
			Matcher matcher = fqdnPattern.matcher(fqdnVal);  
			return matcher.matches();  
		} catch (Exception e){
		}
		return false;


	}

	public static boolean isURL(String url){
		try {
			Matcher matcher = urlPattern.matcher(url);  
			return matcher.matches();  
		} catch (Exception e){
		}
		return false;

	}

	public static boolean isEmailAddr(String emailAddr){
		try {
			Matcher matcher = emailPattern.matcher(emailAddr);  
			return matcher.matches();  
		} catch (Exception e){
		}
		return false;
	}

	public static boolean isHost (String hostname){
		try {
			Matcher matcher = hostPattern.matcher(hostname);  
			return matcher.matches();  
		} catch (Exception e){
		}
		return false;
	}

	public static void main(String args[]){
		try {
			String action = args[0];
			String input = args[1];
			boolean result = false;
			if (action.equals("email")) {
				result = isEmailAddr(input);    
			} else if (action.equals("host")) {
				result = isHost(input);
			} else if(action.equals("url")) {
				result = isURL(input);
			} else if(action.equals("fqdn")) {
				result = isFQDN(input);
			}
			System.out.println("RESULT : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
