package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ConfirmQuitRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public ConfirmQuitRenderer(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
    }

    public void render() {
        float height = Gdx.graphics.getHeight();

        batch.begin();

        font.setColor(Color.WHITE);
        font.getData().setScale(2.2f);
        drawCentered("Save before quitting?", height * 0.70f);

        font.getData().setScale(1.5f);
        drawCentered("Save and Quit (Y)", height * 0.49f);
        drawCentered("Quit Without Saving (N)", height * 0.38f);

        batch.end();
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, layout, x, y);
    }
}
