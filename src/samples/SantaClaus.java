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

import constraints.DomainConstraintFactory;
import constraints.MathVarConstraintFactory;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;
import solver.variables.MRealVar;

/**
 * Santa Claus problem
 * http://arxiv.org/abs/1402.1361
 */

public class SantaClaus {
	public static int[] mix(int n, int p, int seed) {  // p among n, must be called with 0 <= p <= n
		int[] res = new int[p];
		int[] tmp = new int[n];
		for(int i=0; i<n; i++) {
			tmp[i] = i+1;
		}
		Random r = new Random(seed);
		for(int i=0; i<n-2; i++) {
			int idx = r.nextInt(n-i-1)+i+1;
			int v = tmp[idx];
			tmp[idx] = tmp[i];
			tmp[i] = v;
		}
		for(int i=0; i<p; i++) {
			res[i] = tmp[i];
		}
		return res;
	}

	public static int min(int[] positive) {
		int res = Integer.MAX_VALUE;
		for(int i=0; i<positive.length; i++) {
			res = Math.min(res, positive[i]);
		}
		return res;
	}

	public static int max(int[] positive) {
		int res = 0;
		for(int i=0; i<positive.length; i++) {
			res = Math.max(res, positive[i]);
		}
		return res;
	}
	public static String model(int n_kids, int[] gift_price, String system) {
		MSolver solver = new MSolver(system);
		int n = gift_price.length;
		int min_price = min(gift_price);
		int max_price = max(gift_price);
		MIntVar[] kid_gift = new MIntVar[n_kids];
		MIntVar[] kid_price = new MIntVar[n_kids]; 
		MIntVar total_cost = new MIntVar(min_price*n_kids, max_price*n_kids, solver); 
		for(int i=0; i<kid_gift.length; i++) {
			kid_gift[i] = new MIntVar(0,n,solver); 
			kid_price[i] = new MIntVar(min_price,max_price,solver);
		}
		MRealVar average = new MRealVar(min_price, max_price, solver);
		MRealVar average_deviation = new MRealVar(0, max_price*n_kids, solver); 
		solver.add(DomainConstraintFactory.alldifferent(kid_gift));
		for (int i = 0; i < n_kids; i++) {
			solver.add(DomainConstraintFactory.element(kid_gift[i], gift_price, kid_price[i]));
		}
		solver.add(DomainConstraintFactory.sum(kid_price, total_cost, "="));
		solver.add(MathVarConstraintFactory.deviation(kid_price, average, average_deviation));
		solver.setTimeLimit(600000);
		solver.optimize(average_deviation, SolverPolicy.MINIMIZE);
		String res = "";
		if(solver.getSystem()==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n"; 
			for(int i=0; i<n_kids; i++) {
				res+=("\nkid " + (i+1) + ": "+kid_gift[i].getValue() + " at price " + kid_price[i].getValue());
			}
			res+=("\nTotal cost = " + total_cost.getValue());
			res+=("\nAverage = " + average.getValue());
			res+=("\nAverage deviation = " + (average_deviation.getValue()/n_kids)+"\n");
		}
		return res; 
	}
	public static void main(String[] args) {
		int[] u = mix(50,10,1986);
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBIPRESOLVE; 
		System.out.println(model(5,u,system));
	}
}