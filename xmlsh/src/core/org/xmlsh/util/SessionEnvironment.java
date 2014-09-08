/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionEnvironment extends ManagedObject<SessionEnvironment> {

	private		Map<String, ManagedObject>		mVars;

	@Override
	public void close() throws IOException
	{
		if( mVars != null ){
			for( ManagedObject obj : mVars.values() )
				obj.release();
			mVars.clear();
			mVars = null;
		}

	}

	/*
	 * Get a managed object and adds a reference to it
	 */

	public synchronized ManagedObject	getVar(String key)
	{
		if( mVars == null )
			return null;
		ManagedObject obj = mVars.get(key);
		if( obj != null )
			obj.addRef();
		return obj;
	}

	/*
	 * Sets a Session object and adds a reference
	 */
	public synchronized void setVar( String key , ManagedObject obj )
	{
		if( mVars == null )
			mVars = new HashMap<>();

			obj.addRef();
			mVars.put( key , obj );


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
