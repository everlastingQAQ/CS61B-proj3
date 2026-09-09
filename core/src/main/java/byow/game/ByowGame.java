package byow.game;

import byow.game.input.GameInputProcessor;
import byow.game.render.FontManager;
import byow.game.render.GameRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ByowGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private FontManager fonts;
    private ShapeRenderer shapeRenderer;

    private GameRenderer gameRenderer;
    private Engine engine;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new FontManager();
        shapeRenderer = new ShapeRenderer();
        engine = new Engine();
        gameRenderer = new GameRenderer(batch, fonts, shapeRenderer, GameConfig.getTileSize());

        Gdx.input.setInputProcessor(new GameInputProcessor(engine));
    }

    @Override
    public void render() {

        if (engine.shouldQuit()) {
            Gdx.app.exit();
            return;
        }

        ScreenUtils.clear(0, 0, 0, 1);
        gameRenderer.render(engine);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
        shapeRenderer.dispose();
    }

}
