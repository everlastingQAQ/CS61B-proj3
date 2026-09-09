package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static byow.game.render.RenderUtils.drawCentered;

public class SaveRenderer {

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;

    public SaveRenderer(SpriteBatch batch, FontManager fonts) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
    }

    public void render() {
        float height = Gdx.graphics.getHeight();

        batch.begin();

        titleFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            titleFont,
            "SAVE GAME",
            height * 0.82f
        );

        bodyFont.setColor(Color.WHITE);
        for (int slot = 1; slot <= 5; slot++) {
            drawCentered(
                batch,
                bodyFont,
                "Slot " + slot + " (" + slot + ")",
                height * (0.67f - (slot - 1) * 0.09f)
            );
        }

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(
            batch,
            bodyFont,
            "Press 1-5 to save",
            height * 0.18f
        );

        drawCentered(
            batch,
            bodyFont,
            "Saving replaces the existing save in that slot.",
            height * 0.11f
        );

        drawCentered(
            batch,
            bodyFont,
            "Back (B)",
            height * 0.07f
        );

        batch.end();
    }

}
