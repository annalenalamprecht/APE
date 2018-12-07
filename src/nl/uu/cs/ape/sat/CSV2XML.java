package nl.uu.cs.ape.sat;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import nl.uu.cs.ape.sat.models.AllModules;
import nl.uu.cs.ape.sat.models.AllTypes;
import nl.uu.cs.ape.sat.models.Module;

public class CSV2XML {

	public static List<String[]> getTuplesFromCSV(String csvFile) {

		List<String[]> constraints = new ArrayList<>();

		try {

			FileReader filereader = new FileReader(csvFile);

			// create csvReader object and skip first Line
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			constraints = csvReader.readAll();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return constraints;
	}

	public static List<Module> readModuleCSV(String file, AllModules allModules, AllTypes allTypes) {

		List<Module> modulesNew = new ArrayList<Module>();

		for (String[] stringModule : getTuplesFromCSV(file)) {
			modulesNew.add(Module.moduleFromString(stringModule, allModules, allTypes));
		}

		return modulesNew;
	}

	public static void csv2xml(String csvPath) {
		try {

			List<String[]> modules = getTuplesFromCSV(csvPath);

			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("functions");
			/*
			 * Iteration trough all the modules described in the CSV
			 */
			for (String[] function : modules) {
				Element module = root.addElement("function").addAttribute("name", function[1]); // function name

				module.addElement("operation").addText(function[0]); // superclass name - operation from the taxonomy
				/*
				 * Check for the input types (if any)
				 */
				if (!function[2].matches("")) {
					String[] stringModuleInputTypes = function[2].split("#");
					Element inputs = module.addElement("inputs");
					for (String inputStr : stringModuleInputTypes) {
						if (!inputStr.matches("")) {
							inputs.addElement("input").addElement("type").addText(inputStr); // creating an input of a
																								// type "inputStr", no
																								// format is specified.
						}
					}

				}
				/*
				 * Check for output types (if any)
				 */
				if (!function[3].matches("")) {
					String[] stringModuleOutputTypes = function[3].split("#");
					Element outputs = module.addElement("outputs");
					for (String outputStr : stringModuleOutputTypes) {
						if (!outputStr.matches("")) {
							outputs.addElement("output").addElement("type").addText(outputStr); // creating an output of
																								// a type "inputStr", no
																								// format is specified.
						}
					}
				}

			}
			// print the document to System.out
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(document);
			// Save the contents to a file
			XMLWriter fileWriter = new XMLWriter(new FileOutputStream("text.xml"), format);
			fileWriter.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void xml2print(String xmlPath) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlPath);

			System.out.println("Root element :" + document.getRootElement().getName());

			List<Node> functionList = document.selectNodes("/functions/function");
			System.out.println("----------------------------");

			for (Node function : functionList) {
				System.out.println("\nCurrent Element :" + function.getName());
				System.out.println("name: " + function.valueOf("@name"));
				System.out.println("op: " + function.selectSingleNode("operation").getText());
				
				List<Node> inputList = function.selectNodes("inputs/input");
				for(Node input : inputList) {
					System.out.println("Input:");
					List<Node> typeList = input.selectNodes("type");
					for(Node type : typeList) {
						System.out.println("   "+ type.getText());
					}
				}
				
				List<Node> outputList = function.selectNodes("outputs/output");
				for(Node output : outputList) {
					System.out.println("Output:");
					List<Node> typeList = output.selectNodes("type");
					for(Node type : typeList) {
						System.out.println("   "+ type.getText());
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ParserException {
		String csvPath = "/home/vedran/Dropbox/PhD/All Use Cases/Geo_UseCase/GEO_UseCase_2018/UseCase_Paper/modules.csv";
		String xmlPath = "/home/vedran/Dropbox/PhD/All Use Cases/Geo_UseCase/GEO_UseCase_2018/UseCase_Paper/modules.xml";
//		csv2xml(csvPath);
//		xml2print(xmlPath);
		
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		final Formula formula = p.parse("(11) | (12 & 22) | (13 & 23)");

		final Formula nnf = formula.nnf();
		final Formula cnf = formula.cnf();
		
		System.out.println(formula.toString());
		System.out.println(nnf.toString());
		System.out.println(cnf.toString().replace('~', '-').replace(") & (", " 0\n").replace(" | ", " ").replace("(", "").replace(")",  " 0\n"));
		
//		final SATSolver miniSat = MiniSat.miniSat(f);
//		miniSat.add(formula);
//		final Tristate result = miniSat.sat();
		
	}

}