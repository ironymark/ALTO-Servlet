package com.diwan.loghati.alto.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

public class UpdateAnnotationAction{
	private String pid;
	private String annxml;



	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}

	@Action(value="/updateannotation",results={@Result(name="success",location="/jsp/responsedata.jsp")})
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
//			URL ann = new URL(Utils.getConfig(this, "fedoraserver")+"/"+pid+"/datastreams/F_ANNOTATION/content");
//			URLConnection yahooConnection = ann.openConnection();
//			DataInputStream dis = new DataInputStream(yahooConnection.getInputStream());
//			Document doc = null;


//			//parse the requested alto from amuser
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			doc = db.parse(dis);
//			doc.getDocumentElement().normalize();
//			DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
//			DocumentBuilder db2 = dbf2.newDocumentBuilder();
//			Document doc2 = db2.parse(new ByteArrayInputStream(annxml.getBytes("UTF-8")));

//			//updating highlights
//			NodeList dirtyHighlightsList = doc2.getElementsByTagName("Highlights");
//			int nDirtyHighlightsList = dirtyHighlightsList.getLength();
//			if(nDirtyHighlightsList>0){
//			Node dirtyHighlight =  dirtyHighlightsList.item(0);

//			NodeList dirtyAnnList = dirtyHighlight.getChildNodes();
//			int nDirtyAnn = dirtyAnnList.getLength();

//			NodeList  orgHighlightsList = doc.getElementsByTagName("Highlights");
//			int nOrgHighlightsList = orgHighlightsList.getLength();
//			if(nOrgHighlightsList==0){
//			Node nn = doc.importNode(dirtyHighlight,true);
//			doc.appendChild(nn);
//			}else{
//			Node orgHighlight =  orgHighlightsList.item(0);
//			NodeList  orgAnnList = orgHighlight.getChildNodes();
//			int nOrgAnnList = orgAnnList.getLength();
//			for(int j=0;j<nDirtyAnn;j++){
//			boolean bFound = false;
//			Node dirtyAnnNode = dirtyAnnList.item(j);
//			NamedNodeMap attrs1 = dirtyAnnNode.getAttributes();
//			Node tmp1 = attrs1.getNamedItem("AnnotID");
//			String dirtyAnnId = tmp1.getNodeValue();
//			for(int i=0;i<nOrgAnnList;i++){
//			Node node = orgAnnList.item(i);
//			String nodeName = node.getNodeName();
//			if("Annotation".equals(node.getNodeName())){
//			Node pn = node.getParentNode();
//			NamedNodeMap attrs2 = node.getAttributes();
//			Node tmp2 = attrs2.getNamedItem("AnnotID");
//			String orgAnnId = tmp2.getNodeValue();
//			if(orgAnnId.equals(dirtyAnnId)){
//			bFound = true;
//			Node nn = doc.importNode(dirtyAnnNode,true);
//			pn.replaceChild(nn, node);
//			}
//			}			    	        	
//			}
//			if(!bFound){
//			Node nn = doc.importNode(dirtyAnnNode,true);
//			orgHighlight.appendChild(nn);
//			}
//			}    
//			}
//			}
//			//update notes
//			NodeList dirtyNotesList = doc2.getElementsByTagName("Notes");
//			int nDirtyNotesList = dirtyNotesList.getLength();
//			if(nDirtyNotesList>0){
//			Node dirtyNote =  dirtyNotesList.item(0);

//			NodeList dirtyAnnList = dirtyNote.getChildNodes();
//			int nDirtyAnn = dirtyAnnList.getLength();

//			NodeList  orgNotesList = doc.getElementsByTagName("Notes");
//			int nOrgNotesList = orgNotesList.getLength();
//			if(nOrgNotesList==0){
//			Node nn = doc.importNode(dirtyNote,true);
//			doc.appendChild(nn);
//			}else{
//			Node orgNote =  orgNotesList.item(0);
//			NodeList  orgAnnList = orgNote.getChildNodes();
//			int nOrgAnnList = orgAnnList.getLength();
//			for(int j=0;j<nDirtyAnn;j++){
//			boolean bFound = false;
//			Node dirtyAnnNode = dirtyAnnList.item(j);
//			NamedNodeMap attrs1 = dirtyAnnNode.getAttributes();
//			Node tmp1 = attrs1.getNamedItem("AnnotID");
//			String dirtyAnnId = tmp1.getNodeValue();
//			for(int i=0;i<nOrgAnnList;i++){
//			Node node = orgAnnList.item(i);
//			if("Annotation".equals(node.getNodeName())){
//			Node pn = node.getParentNode();
//			NamedNodeMap attrs2 = node.getAttributes();
//			Node tmp2 = attrs2.getNamedItem("AnnotID");
//			String orgAnnId = tmp2.getNodeValue();
//			if(orgAnnId.equals(dirtyAnnId)){
//			bFound = true;
//			Node nn = doc.importNode(dirtyAnnNode,true);
//			pn.replaceChild(nn, node);
//			}
//			}			    	        	
//			}
//			if(!bFound){
//			Node nn = doc.importNode(dirtyAnnNode,true);
//			orgNote.appendChild(nn);
//			}
//			}    
//			}
//			}

//			XPath xpath = XPathFactory.newInstance().newXPath();
//			NodeList annNodeList = (NodeList) xpath.evaluate("//Annotations", doc, XPathConstants.NODESET);
//			Node annNode = annNodeList.item(0);
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			StringWriter writer = new StringWriter();
//			transformer.transform(new DOMSource(annNode), new StreamResult(writer));
//			String s = writer.toString();
//			dis.close();
			//http://loghati.amuser-qstpb.com/fedora/objects/iqra:2883/datastreams/F_ANNOTATION
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

			writer2.write(annxml); // Write POST query string (if any needed).
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

	public String getAnnxml() {
		return annxml;
	}


	public void setAnnxml(String annxml) {
		this.annxml = annxml;
	}
}
