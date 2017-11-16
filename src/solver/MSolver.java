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

package solver;

import java.util.ArrayList;

import constraints.ConstraintI;
import solver.engines.InterfaceI;
import solver.engines.MILPSystem;
import solver.engines.gurobi.Gurobi;
import solver.engines.gurobi.GurobiPresolve;
import solver.engines.text.TextModel;
import solver.variables.VarI;

/**
 * MICE modeler and access to the solver. 
 */

public class MSolver {

	protected String system;       // System name
	protected Object milpsolver;   // System concrete object
	protected InterfaceI milp;     // MILP engine services 
	private long intvarid;  	   // Text mode: unique identifier for the next mathematical variable to be created, starting from 0
	private long boolvarid;
	private long realvarid;
	// NEW
	private ArrayList<LinExpr> linexp;
	// END NEW
	
	public MSolver(String system) {
		this.system = system;
		this.intvarid = 0;
		this.boolvarid = 0;
		this.realvarid = 0;
		switch(system){
		case(MILPSystem.GUROBI):{
			milp = new Gurobi();
		}; break; 
		case(MILPSystem.GUROBIPRESOLVE):{
			milp = new GurobiPresolve();
		}; break; 
		case(MILPSystem.TEXT):{
			milp = new TextModel();
		}; break; 
		}
		if(milp!=null) {
			 milpsolver = milp.createSolver();
		} else {
			throw new Error("Solver: unable to recognize system");
		}
		// NEW
		this.linexp = new ArrayList<LinExpr>();
		// END NEW
	}
	//public Solver() {
	//	this(MILPSystem.GUROBI);
	//}

	public InterfaceI getInterface() {
		return milp;
	}
	public Object getMilpSolver() {
		return milpsolver;
	}
	public String getSystem() {
		return system; 
	}
	public double getExecutionTime() {
		return milp.getExecutionTime(milpsolver);
	}
	public String[] otherStatistics() {
		return milp.otherStatistics(milpsolver);
	}

	public void setTimeLimit(long t) {
		milp.setTimeLimit(milpsolver, t);
	}
	public long intid() {
		this.intvarid ++; 
		return this.intvarid; 
	}
	public long boolid() {
		this.boolvarid ++; 
		return this.boolvarid; 
	}
	public long realid() {
		this.realvarid ++; 
		return this.realvarid; 
	}
	public long getintid() {
		return this.intvarid; 
	}
	public long getboolid() {
		return this.boolvarid; 
	}
	public long getrealid() {
		return this.realvarid; 
	}

	// Post a non linear constraint
	public void add(ConstraintI c) {
		c.build();
	}

	// Post a linear expression, op in {"=", "<=", ">="}
	public void add(LinExpr l, String op, VarI var, String ctrName) {
		l.addTerm(-1, var);
		this.add(l, op, 0, ctrName);
	}
	public void add(LinExpr l, String op, int cst, String ctrName) {
		this.add(l, op, (double)cst, ctrName);
	}
	public void add(LinExpr l, String op, double cst, String ctrName) {
		// NEW
		linexp.add(l);
		// END NEW
		milp.add(milpsolver, l, op, cst, ctrName);
	}

	// NEW
	public LinExpr[] getExpr() {
		Object[] o = linexp.toArray();
		LinExpr[] res = new LinExpr[o.length];
		for(int i=0;i<o.length;i++) {
			res[i] = (LinExpr) o[i];
		}
		return res;
	}
	// END NEW
	
	// Without name
	public void add(LinExpr l, String op, VarI var) {
		this.add(l,op,var,"");
	}
	public void add(LinExpr l, String op, int cst) {
		this.add(l,op,cst,"");
	}
	public void add(LinExpr l, String op, double cst) {
		this.add(l,op,cst,"");
	}

	// Specific method for IntVar (used to keep a trace of the origin of expression: build a domain)
	public void addForDomain(LinExpr l, String op, int cst, String ctrName) {
		milp.addForDomain(milpsolver, l, op, (double)cst, ctrName);
	}

	// Satisfaction problems
	public void solve() {
		milp.solve(milpsolver);
	}

	// Optimization problems
	public void optimize(VarI var, boolean opt) {
		milp.optimize(milpsolver, var, opt);
	}

	public int getSolutionCount() {
		return milp.getSolutionCount(milpsolver);
	}
}
