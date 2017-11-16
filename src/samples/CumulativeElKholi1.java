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

import java.util.Random;

import constraints.MathVarConstraintFactory;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MMathIntVar;

/**
 * A simple cumulative problem using El Kholi event-based decomposition
 */

public class CumulativeElKholi1 {
	
	public static String model(int seed, String system) {
		double epsilon = 1;
		int n = 8;
		int capa = 6; 
		int horizon = 500; 
		MSolver solver = new MSolver(system);
		MMathIntVar makespan = new MMathIntVar(0, horizon, solver, epsilon);
		MMathIntVar c = new MMathIntVar(capa,capa,solver);
		MMathIntVar[] start = new MMathIntVar[n];
		MMathIntVar[] dur = new MMathIntVar[n];
		MMathIntVar[] end = new MMathIntVar[n];
		double[] conso = new double[n];
		Random r = new Random(seed);
		for(int i=0; i<n; i++) {
			int val = r.nextInt(15)+4;
			dur[i] = new MMathIntVar(val-3,val,solver);
			start[i] = new MMathIntVar(0,(int)(horizon-dur[i].getMin()),solver);
			end[i] = new MMathIntVar((int)dur[i].getMin(),horizon,solver);
			conso[i] = (double)r.nextInt(5)+1; 
		}
		solver.add(MathVarConstraintFactory.cumulative_ElKholi(start, dur, end, c, conso, epsilon));
		solver.add(MathVarConstraintFactory.max(end, makespan));
		solver.optimize(makespan, SolverPolicy.MINIMIZE);
		String res = ""; 
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {  
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n"; 
			res += ("capa = "+c.getValue());
			for(int i=0;i<n; i++) {
				res += ("\nT"+i+": st = "+start[i].getValue()+", end = "+end[i].getValue() + ", dur = "+ dur[i].getValue() + ", h = "+conso[i]);
			}
			res += ("\nMakespan = "+makespan.getValue() +"\n");
			for(int j=0; j<makespan.getValue(); j++) {
				res += ("\tt"+j);
			}
			for(int i=0;i<n; i++) {
				res += "\nT"+i+":";
				for(int j=0; j<makespan.getValue(); j++) {
					if(j>=start[i].getValue() && j<end[i].getValue()) {
						res += ("\t"+conso[i]);
					} else {
						res += ("\t.");
					}
				}
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
			//String system = MILPSystem.TEXT; 
			String system = MILPSystem.GUROBI; 
			System.out.println(model(0,system));
	}

}
