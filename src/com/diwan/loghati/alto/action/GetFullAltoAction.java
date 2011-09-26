package com.diwan.loghati.alto.action;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.net.*;
import java.io.*;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.diwan.loghati.alto.Utils;

public class GetFullAltoAction {
	private String pid;
	private String facet;
	private String callback;
	public String getCallback() {
		return callback;
	}


	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	@Action(value="/getfullalto",results={@Result(name="success",location="/jsp/responsedata.jsp")})	
public String execute() {
	
		/*
		* Get the value of form parameter
		*/
		String theString = "";
		if(pid!=null && facet!=null){
	        try {
	            URL alto = new URL(Utils.getConfig(this, "fedoraserver")+"/"+pid+"/datastreams/"+facet+"/content");
	            URLConnection yahooConnection = alto.openConnection();
	            DataInputStream dis = new DataInputStream(yahooConnection.getInputStream());
	            StringWriter writer = new StringWriter();
	            IOUtils.copy(dis, writer, "UTF-8");
	    		theString = writer.toString().replaceAll("[\n\r]", "");
	    	    theString = StringUtils.replace(theString, "  ", "");
	            System.out.print(theString);
	            dis.close();
	        } catch (MalformedURLException me) {
	            System.out.println("MalformedURLException: " + me);
	        } catch (IOException ioe) {
	            System.out.println("IOException: " + ioe);
	        }
		}    		
		
        HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("respData", callback+"('"+theString+"');");		            

		return "success";
		}
		 
public String getPid() {
	return pid;
}


public void setPid(String pid) {
	this.pid = pid;
}


public String getFacet() {
	return facet;
}


public void setFacet(String facet) {
	this.facet = facet;
}		 
}
