# 🚗 多人在线坦克大战游戏

一个基于Canvas和Java Spring Boot的多人在线坦克大战游戏，支持多玩家实时对战、房间系统、聊天功能和排行榜。

## ✨ 功能特性

### 🎮 核心游戏功能
- **多人在线对战**: 支持最多8名玩家同时在线对战
- **实时同步**: 基于WebSocket的实时游戏状态同步
- **多种地图**: 经典地图、竞技场地图、迷宫地图
- **道具系统**: 速度提升、伤害增强、生命恢复、护盾等道具
- **障碍物系统**: 可破坏砖块、钢铁障碍、边界墙

### 🏠 房间系统
- **房间创建**: 玩家可以创建自定义房间
- **快速匹配**: 自动匹配有空位的房间
- **房间管理**: 支持房间列表查看和加入
- **房间状态**: 等待中、进行中、已结束

### 💬 社交功能
- **实时聊天**: 支持房间内聊天和全局聊天
- **排行榜系统**: 多种排行榜（积分、击杀、胜率等）
- **玩家统计**: 详细的个人游戏数据统计

### 🎯 游戏机制
- **坦克控制**: WASD移动，空格键射击
- **碰撞检测**: 精确的碰撞检测系统
- **生命系统**: 生命值、伤害、复活机制
- **得分系统**: 击杀得分、道具得分

## 🛠️ 技术栈

### 前端
- **HTML5 Canvas**: 游戏画面渲染
- **JavaScript ES6+**: 游戏逻辑和交互
- **WebSocket**: 实时通信
- **CSS3**: 现代化UI设计

### 后端
- **Java 11**: 主要开发语言
- **Spring Boot 2.7**: 应用框架
- **Spring WebSocket**: WebSocket支持
- **Spring Data JPA**: 数据持久化
- **MySQL**: 关系型数据库
- **Maven**: 项目构建工具

## 📁 项目结构

```
tank-war-game/
├── frontend/                 # 前端代码
│   ├── index.html           # 主页面
│   └── game.js              # 游戏逻辑
├── backend/                 # 后端代码
│   ├── src/main/java/com/tankwar/server/
│   │   ├── TankWarServerApplication.java  # 主启动类
│   │   ├── config/          # 配置类
│   │   ├── controller/       # REST API控制器
│   │   ├── handler/         # WebSocket处理器
│   │   ├── model/           # 数据模型
│   │   ├── repository/      # 数据访问层
│   │   ├── scheduler/       # 定时任务
│   │   └── service/         # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.yml  # 配置文件
│   └── pom.xml             # Maven配置
└── README.md               # 项目说明
```

## 🚀 快速开始

### 环境要求
- Java 11 或更高版本
- Maven 3.6 或更高版本
- 现代浏览器（Chrome、Firefox、Safari、Edge）
- Python 3（可选，用于启动HTTP服务器）

### 启动步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd tank-war-game
   ```

2. **启动后端服务器**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   
   服务器将在 `http://localhost:8080` 启动，WebSocket服务地址为 `ws://localhost:8080/tank-war`

3. **启动前端服务**
   ```bash
   cd frontend
   python -m http.server 8000
   ```
   然后访问 `http://localhost:8000`

## 👥 多人游戏指南

### 手机热点局域网联机（推荐方式）

本游戏支持通过手机热点创建局域网进行多人联机，以下是详细步骤：

#### 1. 服务器准备（主机）

1. **设置手机热点**
   - 打开您的手机，进入设置 > 个人热点 > 开启WiFi热点
   - 设置一个容易记住的热点名称和密码
   - 记录您的手机热点的IP地址段（通常为192.168.x.x）

2. **连接服务器电脑到热点**
   - 将作为服务器的电脑连接到您的手机热点
   - 在服务器电脑上，打开命令提示符（Windows）或终端（Mac/Linux）
   - 输入 `ipconfig`（Windows）或 `ifconfig`（Mac/Linux）查看本机IP地址
   - **重要**：记录下服务器电脑在热点网络中的IP地址（例如：192.168.80.27）

