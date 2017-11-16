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
import solver.variables.MRealVar;

/**
 * A simple cumulative problem using El Kholi event-based decomposition
 * Large horizon and rational durations
 */

public class CumulativeElKholi2 {
	
	public static String model(int seed, String system) {
		double epsilon = 1;
		int n = 8;
		int capa = 6; 
		int horizon = 50000; 
		MSolver solver = new MSolver(system);
		MMathIntVar makespan = new MMathIntVar(0, horizon, solver, epsilon);
		MMathIntVar c = new MMathIntVar(capa,capa,solver);
		MRealVar[] start = new MRealVar[n];
		MRealVar[] dur = new MRealVar[n];
		MRealVar[] end = new MRealVar[n];
		double[] conso = new double[n];
		Random r = new Random(seed);
		for(int i=0; i<n; i++) {
			double val = r.nextInt(1500)+400.23;
			dur[i] = new MRealVar(val-200.35,val,solver);
			start[i] = new MRealVar(0,(int)(horizon-dur[i].getMin()),solver);
			end[i] = new MRealVar((int)dur[i].getMin(),horizon,solver);
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
		}
		return res;
	}
	
	public static void main(String[] args) {
			//String system = MILPSystem.TEXT; 
			String system = MILPSystem.GUROBI; 
			System.out.println(model(0,system));
	}

}
