package byow.game.render;

import byow.game.Engine;
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
        FontManager fonts,
        ShapeRenderer shapeRenderer,
        float tileSize
    ) {
        this.menuRenderer = new MenuRenderer(batch, fonts, shapeRenderer);
        this.seedRenderer = new SeedRenderer(batch, fonts, shapeRenderer);
        this.worldRenderer = new WorldRenderer(shapeRenderer, batch, fonts, tileSize);
        this.pauseRenderer = new PauseRenderer(batch, fonts);
        this.saveRenderer = new SaveRenderer(batch, fonts);
        this.loadRenderer = new LoadRenderer(batch, fonts);
        this.confirmQuitRenderer = new ConfirmQuitRenderer(batch, fonts);
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
