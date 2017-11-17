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

package solver.engines.gurobi;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import solver.engines.InterfaceI;

/**
 * Interface to Gurobi, may 2016 version. 
 * http://www.gurobi.com
 */

public class Gurobi extends GurobiPresolve implements InterfaceI {

	@Override
	public Object createSolver() {
		try {
			GRBEnv env = new GRBEnv();
			env.set(GRB.IntParam.Presolve,0);
			env.set(GRB.IntParam.LogToConsole, 0);
			GRBModel m = new GRBModel(env);
			return m;
		} catch (GRBException e) {
			throw new Error("Gurobi: public static Object createSolver: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	

}
