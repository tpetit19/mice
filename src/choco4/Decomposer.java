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

import org.chocosolver.solver.constraints.Constraint;
import solver.MSolver;
import solver.variables.VarI;

/**
 * Turn Choco 4 models into a MICE model 
 * http://www.choco-solver.org
 */

public class Decomposer {
		
	Choco4Builder builder; 
	
	public void reset() {
		builder=null;
	}
	
	public MSolver decomposeConstraint(Constraint chococonstraint, boolean mathvar, String outSystem) {
		return decomposeConstraintSet(new Constraint[]{chococonstraint}, new boolean[]{mathvar}, outSystem);
	}
	
	public MSolver decomposeConstraintSet(Constraint[] chococonstraint, boolean[] mathvar, String outSystem) {
		MSolver solver = new MSolver(outSystem);
		builder = new Choco4Builder(solver);
		solver.add(builder.chocoConstraint(chococonstraint[0], mathvar[0])); 
		for(int i=1; i<chococonstraint.length; i++) {
			builder = new Choco4Builder(solver,builder.getNamesFromMice(),builder.getNamesFromChoco());
			solver.add(builder.chocoConstraint(chococonstraint[i], mathvar[i])); 
		}
		return solver; 
	}
	
	// Retrieve a specific variable (e.g., objective)
	
	public VarI findVar(String name) {
		return builder.findVar(name);
	}

	// Get the solver object
	
	public MSolver getSolver() {
		return builder.getSolver();
	}
	
}
