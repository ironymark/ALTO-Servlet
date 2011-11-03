package com.diwan.loghati.alto.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class UpdateAltoAction{
	private String pid;
	private String altoUpdate;
	private String facet;
	public String getAltoUpdate() {
		return altoUpdate;
	}


	public void setAltoUpdate(String altoUpdate) {
		this.altoUpdate = altoUpdate;
	}


	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}
	
	@Action(value="/updatealto",results={@Result(name="success",location="/jsp/responsedata.jsp")})
	public String execute() {
		/*
		* Get the value of form parameter
		*/
		InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream
		("config.xml");
		Properties prop = new Properties();
		String loghatiConf_fedoraserver = "";
    	try {
    		prop.loadFromXML(is);
    		loghatiConf_fedoraserver = prop.getProperty("fedoraserver");
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		
        try {
        	Authenticator.setDefault(new MyAuthenticator());
        	URL alto = new URL(loghatiConf_fedoraserver+"/"+pid+"/datastreams/"+facet+"/content");
            URLConnection yahooConnection = alto.openConnection();
            DataInputStream dis = new DataInputStream(yahooConnection.getInputStream());
            Document doc = null;
            if("F_OCR".equals(facet)){
            	//get textlines elements from the updated alto
	            DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db2 = dbf2.newDocumentBuilder();
	            Document doc2 = db2.parse(new ByteArrayInputStream(altoUpdate.getBytes("UTF-8")));
	            NodeList changedTlList = doc2.getElementsByTagName("TextLine");
	            int nChangedTl = changedTlList.getLength();
	            
	            //parse the requested alto from amuser
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	             doc = db.parse(dis);
	            doc.getDocumentElement().normalize();
            
	            NodeList  nodes = doc.getElementsByTagName("TextLine");
	            int l = nodes.getLength();
	            for(int j=0;j<nChangedTl;j++){
	            	Node changedTlNode = changedTlList.item(Integer.valueOf(j));
	            	NamedNodeMap attrs1 = changedTlNode.getAttributes();
	            	Node tmp1 = attrs1.getNamedItem("ID");
	            	String changedTlId = tmp1.getNodeValue();
	            	for(int i=0;i<l;i++){
	    	        	Node node = nodes.item(Integer.valueOf(i));
	    	        	Node pn = node.getParentNode();
	    	        	NamedNodeMap attrs2 = node.getAttributes();
	    	        	Node tmp2 = attrs2.getNamedItem("ID");
	    	        	String id = tmp2.getNodeValue();
	    	        	if(id.equals(changedTlId)){
	    	        		Node nn = doc.importNode(changedTlNode,true);
	    	        		//pn.insertBefore(nn, node);
	    	        		//pn.removeChild(node);
	    		        	pn.replaceChild(nn, node);
	    	        	}
	    	        	
	    	        }
	            }    
            }else{
	            DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db2 = dbf2.newDocumentBuilder();
	            Document doc2 = db2.parse(new ByteArrayInputStream(altoUpdate.getBytes("UTF-8")));
	            NodeList changedTlList = doc2.getElementsByTagName("Sentence");
	            int nChangedTl = changedTlList.getLength();            	
	            
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            doc = db.parse(dis);
	            doc.getDocumentElement().normalize();
            
	            NodeList  nodes = doc.getElementsByTagName("Sentence");
	            int l = nodes.getLength();
	            for(int j=0;j<nChangedTl;j++){
	            	Node changedTlNode = changedTlList.item(Integer.valueOf(j));
	            	NamedNodeMap attrs1 = changedTlNode.getAttributes();
	            	Node tmp1 = attrs1.getNamedItem("Start");
	            	String changedStartId = tmp1.getNodeValue();
	            	Node tmp2 = attrs1.getNamedItem("End");
	            	String changedEndId = tmp2.getNodeValue();
	            	for(int i=0;i<l;i++){
	    	        	Node node = nodes.item(Integer.valueOf(i));
	    	        	Node pn = node.getParentNode();
	    	        	NamedNodeMap attrs2 = node.getAttributes();
	    	        	Node tmp3 = attrs2.getNamedItem("Start");
	    	        	String orgStartId = tmp3.getNodeValue();
	    	        	Node tmp4 = attrs2.getNamedItem("End");
	    	        	String orgEndId = tmp4.getNodeValue();
	    	        	if(orgStartId.equals(changedStartId) && orgEndId.equals(changedEndId)){
	    	        		Node nn = doc.importNode(changedTlNode,true);
	    	        		//pn.insertBefore(nn, node);
	    	        		//pn.removeChild(node);
	    		        	pn.replaceChild(nn, node);
	    	        	}
	    	        	
	    	        }
	            }  	            
            }
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList altoNode = (NodeList) xpath.evaluate("//alto", doc, XPathConstants.NODESET);
            Node fstNode = altoNode.item(0);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(fstNode), new StreamResult(writer));
            String s = writer.toString();
            dis.close();
            
			String url = loghatiConf_fedoraserver+"/"+pid+"/datastreams/"+facet;
			String charset = "UTF-8";
			
			URLConnection urlConnection = new URL(url).openConnection();
			urlConnection.setConnectTimeout( 20000 );  // long timeout, but not infinite
			urlConnection.setReadTimeout( 20000 );
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true); // Triggers POST.
			urlConnection.setRequestProperty("accept-charset", charset);
			urlConnection.setRequestProperty("content-type", "text/xml");
			OutputStreamWriter writer2 = null;
		    writer2 = new OutputStreamWriter(urlConnection.getOutputStream(), charset);

		    writer2.write(s); // Write POST query string (if any needed).
		    writer2.flush();
		    writer2.close();
		    
		    BufferedInputStream reader = new BufferedInputStream( urlConnection.getInputStream() );

		    byte[] cbuf = new byte[ 2048 ];
		    int num;
		    int offset = 0;
		    while ( -1 != (num=reader.read( cbuf )))
		    {
			    offset += num;
		    }		    
		    
        } catch (Exception ioe) {
            System.out.println("IOException: " + ioe);
        }
		
	        return "success";
		 
		}


	public String getFacet() {
		return facet;
	}


	public void setFacet(String facet) {
		this.facet = facet;
	}
	class MyAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            // I haven't checked getRequestingScheme() here, since for NTLM
            // and Negotiate, the usrname and password are all the same.
            System.err.println("Feeding username and password for " + getRequestingScheme());
            return (new PasswordAuthentication("IQRAUser", "!QraUs3r".toCharArray()));
        }
    }
}
