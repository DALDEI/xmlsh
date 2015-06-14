package org.xmlsh.marklogic;


import java.util.List;

import org.xmlsh.core.Options;
import org.xmlsh.core.XValue;
import org.xmlsh.core.io.OutputPort;
import org.xmlsh.marklogic.util.MLCommand;
import org.xmlsh.marklogic.util.MLUtil;
import org.xmlsh.sh.shell.SerializeOpts;

import com.marklogic.xcc.Request;
import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.types.XdmVariable;

public class invoke extends MLCommand {


	@Override
	public int run(List<XValue> args) throws Exception {
		
		Options opts = new Options("c=connect:,v,t=text,binary",SerializeOpts.getOptionDefs());
		opts.parse(args);
		args = opts.getRemainingArgs();
		boolean asText = opts.hasOpt("t");
		boolean bBinary = opts.hasOpt("binary");
        SerializeOpts serializeOpts = getSerializeOpts(opts);
		
		mContentSource = getConnection(opts);
	
		
		String module = args.remove(0).toString();


        OutputPort out = getStdout();
            mSession = mContentSource.newSession ();
            Request request = mSession.newModuleInvoke(module);
            /*
             *  Add Variables - for now only handle string variables
             */

      		if( opts.hasOpt("v")){
      			// Read pairs from args to set
      			for( int i = 0 ; i < args.size()/2 ; i++ ){
      				
      				
      				
    				String name = args.get(i*2).toString();
    				XValue value = args.get(i*2+1);
    				XdmVariable var = MLUtil.newVariable(name, value, serializeOpts);
    				request.setVariable(var);
      				
      			}
      				
      			
      		}
            ResultSequence rs = mSession.submitRequest (request);

			MLUtil.writeResult(  rs  , out , serializeOpts,asText , bBinary );
           // out.close();
		

	        mSession.close();
	        
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
