package byow.Core.Input;

/**
 * KeyboardInputSource 和 StringInputSource 的统一接口
 *
 * @author everlasting
 * */
public interface InputSource {
    char getNextKey();
    boolean possibleNextInput();
}
