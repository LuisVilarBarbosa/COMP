import java.util.Vector;

public class Sequence {
    private Vector<Token> tokens = new Vector<Token>();
    private int i;

    public Sequence(Vector<Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
    }

    public Token nextToken() {
        if(i < tokens.size())
            return tokens.elementAt(i++);
        else return null;
    }

    public void previousToken() {
        if (i > 0)
            i--;
    }

    public void addToken(Token t) {
        tokens.add(t);
    }

    public int getTokenIndex() { return i; }

    public void setTokenIndex(int i) {
        this.i = i;
    }

    public Token getCurrentToken() {
        if(i < tokens.size())
            return tokens.elementAt(i);
        else return null;
    }

    public int getNumTokens() {
        return tokens.size();
    }
}
