package Semantic;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/*
 * 
 * Resource Monitoring Module
 * 2018-04-09
 * By cclab
 * 
 * Communicate with CSE to MQTT Protocol
 * ToPIC = /oneM2M/semantic/CSENAME/JSON
 * Broker = CSE_IP/1883 
 * 
 * 
 */

public class Monitoring{
	static MqttClient myClient = null;
	static MqttConnectOptions connOpt;
	static RDFExpose re = new RDFExpose();
	final static String clientID = "oneM2M_Semantic_Subscriber";
	static Server server = new Server();
	static Info info = new Info();
	public static Boolean Connect() {
		
		connOpt = new MqttConnectOptions();
		try {
			myClient = new MqttClient("tcp://"+info.brokerip+":"+info.brokerport, clientID);
			myClient.connect(connOpt);
			System.out.println("[INFO] Connected to " + "tcp://"+info.brokerip+":"+info.brokerport);
			return true;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static void runClient() {
		if(Connect()) {
			String getRuleTopic = "/oneM2M/pub/Client/Semantic/Rule";
			String postRuleTopic = "/oneM2M/pub/Semantic/Client/Rule";
			String MonitoringTopic = "/oneM2M/sub/Semantic/Mobius/Json";
			String oneM2MTranslateTopic = "/oneM2M/sub/Semantic/Client/XML";
			String CSEAddress ="http://"+info.cseip+":"+info.cseport;
			
			
			try {
				
				int subQoS = 0;
				myClient.subscribe(getRuleTopic,subQoS);
				myClient.subscribe(postRuleTopic,subQoS);
				myClient.subscribe(MonitoringTopic, subQoS);
				myClient.subscribe(oneM2MTranslateTopic, subQoS);
				myClient.setCallback(new MqttCallback() {

					@Override
					public void connectionLost(Throwable arg0) {
						// TODO Auto-generated method stub
						runClient();
					}

					@Override
					public void deliveryComplete(IMqttDeliveryToken arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
						// TODO Auto-generated method stub
						String response = new String(arg1.getPayload());
						

						if(arg0.equals(MonitoringTopic)) {
//							FileOutputStream rs = new FileOutputStream("./Reasoning.txt",true);
//							FileOutputStream ur = new FileOutputStream("./UpdateResource.txt",true);
//							FileOutputStream ad = new FileOutputStream("./AddDeivce.txt",true);
//							FileOutputStream dd = new FileOutputStream("./DeleteDevice.txt",true);
							System.out.println("[INFO] Monitoring Resource Update");
							System.out.println(response);
							String[] list = response.split("&");
							String type = list[0];
							String target ="";
							String value = "";
							if(type.equals("cin")) {
								long start = System.currentTimeMillis() ; 
								ResourceUpdate.update("update", list[1], list[2], list[3]);
								long end = System.currentTimeMillis(); 
//								System.out.println("Update Resource Time ["+(end-start) +" ms]");		
								String data = "UPDATE_Resource$"+(end-start);
								re.ExposePerformance(data);
//								ur.write(data.getBytes());
//								ur.close();
								
								start = System.currentTimeMillis() ; 
								Reasoning.run_rule(list[1],list[2],list[3]);
								end = System.currentTimeMillis(); 
//								System.out.println("Reasoning Time ["+(end-start) +" ms]");
								data = "MATCHING_Rule$"+(end-start);
								re.ExposePerformance(data);
//								rs.write(data.getBytes());
//								rs.close();
							}
							else if(type.equals("cnt")) {
								long start = System.currentTimeMillis() ; 
								ResourceUpdate.update("add", list[1], list[2], null);
								long end = System.currentTimeMillis(); 
								System.out.println("Make Container Time ["+(end-start) +" ms]");
								RDFExpose.RDFResourceExpose();
								String data = "CREATE_Resource$"+(end-start);
								re.ExposePerformance(data);
//								ad.write(data.getBytes());
//								ad.close();
							}
							else if(type.equals("delete")) {
								long start = System.currentTimeMillis() ; 
								ResourceUpdate.update("delete", list[1],null, null);
								long end = System.currentTimeMillis(); 
								System.out.println("Delete Container Time ["+(end-start) +" ms]");
								RDFExpose.RDFResourceExpose();
								String data = "DELETE_Resource$"+(end-start);
								re.ExposePerformance(data);
//								dd.write(data.getBytes());
//								dd.close();
							}		
						}
						else if(arg0.equals(postRuleTopic)) {
							RDFExpose.RDFResourceExpose();
						}
						else if(arg0.equals(getRuleTopic)) {
							//System.out.println(response);
							Reasoning.reasoning(response);
							
						}
					}
				});
				//System.out.println("[INFO] SubScribe at Monitoring Module");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//System.err.println("[WARN] Fail to SubScribe at Monitoring Module");
			}
		}else {
			//System.err.println("[WARN] Check your MQTT broker");
		}
	}
}

