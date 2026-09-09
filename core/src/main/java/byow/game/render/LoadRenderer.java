package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static byow.game.render.RenderUtils.drawCentered;

public class LoadRenderer {

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;

    public LoadRenderer(SpriteBatch batch, FontManager fonts) {
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
            "LOAD GAME",
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
            "Press 1-5 to load",
            height * 0.20f
        );

        drawCentered(
            batch,
            bodyFont,
            "Empty slots cannot be loaded.",
            height * 0.14f
        );

        drawCentered(
            batch,
            bodyFont,
            "Back (B)",
            height * 0.08f
        );

        batch.end();
    }
}
