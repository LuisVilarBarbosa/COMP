package tuner;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Parser {

	/**
	 * Creates the parser.
	 * @param bufferedReader Data from the C file
	 * @return Sequence with all the tokens
	 * @throws IOException
	 * @throws ParseException
	 */
	public Sequence parse(BufferedReader bufferedReader) throws IOException, ParseException {
		LexicalAnalyser lexicalAnalyser = new LexicalAnalyser();
		Sequence sequence = lexicalAnalyser.generateTokensSequence(bufferedReader);
		SyntacticAnalyser syntacticAnalyser = new SyntacticAnalyser(sequence);

		syntacticAnalyser.Start();

		return sequence;
	}
}
