/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.sh.core;

import org.xmlsh.core.CoreException;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.MutableInteger;

import java.io.IOException;
import java.io.PrintWriter;

public class Assign {
	private boolean	mLocal = false ;
	private	 String		mVariable;
	private String		mOp;		// "=" or "+-" 
	private Word		mValue;		// a single value a=b
	private WordList	mValueList; // a sequence constructor a=(b)
	public Assign(String variable, String op , Word value) {
		if( variable.startsWith("local ")){
			mLocal = true ;
			variable = variable.replaceFirst("local\\s*", "");
		}
		mVariable = variable;
		mOp = op ;
		mValue = value;

	}
	public Assign(String variable, String op , WordList value) {
		if( variable.startsWith("local ")){
			mLocal = true ;
			variable = variable.replaceFirst("local\\s*", "");
		}
		
		mVariable = variable;
		mOp = op;
		mValueList = value;
	}
	public void print(PrintWriter out) {
		if( mLocal )
			out.print("local ");
		out.print(getVariable());
		out.print(mOp);
		if( mValue != null )
			mValue.print(out);
		else
		{
			out.print("(");
			boolean bFirst = true ;
			for( Word w: mValueList ){
				if( ! bFirst )
					out.print(" ");
				w.print(out);
				bFirst = false ;
				
			}
			out.print(")");
				
		}
		out.print( " ");
		
	}
	public String getOp() 
	{
		return mOp ;
	}
	public String getVariable() {
		return mVariable;
	}
	public boolean isLocal () {
		return mLocal ;
	}
	public XValue expand(Shell shell, MutableInteger retVal,SourceLocation loc) throws IOException, CoreException {
		if( mValue != null )
			// Single variables dont expand wildcards
			return mValue.expand(shell, false, false,false,retVal,loc);
		else
		if( mValueList != null )
			// Sequences expand wildcards
			return mValueList.expand(shell, true, false,false,loc);
		else
			return null ;
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
