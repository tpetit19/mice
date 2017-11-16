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

package constraints.domainvar.tablebased;

import solver.LinExpr;
import solver.MSolver;
import solver.variables.MBinVar;
import solver.variables.MIntVar;

/**
 * Table constraint stated by forbidden tuples. 
 */

public class MTableNeg implements TableI {

	protected MIntVar[] vars;
	protected int[][] tuples;
	
	public MTableNeg(MIntVar[] vars, int[][] tuples) {
		if(tuples.length==0 || tuples[0].length!=vars.length) {
			throw new Error("Table: tuple set size should be >0 and tuples length to variable array size.");
		}
		this.vars = vars;
		this.tuples = new int[tuples.length][vars.length]; 
		for(int t=0; t<tuples.length; t++) {
			this.tuples[t] = tuples[t].clone();
		}
	}
	
	// TODO: BinMap comme dans tables positives
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		int n = vars.length; 
		for(int t=0; t<tuples.length; t++) {
			int[] indexval = new int[n];
			int i = 0;
			while(i<n) {
				indexval[i] = vars[i].getIndex(tuples[t][i]);
				if(indexval[i]==-1) {
					i = n;
				}
				i++;
			}
			if(i==n) { // add the tuple
				LinExpr sumt = new LinExpr();
				for(int j=0; j<tuples[t].length; j++) {
					sumt.addTerm(1,(MBinVar)vars[j].getBools()[indexval[j]]);
				}
				solver.add(sumt, "<=", vars.length-1, "table:"+t);
			}
		}
	}

	@Override
	public MSolver getSolver() {
		return vars[0].getSolver();
	}

	@Override
	public int[][] getTuples() {
		return tuples;
	}
	
	@Override
	public int positive() {
		return TableI.negative;
	}

	@Override
	public MIntVar[] getVars() {
		return vars;
	}
	
}
