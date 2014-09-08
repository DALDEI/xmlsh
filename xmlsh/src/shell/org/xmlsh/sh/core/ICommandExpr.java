/**
 * $Id: $
 * $Date: $
 * 
 */

package org.xmlsh.sh.core;

import java.io.PrintWriter;

import org.xmlsh.sh.shell.Shell;

public interface ICommandExpr
{

  public boolean isWait();

  public SourceLocation getLocation();

  public boolean hasLocation();

  public void print(PrintWriter out, boolean bExec);

  public int exec(Shell shell) throws Exception;

  // Is a simple command for purposes of throw-on-error
  public boolean isSimple();

  // Default name if none provided
  public String getName();

  public String toString(boolean b);

}

/*
 * Copyright (C) 2008-2012 David A. Lee.
 * 
 * The contents of this file are subject to the "Simplified BSD License" (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.opensource.org/licenses/bsd-license.php 

 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is David A. Lee
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): David A. Lee
 * 
 */