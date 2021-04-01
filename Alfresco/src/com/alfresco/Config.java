package com.alfresco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {
	
	
	String map(String key) throws IOException {

		String filePath = "config";
		HashMap<String, String> map = new HashMap<String, String>();

		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {

					map.put(prepareValue(parts[0]), prepareValue(parts[1]));
					// map.put(parts[0], parts[1]);

				} else {
					// System.out.println("Invalid line: " + line);
				}
			}
		}
		/**
		 * s Host domain and port with a trailing slash
		 */


		/**
		 * Username of a user with write access to the FOLDER_PATH
		 */
		switch (key) {

		case "USER_NAME":
			return map.get("USER_NAME");

		/**
		 * Password of a user with write access to the FOLDER_PATH
		 */

		case "PASSWORD":
			return map.get("PASSWORD");

		case "FOLDER_PATH":
			return map.get("FOLDER_PATH");

		case "path":
			return map.get("path");

		case "DOWNLOAD_LINK":
			return map.get("DOWNLOAD_LINK");

		case "ALFRESCO_API_URL":
			return map.get("ALFRESCO_API_URL");

		case "CONTENT_TYPE":
			return map.get("CONTENT_TYPE");

		default:
			return "Invalid parameter";

		}

	}

	private static String prepareValue(String a) {

		if (a == null)
			return "";

		return a.trim();
	}
}
