package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static byow.game.render.RenderUtils.drawCentered;

public class ConfirmQuitRenderer {

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;

    public ConfirmQuitRenderer(SpriteBatch batch, FontManager fonts) {
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
            "Save before returning to menu?",
            height * 0.70f
        );

        bodyFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            bodyFont,
            "Save and Quit (Y)",
            height * 0.49f
        );

        drawCentered(
            batch,
            bodyFont,
            "Return Without Saving (N)",
            height * 0.38f
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