3. **启动服务器**
   - 确保防火墙允许8080端口（后端）和8000端口（前端）的通信
   - 进入 `tank-war-game/backend` 目录
   - 执行 `mvn spring-boot:run` 启动后端服务器
   - 服务器将在 `http://localhost:8080` 启动，WebSocket服务地址为 `ws://192.168.80.27:8080/tank-war`

4. **启动前端服务**
   - 进入 `tank-war-game/frontend` 目录
   - 执行 `python -m http.server 8000` 启动前端HTTP服务器

#### 2. 其他玩家加入游戏

1. **连接到同一热点**
   - 确保所有玩家的设备都连接到同一个手机热点

2. **访问游戏界面**
   - 在浏览器中输入 `http://192.168.80.27:8000`（使用服务器电脑的实际IP地址）
   - 系统会自动连接到WebSocket服务器

3. **开始游戏**
   - 输入玩家昵称
   - 点击「开始游戏」按钮
   - 等待连接成功后即可加入游戏

### 传统多人连接方式

如果您不使用手机热点，也可以通过以下方式进行多人游戏：

1. **服务器准备**
   - 确保后端服务器在有公网IP的机器上运行，或者使用内网穿透工具（如Ngrok）
   - 确保8080端口（后端）和8000端口（前端）已在防火墙中开放

2. **获取服务器地址**
   - 本地测试：使用 `localhost` 或 `127.0.0.1`
   - 局域网：使用服务器的内网IP地址（如 `192.168.x.x`）
   - 公网：使用服务器的公网IP或域名

3. **修改前端连接地址（如需）**
   如果后端服务器不在本地运行，其他玩家需要修改 `frontend/game.js` 中的WebSocket连接地址：
   ```javascript
   // 修改连接地址为服务器的实际地址
   const candidates = [
       `${protocol}://服务器IP或域名:8080/tank-war`,
       // 其他地址...
   ];
   ```

4. **访问游戏**
   - 所有玩家访问同一个前端地址（如 `http://服务器IP:8000`）
   - 输入昵称后点击「开始游戏」按钮即可加入

### 游戏人数限制
- 最多支持8名玩家同时在线
- 服务器会自动广播新玩家加入和离开的消息

### 热点联机常见问题排查

1. **无法连接到服务器**
   - 确认所有设备都连接到同一个手机热点
   - 验证服务器电脑的IP地址是否正确
   - 检查防火墙是否阻止了端口8080和8000的连接
   - 确认后端和前端服务都已正确启动

2. **连接不稳定**
   - 确保手机电量充足，最好连接充电器
   - 减少热点连接设备数量，避免网络拥堵
   - 保持设备之间距离较近，减少信号干扰

3. **游戏卡顿**
   - 检查热点网络信号强度
   - 关闭其他占用网络带宽的应用
   - 减少同时在线的玩家数量

4. **IP地址变更问题**
   - 如果服务器电脑的IP地址发生变化（如重新连接热点后），需要更新game.js中的连接地址
   - 推荐使用固定IP地址配置（如果手机热点支持）

### 更换热点时如何修改IP地址

当您切换到不同的手机热点或网络环境时，需要更新游戏中的IP地址配置。有三种方法可以实现：

#### 方法一：通过URL参数指定（推荐）
直接在访问游戏时通过URL参数指定服务器IP地址，无需修改任何文件：
```
http://服务器IP:8000?ip=新的服务器IP地址
```
例如：
```
http://192.168.1.100:8000?ip=192.168.1.100
```

#### 方法二：修改game.js文件
1. 打开 `frontend/game.js` 文件
2. 找到默认IP地址配置行（大约在第130行左右）：
   ```javascript
   let serverIp = '192.168.80.27'; // 默认手机热点IP地址
   ```
