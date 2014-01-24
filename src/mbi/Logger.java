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

	private boolean doLog = true;
	private String pathToFile;

	public Logger() {
		String currentDate = getCurrentDate();
		pathToFile = "/log/log-michal-porcine" + currentDate + ".txt";
		
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
	
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private void appendToLogFile(String message) throws IOException {
		if (doLog) {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(System.getProperty("user.dir") + "/"
							+ pathToFile, true)));
			out.println(message);
			out.close();
		}
	}

	public void log(String description, String elapsedTime)
			throws Exception {
		if (doLog) {
			String message = description + ": " + elapsedTime;
			appendToLogFile(message);
			//System.out.println(message);
		}
	}

	public void log(String message) throws IOException {
		if (doLog) {
			appendToLogFile(message);
			//System.out.println(message);
		}
	}
}
