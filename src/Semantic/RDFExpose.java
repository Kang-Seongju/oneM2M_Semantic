package Semantic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

/*
 * 
 * RDF Graph Expose module
 * 2018-04-11
 * By cclab
 * 
 */

public class RDFExpose {
	static MqttClient myClient = null;
	static MqttConnectOptions connOpt;
	final static String clientID = "oneM2M_Semantic_Publisher";
	static Server server = new Server();
	static Info info = new Info();
	static String myTopic = "/oneM2M/pub/Semantic/Client/Rules";
	static String performance = "/oneM2M/pub/Performance/Client/Semantic";
	public static void RDFResourceExpose() {
		connOpt = new MqttConnectOptions();
		try {
			myClient = new MqttClient("tcp://"+info.brokerip+":"+info.brokerport, clientID);
			myClient.connect(connOpt);
			if(myClient.isConnected()) {
				System.out.println("[INFO] Connected to " + "tcp://"+info.brokerip+":"+info.brokerport);
				ExposeRDFModel();
			}else {
				System.err.println("[WARN] MQTT Client fail to Connect");
			}
			myClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		
	}
	public static void ExposePerformance(String str) {
		connOpt = new MqttConnectOptions();
		try {
			myClient = new MqttClient("tcp://"+info.brokerip+":"+info.brokerport, clientID);
			myClient.connect(connOpt);
			if(myClient.isConnected()) {
				System.out.println("[INFO] Connected to " + "tcp://"+info.brokerip+":"+info.brokerport);
				MqttMessage msg = new MqttMessage(str.getBytes());
				myClient.publish(performance, msg);
				System.out.println(str);
			}else {
				System.err.println("[WARN] MQTT Client fail to Connect");
			}
			myClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	public static void ExposeRDFModel() {
		Model m = ModelFactory.createDefaultModel();
		DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(info.serviceURI+"get");
		m = accessor.getModel();
		try {
			BufferedWriter wt = new BufferedWriter(new FileWriter("./rdf.txt"));
			m.write(wt,"JSON-LD");
			wt.close();
			BufferedReader bw = new BufferedReader(new FileReader("./rdf.txt"));
			String s;
			String line="";
			while ((s = bw.readLine()) != null) {
				line += s+"\n";
			}
			MqttMessage msg = new MqttMessage(line.getBytes());
			bw.close();
			myClient.publish(myTopic, msg);	
			//System.out.println("[INFO] RDF_Model Resource Expose success");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
