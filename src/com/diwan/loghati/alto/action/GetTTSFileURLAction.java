package com.diwan.loghati.alto.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.diwan.loghati.alto.Utils;

public class GetTTSFileURLAction {
	String ttsText;
	String userID;
	int balance;
	int channels;
	int frequency;
	int gain;
	String lang;
	int pitch;
    int speed;
    int timber;
    String voice;
    int volume;
    String arabic;
    private String callback;
	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getTtsText() {
		return ttsText;
	}

	public void setTtsText(String ttsText) {
		this.ttsText = ttsText;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getGain() {
		return gain;
	}

	public void setGain(int gain) {
		this.gain = gain;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getTimber() {
		return timber;
	}

	public void setTimber(int timber) {
		this.timber = timber;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	@Action(value="/getttsurl",results={@Result(name="success",location="/jsp/responsedata.jsp")})
	public String execute() {

	try {
		if("true".equals(arabic)){
			String tmp = Utils.convertUnicodeEscape(ttsText);
			ttsText = tmp;
		}
	    InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream
		("config.xml");
		Properties prop = new Properties();
		String ttsWavFolder = "";
    	try {
    		prop.loadFromXML(is);
    		ttsWavFolder = prop.getProperty("TTSWAVFOLDER");
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }		
    	String fileName = Utils.MD5(ttsText)+".wav"; 
	    File file = new File(ttsWavFolder+"//"+fileName);//File.createTempFile("D1Temp", ".wav", new File(ttsWavFolder));
	    if(!file.exists()){				
			String charset = "UTF-8";
			URLConnection urlConnection = new URL(Utils.getConfig(this,"ttsserver")+"TTS").openConnection();
			urlConnection.setConnectTimeout( 20000 );  // long timeout, but not infinite
			urlConnection.setReadTimeout( 20000 );
			urlConnection.setUseCaches(false);
			urlConnection.setDoOutput(true); // Triggers POST.
			urlConnection.setRequestProperty("accept-charset", charset);
			urlConnection.setRequestProperty("content-type", "text/xml");
	
			OutputStreamWriter writer = null;
			    writer = new OutputStreamWriter(urlConnection.getOutputStream(), charset);
	            StringBuilder sb = new StringBuilder();
	            sb.append("<?xml version='1.0' encoding='UTF-8'?>");
	            sb.append("\r\n");
	            sb.append("<REQUEST_TTS>");
	            sb.append("\r\n");
	            sb.append("<USER_ID></USER_ID>");
	            sb.append("\r\n");
	            sb.append("<REQUEST_MODE>file</REQUEST_MODE>");
	            sb.append("\r\n");
	            sb.append("<TEXT><![CDATA["+ttsText+"]]></TEXT>");
	            sb.append("\r\n");
	            sb.append("<BALANCE>"+balance+"</BALANCE>");
	            sb.append("\r\n");
	            sb.append("<CHANNELS>"+channels+"</CHANNELS>");
	            sb.append("\r\n");
	            sb.append("<CODING>linear</CODING>");
	            sb.append("\r\n");
	            sb.append("<DELAY></DELAY>");
	            sb.append("\r\n");
	            sb.append("<FREQUENCY>"+frequency+"</FREQUENCY>");
	            sb.append("\r\n");
	            sb.append("<GAIN>"+gain+"</GAIN>");
	            sb.append("\r\n");
	            sb.append("<LANGUAGE>"+lang+"</LANGUAGE>");
	            sb.append("\r\n");
	            sb.append("<PITCH>"+pitch+"</PITCH>");
	            sb.append("\r\n");
	            sb.append("<SPEED>"+speed+"</SPEED>");
	            sb.append("\r\n");
	            sb.append("<TIMBRE>"+timber+"</TIMBRE>");
	            sb.append("\r\n");
	            sb.append("<VOICE>"+voice+"</VOICE>");
	            sb.append("\r\n");
	            sb.append("<VOLUME>"+volume+"</VOLUME>");
	            sb.append("\r\n");
	            sb.append("</REQUEST_TTS>");		    
			    
			    writer.write(sb.toString()); // Write POST query string (if any needed).
			    writer.flush();
			    writer.close();
	
			    BufferedInputStream reader = new BufferedInputStream( urlConnection.getInputStream() );
	
			    byte[] cbuf = new byte[ 2048 ];
			    int num;
			    
			    FileOutputStream out = new FileOutputStream(file);
			    
			    while ( -1 != (num=reader.read( cbuf )))
			    {
				    out.write(cbuf,0, num);
			    }		    
			    
			    out.flush();
			    out.close();
			}
		    HttpServletRequest request = ServletActionContext.getRequest();
		    request.setAttribute("respData", callback+"('"+fileName+"');");
		} catch (Exception e) {
		    StackTraceElement[] errorStack = e.getStackTrace();
            HttpServletRequest request = ServletActionContext.getRequest();
            request.setAttribute("respData", callback+"('"+Utils.getConfig(this,"ttsserver")+" "+e.getMessage()+errorStack[1].toString()+"');");
			e.printStackTrace();
		}
		return "success";
		
	}

	public String getArabic() {
		return arabic;
	}

	public void setArabic(String arabic) {
		this.arabic = arabic;
	}


}
