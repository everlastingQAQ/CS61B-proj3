package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static byow.game.render.RenderUtils.drawCentered;

public class PauseRenderer {

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;

    public PauseRenderer(SpriteBatch batch, FontManager fonts) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
    }

    public void render(String message) {
        float height = Gdx.graphics.getHeight();

        batch.begin();

        titleFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            titleFont,
            "游戏暂停",
            height * 0.75f
        );

        bodyFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            bodyFont,
            "继续游戏 (P)",
            height * 0.52f
        );

        drawCentered(
            batch,
            bodyFont,
            "保存游戏 (S)",
            height * 0.43f
        );

        drawCentered(
            batch,
            bodyFont,
            "返回主菜单 (Q)",
            height * 0.34f
        );

        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(batch, bodyFont, message, height * 0.20f);

        batch.end();
    }

}
