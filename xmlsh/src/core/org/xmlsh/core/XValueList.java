/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.core;

import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * A list of objects indexable by string or index (1 based for strings)
 */
public class XValueList  extends AbstractList<XValue> implements IXValueContainer<XValueList> 
{
	private  	List<XValue>   mList;

	public XValueList() {
		mList = new LinkedList<>();
	}

	@Override
	public boolean add( XValue value ) {
		return mList.add(value);
	}

	public XValue getAt( int pos) {
		if( pos < 0 || pos > mList.size() )
			return null ;
		return mList.get(pos);
	}

	public int count() { return mList.size() ; }

	@Override
	public boolean isEmpty() { return mList.isEmpty() ; }

	@Override
	public int size() {
		return mList.size();
	}


	// convert string to 0 basd index 
	@Override
	public XValue get(String name) {
		int ind = Util.parseInt(name, 0);
		if( ind <= 0 || ind >= size() )
			return null ;
		return mList.get(ind);
	}

	@Override
	public void removeAll() {
		mList.clear();

	}

	@Override
	public XValue put(String key, XValue value)
	{
		int ind = Util.parseInt(key, 0);
		if( ind <= 0 || ind >= size() )
			throw new ArrayIndexOutOfBoundsException();
		return mList.set(ind, value);

	}

	@Override
	public Set<String> keySet()
	{
		SortedSet<String> set = new TreeSet<>(
				new Comparator<String>() {

					@Override
					public int compare(String o1, String o2)
					{
						return  Integer.valueOf( o1).compareTo
								(Integer.valueOf(o2));
					}}
				);
		for( int i = 0 ; i < mList.size() ; i++ )
			set.add(String.valueOf(i));
		return set;

	}

	@Override
	public Collection<XValue> values()
	{
		return Collections.unmodifiableCollection(mList);
	}

	public void addAll(List<XValue> args)
	{
		mList.addAll(args);
	}

	@Override
	public void serialize(OutputStream out, SerializeOpts opts) throws IOException
	{
		try ( OutputStreamWriter ps = new OutputStreamWriter(out, opts.getInputTextEncoding() ) ){
			ps.write("[");
			String sep = "";
			for( XValue value : mList  ) {
				ps.write( sep );
				ps.flush();
				value.serialize(out, opts);
				ps.write(" ");
				sep = ",";
			}
			ps.write("]");
		} catch (InvalidArgumentException e) {
			Util.wrapIOException(e);
		}

	}

	@Override
	public XValue get(int index)
	{
		return getAt(index);
	}

	@Override
    public boolean isMap()
    {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public boolean isList()
    {
	    // TODO Auto-generated method stub
	    return true;
    }

	@Override
    public boolean isAtomic()
    {
	    // TODO Auto-generated method stub
	    return false;
    }

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