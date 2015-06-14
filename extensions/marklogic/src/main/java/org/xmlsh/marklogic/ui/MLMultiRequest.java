/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.marklogic.ui;

import java.util.List;

public abstract class MLMultiRequest extends MLRequest {

 
	private List<MLRequest>  mRequests;
	public MLMultiRequest(String operation, List<MLRequest> requests) throws InterruptedException {
		super(operation);
		mRequests = requests ;
	}

	@Override
	void run(MLRequestThread requestThread) throws Exception {
		try {
			for( MLRequest r : mRequests )
				r.run( requestThread );
			
		} finally {
			onComplete();
		}
	}
	abstract void onComplete();
	

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