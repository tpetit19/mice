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
 * Continuous variables. 
 */

public class MRealVar implements VarI{
	
	protected Object x;         // The main integer variable created in MILP solver
	protected MSolver solver;    // The solver
	protected String name;      // The main integer variable name
	protected double min;
	protected double max;
	protected InterfaceI inter; // Methods repository
	
	// Constructors
	
	public MRealVar(double min, double max, MSolver solver, double obj) {  // obj = objective coefficient
		this.solver = solver;
		this.min = min;
		this.max = max; 
		this.name = "r"+solver.realid(); // +"["+min+".."+max+"]";
		this.inter = solver.getInterface();
		x = inter.createRealVar(this, obj);
	}
	public MRealVar(double min, double max, MSolver model) {
		this(min,max,model,0.0);
	}
	public MRealVar(int min, int max, MSolver model) {
		this((double)min,(double)max,model);
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
		return min;
	}
	
	public double getMax() {
		return max; 
	}
	
	public double getValue() {
		return inter.getRealValue(this);
	}
	
}
