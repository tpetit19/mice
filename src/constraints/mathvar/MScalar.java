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
import solver.variables.VarI;

/** Facility for stating scalar products
 * op in {"=", "<=", ">="}
 */

public class MScalar implements ConstraintI {

	VarI[] vars;
	int[] coeffs;
	int value;
	String op; 
	/*
	public MScalar(MIntVar[] vars, int[] coeffs, int value, String op) {
		this.vars = vars;
		this.coeffs = coeffs;
		this.value = value;
		this.op = op; 
	}
	
	public MScalar(MMathIntVar[] vars, int[] coeffs, int value, String op) {
		this.vars = vars;
		this.coeffs = coeffs;
		this.value = value;
		this.op = op; 
	}
	
	public MScalar(MRealVar[] vars, int[] coeffs, int value, String op) {
		this.vars = vars;
		this.coeffs = coeffs;
		this.value = value;
		this.op = op; 
	}
	*/
	public MScalar(VarI[] vars, int[] coeffs, int value, String op) {
		this.vars = vars;
		this.coeffs = coeffs;
		this.value = value;
		this.op = op; 
	}
	
	
	@Override
	public void build() {
		int n = vars.length; 
		MSolver solver = vars[0].getSolver();
		LinExpr sum = new LinExpr();
		for(int i=0; i<n; i++) {
			sum.addTerm(coeffs[i], vars[i]);
		}
		solver.add(sum, op, value);
	}

	@Override
	public MSolver getSolver() {
		return vars[0].getSolver();
	}

}
