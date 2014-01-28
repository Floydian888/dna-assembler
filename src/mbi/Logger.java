package mbi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static boolean logOnlyLongestCommonSubstringLength = true;
	private static String pathToFile;

	public static void InitializeFile(String organismName, String type, String length, String graphDegreeCustomization,
			boolean logOnlyLongest) {
		
		logOnlyLongestCommonSubstringLength = logOnlyLongest;
		
		String onlyChartData = logOnlyLongestCommonSubstringLength ? "onlyChart" : "";
		pathToFile = "/log/" + organismName + "-" + type + "-" + length + "-" + graphDegreeCustomization + "-" + onlyChartData + ".txt";
		
		File fnew = new File(System.getProperty("user.dir") + "/" + pathToFile);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fnew);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();
	}
	
	private static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd-HH-mm");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private static void appendToLogFile(String message) throws IOException {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(System.getProperty("user.dir") + "/" + pathToFile, true)));
			out.println(message);
			out.close();
	}

	public static void log(String description, String elapsedTime)
			throws Exception {
		if(!logOnlyLongestCommonSubstringLength) {
			String message = description + ": " + elapsedTime;
			appendToLogFile(message);
		}
	}

	public static void log(String message) throws IOException {
		if(!logOnlyLongestCommonSubstringLength) {
			appendToLogFile(message);
		}
	}
	
	public static void logLongestCommonSubstringLength(String length) throws IOException {
		appendToLogFile(length);
	}
}
