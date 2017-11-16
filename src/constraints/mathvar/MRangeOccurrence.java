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
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.VarI;

/**
 * Extension of occurrence constraint to intervals.
 * See T. Petit IEEE-ICTAI 2017 paper for details. 
 */


public class MRangeOccurrence implements ConstraintI {

	VarI[] X;
	VarI[] ri; 
	VarI[] riless;
	VarI[] rigeq;
	VarI z;
	double v;
	double w; // excluded
	double epsilon; // precision for strict inequalities
	
	public MRangeOccurrence(VarI[] X, MMathIntVar z, int min, int max, double epsilon) {
		this.X = X;
		this.z = z;
		this.v = min;
		this.w = max; 
		this.ri = new VarI[X.length];
		this.riless = new VarI[X.length];
		this.rigeq = new VarI[X.length];
		this.epsilon = epsilon;
	}
	
	public MRangeOccurrence(VarI[] X, MIntVar z, int min, int max, double epsilon) {
		this.X = X;
		this.z = z;
		this.v = min;
		this.w = max; 
		this.ri = new VarI[X.length];
		this.riless = new VarI[X.length];
		this.rigeq = new VarI[X.length];
		this.epsilon = epsilon;
	}
	
	public MRangeOccurrence(VarI[] X, MIntVar z, double min, double max, double epsilon) {
		this.X = X;
		this.z = z;
		this.v = min;
		this.w = max; 
		this.ri = new VarI[X.length];
		this.riless = new VarI[X.length];
		this.rigeq = new VarI[X.length];
		this.epsilon = epsilon;
	}
	
	@Override
	public void build() {
		MSolver solver = this.getSolver();
		int n = X.length;
		LinExpr zsum = new LinExpr(); 
		for(int i=0;i<n;i++) {
			double mi = X[i].getMin();
			double Mi = X[i].getMax();
			if(Mi < v || mi >= w) {
				ri[i] = new MMathIntVar(0, 0, solver); 
				//System.out.println(ri[i].getMin()+","+ri[i].getMax());
			} else {
				if (Mi < w && mi >= v) {
					ri[i] = new MMathIntVar(1, 1, solver); 
					//System.out.println(ri[i].getMin()+","+ri[i].getMax());
				} else {
					double miless = mi - v;
					double Miless = Mi - v;
					double migeq = w - Mi; 
					double Migeq = w - mi; 
					ri[i] = new MBinVar(solver); 
					riless[i] = new MBinVar(solver); 
					rigeq[i] = new MBinVar(solver); 
					LinExpr e1 = new LinExpr();
					e1.addTerm(1, X[i]);
					e1.addTerm(Miless+1, riless[i]);
					solver.add(e1,"<=",v+Miless+1-epsilon);
					LinExpr e2 = new LinExpr();
					e2.addTerm(1, X[i]);
					e2.addTerm(-miless, riless[i]);
					solver.add(e2,">=",v);
					LinExpr e3 = new LinExpr();
					e3.addTerm(-1, X[i]);
					e3.addTerm(Migeq, rigeq[i]);
					solver.add(e3,"<=",-w+Migeq);
					LinExpr e4 = new LinExpr();
					e4.addTerm(-1, X[i]);
					e4.addTerm(-migeq+1, rigeq[i]);
					solver.add(e4,">=",-w+epsilon);
					LinExpr e5 = new LinExpr();
					e5.addTerm(1, ri[i]);
					e5.addTerm(1, riless[i]);
					solver.add(e5,"<=",1);
					LinExpr e6 = new LinExpr();
					e6.addTerm(1, ri[i]);
					e6.addTerm(1, rigeq[i]);
					solver.add(e6,"<=",1);
					LinExpr sum = new LinExpr();
					sum.addTerm(1, ri[i]);
					sum.addTerm(1, riless[i]);
					sum.addTerm(1, rigeq[i]);
					solver.add(sum,">=",1);
				}
			}
			zsum.addTerm(1, ri[i]);
 		}
		zsum.addTerm(-1, this.z);
		//System.out.println(zsum.toString());
		solver.add(zsum,"=",0);
	}

	@Override
	public MSolver getSolver() {
		return z.getSolver();
	}

}
