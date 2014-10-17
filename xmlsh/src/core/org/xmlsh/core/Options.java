/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlsh.util.StringPair;
import org.xmlsh.util.Util;



public class Options
{

	/*
	 * A single option is of the form
	 *   [+]short[=long][:[+]]
	 *  Multiple options are separated by ","
	 *  
	 *  [+]   	If option starts with a "+" then it is a boolean option that at runtime can start 
	 *     	  	with a + or -.   for example  cmd +opt  
	 *  short 	The short form of the option.  Typically a single letter
	 *  =long	The long form of the option.  Typically a word
	 *  [:[+]]   If followed by a ":" then the option is required to have a value which is taken from the next arg
	 *   		If followed by a ":+" then the option can be specified multiple times 
	 *  
	 *  
	 *  Examples
	 *  
	 *  a			Single optional option "-a" 
	 *  a=all		Long form accepted either "-a" or "-all"
	 *  +v=verbose	Long or short form may be specified with - or + e.g.  -v  or +verbose
	 *  i:			Option requires a value.  e.g    -i inputfile
	 *  i:+			Option may be specified multiple times with values. e.g.  -i input1 -i input2
	 *  
	 *  
	 *  
	 */


	public static class	OptionDef
	{
		private 		String 		name;		// short name typically 1 letter
		private		String		longname;	// long name/alias
		private 		boolean		expectsArg;	// expects an argument
		private 		boolean 	multiple;	// may occur multiple times
		private 		boolean		flag;	// may be preceeded by + 

		public OptionDef( String name , String longname , boolean arg , boolean multi , boolean plus ){
			setName(name);
			setLongname(longname) ;
			setExpectsArg(arg);
			setMultiple(multi);
			setFlag(plus);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLongname() {
			return longname;
		}

		public void setLongname(String longname) {
			this.longname = longname;
		}

		public boolean isExpectsArg() {
			return expectsArg;
		}

		public void setExpectsArg(boolean expectsArg) {
			this.expectsArg = expectsArg;
		}

		public boolean isMultiple() {
			return multiple;
		}

		public void setMultiple(boolean multiple) {
			this.multiple = multiple;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}


	}




	public static class	OptionValue
	{
		private OptionDef		option;
		private	 boolean		optflag = true; // true if '-' , false if '+'
		private List<XValue>	values = new ArrayList<XValue>(); // in the case of multiple values possible

		OptionValue( OptionDef def , boolean flag ) {
			option = def ;
			optflag = flag ;
		}

		// Set a single value
		void setValue( XValue v  )
		{
			if( ! option.isMultiple() )
				values.clear();
			values.add( v );
		}

		// Add to a multi value
		void addValue( XValue v )
		{
			values.add(v);
		}


		/**
		 * @return the option
		 */
		public OptionDef getOptionDef() {
			return option;
		}
		/**
		 * @return the arg
		 */
		public XValue getValue() {
			if( values.isEmpty())
				return null ;
			else
			  return values.get(0);
		}

		public List<XValue> getValues() {
			return values;
		}

		public boolean getFlag()
		{
			return optflag ;
		}

	}

	private List<OptionDef> mDefs;
	private List<XValue> mRemainingArgs;
	private List<OptionValue> mOptions;
	private boolean	  mDashDash = false ;


	/*
	 * Parse a string list shorthand for options defs
	 * "a,b:,cde:" =>  ("a",false),("b",true),("cde",true)
	 */



	public static List<OptionDef> parseDefs(String sdefs)
	{
		ArrayList<OptionDef>	defs = new ArrayList<OptionDef>();

		String[] adefs = sdefs.trim().split("\\s*,\\s*");
		for( String sdef : adefs ){
			boolean bHasArgs = false ;
			boolean bHasMulti = false ;
			boolean bPlus = false ;

			if( sdef.startsWith("+")){
				bPlus = true ;
				sdef = sdef.substring(1);
			}
			else

				if( sdef.endsWith(":")){
					sdef = sdef.substring(0,sdef.length()-1);
					bHasArgs = true ;
				}
				else
					if( sdef.endsWith(":+")){
						sdef = sdef.substring(0,sdef.length()-2);
						bHasArgs = true ;
						bHasMulti = true ;
					}

			// Check for optional long-name
			// a=longer
			StringPair pair = new StringPair( sdef , '=' );


			if( pair.hasDelim() )
				defs.add( new OptionDef(pair.getLeft() ,  pair.getRight(),bHasArgs,bHasMulti, bPlus));
			else
				defs.add( new OptionDef(sdef ,  null , bHasArgs,bHasMulti, bPlus ));

		}

		return defs;

	}


	public Options( String  options  )
	{
		this( parseDefs(options) );
	}


	public Options( List<OptionDef>  options  )
	{
		mDefs = options;

	}

	private Options( List<OptionDef> opt1 , List<OptionDef> opt2 )
	{
		if( opt2 == null )
			mDefs = opt1; 

		else {

			mDefs = new ArrayList<OptionDef>( opt1.size() + opt2.size());
			addOptionDefs( opt1 );
			addOptionDefs( opt2 );

		}
	}

	public Options(String option_str, List<OptionDef> option_list) 
	{
		this( parseDefs(option_str) , option_list );
	}


	public	List<OptionDef> addOptionDefs( String option_str )
	{
		List<OptionDef> option_list =  parseDefs( option_str );
		addOptionDefs(option_list);
		return option_list ;

	}
	public	void addOptionDefs(  List<OptionDef> option_list )
	{
		// 	mDefs.addAll( option_list ); - Dont duplicate !
		for( OptionDef def : option_list ){
			OptionDef exists = getOptDef( def.getName() );
			if( exists != null )
				mDefs.remove(exists);
			mDefs.add(def);

		}

	}


