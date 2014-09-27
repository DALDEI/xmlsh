package org.xmlsh.sh.module;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlsh.core.ICommand;
import org.xmlsh.core.IFunctionExpr;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.FileUtils;


/*
 * Top level root module for the shell
 */
public class RootModule extends Module {
 
	static Logger mLogger = LogManager.getLogger();
	
	// TEMP
	private static RootModule _instance = new RootModule();
	
	public static Module getInstance() {
		return _instance ;
	}
	
	private RootModule() {
		super( new ModuleConfig("internal" , "root",null,  SerializeOpts.defaultOpts ));
	}
	
	@Override
	public String describe() {
		return getName();
	}
	@Override
	public ICommand getCommand(String name) throws IOException {
		return null;
	}

	@Override
	public IFunctionExpr getFunction(String name) {
		return null;
	}

	@Override
	public boolean hasHelp(String name) {
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public URL findResource(String res) {
		mLogger.trace("TODO: maybe look in class's package for resource {}",res);
		return(null);
	}

	public static boolean isEqual(IModule m) {
		return m == _instance ;
	}



}