package byow.game.render.world;

import byow.game.GameConfig;
import byow.game.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FogRenderer {
    private static final Color EXPLORED_COLOR =
        new Color(0.08f, 0.08f, 0.12f, 0.55f);

    private static final Color UNEXPLORED_COLOR =
        new Color(0f, 0f, 0f, 1f);

    private final ShapeRenderer shapeRenderer;
    float tileSize;

    public FogRenderer(ShapeRenderer shapeRenderer, float tileSize) {
        this.shapeRenderer = shapeRenderer;
        this.tileSize = tileSize;
    }

    public void render(boolean[][] visible, boolean[][] explored) {
        // 透明混合
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i <  visible.length; i++) {
            for (int j = 0; j < visible[0].length; j++) {
                if (visible[i][j]) {
                    continue;
                }

                if (explored[i][j]) {
                    shapeRenderer.setColor(EXPLORED_COLOR);
                } else {
                    shapeRenderer.setColor(UNEXPLORED_COLOR);
                }

                shapeRenderer.rect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
