package com.diwan.loghati.alto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utils {
	static public  byte[] compressData(String str){
		//get bytes
		byte[] bytes = str.getBytes();
 
		/*
		 * To create an object of Deflater, use
		 * 
		 * Deflater()
		 * Constructor of Deflater class.
		 * 
		 * This will create a new compressor with
		 * default compression level. 
		 */
 
		 Deflater deflater = new Deflater();
 
		 /*
		  * Set the input of compressor using,
		  * 
		  * setInput(byte[] b)
		  * method of Deflater class.
		  */
 
		  deflater.setInput(bytes);
 
		  /*
		   * We are done with the input, so say finish using
		   * 
		   * void finish()
		   * method of Deflater class.
		   * 
		   * It ends the compression with the current contents of
		   * the input.
		   */
 
		   deflater.finish();
 
		   /*
		    * At this point, we are done with the input.
		    * Now we will have to create another byte array which can
		    * hold the compressed bytes.
		   */
 
		   ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
 
		   byte[] buffer = new byte[1024];
 
		   /*
		    * Use
		    * 
		    * boolean finished()
		    * method of Deflater class to determine whether
		    * end of compressed data output stream reached.
		    *
		    */
		   while(!deflater.finished())
		   {
		   		/*
		   		 * use
		   		 * int deflate(byte[] buffer) 
		   		 * method to fill the buffer with the compressed data.
		   		 * 
		   		 * This method returns actual number of bytes compressed.
		   		 */
 
		   		 int bytesCompressed = deflater.deflate(buffer);
		   		 bos.write(buffer,0,bytesCompressed);
		   }
 
		   try
		   {
			   //close the output stream
			   bos.close();
		   }
		   catch(IOException ioe)
		   {
		   		System.out.println("Error while closing the stream : " + ioe);
		   }
 
		   //get the compressed byte array from output stream
		   return bos.toByteArray();		
	}
	 public static String extractBytes(byte[] input) throws UnsupportedEncodingException, IOException, DataFormatException
	    {
	        Inflater ifl = new Inflater();   //mainly generate the extraction
	        //df.setLevel(Deflater.BEST_COMPRESSION);
	        ifl.setInput(input);
	 
	        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
	        byte[] buff = new byte[1024];
	        while(!ifl.finished())
	        {
	            int count = ifl.inflate(buff);
	            baos.write(buff, 0, count);
	        }
	        baos.close();
	        byte[] output = baos.toByteArray();
	 
	        System.out.println("Original: "+input.length);
	        System.out.println("Extracted: "+output.length);
	        //System.out.println("Data:");
	        //System.out.println(new String(output));
	        return new String(output);
	    }	
	 public static String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
	 static enum ParseState {
			NORMAL,
			ESCAPE,
			UNICODE_ESCAPE
		    }

		    // convert unicode escapes back to char
	 public static String convertUnicodeEscape(String s) {
			char[] out = new char[s.length()];

			ParseState state = ParseState.NORMAL;
			int j = 0, k = 0, unicode = 0;
			char c = ' ';
			for (int i = 0; i < s.length(); i++) {
			    c = s.charAt(i);
			    if (state == ParseState.ESCAPE) {
				if (c == 'u') {
				    state = ParseState.UNICODE_ESCAPE;
				    unicode = 0;
				}
				else { // we don't care about other escapes
				    out[j++] = '\\';
				    out[j++] = c;
				    state = ParseState.NORMAL;
				}
			    }
			    else if (state == ParseState.UNICODE_ESCAPE) {
				if ((c >= '0') && (c <= '9')) {
				    unicode = (unicode << 4) + c - '0';
				}
				else if ((c >= 'a') && (c <= 'f')) {
				    unicode = (unicode << 4) + 10 + c - 'a';
				}
				else if ((c >= 'A') && (c <= 'F')) {
				    unicode = (unicode << 4) + 10 + c - 'A';
				}
				else {
				    throw new IllegalArgumentException("Malformed unicode escape");
				}
				k++;

				if (k == 4) {
				    out[j++] = (char) unicode;
				    k = 0;
				    state = ParseState.NORMAL;
				}
			    }
			    else if (c == '\\') {
				state = ParseState.ESCAPE;
			    }
			    else {
				out[j++] = c;
			    }
			}

			if (state == ParseState.ESCAPE) {
			    out[j++] = c;
			}

			return new String(out, 0, j);
		    }
}
