/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.xmlsh.sh.core.SourceLocation;
import org.xmlsh.sh.grammar.ParseException;
import org.xmlsh.sh.shell.IModule;
import org.xmlsh.sh.shell.ModuleHandle;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.sh.shell.StaticContext;
import org.xmlsh.util.FileUtils;
import org.xmlsh.util.Util;

public class ScriptCommand implements ICommand {

	public enum SourceMode {
		SOURCE, RUN, IMPORT, VALIDATE
	};

	private static Logger mLogger = org.apache.logging.log4j.LogManager
			.getLogger();
	private SourceMode mSourceMode;
	private ModuleHandle mModule; // The module in which the script was located
	private SourceLocation mLocation;
	private ScriptSource mSource;

	// Finalize script command make sure to close
	@Override
	protected void finalize() {
		close();
	}

	public ScriptCommand(ScriptSource source, SourceMode sourceMode,
			SourceLocation location, ModuleHandle moduleHandle)
			throws FileNotFoundException {
		mLogger.entry(source, sourceMode, location, moduleHandle);
		assert (moduleHandle != null);
		mSource = source;
		mSourceMode = sourceMode;
		mLocation = location;
		mModule = moduleHandle;

	}

	@Override
	public int run(Shell shell, String cmd, List<XValue> args)
			throws ThrowException, ParseException, IOException,
			UnimplementedException {

		mLogger.entry(shell, cmd);
		assert (mModule != null);

		try (Reader mScriptStreamSource = getScriptSource()) {

			mLogger.trace("Running {} in {} mode" , cmd , mSourceMode);
			switch (mSourceMode) {
			case SOURCE:
				return shell.runScript(mScriptStreamSource,
						mSource.mScriptName, true).mExitStatus;
			case RUN: {
				try (Shell sh = shell.clone()) {
					if (args != null)
						sh.setArgs(args);
					sh.setArg0(mSource.mScriptName);
					int ret = sh.runScript(mScriptStreamSource,
							mSource.mScriptName, true).mExitStatus;
					return ret;
				}
			}
			case VALIDATE:

				return shell.validateScript(mScriptStreamSource,
						mSource.mScriptName) ? 0 : 1;

			case IMPORT: {
				int ret = shell.runScript(mScriptStreamSource,
						mSource.mScriptName, true).mExitStatus;
				;
				return ret;
			}

			default:
				mLogger.warn("Run mode not implemented: {}", mSourceMode);
				throw new UnimplementedException("Source mode: "
						+ mSourceMode.toString() + " Not implemented");
			}
		}
	}

	private Reader getScriptSource() throws IOException {
		if (mSource.mScriptURL != null)
			return new InputStreamReader(mSource.mScriptURL.openStream(),
					mSource.mEncoding);
		if (mSource.mScriptBody != null)
			return Util.toReader(mSource.mScriptBody);
		throw new IOException("Script body is empty");
	}

	@Override
	public void close() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xmlsh.core.ICommand#getType()
	 */
	@Override
	public CommandType getType() {
		return CommandType.CMD_TYPE_SCRIPT;
	}

	@Override
	public URL getURL() {
		return mSource.mScriptURL; // may be null

	}

	@Override
	public ModuleHandle getModule() {
		return mModule;
	}

	@Override
	public SourceLocation getLocation() {
		return mLocation;
	}

	@Override
	public void setLocation(SourceLocation loc) {
		mLocation = loc;

	}

	public String getScriptName() {
		return mSource.mScriptName;
	}

	@Override
	public void print(PrintWriter w, boolean bExec) {
		w.print(mSource.mScriptName);
	}

}
