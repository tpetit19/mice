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

import constraints.ConstraintI;
import solver.LinExpr;
import solver.MSolver;
import solver.variables.MIntVar;

/**
 * Global cardinality (open version)
 * Holds iff for each i in [0,values.length[ the number of occurrences occ of values[i] in vars
 * satisfies mincards[i] <= occ <= maxcards[i]
 * Values not in int[] values do not constrain vars
*/

public class MGlobalCardinality implements ConstraintI {

	MIntVar[] vars;
	int[] values;
	int[] mincards;
	int[] maxcards;
	
	public MGlobalCardinality(MIntVar[] vars, int[] values, int[] mincards, int[] maxcards) {
		if(values.length!= mincards.length || values.length!= maxcards.length) {
			throw new Error("GlobalCardinality: card array size != value array size.");
		}
		this.vars = vars;
		this.values = values;
		this.mincards = mincards;
		this.maxcards = maxcards;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		for(int i=0; i<values.length; i++) {
			LinExpr expr = new LinExpr(); 
			int cpt = 0;
			for(int j=0; j<vars.length; j++) {
				int index = vars[j].getIndex(values[i]);
				if(index!=-1) {
					expr.addTerm(1,vars[j].getBools()[index]);
					cpt++;
				}
			}
			if(cpt>0) { // at least one variable can take this value
				solver.add(expr,">=",mincards[i]);
				solver.add(expr,"<=",maxcards[i]);
			}
		}
	}

	@Override
	public MSolver getSolver() {
		return vars[0].getSolver();
	}

}

