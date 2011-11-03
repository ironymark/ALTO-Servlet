package com.diwan.loghati.alto.action;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
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


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;


public class UpdateMetadataAction{
	private String pid;
	String title;
	String creator;
	String language;
	String publisher;
	String type;
	@Action(value="/updatemetadata",results={@Result(name="success",location="/jsp/responsedata.jsp")})

	public String execute() {
		/*
		 * Get the value of form parameter
		 */
		InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream
		("config.xml");
		Properties prop = new Properties();
		String loghatiConf_fedoraserver = "";
		String loghatiConf_fedoraDC = "";
    	try {
    		prop.loadFromXML(is);
    		loghatiConf_fedoraserver = prop.getProperty("fedoraserver");
    		loghatiConf_fedoraDC = prop.getProperty("fedoraDC");
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }

		try {
			URL alto = new URL(loghatiConf_fedoraserver+"/"+pid+loghatiConf_fedoraDC);
			URLConnection connection = alto.openConnection();
			DataInputStream dis = new DataInputStream(connection.getInputStream());			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(dis);
			doc.getDocumentElement().normalize();
			NodeList nodeLst;
			Node fstNode;
			
            nodeLst = doc.getElementsByTagName("title");
            fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNm = fstElmnt.getChildNodes();
                ((Node) fstNm.item(0)).setNodeValue(title);
            }			

            nodeLst = doc.getElementsByTagName("creator");
            fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNm = fstElmnt.getChildNodes();
                ((Node) fstNm.item(0)).setNodeValue(creator);
            }			

            nodeLst = doc.getElementsByTagName("language");
            fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNm = fstElmnt.getChildNodes();
                ((Node) fstNm.item(0)).setNodeValue(language);
            }			

            nodeLst = doc.getElementsByTagName("publisher");
            fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNm = fstElmnt.getChildNodes();
                ((Node) fstNm.item(0)).setNodeValue(publisher);
            }			

            nodeLst = doc.getElementsByTagName("type");
            fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                NodeList fstNm = fstElmnt.getChildNodes();
                ((Node) fstNm.item(0)).setNodeValue(type);
            }			
            
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList altoNode = (NodeList) xpath.evaluate("dc", doc,  XPathConstants.NODESET);
            fstNode = altoNode.item(0);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(fstNode), new StreamResult(writer));
            String s = writer.toString();    
	            
            Authenticator.setDefault(new MyAuthenticator());
			String url =loghatiConf_fedoraserver+"/"+pid+"/datastreams/DC";
			String charset = "UTF-8";
			
			HttpURLConnection urlConnection = (HttpURLConnection)new URL(url).openConnection();
			urlConnection.setConnectTimeout( 20000 );  // long timeout, but not infinite
			urlConnection.setReadTimeout( 20000 );
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true); // Triggers POST.
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(s.getBytes().length));
			urlConnection.setRequestProperty("Content-Type", "text/xml");
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

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getPublisher() {
		return publisher;
	}


	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}
	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}
}
