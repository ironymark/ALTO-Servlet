package com.diwan.loghati.alto.action;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.net.*;
import java.io.*;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.diwan.loghati.alto.Utils;

public class GetAnnotationAction {
	private String pid;
	private String facet;
	private String pageNo;
	private String callback;
	public String getCallback() {
		return callback;
	}


	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	@Action(value="/getannotation",results={@Result(name="success",location="/jsp/responsedata.jsp")})	
public String execute() {
	
		/*
		* Get the value of form parameter
		*/
		String theString = "";
		if(pid!=null){
	        try {
	            URL alto = new URL(Utils.getConfig(this, "fedoraserver")+"/"+pid+"/datastreams/F_ANNOTATION/content");
	            URLConnection conn = alto.openConnection();
	            DataInputStream dis = new DataInputStream(conn.getInputStream());
	            StringWriter writer = new StringWriter();
	            IOUtils.copy(dis, writer, "UTF-8");
	    		theString = writer.toString().replaceAll("[\n\r]", "&#0d");
	    	    //theString = StringUtils.replace(theString, "  ", "");
	            //System.out.print(theString);
	            dis.close();
	            HttpServletRequest request = ServletActionContext.getRequest();
	    		request.setAttribute("respData", callback+"('"+theString+"');");
	    		
//	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//	            DocumentBuilder db = dbf.newDocumentBuilder();
//	            Document doc = db.parse(dis);
//	            doc.getDocumentElement().normalize();
	            
//	            NodeList nodeLst = doc.getElementsByTagName("Page");
//	            int l = nodeLst.getLength();
//	            for(int i=0;i<l;i++){
//    	        	Node node = nodeLst.item(i);
//    	        	NamedNodeMap attrs = node.getAttributes();
//    	        	Node attr = attrs.getNamedItem("PgNo");
//    	        	String pgNo = attr.getNodeValue();
//    	        	if(!pageNo.equals(pgNo))
//    	        		node.getParentNode().removeChild(node);
//	            }
//	            XPath xpath = XPathFactory.newInstance().newXPath();
//	            NodeList nodes = (NodeList) xpath.evaluate("//Annotations", doc, XPathConstants.NODESET);
//	            Node fstNode = nodes.item(0);
//	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//	            StringWriter writer = new StringWriter();
//	            transformer.transform(new DOMSource(fstNode), new StreamResult(writer));
//	            theString = writer.toString().replaceAll("[\n\r]", "");
//	            theString = StringUtils.replace(theString, "  ", "");
//	            HttpServletRequest request = ServletActionContext.getRequest();
//	    		request.setAttribute("respData", callback+"('"+theString+"');");		            
//	            dis.close();
	        } catch (Exception me) {
	            System.out.println("MalformedURLException: " + me);
	        }
		}    		
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


public String getPageNo() {
	return pageNo;
}


public void setPageNo(String pageNo) {
	this.pageNo = pageNo;
}		 
}
