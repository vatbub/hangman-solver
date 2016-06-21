package common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javafx.util.converter.ByteStringConverter;

/**
 * All custom internet functions
 * 
 * @author Frederik Kammel
 *
 */
public class Internet {

	/**
	 * Sends an event to the IFTTT Maker Channel. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * 
	 * @param IFTTTMakerChannelApiKey Your Maker API Key. Get your one on <a href="https://ifttt.com/maker">https://ifttt.com/maker</a>
	 * @param eventName The name of the event to trigger.
	 * @throws IOException Should actually never be thrown but occurs if something is wrong with the connection (e. g. not connected)
	 */
	public static String sendEventToIFTTTMakerChannel(String IFTTTMakerChannelApiKey, String eventName) throws IOException {
		return sendEventToIFTTTMakerChannel(IFTTTMakerChannelApiKey, eventName, "");
	}
	
	/**
	 * Sends an event to the IFTTT Maker Channel. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * 
	 * @param IFTTTMakerChannelApiKey Your Maker API Key. Get your one on <a href="https://ifttt.com/maker">https://ifttt.com/maker</a>
	 * @param eventName The name of the event to trigger.
	 * @param Details1 You can send up to three additional fields to the MAker channel which you can use then as IFTTT ingredients. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * @throws IOException Should actually never be thrown but occurs if something is wrong with the connection (e. g. not connected)
	 */
	public static String sendEventToIFTTTMakerChannel(String IFTTTMakerChannelApiKey, String eventName, String Details1) throws IOException {
		return sendEventToIFTTTMakerChannel(IFTTTMakerChannelApiKey, eventName, Details1, "");
	}
	
	/**
	 * Sends an event to the IFTTT Maker Channel. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * 
	 * @param IFTTTMakerChannelApiKey Your Maker API Key. Get your one on <a href="https://ifttt.com/maker">https://ifttt.com/maker</a>
	 * @param eventName The name of the event to trigger.
	 * @param Details1 You can send up to three additional fields to the MAker channel which you can use then as IFTTT ingredients. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * @param Details2 The second additional parameter.
	 * @throws IOException Should actually never be thrown but occurs if something is wrong with the connection (e. g. not connected)
	 */
	public static String sendEventToIFTTTMakerChannel(String IFTTTMakerChannelApiKey, String eventName, String Details1,
			String Details2) throws IOException {
		return sendEventToIFTTTMakerChannel(IFTTTMakerChannelApiKey, eventName, Details1, Details2, "");
	}
	
	/**
	 * Sends an event to the IFTTT Maker Channel. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * 
	 * @param IFTTTMakerChannelApiKey Your Maker API Key. Get your one on <a href="https://ifttt.com/maker">https://ifttt.com/maker</a>
	 * @param eventName The name of the event to trigger.
	 * @param Details1 You can send up to three additional fields to the MAker channel which you can use then as IFTTT ingredients. See <a href="https://maker.ifttt.com/use/">https://maker.ifttt.com/use/</a> for more information.
	 * @param Details2 The second additional parameter.
	 * @param Details3 The third additional parameter.
	 * @throws IOException Should actually never be thrown but occurs if something is wrong with the connection (e. g. not connected)
	 */
	public static String sendEventToIFTTTMakerChannel(String IFTTTMakerChannelApiKey, String eventName, String Details1,
			String Details2, String Details3) throws IOException {
		HttpURLConnection connection = null;
		String response = "";

		URL url;
		try {
			url = new URL("https://maker.ifttt.com/trigger/" + eventName + "/with/key/" + IFTTTMakerChannelApiKey);
			String postData = "{ \"value1\" : \"" + Details1 + "\", \"value2\" : \"" + Details2 + "\", \"value3\" : \""
					+ Details3 + "\" }";
			byte[] postData2 = postData.getBytes(StandardCharsets.UTF_8);
			System.out.println(postData);

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			ByteStringConverter bs = new ByteStringConverter();

			wr.write(postData2);
			connection.connect();

			Reader in;

			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			for (int c; (c = in.read()) >= 0;) {
				response = response + Character.toString((char) c);
			}
			return response;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}
	
	/**
	 * Sends a GET request to url and retreives the server response
	 * @param url The url to call.
	 * @return The server response
	 * @throws IOException No Internet connection
	 */
	public static String webread(URL url) throws IOException {
	      StringBuilder result = new StringBuilder();
	      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	      conn.setRequestMethod("GET");
	      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = rd.readLine()) != null) {
	         result.append(line);
	      }
	      rd.close();
	      return result.toString();
	   }
}