3. 将 `192.168.80.27` 修改为新热点网络中的服务器IP地址
4. 保存文件并刷新浏览器页面

#### 方法三：重新运行一键启动脚本
如果您使用的是 `start-game.bat` 脚本，它会自动检测并使用当前网络环境中以192.168开头的IPv4地址。只需：
1. 关闭当前运行的所有服务
2. 重新运行 `start-game.bat` 脚本
3. 脚本会自动检测新热点下的IP地址并更新相应配置

**提示**：为了获得最佳游戏体验，建议服务器电脑通过USB数据线连接手机，使用USB共享网络而非WiFi热点，这样可以获得更稳定的连接。

## 🎮 游戏玩法

### 操作说明
- **移动**: WASD键或方向键控制坦克移动
- **射击**: 空格键发射子弹
- **聊天**: 在聊天框输入文字，按Enter发送

### 游戏规则
1. **目标**: 尽可能多地消灭其他玩家，获取高分
2. **生命值**: 被敌人击中会损失生命值
3. **道具系统**:
   - 红色道具: 提升移动速度
   - 蓝色道具: 增强子弹伤害
   - 绿色道具: 恢复生命值
   - 黄色道具: 获得临时护盾
4. **障碍物**: 地图中有固定障碍物，坦克无法穿越
5. **得分系统**: 击杀敌人获得10分，吃到道具获得2分

### 排行榜
- 游戏实时显示玩家分数排名
- 得分越高，排名越靠前

## 📊 游戏状态显示
- **小地图**: 右上角显示所有玩家位置
- **玩家列表**: 左侧显示当前在线玩家
- **聊天区域**: 底部显示游戏聊天消息

4. **开始游戏**
   - 输入玩家昵称
   - 点击"开始游戏"
   - 使用WASD移动，空格键射击

## 🎮 游戏操作

### 基本操作
- **W/↑**: 向上移动
- **S/↓**: 向下移动
- **A/←**: 向左移动
- **D/→**: 向右移动
- **空格键**: 射击
- **Enter**: 发送聊天消息

### 游戏规则
- 每名玩家有100点生命值
- 被子弹击中会减少25点生命值
- 生命值归零后需要等待复活
- 击杀其他玩家获得100分
- 收集道具获得10分

## 🔧 配置说明

### 服务器配置 (application.yml)
```yaml
server:
  port: 8080                    # 服务器端口

game:
  max-players: 8                 # 最大玩家数
  map-width: 800                # 地图宽度
  map-height: 600               # 地图高度
  bullet-speed: 8.0             # 子弹速度
  player-speed: 3.0             # 玩家移动速度
  bullet-damage: 25             # 子弹伤害

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tankwar?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

## 📊 数据库设计

### 数据持久化功能
本游戏使用MySQL数据库进行数据持久化，实现了以下核心功能：

- **玩家分数保存**: 自动保存玩家的游戏分数和统计数据
- **分数继承**: 玩家在游戏中死亡后，分数会自动保存到数据库
- **历史记录**: 记录玩家的游戏历史和成绩
- **数据恢复**: 服务器重启后，玩家重新加入时可恢复历史分数

### 数据库结构

#### player_stats 表

| 字段名 | 数据类型 | 说明 |
| :--- | :--- | :--- |
| id | BIGINT | 主键ID |
| player_name | VARCHAR(255) | 玩家昵称（唯一） |
| score | INT | 当前分数 |
| kills | INT | 击杀数 |
| deaths | INT | 死亡数 |
| games_played | INT | 游戏场数 |
| last_game_time | TIMESTAMP | 最后游戏时间 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 数据库操作流程

1. **玩家首次加入**
   - 系统检查数据库中是否存在该玩家
   - 如果不存在，创建新的玩家记录
   - 如果存在，加载历史分数和统计数据

2. **游戏过程中**
   - 玩家获取分数时实时更新内存中的数据
   - 玩家死亡时，将当前分数保存到数据库

3. **玩家离开游戏**
   - 系统自动将玩家的最新分数和统计数据保存到数据库
   - 更新最后游戏时间

4. **服务器重启**
   - 系统自动创建必要的数据表（如不存在）
   - 保留所有玩家的历史数据

### 数据库配置说明

- **数据库名称**: `tankwar`
- **用户名**: `root`
- **密码**: `1234`
- **自动创建数据库**: 通过 `createDatabaseIfNotExist=true` 参数实现
- **自动建表**: 通过 Spring Data JPA 的 `ddl-auto: update` 配置实现

### MySQL数据库要求

- MySQL 8.0 或更高版本
- 确保MySQL服务已启动
- 默认端口为3306
- 数据库用户需要有创建数据库和表的权限

### 前端配置
可以在 `game.js` 中修改游戏参数：
- 射击冷却时间
- 玩家移动速度
- 画布大小
- WebSocket连接地址

## 📊 API接口

### WebSocket接口
- **连接**: `ws://localhost:8080/tank-war`
- **消息类型**:
  - `join`: 加入游戏
  - `move`: 移动
  - `shoot`: 射击
  - `chat`: 聊天
  - `disconnect`: 断开连接