	public OptionDef	getOptDef(String str)
	{

		if( mDefs == null )
			return null;

		for (OptionDef opt : mDefs) {

			if( Util.isEqual( str , opt.getName() ) ||
					Util.isEqual( str , opt.getLongname() )
					)
				return opt;

		}
		return null;
	}


	public List<OptionValue>	parse(List<XValue> args) throws UnknownOption
	{
		if( mOptions != null )
			return mOptions;



		mOptions = new ArrayList<OptionValue>();




		for ( Iterator<XValue> I = args.iterator() ; I.hasNext() ; ) {
			XValue arg = I.next();

			String sarg = ( arg.isAtomic()  ? arg.toString() : null );

			if( sarg != null &&  (sarg.startsWith("-") || sarg.startsWith("+")) && ! sarg.equals("--") && ! Util.isInt(sarg,true) ){
				String a = sarg.substring(1);
				char flag = sarg.charAt(0);

				OptionDef def = getOptDef(a);
				if( def == null )
					throw new UnknownOption("Unknown option: " + a);

				if( flag == '+' && ! def.isFlag() )
					throw new UnknownOption("Option : " + a + " cannot start with +");


				OptionValue ov = this.getOpt(def);
				boolean bReuse = (ov != null);

				if( ov != null && ! def.isMultiple() )
					throw new UnknownOption("Unexpected multiple use of option: " + arg);
				if( ov == null )
					ov = new OptionValue(def  , flag == '-');
				ov.option = def ;
				if( def.isExpectsArg() ){
					if( !I.hasNext() )
						throw new UnknownOption("Option has no args: " + arg);
					if( def.isMultiple() )
						ov.addValue(I.next());
					else
						ov.setValue( I.next());
				}
				if( ! bReuse )
					mOptions.add(ov);

			} else {

				mRemainingArgs = new ArrayList<XValue>( );

				if( arg.isAtomic() && arg.equals("--") ){
					arg = null;
					mDashDash = true ;
				}
				if( arg != null )
					mRemainingArgs.add(arg);
				while( I.hasNext() )
					mRemainingArgs.add( I.next());


				break;

			}

		}
		return mOptions;

	}

	public List<OptionValue>	getOpts()
	{
		return mOptions ;
	}

	public OptionValue getOpt(OptionDef def) {
		for( OptionValue ov : mOptions ){

			if( ov.option == def )
				return ov;
		}
		return null;
	}


	public OptionValue	getOpt( String opt )
	{
		for( OptionValue ov : mOptions ){
			if( Util.isEqual(opt,ov.getOptionDef().getName())||
					Util.isEqual(opt,ov.getOptionDef().getLongname() )
					)
				return ov;

		}
		return null;
	}
	public boolean		hasOpt( String opt )
	{
		return getOpt(opt) != null;

	}

	public boolean getOptFlag( String opt, boolean defValue )
	{
		OptionValue value = getOpt(opt);
		if( value == null)
			return defValue;
		else
			return value.getFlag();
	}

	public String getOptString( String opt , String defValue )
	{
		OptionValue value = getOpt(opt);
		if( value != null )
			return value.getValue().toString();
		else
			return defValue ;

	}
	public String getOptStringRequired( String opt  ) throws InvalidArgumentException 
	{
		OptionValue value = getOpt(opt);
		if(value != null)
			return value.getValue().toString();

		throw new InvalidArgumentException("Required option: -" + opt );

	}

	public boolean getOptBool( String opt, boolean defValue )
	{
		OptionValue value = getOpt(opt);
		if( value != null )
			try {
				return value.getValue().toBoolean();
			} catch (Exception e) {
				return false ;
			}
		return defValue ;

	}

	public List<XValue> getRemainingArgs()
	{
		if( mRemainingArgs == null )
			mRemainingArgs = new ArrayList<XValue>(0);
		return mRemainingArgs;
	}


	public XValue getOptValue(String arg) {
		OptionValue ov = getOpt(arg);
		if( ov == null )
			return null;
		else
			return ov.getValue();
	}

	public XValue getOptValueRequired(String arg) throws InvalidArgumentException {
		OptionValue ov = getOpt(arg);
		if( ov != null )
			return ov.getValue();
		throw new InvalidArgumentException("Required option: -" + arg );
	}

	public List<XValue> getOptValuesRequired(String arg) throws InvalidArgumentException {
		OptionValue ov = getOpt(arg);
		if( ov != null )
			return ov.values;
		throw new InvalidArgumentException("Required option: -" + arg );
	}


	public List<XValue> getOptValues(String arg) throws InvalidArgumentException {
		OptionValue ov = getOpt(arg);
		if( ov != null )
			return ov.values;
		else
			return null ;
	}




	public boolean hasRemainingArgs() {
		return mRemainingArgs != null && ! mRemainingArgs.isEmpty();
	}
	public double getOptDouble(String opt, double def) {
		return Util.parseDouble(getOptString(opt,""), def);
	}
	public int getOptInt(String opt, int def) {
		return Util.parseInt(getOptString(opt,""), def);
	}

	public long getOptLong(String opt, long l) {
		return Util.parseLong(getOptString(opt,""), l);
	}
	public boolean hasDashDash() { return mDashDash ; }


	/**
	 * @return the defs
	 */
	public List<OptionDef> getOptDefs() {
		return mDefs;
	}
}

//
//
//Copyright (C) 2008-2014    David A. Lee.
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
