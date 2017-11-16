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

import constraints.mathvar.MCumulative_ElKholi;
import constraints.mathvar.MDeviation;
import constraints.mathvar.MDistanceXYZ;
import constraints.mathvar.MInterDistance;
import constraints.mathvar.MMax;
import constraints.mathvar.MRangeGlobalCardinality;
import constraints.mathvar.MScalar;
import constraints.mathvar.MSortDecreasing;
import constraints.mathvar.MSortIncreasing;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.VarI;

/**
 *  Factory for constraints set on mathematical variables
 */

public class MathVarConstraintFactory {

	public static ConstraintI distanceXYZ(VarI x, VarI y, VarI z) {
		return new MDistanceXYZ(x,y,z);
	}
	
	public static ConstraintI deviation(VarI[] vars, VarI z, VarI s) {
		return new MDeviation(vars,z,s);
	}
	
	public static ConstraintI cumulative_ElKholi(VarI[] start, VarI[] dur, VarI[] end, VarI capa, double[] conso, double epsilon) {
		return new MCumulative_ElKholi(start,dur,end, capa,conso,epsilon);
	}
	
	public static ConstraintI interdistance(VarI[] vars, VarI z) {
		return new MInterDistance(vars,z);
	}
	
	public static ConstraintI max(VarI[] vars, VarI max) {
		return new MMax(vars, max);
	}
	
	public static ConstraintI rangeGlobalCardinality(VarI[] vars, double[][] intervals, int[] cardmin, int[] cardmax, double epsilon) {
		return new MRangeGlobalCardinality(vars, intervals, cardmin, cardmax, epsilon);
	}
	
	public static ConstraintI rangeAlldifferent(MMathIntVar[] vars, double epsilon) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for(int i=0; i<vars.length; i++){
			min = (int) Math.min(min, vars[i].getMin());
			max = (int) Math.max(max, vars[i].getMax());
		}
		double[][] intervals = new double[max-min+1][2];
		int[] cardmin = new int[max-min+1];
		int[] cardmax = new int[max-min+1];
		for(int i=0; i<cardmin.length; i++) {
			intervals[i][0] = i;
			intervals[i][1] = i+0.01;
			cardmin[i] = 0;
			cardmax[i] = 1;
		}
		return rangeGlobalCardinality(vars, intervals, cardmin, cardmax,epsilon); 
	}
	
	public static ConstraintI scalar(VarI[] vars, int[] coeffs, int value, String op) {
		return new MScalar(vars, coeffs, value, op);
	}
	
	public static ConstraintI sum(VarI[] vars, MIntVar z, String op) {
			VarI[] svars = new VarI[vars.length+1];
			int[] c = new int[vars.length+1];
			for(int i=0;i<vars.length;i++) {
				c[i] = 1;
				svars[i] = vars[i];
			}
			svars[vars.length] = z;
			c[vars.length] = -1;
			return new MScalar(svars,c,0,op);
		}
	
	public static ConstraintI sortIncreasing(MMathIntVar[] x, MMathIntVar[] s) {
		return new MSortIncreasing(x,s);
	}
	
	public static ConstraintI sortDecreasing(MMathIntVar[] x, MMathIntVar[] s) {
		return new MSortDecreasing(x,s);
	}
	
}
