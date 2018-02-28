import java.io.IOException;

public class PatentParser {
	String ps;	//patent string to parse
	JsonParser  parser;
	
	public PatentParser(String str) throws JsonParseException, IOException{
		ps = str;
		parser = JFact.factory.createParser(ps);
	}
	public String findFieldValue(String fname){
		try {
		    while(!parser.isClosed()){
		        JsonToken jsonToken = parser.nextToken();
		        if(JsonToken.FIELD_NAME.equals(jsonToken)){	//is the token a field name?
					String fieldName = parser.getCurrentName();
		            if(fname.equals(fieldName)){					//is it the desired field name?
						jsonToken = parser.nextToken();			//yes, move to field value
						String tv = null;
						if(jsonToken != null){
							tv = parser.getValueAsString();
						}
		            	return tv;			//return the value as a String
		            }
		        }
		    }//end while
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;	//token not found
	}

}
