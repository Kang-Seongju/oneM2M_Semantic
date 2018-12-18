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

public class SemanticFunction {
	static String ns = "http://cclab.kw.ac.kr#";
	static Info info = new Info();
	public void add_device(String device) {
		UpdateRequest update;
		UpdateProcessor processor;
		String make_device =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "INSERT DATA {\n"
				+ "<"+ns+device+"> rdf:type \"Device\";\n"
				+ "rdf:subject \""+device+"\".\n"
				+ "}";
		update = UpdateFactory.create(make_device);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
	}
	public void add_service(String device,String service,String value) { // /Mobius/kwu-hub/device , service ,value
		UpdateRequest update;
		UpdateProcessor processor;	
		String make_service =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "INSERT DATA {\n"
				+ "<"+ns+device+"> rdf:object \""+service+"\";\n"
				+ "}";
		update = UpdateFactory.create(make_service);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
		String make_device =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "INSERT DATA {\n"
				+ "<"+ns+device+"/"+service+"> rdf:type \"Service\";\n"
				+ "rdf:subject \""+device+"/"+service+"\";\n"
				+ "rdf:object \""+service+"\";\n"
				+ "rdf:value \""+value+"\"."
				+ "}";
		update = UpdateFactory.create(make_device);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
	}
	public void delete_device(String Device) {
		UpdateRequest update;
		UpdateProcessor processor;	
		Model model = ModelFactory.createDefaultModel();	    
	    DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(info.serviceURI+"get");
		model = accessor.getModel();
		String find_service =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "SELECT ?x where {\n"
				+ "<"+ns+Device+"> rdf:object ?x\n."
				+ "}";
		String[] arr = new String[20];
		int index = 0;
		Query query = QueryFactory.create(find_service);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		try {
			ResultSet rs = qexec.execSelect();
			while(rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				arr[index] = Device+"/"+soln.getLiteral("x").toString();
				index +=1;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			qexec.close();
		}
		arr[index] = Device;
		index+=1;
		for(int i = 0 ; i < index ;  i ++) {
			String DELETE_GRAPH =""
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
					+ "DELETE WHERE {"
					+ "<"+ns+arr[i]+"> ?p ?o ."
					+ "}";
			update = UpdateFactory.create(DELETE_GRAPH);
			processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
			processor.execute();
		}
	}
	public void update_resource(String target,String service,String value) {
		UpdateRequest update;
		UpdateProcessor processor;	
		String DELETE_GRAPH =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "DELETE Where {"
				+ "<"+ns+target+"/"+service+"> rdf:value ?o.\n"
				+ "}";
		update = UpdateFactory.create(DELETE_GRAPH);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
		add_service(target,service,value);
	}
	public void add_rule(String cd,String cs,String co,String cv,String ad,String as,String av) {
		
		String va = co+" "+cv+"/"+av;
		UpdateRequest update;
		UpdateProcessor processor;
		String[] cdd = cd.split("/");
		String[] add = ad.split("/");
		String Rulename = cdd[3]+cs+co+cv+add[3]+as+av;
		System.out.println(Rulename);
 		String make_rule =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "INSERT DATA {\n"
				+ "<"+ns+Rulename+"> rdf:type \"Command\";\n"
				+ 						  "rdf:subject \""+cd+"/"+cs+"\";\n"
				+ 						  "rdf:object \""+ad+"/"+as+"\";\n"
				+ 						  "rdf:value \""+va+"\";\n"
				+ "}";
 		//System.out.println(make_rule);
		update = UpdateFactory.create(make_rule);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
	}
	public void delete_rule(String cd,String cs,String co,String cv,String ad,String as,String av) {		
		String Rulename = cd+cs+co+cv+ad+as+av;
		UpdateRequest update;
		UpdateProcessor processor;
		String delete_rule =""
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "delete where {\n"
				+ "<"+ns+Rulename+"> ?p ?o.\n"
				+ "}";
		System.out.println(Rulename);
		update = UpdateFactory.create(delete_rule);
		processor = UpdateExecutionFactory.createRemote(update,info.serviceURI+"update");
		processor.execute();
	}
}
