/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.util;

import org.xmlsh.types.ITypeConverter;
import java.util.Iterator;

public class TypeConvertingIterator<S,D> implements Iterator<D>
{

  private final Iterator<S> iter;
  private final ITypeConverter<S,D> converter;
  
  TypeConvertingIterator( final Iterator<S> iter , final ITypeConverter<S,D> converter ){
  
    this.iter = iter;
    this.converter = converter ;
  }
  
  @Override
  public boolean hasNext()
  {
    return iter.hasNext();
  }

  @Override
  public D next()
  {
    return converter.convert(iter.next());
  }

  @Override
  public void remove()
  {
    iter.remove();
    
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