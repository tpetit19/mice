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

package constraints.domainvar.adhoc;

import constraints.ReifiedI;
import solver.LinExpr;
import solver.MSolver;
import solver.variables.MBinVar;
import solver.variables.MIntVar;

/** Reified global cardinality (open version)
 * 1 iff for each i in [0,values.length[ the number of occurrences occ of values[i] in vars
 * satisfies mincards[i] <= occ <= maxcards[i]. Values not in int[] values do not constrain vars
 * 0 otherwise.
 * This decomposition is more compact than the generic one
 */

public class MReifiedGlobalCardinality implements ReifiedI {

	MIntVar[] vars;
	int[] values;
	int[] mincards;
	int[] maxcards;
	MBinVar bool; 
	MBinVar[] rlb;
	MBinVar[] rub;

	public MReifiedGlobalCardinality(MIntVar[] vars, int[] values, int[] mincards, int[] maxcards, MBinVar bool) {
		if(values.length!= mincards.length || values.length!= maxcards.length) {
			throw new Error("GlobalCardinality: card array size != value array size.");
		}
		this.vars = vars;
		this.values = values;
		this.mincards = mincards;
		this.maxcards = maxcards;
		this.bool = bool; 
	}

	@SuppressWarnings("unused")
	@Override
	public void build() {
		MSolver solver = getSolver();
		int n = vars.length; 
		// Create O(t) binary variables for lower bounds
		rlb = new MBinVar[values.length];
		for(int i=0; i<values.length; i++) {
			rlb[i] = new MBinVar(solver);
			// sum(b) - mincard[i]rlb[i] <= 0
			LinExpr exp1 = new LinExpr();
			int cpt = 0;
			for(int j=0; j<vars.length; j++) {
				int index = vars[j].getIndex(values[i]);
				if(index!=-1) {
					exp1.addTerm(1,vars[j].getBools()[index]);
					cpt++;
				}
			}
			exp1.addTerm(-mincards[i], rlb[i]);
			solver.add(exp1,">=",0);
			// sum(b) - (n+1)rlb[i] <= mincards[i]-1
			LinExpr exp2 = new LinExpr();
			cpt = 0;
			for(int j=0; j<vars.length; j++) {
				int index = vars[j].getIndex(values[i]);
				if(index!=-1) {
					exp2.addTerm(1,vars[j].getBools()[index]);
					cpt++;
				}
			}
			exp2.addTerm((-n-1), rlb[i]);
			solver.add(exp2,"<=",(mincards[i]-1));
		}
		// Create O(t) binary variables for upper bounds
		rub = new MBinVar[values.length];
		for(int i=0; i<values.length; i++) {
			rub[i] = new MBinVar(solver);
			// sum(b) + maxcard[i]rub[i] >= maxcards[i]+1
			LinExpr exp1 = new LinExpr();
			int cpt = 0;
			for(int j=0; j<vars.length; j++) {
				int index = vars[j].getIndex(values[i]);
				if(index!=-1) {
					exp1.addTerm(1,vars[j].getBools()[index]);
					cpt++;
				}
			}
			exp1.addTerm(maxcards[i]+1, rub[i]);
			solver.add(exp1,">=",maxcards[i]+1);
			// sum(b) + (n)rub[i] <= maccards[i] + n
			LinExpr exp2 = new LinExpr();
			cpt = 0;
			for(int j=0; j<vars.length; j++) {
				int index = vars[j].getIndex(values[i]);
				if(index!=-1) {
					exp2.addTerm(1,vars[j].getBools()[index]);
					cpt++;
				}
			}
			exp2.addTerm(n, rub[i]);
			solver.add(exp2,"<=",(maxcards[i]+n));
		}
		// final expressions
		// sum(all_b) - bool <= 2|t|-1
		int t = values.length;
		LinExpr f1 = new LinExpr(); 
		for(int i=0; i<rlb.length; i++) {
			f1.addTerm(1, rlb[i]);
			f1.addTerm(1, rub[i]);
		}
		f1.addTerm(-1, bool);
		solver.add(f1,"<=",(2*t-1));
		//sum(all_b) - 2|t|bool >=0
		LinExpr f2 = new LinExpr(); 
		for(int i=0; i<rlb.length; i++) {
			f2.addTerm(1, rlb[i]);
			f2.addTerm(1, rub[i]);
		}
		f2.addTerm((-2*t), bool);
		solver.add(f2,">=",0);
	}

	@Override
	public MSolver getSolver() {
		return vars[0].getSolver();
	}

	@Override
	public MBinVar getTruthVar() {
		return bool;
	}

}
