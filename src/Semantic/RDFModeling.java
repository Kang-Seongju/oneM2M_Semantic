package Semantic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDF;


/*
 * 
 * oneM2M Resource RDF Modeling Module
 * 2018-04-09
 * By cclab
 * 
 * oneM2M Registered Device list & ContentInstance (latest)
 *  
 */
public class RDFModeling {
	static Info info = new Info();
	static SemanticFunction sf = new SemanticFunction();
	public static void onload() {
		Info info = new Info();
		
	    Connection connection = null;
	    Statement st = null;
		
	    try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql://localhost:"+info.csedbport+"/mobiusdb?&useSSL=false", "root", "coco2006");			
			st = connection.createStatement();
			String get_device_list = "Select * From mobiusdb.lookup where ty = 3;";
			ResultSet r_device_list = st.executeQuery(get_device_list);
			while(r_device_list.next()) {
				String[] list = r_device_list.getString("ri").toString().split("/");
				if(list.length == 4) {  // service
					sf.add_device(r_device_list.getString("ri"));
				}
				if(list.length == 5) {
					String get_cin_value = "Select b.* from mobiusdb.cin as b where b.ri = (SELECT a.ri FROM mobiusdb.lookup as a where a.pi=? order by ct desc limit 1);";
					PreparedStatement ps = connection.prepareStatement(get_cin_value);
					ps.setString(1, r_device_list.getString("ri").toString());
					ResultSet prs = ps.executeQuery();
					while(prs.next()) {
						sf.add_service(r_device_list.getString("pi"), r_device_list.getString("rn"), prs.getString("con"));
					}
				}
			}
		} catch (SQLException ex) {
			System.err.println("SQLException:" + ex);
	
		} catch (Exception e) {
			System.err.println("Exception:" + e);
		}
	}
}
