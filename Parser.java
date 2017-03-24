import java.io.BufferedReader;
import java.io.IOException;

public class Parser {

    public Sequence parse(BufferedReader bufferedReader) throws IOException {
        LexicalAnalyser lexicalAnalyzer = new LexicalAnalyser();
        Sequence sequence = lexicalAnalyzer.generateTokensSequence(bufferedReader);
        /*SyntacticAnalyser syntacticAnalyzer = new SyntacticAnalyser(sequence);
        if(!syntacticAnalyzer.Start())
            System.out.println("Syntactic analysis error.");*/
        return sequence;
    }
}
