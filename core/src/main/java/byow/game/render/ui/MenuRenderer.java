package byow.game.render.ui;

import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static byow.game.render.RenderUtils.drawCentered;

public class MenuRenderer {

    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final ShapeRenderer shapeRenderer;

    public MenuRenderer(SpriteBatch batch, FontManager fonts, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.titleFont = fonts.title();
        this.bodyFont = fonts.body();
        this.shapeRenderer = shapeRenderer;
    }

    public void render() {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, width, height);

        shapeRenderer.end();

        batch.begin();

        // 标题
        titleFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            titleFont,
            "地牢探险",
            height * 0.75f
        );

        // 菜单
        bodyFont.setColor(Color.WHITE);
        drawCentered(
            batch,
            bodyFont,
            "新游戏 (N)",
            height * 0.50f
        );

        drawCentered(
            batch,
            bodyFont,
            "读取游戏 (L)",
            height * 0.42f
        );

        drawCentered(
            batch,
            bodyFont,
            "退出游戏 (Q)",
            height * 0.34f
        );

        batch.end();
    }
}
