package byow.game.render;

import byow.game.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera uiCamera;

    public GameRenderer(
        SpriteBatch batch,
        FontManager fonts,
        ShapeRenderer shapeRenderer,
        float tileSize
    ) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;

        this.uiCamera = new OrthographicCamera();

        updateUiCamera();

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
            case PLAYING -> worldRenderer.render(engine.world(), engine.player());
            case MENU -> {
                useUiCamera();
                menuRenderer.render();
            }
            case SEED -> {
                useUiCamera();
                seedRenderer.render(engine.seedString());
            }
            case PAUSE -> {
                useUiCamera();
                pauseRenderer.render();
            }
            case SAVE -> {
                useUiCamera();
                saveRenderer.render();
            }
            case LOAD -> {
                useUiCamera();
                loadRenderer.render();
            }
            case CONFIRM_QUIT -> {
                useUiCamera();
                confirmQuitRenderer.render();
            }
        }
    }

    private void updateUiCamera() {
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
    }

    private void useUiCamera() {
        updateUiCamera();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
    }
}
