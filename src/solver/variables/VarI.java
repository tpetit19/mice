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

package solver.variables;

import solver.MSolver;

/**
 * MICE variables interface. 
 */

public interface VarI {
	public Object get();
	public MSolver getSolver();
	public String getName();
	public double getMin();
	public double getMax();
}