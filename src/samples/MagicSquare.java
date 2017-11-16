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

import java.util.Arrays;

import constraints.DomainConstraintFactory;
import solver.MSolver;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;

/**
 * http://csplib.org/Problems/prob019/
 */

public class MagicSquare {
	
	public static String model(int n, String system) {
		MSolver solver = new MSolver(system);
		int ms = n * (n * n + 1) / 2;
		MIntVar[][] matrix = new MIntVar[n][n];
		MIntVar[][] invMatrix = new MIntVar[n][n];
		MIntVar[] vars = new MIntVar[n * n];
		int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++, k++) {
                matrix[i][j] = new MIntVar(1,n*n,solver);
                vars[k] = matrix[i][j];
                invMatrix[j][i] = matrix[i][j];
                if(i==0 && j==0) {
                	matrix[i][j] = new MIntVar(1,1,solver);
                }
            }
        }
        MIntVar[] diag1 = new MIntVar[n];
        MIntVar[] diag2 = new MIntVar[n];
        for (int i = 0; i < n; i++) {
            diag1[i] = matrix[i][i];
            diag2[i] = matrix[(n - 1) - i][i];
        }
        solver.add(DomainConstraintFactory.alldifferent(vars));
        int[] coeffs = new int[n];
        Arrays.fill(coeffs, 1);
        for (int i = 0; i < n; i++) {
        	solver.add(DomainConstraintFactory.scalar(matrix[i], coeffs, ms,"="));
        	solver.add(DomainConstraintFactory.scalar(invMatrix[i], coeffs, ms,"="));
        }
        solver.add(DomainConstraintFactory.scalar(diag1, coeffs, ms,"="));
        solver.add(DomainConstraintFactory.scalar(diag2, coeffs, ms,"="));
        solver.solve();
        String res = "";
        if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else { 
			res += "SOLVING TIME = "+ solver.getExecutionTime() + "\n";
			res+="Solution:\n";
			for (int i = 0; i < n; i++) {
				res+="\n";
	            for (int j = 0; j < n; j++, k++) {
	            	res+=(matrix[i][j].getValue() + " ");
	            }
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBIPRESOLVE; 
		System.out.println(model(7,system)); 
	}


}
