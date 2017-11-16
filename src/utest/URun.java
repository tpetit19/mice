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

package utest;

import java.util.ArrayList;

public class URun {
	
	public static void main(String[] args) {
		ArrayList<UTestI> test = new ArrayList<UTestI>();
		// Add tests
		test.add(new TablePosUT());
		test.add(new SortednessUT());
		test.add(new InterDistanceUT()); 
		// Display
		System.out.flush();
		int size = test.size();
		for(int i=0; i<size; i++) {
			System.out.println(test.get(i).all());
		}
	}

}
