package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontManager {

    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final BitmapFont tileFont;

    public FontManager() {
        titleFont = createFont(
            "font/JetBrainsMono-Medium.ttf",
            40
        );

        bodyFont = createFont(
            "font/JetBrainsMonoNL-Regular.ttf",
            24
        );

        tileFont = createTileFont(
            "font/NotoSansSymbols2-Regular.ttf",
            16
        );
    }

    private BitmapFont createFont(String path, int size) {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal(path)
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }

    private BitmapFont createTileFont(String path, int size) {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal(path)
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;

        parameter.characters =
            FreeTypeFontGenerator.DEFAULT_CHARS
                + "·≈❀█▢▒▲♠●";

        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        BitmapFont font = generator.generateFont(parameter);

        generator.dispose();

        return font;
    }

    public BitmapFont title() {
        return titleFont;
    }

    public BitmapFont body() {
        return bodyFont;
    }

    public BitmapFont tile() {
        return tileFont;
    }

    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
        tileFont.dispose();
    }
}
