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

/** Power constraint x^pow = z, 
 * Represented by a positive table
 */

public class MPower implements TableI { 

	MIntVar x;
	MIntVar z;
	int pow;

	public MPower(MIntVar x, int pow, MIntVar z) {
		this.x = x;
		this.z = z;
		this.pow = pow; 
	}

	@Override
	public int[][] getTuples() {
		int[] dom1 = x.getDomain();
		int[] zdom = z.getDomain();
		ArrayList<int[]> l = new ArrayList<int[]>();
		for(int i=0; i<dom1.length; i++) {
			for(int j=0; j<zdom.length; j++) {
				if(zdom[j]==Math.pow(dom1[i],pow)) {
					l.add(new int[]{dom1[i],zdom[j]});
				}
			}
		}
		int size = l.size();
		int[][] res = new int[size][2];
		for(int i=0; i<res.length; i++) {
			res[i][0] = l.get(i)[0];
			res[i][1] = l.get(i)[1];
		}
		return res;
	}

	@Override
	public void build() {
		this.getSolver().add(new MTablePos(new MIntVar[]{x,z},getTuples()));
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
		return new MIntVar[]{x,z};
	}
	
}
