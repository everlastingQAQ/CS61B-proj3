package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static byow.game.render.RenderUtils.drawCentered;

/** 绘制胜利和失败画面。 */
public final class EndRenderer {
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final ShapeRenderer shapeRenderer;

    public EndRenderer(SpriteBatch batch, FontManager fonts, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
        this.shapeRenderer = shapeRenderer;
    }

    public void render(boolean won) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, width, height);
        shapeRenderer.end();

        batch.begin();
        titleFont.setColor(won ? Color.GOLD : Color.SCARLET);
        drawCentered(batch, titleFont, won ? "成功逃脱" : "游戏结束", height * 0.68f);

        bodyFont.setColor(Color.WHITE);
        drawCentered(batch, bodyFont, "新游戏 (N)", height * 0.45f);
        drawCentered(batch, bodyFont, "返回主菜单 (M / Q)", height * 0.34f);
        batch.end();
    }
}
