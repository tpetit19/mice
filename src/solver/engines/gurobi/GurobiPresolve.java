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

import java.util.ArrayList;
import java.util.Arrays;

import gurobi.*;
import solver.LinExpr;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.InterfaceI;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.MRealVar;
import solver.variables.VarI;

/** 
 * Interface to Gurobi with presolve.
 * WARNING: default presolve used: infeasible models may not be detected as infeasible.
 * Use MILPSystem.GUROBI for safer use in this context (possibly less efficient). 
 * http://www.gurobi.com
 */

public class GurobiPresolve implements InterfaceI {
	
	protected static char gurobiOp(String op) {
		switch(op) {
		case("="):{
			return GRB.EQUAL;
		}
		case("<="):{
			return GRB.LESS_EQUAL;
		}
		case(">="):{
			return GRB.GREATER_EQUAL;
		}
		}
		throw new Error("Gurobi: operator "+op+" unknown");
	}

	@Override
	public Object createSolver() {
		try {
			GRBEnv env = new GRBEnv();
			//env.set(GRB.IntParam.LogToConsole, 0);
			return new GRBModel(env);
		} catch (GRBException e) {
			throw new Error("Gurobi: public static Object createSolver: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(Object milpsolver, LinExpr l, String op, double cst, String ctrName) {
		try {
			GRBLinExpr gexpr = new GRBLinExpr();
			ArrayList<Object[]> expr = (ArrayList<Object[]>) l.get();
			int size = expr.size();
			for(int i=0; i<size; i++) {
				Object[] obj = expr.get(i);
				GRBVar x = (GRBVar) ((VarI) obj[0]).get();
				double coef = (double) obj[1];
				gexpr.addTerm(coef, x);
			}
			char gop = gurobiOp(op);
			GRBModel gmodel = (GRBModel) milpsolver;
			gmodel.addConstr(gexpr, gop, cst, ctrName);
		} catch (GRBException e) {
			throw new Error("Gurobi: public vois add: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see solver.engines.InterfaceI#addForDomain(java.lang.Object, solver.LinExpr, java.lang.String, double, java.lang.String)
	 */
	@Override
	public void addForDomain(Object milpsolver, LinExpr l, String op, double cst, String ctrName) {
		this.add(milpsolver, l, op, cst, ctrName);
	}
	
	@Override
	public Object createBinVar(MBinVar var, double obj) {  // obj = objective coefficient
		MSolver solver = var.getSolver();
		String name = var.getName();
		try {
			GRBModel milpsolver = (GRBModel) solver.getMilpSolver();
			Object x = milpsolver.addVar(0,1,obj,GRB.BINARY, name);
			milpsolver.update();
			return x; 
		} catch (GRBException e) {
			throw new Error("Gurobi: public static Object createBinVar: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	@Override
	public Object createIntVar(MIntVar var, double obj) {  // A voir pourquoi est-ce du code Gurobi pour l'expression ? parce que contient x - a dire dans le papier
		MSolver solver = var.getSolver();
		String name = var.getName();
		int[] dom = var.getDomain();
		MBinVar[] bools = var.getBools();
		try {
				GRBModel milpsolver = (GRBModel) solver.getMilpSolver();
				Arrays.sort(dom);
				Object x = milpsolver.addVar(dom[0],dom[dom.length-1],obj,GRB.INTEGER,name);
				milpsolver.update();
				GRBLinExpr sumx = new GRBLinExpr();
				for(int i=0; i<dom.length; i++) {
					sumx.addTerm(dom[i],(GRBVar) bools[i].get());
				}
				milpsolver.addConstr(sumx, GRB.EQUAL, (GRBVar)x, "dom ctr value sum:"+name);
				return x; 
		} catch (GRBException e) {
				throw new Error("Gurobi: public static Object createIntVar: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}	
	}

	@Override
	public Object createMathematicalIntVar(MMathIntVar var, double obj) {  // obj = objective coefficient
		String name = var.getName();
		MSolver solver = var.getSolver();
		try {
			GRBModel milpsolver = (GRBModel) solver.getMilpSolver();
			Object x = milpsolver.addVar(var.getMin(),var.getMax(),obj,GRB.INTEGER,name);
			milpsolver.update();
			return x; 
		} catch (GRBException e) {
			throw new Error("Gurobi: public static Object createObjVar: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
	}
	}

	@Override
	public Object createFixedVar(MIntVar var, int value, double obj) {
		String name = var.getName();
		MSolver solver = var.getSolver();
		try {
			GRBModel milpsolver = (GRBModel) solver.getMilpSolver();
			Object x = milpsolver.addVar(value,value,obj,GRB.INTEGER,name);
			milpsolver.update();
			return x; 
		} catch (GRBException e) {
			throw new Error("Gurobi: public static Object createObjVar: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}
	
	@Override
	public Object createRealVar(MRealVar var, double obj) {
		MSolver solver = var.getSolver();
		String name = var.getName();
		double min = var.getMin();
		double max = var.getMax();
			try {
				GRBModel milpsolver = (GRBModel) solver.getMilpSolver();
				Object x = milpsolver.addVar(min, max, obj,GRB.CONTINUOUS,name);
				milpsolver.update();
				return x;
			} catch (GRBException e) {
				throw new Error("Gurobi: public static Object createRealVar: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
			}
	}

	@Override
	public int getIntValue(MIntVar var) {
		Object x = var.get();
		String name = var.getName();
		try {
			return (int) ((GRBVar)x).get(GRB.DoubleAttr.X);
		} catch (GRBException e) {
			System.out.println("IntVar:"+name+": unable to get fixed value.");
			System.out.println("Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		return Integer.MIN_VALUE;
	}

	@Override
	public int getBinValue(MBinVar var) {
		Object x = var.get();
		String name = var.getName();
		try {
			return (int) ((GRBVar)x).get(GRB.DoubleAttr.X);
		} catch (GRBException e) {
			System.out.println("BinVar:"+name+": unable to get fixed value.");
			System.out.println("Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		return Integer.MIN_VALUE;
	}

	@Override
	public double getRealValue(MRealVar var) {
		Object x = var.get();
		String name = var.getName();
		try {
			return (double) ((GRBVar)x).get(GRB.DoubleAttr.X);
		} catch (GRBException e) {
			System.out.println("IntVar:"+name+": unable to get fixed value.");
			System.out.println("Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		return Double.MIN_VALUE;
	}

	@Override
	public void solve(Object milpsolver) {
		try {
			GRBModel gmodel = (GRBModel) milpsolver;
			GRBVar dummy = gmodel.addVar(0,0,0.0,GRB.INTEGER,"solvedummyvalue");
			gmodel.update();
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1, dummy);
			gmodel.setObjective(expr, GRB.MAXIMIZE);
			gmodel.optimize();
		} catch (GRBException e) {
			System.out.println("public void solve: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	@Override
	public void optimize(Object milpsolver, VarI var, boolean opt) {
		try {
			GRBModel gmodel = (GRBModel) milpsolver;
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1, (GRBVar)var.get());
			if(opt==SolverPolicy.MAXIMIZE) {
				gmodel.setObjective(expr, GRB.MAXIMIZE);
			} else {
				gmodel.setObjective(expr, GRB.MINIMIZE);
			}
			gmodel.optimize();
		} catch (GRBException e) {
			System.out.println("public void optimize: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		
	}

	@Override
	public String solverstats(Object milpsolver) {
		return ""; 
	}

	@Override
	public void setTimeLimit(Object solver, long t) {
		GRBModel s = (GRBModel) solver;
		try {
		s.getEnv().set(GRB.DoubleParam.TimeLimit,((double)(t/1000)));
		} catch (GRBException e) {
			System.out.println("public void setTimeLimit: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	@Override
	public double getExecutionTime(Object solver) {
		double t = 0.0;
		GRBModel s = (GRBModel) solver;
		try {
			t = s.get(GRB.DoubleAttr.Runtime);
			} catch (GRBException e) {
				System.out.println("public void setTimeLimit: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
			}
		return t;
	}

	@Override
	public String[] otherStatistics(Object solver) {
		String[] res = new String[2];
		GRBModel s = (GRBModel) solver;
		try {
			res[0] = "gap = " + s.get(GRB.DoubleAttr.MIPGap);
			res[1] = "\nobj bound = " + s.get(GRB.DoubleAttr.ObjBound);
			} catch (GRBException e) {
				System.out.println("public void setTimeLimit: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
			}
		return res;
	}

	/* (non-Javadoc)
	 * @see solver.engines.InterfaceI#getSolutionCount()
	 */
	@Override
	public int getSolutionCount(Object solver) {
		GRBModel s = (GRBModel) solver;
		try {
			return s.get(GRB.IntAttr.SolCount);
		} catch (GRBException e) {
			System.out.println("public void setTimeLimit: Gurobi error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
		return 0;
	}

}
