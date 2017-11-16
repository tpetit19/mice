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
import solver.variables.VarI;

/** 
 * Implementation from El Kholi event based decomposition
 * A. O. El-Kholy. Resource Feasibility in Planning. PhD thesis, Imperial College, University of London, 1996.
 */

public class MCumulative_ElKholi implements ConstraintI {

	protected VarI[] start;
	protected VarI[] dur;
	protected VarI[] end; 
	protected VarI capa;
	protected double[] conso; 
	protected double epsilon;

	public MCumulative_ElKholi(VarI[] start, VarI[] dur, VarI[] end, VarI capa, double[] conso, double epsilon) {
		this.start = start;
		this.dur = dur;
		this.end = end;
		this.capa = capa;
		this.conso = conso; 
		this.epsilon = epsilon;
	}

	public MCumulative_ElKholi(MIntVar[] start, MIntVar[] dur, MIntVar[] end, MIntVar capa, double[] conso) {
		this(start,dur,end,capa,conso,1);
	}
	/* (non-Javadoc)
	 * @see constraints.ConstraintI#build()
	 */
	@Override
	public void build() {
		MSolver solver = this.getSolver();
		int n = start.length;
		// start + dur = end
		for(int i=0; i<n; i++) {
			LinExpr e = new LinExpr();
			e.addTerm(1, start[i]);
			e.addTerm(1, dur[i]);
			e.addTerm(-1, end[i]);
			solver.add(e,"=",0);
		}
		// Cumulative
		MBinVar[][] b = new MBinVar[n][]; 
		MBinVar[][] b1  = new MBinVar[n][];
		MBinVar[][] b2  = new MBinVar[n][];
		for(int i=0; i<n; i++) {
			LinExpr sum = new LinExpr(); 
			b[i] = new MBinVar[n-1]; 
			b1[i]  = new MBinVar[n-1];
			b2[i]  = new MBinVar[n-1];
			int j = 0;
			int pos = 0;
			while(j<n) {
				if(i!=j) {
					b[i][pos] = new MBinVar(solver); 
					b1[i][pos] = new MBinVar(solver); 
					b2[i][pos] = new MBinVar(solver); 
					// sum constraint
					sum.addTerm(conso[j], b[i][pos]);
					// bij <=> bij1 and bij2
					LinExpr expr0 = new LinExpr();
					expr0.addTerm(1, b[i][pos]);
					expr0.addTerm(-1, b1[i][pos]);
					solver.add(expr0, "<=", 0);  
					LinExpr expr1 = new LinExpr();
					expr1.addTerm(1, b[i][pos]);
					expr1.addTerm(-1, b2[i][pos]);
					solver.add(expr1, "<=", 0);  
					LinExpr expr2 = new LinExpr();
					expr2.addTerm(1, b[i][pos]);
					expr2.addTerm(-1, b1[i][pos]);
					expr2.addTerm(-1, b2[i][pos]);
					solver.add(expr2, ">=", -1); 
					// b1ij <=> start[j] <= start[i]
					double m = start[j].getMin() - start[i].getMax(); 
					double M = start[j].getMax() - start[i].getMin();
					LinExpr expr3 = new LinExpr();
					expr3.addTerm(1, start[j]);
					expr3.addTerm(-1, start[i]);
					expr3.addTerm(M,b1[i][pos]);
					solver.add(expr3, "<=", M);  
					LinExpr expr4 = new LinExpr();
					expr4.addTerm(1, start[j]);
					expr4.addTerm(-1, start[i]);
					expr4.addTerm(-m+1,b1[i][pos]);
					solver.add(expr4, ">=", epsilon);  
					// b2ij <=> start[i] <= start[j] + dur[j] - epsilon
					//      <=> start[i] - start[j] - dur[j] <= - epsilon
					m = start[i].getMin() - (start[j].getMax() + dur[j].getMax()) + epsilon; 
					M = start[i].getMax() - (start[j].getMin() + dur[j].getMin()) + epsilon;
					//System.out.println("m = "+m+", M = "+M);
					LinExpr expr5 = new LinExpr();
					expr5.addTerm(1, start[i]);
					expr5.addTerm(-1, start[j]);
					expr5.addTerm(-1, dur[j]);
					expr5.addTerm(M, b2[i][pos]);
					solver.add(expr5,"<=",M-epsilon);
					LinExpr expr6 = new LinExpr();
					expr6.addTerm(1, start[i]);
					expr6.addTerm(-1, start[j]);
					expr6.addTerm(-1, dur[j]);
					expr6.addTerm(-m+1,b2[i][pos]);
					solver.add(expr6, ">=", 0);    // epsilon - epsilon
					pos++;
				} 
				j++;
			}
			// sum constraint
			// sum(conso[j].bij) <= capa - conso[i]
			sum.addTerm(-1, capa);
			//System.out.println(sum.toString() + " <= " + (-conso[i]));
			solver.add(sum,"<=",(-conso[i]));
		}
	}
	/* (non-Javadoc)
	 * @see constraints.ConstraintI#getSolver()
	 */
	@Override
	public MSolver getSolver() {
		return capa.getSolver();
	}

}
