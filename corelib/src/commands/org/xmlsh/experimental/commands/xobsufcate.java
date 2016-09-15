/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.experimental.commands;

import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.xmlsh.core.InputPort;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.io.OutputPort;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.Util;

public class xobsufcate extends XCommand {

	XMLEventFactory	mFactory = XMLEventFactory.newInstance();
	java.util.Random mRand = new java.util.Random();

	@Override
	public int run(List<XValue> args) throws Exception {

		InputPort stdin = null;
		if( args.size() > 0 )
			stdin = getInput( args.get(0));
		else
			stdin = getStdin();
		if( stdin == null )
			throw new InvalidArgumentException("Cannot open input");

		SerializeOpts opts = getSerializeOpts();

		XMLEventReader	reader = stdin.asXMLEventReader(opts);
		OutputPort stdout = getStdout();
		XMLEventWriter  writer = stdout.asXMLEventWriter(opts);

		stdout.setSystemId(stdin.getSystemId());
		XMLEvent e;

		try {
			while( reader.hasNext() ){
				e = (XMLEvent) reader.next();
				e = obsufcate(e);

				writer.add(e);
			}
			// writer.add(reader);
		} finally {
			Util.safeClose(reader);
			Util.safeClose(writer);
		}
		return 0;


	}
	private char rand(int mod)
	{
		int c =  ( (mRand.nextInt()  & 0xFFFF) % mod );
		return (char) c;

	}

	private XMLEvent obsufcate(XMLEvent e) {
		if( e.isCharacters()){
			Characters ch = e.asCharacters();
			if( ch.isWhiteSpace() ) 
				return ch;

			String data = ch.getData();

			StringBuffer sb = new StringBuffer();

			int len = data.length();
			for( int i = 0 ; i < len ; i++ ){
				char c = data.charAt(i);
				if( c >= 'a' && c <= 'z' ){
					c = (char)(rand(26) + 'a');
				} else
					if( c >= 'A' && c <= 'Z' )
					{
						c = (char)(rand(26) + 'A') ;
					}

				sb.append(c);
			}
			return mFactory.createCharacters(sb.toString());



		}
		else
			return e ;




	}



}



//
//
// Copyright (C) 2008-2014    David A. Lee.
//
// The contents of this file are subject to the "Simplified BSD License" (the
// "License");
// you may not use this file except in compliance with the License. You may
// obtain a copy of the
// License at http://www.opensource.org/licenses/bsd-license.php
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations
// under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is David A. Lee
//
// Portions created by (your name) are Copyright (C) (your legal entity). All
// Rights Reserved.
//
// Contributor(s): none.
//
