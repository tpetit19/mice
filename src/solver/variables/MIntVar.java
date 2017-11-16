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

package solver.variables;

import java.util.Arrays;

import solver.LinExpr;
import solver.MSolver;
import solver.engines.InterfaceI;

/** 
 * MICE integer variables. Basically, domain variables. 
 * Use MMathIntVar subclass for mathematical integer variables. 
 */

public class MIntVar implements VarI {

	protected Object x;         // The main integer variable created in MILP solver
	protected MBinVar[] bools;   // The set of binary variables representing each value in domain
	protected MSolver solver;    // The solver
	protected int[] dom;        // The domain, one-to-one mapped with bools
	protected String name;      // The main integer variable name
	protected InterfaceI inter; // Methods repository

	// Main constructor

	public MIntVar(int[] dom, MSolver solver, double obj) {
		this.solver = solver;
		Arrays.sort(dom);
		this.name = "x"+solver.intid(); // +"["+dom[0]+",...,"+dom[dom.length-1]+"]";
		this.dom = dom; 
		this.inter = solver.getInterface();
		makeit(dom,obj);
	}

	public MIntVar(int[] dom, MSolver model) {
		this(dom,model,0.0);
	}

	public MIntVar(int min, int max, MSolver model, double obj) {
		this(makedom(min,max),model,obj);
	}

	public MIntVar(int min, int max, MSolver model) {
		this(makedom(min,max),model,0.0);
	}
	
	// For inheritance
	
	public MIntVar(MSolver model) {
		this.solver = model;
		this.name = "x"+solver.intid(); // +"["+dom[0]+",...,"+dom[dom.length-1]+"]";
		this.inter = solver.getInterface();
	}
	
	// Make a domain variable 

	protected void makeit(int[] dom, double obj) {

		// ----------------------------
		// Create the domain constraint
		// This is MICE code
		// ----------------------------

		bools = new MBinVar[dom.length];
		for(int i=0; i<dom.length; i++) {
			bools[i] = new MBinVar(solver,0.0);
		}
		// Each variable takes only one value
		LinExpr domx = new LinExpr();
		for(int i=0; i<dom.length; i++) {
			domx.addTerm(1, bools[i]);
		}
		solver.addForDomain(domx, "=", 1, "Domain Constraint: "+name);

		// ------------------------------------------
		// Link with the milp system integer variable
		// ------------------------------------------

		x = inter.createIntVar(this, obj);
	}

	// Simplified constructors

	protected static int[] makedom(int min, int max) {
		int[] dom = new int[max-min+1];
		for(int i=0; i<dom.length; i++) {
			dom[i] = i+min;
		}
		return dom;
	}

	// Retrieve a value in domain

	public int getIndex(int value) {
		int lb = 0;
		int ub = dom.length-1;
		while(lb<ub) {
			int index = (lb+ub)/2;
			if(dom[index]==value) {
				return index;
			}
			if(dom[index]>value) {
				ub = index-1;
			} else { 
				lb = index+1;
			}
		}
		if(dom.length>0 && dom[lb]==value) {
			return lb;
		}
		return -1;
	}

	// Getters

	public MBinVar[] getBools(){
		return bools;
	}

	public int[] getDomain() {
		return dom;  
	}

	@Override
	public Object get() {
		return x;
	}

	@Override
	public MSolver getSolver() {
		return solver;
	}

	@Override
	public String getName() {
		return name;
	}

	public double getMin() {
		return dom[0];
	}
	
	public double getMax() {
		return dom[dom.length-1];
	}
	
	// Get the value once fixed

	public int getValue() {
		return inter.getIntValue(this);
	}

}

