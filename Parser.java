import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser {

    public ArrayList<String> parse(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> lines = getLines(bufferedReader);
        return getTokens(lines);
    }

    private ArrayList<String> getLines(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String str;
        while ((str = bufferedReader.readLine()) != null)
            lines.add(str);
        return lines;
    }

	private ArrayList<String> getTokens(ArrayList<String> lines) {
		ArrayList<String> words = new ArrayList<>();
		ArrayList<String> tokens = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			StringTokenizer st = new StringTokenizer(lines.get(i), " 	", false);
			while(st.hasMoreTokens())
				tokens.add(st.nextToken());
		}
		return tokens;
	}

}
