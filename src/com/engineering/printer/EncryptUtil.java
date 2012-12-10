package com.engineering.printer;

public class EncryptUtil {
	
private static final char KEY = 1337;
	
	public static String encryptPassword(String pw) {
		char letters[] = pw.toCharArray();
		for(int i = 0; i < letters.length; i++) letters[i] = (char)(KEY ^ pw.charAt(letters.length - i - 1));
		return new String(letters);
	}
	
	public static String decryptPassword(String pw) {
		char letters[] = pw.toCharArray();
		for(int i = 0; i < letters.length; i++) letters[i] = (char)(KEY ^ pw.charAt(letters.length - i - 1));
		return new String(letters);
	}
}
