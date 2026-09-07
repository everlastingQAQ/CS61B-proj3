package byow.game;

import byow.game.render.TileRenderer;
import byow.game.render.WorldRenderer;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import com.badlogic.gdx.ApplicationAdapter;
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
    private TileRenderer tileRenderer;
    private WorldRenderer worldRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        float tileSize = 20f;

        tileRenderer = new TileRenderer(
            batch,
            font,
            shapeRenderer,
            tileSize
        );

        worldRenderer = new WorldRenderer(
            shapeRenderer,
            batch,
            tileRenderer
        );

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

//        worldRenderer.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }

}
