package byow.game.render.ui;

import byow.game.item.Item;
import byow.game.item.ItemRules;
import byow.game.monster.MonsterType;
import byow.game.render.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

import static byow.game.GameConfig.HUD_HEIGHT;

/** 绘制顶部玩家状态和左下角的单行情境日志。 */
public class HudRenderer {
    private static final float HORIZONTAL_PADDING = 24f;
    private static final float MESSAGE_X = 24f;
    private static final float MESSAGE_Y = 32f;

    private static final Color HUD_BACKGROUND =
        new Color(18 / 255f, 24 / 255f, 32 / 255f, 1f);
    private static final Color STATUS_COLOR =
        new Color(120 / 255f, 220 / 255f, 255 / 255f, 1f);
    private static final Color MONSTER_ACCENT =
        new Color(255 / 255f, 130 / 255f, 100 / 255f, 1f);
    private static final Color ITEM_ACCENT =
        new Color(100 / 255f, 220 / 255f, 190 / 255f, 1f);

    private final SpriteBatch batch;
    private final BitmapFont bodyFont;
    private final BitmapFont messageFont;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();

    public HudRenderer(SpriteBatch batch, FontManager fonts, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.bodyFont = fonts.body();
        this.messageFont = fonts.message();
        this.shapeRenderer = shapeRenderer;
    }

    /**
     * 绘制完整 HUD。
     *
     * 顶部从左到右显示生命值、当前 Buff 状态和遗物进度。左下角以
     * 单行日志显示首次遇见的怪物；没有怪物提示时显示附近遗物。
     */
    public void render(
        int currentHp,
        int maxHp,
        int collectedCount,
        int targetCount,
        int shieldCharges,
        int frozenMonsterTurns,
        int visionBonus,
        boolean radarActive,
        MonsterType monsterNoticeType,
        Item nearbyItem
    ) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        InfoContent info = createInfo(monsterNoticeType, nearbyItem);

        drawBackground(width, height);
        drawText(
            width,
            height,
            currentHp,
            maxHp,
            collectedCount,
            targetCount,
            shieldCharges,
            frozenMonsterTurns,
            visionBonus,
            radarActive,
            info
        );
    }

    private void drawBackground(float width, float height) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(HUD_BACKGROUND);
        shapeRenderer.rect(0, height - HUD_HEIGHT, width, HUD_HEIGHT);
        shapeRenderer.end();
    }

    private void drawText(
        float width,
        float height,
        int currentHp,
        int maxHp,
        int collectedCount,
        int targetCount,
        int shieldCharges,
        int frozenMonsterTurns,
        int visionBonus,
        boolean radarActive,
        InfoContent info
    ) {
        String hpText = "HP  " + currentHp + " / " + maxHp;
        String itemText = "ITEMS  " + collectedCount + " / " + targetCount;
        String statusText = createStatusText(
            shieldCharges,
            frozenMonsterTurns,
            visionBonus,
            radarActive
        );

        batch.begin();
        bodyFont.setColor(Color.WHITE);
        layout.setText(bodyFont, hpText);
        float textY = height - HUD_HEIGHT + (HUD_HEIGHT + layout.height) / 2;
        bodyFont.draw(batch, hpText, HORIZONTAL_PADDING, textY);

        bodyFont.setColor(STATUS_COLOR);
        layout.setText(bodyFont, statusText);
        bodyFont.draw(batch, statusText, (width - layout.width) / 2f, textY);

        bodyFont.setColor(collectedCount > targetCount ? Color.RED : Color.WHITE);
        layout.setText(bodyFont, itemText);
        bodyFont.draw(batch, itemText, width - HORIZONTAL_PADDING - layout.width, textY);

        if (info != null) {
            drawInfoText(info);
        }
        batch.end();
    }

    /** 像素地牢式左下角单行日志，用阴影保证文字在地图上可读。 */
    private void drawInfoText(InfoContent info) {
        messageFont.setColor(Color.BLACK);
        messageFont.draw(batch, info.text(), MESSAGE_X + 2f, MESSAGE_Y - 2f);
        messageFont.setColor(info.accent());
        messageFont.draw(batch, info.text(), MESSAGE_X, MESSAGE_Y);
    }

    private static String createStatusText(
        int shieldCharges,
        int frozenMonsterTurns,
        int visionBonus,
        boolean radarActive
    ) {
        List<String> states = new ArrayList<>();
        if (shieldCharges > 0) {
            states.add("SHIELD x" + shieldCharges);
        }
        if (frozenMonsterTurns > 0) {
            states.add("STUN " + frozenMonsterTurns);
        }
        if (visionBonus > 0) {
            states.add("VISION +" + visionBonus);
        }
        if (radarActive) {
            states.add("RADAR");
        }
        return states.isEmpty() ? "STATUS  NONE" : String.join("   ", states);
    }

    /** 怪物首次提示优先于附近遗物提示。 */
    private static InfoContent createInfo(
        MonsterType monsterNoticeType,
        Item nearbyItem
    ) {
        if (monsterNoticeType != null) {
            return new InfoContent(
                monsterDescription(monsterNoticeType),
                MONSTER_ACCENT
            );
        }
        if (nearbyItem != null) {
            ItemRules.Rule rule = ItemRules.ruleFor(nearbyItem.type());
            return new InfoContent(
                "附近有 " + displayName(nearbyItem.type())
                    + "：拾取会激活 " + displayName(rule.monsterType())
                    + "；第 4、5 件会获得 " + displayName(rule.buffType()) + "。",
                ITEM_ACCENT
            );
        }
        return null;
    }

    private static String monsterDescription(MonsterType type) {
        return switch (type) {
            case HUNTER ->
                "发现 Hunter：会锁定你的位置 2 回合并通过 BFS 追踪。";
            case AMBUSHER ->
                "发现 Ambusher：会预测你移动方向前方最多 3 格。";
            case GUARDIAN ->
                "发现 Guardian：靠近守护点时会开始追击。";
            case PATROLLER ->
                "发现 Patroller：会在两个远端点之间巡逻。";
            case COWARD ->
                "发现 Coward：靠近时逃跑，拉开距离后追击。";
        };
    }

    /** 把枚举常量转换为说明文字中使用的英文专有名词。 */
    private static String displayName(Enum<?> value) {
        String lower = value.name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    /** 保存左下角单行日志的文字和颜色。 */
    private record InfoContent(String text, Color accent) {}
}
