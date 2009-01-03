/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.SynchronizedInputStream;

/*
 * An InputPort represents an input source of data, either Stream (bytes) or XML data
 * 
 */


public class InputPort  implements IPort
{
	
	// Actual input stream
	private InputStream	 mStream;
	private int mRef = 1;
	private String mSystemid;


	public InputPort( InputStream is , String systemid ) throws IOException
	{
		mStream = is;
		mSystemid = systemid;
	}

	/*
	 * Standard input stream - created on first request
	 */
	
	public	synchronized InputStream asInputStream() 
	{
		
		return mStream == null ? null : new SynchronizedInputStream(mStream);
	}

	public synchronized void release() throws IOException {

		if( --mRef <= 0 && mStream != null )
			mStream.close();
		
		
	}
	
	public synchronized Source asSource()
	{
	
		Source s = new StreamSource( asInputStream());
		s.setSystemId(getSystemId());
		return s;
	}
	private String getSystemId() {
		// TODO Auto-generated method stub
		return mSystemid;
	}

	public synchronized XdmNode asXdmNode() throws SaxonApiException
	{
		net.sf.saxon.s9api.DocumentBuilder builder = Shell.getProcessor().newDocumentBuilder();
		return builder.build( asSource() );
	}

	public synchronized void addRef() {
		mRef++;

	}
	
	public synchronized Document asDocument() throws ParserConfigurationException, SAXException, IOException
	{
		
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true); // never forget this!
	    javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
	    return  builder.parse(asInputStream());
	
	}

}



//
//
//Copyright (C) 2008, David A. Lee.
//
//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.
//
//The Original Code is: all this file.
//
//The Initial Developer of the Original Code is David A. Lee
//
//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
//Contributor(s): none.
//