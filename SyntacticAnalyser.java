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

public class SyntacticAnalyser {
    private Sequence sequence;

    public SyntacticAnalyser(Sequence sequence) {
        this.sequence = sequence;
    }

    public void Start() throws ParseException {
        int numTokens = sequence.getNumTokens();
        for (int i = 0; sequence.getTokenIndex() < numTokens; i++) {
            while (sequence.getTokenIndex() < numTokens && !sequence.nextToken().getToken().equalsIgnoreCase("#pragma"))
                ;
            sequence.previousToken();

            try {
                Expr();
            } catch (NullPointerException e) {
                if (i == 0)  // in the first iteration
                    System.out.println("Syntactic analysis warning - no '#pragma' found.");
            }
        }
    }

    private void Expr() throws ParseException {
        Token token = sequence.nextToken();

        if (token.getToken().equalsIgnoreCase("#pragma")) {
            token = sequence.nextToken();
            if (token.getToken().equalsIgnoreCase("tuner"))
                Spec();
        }
        error();
    }

    private void Spec() throws ParseException {
        Token token = sequence.nextToken();

        if (token.getToken().equalsIgnoreCase("explore")) {
            Macro();
            Reference();
        } else if (token.getToken().equalsIgnoreCase("max_abs_error")) {
            token = sequence.nextToken();
            if (token.getType() == token.VAR_TYPE)
                Value();
        }
        error();
    }

    private void Macro() throws ParseException {
        Token token = sequence.nextToken();

        if (token.getType() == token.VAR_TYPE) {
            token = sequence.nextToken();
            if (token.getToken().equalsIgnoreCase("(")) {
                Value();
                token = sequence.nextToken();
                if (token.getToken().equalsIgnoreCase(",")) {
                    Value();
                    token = sequence.nextToken();
                    if (token.getToken().equalsIgnoreCase(")"))
                        return;
                }
            }
        }
        error();
    }

    private void Reference() throws ParseException {
        Token token = sequence.nextToken();

        if (token.getToken().equalsIgnoreCase("reference")) {
            token = sequence.nextToken();
            if (token.getToken().equalsIgnoreCase("(")) {
                token = sequence.nextToken();
                if (token.getType() == token.VAR_TYPE) {
                    token = sequence.nextToken();
                    if (token.getToken().equalsIgnoreCase("=")) {
                        Value();
                        token = sequence.nextToken();
                        if (token.getToken().equalsIgnoreCase(")"))
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
