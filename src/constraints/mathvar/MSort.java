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
import constraints.DomainConstraintFactory;
import constraints.MathVarConstraintFactory;
import solver.LinExpr;
import solver.MSolver;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.VarI;

/**
 * General sort constraint
 * s is a sorted arrangement of x. Mapping indexes are given by the integer variable set p.
 * values in p are all different and for all i, p[i] = j => s[i] = x[j]
 */

public abstract class MSort implements ConstraintI {

	protected VarI[] x;
	protected MIntVar[] p;
	protected VarI[] s; 
	protected MSolver solver;
	protected double epsilon;

	@Override
	public void build() {
		int n = p.length; 
		// for all i, p[i] = j => s[i] = x[j]
		MBinVar[][] left = new MBinVar[n][n];
		MBinVar[][] negright = new MBinVar[n][n];
		MBinVar[][] right = new MBinVar[n][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				LinExpr leftexp = new LinExpr();
				leftexp.addTerm(1, p[i]);
				left[i][j] = leftexp.reify(solver, "=", j, 1); // Integer variables: epsilon = 1
				LinExpr rightExp = new LinExpr();
				rightExp.addTerm(1,s[i]);
				rightExp.addTerm(-1, x[j]);
				right[i][j] = rightExp.reify(solver, "=", 0, epsilon);
				// Logical implication
				// (1) negation of left[i][j]
				negright[i][j] = new MBinVar(solver);
				LinExpr negExpr = new LinExpr();
				negExpr.addTerm(1, negright[i][j]);
				negExpr.addTerm(1, left[i][j]);
				solver.add(negExpr,"=",1);
				// (2) logical OR
				LinExpr e = new LinExpr();
				e.addTerm(1, negright[i][j]);
				e.addTerm(1, right[i][j]);
				solver.add(e, ">=", 1);
			}
		}
		// The nexr call states the type of sort: non decreasing, decreasing, none...  
		sort();
		// alldifferent(p)
		if(p[0] instanceof MMathIntVar) {
			solver.add(MathVarConstraintFactory.rangeAlldifferent((MMathIntVar[])p, 0.0001));
		} else {
			solver.add(DomainConstraintFactory.alldifferent(p));
		}

	}

	protected void sort() { // by default no sort
	}
	
	public MIntVar[] getP() {
		return p;
	}

	/* (non-Javadoc)
	 * @see constraints.ConstraintI#getSolver()
	 */
	@Override
	public MSolver getSolver() {
		return solver;
	}

}
