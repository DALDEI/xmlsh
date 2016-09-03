/**
 * $Id: echo.java 88 2008-11-27 17:06:00Z daldei $
 * $Date: 2008-11-27 12:06:00 -0500 (Thu, 27 Nov 2008) $
 *
 */

package org.xmlsh.internal.commands;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.io.OutputPort;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.Util;

public class xurlencode extends XCommand {

  @Override
  public int run(List<XValue> args) throws Exception {

    Options opts = new Options("n,p=port:,q=query",
        SerializeOpts.getOptionDefs());
    opts.parse(args);

    boolean nolf = opts.hasOpt("n");
    String port = opts.getOptString("p", null);
    boolean query = opts.hasOpt("q");

    OutputPort stdout = port != null ? getEnv().getOutputPort(port)
        : getEnv().getStdout();

    if(stdout == null)
      throw new InvalidArgumentException("Output port not found: " + port);

    SerializeOpts serializeOpts = getSerializeOpts(opts);

    OutputStream out = stdout.asOutputStream(serializeOpts);

    args = Util.expandSequences(opts.getRemainingArgs());

    // If arguments behave like echo and copy args to output stream
    if(args.size() > 0) {
      if(query && (args.size() % 2 != 0)) {
        usage();
        return -1;
      }

      args = Util.expandSequences(args);
      boolean bFirst = true;
      boolean bOdd = false;
      for(XValue arg : args) {

        if(!bFirst) {
          if(query && bOdd)
            out.write('=');
          else
            out.write('&');
        }

        bFirst = false;
        String value = arg.toString();
        value = URLEncoder.encode(value, serializeOpts.getInputXmlEncoding());
        out.write(value.getBytes(serializeOpts.getOutputTextEncoding()));
        bOdd = !bOdd;
      }
      if(!nolf)
        out.write(Util.getNewlineBytes(serializeOpts));
    }
    // Else copy input to out using text mode exclusively
    else {

      InputStream is = getStdin().asInputStream(serializeOpts);
      String data = Util.readString(is, serializeOpts.getInputTextEncoding());
      String value = URLEncoder.encode(data,
          serializeOpts.getInputTextEncoding());
      out.write(value.getBytes(serializeOpts.getOutputTextEncoding()));
      is.close();

    }

    out.close();

    return 0;
  }
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
