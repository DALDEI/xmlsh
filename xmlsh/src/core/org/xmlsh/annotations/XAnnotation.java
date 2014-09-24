/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author David A. Lee
 */
/**
 * Meta-annotation (annotations used on other annotations)
 * used for marking all annotations that are
 * part of Jackson package. Can be used for recognizing all
 * Jackson annotations generically, and in future also for
 * passing other generic annotation configuration.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAnnotation {

}



/*
 * Copyright (C) 2008-2014 David A. Lee.
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