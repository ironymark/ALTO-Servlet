package com.diwan.loghati.alto.action;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.diwan.loghati.alto.Utils;

public class GetPartOfAltoAction{
	private String pid;
	private String facet;
	private String part;
	private String callback;
	private String textBlkIndex;
	private String debug;
	
	public String getDebug() {
		return debug;
	}


	public void setDebug(String debug) {
		this.debug = debug;
	}


	public String getCallback() {
		return callback;
	}


	public void setCallback(String callback) {
		this.callback = callback;
	}


	@Action(value="/getpartofalto",results={@Result(name="success",location="/jsp/responsedata.jsp")})
	public String execute() {
		String theString="";
		DataInputStream dis = null;
		if(pid!=null && facet!=null){
	        try {
	        	if("true".equals(debug)){
	        		File inFile = new File("c:\\temp\\altoxml\\alto_panamapacificint00moor_Pg_1.xml");
	        		dis = new DataInputStream(new FileInputStream(inFile));
	        		  	        		
	        	}else{
	        		URL alto = new URL(Utils.getConfig(this, "fedoraserver")+"/"+pid+"/datastreams/"+facet+"/content");
	        		URLConnection connection = alto.openConnection();
	        		dis = new DataInputStream(connection.getInputStream());
	        	}
	            //StringWriter writer = new StringWriter();
	            //IOUtils.copy(dis, writer, "UTF-8");
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document doc = db.parse(dis);
	            doc.getDocumentElement().normalize();
                HttpServletRequest request;
                XPath xpath = XPathFactory.newInstance().newXPath();
                StringWriter writer = new StringWriter();
	            if("getlayout".equals(part)){
		            Utils.removeAll(doc, Node.ELEMENT_NODE,"TextLine" );
		            Utils.removeAll(doc, Node.ELEMENT_NODE,"Sentence" );
		            doc.getDocumentElement().normalize();	            
		            NodeList nodes = (NodeList) xpath.evaluate("//alto", doc, XPathConstants.NODESET);
		            Node fstNode = nodes.item(0);
		            Transformer transformer = TransformerFactory.newInstance().newTransformer();
		            transformer.transform(new DOMSource(fstNode), new StreamResult(writer));
	            }else if("gettextblocks".equals(part)){
	            	NodeList nodes = (NodeList) xpath.evaluate("//TextBlock", doc, XPathConstants.NODESET);	            	
		    	    Node fstNode = nodes.item(Integer.valueOf(textBlkIndex));
		    	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    	    transformer.transform(new DOMSource(fstNode), new StreamResult(writer));
	            }
                theString = writer.toString().replaceAll("[\n\r]", "");
                theString = JSONObject.escape(theString);
                theString = StringUtils.replace(theString, "  ", "");
//              theString = StringUtils.replace(theString, "'", "\\'");
                request = ServletActionContext.getRequest();
                request.setAttribute("respData", callback+"(\""+theString+"\");");
	            dis.close();
	            
	        } catch (MalformedURLException me) {
                HttpServletRequest request = ServletActionContext.getRequest();
                request.setAttribute("respData", callback+"('"+me+"');");
	            System.out.println("MalformedURLException: " + me);
	        } catch (Exception ioe) {
                HttpServletRequest request = ServletActionContext.getRequest();
                request.setAttribute("respData", callback+"('error');");
	            System.out.println("IOException: " + ioe);
	        }
		}    		
		
		return "success";
		 
		}
		 
		 
		public void destroy() {
		 
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


		public String getPart() {
			return part;
		}


		public void setPart(String part) {
			this.part = part;
		}


		public String getTextBlkIndex() {
			return textBlkIndex;
		}


		public void setTextBlkIndex(String textBlkIndex) {
			this.textBlkIndex = textBlkIndex;
		}		
}
