/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.Util;

public class ScriptCommand implements ICommand {
	
	private String	mScriptName;
	private InputStream mScript;
	private boolean mSourceMode;
	private File	 mScriptFile; // file for script, may be null if internal script
	
	public ScriptCommand( File script, boolean bSourceMode ) throws FileNotFoundException
	{
		mScript = new FileInputStream(script);
		mScriptName = script.getPath();
		mSourceMode = bSourceMode;
		mScriptFile = script;
		
	}
	
	public ScriptCommand( String script ) throws UnsupportedEncodingException
	{
		mScript = Util.toInputStream(script);
		mSourceMode = true ;
		
	}

	public ScriptCommand(String name , InputStream is, boolean bSourceMode ) {
		mScriptName = name;
		mScript = is;
		mSourceMode = bSourceMode;
		
	}

	public int run(Shell shell, String cmd, List<XValue> args) throws Exception {
		
		try {
			if( mSourceMode ){
				return shell.runScript(mScript);
			} else {
			
				Shell sh = shell.clone();
				if( args != null )
					sh.setArgs(args);
				sh.setArg0(mScriptName);
				int ret = sh.runScript(mScript);
				sh.close();
				
				return ret;
			}
		} finally {
			
			mScript.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xmlsh.core.ICommand#getType()
	 */
	public CommandType getType() {
		return CommandType.CMD_TYPE_SCRIPT ;
	}

	public File getFile() {
		return mScriptFile ; // may be null 
		
	}
	
}
