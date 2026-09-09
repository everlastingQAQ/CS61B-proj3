package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static byow.game.render.RenderUtils.drawCentered;

public class SeedRenderer {
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final ShapeRenderer shapeRenderer;

    public SeedRenderer(SpriteBatch batch, FontManager fonts, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
        this.shapeRenderer = shapeRenderer;
    }

    public void render(String seed) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // 输入框
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
            width * 0.30f,
            height * 0.43f,
            width * 0.40f,
            height * 0.12f
        );

        shapeRenderer.end();

        // 文本
        batch.begin();

        titleFont.setColor(Color.WHITE);

        drawCentered(
            batch,
            titleFont,
            "Enter a seed",
            height * 0.70f
        );

        bodyFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            bodyFont,
            seed,
            height * 0.50f
        );

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(
            batch,
            bodyFont,
            "Press S to start",
            height * 0.35f
        );

        drawCentered(
            batch,
            bodyFont,
            "Back (B)",
            height * 0.27f
        );

        batch.end();
    }
}
