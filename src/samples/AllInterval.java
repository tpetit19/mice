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

package samples;

import constraints.DomainConstraintFactory;
import constraints.MathVarConstraintFactory;
import solver.MSolver;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;

/** 
 * http://www.csplib.org/Problems/prob007/  
 **/

public class AllInterval {

	public static String model(int n, String system) {
		MSolver solver = new MSolver(system);
		double t = System.currentTimeMillis();
		MIntVar[] x = new MIntVar[n]; 
		MIntVar[] d = new MIntVar[n-1]; 
		for(int i=0; i<x.length; i++) {
			x[i] = new MIntVar(0,n-1,solver);
			if(i<n-1) {
				d[i] = new MIntVar(1,n-1,solver);
			}
		}
		solver.add(DomainConstraintFactory.alldifferent(x));
		solver.add(DomainConstraintFactory.alldifferent(d));
		for(int i=0; i<n-1; i++) {
			solver.add(MathVarConstraintFactory.distanceXYZ(x[i], x[i+1], d[i]));
		}
		double t2 = System.currentTimeMillis();
		String res = "\nBUILDING TIME = "+(t2-t)/1000 + "\n";
		solver.solve();
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else { 
			res += "SOLVING TIME = "+ solver.getExecutionTime() + "\n";
			res += "x: ";
			for (int i = 0; i < n; i++) {
				res += x[i].getValue()+" ";
			}
			res += "\nd: ";
			for (int i = 0; i < n-1; i++) {
				res += d[i].getValue()+" ";
			}
		}
		return res; 
	}

	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBIPRESOLVE; 
		System.out.println(model(16,system)); 
	}

}
