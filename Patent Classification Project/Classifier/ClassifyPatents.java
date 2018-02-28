/**
 * Oneal Abdulrahim
 * CSCE 310 - 500
 * Homework 5
 */

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

public class ClassifyPatents {
    public static int[] knownDByes = {2,3,4,5,6,7,8,12,13,14,17,18,19,20,21,23,24,27,28,29,30,31,34,36,37,38,39,41,46,50};
	public static int[] knownDBno = {0,1,9,10,11,15,16,22,25,26,32,33,35,40,42,43,44,45,47,48,49};
	public static double patYesCnt = knownDByes.length;
	public static double patNoCnt = knownDBno.length;
	public static HashSet<String> hset = new HashSet<String>();
	public static HashMap<String, DocWord> dict = new HashMap<String, DocWord>(); //dictionary from training set
    
    public static final String DB_LOCATION = "database-new.cse.tamu.edu";
    public static final String DB_NAME = "oneal.abdulrahim-patentDB";
    public static final String DB_USER = "oneal.abdulrahim";
    public static final String DB_PASS = "*******";

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


	public ClassifyPatents() throws IOException {
        Connection connection;
        try {
            connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT title FROM patent WHERE patNum=?");

		    try {
                BufferedReader in = new BufferedReader(new FileReader("stopwords.txt"));
                String str;
                while ((str = in.readLine()) != null) {
                    hset.add(str.toLowerCase().trim());
                }
                in.close();
            } catch (IOException e) {
                System.out.println("File Read Error");
            }
		    //train the classifier
		    for (int yesDB: knownDByes) {
                preparedStatement.setInt(1, yesDB);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                train(resultSet.getString("title"), true);
            }
        
		    System.out.println("YesWord Dictionary size: "+dict.size());
		    for (int noDB: knownDBno) {
                preparedStatement.setInt(1, noDB);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                train(resultSet.getString("title"), false);
            }
		    	
		    System.out.println("Total Dictionary size: "+dict.size());
        
		    //compute word probabilities
		    Set<String> keys = dict.keySet();
		    for(String k: keys){
		    	DocWord entry = dict.get(k);
		    	entry.yesFrac = entry.yesCnt/patYesCnt;	//avg freq per doc type
		    	entry.noFrac = entry.noCnt/patNoCnt;
		    	entry.yesProb = entry.yesFrac/(entry.yesFrac+entry.noFrac);
		    	//if(Float.isNaN(entry.yesProb)) throw new Exception();
		    	if(entry.yesProb < 0.01) entry.yesProb = 0.01D;
		    	else if(entry.yesProb > 0.99) entry.yesProb= 0.99D;
		    	entry.noProb = 1.0D - entry.yesProb;

		    	dict.replace(k, entry);
            }
            
            connection.close(); // close connection to DB -- always
        } catch (SQLException exception) {
            System.err.println("SQL Exception during connection: ");
            exception.printStackTrace();
        }
    }
    
	public void train(String patent, boolean DBinvention) throws IOException {
		String[] tokens = splitPatent(patent);
        for (int i = 0; i < tokens.length; i++){
        	if(hset.contains(tokens[i]))//forget stop words
        		continue;
        	else processToken(tokens[i],DBinvention);
        }
        return;
    }
    
	private void processToken(String token, boolean patType){
		
		if(!dict.containsKey(token)){//is token in the dictionary?
			//add it
			DocWord entry = new DocWord();
			entry.word = token;
			entry.yesCnt = 0;
			entry.noCnt = 0;
			dict.put(token, entry);
		}
		DocWord entry = dict.get(token);
		if(patType==true)
			entry.yesCnt++;
		else entry.noCnt++;
		dict.replace(token, entry);
    }
    
	public static double classify(String p) throws Exception{
		HashSet<String> tset = new HashSet<String>();
		ArrayList<DocWord> words = new ArrayList<DocWord>();//words in new patent we are classifying
		String[] tokens = splitPatent(p);
		for(int i = 0; i < tokens.length; i++){
			if(dict.containsKey(tokens[i]) && !tset.contains(tokens[i])){
				//the token is in the dictionary and this is 1st time we have seen it in the patent
				tset.add(tokens[i]); //remember that we have seen this word
				words.add(dict.get(tokens[i]));
			}
			//else //it is an unknown word (not in dict) so just skip it for now
		}
		System.out.println("Patent word count: "+words.size());
		//determine yes or no according to: http://www.paulgraham.com/naivebayes.html
		double yesProduct = 1.0D;
		double noProduct = 1.0D;
	    for (DocWord dw: words){
	    	yesProduct *= dw.yesProb;
	    	noProduct *= dw.noProb;
	    }
	    if(Double.isNaN(yesProduct)) throw new Exception();
	    if(Double.isNaN(noProduct)) throw new Exception();
	    double yesProb = yesProduct / (yesProduct + noProduct);
	    if(Double.isNaN(yesProb)) throw new Exception();
		return yesProb;
	}
	
	private static String[] splitPatent(String p) throws IOException {
        String text = p.replace('.', ' ').toLowerCase().trim();//take out periods which could be used in things like e.g., or i.e.
        String[] tokens = text.split("[\\,\\s!;?:\"]+"); //split and take out whitespace and other word separators (leave apostrophe)
        return tokens;
	}


    public static void main(String[] args) throws Exception {
		//read index of desired start patent	
		while(true){
	        System.out.println("Enter patent start number: ");
	        Scanner input = new Scanner(System.in);
	        String inputLine = input.nextLine();
	        int pn = Integer.parseInt(inputLine.trim());
            
            ClassifyPatents classifyPatents = new ClassifyPatents();

            Connection connection = null;
            
	        for (int i = pn; i < 1000; i++) {
                try {
                    connection = connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM patent WHERE patNum=?");
                    preparedStatement.setInt(1, i);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    
                    resultSet.next();
                    String patent = resultSet.getString("title");
                    
                    double pr = classify(patent);
		            System.out.println("\nPatent index:"+i+"\t"+"Class: "+pr+"\t");
		            inputLine = input.nextLine();
		            if(inputLine.toLowerCase().contains("exit")) 
		        	    break;   
	            } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            connection.close();
	    }
    }
}