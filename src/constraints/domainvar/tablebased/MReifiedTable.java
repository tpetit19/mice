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

package constraints.domainvar.tablebased;

import constraints.ReifiedI;
import solver.LinExpr;
import solver.MSolver;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.VarI;

/**
 *  Compact reified table constraint. 
 *  Works both for an original table based on allowed or based on forbidden tuples. 
 */

public class MReifiedTable implements TableI, ReifiedI {

	protected MIntVar[] vars;
	protected int[][] tuples;
	protected MBinVar bool; 
	protected int positive;
	MBinVar[] tbool; 
	MIntVar[] tuplevar;

	public MReifiedTable(TableI ctr, MBinVar bool) {
		this.vars = ctr.getVars();
		this.tuples = ctr.getTuples(); 
		this.bool = bool;
		this.positive = ctr.positive();
		for(int t=0; t<tuples.length; t++) {
			this.tuples[t] = tuples[t].clone();
		}
	}

	@Override
	public void build() {
		MSolver solver = getSolver();
		int n = vars.length; 
		
		// number of tuples
		
		int size = 0;
		for(int t=0; t<tuples.length; t++) {
			int[] indexval = new int[n];
			int i = 0;
			while(i<n) {
				indexval[i] = vars[i].getIndex(tuples[t][i]);
				if(indexval[i]==-1) {
					i = n;
				}
				i++;
			}
			if(i==n) { 
				size++;
			}
		}
		
		tuplevar = new MIntVar[size];
		int pos = 0;
		for(int t=0; t<tuples.length; t++) {
			int[] indexval = new int[n];
			int i = 0;
			while(i<n) {
				indexval[i] = vars[i].getIndex(tuples[t][i]);
				if(indexval[i]==-1) {
					i = n;
				}
				i++;
			}
			if(i==n) { 
				tuplevar[pos] = new MIntVar(0,n, solver);
				LinExpr sumt = new LinExpr();
				for(int j=0; j<tuples[t].length; j++) {
					int indexvalue = vars[j].getIndex(tuples[t][j]);
					sumt.addTerm(1,(VarI)vars[j].getBools()[indexvalue]);
					System.out.println("var considered: " + vars[j].getName() + "=>" +  vars[j].getBools()[indexvalue].getName());
				}
				solver.add(sumt,"=",tuplevar[pos],"TablePos:ctr:"+pos);
				pos++; 
			}
		}
		
		// at least one tuple is complete; the atmost side in ensured by domain constraints
		// one boolean tbool[i] per tuple, equal to 1 iff assignment of vars == the tuple
		
		tbool = new MBinVar[size]; 
		for(int i=0; i<size; i++) {
			tbool[i] = new MBinVar(solver); 
		}
		LinExpr[] tbooln = new LinExpr[size]; 
		for(int i=0; i<size; i++) {
			tbooln[i] = new LinExpr();
			tbooln[i].addTerm(n, tbool[i]);  // tbooln[i] = n*tbool[i]
			solver.add(tbooln[i],"<=",tuplevar[i],"TablePos:link-sum-finalbinary:tbooln:"+i);   
			// n*tbool[i] <= sum of bools equal to 1 in tuple, means that if tbool[i] == 1 then the tuple
			// is satisfied, if tbool[i] == 0 then the tuple can be satified or not
		}
		
		LinExpr[] tboolnsup = new LinExpr[size]; 
		for(int i=0; i<size; i++) {
			tboolnsup[i] = new LinExpr();
			tboolnsup[i].addTerm(-n, tbool[i]);  
			tboolnsup[i].addTerm(1, tuplevar[i]);   // n 
			solver.add(tboolnsup[i],"<=",n-1,"TablePos:link-sum-finalbinary:tboolnsup:"+i);   //  tuplevar - n*tbool[i] < n, means that tbool[i] cannot == 0 if the tuple is satisfied
		}
		
		
		// Reification
		
		LinExpr uniquetuple = new LinExpr();
		for(int i=0; i<size; i++) {
			uniquetuple.addTerm(1, tbool[i]);   // si l'un d'entre eux est egal a un, forcement un tuple complet								
		}
		
		// tbool[i]>0  OR tuplevar[i] < n
		
		if(positive==TableI.positive) {
			uniquetuple.addTerm(-1, bool);
			solver.add(uniquetuple,"=",0,"ReifiedTable: Reification constraint"); // bool = uniquetuple    <=> uniquetuple - bool = 0
		} else {
			uniquetuple.addTerm(1, bool);
			solver.add(uniquetuple,"=",1,"ReifiedTable: Reification constraint"); // 1 - bool = uniquetuple    <=>  uniquetuple + bool = 1
		}
		 
	}

	@Override
	public MSolver getSolver() {
		return vars[0].getSolver();
	}

	@Override
	public int[][] getTuples() {
		return tuples;
	}
	
	@Override
	public int positive() {
		return TableI.positive;
	}

	@Override
	public MIntVar[] getVars() {
		return vars;
	}

	@Override
	public MBinVar getTruthVar() {
		return bool;
	}
	
	public MBinVar[] getBool() {
		return tbool; 
	}
	
	public MIntVar[] getTupleVar() {
		return tuplevar; 
	}
	
}

