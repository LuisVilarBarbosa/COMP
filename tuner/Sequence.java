package tuner;
import java.util.Vector;

public class Sequence {
	private Vector<Token> tokens = new Vector<Token>();
	private int i;

	/**
	 * Creates a Sequence of tokens.
	 * @param tokens Vector with all the tokens
	 */
	public Sequence(Vector<Token> tokens) {
		this.tokens = tokens;
		this.i = 0;
	}

	/**
	 * Checks if the sequence has more elements.
	 * @return true if there are more elements, false if there are not
	 */
	public boolean hasNext(){
		return i < tokens.size()-1;
	}

	/**
	 * Advances to the next token in the sequence.
	 * @return Next token in the sequence
	 */
	public Token nextToken() {
		if(i < tokens.size()-1){
			i++;
			return tokens.elementAt(i);
		}
		return null;
	}

	/**
	 * Goes back to the previous token in the sequence.
	 * @return Previous token in the sequence
	 */
	public void previousToken() {
		if (i > 0)
			i--;
	}

	/**
	 * Adds a token to the sequence.
	 * @param t Token to be added
	 */
	public void addToken(Token t) {
		tokens.add(t);
	}

	/**
	 * @return the index
	 */
	public int getTokenIndex() {
		return i;
	}

	/**
	 * @param i the index to set
	 */
	public void setTokenIndex(int i) {
		this.i = i;
	}

	/**
	 * Returns the token in the current index.
	 * @return Token
	 */
	public Token getCurrentToken() {
		if(i < tokens.size())
			return tokens.elementAt(i);
		return null;
	}

	/**
	 * Returns the number of tokens in sequence.
	 * @return int the size of the sequence
	 */
	public int getNumTokens() {
		return tokens.size();
	}
}
