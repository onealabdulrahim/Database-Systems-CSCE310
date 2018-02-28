package derby;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;

//
//This class is used to read a serialized data file of Patents stored in JSON format
//It returns a designated record in the file
//Author, Ronnie Ward

public class GetPatentDatafile {
	ArrayList<String> patentList = null;

		//https://stackoverflow.com/questions/17293991/how-to-write-and-read-java-serialized-objects-into-a-file
	public GetPatentDatafile() throws IOException {
		ObjectInputStream objectinputstream = null;

		try {
            System.out.println("Enter patent input file name: ");
            Scanner input = new Scanner(System.in);
            String inputFile = input.nextLine().toLowerCase();
            int i = inputFile.indexOf(".ser");
            if(-1 != i)
            	inputFile = inputFile.substring(0, i);
        	FileInputStream streamIn = new FileInputStream(inputFile+".ser");//patent1-1000JSON.ser");//
		    objectinputstream = new ObjectInputStream(streamIn);
	    	patentList  = (ArrayList<String>) objectinputstream.readObject();
	    	if (patentList != null){
	    		System.out.println("Number of patents read: "+patentList.size());
	    	}
	    	else System.out.println("Patent Data not read.");
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if(objectinputstream != null){
		        objectinputstream .close();
		    } 
		}
	}
	public String getPatent(int rn){
        if(rn >=0 && rn < patentList.size())
        	return patentList.get(rn);
        return null;
	}
}