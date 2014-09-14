/**
 * $Id: $
 * $Date: $
 * 
 */

package org.xmlsh.sh.shell;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlsh.core.XClassLoader;
import org.xmlsh.core.XValue;


abstract class AbstractModule implements IModule {

	protected String mName;
	protected boolean bClosed = true;

	protected ClassLoader mClassLoader; // Classloader for this module

	protected URL mHelpURL = null;

	private HashMap<String, Class<?>> mClassCache = new HashMap<String, Class<?>>();

	protected HashMap<String, Boolean> mScriptCache = new HashMap<String, Boolean>();

	// Not static - use derived class
	protected final static Logger mLogger = LogManager.getLogger();


	protected AbstractModule(String name) {
		mName = name;
		bClosed = false ;
	}

	@Override
	public void close() throws IOException {

		mLogger.entry();
		if (bClosed)
			return;

		mClassCache.clear();
		mScriptCache.clear();
		mClassCache = null;
		mScriptCache = null;
		mClassLoader = null;
		bClosed = true ;
		
		mLogger.exit();
		
	}

	protected Class<?> findClass(String className) {

		// Find cached class name even if null
		// This caches failures as well as successes
		// Consider changing to a WeakHashMap<> if this uses up too much memory
		// caching failed lookups
		if (mClassCache.containsKey(className))
			return mClassCache.get(className);

		Class<?> cls = null;
		try {
			cls = Class.forName(className, true, mClassLoader);
		} catch (ClassNotFoundException e) {

		}
		// Store class in cache even if null
		mClassCache.put(className, cls);
		return cls;

	}

	protected Class<?> findClass(String name, List<String> packages) {
		for (String pkg : packages) {
			Class<?> cls = findClass(pkg + "." + name);
			if (cls != null)
				return cls;
		}
		return null;
	}

	protected ClassLoader getClassLoader(List<URL> classpath) {
		if (classpath == null || classpath.size() == 0)
			return getClass().getClassLoader();

		return new XClassLoader(classpath.toArray(new URL[classpath.size()]),
				getClass().getClassLoader());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xmlsh.sh.shell.IModule#getHelpURL()
	 */
	@Override
	public URL getHelpURL() {
		return mHelpURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xmlsh.sh.shell.IModule#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	public URL getResource(String res) {
		/*
		 * Undocumented: When using a classloader to get a resource, then the
		 * name should NOT begin with a "/"
		 */
		if (res.startsWith("/"))
			res = res.substring(1);
		return mClassLoader.getResource(res);
	}

	@Override
	public void onInit(Shell shell, List<XValue> args) throws Exception {
		mLogger.trace("module {} onInit()", getName());

	}

	@Override
	public void onLoad(Shell shell) {
		mLogger.trace("module {} onLoad()", getName());

	}

	protected String toResourceName(String name, String pkg) {
		String resource = pkg.replace('.', '/') + "/" + name;
		return resource;
	}

 
	public String toString() { return getName() ; }
}

//
//
// Copyright (C) 2008-2014 David A. Lee.
//
// The contents of this file are subject to the "Simplified BSD License" (the
// "License");
// you may not use this file except in compliance with the License. You may
// obtain a copy of the
// License at http://www.opensource.org/licenses/bsd-license.php
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations
// under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is David A. Lee
//
// Portions created by (your name) are Copyright (C) (your legal entity). All
// Rights Reserved.
//
// Contributor(s): none.
//
