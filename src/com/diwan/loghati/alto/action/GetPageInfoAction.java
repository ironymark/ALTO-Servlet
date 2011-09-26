package com.diwan.loghati.alto.action;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;


public class GetPageInfoAction{
	private String pid;
	private String facet;
	private String callback;
	private String debug;
	
	public String getCallback() {
		return callback;
	}


	public String getDebug() {
		return debug;
	}


	public void setDebug(String debug) {
		this.debug = debug;
	}


	public void setCallback(String callback) {
		this.callback = callback;
	}

	@Action(value="/getpageinfo",results={@Result(name="success",location="/jsp/responsedata.jsp")})
	public String execute() {
		/*
		* Get the value of form parameter
		*/
		
		InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream
		("config.xml");
		Properties prop = new Properties();
		String loghatiConf_fedoraserver = "";
		String loghatiConf_fedoraRELS = "";
		String loghatiConf_fedoraDC = "";
    	try {
    		prop.loadFromXML(is);
    		loghatiConf_fedoraserver = prop.getProperty("fedoraserver");
    		loghatiConf_fedoraRELS = prop.getProperty("fedoraRELS");
    		loghatiConf_fedoraDC = prop.getProperty("fedoraDC");
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
		String title="";
		String hasMTEn="";
		String hasMTAr ="";
		String pageId = "";
		ArrayList<String> parts = new ArrayList<String>();
		if(pid!=null && facet!=null){
			if("true".equals(debug)){
	    		String mtFacet = "";
	    		title = "Debug Test";
	    		parts.add("iqra:58");
	    		//parts.add("iqra:59");
    			mtFacet = "F_OCR";
	    		String json = "{\"title\":\""+title+"\",\"mtfacet\":\""+mtFacet+"\",\"pages\":[";
	    		int i = 0;
	    		for(int j=0;j< parts.size();j++){
	    			json += "{\""+j+"\":\""+parts.get(j)+"\"}";
	    			if(i<(parts.size()-1))
	    				json+= ",";
	    			i++;
	    		}
	    		json+= "]}";    		
	    		
	            HttpServletRequest request = ServletActionContext.getRequest();
	    		request.setAttribute("respData", callback+"('"+json+"');");					
			}else{
		        try {
		            URL url = new URL(loghatiConf_fedoraserver+"/"+pid+loghatiConf_fedoraDC);
		            URLConnection connection = url.openConnection();
		            DataInputStream dis = new DataInputStream(connection.getInputStream());
		            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		            DocumentBuilder db = dbf.newDocumentBuilder();
		            Document doc = db.parse(dis);
		            doc.getDocumentElement().normalize();	            
		            NodeList nodeLst = doc.getElementsByTagName("title");
		            Node fstNode = nodeLst.item(0);
		            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		                Element fstElmnt = (Element) fstNode;
		                NodeList fstNm = fstElmnt.getChildNodes();
		                title = ((Node) fstNm.item(0)).getNodeValue();
		            }
		            
		        } catch (MalformedURLException me) {
		            System.out.println("MalformedURLException: " + me);
		        } catch (Exception ioe) {
		            System.out.println("IOException: " + ioe);
		        }
		        try {
		            URL url = new URL(loghatiConf_fedoraserver+"/"+pid+loghatiConf_fedoraRELS);
		            URLConnection connection = url.openConnection();
		            DataInputStream dis = new DataInputStream(connection.getInputStream());
		            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		            DocumentBuilder db = dbf.newDocumentBuilder();
		            Document doc = db.parse(dis);
		            doc.getDocumentElement().normalize();	            
		            NodeList nodeLst = doc.getElementsByTagName("rdf:Description");
		            Node fstNode = nodeLst.item(0);
		            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
		                Element fstElmnt = (Element) fstNode;
		                NodeList fstNm = fstElmnt.getChildNodes();
		                int l = fstNm.getLength();
		                for(int i=0;i<l;i++){
		                	Node fstChildNode = fstNm.item(i);
		                	if (fstChildNode.getNodeType() == Node.ELEMENT_NODE) {
		                		String child = fstChildNode.getNodeName();
		                		if("hasPageStruct".equals(child)){
		                			NamedNodeMap attrs = fstChildNode.getAttributes();
		                			Node attr = attrs.getNamedItem("rdf:resource");
		                			String a = attr.getNodeValue();
		                			String[] v = org.apache.commons.lang.StringUtils.split(a,"/");
	            					pageId = v[1];
		                		}
		                		else 
		                			if(child.contains("hasFacet_F_MT_en")){
		                				hasMTEn = child;
		                			}else 
		                				if(child.contains("hasFacet_F_MT_ar")){
		                					hasMTAr = child;
		                				}	                		
		                	}
		                }
		            }
		            
		        } catch (MalformedURLException me) {
		            System.out.println("MalformedURLException: " + me);
		        } catch (Exception ioe) {
		            System.out.println("IOException: " + ioe);
		        }
		        if(pageId!=null && !"".equals(pageId)){
			        try {
			            URL url = new URL(loghatiConf_fedoraserver+"/"+pageId+loghatiConf_fedoraRELS);
			            URLConnection connection = url.openConnection();
			            DataInputStream dis = new DataInputStream(connection.getInputStream());
			            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			            DocumentBuilder db = dbf.newDocumentBuilder();
			            Document doc = db.parse(dis);
			            doc.getDocumentElement().normalize();	            
			            NodeList nodeLst = doc.getElementsByTagName("rdf:Description");
			            Node fstNode = nodeLst.item(0);
			            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			                Element fstElmnt = (Element) fstNode;
			                NodeList fstNm = fstElmnt.getChildNodes();
			                int l = fstNm.getLength();
			                for(int i=0;i<l;i++){
			                	Node fstChildNode = fstNm.item(i);
			                	if (fstChildNode.getNodeType() == Node.ELEMENT_NODE) {
			                		String child = fstChildNode.getNodeName();
			                		if(child.contains("hasPart")){
			                			NamedNodeMap attrs = fstChildNode.getAttributes();
			                			Node attr = attrs.getNamedItem("rdf:resource");
			                			String a = attr.getNodeValue();
			                			String[] v = org.apache.commons.lang.StringUtils.split(a,"/");
			                			parts.add(v[1]);
			                		}
			                	}
			                }
			            }
			            
			        } catch (MalformedURLException me) {
			            System.out.println("MalformedURLException: " + me);
			        } catch (Exception ioe) {
			            System.out.println("IOException: " + ioe);
			        }
		        }
	    		String mtFacet = "";
	    		if("hasFacet_F_MT_en_EN".equals(hasMTEn))
	    			mtFacet = "F_MT_en_EN";
	    		if(!"".equals(hasMTAr))
	    			mtFacet = "F_MT_ar_QA";
	    		String json = "{\"title\":\""+title+"\",\"mtfacet\":\""+mtFacet+"\",\"pages\":[";
	    		int i = 0;
	    		for(int j=0;j< parts.size();j++){
	    			json += "{\""+j+"\":\""+parts.get(j)+"\"}";
	    			if(i<(parts.size()-1))
	    				json+= ",";
	    			i++;
	    		}
	    		json+= "]}";    		
	    		
	            HttpServletRequest request = ServletActionContext.getRequest();
	    		request.setAttribute("respData", callback+"('"+json+"');");		   
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
}
