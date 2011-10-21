package com.diwan.loghati.alto.action;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;



public class GetASRAction {
	private String pid;
	@Action(value="/getasr",results={@Result(name="success",location="/jsp/responsedata.jsp")})	
	public String execute() {
		try {
	    	HttpServletRequest request = ServletActionContext.getRequest();
	    	ServletInputStream sis = request.getInputStream();
			BufferedInputStream soundData = new BufferedInputStream( sis );
			
	        String userID;
	        String bookID;
	        String language;
	        String numOfSuggestions;
	        String recognitionMode;		    
            userID = "191";
            bookID = pid;//"iqra:3390";
            numOfSuggestions = "3";
            language = "en-gb";
            recognitionMode = "search";		    
		    HashMap<String, String> postParameters = new HashMap<String, String>();
            postParameters.put("USER_ID", userID);
            postParameters.put("LANGUAGE", language);
            postParameters.put("CONTEXT_ID", bookID);
            postParameters.put("MAX_NBEST", numOfSuggestions);
            postParameters.put("RECOGNITION_MODE", recognitionMode);	        
		    String formDataBoundary = "-----------------------------28947758029299";
	        String contentType = "multipart/form-data; boundary=" + formDataBoundary;

	        ByteArrayOutputStream formData = GetMultipartFormData(postParameters, formDataBoundary, soundData);
	        
	        String charset = "UTF-8";
			URLConnection urlConnection = new URL("http://loghati.amuser-qstpb.com:8081/iqra-tts/Recog").openConnection();
			urlConnection.setConnectTimeout( 20000 );  // long timeout, but not infinite
			urlConnection.setReadTimeout( 20000 );
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true); // Triggers POST.
			urlConnection.setRequestProperty("accept-charset", charset);
			urlConnection.setRequestProperty("content-type", contentType);
			urlConnection.setRequestProperty("User-agent", userID);
			
			OutputStream cos =urlConnection.getOutputStream(); 
			cos.write(formData.toByteArray());
			cos.flush();
			cos.close();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		    String line;
		    StringBuffer sb = new StringBuffer();
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line);
		    }
		    rd.close();		
	    	System.out.println(sb.toString());
		
			
		    
			
			ServletActionContext.getRequest().setAttribute("respData", sb.toString());
			return "success";	
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
	  private ByteArrayOutputStream GetMultipartFormData(HashMap<String, String> postParameters, String boundary, BufferedInputStream soundData)
	    {
	    	ByteArrayOutputStream formDataStream = new ByteArrayOutputStream();
	    	try{
	    	Iterator<String> keys = postParameters.keySet().iterator();
	    	StringBuffer sb = new StringBuffer();
	    	while(keys.hasNext()){
				String key = keys.next();
				String postData = "--"+boundary+"\r\nContent-Disposition: form-data; name=\""+key+"\"\r\n\r\n"+postParameters.get(key)+"\r\n";
				sb.append(postData);
				formDataStream.write(postData.getBytes("UTF8"));
	    	}

	    	// Add just the first part of this param, since we will write the file data directly to the Stream
	        String header = "--"+boundary+"\r\nContent-Disposition: form-data; name=\""+"AUDIO_F"+"\"; filename=\""+"AUDIO_F"+"\";\r\nContent-Type: application/octet-stream\r\n\r\n";
	        sb.append(header);
	        formDataStream.write(header.getBytes("UTF8"));

	        // Write the file data directly to the Stream, rather than serializing it to a String.  This 
	        //formDataStream.Write(fileData, 0, fileData.Length);

//	        String file = "c:/temp/fromahmed.wav";
//	        FileInputStream fileinputstream = new FileInputStream(file);
	//
//	        int numberBytes = fileinputstream.available();
//	        byte bytearray[] = new byte[numberBytes];
	//
//	        fileinputstream.read(bytearray);
	//
//	        formDataStream.write(bytearray);
	//
//	        fileinputstream.close();        
		    byte[] cbuf = new byte[ 2048 ];
		    int num;
		    while ( -1 != (num=soundData.read( cbuf )))
		    {
		    	formDataStream.write(cbuf,0, num);
		    }		        

	        // Add the end of the request
	        String footer = "\r\n--" + boundary + "--\r\n";
	        formDataStream.write(footer.getBytes("UTF8"));
	        
	       
	    	}catch(Exception e){
	    		
	    	}
	    	 return formDataStream;
	    }
	 		 
public String getPid() {
	return pid;
}


public void setPid(String pid) {
	this.pid = pid;
}
		 
}
