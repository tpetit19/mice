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

import solver.LinExpr;
import solver.MSolver;
import solver.variables.MBinVar;
import solver.variables.MIntVar;
import solver.variables.VarI;

/**
 * Table constraint stated by allowed tuples. 
 */

public class MTablePos implements TableI {

	protected MIntVar[] vars;
	protected int[][] tuples;

	public MTablePos(MIntVar[] vars, int[][] tuples) {
		if(tuples.length==0 || tuples[0].length!=vars.length) {
			throw new Error("Table: tuple set size should be >0 and tuples length to variable array size.");
		}
		this.vars = vars;
		this.tuples = new int[tuples.length][vars.length]; 
		for(int t=0; t<tuples.length; t++) {
			this.tuples[t] = tuples[t].clone();
		}
	}

	public static MIntVar[] binMap(MBinVar[] vars) {
		MSolver solver = vars[0].getSolver();
		MIntVar[] ivars = new MIntVar[vars.length];
		for(int i=0; i<ivars.length; i++) {
			ivars[i] = new MIntVar(0,1,solver);
			LinExpr l = new LinExpr();
			l.addTerm(1, ivars[i]);
			l.addTerm(-1, vars[i]);
			solver.add(l,"=",0);
		}
		return ivars;
	}

	public MTablePos(MBinVar[] vars, int[][] tuples) {
		this(binMap(vars),tuples);
	}

	//@Override
	public void build2() {  // less effective? 
		MSolver solver = getSolver();
		int n = vars.length; 
		
		// number of allowed tuples

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
		
		int pos = 0;
		LinExpr[] sumt = new LinExpr[size];
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
				sumt[pos] = new LinExpr();
				for(int j=0; j<tuples[t].length; j++) {
					int indexvalue = vars[j].getIndex(tuples[t][j]);
					sumt[pos].addTerm(-1,(VarI)vars[j].getBools()[indexvalue]);
				}
				pos++; 
			}
		}
		MBinVar[] tbool = new MBinVar[size]; 
		for(int i=0; i<size; i++) {
			tbool[i] = new MBinVar(solver); 
		}
		for(int i=0; i<size; i++) {
			sumt[i].addTerm(n, tbool[i]);  // tbooln[i] = n*tbool[i]
			solver.add(sumt[i],"<=",0,"TablePos:link-sum-finalbinary:tbooln:"+i); // tbooln[i] <= sum of bools equal to 1 in tuple
		}
		LinExpr uniquetuple = new LinExpr();
		for(int i=0; i<size; i++) {
			uniquetuple.addTerm(1, tbool[i]);
		}
		solver.add(uniquetuple,">=",1,"TablePos:one-satisfied-tuple"); // at least one tuple is complete
		
	}

	
	public void build() {
		MSolver solver = getSolver();
		int n = vars.length; 

		// number of allowed tuples

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

		// constraints linking an integer var to booleans of each tuple
		// TODO: we can remove tuplevar that only states the value of a sum

		MIntVar[] tuplevar = new MIntVar[size];
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
				}
				solver.add(sumt,"=",tuplevar[pos],"TablePos:ctr:"+pos);
				pos++; 
			}
		}

		// at least one tuple is complete; the atmost side in ensured by domain constraints
		// one boolean tbool[i] per tuple, equal to 1 iff assignment of vars == the tuple
		MBinVar[] tbool = new MBinVar[size]; 
		for(int i=0; i<size; i++) {
			tbool[i] = new MBinVar(solver); 
		}
		LinExpr[] tbooln = new LinExpr[size]; 
		for(int i=0; i<size; i++) {
			tbooln[i] = new LinExpr();
			tbooln[i].addTerm(n, tbool[i]);  // tbooln[i] = n*tbool[i]
			solver.add(tbooln[i],"<=",tuplevar[i],"TablePos:link-sum-finalbinary:tbooln:"+i); // tbooln[i] <= sum of bools equal to 1 in tuple
		}
		LinExpr uniquetuple = new LinExpr();
		for(int i=0; i<size; i++) {
			uniquetuple.addTerm(1, tbool[i]);
		}
		solver.add(uniquetuple,">=",1,"TablePos:one-satisfied-tuple"); // at least one tuple is complete
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

}

