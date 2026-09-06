package byow.Core.Input;

/**
 * Input 类负责创建不同类型的输入源。
 *
 * @author everlasting
 */
public class Input {

    public static InputSource keyboardInput() {
        return new KeyboardInputSource();
    }

    public static InputSource stringInput(String input) {
        return new StringInputSource(input);
    }
}
