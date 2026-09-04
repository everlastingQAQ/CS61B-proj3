# BYOW Design document

---
## 主要玩法

地图里有 5 个东西，但只需要其中 3 个，并且拿走它们会改变世界。
例如拿走“光源核心”后整层变暗，拿走“安静核心”后怪物开始听见你。通关条件还是收集，但每次收集都会改变规则

---
## 阶段一

### 0. 协作

双人协作

### 1.基本世界生成方案 

#### a.生成方法
[迷宫设计](https://indienova.com/indie-game-development/rooms-and-mazes-a-procedural-dungeon-generator/#iah-5)
1. 放置不重叠的矩形
2. 在空隙中放置迷宫（洪水填充算法）
3. 寻找生成树，同时随机增加开放点
4. 移除死胡同

#### b.主要架构
``` text
- Core/
    - WorldGenerator/
        - WorldGenerator.java -- 总调度 生成简单世界
        - RoomGenerator.java -- 放置不重叠的矩形 by Everlasting
        - MazeGenerator.java -- 放置迷宫 by Ichooooooo
        - RegionConnector.java -- 连接房间和迷宫 by Everlasting
        - DeadEndRemover.java -- 移除死胡同 by Ichooooooo
        - Room.java -- Room类记录房子 by Everlasting
        - Test.java -- 小测试生成的简单世界 by Ichooooooo
```

### 2.指令交互

完成interactWithInputString指令

#### 1. 完成
- 输入格式 : N#######S
- 输入字符串中的字母可以是大写或小写。你的引擎必须同时接受两种形式。例如，N 和 n 都应开始世界生成流程。
- interactWithInputString() 不应渲染世界, 它只应返回一个 TETile[][] 数组

--- 

## 阶段二

### 1. 搭建 Input 输入框架

```text
Core/
└── Input/
    ├── Input.java
    ├── InputSource.java
    ├── KeyboardInputSource.java
    └── StringInputSource.java
```

* `InputSource`：统一输入接口。
* `KeyboardInputSource`：处理键盘输入。
* `StringInputSource`：处理字符串输入，用于测试和自动评测。
* `Input`：统一创建不同类型的输入源。

`Engine` 只通过 `InputSource` 获取输入，不需要区分输入来自键盘还是字符串。

### 2. 搭建 Engine 输入处理框架

`Engine` 负责游戏流程控制，根据当前 `GameState` 将输入分发给对应逻辑。

目前状态：

* `MENU`：处理主菜单输入，如 `N / L / Q`。
* `SEED`：读取新游戏种子，读到 `S` 后生成世界。
* `PLAYING`：处理游戏过程中的输入。

整体流程：

```text
InputSource
    ↓
Engine.interact()
    ↓
GameState
    ├── MENU
    ├── SEED
    └── PLAYING
```

具体地图生成、玩家移动等逻辑由对应模块负责，`Engine` 主要负责流程控制和调用。

### 3. 渲染和 UI

```text
Core/
└── UI/
├── MenuRender.java
├── WorldRender.java
└── SeedInputRender.java
```

### 4. 创建用户和用户行为

```text
Core/
└── Player/
├── Player.java
```

#### 1. 属性

- 当前位置

#### 2. 行为

- WASD, moveWUSD(world)
- get位置

#### 3. 构造

- 出生点

### 5. 持久化

```text
Core/
└── Save/
├── savefile.txt
```


## 阶段三

### 1. 细节ui和加入属性

### 2. 实现270分主要功能（实现主要玩法）

### 3. 加入怪物和结束关卡细节

----

## 阶段四

### 1. 实现90次要功能和额外加分
