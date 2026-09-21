package byow.game.render.ui;

import byow.game.render.FontManager;
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
            "保存游戏",
            height * 0.82f
        );

        bodyFont.setColor(Color.WHITE);
        for (int slot = 1; slot <= 5; slot++) {
            drawCentered(
                batch,
                bodyFont,
                "存档位 " + slot + " (" + slot + ")",
                height * (0.67f - (slot - 1) * 0.09f)
            );
        }

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(
            batch,
            bodyFont,
            "按 1-5 选择存档位",
            height * 0.18f
        );

        drawCentered(
            batch,
            bodyFont,
            "保存会覆盖该位置已有的存档。",
            height * 0.11f
        );

        drawCentered(
            batch,
            bodyFont,
            "返回 (B)",
            height * 0.07f
        );

        batch.end();
    }

}
