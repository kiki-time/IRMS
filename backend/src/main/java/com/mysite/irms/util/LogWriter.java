package com.mysite.irms.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogWriter {
	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
	public static void writeCreateLog(String user, String original, String zipName, boolean ok) {
		write(user, "CREATE", original +" →"+zipName, ok);
	}
	public static void writeVerifyLog(String user, String envelope, boolean ok) {
		write(user, "VERIFY", envelope, ok);
	}
	
	private static void write(String user, String action, String detail, boolean ok) {
		try {
			Path logDir = Paths.get("log");
			Files.createDirectories(logDir);
			Path logfile = logDir.resolve(user+".log");
			String time = LocalDateTime.now().format(FMT);
			String ip = "127.0.0.1"; // HttpServletRequest로 받아서 기록하도록 수정
			String entry = String.format("%s | %s | %s | %-6s | %s%n",
					time, ip, user, action, detail + (ok ? " | OK" : " | FAIL"));
			Files.write(logfile,  entry.getBytes(StandardCharsets.UTF_8), 
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
