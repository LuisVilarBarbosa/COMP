package tuner;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyser {

    public Sequence generateTokensSequence(BufferedReader bufferedReader) throws IOException {
        Vector<String> lines = getLines(bufferedReader);
        Vector<Token> tokens = getTokens(lines);
        Sequence sequence = new Sequence(tokens);
        return sequence;
    }

    private Vector<String> getLines(BufferedReader bufferedReader) throws IOException {
        Vector<String> lines = new Vector<>();
        String str;
        while ((str = bufferedReader.readLine()) != null)
            lines.add(str.concat("\n"));
        return lines;
    }

    private Vector<Token> getTokens(Vector<String> lines) {
        Pattern VAR = Pattern.compile("(_|[a-z]|[A-Z])(_|[a-z]|[A-Z]|[0-9])*");
        Pattern INT = Pattern.compile("([0-9]+)");
        Pattern FLOAT = Pattern.compile("([0-9]+.[0-9]*)");


        Vector<Token> tokens = new Vector<>();
        for (int i = 0; i < lines.size(); i++) {
            StringTokenizer st1 = new StringTokenizer(lines.get(i), " \t", false);
            while (st1.hasMoreTokens()) {
                String t1 = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer(t1, "(,)=\n", true);

                while (st2.hasMoreTokens()) {
                    String t2 = st2.nextToken();
                    Token token;

                    Matcher m1 = VAR.matcher(t2);
                    Matcher m2 = INT.matcher(t2);
                    Matcher m3 = FLOAT.matcher(t2);
                    if (m1.matches()) token = new Token(t2, Token.VAR_TYPE);
                    else if (m2.matches()) token = new Token(t2, Token.INT_TYPE);
                    else if (m3.matches()) token = new Token(t2, Token.FLOAT_TYPE);
                    else token = new Token(t2, Token.UNDEFINED_TYPE);

                    tokens.add(token);
                }
            }
        }
        return tokens;
    }
}