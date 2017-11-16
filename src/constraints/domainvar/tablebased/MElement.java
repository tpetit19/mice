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

import constraints.ConstraintI;
import solver.MSolver;
import solver.variables.MIntVar;

/** 
 * Binary element constraint.
 * Table based decomposition.
 */

public class MElement implements ConstraintI {

	MSolver solver;
	MIntVar x,z;
	int[] values; 
	
	public MElement(MIntVar x, int[] values, MIntVar z) {
		this.x = x;
		this.z = z;
		this.values = values;
		solver = x.getSolver();
	}
	
	protected static boolean member(int elt, int[] array) {  // O(log(n)) as domains are sorted
		int lb = 0;
		int ub = array.length-1;
		while(lb<ub) {
			int index = (lb+ub)/2;
			if(array[index]==elt) {
				return true;
			}
			if(array[index]>elt) {
				ub = index-1;
			} else { // array[index]<elt
				lb = index+1;
			}
		}
		return array.length>0 && array[lb]==elt;
	}
	
	public int[][] getTuples(int[] xdom, int[] zdom) {
		ArrayList<int[]> l = new ArrayList<int[]>();
		for(int i=0; i<values.length; i++) {
			if(member(i,xdom) && member(values[i],zdom)) {
				l.add(new int[]{i,values[i]});
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
		this.getSolver().add(new MTablePos(new MIntVar[]{x,z},getTuples(x.getDomain(),z.getDomain())));
	}

	@Override
	public MSolver getSolver() {
		return solver;
	}

	
	
}

