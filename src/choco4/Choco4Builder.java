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

package choco4;

import java.util.Arrays;
import java.util.Hashtable;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.ConstraintsName;
import org.chocosolver.solver.constraints.nary.cumulative.Cumulative;
import org.chocosolver.solver.variables.IntVar;
import constraints.ConstraintI;
import constraints.mathvar.MCumulative_ElKholi;
import constraints.mathvar.MMax;
import solver.LinExpr;
import solver.MSolver;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.VarI;

/**
 * Build MICE constraints from Choco 4 constraints
 * http://www.choco-solver.org
 */

public class Choco4Builder {

	protected Hashtable<String, String> namesFromMice; 
	protected Hashtable<String, String> namesFromChoco; 
	protected MSolver solver;
	
	public Choco4Builder(MSolver solver) {
		this(solver,new Hashtable<String,String>(),new Hashtable<String,String>());
	}
	
	public Choco4Builder(MSolver solver,Hashtable<String, String> namesFromMice,Hashtable<String, String> namesFromChoco) {
		this.namesFromMice = namesFromMice;
		this.namesFromChoco = namesFromChoco;
		this.solver = solver;
	}
	
	public ConstraintI chocoConstraint(Constraint chococonstraint, boolean mathvar) {
		String name = chococonstraint.getName();
		switch(name){
		case(ConstraintsName.CUMULATIVE):{
			return CumulativeBuilder((Cumulative) chococonstraint, mathvar);
		}
		case(ConstraintsName.MAX):{
			return MaxBuilder(chococonstraint, mathvar); 
		}
		}
		return null;
	}
	
	// Hash Table getters
	
	public Hashtable<String, String> getNamesFromMice () {
		return namesFromMice;
	}
	
	public Hashtable<String, String> getNamesFromChoco () {
		return namesFromChoco;
	}
	
	// Variable finder
	
	public VarI findVar(String name) {
		LinExpr[] l = solver.getExpr();
		for(int i=0; i<l.length; i++) {
			VarI[] vars = l[i].getIntVars(); 
			for(int j=0; j<vars.length;j++) {
				if(getNamesFromChoco().get(name)==vars[j].getName()) {
					return vars[j];
				}
			}
		}
		System.out.println("not found");
		return null; 
	}
	
	// Get the solver
	
	public MSolver getSolver() {
		return solver; 
	}
	
	// Variable extractors
	
	private MIntVar[] intVarExtractor(IntVar[] vars) { 
		MIntVar[] res = new MIntVar[vars.length];
		for(int i=0;i<vars.length;i++){
			if(!namesFromChoco.containsKey(vars[i].getName())) { 
			int m = vars[i].getLB();
			int M = vars[i].getUB();
			if(!vars[i].hasEnumeratedDomain()) {
				res[i] = new MIntVar(m,M,solver);
			} else {
				int[] dom = new int[vars[i].getDomainSize()];
				int idx = 0;
				for (int val=m; val<=M; val= vars[i].nextValue(val)) {
					dom[idx] = val;
				}
				res[i] = new MIntVar(dom,solver);
			}
			namesFromMice.put(res[i].getName(), vars[i].getName());
			namesFromChoco.put(vars[i].getName(), res[i].getName());
			} else {
				res[i] = (MIntVar)findVar(vars[i].getName());  
			}
		}
		return res;
	}
	
	private MIntVar[] mathIntVarExtractor(IntVar[] vars) {
		MIntVar[] res = new MMathIntVar[vars.length];
		for(int i=0;i<vars.length;i++){
			if(!namesFromChoco.containsKey(vars[i].getName())) { 
				res[i] = new MMathIntVar(vars[i].getLB(),vars[i].getUB(),solver);
				namesFromMice.put(res[i].getName(), vars[i].getName());
				namesFromChoco.put(vars[i].getName(), res[i].getName());
			} else {
				res[i] = (MIntVar)findVar(vars[i].getName()); // keep domain variables if any
			}
		}
		return res;
	}
	
	// General variable extraction 
	
	protected MIntVar[] extract(Constraint choco, boolean mathvar) {
		IntVar[] vars = (IntVar[]) choco.getPropagator(0).getVars();
		MIntVar[] scope;
		if(mathvar) {
			scope = mathIntVarExtractor(vars);
		} else {
			scope = intVarExtractor(vars);
		}
		return scope;
	}
	
	// Max constraint
	
	protected ConstraintI MaxBuilder(Constraint maxct, boolean mathvar) {
		MIntVar[] scope = extract(maxct, mathvar); 
		MIntVar maxvar = scope[scope.length-1]; 
		MIntVar[] vars = new MIntVar[scope.length-1]; 
		System.arraycopy(scope, 0, vars, 0, scope.length-1);
		return new MMax(vars,maxvar);
	}
	
	// Cumulative constraint
	
	protected ConstraintI CumulativeBuilder(Cumulative choco, boolean mathvar) {
		IntVar[] vars = (IntVar[]) choco.getPropagator(0).getVars(); 
		int n = (vars.length-1)/4; 
		IntVar[] s = Arrays.copyOfRange(vars, 0, n);
        IntVar[] d = Arrays.copyOfRange(vars, n, n*2);
        IntVar[] e = Arrays.copyOfRange(vars, n*2, n*3);
        IntVar[] origh = Arrays.copyOfRange(vars, n*3, n*4);
        double[] heights = new double[origh.length];
        for(int i=0;i<origh.length;i++) {
        	if(!origh[i].isInstantiated()) {
        		throw new Error("Choco4Builder: public ConstraintI choco: Mice decomposition of Cumulative only works with fixed heights.");
        	}
        	heights[i]=origh[i].getValue();
        }
        IntVar[] c = new IntVar[]{vars[4*n]};
        MIntVar[] start;
        MIntVar[] dura;
        MIntVar[] end;
        MIntVar capa; 
        if(mathvar) {
        	start = mathIntVarExtractor(s);
        	dura = mathIntVarExtractor(d);
        	end = mathIntVarExtractor(e);
        	capa = mathIntVarExtractor(c)[0];
        } else {
        	start = intVarExtractor(s);
        	dura = intVarExtractor(d);
        	end = intVarExtractor(e);
        	capa = intVarExtractor(c)[0];
        }
        return new MCumulative_ElKholi(start,dura,end,capa,heights);
	}
	
}
