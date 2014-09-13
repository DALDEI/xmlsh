/**
 * $Id: ScriptCommand.java 27 2008-07-05 14:30:33Z daldei $
 * $Date: 2008-07-05 10:30:33 -0400 (Sat, 05 Jul 2008) $
 *
 */

package org.xmlsh.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.sf.saxon.trans.UncheckedXPathException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlsh.sh.core.ICommandExpr;
import org.xmlsh.sh.core.SourceLocation;
import org.xmlsh.sh.shell.IModule;
import org.xmlsh.sh.shell.ModuleHandle;
import org.xmlsh.sh.shell.ScriptModule;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.sh.shell.StaticContext;

public class FunctionCommand implements ICommand {

	String mName;
	ICommandExpr mFunction;
	SourceLocation mLocation;
	ModuleHandle mModule;
	boolean bClosed = false;
	private static Logger mLogger = LogManager.getLogger();

	public FunctionCommand(ModuleHandle moduleHandle, String name,
			ICommandExpr func, SourceLocation loc) {
		mLogger.entry(moduleHandle, name, func, loc);
		assert (moduleHandle != null);

		mName = name;
		mFunction = func;
		mLocation = loc;
		mModule = moduleHandle;
	}

	@Override
	public URL getURL() throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int run(Shell shell, String cmd, List<XValue> args) throws Exception {
		mLogger.entry(shell, cmd);

		int ret = shell
				.execFunctionAsCommand(mName, mFunction, mLocation, args);
		if (ret == 0)
			ret = shell.getReturnValueAsExitStatus(ret);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xmlsh.core.ICommand#getType()
	 */
	@Override
	public CommandType getType() {
		return CommandType.CMD_TYPE_FUNCTION;
	}

	@Override
	public ModuleHandle getModule() {
		return mModule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xmlsh.core.ICommand#close()
	 */
	@Override
	public void close() throws IOException {
		mLogger.entry();
		if (bClosed)
			return;

		mModule.release();
		bClosed = true;
	}

	@Override
	public SourceLocation getLocation() {
		return mLocation;
	}

	@Override
	public void setLocation(SourceLocation loc) {
		mLocation = loc;
	}

	@Override
	public void print(PrintWriter w, boolean bExec) {
		w.print(mName);
	}

}
