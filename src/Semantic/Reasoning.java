package Semantic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/*
 * 
 * RDF Resource Reasoning Module
 * 2018-04-09
 * By cclab
 * 
 * Work based on User customized Rule
 * 
 * 
 */

public class Reasoning {
	static Info info = new Info();
	static RDFExpose re = new RDFExpose();
	static SemanticFunction sf = new SemanticFunction();
	public static void reasoning(String rule) throws IOException {		
		String[] body = rule.split("&");
		String[] list = body[1].split("\\[");
		String[] mlist = null;
		String str = "";
		for(int i = 1 ; i < list.length ; i++) {
			mlist = list[i].split("]"); 
			str += mlist[0]+"&";
		}
		if(body[0].equals("ADD")) {
			long start = System.currentTimeMillis() ; 
			make_graph(str);
			long end = System.currentTimeMillis(); 
			System.out.println("Add Rule Time ["+(end-start) +" ms]");	
			
			String data = "ADD_Rule$"+(end-start);
			re.ExposePerformance(data);
//			FileOutputStream ar = new FileOutputStream("./AddRule.txt",true);
//			ar.write(data.getBytes());
//			ar.close();
		}
		else if(body[0].equals("DELETE")) {
			long start = System.currentTimeMillis() ; 
			delete_graph(str);
			long end = System.currentTimeMillis(); 
			System.out.println("Delete Rule Time ["+(end-start) +" ms]");		
			String data = "DELETE_Rule$"+(end-start);
			re.ExposePerformance(data);
//			FileOutputStream dr = new FileOutputStream("./DeleteRule.txt",true);
//			dr.write(data.getBytes());
//			dr.close();
		}
	}
	public static void run_rule(String target,String service, String contents) { // /Mobius/kwu-hub/Nest current_temperature 21
		Model m = ModelFactory.createDefaultModel();
		DatasetAccessor maccessor = DatasetAccessorFactory.createHTTP(info.serviceURI+"get");
		m = maccessor.getModel();
		String find_URI =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "SELECT * where {"
				+ " ?x rdf:type \"Command\"."
				+ " ?x rdf:subject \""+target+"/"+service+"\"."
				+ " ?x rdf:object ?o."
				+ " ?x rdf:value ?v."
				+ "}";
		Query query = QueryFactory.create(find_URI);
		QueryExecution qexec = QueryExecutionFactory.create(query,m);
		
		try {
			ResultSet rs = qexec.execSelect();
			while(rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				String[] r_object = soln.getLiteral("o").toString().split("/");
				String[] r_value = soln.getLiteral("v").toString().split("/");
				String c_value = r_value[0];
				String a_value = r_value[1];
				String a_service = "/Mobius/kwu-hub/"+r_object[3]+"/"+r_object[4];
				String a_device = "/Mobius/kwu-hub"+r_object[3];
				
				rule_checker(target,target+"/"+service,c_value,a_device,a_service,a_value,contents);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			qexec.close();
		}
	}
	public static void rule_checker(String c1,String c2,String c3,String a1,String a2,String a3,String current_value) {
		System.out.println("[INFO] Running Rule Checker....");
		System.out.println("[INFO] Condition ( if "+ c1+"'s "+c2+" "+c3+" )");
		System.out.println("[INFO] Current_Condition ( value : "+current_value+" )");
		String[] cv = c3.split(" ");
		String comparision = cv[0];
		String com_value = cv[1];
	
		if(comparision.equals("lower")) {
			double cv1 = Double.parseDouble(com_value);
			double cv2 = Double.parseDouble(current_value);
			if(cv1 > cv2) {
				System.out.println("[INFO] Action ( "+a1+"'s "+a2+" control to "+a3+")");
				oneM2MTranslation.oneM2MTranslate(a1, a2, a3);
			}
		}
		else if (comparision.equals("higher")) {
			double cv1 = Double.parseDouble(com_value);
			double cv2 = Double.parseDouble(current_value);
			if( cv1 < cv2) {
				System.out.println("[INFO] Action ( "+a1+"'s "+a2+" control to "+a3+")");
				oneM2MTranslation.oneM2MTranslate(a1, a2, a3);
			}
			
		}
		else if (comparision.equals("equals")) {
			if(current_value.equals(cv[1])) {
				System.out.println("[INFO] Action ( "+a1+"'s "+a2+" control to "+a3+")");
				oneM2MTranslation.oneM2MTranslate(a1, a2, a3);
			}
		}
	}
	public static void make_graph(String str) {
			String[] list = str.split("&");
//			sf.add_rule(list[0], list[1], list[2]+" "+list[3], list[4], list[5], list[6]);
			sf.add_rule(list[0], list[1], list[2],list[3], list[4], list[5], list[6]);

			RDFExpose.RDFResourceExpose();
		}
		public static void delete_graph(String str) {
			String[] list = str.split("&");
			sf.delete_rule(list[0], list[1], list[2],list[3], list[4], list[5], list[6]);
//			sf.delete_rule(list[0], list[1], list[2]+" "+list[3], list[4], list[5], list[6]);
			RDFExpose.RDFResourceExpose();
		}
}
