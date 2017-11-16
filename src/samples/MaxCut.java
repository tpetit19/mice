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
package samples;

import constraints.DomainConstraintFactory;
import solver.LinExpr;
import solver.MSolver;
import solver.SolverPolicy;
import solver.engines.MILPSystem;
import solver.engines.text.TextModel;
import solver.variables.MIntVar;
import solver.variables.MMathIntVar;

/**
 * Max-cut benchmark inspired from rudy instances http://biqmac.uni-klu.ac.at/biqmaclib.html
 * Given an undirected graph with {0,1} weighted edges Gw = (V,Ew), Max-Cut is the problem of finding
 * a cut in G of maximum weight. i.e., split the vertex set in two so that to have a maximum weighted 
 * set S of edges that have an end point in each set.
 */

public class MaxCut {

	public static String model(String system) {
	
	int[][] graph = { // Extracted from Rudy g05_60 instance
			 {0,0,0,0,1,0,0,0,0,1,1,1,0,1,1,1,0,1,1,1,1,0,1},
			 {0,0,1,0,1,0,1,0,0,1,1,1,0,0,0,0,0,0,1,1,0,0,1},
			 {0,0,0,0,1,0,0,1,1,1,0,1,1,0,0,0,0,1,0,0,0,0,0},
			 {0,0,0,0,1,1,1,0,1,0,0,0,1,1,1,1,0,1,0,0,1,1,0},
			 {0,0,0,0,0,1,0,1,0,1,0,1,1,1,1,1,1,0,1,1,1,0,1},
			 {0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,0,1,1,1,0,0,1,0},
			 {0,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,0,0,1},
			 {0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,1,0,1,0,0},
			 {0,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1,1,1,1,1,0,1,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,0,1,1,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,0,0,0,0,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,1,1,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			 {0, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			 {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			};
	
		MSolver solver = new MSolver(system); 
		double t = System.currentTimeMillis();
		System.out.println("build the model...");
		int n = graph.length; 
		MIntVar[] vertices = new MIntVar[n];
		for(int i=0; i<n; i++) {
			vertices[i] = new MIntVar(new int[]{-1,1},solver);  // corresponds to the quadratic constraint x^2 = 1
		}
		MIntVar[][] cut = new MIntVar[n][];
		MIntVar[][] prod = new MIntVar[n][];
		for(int i=0; i<n; i++) {
			cut[i] = new MIntVar[n-i-1];
			prod[i] = new MIntVar[n-i-1];
			for(int j=i+1; j<n; j++) {
				prod[i][j-i-1] = new MIntVar(new int[]{-1,1},solver); 
				cut[i][j-i-1] = new MIntVar(new int[]{0,2},solver); 
			}
		}
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				solver.add(DomainConstraintFactory.times(vertices[i], vertices[j], prod[i][j-i-1]));
				solver.add(DomainConstraintFactory.scalar(new MIntVar[]{prod[i][j-i-1],cut[i][j-i-1]}, new int[]{1,1}, 1, "="));
			}
		}
		LinExpr sum = new LinExpr();
		for(int i=0; i<n; i++) {
			for(int j=i+1; j<n; j++) {
				sum.addTerm(graph[i][j], cut[i][j-i-1]);
			}
		}
		MMathIntVar obj = new MMathIntVar(-n*(n-1)/2,n*(n-1)/2,solver); // weights in [-1,1]
		solver.add(sum,"=",obj);
		double t2 = System.currentTimeMillis();
		System.out.println("BUILDING TIME = "+(t2-t)/1000 + "\nSolve and prove optimal...");
		solver.optimize(obj, SolverPolicy.MAXIMIZE);
		String res = "";
		if(system==MILPSystem.TEXT) {
			res += ((TextModel) solver.getInterface()).getOutput();
		} else {  
			res += "SOLVING TIME: "+solver.getExecutionTime()+"\n"; 
			res += ("obj = "+obj.getValue()+" (corresponds to problem obj = " +(obj.getValue()/2) + ")\n");
			res += "Vertex set S: { ";
			for(int i=0; i<n; i++) {
				int v = vertices[i].getValue();
				if(v==1) { 
					res+=(i+ " ");
				}
			}
			res += "}\nOther vertices: { ";
			for(int i=0; i<n; i++) {
				int v = vertices[i].getValue();
				if(v==-1) { 
					res+=(i+ " ");
				}
			}
			res += "}";
			res += "\nEdges of weight 1 in the cut:\n";
			int pos = 1; 
			for(int i=0; i<n; i++) {
				for(int j=i+1; j<n; j++) {
					if(vertices[i].getValue()!=vertices[j].getValue() && graph[i][j]==1) {
						res += "(" + i + ", "+ j + "); ";
						pos ++;
						if(pos%5==0) {
							pos = 1;
							res += "\n";
						}
					}
				}
			}
			res += "\nEdges of weight 0 in the cut:\n";
			pos = 1; 
			for(int i=0; i<n; i++) {
				for(int j=i+1; j<n; j++) {
					if(vertices[i].getValue()!=vertices[j].getValue() && graph[i][j]==0) {
						res += "(" + i + ", "+ j + "); ";
						pos ++;
						if(pos%5==0) {
							pos = 1;
							res += "\n";
						}
					}
				}
			}
		}
		return res; 
	}
	
	public static void main(String[] args) {
		//String system = MILPSystem.TEXT; 
		String system = MILPSystem.GUROBIPRESOLVE; 
		System.out.println(model(system));
	}
}
