package mbi;

import java.util.List;
//import javax.swing.JApplet;
//import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {

		/*if (args.length != 2) {
			System.exit(1);
		}*/

		//String path = null;
		//int randLength = -1;
		String sequence;
		sequence="ACCGGGT";
		int k = 3;
		
		// -f <PATH> -k <K> z pliku
		// -r <LENGTH> -k <K> losowy

		/*for (int i = 0; i < args.length - 1; ++i) {
			if (args[i].equals("-f")) {
				path = args[i + 1];
			} else if (args[i].equals("-r")) {
				randLength = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-k")) {
				k = Integer.parseInt(args[i + 1]);
			}
		}*/

		/*if (randLength > 0) {
			sequence = Sequencer.generateSequence(randLength);
		} else {
			sequence = Sequencer.readSequenceFromFile(path);
		}*/

		List<String> kmers = Sequencer.shotgun(sequence, k);

		/*JFrame mainFrame = new JFrame("DNA assembler");
		rysowanie();
		mainFrame.add(grApphlet, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);*/
		
		System.out.println("K-MERS: " + kmers.toString());
		System.out.println("input:  " + sequence);
		try {
			String result = Sequencer.assemble(kmers);
			System.out.println("result: " + result);
			System.out.println("INPUT "
					+ (sequence.equals(result) ? "equals" : "differs from")
					+ " RESULT");
		} catch (MbiException e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
}
