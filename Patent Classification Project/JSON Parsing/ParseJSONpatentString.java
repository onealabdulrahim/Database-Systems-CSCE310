package derby;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.derby.jdbc.ClientDriver;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
//
// This program reads a Patent stored as a String in JSON format and parses for a given field name and prints it.
// Each patent has the following JSON fields: {"patNum":"D238414","iDate":"January 13, 1976","title":"","abstract":"","inventors":"","assignee":"","familyID":"","applNum":"","dateFiled":"","docID":"","pubDate":"","USclass":"D10/50","references":"","claims":"","examiner":"","legalfirm":"","summary":"","description":""}
// All field values are presented as strings, but might be better stored in the database as different 
// types to facilitate comparisons (e.g. DATE type)
// The field "inventors" may have multiple subfields representing individual inventors, but the data is presented as a single String. It needs to be further parsed. 
// The fields "references" and "claims" may have subfields, and if so they are presented as JSON subfields named “ref#” and “claim#” where # is a number. E.g. ref14 or claim7
// Text fields like "abstract", "claims", "summary" and "description" make be large text strings (thousands of characters).

// Author: Ronnie Ward
//
public class ParseJSONpatentString {

	public static void main(String[] args) throws IOException {

		//read patent data
		GetPatentDatafile pdf = new GetPatentDatafile();
		//read name of desired field	
		while(true){
	        System.out.println("Enter record number and field name of interest (separated by comma): ");
	        Scanner input = new Scanner(System.in);
	        String inputLine = input.nextLine();
	        String[] tokens = inputLine.split(",");
	        int rn = Integer.parseInt(tokens[0].trim());
	        String fldname = tokens[1].trim();
	        String patent = pdf.getPatent(rn);
	        String searchStr = "\""+fldname+"\":\""; //e.g. "iDate":"
	        int i = patent.indexOf(searchStr);	//find the start of the desired field
	        if(-1 != i) {
	        	String s = patent.substring(i+searchStr.length());
	        	searchStr = "\",\"";
	        	int j = s.indexOf(searchStr);	//find the end of the field (doesn't work for if it has subfields!)
	        	s = s.substring(0,j);	//pick off the field
	        	System.out.println("Here is the field "+fldname+":");
	        	System.out.println(s);
	        }
		}
	}

}