package mbi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Helpers {
	
	private static final String fileName = "log.txt";
	
	private static void appendToLogFile(String message) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/" +  fileName, true)));
	    out.println(message);
	    out.close();
	}
	
	public static void log(String description, String elapsedTime) throws Exception {
		String message = description + ": " + elapsedTime;
		appendToLogFile(message);
		System.out.println(message);
	}
	
	public static void log(String message) throws IOException {
		appendToLogFile(message);
		System.out.println(message);
	}
}
