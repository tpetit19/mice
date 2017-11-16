/** 
 * MICE 1.0 
 * Copyright (c) 2017 Thierry Petit. All rights reserved. 
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 *   ()-()
 *  _(o o)_  
 *   /\o/\
 * tpetit19@gmail.com
 **/

package solver.engines.text;

/**
 * Log of text engine. 
 */

public class TextLog {

	public String data;    // linear expressions, except domain representation
	public String datadom; // linear expressions used for representing domains
	public String domains; // text representation of domains (should be consistent with datadom)
	public String toString() {
		String s = "";
		if(this.domains != null) {
			s += this.domains;
		}
		s += this.datadom;
		s += this.data;
		return s; 
	}
}
