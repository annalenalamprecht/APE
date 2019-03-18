package nl.uu.cs.ape.sat.constraints;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.formulas.*;

/**
 * Implements constraints of the form:<br/>
 * <br/>
 * If we use module <b>parameters[0]</b>, then use <b>parameters[1]</b>
 * consequently using the function {@link #getConstraint}.
 * 
 * @author Vedran Kasalica
 *
 */
public class Constraint_if_then_module extends ConstraintTemplate {


	public Constraint_if_then_module(int parametersNo, String description) {
		super(parametersNo, description);
	}

	@Override
	public String getConstraint(String[] parameters, AllModules allModules, AllTypes allTypes, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		if (parameters.length != 2) {
			super.throwParametersError(parameters.length);
			return null;
		}
		String constraint = "";
		AbstractModule if_module = allModules.get(parameters[0]);
		AbstractModule then_module = allModules.get(parameters[1]);
		if (if_module == null || then_module == null) {
			System.err.println("Constraint argument does not exist in the tool taxonomy.");
			return null;
		}
		constraint = SLTL_formula.ite(if_module, then_module, moduleAutomaton, typeAutomaton, mappings);

		return constraint;
	}

}