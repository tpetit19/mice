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

package constraints.domainvar.tablebased;

import constraints.ConstraintI;
import solver.variables.MIntVar;

/**
 * Interface for table constraints.
 * The boolean values state whether the table is stated by allowed or forbidden tuples. 
 */

public interface TableI extends ConstraintI {

	public final static int positive = 0;
	public final static int negative = 1;
	
	public int[][] getTuples();
	public int positive();
	public MIntVar[] getVars();
	
}
