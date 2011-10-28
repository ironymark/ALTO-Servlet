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


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.diwan.loghati.alto.Utils;


public class UpdateBookmarkAction{
	private String pid;
	private String annxml;
	
	

	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}
	
	@Action(value="/updatebookmark",results={@Result(name="success",location="/jsp/responsedata.jsp")})
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
			Node newBookmarkNode = null;
			if(!annxml.isEmpty()){
				DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
				DocumentBuilder db2 = dbf2.newDocumentBuilder();
				Document newDataDoc = db2.parse(new ByteArrayInputStream(annxml.getBytes("UTF-8")));        	
				NodeList newDataBookmark = newDataDoc.getElementsByTagName("Bookmarks");
				newBookmarkNode = newDataBookmark.item(0);
			}
			//get the xml
			URL alto = new URL(Utils.getConfig(this, "fedoraserver")+"/"+pid+"/datastreams/F_ANNOTATION/content");
			URLConnection conn = alto.openConnection();
			DataInputStream dis = new DataInputStream(conn.getInputStream());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(dis);
			doc.getDocumentElement().normalize();
			//remove old bookmark
			Utils.removeAll(doc, Node.ELEMENT_NODE,"Bookmarks" );          
			if(newBookmarkNode!=null){
				NodeList pageNodeList = doc.getElementsByTagName("Page");
				Node pageNode = pageNodeList.item(0);
				Node nn = doc.importNode(newBookmarkNode,true);
				pageNode.appendChild(nn);            
			}
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList annNodeList = (NodeList) xpath.evaluate("//Page", doc, XPathConstants.NODESET);
			Node annNode = annNodeList.item(0);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(annNode), new StreamResult(writer));
			String newXml = writer.toString();
			dis.close();    		

			Authenticator.setDefault(new MyAuthenticator());
			String url = loghatiConf_fedoraserver+"/"+pid+"/datastreams/F_ANNOTATION";
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

			writer2.write(newXml); // Write POST query string (if any needed).
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


	class MyAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            // I haven't checked getRequestingScheme() here, since for NTLM
            // and Negotiate, the usrname and password are all the same.
            System.err.println("Feeding username and password for " + getRequestingScheme());
            return (new PasswordAuthentication("IQRAUser", "!QraUs3r".toCharArray()));
        }
    }
	public String getAnnxml() {
		return annxml;
	}


	public void setAnnxml(String annxml) {
		this.annxml = annxml;
	}
}
