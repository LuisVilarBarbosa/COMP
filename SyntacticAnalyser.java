public class SyntacticAnalyser {
    private Sequence sequence;

    public SyntacticAnalyser(Sequence sequence) {
        this.sequence = sequence;
    }

    public boolean Start() {
        boolean goodSequence = true;

        while(goodSequence) {
            while(!sequence.nextToken().getToken().equalsIgnoreCase("#pragma"));
            goodSequence &= Expr();
        }

        return goodSequence;
    }

    private boolean Expr() {
        int pos = sequence.getTokenIndex();
        Token token = sequence.getCurrentToken();

        if (token.getToken().equalsIgnoreCase("#pragma")) {
            token = sequence.nextToken();
            if (token.getToken().equalsIgnoreCase("tuner"))
                return Spec();
            else
                return error(pos);
        } else
            return error(pos);
    }

    private boolean Spec() {
        int pos = sequence.getTokenIndex();
        Token token = sequence.nextToken();

        if (token.getToken().equalsIgnoreCase("explore")) {
            if (Macro())
                return Reference();
            else
                return error(pos);
        } else if (token.getToken().equalsIgnoreCase("max_abs_error")) {
            token = sequence.nextToken();
            if (token.getType() == token.VAR_TYPE)
                return Value();
            else
                return error(pos);
        } else
            return error(pos);
    }

    private boolean Macro() {
        int pos = sequence.getTokenIndex();
        Token token = sequence.nextToken();

        if (token.getType() == token.VAR_TYPE) {
            token = sequence.nextToken();
            if (token.getToken().equalsIgnoreCase("(")) {
                if (Value()) {
                    token = sequence.nextToken();
                    if (token.getToken().equalsIgnoreCase(",")) {
                        if (Value()) {
                            token = sequence.nextToken();
                            return token.getToken().equalsIgnoreCase(")");
                        } else
                            return error(pos);
                    } else
                        return error(pos);
                } else
                    return error(pos);
            } else
                return error(pos);
        } else
            return error(pos);
    }

    private boolean Reference() {
        int pos = sequence.getTokenIndex();
        Token token = sequence.nextToken();

        if (token.getToken().equalsIgnoreCase("reference(")) {
            token = sequence.nextToken();
            if (token.getType() == token.VAR_TYPE) {
                token = sequence.nextToken();
                if (token.getToken().equalsIgnoreCase("=")) {
                    if (Value()) {
                        token = sequence.nextToken();
                        return token.getToken().equalsIgnoreCase(")");
                    } else
                        return error(pos);
                } else
                    return error(pos);
            } else
                return error(pos);
        } else
            return error(pos);
    }

    private boolean Value() {
        int pos = sequence.getTokenIndex();
        Token token = sequence.nextToken();
        if (token.getType() == token.INT_TYPE | token.getType() == token.FLOAT_TYPE)
            return true;
        else
            return error(pos);
    }

    private boolean error(int originalPos) {
        sequence.setTokenIndex(originalPos);
        return false;
    }
}
