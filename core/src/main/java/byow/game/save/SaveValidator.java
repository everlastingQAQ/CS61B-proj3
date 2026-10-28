package byow.game.save;

import byow.game.scene.SceneObjectType;
import byow.game.tile.TileType;

import java.util.HashSet;
import java.util.Set;

import static byow.game.GameConfig.ITEM_NUMBER;
import static byow.game.GameConfig.REQUIRED_ITEM_COUNT;

/** 在修改运行中的游戏前检查存档结构与坐标。缺少可选字段的旧存档仍可加载。 */
final class SaveValidator {
    static void validate(GameSave save) {
        require(save != null && save.worldData() != null && save.playerData() != null);
        TileType[][] world = save.worldData().world();
        require(world != null && world.length >= 3 && world.length <= 1024);
        require(world[0] != null && world[0].length >= 3 && world[0].length <= 1024);
        int height = world[0].length;
        for (TileType[] column : world) {
            require(column != null && column.length == height);
            for (TileType tile : column) {
                require(tile != null);
            }
        }
        PlayerData player = save.playerData();
        require(walkable(world, player.x(), player.y()));
        require(player.maxHp() >= 0 && player.currentHp() >= 0 && player.shieldCharges() >= 0);
        require(player.maxHp() == 0 || player.currentHp() <= player.maxHp());
        require(Math.abs((long) player.lastMoveDx()) + Math.abs((long) player.lastMoveDy()) <= 1);
        require(save.visionRadius() >= 0 && save.frozenMonsterTurns() >= 0);
        require(save.items() != null && save.items().size() <= ITEM_NUMBER);
        Set<String> itemPositions = new HashSet<>();
        Set<Object> itemTypes = new HashSet<>();
        for (ItemData item : save.items()) {
            require(item != null && item.type() != null && walkable(world, item.x(), item.y()));
            require(itemPositions.add(key(item.x(), item.y())) && itemTypes.add(item.type()));
            require(item.x() != player.x() || item.y() != player.y());
        }
        if (save.explored() != null) {
            require(save.explored().length == world.length);
            for (boolean[] column : save.explored()) {
                require(column != null && column.length == height);
            }
        }
        Set<Long> monsterIds = new HashSet<>();
        Set<String> monsterPositions = new HashSet<>();
        if (save.monsters() != null) {
            for (MonsterData monster : save.monsters()) {
                require(monster != null && monster.type() != null && monster.state() != null);
                require(monster.id() > 0 && monster.id() < Long.MAX_VALUE && monsterIds.add(monster.id()));
                require(walkable(world, monster.x(), monster.y()));
                require(monsterPositions.add(key(monster.x(), monster.y())));
                require(monster.x() != player.x() || monster.y() != player.y());
                require(walkable(world, monster.homeX(), monster.homeY()));
                require(monster.commitmentTurns() >= 0 && monster.commitmentTurns() <= 2);
                if (monster.commitmentTurns() > 0) {
                    require(walkable(world, monster.committedTargetX(), monster.committedTargetY()));
                }
                if (monster.patrolRouteInitialized()) {
                    require(monster.patrolTargetIndex() == 0 || monster.patrolTargetIndex() == 1);
                    require(walkable(world, monster.patrolAX(), monster.patrolAY()));
                    require(walkable(world, monster.patrolBX(), monster.patrolBY()));
                }
            }
        }
        if (save.seenMonsterTypes() != null) {
            require(save.seenMonsterTypes().stream().allMatch(type -> type != null));
        }
        if (save.exit() != null) {
            ExitData exit = save.exit();
            require(interior(world, exit.x(), exit.y()));
            require(exit.active() == (ITEM_NUMBER - save.items().size() >= REQUIRED_ITEM_COUNT));
            require(world[exit.x()][exit.y()] == (exit.active() ? TileType.OPEN_DOOR : TileType.CLOSED_DOOR));
            require(!itemPositions.contains(key(exit.x(), exit.y())));
        }
        Set<String> objectPositions = new HashSet<>();
        if (save.sceneObjects() != null) {
            for (SceneObjectData object : save.sceneObjects()) {
                require(object != null && object.type() != null && walkable(world, object.x(), object.y()));
                require(objectPositions.add(key(object.x(), object.y())));
                require(!itemPositions.contains(key(object.x(), object.y())));
                require(save.exit() == null || object.x() != save.exit().x() || object.y() != save.exit().y());
                if (object.type() == SceneObjectType.PORTAL) {
                    require(walkable(world, object.targetX(), object.targetY()));
                    require(object.x() != object.targetX() || object.y() != object.targetY());
                    require(save.sceneObjects().stream().anyMatch(other -> other != null
                        && other.type() == SceneObjectType.PORTAL
                        && other.x() == object.targetX() && other.y() == object.targetY()
                        && other.targetX() == object.x() && other.targetY() == object.y()));
                }
            }
        }
    }

    private static boolean interior(TileType[][] world, int x, int y) {
        return x > 0 && x < world.length - 1 && y > 0 && y < world[0].length - 1;
    }

    private static boolean walkable(TileType[][] world, int x, int y) {
        return interior(world, x, y) && world[x][y].isWalkable();
    }

    private static String key(int x, int y) {
        return x + ":" + y;
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new SaveException("存档数据无效，无法读取。");
        }
    }

    private SaveValidator() {}
}
