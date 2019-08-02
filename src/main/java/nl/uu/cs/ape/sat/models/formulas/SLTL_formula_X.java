package nl.uu.cs.ape.sat.models.formulas;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.constructs.TaxonomyPredicate;
import nl.uu.cs.ape.sat.models.*;

public abstract class SLTL_formula_X extends SLTL_formula {

	public SLTL_formula_X(TaxonomyPredicate predicate) {
		super(predicate);
	}
	
	public SLTL_formula_X(boolean sign, TaxonomyPredicate formula) {
		super(sign, formula);
	}

}
