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
import solver.engines.MILPSystem;
import solver.variables.MMathIntVar;
import solver.variables.MRealVar;
import solver.variables.VarI;

/**
 * Interdistance constraint. |xi-xj| >= z for all i,j, i!=j 
 * The mean is represented by a rational variable. 
 */

public class MInterDistance implements ConstraintI {

	VarI[] vars;
	VarI z;
	VarI[] absvars; 
	
	public MInterDistance(VarI[] vars, VarI z) {
		this.vars = vars;
		this.z = z;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		int n = vars.length;
		absvars = new VarI[(n-1)*n/2];
		int idx = 0;
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
			double maxv = Math.max(Math.abs(vars[i].getMax()-vars[j].getMin()),Math.abs(vars[j].getMax()-vars[i].getMin()));
			absvars[idx] = new MRealVar(0,maxv,solver);
			idx++;
			}
		}
		idx=0;
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				solver.add(new MDistanceXYZ(vars[i],vars[j],absvars[idx]));
				LinExpr exp = new LinExpr();
				exp.addTerm(1,absvars[idx]);
				exp.addTerm(-1, z);
				solver.add(exp,">=",0);
				idx++;
			}
		}
	}

	@Override
	public MSolver getSolver() {
		return z.getSolver();
	}

	public static void main(String[] args) {
		MSolver solver = new MSolver(MILPSystem.GUROBI); 
		int n = 3; 
		MMathIntVar[] x1 = new MMathIntVar[n];
		MMathIntVar[] x2 = new MMathIntVar[n];
		MMathIntVar z = new MMathIntVar(5,20,solver);
		for(int i=0; i<n; i++) {
			x1[i] = new MMathIntVar(-10,10,solver);
			x2[i] = new MMathIntVar(-40,-20,solver);
		}
		solver.add(new MInterDistance(x1,z));
		solver.add(new MInterDistance(x2,z));
		solver.solve();
		System.out.print("; z = "+z.getValue()+"\nx1 = ");
		for(int i=0; i<n; i++) {
			System.out.print(x1[i].getValue()+" ");
		}
		System.out.print("\nx2 = ");
		for(int i=0; i<n; i++) {
			System.out.print(x2[i].getValue()+" ");
		}
	}
	
}

