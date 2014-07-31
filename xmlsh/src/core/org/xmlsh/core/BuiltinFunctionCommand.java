/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import org.xmlsh.sh.core.Command;
import org.xmlsh.sh.shell.Shell;

import java.io.PrintWriter;
import java.util.List;

public abstract class BuiltinFunctionCommand extends Command implements IFunction {

	public abstract	XValue	run( Shell shell , List<XValue> args ) throws Exception;
	protected BuiltinFunctionCommand( String name )
	{ 
		super(name);
	}
	
	
	@Override
	public int exec(Shell shell) throws Exception {
		XValue retVal = run( shell , shell.getArgs());
		shell.exec_return(retVal);
		return 0;
	}

	@Override
	public boolean isSimple() {
		
		return true;
	}

	@Override
	public void print(PrintWriter out, boolean bExec) {
		out.print(getName());

	}


	public Command getBody()
	{
		return this ;
	}
	/* (non-Javadoc)
	 * @see org.xmlsh.core.IFunction#argumentEnv(org.xmlsh.core.EvalEnv)
	 */
    @Override
    public EvalEnv argumentEnv(EvalEnv parent)
    {
	   return parent ;
    }
	/* (non-Javadoc)
	 * @see org.xmlsh.core.IFunction#returnEnv(org.xmlsh.core.EvalEnv)
	 */
    @Override
    public EvalEnv returnEnv(EvalEnv parent)
    {
    	return EvalEnv.evalNone() ;
    }
}



//
//
//Copyright (C) 2008-2014 David A. Lee.
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
