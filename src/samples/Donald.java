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

public class Donald {
	
	public static String model(String system) {
		MSolver solver = new MSolver(system); 
		MIntVar d = new MIntVar(0,9,solver); 
		MIntVar o = new MIntVar(0,9,solver); 
		MIntVar n = new MIntVar(0,9,solver); 
		MIntVar a = new MIntVar(0,9,solver); 
		MIntVar l = new MIntVar(0,9,solver); 
		MIntVar g = new MIntVar(0,9,solver); 
		MIntVar e = new MIntVar(0,9,solver); 
		MIntVar r = new MIntVar(0,9,solver); 
		MIntVar b = new MIntVar(0,9,solver);
		MIntVar t = new MIntVar(0,9,solver); 
		MIntVar[] vars = new MIntVar[]{d,o,n,a,l,g,e,r,b,t};
		solver.add(DomainConstraintFactory.alldifferent(vars));
		MIntVar[] refs = new MIntVar[]{d,o,n,a,l,d,g,e,r,a,l,d,r,o,b,e,r,t};
		String[] names = new String[]{"D","O","N","A","L","D","G","E","R","A","L","D","R","O","B","E","R","T"};
		int[] coefs = new int[]{100000, 10000, 1000, 100, 10, 1,
                				100000, 10000, 1000, 100, 10, 1,
                				-100000, -10000, -1000, -100, -10, -1};
		solver.add(DomainConstraintFactory.scalar(refs, coefs, 0, "="));
		solver.solve(); 
		String res = ""; 
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {  
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n\n"; 
			int sum = 0;
		    for (int i=0; i<6; i++) {
		    	res += names[i] + "(" + (refs[i].getValue())+"*"+(coefs[i]) + ") + ";
		    	sum += (refs[i].getValue()*coefs[i]);
		    }
		    res += "\n";
		    for (int i=6; i<11; i++) {
		    	res += names[i] + "(" + (refs[i].getValue()+"*"+coefs[i]) + ") + ";
		    	sum += (refs[i].getValue()*coefs[i]);
		    }
		    res += names[11] + "(" + (refs[11].getValue()+"*"+coefs[11]) + ") = ";
		    sum += (refs[11].getValue()*coefs[11]); 
		    res += sum + "\n\n"; 
		    sum = 0;
		    for (int i=12; i<names.length-1; i++) {
		    	res += names[i] + "(" + (refs[i].getValue()+"*"+(-coefs[i]))+ ") + ";
		    	sum += (refs[i].getValue()*(-coefs[i])); 
		    }
		    res += names[names.length-1] + "(" + (refs[names.length-1].getValue()+"*"+(-coefs[names.length-1])) +") = ";
		    sum += (refs[names.length-1].getValue()*(-coefs[names.length-1])); 
		    res += sum; 
		}
		return res; 
	}

	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBI; 
		System.out.println(model(system));
	}
	
}
