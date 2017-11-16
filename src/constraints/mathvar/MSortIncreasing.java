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
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.VarI;

/**
 * Variables in s are sorted in (non strict) increasing order
 */

public class MSortIncreasing extends MSort implements ConstraintI {

	public MSortIncreasing(VarI[] x, VarI[] s, double epsilon) {
		this.solver = x[0].getSolver();
		this.x = x;
		this.s = s;
		this.p = new MIntVar[x.length];
		// Creation of permutation variables
		int n = p.length;
		for(int i=0; i<n; i++) {
			if(n>3000) {   // 3000 is a bit arbitrary
				p[i] = new MMathIntVar(0,x.length-1,solver);
			} else {
				p[i] = new MIntVar(0,x.length-1,solver);
			}
		}
		this.epsilon = epsilon;
	}
	
	public MSortIncreasing(VarI[] x, VarI[] s, MIntVar[] p, double epsilon) { // Permits re-using the permutation 
		this(x,s,epsilon);
		this.p = p;
	}

	public MSortIncreasing(MMathIntVar[] x, MMathIntVar[] s) { // Integer case
		this(x,s,1);
	}

	public MSortIncreasing(MMathIntVar[] x, MIntVar[]p, MMathIntVar[] s) { // Integer case
		this(x,s,1);												   // Permits re-using the permutation 
		this.p = p; 
	}

	protected void sort() { // by default no sort
		int n = p.length;
		for(int i=1; i<n; i++) {
			LinExpr e = new LinExpr();
			e.addTerm(1, s[i-1]);
			e.addTerm(-1, s[i]);
			solver.add(e,"<=",0);
		}
	}
	
	
	
}
