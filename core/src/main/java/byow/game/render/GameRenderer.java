package byow.game.render;

import byow.game.Engine;
import byow.game.render.ui.*;
import byow.game.render.world.WorldRenderer;
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

        // 初始化 UI camera 的 viewport。
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

    /**
     * 更新 UI 摄像机。
     */
    private void updateUiCamera() {
        // 将 UI camera 设置成 2D 坐标系形式。
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // 更新 uiCamera
        uiCamera.update();
    }

    /**
     * 将渲染坐标系切换为 UI 坐标系。
     */
    private void useUiCamera() {
        // 先根据当前窗口尺寸更新 UI camera。
        updateUiCamera();

        // 绘制的文字使用 UI 坐标系，
        batch.setProjectionMatrix(uiCamera.combined);

        // 背景也使用 UI 坐标系
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
    }
}
