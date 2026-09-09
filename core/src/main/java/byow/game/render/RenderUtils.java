package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class RenderUtils {

    private static final GlyphLayout LAYOUT = new GlyphLayout();

    private RenderUtils() {
    }

    public static void drawCentered(SpriteBatch batch, BitmapFont font, String text, float y) {
        LAYOUT.setText(font, text);

        float x = (Gdx.graphics.getWidth() - LAYOUT.width) / 2f;

        font.draw(batch, LAYOUT, x, y);
    }
}
