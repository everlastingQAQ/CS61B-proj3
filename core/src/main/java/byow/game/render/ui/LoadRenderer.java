package byow.game.render.ui;

import byow.game.Engine;
import byow.game.render.FontManager;
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
        titleFont = fonts.title();
        bodyFont = fonts.body();
    }

    public void render(Engine engine) {
        float height = Gdx.graphics.getHeight();
        batch.begin();
        titleFont.setColor(Color.WHITE);
        drawCentered(batch, titleFont, "读取游戏", height * 0.85f);
        bodyFont.setColor(Color.WHITE);
        for (int slot = 1; slot <= 5; slot++) {
            String status = engine.saveSlotExists(slot) ? "已有存档" : "空";
            drawCentered(batch, bodyFont, "存档位 " + slot + " - " + status,
                height * (0.69f - (slot - 1) * 0.08f));
        }
        drawCentered(batch, bodyFont, "按 1-5 读取存档", height * 0.25f);
        bodyFont.setColor(Color.SALMON);
        drawCentered(batch, bodyFont, engine.uiMessage(), height * 0.13f);
        bodyFont.setColor(Color.LIGHT_GRAY);
        drawCentered(batch, bodyFont, "返回 (B)", height * 0.07f);
        batch.end();
    }
}
