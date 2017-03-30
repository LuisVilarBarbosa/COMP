package tuner;
public class Token {
    public static final int UNDEFINED_TYPE = 0;
    public static final int VAR_TYPE = 1;
    public static final int INT_TYPE = 2;
    public static final int FLOAT_TYPE = 3;
    private String token;
    private int type;

    public Token(String token, int type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public int getType() {
        return type;
    }
}
