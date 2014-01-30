package mbi;

import java.io.IOException;
import java.util.List;

public class DnaAssembler {
	
	public static String assemble(String pathToFile, int oneKmerLength, int kmersOverlapLength, int graphDegree) throws IOException, MbiException {
		Sequencer sequencer = new Sequencer();	
		sequencer.loadSequenceFromOneLineFile(pathToFile);
		List<String> kmers = sequencer.shotgun(oneKmerLength, kmersOverlapLength);
		sequencer.buildDeBruijnGraph(kmers, graphDegree);
		String resultSequence = sequencer.assemble();
		return resultSequence;
	}
	
}
