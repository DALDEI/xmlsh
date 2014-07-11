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
import org.xmlsh.util.Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*
 * A Value that evaulates to a "cmd_word" which is either a simple string,
 * or a subprocess expression 
 * 
 */
public abstract class Word {
	
	private		boolean 	bExpand = true ;
	
	public abstract void print( PrintWriter out );


	public XValue expandWords( Shell shell , String word ,  boolean bExpandWords ,boolean bTongs ) throws IOException {
		// if expand word then need to do IFS splitting
		if( bExpandWords && ! bTongs  )
			return new XValue( (String[]) shell.getIFS().split(word).toArray() );
		else
			return new XValue( word);
				
	}

	// Core word expansion
	public List<XValue>  expand(Shell shell,String word ,  boolean bExpandSequences ,  boolean bExpandWild , boolean bExpandWords ,boolean bTongs,  MutableInteger retValue , SourceLocation loc )  throws IOException, CoreException
	{
	   return shell.expand(word ,bExpandSequences , bExpandWild , bExpandWords , bTongs , loc );
   }
	
	public abstract XValue expand(Shell shell,boolean bExpandWild , boolean bExpandWords ,boolean bTongs,  MutableInteger retValue , SourceLocation loc ) throws IOException, CoreException;
	
	public  XValue expand(Shell shell,boolean bExpandWild , boolean bExpandWords , boolean bTongs, SourceLocation loc ) throws IOException, CoreException
	{
		return expand( shell , bExpandWild , bExpandWords , bTongs , null, loc );
	}

	public String expandString(Shell shell, boolean bExpandWild, SourceLocation loc ) throws IOException, CoreException {
		return expand(shell,bExpandWild,false,false,loc).toString();
	}
	
	public List<XValue> expand(Shell shell, boolean bExpandSequences , boolean bExpandWild , boolean bExpandWords , boolean bTongs , SourceLocation loc ) throws IOException, CoreException {
		XValue v = expand( shell , bExpandWild,bExpandWords, bTongs , loc);
		List<XValue> list = new ArrayList<XValue>(1);
		if( v != null )
		   list.add( v );
		if( bExpandSequences)
			list = Util.expandSequences(list);
		return list;
		
	}

	public abstract boolean isEmpty();
	
	public abstract String toString();

	public boolean isExpand() {
		
		return bExpand;
	}
	public void setExpand( boolean expand ){
		bExpand = expand ;
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
