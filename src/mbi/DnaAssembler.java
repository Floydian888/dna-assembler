package mbi;

import java.io.IOException;
import java.util.List;

public class DnaAssembler {
	
	public static String assemble(String pathToFile, int oneKmerLength, int kmersOverlapLength, int graphDegree) throws IOException, MbiException {
		Assembler assembler = new Assembler();
		KmersGenerator kmersGenerator = new KmersGenerator();
		kmersGenerator.loadSequenceFromFile(pathToFile);
		List<String> kmers = kmersGenerator.getKmers(oneKmerLength, kmersOverlapLength);
		assembler.loadKmers(kmers);
		assembler.buildDeBruijnGraph(graphDegree);
		assembler.assemble();
		String resultSequence = assembler.getAssembledSequence();
		return resultSequence;
	}
	
}
