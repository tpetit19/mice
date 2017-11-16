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

package constraints;

import solver.variables.MBinVar;

/**
 * Interface for MICE reified constraints
 */

public interface ReifiedI extends ConstraintI {
	public MBinVar getTruthVar();
}

