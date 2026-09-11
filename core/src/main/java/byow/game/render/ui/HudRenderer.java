package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static byow.game.GameConfig.HUD_HEIGHT;

public class HudRenderer {
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();

    public HudRenderer(SpriteBatch batch, FontManager fonts, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
        this.shapeRenderer = shapeRenderer;
    }

    public void render(int collectedCount, int targetCount) {

        // 直接获取当前真实窗口大小
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // HUD background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(
            new Color(
                18 / 255f,
                24 / 255f,
                32 / 255f,
                1f
            )
        );

        shapeRenderer.rect(
            0,
            height - HUD_HEIGHT,
            width,
            HUD_HEIGHT
        );

        shapeRenderer.end();

        // HUD text
        batch.begin();

        bodyFont.setColor(Color.WHITE);

        // 计算文字占据空间
        String text = "ITEMS  " + collectedCount + " / " + targetCount;

        layout.setText(bodyFont, text);

        // 居中计算Y: 容器起点 + (容器大小 + 内容大小) / 2
        float textY = height - HUD_HEIGHT + (HUD_HEIGHT + layout.height) / 2;

        bodyFont.draw(
            batch,
            text,
            24,
            textY
        );

        batch.end();
    }
}
