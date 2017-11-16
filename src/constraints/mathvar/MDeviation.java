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
import solver.variables.MRealVar;
import solver.variables.VarI;

/**
 * Deviation constraint. 
 * The mean is represented by a rational variable. 
 */

public class MDeviation implements ConstraintI {

	VarI[] vars;
	VarI z;
	VarI s;
	VarI[] absvars; 
	
	public MDeviation(VarI[] vars, VarI z, VarI s) {
		this.vars = vars;
		this.z = z;
		this.s = s;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		solver.add(new MAverage(vars,z));
		int n = vars.length;
		absvars = new VarI[n];
		for(int i=0; i<n; i++) {
			double maxv = Math.max(Math.abs(vars[i].getMax()-z.getMin()),Math.abs(z.getMax()-vars[i].getMin()));
			absvars[i] = new MRealVar(0,maxv,solver);  
		}
		for(int i=0; i<n; i++) {
			solver.add(new MDistanceXYZ(vars[i],z,absvars[i]));
		}
		LinExpr exp = new LinExpr();
		for(int i=0; i<n; i++) {
			exp.addTerm(1,absvars[i]);
		}
		exp.addTerm(-1, s);
		solver.add(exp, "=", 0);
	}

	@Override
	public MSolver getSolver() {
		return z.getSolver();
	}

}

