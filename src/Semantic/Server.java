package Semantic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/*
 * 
 * Semantic Interoperability in oneM2M Architecture 
 * 2018-04-10
 * By cclab
 * 
 * Work based on oneM2M_ontology.owl
 * RDF Model : Jena Framework
 * Database : Fuseki
 * 
 */

public class Server {
	static Info info = new Info();
	public static void main(String[] args) {

		/* original */
		String ip;
		Scanner scan = new Scanner(System.in);
		System.out.print("Please enter the cse ip: ");
//		ip = scan.next();
		ip = "128.134.65.120";
		info.CSE(ip);
		info.FUSEKI(ip);
		System.out.println("mobius ip: [" + info.cseip + "]");
		System.out.println("Fuseki ip: [" + info.fusekiip + "]");
		System.out.print("Please enter the MQTT broker ip: ");
//		ip = scan.next();
		ip = "128.134.65.120";
		info.BROKER(ip);
		System.out.println("MQTT broker ip: [" + info.brokerip + "]");
		    	
    	// step 1 . connect to cse or other server
    	String BOOL ="";
    	try {
    		BOOL = sendGet(info.cseip,info.cseport,info.csename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(BOOL.equals(" ") || BOOL.equals("failure")) {
    		System.err.println("[WARN] Connect to Server Failure");
    	}
    	else {
    		System.out.println("[INFO] Connect to Server Success");
        	RDFModeling.onload(); // CSE database scan and make RDF resource structure
        	Monitoring.runClient(); // CSE database Monitoring
    	}

    }
	private static String sendGet(String ip,String port,String csename) throws Exception {
		
		String url = "http://"+ip+":"+port+"/"+csename;
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("X-M2M-Origin", "SOrigin");
		con.setRequestProperty("X-M2M-RI", "12345");
		con.setRequestProperty("Accept", "application/json");
		
		int responseCode = con.getResponseCode();
		System.out.println("[INFO] Sending 'GET' request to URL : " + url);
		System.out.println("[INFO] Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		if(responseCode < 210) {
			return "connect";
		}
		else {
			return "failure";
		}
	}
	
}
