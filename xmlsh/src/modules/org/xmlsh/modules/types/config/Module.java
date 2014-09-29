package org.xmlsh.modules.types.config;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xmlsh.annotations.Function;
import org.xmlsh.core.AbstractBuiltinFunction;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.XConfiguration;
import org.xmlsh.core.XValue;
import org.xmlsh.core.XValueProperties;
import org.xmlsh.core.XValueSequence;
import org.xmlsh.modules.types.Types;
import org.xmlsh.sh.module.ModuleConfig;
import org.xmlsh.sh.module.PackageModule;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.util.Util;


@org.xmlsh.annotations.Module( name="types.config")
public class Module extends Types {
	static Logger mLogger = LogManager.getLogger();

	public Module(Shell shell,ModuleConfig config) throws CoreException {
		super(shell, config);
		mLogger.entry(config);
	}

	@Function( name="get-section" , names={"section","properties"} )
	public static class getSection extends AbstractBuiltinFunction {
	  @Override
	  public XValue run(Shell shell, List<XValue> args) throws Exception
	  {
	    if( args.size() != 2 ||! args.get(0).isInstanceOf( XConfiguration.class)){
	    	usage(shell, "config section-name");
		    return XValue.nullValue();
	    }
	    
	    XConfiguration conf = args.get(0).asInstanceOf(XConfiguration.class );   
	    String name = args.get(1).toString();
	    return XValue.newXValue(conf.getSection(name));
	  }
	}
	

	@Function( name="get-value" , names={"value" , "property"} )
	public static class getValue extends AbstractBuiltinFunction {
		@Override
	  public XValue run(Shell shell, List<XValue> args) throws Exception
	  {
	    if( args.size() != 3 ||! args.get(0).isInstanceOf( XConfiguration.class)){
	    	usage(shell, "config section-name key-name");
		    return XValue.nullValue();
	    }
	    
	    XConfiguration conf = args.get(0).asInstanceOf(XConfiguration.class );   
	    String section = args.get(1).toString();
	    String name = args.get(2).toString();
	    return XValue.newXValue(conf.getProperty(section, name));
	  }
	}
	
	@Function( "sections" )
	public static class keys extends Types.keys {
	}

}
