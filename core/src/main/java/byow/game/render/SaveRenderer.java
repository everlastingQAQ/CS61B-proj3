package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SaveRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public SaveRenderer(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
    }

    public void render() {
        float height = Gdx.graphics.getHeight();

        batch.begin();

        font.setColor(Color.WHITE);
        font.getData().setScale(2.5f);
        drawCentered("SAVE GAME", height * 0.82f);

        font.getData().setScale(1.5f);

        for (int slot = 1; slot <= 5; slot++) {
            drawCentered(
                "Slot " + slot + " (" + slot + ")",
                height * (0.67f - (slot - 1) * 0.09f)
            );
        }

        font.setColor(Color.LIGHT_GRAY);
        font.getData().setScale(1.2f);
        drawCentered("Press 1-5 to save", height * 0.18f);
        drawCentered(
            "Saving replaces the existing save in that slot.",
            height * 0.11f
        );

        batch.end();
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.draw(batch, layout, x, y);
    }
}
