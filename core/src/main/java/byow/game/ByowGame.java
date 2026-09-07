package byow.game;

import byow.game.input.GameInputProcessor;
import byow.game.render.GameRenderer;
import byow.game.render.MenuRenderer;
import byow.game.render.TileRenderer;
import byow.game.render.WorldRenderer;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import byow.game.worldGenerator.WorldGenerator;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ByowGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private GameRenderer gameRenderer;
    private Engine engine;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        engine = new Engine();
        gameRenderer = new GameRenderer(batch, font, shapeRenderer, GameConfig.getTileSize());

        Gdx.input.setInputProcessor(
            new GameInputProcessor(engine)
        );
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        gameRenderer.render(engine);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

}
