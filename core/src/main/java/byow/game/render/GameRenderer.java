package byow.game.render;

import byow.game.Engine;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameRenderer {

    private final MenuRenderer menuRenderer;
    private final SeedRenderer seedRenderer;
    private final WorldRenderer worldRenderer;
    private final PauseRenderer pauseRenderer;
    private final SaveRenderer saveRenderer;
    private final LoadRenderer loadRenderer;
    private final ConfirmQuitRenderer confirmQuitRenderer;

    public GameRenderer(
        SpriteBatch batch,
        BitmapFont font,
        ShapeRenderer shapeRenderer,
        float tileSize
    ) {
        this.menuRenderer = new MenuRenderer(batch, font, shapeRenderer);
        this.seedRenderer = new SeedRenderer(batch, font, shapeRenderer);
        this.worldRenderer = new WorldRenderer(shapeRenderer, batch, font, tileSize);
        this.pauseRenderer = new PauseRenderer(batch, font);
        this.saveRenderer = new SaveRenderer(batch, font);
        this.loadRenderer = new LoadRenderer(batch, font);
        this.confirmQuitRenderer = new ConfirmQuitRenderer(batch, font);
    }

    public void render(Engine engine) {
        switch (engine.state()) {
            case MENU -> menuRenderer.render();
            case SEED -> seedRenderer.render(engine.seedString());
            case PLAYING -> worldRenderer.render(engine.world(), engine.player());
            case PAUSE -> pauseRenderer.render();
            case SAVE -> saveRenderer.render();
            case LOAD -> loadRenderer.render();
            case CONFIRM_QUIT -> confirmQuitRenderer.render();
        }
    }

}
