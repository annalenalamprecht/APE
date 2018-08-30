package SAT.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.ModuleState;
import SAT.automaton.TypeAutomaton;
import SAT.automaton.TypeBlock;
import SAT.automaton.TypeState;

/**
 * The {@code AllModules} class represent the set of all modules/tools that can be part of our program. Each of them is either {@link Module} or {@link AbstractModule}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllModules{

	private Map<String, AbstractModule> modules;

	public AllModules() {
		this.modules = new HashMap<>();
	}

	/**
	 * Create a map-set containing all modules from @modules, omitting the
	 * duplicates.
	 * 
	 * @param modules
	 */
	public AllModules(Collection<? extends AbstractModule> modules) {
		this.modules = new HashMap<>();
		for (AbstractModule module : modules) {
			this.addModule(module);
		}
	}

	public Map<String, AbstractModule> getModules() {
		return modules;
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional
	 * operation) and returns it. More formally, adds the specified element e to
	 * this set if the set contains no element e2 such that (e==null ? e2==null :
	 * e.equals(e2)). If this set already contains the element, the call leaves the
	 * set unchanged and returns the existing element. In combination with the
	 * restriction on constructors, this ensures that sets never contain duplicate
	 * elements. It also check whether the new element extends the existing one, it
	 * that case the existing one is replaced by the extended one.
	 * 
	 * @param module
	 *            - the element that needs to be added
	 * @return The same element if it's a new one or the existing element if this
	 *         set contains the specified element.
	 */
	public AbstractModule addModule(AbstractModule module) {
		AbstractModule tmpModule = modules.get(module.getModuleID());
		if (module instanceof Module && (tmpModule != null)) {
			if (tmpModule instanceof Module) {
				return tmpModule;
			} else {
				Module newModule = new Module(((Module) module), tmpModule);
				/*
				 * swap the AbstractModule with the Module
				 */
				swapAbstractModule2Module(newModule, tmpModule);
				return module;
			}
		} else {
			if (tmpModule != null) {
				return tmpModule;
			} else {
				this.modules.put(module.getModuleID(), module);
				return module;
			}
		}
	}

	/**
	 * Removes the {@code AbstractModule} from the set of all modules and adds the {@link Module}
	 * element (or vice versa). Swaps the objects in the set of all modules.
	 * 
	 * @param newModule
	 *            - object that will be added
	 * @param oldModule
	 *            - object that will be removed
	 */
	public void swapAbstractModule2Module(AbstractModule newModule, AbstractModule oldModule) {
		this.modules.remove(oldModule.getModuleID());
		this.modules.put(newModule.getModuleID(), newModule);
	}

	/**
	 * Returns the module to which the specified key is mapped to, or {@code null} if the moduleID has no mappings.
	 * 
	 * @param moduleID
	 *            - the key whose associated value is to be returned
	 * @return {@link AbstractModule} or {@link Module} to which the specified key is mapped to, or {@code null} if the moduleID has no mappings
	 */
	public AbstractModule get(String moduleID) {
		return this.modules.get(moduleID);
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param module
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(AbstractModule module) {
		return modules.containsKey(module.getModuleID());
	}

	public int size() {
		return modules.size();
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	private List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<>();

		List<AbstractModule> iterator = new ArrayList<>();
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			if (module.isTool())
				iterator.add(module);
		}

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Return the CNF representation of the input type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Pipeline Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String inputPipelineCons(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {

		String constraints = "";
		// setting up input constraints (Pipeline)

		// for each module
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			// that is a Tool and has input
			if ((module instanceof Module) && !module.getModuleInput().isEmpty()) {
				// iterate through all the states
				for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// that are not the first state (no input state)
					if (!moduleState.isFirst()) {
						// and for each input type of that module
						for (Type type : module.getModuleInput()) {
							// if module was used in the state
							constraints += "-" + mappings.add(module.getPredicate(), moduleState.getStateName()) + " ";
							// require the type to be used in at least one of the
							// directly preceding input states
							for (TypeState typeState : typeAutomaton.getBlock(moduleNo - 1).getTypeStates()) {
								constraints += mappings.add(type.getPredicate(), typeState.getStateName()) + " ";
							}
							constraints += "0\n";
						}
					} else {
						// tools that require INPUT cannot be in the first module state
						constraints += "-" + mappings.add(module.getPredicate(), moduleState.getStateName()) + " 0\n";
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * Return the CNF representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the General Memory Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String inputGenMemoryCons(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton,
			AtomMapping mappings) {

		String constraints = "";
		// setting up input constraints (General Memory)

		// for each module
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			// that is a Tool and has input
			if ((module instanceof Module) && !module.getModuleInput().isEmpty()) {
				// iterate through all the states
				for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// that are not the first state (no input state)
					if (!moduleState.isFirst()) {
						// and for each input type of that module
						for (Type type : module.getModuleInput()) {
							// if module was used in the state
							constraints += "-" + mappings.add(module.getPredicate(), moduleState.getStateName()) + " ";
							// require the type to be used in at least one of the
							// preceding input states
							for (int i = 0; i < moduleNo; i++) {
								for (TypeState typeState : typeAutomaton.getBlock(i).getTypeStates()) {
									constraints += mappings.add(type.getPredicate(), typeState.getStateName()) + " ";
								}
							}
							constraints += "0\n";
						}
					} else {
						// tools that require INPUT cannot be in the first module state
						constraints += "-" + mappings.add(module.getPredicate(), moduleState.getStateName()) + " 0\n";
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * Return the CNF representation of the output type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param emptyType - represents absence of types
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String outputCons(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, Type emptyType,
			AtomMapping mappings) {

		String constraints = "";

		// for each module
		for (Entry<String, AbstractModule> mapModule : modules.entrySet()) {
			AbstractModule module = mapModule.getValue();
			// that is a Tool
			if ((module instanceof Module)) {
				// iterate through all the states
				for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// and for each state and output state of that module state
					List<TypeState> currOutputStates = typeAutomaton.getBlock(moduleNo).getTypeStates();
					List<Type> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						// if module was used in the module state
						constraints += "-" + mappings.add(module.getPredicate(), moduleState.getStateName()) + " ";
						// require type to be used in one of the directly
						// proceeding output states if it exists, otherwise use empty type
						if (i < moduleOutputs.size()) {
							constraints += mappings.add(moduleOutputs.get(i).getPredicate(),
									currOutputStates.get(i).getStateName()) + " 0\n";
						} else {
							constraints += mappings.add(emptyType.getPredicate(),
									currOutputStates.get(i).getStateName()) + " 0\n";
						}
					}
				}
			}
		}

		return constraints;
	}

	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a pipeline or
	 * general memory approach.
	 * 
	 * @param moduleAutomaton
	 *            - represents the module automaton
	 * @param typeAutomaton
	 *            - represent the type automaton
	 * @param pipeline
	 *            - if true pipeline approach, otherwise the general memory approach
	 *            is used
	 * @param emptyType - represents absence of types
	 * @param mappings
	 * @return {@link String} representation of constraints regarding the required INPUT and
	 *         OUTPUT types of the modules
	 */
	public String modulesConstraints(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, boolean pipeline,
			Type emptyType, AtomMapping mappings) {

		String constraints = "";
		if (pipeline) {
			constraints += inputPipelineCons(moduleAutomaton, typeAutomaton, mappings);
		} else {
			constraints += inputGenMemoryCons(moduleAutomaton, typeAutomaton, mappings);
		}

		constraints += outputCons(moduleAutomaton, typeAutomaton, emptyType, mappings);
		return constraints;
	}

	/**
	 * Generating the mutual exclusion constraints for each pair of tools
	 * from modules (excluding abstract modules from the taxonomy) in each state
	 * of moduleAutomaton.
	 * 
	 * @param moduleAutomaton
	 * @param mappings
	 * @return {@link String} representation of constraints
	 */
	public String moduleMutualExclusion(ModuleAutomaton moduleAutomaton, AtomMapping mappings) {

		String constraints = "";

		for (Pair pair : getToolPairs()) {
			for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
				constraints += "-" + mappings.add(pair.getFirst().getPredicate(), moduleState.getStateName()) + " ";
				constraints += "-" + mappings.add(pair.getSecond().getPredicate(), moduleState.getStateName()) + " 0\n";
			}
		}

		return constraints;
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootModuleID
	 *            - represent the ID of the root module in the module taxonomy
	 * @param moduleAutomaton
	 *            - module automaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	public String moduleMandatoryUsage(String rootModuleID, ModuleAutomaton moduleAutomaton, AtomMapping mappings) {
		String constraints = "";

		AbstractModule module = modules.get(rootModuleID);
		for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
			constraints += mappings.add(module.getPredicate(), moduleState.getStateName()) + " 0\n";
		}

		return constraints;
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 * 
	 * @param rootModuleID
	 *            - represent the ID of the root module in the module taxonomy
	 * @param moduleAutomaton
	 *            - module automaton
	 * @param mappings
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public String moduleEnforceTaxonomyStructure(String rootModuleID, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings) {

		String constraints = "";
		for (ModuleState moduleState : moduleAutomaton.getModuleStates()) {
			constraints += moduleEnforceTaxonomyStructureForState(rootModuleID, moduleAutomaton, mappings, moduleState);
		}
		return constraints;
	}

	/**
	 * Providing the recursive method used in {@link #moduleEnforceTaxonomyStructure(String, ModuleAutomaton, AtomMapping) moduleEnforceTaxonomyStructure}.
	 */
	private String moduleEnforceTaxonomyStructureForState(String rootModuleID, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings, ModuleState moduleState) {
		AbstractModule currModule = modules.get(rootModuleID);
		String constraints = "";
		String superModule_state = mappings.add(currModule.getPredicate(), moduleState.getStateName()).toString();
		String currConstraint = "-" + superModule_state + " ";
		List<String> subModules_States = new ArrayList<>();
		if (!(currModule.getSubModules() == null || currModule.getSubModules().isEmpty())) {
			for (String subModuleID : currModule.getSubModules()) {
				AbstractModule subModule = modules.get(subModuleID);
				String subModule_State = mappings.add(subModule.getPredicate(), moduleState.getStateName()).toString();
				currConstraint += subModule_State + " ";
				subModules_States.add(subModule_State);
				constraints += moduleEnforceTaxonomyStructureForState(subModuleID, moduleAutomaton, mappings,
						moduleState);
			}
			currConstraint += "0\n";
			for (String subModule_State : subModules_States) {
				currConstraint += "-" + subModule_State + " " + superModule_state + " 0\n";
			}
			return currConstraint + constraints;
		} else {
			return "";
		}
	}

}
