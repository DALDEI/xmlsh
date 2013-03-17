/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands.json;

import java.util.List;

import net.sf.saxon.trans.XPathException;
import org.xmlsh.core.BuiltinFunctionCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.Shell;

import com.jayway.jsonpath.JsonModel;

public class json extends BuiltinFunctionCommand {

	public json()
	{
		super("array");
	}
	
	@Override
	public XValue run(Shell shell, List<XValue> args) throws XPathException {

		StringBuffer sb = new StringBuffer();
		for( XValue arg : args ){
			if( sb.length() > 0 )
				sb.append(" ");
			sb.append( arg.toString());
		}
		
		JsonModel model = JsonModel.create( sb.toString() );
		return new XValue( model );
	}

}



//
//
//Copyright (C) 2008,2009,2010,2011,2012 David A. Lee.
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