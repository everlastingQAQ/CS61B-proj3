package byow.game.input;

import byow.game.Engine;
import com.badlogic.gdx.InputAdapter;
public class GameInputProcessor extends InputAdapter {

    private Engine engine;

    public GameInputProcessor(Engine engine) {
        this.engine = engine;
    }

    @Override
    public boolean keyTyped(char c) {
        engine.handleKey(c);
        return true;
    }
}
