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

import java.util.ArrayList;

import solver.MSolver;
import solver.variables.MIntVar;

/**
 * Product constraint z = x*y.
 * Positive table decomposition. 
 */

public class MTimes implements TableI {

	MIntVar x;
	MIntVar y;
	MIntVar z;
	
	public MTimes(MIntVar x, MIntVar y, MIntVar z) {
		this.x = x;
		this.y = y;
		this.z = z; 
	}
	
	@Override
	public int[][] getTuples() {
		int[] dom1 = x.getDomain();
		int[] dom2 = y.getDomain();
		int[] zdom = z.getDomain();
		ArrayList<int[]> l = new ArrayList<int[]>();
		for(int i=0; i<dom1.length; i++) {
			for(int j=0; j<dom2.length; j++) {
				for(int k=0; k<zdom.length; k++) {
					if(dom1[i]*dom2[j]==zdom[k]) {
						l.add(new int[]{dom1[i],dom2[j],zdom[k]});
					}
				}
			}
		}
		int size = l.size();
		int[][] res = new int[size][3];
		for(int i=0; i<res.length; i++) {
			res[i][0] = l.get(i)[0];
			res[i][1] = l.get(i)[1];
			res[i][2] = l.get(i)[2];
		}
		return res;
	}
	
	@Override
	public void build() {
		this.getSolver().add(new MTablePos(new MIntVar[]{x,y,z},getTuples()));
	}

	@Override
	public MSolver getSolver() {
		return x.getSolver();
	}

	@Override
	public int positive() {
		return TableI.positive;
	}

	@Override
	public MIntVar[] getVars() {
		return new MIntVar[]{x,y,z};
	}

}
