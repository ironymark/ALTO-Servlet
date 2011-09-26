package com.diwan.loghati.alto.action;

import java.io.DataInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.diwan.loghati.alto.Utils;

public class GetBooksListAction {
    private String callback;

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @Action(value = "/getbookslist", results = { @Result(name = "success", location = "/jsp/responsedata.jsp") })
    public String execute() {
        try {
            URL alto = new URL(Utils.getConfig(this, "fedoraserver") + "?pid=true&title=true&terms=&query=label%3Dbook+ownerId~*1*&maxResults=100&resultFormat=xml");
            URLConnection connection = alto.openConnection();
            DataInputStream dis = new DataInputStream(connection.getInputStream());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(dis);
            doc.getDocumentElement().normalize();
            String json = "";
            int i = 0;

            NodeList nodeLst = doc.getElementsByTagName("resultList");
            Node fstNode = nodeLst.item(0);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rlElmnt = (Element) fstNode;
                NodeList ofNodeLst = rlElmnt.getElementsByTagName("objectFields");
                int l = ofNodeLst.getLength();
                for (int j = 0; j < l; j++) {
                    Element ofElmnt = (Element) ofNodeLst.item(j);
                    if (ofElmnt.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList pidElmntLst = ofElmnt.getElementsByTagName("pid");
                        Element pidElmnt = (Element) pidElmntLst.item(0);
                        NodeList pidLst = pidElmnt.getChildNodes();
                        NodeList titleElmntLst = ofElmnt.getElementsByTagName("title");
                        Element titleElmnt = (Element) titleElmntLst.item(0);
                        NodeList title = titleElmnt.getChildNodes();
                        if (!json.equals(""))
                            json += ",";
                        json += "{\"" + i + "\":";
                        json += "{\"pid\":\"" + ((Node) pidLst.item(0)).getNodeValue() + "\",";
                        json += "\"title\":\"" + ((Node) title.item(0)).getNodeValue() + "\"}}";
                        i++;
                    }
                }
                json = "{\"books\":[" + json + "]}";
                HttpServletRequest request = ServletActionContext.getRequest();
                request.setAttribute("respData", callback + "('" + json + "');");
            }
        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException: " + me);
        } catch (Exception ioe) {
            System.out.println("IOException: " + ioe);
        }

        return "success";

    }
}
