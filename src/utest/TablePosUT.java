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
import constraints.domainvar.tablebased.MTablePos;
import gurobi.GRBException;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.variables.MIntVar;


public class TablePosUT implements UTestI {   

	final static String SYSTEM = MILPSystem.GUROBI;  // without presolve to ensure infeasibility testing

	// Test 1: tuple outside from domains, 4 variables
	public static String test1() {  
		int n = 4;
		int [][] tuples = new int[2][n];
		tuples[0] = new int[]{2,2,2,2}; // outside
		tuples[1] = new int[]{3,3,3,3}; // inside
		MSolver solver = new MSolver(SYSTEM);
		MIntVar[] vars = new MIntVar[n];
		for(int i=0; i<vars.length; i++) {
			vars[i] = new MIntVar(new int[]{1,3},solver); // unique solution is (3,3,3,3)
		}
		ConstraintI table = new MTablePos(vars,tuples);
		solver.add(table);
		solver.solve();
		boolean res = true; 
		for(int i=0; i<vars.length; i++) {
			res &= (vars[i].getValue()==3); 
		}
		return res ? "OK" : "ERROR"; 
	}

	// Test 2: cycle of binary difference constraints over Di = {1,2} 
	// n variables x0 != x1 != ... != x0
	// Solution if n is even, no solution otherwise
	public static String test2(int n){
		int[][] tuples = new int[2][2];
		tuples[0] = new int[]{1,2};
		tuples[1] = new int[]{2,1};
		MSolver solver = new MSolver(SYSTEM);
		MIntVar[] vars = new MIntVar[n];
		for(int i=0; i<vars.length; i++) {
			vars[i] = new MIntVar(new int[]{1,2},solver);
		}
		ConstraintI[] table = new MTablePos[n-1];
		for(int i=0; i<table.length; i++) {
			table[i] = new MTablePos(new MIntVar[]{vars[i],vars[i+1]},tuples);
			solver.add(table[i]);
		}
		solver.add(new MTablePos(new MIntVar[]{vars[n-1],vars[0]},tuples));
		solver.solve(); 
		boolean res = true; 
		if(solver.getSolutionCount()==0) {
			res = (n%2!=0); 
		} else {
			for(int i=0; i<vars.length-1; i++) {
				res &= (vars[i].getValue()!=vars[i+1].getValue()); 
			}
			res &= (vars[n-1].getValue()!=vars[0].getValue());
		}
		return res ? "OK" : "ERROR";
	}

	public String all() {
		String s = "\nconstraints.intdomain.table: TablePos\n";
		s += "\n" + test1();
		s += "\n" + test2(4);
		s += "\n" + test2(5);
		return s; 
	}

}
