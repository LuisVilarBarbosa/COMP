package tuner;

/*
Context-free grammar:
    Start -> Expr
    Expr -> "#pragma" "tuner" Spec
    Spec -> "explore" Macro Reference
    Spec -> "max_abs_error" VAR Value
    Macro -> VAR "(" Value "," Value ")"
    Reference -> "reference(" VAR "=" Value ")"
    Value -> INT | FLOAT
 */

import java.text.ParseException;

/* To delete */
public class SyntacticAnalyser {
	private Sequence sequence;

	public SyntacticAnalyser(Sequence sequence) {
		this.sequence = sequence;
	}

	public void Start() throws ParseException {
		while(sequence.hasNext()) {
			sequence.nextToken();

			// skip C code
			if("#pragma".equalsIgnoreCase(sequence.getCurrentToken().getToken())){
				try {
					Expr();
				} catch (NullPointerException e) {
					System.out.println("Syntactic analysis warning - no '#pragma' found.");
				}
			}
		}
	}

	private void Expr() throws ParseException {
		Token token = sequence.getCurrentToken();

		System.out.println("Pragma? " + token.getToken());
		if ("#pragma".equalsIgnoreCase(token.getToken())) {
			token = sequence.nextToken();

			System.out.println("Tuner? " + token.getToken());
			if ("tuner".equalsIgnoreCase(token.getToken())) {
				Spec();
				return;
			}
		}

		error();
	}

	private void Spec() throws ParseException {
		Token token = sequence.nextToken();

		System.out.println("Explore or Max_Abs_Error? " + token.getToken());
		if ("explore".equalsIgnoreCase(token.getToken())) {
			Macro();
			Reference();
			return;
		} 
		else if ("max_abs_error".equalsIgnoreCase(token.getToken())) {
			token = sequence.nextToken();
			if (token.getType() == token.VAR_TYPE) {
				Value();
				return;
			}
		}

		error();
	}

	private void Macro() throws ParseException {
		Token token = sequence.nextToken();

		if (token.getType() == token.VAR_TYPE) {
			token = sequence.nextToken();
			if ("(".equalsIgnoreCase(token.getToken())) {
				Value();
				token = sequence.nextToken();
				if (",".equalsIgnoreCase(token.getToken())) {
					Value();
					token = sequence.nextToken();
					if (")".equalsIgnoreCase(token.getToken()))
						return;
				}
			}
		}
		error();
	}

	private void Reference() throws ParseException {
		Token token = sequence.nextToken();

		if ("reference".equalsIgnoreCase(token.getToken())) {
			token = sequence.nextToken();
			if ("(".equalsIgnoreCase(token.getToken())) {
				token = sequence.nextToken();
				if (token.getType() == token.VAR_TYPE) {
					token = sequence.nextToken();
					if ("=".equalsIgnoreCase(token.getToken())) {
						Value();
						token = sequence.nextToken();
						if (")".equalsIgnoreCase(token.getToken()))
							return;
					}
				}
			}
		}
		error();
	}

	private void Value() throws ParseException {
		Token token = sequence.nextToken();

		if (token.getType() == token.INT_TYPE | token.getType() == token.FLOAT_TYPE)
			return;

		error();
	}

	private void error() throws ParseException {
		throw new ParseException(sequence.getCurrentToken().getToken(), sequence.getTokenIndex()); // assuming LL(1) parser
	}
}
