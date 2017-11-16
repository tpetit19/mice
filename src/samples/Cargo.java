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

import constraints.MathVarConstraintFactory;
import solver.LinExpr;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MRealVar;

/**
 * Cargo problem from Besley 
 * A cargo plane has three compartments for storing cargo (merchandises): front, centre and rear. 
 * These compartments have the following limits on both weight and space:
 * Compartment   Weight capacity (tonnes)   Space capacity (cubic metres)
 * Front         10                         6800
 * Centre        16                         8700
 * Rear          8                          5300
 * Furthermore, the weight of the cargo in the respective compartments must be the same proportion of that 
 * compartment's weight capacity to maintain the balance of the plane.
 * The following four cargoes are available for shipment on the next flight:
 * Cargo   Weight (tonnes)   Volume (cubic metres/tonne)  Profit (£/tonne)
 * C1      18                480                          310
 * C2      15                650                          380
 * C3      23                580                          350
 * C4      12                390                          285
 * Any proportion of these cargoes can be accepted. 
 * The objective is to determine how much (if any) of each cargo C1, C2, C3 and C4 should be accepted 
 * and how to distribute each among the compartments so that the total profit for the flight is maximised. 
 */

public class Cargo {

	public static String model(String system) {
		
		MSolver solver = new MSolver(system);
		MRealVar[][] cargos = new MRealVar[4][3];
		MRealVar[][] compartments = new MRealVar[3][4];
		for(int i=0; i<cargos.length; i++) {
			for(int j=0; j<cargos[i].length; j++) {
				cargos[i][j] = new MRealVar(0,Integer.MAX_VALUE,solver);
				compartments[j][i] = cargos[i][j];
			}
		}
		// Cannot pack more of each of the four cargoes than we have available
		int[] weights = new int[]{18,15,23,12};
		for(int i=0; i<cargos.length; i++) {
			solver.add(MathVarConstraintFactory.scalar(cargos[i], new int[]{1,1,1}, weights[i], "<="));
		}
		// The weight capacity of each compartment must be respected 
		int[] capas = new int[]{10,16,8};
		int[] space = new int[]{6800,8700,5300}; 
		for(int i=0; i<compartments.length; i++) {
			solver.add(MathVarConstraintFactory.scalar(compartments[i], new int[]{1,1,1,1}, capas[i], "<="));
			solver.add(MathVarConstraintFactory.scalar(compartments[i], new int[]{480,650,580,390}, space[i], "<="));
		}
		// The weight of the cargo in the respective compartments must be the same proportion of that 
		// compartment's weight capacity to maintain the balance of the plane.
		LinExpr e1 = new LinExpr(); 
		LinExpr e2 = new LinExpr(); 
		for(int i=0; i<cargos.length; i++) {
				e1.addTerm((1/capas[0]), compartments[0][i]);
				e1.addTerm((-1/capas[1]), compartments[1][i]);
				e2.addTerm((1/capas[0]), compartments[0][i]);
				e2.addTerm((-1/capas[2]), compartments[2][i]);
		}
		solver.add(e1,"=",0);
		solver.add(e2,"=",0);
		// Maximize total profit
		LinExpr e = new LinExpr(); 
		int[] profit = new int[] {310,380,350,285};
		for(int i=0; i<cargos.length; i++) {
			for(int j=0; j<cargos[i].length; j++) {
				e.addTerm(profit[i],cargos[i][j]); 
			}
		}
		MRealVar obj = new MRealVar(0,Double.MAX_VALUE,solver); 
		e.addTerm(-1, obj);
		solver.add(e, "=", 0);
		solver.optimize(obj, SolverPolicy.MAXIMIZE);
		String res = ""; 
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {  
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n"; 
			res += "Profit = " + ((int)(obj.getValue()*100))/100.0 + "\n"; 
			res += "Cargo quantities:\t";
			for(int j=0; j<cargos.length; j++) {
				res+="C"+(j+1)+"\t";
			}
			for(int i=0; i<compartments.length; i++) {
				res += "\nCompartment "+i+":\t\t";
				for(int j=0; j<compartments[i].length; j++) {
					res += (((int)(compartments[i][j].getValue()*100))/100.0) + "\t";
				}
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBI; 
		System.out.println(model(system));
}
}
