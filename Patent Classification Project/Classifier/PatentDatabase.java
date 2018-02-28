/**
 * Oneal Abdulrahim
 * CSCE 310 - 500
 * Homework 5
 */

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class PatentDatabase {
    // Connection to the database on CS servers (MariaDB)
    public static final String DB_LOCATION = "database-new.cse.tamu.edu";
    public static final String DB_NAME = "oneal.abdulrahim-patentDB";
    public static final String DB_USER = "oneal.abdulrahim";
    public static final String DB_PASS = "*******";
    public static final List<String> PATENT_ATTRIBUTES = Arrays.asList("patnum",
                                                                       "title",
                                                                       "inventors",
                                                                       "assignee",
                                                                       "familyID",
                                                                       "applNum",
                                                                       "dateFiled",
                                                                       "docID",
                                                                       "pubDate",
                                                                       "USclass",
                                                                       "examiner",
                                                                       "legalfirm");
    
        /**
         * Attempts connection to the database given the URL.
         * @param
         * @return connection   Connection datatype containing active connection to DB
         * @throws SQLException To be considered when calling connect() 
         */
        public static Connection connect() throws SQLException {
            Connection connection = null;

            try {
                String connectionString = "jdbc:mysql://"+ DB_LOCATION + "/" + DB_NAME;
                Class.forName ("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(connectionString, DB_USER, DB_PASS);
                //System.out.println ("Database connection established!");
            } catch (Exception e) {
                System.out.println("Connection Issue: " + e.getMessage());
            }
            
            return connection;
        }

        /**
         * Using the provided code from earlier in class, open a file and parse JSON as
         * an ArrayList for eaier access to String elements. Returns vector of Strings
         * @param   filename    The name of the file without extension
         * @return              ArrayList of Strings containing parsed data from file
         */
        public static ArrayList<String> getPatentDataFile(String filename) throws IOException {
            ArrayList<String> patentList = null;

            ObjectInputStream objectinputstream = null;

		    try {
            	FileInputStream streamIn = new FileInputStream(filename + ".ser"); //patent1-1000JSON.ser")
		        objectinputstream = new ObjectInputStream(streamIn);
	        	patentList  = (ArrayList<String>) objectinputstream.readObject();
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        if (objectinputstream != null){
		            objectinputstream.close();
		        } 
            }
            
            return patentList;
        }

        /**
         * Given the list of patent data as a String and the index of the patent from
         * our vector data model (from JSON parsed data), sort through and load a new
         * ArrayList with a vector of patent attributes, in order. Usage of global var
         * @param patentList    The ArrayList of Strings containing parsed data from file
         * @param index         The index (or patent number) for one patent
         * @return              ArrayList of Strings containing ordered attributes for 1 patent
         */
        public static List<String> getPatentData(ArrayList<String> patentList, int index) {
            List<String> attributes = new ArrayList<String>();
            String currentPatent = "";

            if (index >=0 && index < patentList.size()) {
                currentPatent = patentList.get(index);
            }

            attributes.add(Integer.toString(index));

            for (String attribute : PATENT_ATTRIBUTES) {
                
                String searchStr = "\"" + attribute + "\":\""; //e.g. "iDate":"
	            int i = currentPatent.indexOf(searchStr);	//find the start of the desired field
	            if (-1 != i) {
	        	    String s = currentPatent.substring(i + searchStr.length());
	        	    searchStr = "\",\"";
	        	    int j = s.indexOf(searchStr);	//find the end of the field (doesn't work for if it has subfields!)
                    s = s.substring(0, j);	//pick off the field
	        	    attributes.add(s);
	            }
            }

            return attributes;
        }

        /**
         * Given an unverified ArrayList of attributes, insert into Patent.
         * Note: parameters are unchecked
         * @param attributes    Assumed well-formed field arguments
         */
        public static void insertIntoPatent(List<String> attributes) {
            Connection connection;
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO patent (patnum, title, inventors, assignee, familyID, "
                  + "applNum, dateFiled, docID, pubDate, USclass, examiner, legalfirm) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                // let's just put it in a loop
                for (int i = 1; i <= attributes.size(); i++) {
                    preparedStatement.setString(i, attributes.get(i - 1));
                }
                
                preparedStatement.executeUpdate();

                connection.close(); // close connection to DB -- always
            } catch (SQLException exception) {
                System.err.println("SQL Exception during connection: ");
                exception.printStackTrace();
            }
        }

        public static void main(String[] args) {
            
            try {
                ArrayList<String> patentDataFile = getPatentDataFile("patent1-1000JSON");
                
                // do them all!! release the hounds (takes a hot second)
                for (int i = 0; i < patentDataFile.size(); i++) {
                    insertIntoPatent(getPatentData(patentDataFile, i));
                }
            

            } catch (Exception e) {
                System.err.println("SQL Exception during connection: ");
                e.printStackTrace();
            }
            
        }
}