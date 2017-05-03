package tuner;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

public class Parser {

	/**
	 * Creates the parser.
	 * @param bufferedReader Data from the C file
	 * @return Sequence with all lines
	 * @throws IOException
	 */
	public Vector<String> parse(BufferedReader bufferedReader) throws IOException {
		return getLines(bufferedReader);
	}

	private Vector<String> getLines(BufferedReader bufferedReader) throws IOException {
		Vector<String> lines = new Vector<>();
		String str;
		while ((str = bufferedReader.readLine()) != null)
			lines.add(str);
		return lines;
	}
}
