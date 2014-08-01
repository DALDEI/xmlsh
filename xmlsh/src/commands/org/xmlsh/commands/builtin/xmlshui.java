/**
 * $Id: colon.java 245 2009-05-29 11:44:01Z daldei $
 * $Date: 2009-05-29 07:44:01 -0400 (Fri, 29 May 2009) $
 *
 */

package org.xmlsh.commands.builtin;

import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.ui.XShell;

import java.util.List;

public class xmlshui extends BuiltinCommand {


	boolean mTopShell = false ;


	public xmlshui()
	{

	}


	/*
	 * Special constructor for a top level shell which doesnt clone
	 */
	public xmlshui( boolean bTopShell )
	{
		mTopShell = bTopShell ;
	}



	@Override
	public int run( List<XValue> args ) throws Exception {

		XShell.run( this.getCurdir() ,  args , getShell().getSerializeOpts().clone() , getShell()  );
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
