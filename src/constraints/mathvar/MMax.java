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
import solver.variables.MBinVar;
import solver.variables.VarI;

/**
 * max is equal to the maximum value in vars. 
 */

public class MMax implements ConstraintI {

	protected VarI[] vars;
	protected VarI max;
	
	public MMax(VarI[] vars, VarI max) {
		this.vars = vars;
		this.max = max; 
	}
	
	/* (non-Javadoc)
	 * @see constraints.ConstraintI#build()
	 */
	@Override
	public void build() {
		MSolver solver = this.getSolver();
		int n = vars.length;
		double maxub = Double.MIN_VALUE;
		for(int i=0; i<n; i++) {
			maxub = Math.max(maxub, vars[i].getMax());
		}
		MBinVar[] b = new MBinVar[n];
		LinExpr sum = new LinExpr();
		for(int i=0; i<n; i++) {
			b[i] = new MBinVar(solver);
			// sum of b[i]
			sum.addTerm(1, b[i]);
			// xi <= max
			LinExpr e1 = new LinExpr();
			e1.addTerm(1, vars[i]);
			e1.addTerm(-1, max);
			solver.add(e1,"<=",0);
			// max <= xi + (maxub - xi.getMin())(1 - b[i])
			LinExpr e2 = new LinExpr();
			e2.addTerm(1, max);
			e2.addTerm(-1, vars[i]);
			e2.addTerm((maxub-vars[i].getMin()), b[i]);
			solver.add(e2,"<=",(maxub-vars[i].getMin()));
		}
		solver.add(sum,"=",1);	
	}

	/* (non-Javadoc)
	 * @see constraints.ConstraintI#getSolver()
	 */
	@Override
	public MSolver getSolver() {
		return max.getSolver();
	}

}
