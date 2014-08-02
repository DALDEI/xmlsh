/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.sh.core;

import org.xmlsh.core.CoreException;
import org.xmlsh.core.EvalEnv;
import org.xmlsh.core.EvalFlag;
import org.xmlsh.core.XValue;
import org.xmlsh.core.XValueList;
import org.xmlsh.sh.shell.ParseResult;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.types.TypeFamily;

import java.io.IOException;
import java.io.PrintWriter;

public class BraceWord extends Word
{

	private WordList	mWords;

	public BraceWord(WordList w)
	{
		mWords = w;
	}

	@Override
	public void print(PrintWriter out)
	{
		out.print("{");
		String sep="";
		for( Word w : mWords ) {
			w.print(out);
			out.print(sep);
			sep="";
		}
		out.print("}");
	}


	@Override
	public boolean isEmpty()
	{
		return mWords == null || mWords.isEmpty();
	}



	@Override
	String getSimpleName()
	{
		return isEmpty() ? "{}" : mWords.get(0).getSimpleName() + "...";
	}

	/* (non-Javadoc)
	 * @see org.xmlsh.sh.core.Word#evalEnv(org.xmlsh.core.EvalEnv)
	 */
	@Override
	public EvalEnv evalEnv(EvalEnv parent)
	{
		return parent.withFlagsMasked( EvalFlag.preserveValueFlags());
	}

	@Override
	protected ParseResult expandToResult(Shell shell, EvalEnv env, SourceLocation loc, ParseResult result) throws IOException,
	CoreException
	{
		if( mWords.isEmpty() )
			return result  ;
		if( mWords.size() == 1 )
			return mWords.get(0).expandToResult(shell, evalEnv(env), loc, result);
		
		XValueList list = new XValueList();
		for( Word w : mWords ) {
			XValue xv = w.expand(shell, evalEnv(env), loc);
			list.add(xv);
		}
		result.add( new XValue( TypeFamily.XTYPE , list ) );
		return result ;

	}


}

/*
 * Copyright (C) 2008-2012 David A. Lee.
 * 
 * The contents of this file are subject to the "Simplified BSD License" (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.opensource.org/licenses/bsd-license.php
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: all this file.
 * 
 * The Initial Developer of the Original Code is David A. Lee
 * 
 * Portions created by (your name) are Copyright (C) (your legal entity). All
 * Rights Reserved.
 * 
 * Contributor(s): David A. Lee
 */