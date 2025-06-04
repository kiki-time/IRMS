package com.mysite.irms.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileCleaner {
	private final Path root = Paths.get("uploads");
	
	@Scheduled(cron = "0 0 3 * * ?")
	public void cleanOldFiles() throws IOException{
		if(!Files.exists(root)) {
			return;
		}
		
		Files.walk(root)
			.filter(Files::isRegularFile)
			.forEach(file -> {
				try {
					FileTime lastModified = Files.getLastModifiedTime(file);
					Instant fileTime = lastModified.toInstant();
					if(fileTime.isBefore(Instant.now().minus(7, ChronoUnit.DAYS))) {
						Files.delete(file);
						log.info("자동 삭제됨: {}", file);
					}
				}catch(IOException e) {
					log.warn("파일 삭제 실패: {}", file, e);
				}
			});
	}
}
