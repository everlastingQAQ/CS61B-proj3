package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PauseRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public PauseRenderer(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
    }

    public void render() {
        float height = Gdx.graphics.getHeight();

        batch.begin();

        font.setColor(Color.WHITE);
        font.getData().setScale(2.5f);
        drawCentered("PAUSED", height * 0.75f);

        font.getData().setScale(1.5f);
        drawCentered("Resume (P)", height * 0.52f);
        drawCentered("Save Game (S)", height * 0.43f);
        drawCentered("Quit (Q)", height * 0.34f);

        batch.end();
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, layout, x, y);
    }
}
