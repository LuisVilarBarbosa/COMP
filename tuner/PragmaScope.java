package tuner;

public class PragmaScope {
    private int startIndex;
    private int endIndex;

    public PragmaScope(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
