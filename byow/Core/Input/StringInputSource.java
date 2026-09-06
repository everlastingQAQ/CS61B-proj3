package byow.Core.Input;

/**
 * 从指令读取输入的 InputSource 实现。
 *
 * @author everlasting
 */
public class StringInputSource implements InputSource {

    private final String input;
    private int index;

    public StringInputSource(String input) {
        this.index = 0;
        this.input = input;
    }

    @Override
    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return returnChar;
    }

    @Override
    public boolean possibleNextInput() {
        return index < input.length();
    }
}
