package byow.Core.Input;

public class Input {

    public static InputSource keyboardInput() {
        return new KeyboardInputSource();
    }

    public static InputSource stringInput(String input) {
        return new StringInputSource(input);
    }
}
