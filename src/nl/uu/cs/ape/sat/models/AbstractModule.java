package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.uu.cs.ape.sat.models.constructs.Predicate;
/**
 *  The {@code AbstractModule} class represents modules/tools that can be used. {@code AbstractModules} can be actual tools or their abstraction classes.
 *  
 * @author Vedran Kasalica
 *
 */
public class AbstractModule extends Predicate {

	private String moduleName;
	private String moduleID;
	/**
	 *  Set of all the modules that are subsumed by the abstract module (null if the module is a tool)
	 */
	private Set<String> subModules;
	/**
	 * Describes the data node in from the taxonomy. The type can represent a root type, subroot type, an abstract or a simple (implemented leaf) type, or be an empty type.
	 */
	private NodeType nodeType;

	/**
	 * Creates an abstract module from @moduleName and @moduleID. If @isTool is
	 * true, module is an actual tool, otherwise it's an abstract/non-tool module.
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param isTool
	 *            - determines whether the module represents a tool
	 */
	public AbstractModule(String moduleName, String moduleID, String rootNode, NodeType nodeType) {
		super(rootNode, nodeType);
		this.moduleName = moduleName;
		this.moduleID = moduleID;
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subModules = new HashSet<String>();
		}
	}

	/**
	 * Generate an AbstractModule from an existing one. In order to provide means
	 * for combining Module and AbstractModule objects
	 * 
	 * @param abstractModule
	 *            - abstract module that is being copied
	 * @param isTool
	 *            - determines whether the module represents a tool
	 */
	public AbstractModule(AbstractModule abstractModule, NodeType nodeType) {
		super(abstractModule.getRootNode(), (nodeType != null) ? nodeType : abstractModule.getNodeType());
		this.moduleName = abstractModule.getModuleName();
		this.moduleID = abstractModule.getModuleID();
		
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			this.subModules = abstractModule.getSubModules();
			if (this.subModules == null) {
				this.subModules = new HashSet<String>();
			}
		}
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * True if the module is a tool, false otherwise.
	 * 
	 * @return true if the (abstract) module represent an actual tool
	 */
	public boolean isTool() {
		return this.nodeType == NodeType.LEAF;
	}
	
	/**
	 * True if the module is the root module, false otherwise.
	 * 
	 * @return true if the (abstract) module represent the root module
	 */
	public boolean isRoot() {
		return this.nodeType == NodeType.ROOT;
	}

	public void setToTool() {
		this.nodeType = NodeType.LEAF;
	}

	/**
	 * Returns null. Abstract classes do not have input types.
	 * 
	 * @return null
	 */
	public List<Types> getModuleInput() {
		return null;
	}

	/**
	 * Returns null. Abstract classes do not have output types.
	 * 
	 * @return null
	 */
	public List<Types> getModuleOutput() {
		return null;
	}

	/**
	 * Return a printable String version of the abstract module.
	 * 
	 * @return abstract module as printable String
	 */
	public String print() {

		return getModuleID() + ", " + getModuleName();
	}

	/**
	 * Print the ID of the AbstractModule
	 * 
	 * @return module ID as a {@link String}
	 */
	public String printShort() {
		return moduleID;
	}

	@Override
	public String getPredicate() {
		return moduleID;
	}

	@Override
	public String getType() {
		return "abstract module";
	}
	
	/**
	 * Adds a submodule to an abstract/non-tool module, if it was not added present
	 * already.
	 * 
	 * @param module
	 *            - module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(AbstractModule module) {
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			subModules.add(module.getModuleID());
			return true;
		} else {
			System.err.println("Cannot add submodules to a tool/leaf module!");
			return false;
		}
	}

	/**
	 * Adds a submodule to an abstract/non-tool module, if not present already.
	 * 
	 * @param moduleID
	 *            - ID of the module that will be added as a subclass
	 * @return True if submodule was added, false otherwise.
	 */
	public boolean addSubModule(String moduleID) {
		if (!(nodeType == NodeType.LEAF || nodeType == NodeType.EMPTY)) {
			return subModules.add(moduleID);
		} else {
			System.err.println("Cannot add submodules to a tool/leaf module!");
			return false;
		}
	}

	/**
	 * Returns the list of the modules that are directly subsumed by the modules.
	 * 
	 * @return List of the submodules or null in case of a tool/leaf module
	 */
	public Set<String> getSubModules() {
		return subModules;
	}

	@Override
	public boolean equals(Object obj) {
		AbstractModule other = (AbstractModule) obj;
		return this.moduleID.matches(other.moduleID);
	}

	@Override
	public int hashCode() {
		return moduleID.hashCode();
	}

	/**
	 * The class is used to check weather the module with @moduleID was already
	 * introduced earlier on in @allModules. In case it was it returns the item,
	 * otherwise the new element is generated and returned.
	 * 
	 * @param moduleName
	 *            - module name
	 * @param moduleID
	 *            - unique module identifier
	 * @param isTool
	 *            - determines whether the module represents a tool
	 * @param allModules
	 *            - set of all the modules created so far
	 * @return the Abstract Module representing the item.
	 */
	public static AbstractModule generateModule(String moduleName, String moduleID, String rootNode, NodeType nodeType,
			AllModules allModules) {
		return allModules.addModule(new AbstractModule(moduleName, moduleID, rootNode, nodeType));
	}

	/**
	 * Print the tree shaped representation of the module taxonomy.
	 * 
	 * @param str - string that is helping the recursive function to distinguish between the tree levels
	 * @param allModules - set of all the modules
	 */
	public void printTree(String str, AllModules allModules) {
		System.out.println(str + printShort() + "[" + getNodeType()+ "]");
		if (subModules != null)
			for (String moduleID : subModules) {
				allModules.get(moduleID).printTree(str + " > ", allModules);
			}
	}

}