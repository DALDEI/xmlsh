/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.sh.shell;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

class Logging {

	
	
	static void configureLogger(boolean bDisabled)
	{
		// Only configure logger if it has not already been configured
		// This avoids adding appenders to embedded invocations of xmlsh
		if( !isLog4JConfigured() )
			configureLogger2(bDisabled);
		
		
	}
	private static void configureLogger2(boolean bDisabled )
	{
		
		if( bDisabled ){
			BasicConfigurator.configure();
			disableLogging();		
			return ;
		
		}
		
		// First look for a properties file 
		String log4jpath = System.getenv("XLOG4JPATH");
		if( log4jpath != null ){
			try {
				PropertyConfigurator.configure( log4jpath );
				return ;
			} catch( Exception e ) 
			{}
		}

		// If none found log to XLOGFILE
		String filename = System.getenv("XLOGFILE");
	
/*	
 	Dont log to $XMLSH by default
 	
 
		// If not found log to XMLSH
		if( filename == null ){
			String xmlsh = System.getenv("XMLSH");
			if( xmlsh != null )
				filename = System.getenv("XMLSH") + "/xmlsh.log" ;
		}
		
*/
		if( filename == null ){
			String home = System.getProperty("user.home");
			if( home == null )
				home = System.getProperty("user.dir");
			if( home != null )
				filename = home + "/xmlsh.log";
			
		}
		
		
		if( filename != null )
		{
			try {
				
				RollingFileAppender rollingFileAppender = new RollingFileAppender( 
						new PatternLayout( "%d %-5r %-5p [%t] %c{2} - %m%n"), 
						filename ,
						true 
				);
				rollingFileAppender.setMaxBackupIndex(5);
				BasicConfigurator.configure( 
						rollingFileAppender
					);
			} catch( Exception e ) {
				
				BasicConfigurator.configure();
				disableLogging();
				return ;
				
			}
		}
		else
			BasicConfigurator.configure();
				
	
	}

	public static void disableLogging()
	{
		@SuppressWarnings("unchecked")
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
		    logger.setLevel( Level.OFF);
		}
	}
	
	/**
	 * DAL: Ripped code from http://wiki.apache.org/logging-log4j/UsefulCode
	 *  
     * Returns true if it appears that log4j have been previously configured. This code 
     * checks to see if there are any appenders defined for log4j which is the 
     * definitive way to tell if log4j is already initialized 
    */ 
	public static boolean isLog4JConfigured() { 
	    Enumeration<?> appenders = Logger.getRootLogger().getAllAppenders(); 
	    if (appenders.hasMoreElements()) { 
	        return true; 
	    } 
	    else { 
	        Enumeration<?> loggers = LogManager.getCurrentLoggers() ; 
	        while (loggers.hasMoreElements()) { 
	            Logger c = (Logger) loggers.nextElement(); 
	            if (c.getAllAppenders().hasMoreElements()) 
	                return true; 
	        } 
	    } 
	    return false; 
	} 
		
	
	
}
