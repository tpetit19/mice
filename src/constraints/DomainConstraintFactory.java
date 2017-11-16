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

package constraints;

import java.util.ArrayList;

import constraints.domainvar.adhoc.MGlobalCardinality;
import constraints.domainvar.tablebased.MElement;
import constraints.domainvar.tablebased.MPower;
import constraints.domainvar.tablebased.MTableNeg;
import constraints.domainvar.tablebased.MTablePos;
import constraints.domainvar.tablebased.MTimes;
import constraints.mathvar.MScalar;
import solver.variables.MIntVar;

/**
 *  Factory for constraints set on integer domain variables
 */

public class DomainConstraintFactory {

	// AllDifferent 
	public static ConstraintI alldifferent(MIntVar[] vars) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for(int i=0; i<vars.length; i++) {
			int[] dom = vars[i].getDomain();
			for(int j=0; j<dom.length;j++) {
				if(!l.contains((Integer)dom[j])) {
					l.add((Integer)dom[j]);
				}
			}
		}
		int size = l.size();
		int[] values = new int[size];
		int[] mincards = new int[size];
		int[] maxcards = new int[size];
		for(int i=0;i<size; i++) {
			values[i] = (int) l.get(i);
			mincards[i] = 0;
			maxcards[i] = 1;
		}
		return new MGlobalCardinality(vars,values,mincards,maxcards);
	}

	// Element 
	public static ConstraintI element(MIntVar x, int[] values, MIntVar z) {
		return new MElement(x,values,z); 
	}

	// GlobalCardinality 
	public static ConstraintI globalCardinality(MIntVar[] vars, int[] values, int[] mincards, int[] maxcards) {
		return new MGlobalCardinality(vars,values,mincards,maxcards);
	}

	// Power (z = x^pow)
	public static ConstraintI power(MIntVar x, int pow, MIntVar z) {
		return new MPower(x,pow,z);
	}

	// Scalar product
	public static ConstraintI scalar(MIntVar[] vars, int[] coeffs, int value, String op) {
		return new MScalar(vars, coeffs, value, op);
	}
	
	// Square (z = x^2)
	public static ConstraintI square(MIntVar x, MIntVar z) {
			return new MPower(x,2,z);
	}
	
	// Sum 
	public static ConstraintI sum(MIntVar[] vars, MIntVar z, String op) {
		MIntVar[] svars = new MIntVar[vars.length+1];
		int[] c = new int[vars.length+1];
		for(int i=0;i<vars.length;i++) {
			c[i] = 1;
			svars[i] = vars[i];
		}
		svars[vars.length] = z;
		c[vars.length] = -1;
		return new MScalar(svars,c,0,op);
	}

	// Table 
	public static ConstraintI table(MIntVar[] vars, int[][] tuples, boolean positive) {
		if(positive) {
			return new MTablePos(vars,tuples);
		} else {
			return new MTableNeg(vars,tuples);
		}
	}

	// Times (z = x * y)
	public static ConstraintI times(MIntVar x, MIntVar y, MIntVar z) {
		return new MTimes(x,y,z);
	}

}
