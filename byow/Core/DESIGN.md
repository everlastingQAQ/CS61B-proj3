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

### 1. 搭建好 Input 框架

### 2. 实现主菜单与基础命令

### 3. 实现存档与读档

### 4. 加入玩家系统



## 阶段三

### 1. 细节ui和加入属性

### 2. 实现270分主要功能（实现主要玩法）

### 3. 加入怪物和结束关卡细节

----

## 阶段四

### 1. 实现90次要功能和额外加分
