package nl.uu.cs.ape.sat.automaton;

import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;

/***
 * The {@code State} class is used to represent the states in module and type automatons. Automaton corresponds to the structure of the possible solutions of the synthesis, i.e. it represents the structure that the provided solutions will follow.
 * <br><br>
 * Labeling of the automaton is provided in /APE/res/WorkflowAutomaton_Implementation.png
 * 
 * @author Vedran Kasalica
 *
 */
public class State implements PredicateLabel {
	


	private final String stateName;
	private final int stateNumber;
	private final int absoluteStateNumber;
	private final WorkflowElement workflowStateType;
	
	/**
	 * Creates a state that corresponds to a state of the overall solution workflow.
	 * @param blockNumber - corresponds to the block number within the type automaton (not applicable for the module automaton)
	 * @param stateNumber - corresponds to the state number within block
	 * @param input_branching - max number of branching
	 * @param workflowStateType - parameter determining the state type:
	 */
	public State(WorkflowElement workflowStateType, Integer blockNumber, int stateNumber, int input_branching) {
		
		this.stateName = WorkflowElement.getStringShorcut(workflowStateType, blockNumber, stateNumber);
		this.stateNumber = stateNumber;
		this.absoluteStateNumber = calculateAbsStateNumber(blockNumber, stateNumber, input_branching, workflowStateType);
		this.workflowStateType = workflowStateType;
	}


	@Override
	public int hashCode() {
		return stateName.hashCode() + absoluteStateNumber;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		return this.stateName.equals(other.getPredicateID()) && (absoluteStateNumber == other.absoluteStateNumber);
	}


	/**
	 * Returns the text representing the state (e.g. M04 or T02.31)
	 * @return String representation of the state.
	 */
	public String getPredicateID() {
		return stateName;
	}

	/**
	 * Returns the order number of the state in the respective array of states.
	 * @return Order number of the state (within the block).
	 */
	public int getStateNumber() {
		return stateNumber;
	}

	/**
	 * Returns the absolute order number of the state within the whole workflow. Unlike {@link getStateNumber}, this function returns number that can be used to compare ordering of any 2 states in the system,
	 * disregarding the block or their type (data type, tool, etc.). {@code NULL} state has AbsoluteStateNumber -1.
	 * @return Non-negative number that corresponds to the absolute order number of the state or -1 for {@code NULL} state.
	 */
	public int getAbsoluteStateNumber() {
		return absoluteStateNumber;
	}
	
	/**
	 * Get the type of the state (e.g. tool, memory type, etc.)
	 * @return {@link WorkflowElement} that describes the state.
	 */
	public WorkflowElement getWorkflowStateType() {
		return workflowStateType;
	}


	/**
	 * Function used to calculate the absolute order number of a state based on the information regarding its block number, order number within the block and type of the state.
	 * @param blockNumber - corresponds to the block number within the type automaton (not applicable for the module automaton)
	 * @param stateNumber - corresponds to the state number within block
	 * @param input_branching - max number of branching
	 * @param typeOfTheState - parameter determining the state type:
	 * <br>MEMORY_TYPE corresponds to the Memory Type State, 
	 * <br>USED_TYPE corresponds to the Used Type State, 
	 * <br>MODULE corresponds to the Module/Tool State
	 * @return The calculated absolute order number of the state.
	 */
	private static int calculateAbsStateNumber(Integer blockNumber, int stateNumber, int input_branching, WorkflowElement typeOfTheState) {
		int absOrderNumber = -1;
		
		if (typeOfTheState == WorkflowElement.MEMORY_TYPE) {		/* Case: Memory Type State */
			absOrderNumber = (blockNumber * input_branching * 2) + blockNumber + stateNumber;
		} else if (typeOfTheState == WorkflowElement.USED_TYPE) {	/* Case: Used Type State */
			absOrderNumber = (blockNumber * input_branching * 2) + blockNumber + input_branching + stateNumber;
		} else if (typeOfTheState == WorkflowElement.MODULE) {		/* Case: Module/Tool State */
			absOrderNumber = (stateNumber * input_branching * 2) + stateNumber - 1;
		}
		
		return absOrderNumber;
	}



	
}