### REST API接口
- `GET /api/rooms`: 获取房间列表
- `POST /api/rooms/create`: 创建房间
- `POST /api/rooms/quick-match`: 快速匹配
- `GET /api/leaderboard/all`: 获取所有排行榜
- `GET /api/chat/history`: 获取聊天历史

## 🎨 自定义开发

### 添加新地图
1. 在 `MapAndPowerUpService` 中添加新的地图生成方法
2. 在 `MapController` 中添加对应的API接口
3. 在前端添加地图选择功能

### 添加新道具
1. 在 `PowerUp` 模型中添加新的道具类型
2. 在 `GameService` 中实现道具效果逻辑
3. 在前端添加道具显示和效果

### 添加新游戏模式
1. 在 `GameRoom` 中添加游戏模式字段
2. 在 `GameService` 中实现不同的游戏逻辑
3. 在前端添加模式选择界面

## 🐛 故障排除

### 常见问题

1. **无法连接到服务器**
   - 检查服务器是否启动
   - 确认端口8080未被占用
   - 检查防火墙设置

2. **游戏画面卡顿**
   - 检查网络连接
   - 降低游戏帧率
   - 关闭其他占用资源的程序

3. **WebSocket连接失败**
   - 确认浏览器支持WebSocket
   - 检查代理设置
   - 尝试刷新页面

4. **端口占用问题**
   - **Windows系统**：
     ```bash
     # 查找占用8080端口的进程
     netstat -ano | findstr :8080
     # 终止占用端口的进程（PID为查找到的进程ID）
     taskkill /PID [PID] /F
     ```
   - **Mac/Linux系统**：
     ```bash
     # 查找占用8080端口的进程
     lsof -i :8080
     # 终止占用端口的进程（PID为查找到的进程ID）
     kill -9 [PID]
     ```

5. **后端服务重启**
   - **使用Ctrl+C终止**：在运行后端服务的终端中按Ctrl+C键终止服务
   - **强制终止进程**：如果无法正常终止，可参考端口占用清理步骤，找到并终止Java进程
   - **重新启动**：
     ```bash
     cd backend
     mvn spring-boot:run
     ```
   - **使用一键脚本**：执行根目录下的`start-game.bat`（Windows）或`start-server.sh`（Mac/Linux）一键重启服务

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- Spring Boot 团队提供的优秀框架
- HTML5 Canvas 社区的技术支持
- 所有贡献者和测试用户

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至 [your-email@example.com]
- 加入讨论群 [群号]

---

**享受游戏，祝您玩得愉快！** 🎮✨
