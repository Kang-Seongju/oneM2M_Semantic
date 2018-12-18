package Semantic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


/*
 * 
 * oneM2M Message Generator
 * 2018-04-12
 * By cclab
 * 
 * non-oneM2M Request msg -> oneM2M msg
 * 
 */


public class oneM2MTranslation {
	static Info info = new Info();
	public static void oneM2MTranslate(String device_uri, String device_function, String value) {
		System.out.println("[INFO] µé¾î¿È oneM2MTranslate.java");
		try {	
			String requestBody = 			 
					"<m2m:cin\n" +
							"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
							"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
							"<con>"+value+"</con>\n"+
					"</m2m:cin>";
			
			StringEntity entity = new StringEntity(
					new String(requestBody.getBytes()));
	
			URI uri = new URIBuilder()
					.setScheme("http")
					.setHost(info.cseip + ":" + info.cseport)
					.setPath(device_function)
					.build();
			
			HttpPost post = new HttpPost(uri);
					post.setHeader("Content-Type", "application/vnd.onem2m-res+xml; ty=4");
					post.setHeader("Accept", "application/json");
					post.setHeader("X-M2M-RI", "12345");
					post.setHeader("X-M2M-Origin", "/0.2.481.1.21160310105204806");
					post.setEntity(entity);
				
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			HttpResponse response = httpClient.execute(post);
			
			HttpEntity responseEntity = response.getEntity();
			
			String responseString = EntityUtils.toString(responseEntity);
			
			int responseCode = response.getStatusLine().getStatusCode();
			
			System.out.println("[&CubeThyme] AE create HTTP Response Code : " + responseCode);
			System.out.println("[&CubeThyme] AE create HTTP Response String : " + responseString);
			
			httpClient.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
