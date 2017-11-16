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
 * Reified NEG
 */

public class MReifiedNEG implements ReifiedI {

	ReifiedI c1;
	MBinVar bool; 
	
	public MReifiedNEG(ReifiedI c1, MBinVar bool) {
		this.c1 = c1;
		this.bool = bool;
	}
	
	@Override
	public void build() {
		MSolver solver = getSolver();
		MBinVar[] b = new MBinVar[]{c1.getTruthVar(),bool};
		int[] t1 = new int[]{0,1};
		int[] t2 = new int[]{1,0};
		int[][] tuples = new int[][]{t1,t2};
		ConstraintI table = new MTablePos(b,tuples);
		solver.add(c1);  
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

