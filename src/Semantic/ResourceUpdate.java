package Semantic;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

/*
 * 
 * RDF Resource UPdate Module
 * 2018-04-10
 * By cclab
 * 
 * Delete legacy value before insert new value 
 * 
 * Use below 
 * ResourceUpdate.update(fuseki_URI, oneM2M_URI, function, value)
 * 
 * 
 */

public class ResourceUpdate {
	static Info info = new Info();
	static SemanticFunction sf = new SemanticFunction();
	public static void update(String work,String target,String service, String contents) { // bulb, color, blue
		String[] list = target.split("/");
		if(work.equals("add")) { // 디바이스 및 서비스 추가
			if(list.length == 4 ) {
				sf.add_device(target);
			}else if(list.length == 5){
				String d = list[3];
				String s = list[4];
				sf.add_service("/Mobius/kwu-hub/"+d, s, contents);
			}
		}else if(work.equals("delete")) {
			sf.delete_device(target);
		}else if(work.equals("update")) {
			sf.update_resource(target, service,contents);
		}
	}	
}