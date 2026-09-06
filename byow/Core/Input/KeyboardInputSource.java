package byow.Core.Input;

import edu.princeton.cs.introcs.StdDraw;

/**
 * 从键盘读取输入的 InputSource 实现。
 *
 * @author everlasting
 */
public class KeyboardInputSource implements InputSource {

    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return StdDraw.nextKeyTyped();
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
