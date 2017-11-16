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

package choco4.samples;

import java.util.Random;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

import choco4.Decomposer;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;

/**
 * Simple test of Choco 4 decomposer with El Kholi decomposition of cumulative
 * http://www.choco-solver.org
 */

public class CumulativeTest {

	// Choco 4
	private IntVar makespan;
	private IntVar[] start;
	private IntVar[] end;
	private IntVar[] dur;
	private IntVar[] h; 
	private IntVar capa; 
	
	// Mice
	private MIntVar mmakespan;
	/*private MIntVar[] mstart;
	private MIntVar[] mdur;
	private MIntVar[] mend; 
	private double[] conso;
	private MIntVar mcapa;*/
	
	public Model chocoModel(int seed) {
		int n = 6;
		int horizon = 500; 
		Model model = new Model();
		capa = model.intVar(6,6); 
		start = new IntVar[n];
		end = new IntVar[n];
		dur = new IntVar[n];
		h = new IntVar[n];
		Task[] task = new Task[n];
		Random r = new Random(seed);
		// for displaying solutions: conso
		double[] conso = new double[h.length];
		for(int i=0;i<n;i++){
			int val = r.nextInt(15)+4;
			dur[i] = model.intVar(val-3,val);
			start[i] = model.intVar(0,(int)(horizon-dur[i].getLB()));
			conso[i] = r.nextInt(5)+1;
			h[i] = model.intVar((int)conso[i],(int)conso[i]);
			end[i] = model.intVar(dur[i].getLB(),horizon);
			task[i] = new Task(start[i],dur[i],end[i]);
		}
		makespan = model.intVar(0,horizon);
		model.post(model.cumulative(task, h, capa)); 
		model.post(model.max(makespan, end)); 
		return model;
	}
	
	public String chocoSolve(int seed) {
		Model model = chocoModel(seed);
		model.setObjective(Model.MINIMIZE,makespan); 
		String s = "";
		while(model.getSolver().solve()) {
			s = ""+makespan.getValue();
		}
		return s;
	}
	
	public String miceSolve(int seed, String system) {
		Model model = chocoModel(seed);
		Decomposer d = new Decomposer();
		MSolver solver = d.decomposeConstraintSet(model.getCstrs(),new boolean[]{true,true},system);
		solver.optimize(d.findVar(makespan.getName()),SolverPolicy.MINIMIZE);
		if(system==MILPSystem.TEXT) {
			return ((TextModel) solver.getInterface()).getOutput();
		} else {
			mmakespan = (MIntVar) d.findVar(makespan.getName());
			return ""+mmakespan.getValue(); 
		}
	}
	
	public static void main(String[] args) {
		CumulativeTest t = new CumulativeTest(); 
		System.out.println("Choco optimals"); 
		for(int i=0; i<10; i++) {
			System.out.print(" "+t.chocoSolve(i));
		}
		String system = MILPSystem.TEXT; // MILPSystem.TEXT; 
		System.out.println("\nMice optimals/models"); 
		for(int i=0; i<1; i++) {
			System.out.print(" "+t.miceSolve(i,system));
		}
	}
}
