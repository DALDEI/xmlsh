/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.trans.XPathException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlsh.sh.shell.SerializeOpts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javanet.staxutils.ContentHandlerToXMLEventWriter;
import javanet.staxutils.StAXEventContentHandler;
import javanet.staxutils.XMLEventStreamWriter;
import javanet.staxutils.events.EventFactory;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

/*
 * An OutputPort represents an output sync of data, either Stream (bytes) or XML data
 * 
 */




public class XMLEventOutputPort extends OutputPort
{
	private		XMLEventWriter mWriter;
	private 	SerializeOpts mOpts;




	/*
	 * A special output stream that turns every xmlevent into text
	 */

	private class XMLEventOutputStream extends OutputStream
	{
		private XMLEventFactory mFactory = EventFactory.newInstance();
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int b) throws IOException {
			byte bytes[] = new byte[] {(byte)b };
			write( bytes );
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] b, int off, int len) throws IOException {

			XMLEvent event = mFactory.createCharacters(new String(b,off,len,mOpts.getOutputXmlEncoding()));

			try {
				mWriter.add(event);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}



		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		@Override
		public void write(byte[] b) throws IOException {
			write(b,0,b.length);
		}


	}




	public XMLEventOutputPort( XMLEventWriter writer , SerializeOpts opts ) 
	{
		mWriter = writer;
		mOpts = opts;
	}




	/*
	 * Standard input stream - created on first request
	 */

	@Override
	public	synchronized OutputStream asOutputStream(SerializeOpts serializeOpts) 
	{
		return new XMLEventOutputStream();
	}

	@Override
	public synchronized void flush() throws IOException
	{

		try {
			mWriter.flush();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}



	@Override
	public synchronized void close() throws IOException {
		try {
			mWriter.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}



	public synchronized PrintStream asPrintStream()
	{
		return new PrintStream(asOutputStream(mOpts));
	}

	@Override
	public synchronized Destination asDestination(SerializeOpts opts) throws CoreException
	{

		DefaultHandler handler;
		try {
			handler = new ContentHandlerToXMLEventWriter( asXMLEventWriter(opts) );
		} catch (XMLStreamException e) {
			throw new CoreException(e);
		}
		Destination dest = new SAXDestination( handler );
		return dest ;


	}





	@Override
	public synchronized PrintWriter asPrintWriter(SerializeOpts opts) throws UnsupportedEncodingException {
		return new PrintWriter( 		
				new OutputStreamWriter(asOutputStream(mOpts) , 
						opts.getOutputTextEncoding() ));
	}




	@Override
	public synchronized void writeSequenceSeperator(SerializeOpts opts) throws IOException, InvalidArgumentException
	{



	}

	@Override
	public void writeSequenceTerminator(SerializeOpts opts) throws IOException {


	}




	@Override
	public XMLStreamWriter asXMLStreamWriter(SerializeOpts opts) throws XMLStreamException {
		return new XMLEventStreamWriter(mWriter);

	}




	@Override
	public XMLEventWriter asXMLEventWriter(SerializeOpts opts) throws InvalidArgumentException, XMLStreamException {
		return mWriter;

	}

	/* (non-Javadoc)
	 * @see org.xmlsh.core.OutputPort#asXdmItemOutputStream(org.xmlsh.sh.shell.SerializeOpts)
	 */
	@Override
	public IXdmItemOutputStream asXdmItemOutputStream(SerializeOpts opts) throws CoreException {

		return new DestinationXdmValueOutputStream( asDestination(opts) );

	}


	/* (non-Javadoc)
	 * @see org.xmlsh.core.OutputPort#asContentHandler(org.xmlsh.sh.shell.SerializeOpts)
	 */
	@Override
	public ContentHandler asContentHandler(SerializeOpts opts) throws XPathException {


		return new StAXEventContentHandler( mWriter );

	}



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
