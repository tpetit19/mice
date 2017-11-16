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
import solver.variables.MRealVar;
import solver.variables.VarI;

/**
 * z = |x-y|
 */

public class MDistanceXYZ implements ConstraintI {

	VarI x;
	VarI y;
	VarI z;

	public MDistanceXYZ(VarI varx, VarI vary, VarI varz) { 
		this.x = varx;
		this.y = vary;
		this.z = varz; 
	}

	@Override
	public void build() {
		MSolver solver = this.getSolver();
		// Bounds of absolute variable domain
		double minv = Math.min(x.getMin(), y.getMin());
		double maxv = Math.max(x.getMax(), y.getMax());
		double A = maxv-minv;
		//System.out.println("M = "+A);
		double a = 0.0;
		if(x.getMax()<y.getMin()) {
			a = y.getMin() - x.getMax();
		}
		if(x.getMin()>y.getMax()) {
			a = x.getMin() - y.getMax();
		}
		//System.out.println("m = "+a);
		// Bounds of diff x - y
		double d = x.getMin() - y.getMax();
		double D = x.getMax() - y.getMin();
		//double d = minv-maxv;
		//double D = maxv-minv;
		//System.out.println("L = "+ d + ", U = " + D);
		// diff = x - y
		MRealVar diff = new MRealVar(d,D,solver);
		LinExpr expr0 = new LinExpr();
		expr0.addTerm(1, x);
		expr0.addTerm(-1, y);
		solver.add(expr0, "=", diff);  
		// Simple cases
		if(D<=0) {
			//System.out.println("ici");
			LinExpr expr = new LinExpr();
			expr.addTerm(-1,diff);
			solver.add(expr, "=", z);
			return;
		}

		if(d>=0) {
			LinExpr expr = new LinExpr();
			expr.addTerm(1,diff);
			solver.add(expr, "=", z, "");
			return;
		}
		// d < 0 < D
		MRealVar diffplus = new MRealVar(0,D,solver);
		MRealVar diffminus = new MRealVar(0,Math.abs(d),solver);
		MBinVar bin = new MBinVar(solver);
		// a <= z <= A
		LinExpr zb = new LinExpr(); 
		zb.addTerm(1, z);
		solver.add(zb,">=",a);
		solver.add(zb,"<=",A);
		// diff = diffplus - diffminus
		LinExpr expr1 = new LinExpr();
		expr1.addTerm(1, diff);
		expr1.addTerm(-1, diffplus);
		expr1.addTerm(1, diffminus);
		solver.add(expr1, "=", 0);  
		// diffplus <= bin*D
		LinExpr expr2 = new LinExpr();
		expr2.addTerm(1,diffplus);
		expr2.addTerm(-D, bin);
		solver.add(expr2, "<=", 0);  
		// diffminus <= |d|*(1-bin)    <==>    diffminus + |d|*bin <= |d|
		LinExpr expr3 = new LinExpr();
		expr3.addTerm(1,diffminus);
		expr3.addTerm(Math.abs(d), bin);
		solver.add(expr3, "<=", Math.abs(d));  
		// final expression abs = diffplus + diffminus
		LinExpr absexpr = new LinExpr();
		absexpr.addTerm(1,z);
		absexpr.addTerm(-1,diffplus);
		absexpr.addTerm(-1,diffminus);
		solver.add(absexpr, "=", 0, "");  
	}

	@Override
	public MSolver getSolver() {
		return x.getSolver();
	}

}

