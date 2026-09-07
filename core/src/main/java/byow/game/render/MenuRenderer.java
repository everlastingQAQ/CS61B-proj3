package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuRenderer {

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    public MenuRenderer(SpriteBatch batch, BitmapFont font, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.font = font;
        this.shapeRenderer = shapeRenderer;
    }

    public void render() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, width, height);

        shapeRenderer.end();

        batch.begin();

        font.setColor(Color.WHITE);

        font.getData().setScale(2.5f);
        font.draw(batch,
            "CS61B: THE GAME",
            width * 0.37f,
            height * 0.75f);

        font.getData().setScale(1.5f);

        font.draw(batch,
            "New Game (N)",
            width * 0.43f,
            height * 0.50f);

        font.draw(batch,
            "Load Game (L)",
            width * 0.43f,
            height * 0.42f);

        font.draw(batch,
            "Quit (Q)",
            width * 0.46f,
            height * 0.34f);

        batch.end();
    }
}
