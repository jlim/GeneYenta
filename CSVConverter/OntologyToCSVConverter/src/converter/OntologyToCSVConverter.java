package converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.file.FileOntologyService;


public class OntologyToCSVConverter {

	static final String INDENT = "_";

	public static void main(String[] args) throws OntologyServiceException,
			URISyntaxException, IOException {

		File ontologyFile = new File(args[0] + args[1]);
		OntologyService os = new FileOntologyService(ontologyFile.toURI());
		Ontology o = os.getOntologies().get(0);
		OntologyTerm hpoRoot = os.getRootTerms(o).get(0);
		for (int i = 0; i < 3; i++) {
			OntologyTerm newRoot = os.getChildren(hpoRoot).get(i);
			if (i < 2) {
				FileWriter structureWriter = new FileWriter(args[0]
						+ args[1].split("\\.")[0] + "_Structure"
						+ String.valueOf(i + 1) + ".csv");

				writeStructureToCSV(structureWriter, os, o, newRoot, 0, false,
						true);

				structureWriter.flush();
				structureWriter.close();

				FileWriter valueWriter = new FileWriter(args[0]
						+ args[1].split("\\.")[0] + "_value"
						+ String.valueOf(i + 1) + ".csv");
				writeValuesToCSVFromRoot(newRoot, os, 0, valueWriter, true);

				valueWriter.flush();
				valueWriter.close();
			} else {
				int j = 3;
				for (OntologyTerm t : os.getChildren(newRoot)) {
					FileWriter structureWriter = new FileWriter(args[0]
							+ args[1].split("\\.")[0] + "_Structure"
							+ String.valueOf(j) + ".csv");

					writeStructureToCSV(structureWriter, os, o, t, 0, false,
							true);

					structureWriter.flush();
					structureWriter.close();

					FileWriter valueWriter = new FileWriter(args[0]
							+ args[1].split("\\.")[0] + "_value"
							+ String.valueOf(j) + ".csv");
					writeValuesToCSVFromRoot(t, os, 0, valueWriter, true);

					valueWriter.flush();
					valueWriter.close();
					j++;
				}
			}

		}
	}

	private static void writeValuesToCSVFromRoot(OntologyTerm ot,
			OntologyService os, int depth, FileWriter writer, boolean isRoot)
			throws IOException, OntologyServiceException {

		if (isRoot) {
			writer.append('\"' + ot.getLabel() + '\"');
			writer.append(',');
			writer.append('\"' + ot.getAccession() + '\"');
			writer.append(',');
			writer.append('\"' + ot.getURI().toString() + '\"');
			writer.append(',');
			writer.append('\"' + String.valueOf(depth) + '\"');
			writer.append(',');
			writer.append('\"' + String.valueOf(os.getChildren(ot).isEmpty()) + '\"');
		}

		for (OntologyTerm t : os.getChildren(ot)) {
			writer.append('\n');
			writer.append('\"' + t.getLabel() + '\"');
			writer.append(',');
			writer.append('\"' + t.getAccession() + '\"');
			writer.append(',');
			writer.append('\"' + t.getURI().toString() + '\"');
			writer.append(',');
			writer.append('\"' + String.valueOf(depth + 1) + '\"');
			writer.append(',');
			writer.append('\"' + String.valueOf(os.getChildren(t).isEmpty()) + '\"');
			writeValuesToCSVFromRoot(t, os, depth + 1, writer, false);
		}

	}



	public static void writeStructureToCSV(FileWriter writer,
			OntologyService os, Ontology o, OntologyTerm t, int level,
			boolean isFirstChild, boolean isRoot)
			throws OntologyServiceException, IOException {

		int newLevel = level + 1;
		if (!isFirstChild) {
			if (!isRoot)
				writer.append('\n');
			for (int i = 0; i < level; i++) {
				writer.append(',');
			}
			writer.append('\"' + t.getLabel() + '\"');
		}
		if (os.getChildren(t).isEmpty()) {

		} else {
			List<OntologyTerm> children = os.getChildren(t);
			for (int i = 0; i < os.getChildren(t).size(); i++) {
				if (i == 0) {
					writer.append(',');
					writer.append('\"' + children.get(i).getLabel() + '\"');
					writeStructureToCSV(writer, os, o, children.get(i),
							newLevel, true, false);
				} else {

					writeStructureToCSV(writer, os, o, children.get(i),
							newLevel, false, false);
				}

			}
		}

	}

	public static void printAllFromRoot(OntologyTerm ot, OntologyService os,
			int level) {
		int l = level + 1;
		String indent = "";
		for (int i = 0; i < level; i++)
			indent += INDENT;
		try {
			for (OntologyTerm term : os.getChildren(ot)) {
				System.out.print(indent);
				System.out.print(level);
				System.out.print("  ");
				System.out.println(term);

				printAllFromRoot(term, os, l);
			}
		} catch (OntologyServiceException e) {

			e.printStackTrace();
		}
	}


}