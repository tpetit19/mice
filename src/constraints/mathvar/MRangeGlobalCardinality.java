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
import solver.variables.MIntVar;
import solver.variables.VarI;

/**
 * Extension of global cardinality to intervals.
 * See T. Petit IEEE-ICTAI 2017 paper for details. 
 */

public class MRangeGlobalCardinality implements ConstraintI {

	VarI[] X;
	double[][] intervals;  // array of size twp, represents [v,w) -- w excluded
	int[] mincards;  // mapped with intervals
	int[] maxcards;  // mapped with intervals
	double epsilon;  // MUST be>0
	
	public MRangeGlobalCardinality(VarI[] X, double[][] intervals, int[] mincards, int[] maxcards, double epsilon) {
		if(intervals.length!= mincards.length || intervals.length!= maxcards.length) {
			throw new Error("RangeGlobalCardinality: card array size != interval array size.");
		}
		this.X = X; 
		this.intervals = intervals;
		this.mincards = mincards;
		this.maxcards = maxcards;
		this.epsilon = epsilon; 
	}
	
	public MRangeGlobalCardinality(VarI[] X, int[][] intervals, int[] mincards, int[] maxcards, double epsilon) {
		if(intervals.length!= mincards.length || intervals.length!= maxcards.length) {
			throw new Error("RangeGlobalCardinality: card array size != interval array size.");
		}
		this.X = X; 
		this.intervals = new double[intervals.length][2];
		for(int i=0;i<intervals.length;i++) {
			this.intervals[i][0] = intervals[i][0];
			this.intervals[i][1] = intervals[i][1];
		}
		this.mincards = mincards;
		this.maxcards = maxcards;
		this.epsilon = epsilon; 
	}
	
	@Override
	public void build() {
		MSolver solver = this.getSolver();
		int m = intervals.length;
		int n = X.length;
		MIntVar[] zk = new MIntVar[m];
		for(int i=0; i<m; i++) {
			zk[i] = new MIntVar(0,n,solver); 
			solver.add(new MRangeOccurrence(X,zk[i],intervals[i][0],intervals[i][1],epsilon)); 
			LinExpr e1 = new LinExpr();
			e1.addTerm(1, zk[i]);
			solver.add(e1,">=",mincards[i]);
			LinExpr e2 = new LinExpr();     // normalement inutile on peut contraindre plusieurs fois la meme expression
			e2.addTerm(1, zk[i]);
			solver.add(e2,"<=",maxcards[i]);
		}
	}

	@Override
	public MSolver getSolver() {
		if(X.length>0) {
			return X[0].getSolver();
		}
		return null;
	}

}
