package mbi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

	private static boolean doLog = true;
	private static final String pathToFile = "/log/log-michal-phiX174.txt";

	static {
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
