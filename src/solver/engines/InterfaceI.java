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

import solver.LinExpr;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.MRealVar;
import solver.variables.VarI;

/**
 * Interface to be implemented for each new solving engine. 
 */

public interface InterfaceI {
		public Object createSolver();
		public void add(Object milpsolver, LinExpr l, String op, double cst, String ctrName); // constrained linear expression
		public void addForDomain(Object milpsolver, LinExpr l, String op, double cst, String ctrName); // same method but when creating an integer domain
		public Object createBinVar(MBinVar var, double obj);
		public Object createIntVar(MIntVar var, double obj);
		public Object createMathematicalIntVar(MMathIntVar var, double obj);
		public Object createRealVar(MRealVar var, double obj);
		public Object createFixedVar(MIntVar var, int value, double obj);
		public int getIntValue(MIntVar var);
		public int getBinValue(MBinVar var);
		public double getRealValue(MRealVar var);
		public void solve(Object milpsolver);
		public void optimize(Object milpsolver, VarI var, boolean opt);  // opt is SolverPolicy.MAXIMIZE or SolverPolicy.MINIMIZE
		public String solverstats(Object milpsolver);
		public void setTimeLimit(Object solver, long t);  // t in milliseconds
		public double getExecutionTime(Object solver);  // t in seconds
		public String[] otherStatistics(Object solver); // buffer for any other statistics
		public int getSolutionCount(Object solver);
}
