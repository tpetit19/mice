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

public class Sudoku {

	public static String model(String system) {
		int[][] grid = new int[][]{
			{0, 1, 0, 0, 0, 0, 0, 0, 0},
			{8, 0, 0, 0, 0, 2, 1, 7, 0},
			{0, 0, 4, 0, 0, 0, 0, 0, 0},
			{0, 2, 0, 0, 0, 6, 0, 1, 3},
			{0, 5, 3, 0, 7, 0, 6, 0, 2},
			{1, 0, 0, 8, 0, 0, 5, 4, 0},
			{0, 0, 0, 3, 1, 5, 0, 2, 6},
			{0, 4, 0, 2, 0, 0, 0, 0, 7},
			{0, 0, 0, 4, 8, 0, 3, 0, 0}
		};
		MSolver solver = new MSolver(system);
		int n = 9;
	    MIntVar[][] rows, cols, carres;
	    rows = new MIntVar[n][n];
	    cols = new MIntVar[n][n];
	    carres = new MIntVar[n][n];
	    for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++) {
	              if (grid[i][j] > 0) {
	             	rows[i][j] = new MIntVar(new int[]{grid[i][j]},solver);
	             } else {
	            	rows[i][j] = new MIntVar(1,9,solver);
	            }
	            cols[j][i] = rows[i][j];
	        }
	    }
	    for (int i = 0; i < 3; i++) {
	        for (int j = 0; j < 3; j++) {
	            for (int k = 0; k < 3; k++) {
	                carres[j + k * 3][i] = rows[k * 3][i + j * 3];
	                carres[j + k * 3][i + 3] = rows[1 + k * 3][i + j * 3];
	                carres[j + k * 3][i + 6] = rows[2 + k * 3][i + j * 3];
	            }
	        }
	    }
	    for (int i = 0; i < n; i++) {
	    	solver.add(DomainConstraintFactory.alldifferent(rows[i]));
	    	solver.add(DomainConstraintFactory.alldifferent(cols[i]));
	    	solver.add(DomainConstraintFactory.alldifferent(carres[i]));
	    }
	    solver.solve();
	    String res = "";
	    if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else { 
			res += "SOLVING TIME = "+ solver.getExecutionTime() + "\n";
			for (int i = 0; i < n; i++) {
		         for (int j = 0; j < n; j++) {
		        	 res += rows[i][j].getValue()+" ";
		         }
		         res+="\n";
		    }
		}
		return res;
	}
	
	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBIPRESOLVE; 
		System.out.println(model(system)); 
	}
	
}
