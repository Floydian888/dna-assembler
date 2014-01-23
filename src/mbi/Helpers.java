package mbi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Helpers {

	private static boolean doLog = true;
	private static final String pathToFile = "/log/log5.txt";

	private static void appendToLogFile(String message) throws IOException {
		if (doLog) {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(System.getProperty("user.dir") + "/"
							+ pathToFile, true)));
			out.println(message);
			out.close();
		}
	}

	public static void log(String description, String elapsedTime)
			throws Exception {
		if (doLog) {
			String message = description + ": " + elapsedTime;
			appendToLogFile(message);
			//System.out.println(message);
		}
	}

	public static void log(String message) throws IOException {
		if (doLog) {
			appendToLogFile(message);
			//System.out.println(message);
		}
	}
}
