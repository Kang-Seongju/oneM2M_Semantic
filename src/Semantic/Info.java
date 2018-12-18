package Semantic;

/*
 * 
 * Save Information
 * cse / mqtt broker / fuseki
 * 
 */

public class Info {
	static String cseip;
	static String cseport = "7579";
	static String csedbport = "3306";
	static String csename = "Mobius";
	static String brokerip;
	static String aeid;
	static String brokerport = "1883";
	static String fusekiip;
	static String fusekiport = "3030";
	static String fusekiname = "oneM2M_RDF";
	static String serviceURI;
	static String ruleURI;
	public void CSE(String ip) {
		this.cseip = ip;
	}
	public void BROKER(String ip) {
		this.brokerip = ip;
	}
	
	public void FUSEKI(String ip) {
		this.fusekiip = ip;
		serviceURI ="http://"+this.fusekiip+":"+this.fusekiport+"/"+this.fusekiname+"/";
//		serviceURI ="http://128.134.65.118:3030/"+this.fusekiname+"/";
		System.out.println("INFO " + serviceURI);
	}
}
