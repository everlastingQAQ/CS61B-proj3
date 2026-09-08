package byow.game.random;

import java.util.Collections;
import java.util.List;

public class GameRandom {

    private long state;

    public GameRandom(long seed) {
        this.state = seed;
    }

    /**
     * 返回 [0, bound) 的随机整数
     */
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        state = state * 6364136223846793005L + 1442695040888963407L;
        long value = state >>> 32;
        return (int) (value % bound);
    }

    public long state() {
        return state;
    }

    /**
     * 返回 [origin, bound) 的随机整数
     *
     * 模拟 Random.nextInt(origin,bound)
     */
    public int nextInt(int origin, int bound) {

        if (origin >= bound) {
            throw new IllegalArgumentException();
        }

        return origin + nextInt(bound - origin);
    }

    public <T> void shuffle(List<T> list) {
        for (int i = list.size() - 1; i > 0; i--) {

            int j = nextInt(i + 1);

            Collections.swap(list, i, j);
        }
    }
}
