package byow.Core.UI;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class SeedInputRender {

    public static void seedInputRender(String seed) {
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.7, "Enter a seed");

        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 24));
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.5, seed);

        StdDraw.setPenRadius(0.01);
        StdDraw.line(WIDTH * 0.3, HEIGHT * 0.46, WIDTH * 0.7, HEIGHT * 0.46);
        StdDraw.line(WIDTH * 0.3, HEIGHT * 0.55, WIDTH * 0.7, HEIGHT * 0.55);

        StdDraw.line(WIDTH * 0.3, HEIGHT * 0.46, WIDTH * 0.3, HEIGHT * 0.55);
        StdDraw.line(WIDTH * 0.7, HEIGHT * 0.46, WIDTH * 0.7, HEIGHT * 0.55);

        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));

        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.35, "Press S to start");

        StdDraw.show();
    }

}
