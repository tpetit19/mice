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

package solver.engines.text;

import gurobi.GRBVar;
import solver.LinExpr;
import solver.SolverPolicy;
import solver.engines.InterfaceI;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;
import solver.variables.MRealVar;
import solver.variables.VarI;

/**
 * Engine displaying the linear model in text mode. 
 */

public class TextModel implements InterfaceI {

	TextLog log; 
	String output; 
	
	public String getOutput() {
		return output;
	}
	
	@Override
	public Object createSolver() {
		log = new TextLog(); 
		output = ""; 
		log.domains = "Domains of integer/real variables:\n";
		log.datadom = "\nDomain representation:\n"; 
		log.data = "\nMILP Model:\n";
		return log; 
	}

	@Override
	public void add(Object milpsolver, LinExpr l, String op, double cst, String ctrName) {
		TextLog smodel = (TextLog) milpsolver;
		smodel.data = smodel.data + l.toString();
		smodel.data += " " + op + " " + cst + "\n";
	}

	/* (non-Javadoc)
	 * @see solver.engines.InterfaceI#addForDomain(java.lang.Object, solver.LinExpr, java.lang.String, double, java.lang.String)
	 */
	@Override
	public void addForDomain(Object milpsolver, LinExpr l, String op, double cst, String ctrName) {
		TextLog smodel = (TextLog) milpsolver;
		smodel.datadom = smodel.datadom + l.toString();
		smodel.datadom += " " + op + " " + cst + "\n";
	}

	@Override
	public Object createBinVar(MBinVar var, double obj) {
		//log.domains += "D(" + var.getName() + ") = { 0, 1 }\n";
		return null;
	}

	@Override
	public Object createIntVar(MIntVar var, double obj) {
		int[] dom = var.getDomain();
		MBinVar[] bools = var.getBools();
		log.datadom += "x"+var.getSolver().getintid()+" = ";
		for(int i=0; i<dom.length; i++) {
			if(i>0) {
				log.datadom += " + ";
			}
			log.datadom += dom[i]+"*"+bools[i].getName();
		}
		log.datadom += "\n";
		log.domains += "D(" + var.getName() + ") = {";
		for(int i=0; i<dom.length; i++) {
			if(i>0) {
				log.domains += ","; 
			}
			log.domains += " "+dom[i];
		}
		log.domains += " }\n";
		return null;
	}

	/* (non-Javadoc)
	 * @see solver.engines.InterfaceI#createMathematicalIntVar(solver.variables.MathIntVar, double)
	 */

	@Override
	public Object createMathematicalIntVar(MMathIntVar var, double obj) {
		log.datadom += "x"+var.getSolver().getintid()+" >= "+var.getMin() + "\n";
		log.datadom += "x"+var.getSolver().getintid()+" <= "+var.getMax() + "\n";
		log.domains += "D(" + var.getName() + ") = [ "+var.getMin()+", "+var.getMax()+" ]\n";
		return null;
	}

	@Override
	public Object createRealVar(MRealVar var, double obj) {
		log.datadom += "r"+var.getSolver().getrealid()+" >= "+var.getMin() + "\n";
		log.datadom += "r"+var.getSolver().getrealid()+" <= "+var.getMax() + "\n";
		log.domains += "D(" + var.getName() + ") = [ "+var.getMin()+", "+var.getMax()+" ]\n";
		return null;
	}

	@Override
	public int getIntValue(MIntVar var) {
		return 0;
	}

	@Override
	public int getBinValue(MBinVar var) {
		return 0;
	}

	@Override
	public double getRealValue(MRealVar var) {
		return 0.0;
	}

	@Override
	public void solve(Object milpsolver) {
		output += milpsolver;
	}

	@Override
	public void optimize(Object milpsolver, VarI var, boolean opt) {
		output += "Objective: " + var.getName() + " to be";
		if(opt==SolverPolicy.MAXIMIZE) {
			output += " maximized\n";
		} else {
			output += " minimized\n";
		}
		output += milpsolver;
	}

	@Override
	public String solverstats(Object milpsolver) {
		return "";
	}

	@Override
	public void setTimeLimit(Object solver, long t) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getExecutionTime(Object solver) {
		// TODO Auto-generated method stub	
		return 0.0;
	}

	@Override
	public String[] otherStatistics(Object solver) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object createFixedVar(MIntVar var, int value, double obj) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see solver.engines.InterfaceI#getSolutionCount(java.lang.Object)
	 */
	@Override
	public int getSolutionCount(Object solver) {
		// TODO Auto-generated method stub
		return 0;
	}


}

