/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import org.xml.sax.InputSource;
import org.xmlsh.sh.shell.SerializeOpts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import com.fasterxml.jackson.databind.JsonNode;

/*
 * An InputPort represents an input source of data, either Stream (bytes) or XML data
 * 
 */


public abstract class InputPort  extends IPort
{
	


	

	public	abstract InputStream asInputStream(SerializeOpts opts) throws CoreException ;
	

	public abstract void close() throws CoreException ;
	
	
	public abstract Source asSource(SerializeOpts opts) throws CoreException;
	public abstract InputSource	asInputSource(SerializeOpts opts) throws CoreException;
	

	public abstract XdmNode asXdmNode(SerializeOpts opts) throws CoreException;
	
	public  abstract void copyTo(OutputStream out, SerializeOpts opts ) throws  CoreException, IOException;


	public abstract XMLEventReader asXMLEventReader(SerializeOpts opts) throws CoreException;
	public abstract XMLStreamReader asXMLStreamReader(SerializeOpts opts) throws  CoreException;


	public abstract XdmItem asXdmItem(SerializeOpts serializeOpts) throws CoreException;
	
	

	
	public Reader 	asReader( SerializeOpts serializeOpts ) throws UnsupportedEncodingException, CoreException {
		return new InputStreamReader( asInputStream(serializeOpts) , serializeOpts.getInputTextEncoding()); 
	}
	
	// Default implementation uses a singleton as the input stream
	public IXdmItemInputStream asXdmItemInputStream(SerializeOpts serializeOpts)
			throws CoreException {
		
		return new ValueXdmItemInputStream( asXdmItem( serializeOpts),serializeOpts);
	}


	public abstract JsonNode asJson(SerializeOpts serializeOpts) throws IOException, CoreException ;
	
	
}



//
//
//Copyright (C) 2008-2014    David A. Lee.
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
