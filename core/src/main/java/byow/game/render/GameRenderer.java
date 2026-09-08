package byow.game.render;

import byow.game.Engine;
import byow.game.worldGenerator.WorldGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameRenderer {

    private final MenuRenderer menuRenderer;
    private final SeedRenderer seedRenderer;
    private final WorldRenderer worldRenderer;

    public GameRenderer(SpriteBatch batch, BitmapFont font, ShapeRenderer shapeRenderer, float tileSize) {
        this.menuRenderer = new MenuRenderer(batch, font, shapeRenderer);
        this.seedRenderer = new SeedRenderer(batch, font, shapeRenderer);
        this.worldRenderer = new WorldRenderer(shapeRenderer, batch, font, tileSize);
    }

    public void render(Engine engine) {
        switch (engine.state()) {
            case MENU -> menuRenderer.render();
            case SEED -> seedRenderer.render(engine.seedString());
            case PLAYING -> worldRenderer.render(engine.world());
        }
    }
}
