/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.internal.commands;

import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.io.OutputPort;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.XFile;


public class xpwd extends XCommand
{




	@Override
	public int run(  List<XValue> args )	throws Exception
	{
		Options opts = new Options(	SerializeOpts.getOptionDefs() );
		opts.parse(args);
		// args = opts.getRemainingArgs();

		XFile file = new XFile(getCurdir());


		OutputPort stdout = getStdout();
		SerializeOpts serializeOpts = getSerializeOpts(opts);
		XMLStreamWriter writer = stdout.asXMLStreamWriter(serializeOpts);

		writer.writeStartDocument();

		file.serialize(writer,false,false, false);

		writer.writeEndDocument();
		writer.close();

		stdout.writeSequenceTerminator(serializeOpts);
		// stdout.close();




		return 0;

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
