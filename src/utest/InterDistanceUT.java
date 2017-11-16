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

package utest;

import constraints.ConstraintI;
import constraints.MathVarConstraintFactory;
import constraints.mathvar.MInterDistance;
import constraints.mathvar.MSort;
import solver.MSolver;
import solver.engines.MILPSystem;
import solver.variables.MMathIntVar;

public class InterDistanceUT implements UTestI {

	final static String SYSTEM = MILPSystem.GUROBI;
	
	public static String test1() {
		MSolver solver = new MSolver(MILPSystem.GUROBI); 
		int n = 3; 
		MMathIntVar[] x1 = new MMathIntVar[n];
		MMathIntVar[] x2 = new MMathIntVar[n];
		MMathIntVar z = new MMathIntVar(5,20,solver);
		for(int i=0; i<n; i++) {
			x1[i] = new MMathIntVar(-10,10,solver);
			x2[i] = new MMathIntVar(-40,-20,solver);
		}
		solver.add(new MInterDistance(x1,z));
		solver.add(new MInterDistance(x2,z));
		solver.solve();
		boolean res = true;
		/*System.out.print("; z = "+z.getValue()+"\nx1 = ");
		for(int i=0; i<n; i++) {
			System.out.print(x1[i].getValue()+" ");
		}
		System.out.print("\nx2 = ");
		for(int i=0; i<n; i++) {
			System.out.print(x2[i].getValue()+" ");
		}*/
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				res &= (Math.abs(x1[i].getValue()-x1[j].getValue())>=z.getValue() && Math.abs(x2[i].getValue()-x2[j].getValue())>=z.getValue());
			}
		}
		return res ? "OK" : "ERROR"; 
	}
	
	
	/* (non-Javadoc)
	 * @see utest.UTestI#all()
	 */
	@Override
	public String all() {
		String s = "\nconstraints.mathvar: InterDistance\n";
		s += "\n" + test1();
		return s; 
	}

}
