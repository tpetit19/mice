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

package solver;

import java.util.ArrayList;

import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.VarI;

/**
 * MICE linear expressions.
 */

public class LinExpr {

	ArrayList<Object[]> expr;
	String system;

	public LinExpr() {
		expr = new ArrayList<Object[]>();   // List of pairs {variable, coefficient}
	}
	public void addTerm(double coef, VarI var) {
		Object[] o = new Object[]{var,coef};
		expr.add(o);
	}
	public void addTerm(int coef, VarI var) {
		addTerm((double) coef,var);
	}
	public Object get() {
		return expr;
	}

	public MIntVar[] getIntVars() { // useful for text model, local search...
		int nb = 0;
		int size = expr.size();
		for(int i=0; i< size; i++) {
			if(expr.get(i)[0] instanceof MIntVar) {
				nb ++;
			}
		}
		MIntVar[] res = new MIntVar[nb];
		int idx = 0;
		for(int i=0; i< size; i++) {
			if(expr.get(i)[0] instanceof MIntVar) {
				res[idx] = (MIntVar) expr.get(i)[0];
				idx ++;
			}
		}
		return res; 
	}
	public String toString() {
		String s = "";
		int size = expr.size();
		for(int i=0; i< size; i++) {
			double d = ((double) expr.get(i)[1]);
			String de = "";
			if(d!=1 && d!=-1) {
				if(d>=0) {
					de+=d+"*";
				} else {
					de+=(-d)+"*";
				}
			}
			if(i>0) { 
				if (d>=0) {
					s += " + " + (de);
				} else { 
					s += " - " + (de);
				}
			} else {
				if (d>=0) {
					s += (de);

				} else { 
					s += " - " + (de);
				}
			}
			s += ((VarI) expr.get(i)[0]).getName();
		}
		return s; 
	}

	public LinExpr clone() {
		LinExpr clone = new LinExpr();
		int size = expr.size();
		for(int i=0; i< size; i++) {
			VarI var = ((VarI) expr.get(i)[0]);
			double d = ((double) expr.get(i)[1]);
			clone.addTerm(d, var);
		}
		return clone; 
	}

	public MBinVar reify(MSolver solver, String op, double constant, double epsilon) {  
		MBinVar r = new MBinVar(solver); 
		if(op=="=") { 
			MBinVar b1 = reify(solver,"<=",constant,epsilon);
			MBinVar b2 = reify(solver,">=",constant,epsilon);
			// r = b1 and b2
			LinExpr e1 = new LinExpr();
			e1.addTerm(1, r);
			e1.addTerm(-1, b1);
			solver.add(e1,"<=",0);
			LinExpr e2 = new LinExpr();
			e2.addTerm(1, r);
			e2.addTerm(-1, b2);
			solver.add(e2,"<=",0);
			LinExpr e3 = new LinExpr();
			e3.addTerm(-1, b1);
			e3.addTerm(-1, b2);
			e3.addTerm(1, r);
			solver.add(e3,">=",-1);
		} else {
			LinExpr e1 = new LinExpr();
			LinExpr e2 = new LinExpr();
			int size = expr.size();
			double m = 0;
			double M = 0;
			for(int i=0; i< size; i++) {
				VarI var = ((VarI) expr.get(i)[0]);
				double d = ((double) expr.get(i)[1]);
				if(op==">=") {
					d = -d; 
				}
				e1.addTerm(d, var);
				e2.addTerm(d, var);
				double x1 = var.getMin()*d;
				double x2 = var.getMin()*(-d);
				double x3 = var.getMax()*d;
				double x4 = var.getMax()*(-d);
				m += Math.min(Math.min(Math.min(x1, x2),x3),x4);
				M += Math.max(Math.max(Math.max(x1, x2),x3),x4);
			}
			if(op==">=") {
				constant = -constant;
			}
			e1.addTerm(M, r);
			solver.add(e1,"<=",M+constant);
			e2.addTerm(-m+1, r);
			solver.add(e2,">=",constant+Math.abs(epsilon));
		}
		return r; 
	}

}
