/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.marklogic.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.xmlsh.marklogic.util.MLUtil;
import org.xmlsh.sh.shell.SerializeOpts;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.types.XdmVariable;

public class MLGetRequest extends MLQueryRequest
{
	private File mOutput ;
	@SuppressWarnings("unused")
	private SerializeOpts mSerializeOpts;
	
	MLGetRequest( String url , final File output ,final SerializeOpts sopts ) throws Exception 
	{
		super("Getting " + url +  " ...",
				"declare variable $doc external;" +
				"fn:doc($doc)"  , 
				  new XdmVariable[] { 	MLUtil.newVariable("doc",url)} , null );
		
		mOutput = output;
		mSerializeOpts  = sopts ;
	}
	
	@Override
	void onComplete(ResultSequence rs) throws Exception {
		OutputStream os =  new FileOutputStream( mOutput) ;
		if( rs.hasNext() ){
			rs.next().writeTo(os);
		}
		os.close();
	}

}



/*
 * Copyright (C) 2008-2014 David A. Lee.
 * 
 * The contents of this file are subject to the "Simplified BSD License" (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.opensource.org/licenses/bsd-license.php 

 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is David A. Lee
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): David A. Lee
 * 
 */