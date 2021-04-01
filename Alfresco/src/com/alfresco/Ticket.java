package com.alfresco;

import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.chemistry.opencmis.commons.impl.json.JSONValue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Class TestApi.
 */
public class Ticket {

	// static String urlName
	// ="http://localhost:8080/alfresco/api/-default-/public/authentication/versions/1/tickets";

	public static String tkt;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException
	 */
	public static String getticket() throws IOException {

		// String sql = "select version()";
		// System.out.println(getDataFromDB(sql));
		//

		/*
		 * try { if (args.length != 1) {
		 * System.out.println("Invalid parameters, eg: TestApi  beats.txt"); return; }
		 */

		String input = readFromFile("credentials");
		HttpURLConnection conn = getConnection();
		if (conn == null) {
			System.out.println("Not able to get URL connection");
			return "";
		}
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		int status = conn.getResponseCode();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;

		

		if (status != Constants.Error_code) {

			System.out.println("Response: ");
			System.out.println("Error code: " + status);

			System.out.println("Response: ");
			while ((output = br.readLine()) != null) {
				System.out.println(output);

			}

		} else {

			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject json = (JSONObject) JSONValue.parse(sb.toString());
			JSONObject json1 = (JSONObject) (json.get("entry"));

			// System.out.println(json1.get("id"));
			tkt = (String) json1.get("id");

			/*
			 * System.out.println("Output from Server .... \n"); //while ((output =
			 * br.readLine()) != null) { System.out.println(sb.toString()); //}
			 */
			conn.disconnect();
		}
		return tkt;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	private static HttpURLConnection getConnection() {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(Constants.URL_NAME);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setReadTimeout(60000);
			conn.setConnectTimeout(60000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Read from file.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	private static String readFromFile(String fileName) {

		InputStream is;
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader buf = null;

		try {
			is = new FileInputStream(fileName);

			buf = new BufferedReader(new InputStreamReader(is));
			line = buf.readLine();

			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buf != null)
				try {
					buf.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		return sb.toString();
	}

}
