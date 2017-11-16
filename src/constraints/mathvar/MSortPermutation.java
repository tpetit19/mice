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

package constraints.mathvar;

import constraints.ConstraintI;
import solver.variables.MIntVar;
import solver.variables.VarI;

/**
 *  The ordering of variables in s is simply guided by the permutation vars
 */

public class MSortPermutation extends MSort implements ConstraintI {
	
	public MSortPermutation(VarI[] x, VarI[] s, MIntVar[] p, double epsilon) {
		this.solver = x[0].getSolver();
		this.x = x;
		this.s = s;
		this.p = p;
		this.epsilon = epsilon;
	}

}
