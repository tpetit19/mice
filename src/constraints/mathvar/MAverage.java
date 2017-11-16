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
import solver.LinExpr;
import solver.MSolver;
import solver.variables.VarI;

/**
 * z = average value of variables in vars
 */

public class MAverage implements ConstraintI {

	VarI[] vars;
	VarI z;
	
	public MAverage(VarI[] vars, VarI z) {
		this.vars = vars;
		this.z = z;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		LinExpr exp = new LinExpr();
		int n = vars.length;
		exp.addTerm(n,z);
		for(int i=0;i<n;i++){
			exp.addTerm(-1, vars[i]);
		}
		solver.add(exp,"=",0.0);
	}

	@Override
	public MSolver getSolver() {
		return z.getSolver();
	}

}

