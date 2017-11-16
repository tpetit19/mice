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

package constraints.logical;

import constraints.ConstraintI;
import constraints.ReifiedI;
import constraints.domainvar.tablebased.MTablePos;
import solver.MSolver;
import solver.variables.MBinVar;

/** 
 * Reified OR
 */

public class MReifiedOR implements ReifiedI {

	ReifiedI c1;
	ReifiedI c2;
	MBinVar bool; 
	
	public MReifiedOR(ReifiedI c1, ReifiedI c2, MBinVar bool) {
		this.c1 = c1;
		this.c2 = c2;
		this.bool = bool;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		MBinVar[] b = new MBinVar[]{c1.getTruthVar(),c2.getTruthVar(),bool};
		int[] t1 = new int[]{0,0,0};
		int[] t2 = new int[]{0,1,1};
		int[] t3 = new int[]{1,0,1};
		int[] t4 = new int[]{1,1,1};
		int[][] tuples = new int[][]{t1,t2,t3,t4};
		ConstraintI table = new MTablePos(b,tuples);
		solver.add(c1);  
		solver.add(c2);
		solver.add(table);
	}

	@Override
	public MSolver getSolver() {
		return c1.getSolver();
	}

	@Override
	public MBinVar getTruthVar() {
		return bool;
	}

}

