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
import solver.MSolver;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;

/**
 * http://csplib.org/Problems/prob003/
 */

public class LatinSquare {

	public static String model(int n, String system) {
		MSolver solver = new MSolver(system); 
		MIntVar[][] rows = new MIntVar[n][n];
		MIntVar[][] cols = new MIntVar[n][n];
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				rows[i][j] = new MIntVar(1,n,solver);
				cols[j][i] = rows[i][j];
			}
		}
		for(int i=0; i<n; i++) {
			solver.add(DomainConstraintFactory.alldifferent(rows[i]));
			solver.add(DomainConstraintFactory.alldifferent(cols[i]));
		}
		solver.solve();
		String res = "";
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {  
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n"; 
		    for (int i = 0; i < n; i++) {
				 res += "\n";
		         for (int j = 0; j < n; j++) {
		        	 res += rows[i][j].getValue()+" ";
		         }
		    }
		}
		return res; 
	}
	
	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBI; 
		System.out.println(model(15,system));
	}
	
}
