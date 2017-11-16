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

import solver.MSolver;
import solver.engines.InterfaceI;

/**
 * MICE binary variables. 
 */

public class MBinVar implements VarI {

	protected Object x;         // The main binary variable created in MILP solver
	protected MSolver solver;   // The solver
	protected String name;      // The main integer variable name
	protected InterfaceI inter; // Methods repository
	
	// Main constructor

	public MBinVar(MSolver solver, double obj) {  // obj = objective coefficient
		this.solver = solver;
		this.name = "b"+solver.boolid();
		inter = solver.getInterface();
		x = inter.createBinVar(this, obj);
	}
	
	public MBinVar(MSolver model) {
		this(model,0.0); 
	}

	// Getters

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
		return 0;
	}
	
	public double getMax() {
		return 1; 
	}
	
	// Get the value once fixed

	public int getValue() {
		return inter.getBinValue(this);
	}

}