import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Parser {

    public Sequence parse(BufferedReader bufferedReader) throws IOException, ParseException {
        LexicalAnalyser lexicalAnalyser = new LexicalAnalyser();
        Sequence sequence = lexicalAnalyser.generateTokensSequence(bufferedReader);
        SyntacticAnalyser syntacticAnalyser = new SyntacticAnalyser(sequence);
        if (!syntacticAnalyser.Start())
            throw new ParseException("Syntactic analysis error.", 0);
        return sequence;
    }
}
