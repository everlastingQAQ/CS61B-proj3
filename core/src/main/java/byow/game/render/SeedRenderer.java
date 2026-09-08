package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SeedRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public SeedRenderer(SpriteBatch batch, BitmapFont font, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.font = font;
        this.shapeRenderer = shapeRenderer;
    }

    public void render(String seed) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // 输入框
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.rect(
            width * 0.30f,
            height * 0.43f,
            width * 0.40f,
            height * 0.12f
        );

        shapeRenderer.end();

        // 文本
        batch.begin();

        font.getData().setScale(2.2f);
        font.draw(batch,
            "Enter a seed",
            width * 0.41f,
            height * 0.70f);

        font.getData().setScale(1.8f);
        font.draw(batch,
            seed,
            width * 0.32f,
            height * 0.50f);

        font.getData().setScale(1.4f);
        font.draw(batch,
            "Press S to start",
            width * 0.42f,
            height * 0.35f);

        batch.end();
    }
}
