package byow.game.render.world;

import byow.game.GameConfig;
import byow.game.player.Player;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FogRenderer {
    private final ShapeRenderer shapeRenderer;

    float tileSize;

    public FogRenderer(ShapeRenderer shapeRenderer, float tileSize) {
        this.shapeRenderer = shapeRenderer;
        this.tileSize = tileSize;
    }

    public void render(boolean[][] visible) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

        for (int i = 0; i < GameConfig.WORLD_WIDTH; i++) {
            for (int j = 0; j < GameConfig.WORLD_HEIGHT; j++) {
                if (!visible[i][j]) {
                    shapeRenderer.rect(i * tileSize, j * tileSize, tileSize, tileSize);
                }
            }
        }

        shapeRenderer.end();
    }
}
