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
 * Mathematical bounded integer variables. 
 */

public class MMathIntVar extends MIntVar {

	/**
	 * @param min
	 * @param max
	 * @param model
	 * @param obj
	 * @param name
	 */
	
	protected int min;
	protected int max;
	
	public MMathIntVar(int min, int max, MSolver model, double obj) {
		super(model);
		this.min = min; 
		this.max = max;
		makeit(null,obj);
	}
	
	public MMathIntVar(int min, int max, MSolver model) {
		this(min,max,model,0.0); 
	}
	
	protected void makeit(int[] dom, double obj) {
		x = inter.createMathematicalIntVar(this, obj);
	}

	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max; 
	}

	public int getValue() {
		return inter.getIntValue(this);
	}
	
}
