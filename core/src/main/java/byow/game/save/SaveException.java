package byow.game.save;

/** 可直接显示给玩家的存读档错误。 */
public final class SaveException extends RuntimeException {
    public SaveException(String message) {
        super(message);
    }

    public SaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
