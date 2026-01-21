package com.example.geco;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GecoApplication {

	public static void main(String[] args) {
		loadDotenv();
		// debug: print GLTFPACK value loaded from .env (if any)
		String gltf = System.getProperty("GLTFPACK");
		if (gltf != null) {
			System.out.println("GLTFPACK (from .env or env): " + gltf);
		} else {
			System.out.println("GLTFPACK not set in environment or .env");
		}
		SpringApplication.run(GecoApplication.class, args);
	}

	private static void loadDotenv() {
		try {
			Path envPath = Paths.get(System.getProperty("user.dir"), ".env");
			if (!Files.exists(envPath)) return;
			try (Stream<String> lines = Files.lines(envPath, StandardCharsets.UTF_8)) {
				lines.map(String::trim)
					 .filter(l -> !l.isEmpty() && !l.startsWith("#"))
					 .forEach(line -> {
						 int idx = line.indexOf('=');
						 if (idx <= 0) return;
						 String key = line.substring(0, idx).trim();
						 String val = line.substring(idx + 1).trim();
						 if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
							 val = val.substring(1, val.length() - 1);
						 }
						 // only set as system property if not already present as env var or property
						 if (System.getProperty(key) == null && System.getenv(key) == null) {
							 System.setProperty(key, val);
						 }
					 });
			}
		} catch (IOException e) {
			System.err.println("Failed to load .env: " + e.getMessage());
		}
	}

}
