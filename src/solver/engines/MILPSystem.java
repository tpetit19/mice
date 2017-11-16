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

package solver.engines;

/**
 * Currently implemented interfaces. 
 */

public class MILPSystem {
	public final static String GUROBI = "MILPSystem.GUROBI";   // Gurobi commercial solver without presolve
	public final static String GUROBIPRESOLVE = "MILPSystem.GUROBIPRESOLVE";  // Gurobi commercial solver with presolve
	public final static String TEXT = "MILPSystem.TEXT";       // Writes the decomposed model in a file
}
