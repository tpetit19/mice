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
import constraints.mathvar.MSort;
import solver.MSolver;
import solver.engines.MILPSystem;
import solver.variables.MMathIntVar;

public class SortednessUT implements UTestI {

	final static String SYSTEM = MILPSystem.GUROBI;
	
	public static String test1() {
		MSolver solver = new MSolver(SYSTEM);
		int n = 5; 
		MMathIntVar[] x = new MMathIntVar[n];
		MMathIntVar[] s = new MMathIntVar[n];
		for(int i=0; i<n; i++) {
			x[i] = new MMathIntVar(i,n*2,solver);
			s[i] = new MMathIntVar(0,n*2,solver);
			//System.out.println("x["+i+"] = ["+x[i].getMin() +", " + x[i].getMax() +"], s["+i+"] = ["+s[i].getMin() + ", "+s[i].getMax() +"]");
		}
		ConstraintI c = MathVarConstraintFactory.sortIncreasing(x, s);
		solver.add(c);
		solver.solve();
		boolean res = true;
		// System.out.println("x["+0+"] = "+x[0].getValue() +", s["+0+"] = "+s[0].getValue()+", p["+0+"] ="+((MSort)c).getP()[0].getValue());
		for(int i=1;i<n;i++) {
			res &= (s[i-1].getValue()<=s[i].getValue());
			// System.out.println("x["+i+"] = "+x[i].getValue() +", s["+i+"] = "+s[i].getValue()+", p["+i+"] ="+((MSort)c).getP()[i].getValue());
		}
		return res ? "OK" : "ERROR"; 
	}
	
	
	/* (non-Javadoc)
	 * @see utest.UTestI#all()
	 */
	@Override
	public String all() {
		String s = "\nconstraints.mathvar: Sortedness\n";
		s += "\n" + test1();
		return s; 
	}

}
