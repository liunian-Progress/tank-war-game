// 多人在线坦克大战游戏 - 前端JavaScript代码 (优化版)
class TankWarGame {
    constructor() {
        this.canvas = document.getElementById('gameCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.socket = null;
        this.playerId = null;
        this.playerName = '';
        this.gameState = {
            players: new Map(),
            bullets: [],
            obstacles: [],
            powerUps: [],
            gameRunning: false
        };
        
        this.keys = {};
        this.lastShotTime = 0;
        this.shotCooldown = 300; // 射击冷却时间(毫秒)
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 3;
        
        // 初始化UI元素
        this.initUIElements();
        this.init();
    }
    
    initUIElements() {
        // 创建状态提示元素
        this.statusElement = document.createElement('div');
        this.statusElement.className = 'status';
        document.body.appendChild(this.statusElement);
        
        // 创建加载元素
        this.loadingElement = document.createElement('div');
        this.loadingElement.className = 'loading';
        this.loadingElement.setAttribute('data-text', '连接中...');
        document.body.appendChild(this.loadingElement);
    }
    
    init() {
        this.setupEventListeners();
        this.generateObstacles();
        this.generatePowerUps();
        this.gameLoop();
    }
    
    setupEventListeners() {
        // 登录按钮
        document.getElementById('loginButton').addEventListener('click', () => {
            this.login();
        });
        
        // 回车键登录
        document.getElementById('playerName').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.login();
            }
        });
        
        // 键盘事件
        document.addEventListener('keydown', (e) => {
            this.keys[e.key.toLowerCase()] = true;
            
            // 射击
            if (e.key === ' ' && this.gameState.gameRunning) {
                e.preventDefault();
                this.shoot();
            }
            
            // 发送聊天消息
            if (e.key === 'Enter' && document.activeElement.id === 'chatInput') {
                this.sendChatMessage();
            }
        });
        
        document.addEventListener('keyup', (e) => {
            this.keys[e.key.toLowerCase()] = false;
        });
        
        // 聊天输入
        document.getElementById('chatInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.sendChatMessage();
            }
        });
    }
    
    login() {
        const playerName = document.getElementById('playerName').value.trim();
        if (!playerName) {
            this.showStatus('请输入您的昵称！', 'error');
            return;
        }
        
        this.playerName = playerName;
        this.connectToServer();
    }
    
    // 显示状态消息
    showStatus(message, type = 'info', duration = 3000) {
        this.statusElement.textContent = message;
        this.statusElement.className = `status ${type}`;
        
        setTimeout(() => {
            this.statusElement.classList.add('show');
        }, 10);
        
        setTimeout(() => {
            this.statusElement.classList.remove('show');
        }, duration);
    }
    
    // 显示加载动画
    showLoading(show = true) {
        if (show) {
            this.loadingElement.classList.add('active');
        } else {
            this.loadingElement.classList.remove('active');
        }
    }
    
    connectToServer() {
        this.showLoading(true);
        this.showStatus('正在连接服务器...');
		
		// 自适应选择可用的 WebSocket 地址并自动重试
		const protocol = location.protocol === 'https:' ? 'wss' : 'ws';
		const host = location.hostname || 'localhost';
		const currentPort = location.port || '';
		
		// 【远程联机配置】支持动态IP检测和URL参数配置
		// 可以通过URL参数 ip=xxx.xxx.xxx.xxx 来指定服务器IP地址
		let serverIp = '192.168.80.27'; // 默认手机热点IP地址
		
		// 检查URL参数是否指定了IP地址
		const urlParams = new URLSearchParams(window.location.search);
		if (urlParams.has('ip')) {
			serverIp = urlParams.get('ip');
			console.log('从URL参数获取服务器IP:', serverIp);
		}
		
		// 【重要】远程服务器WebSocket地址
		const remoteServerUrl = `ws://${serverIp}:8080/tank-war`;
		console.log('远程服务器地址配置:', remoteServerUrl);
		
		// 确保优先使用局域网IP地址，支持手机热点多人联机
		const candidates = [
			// 如果配置了远程服务器地址，优先使用
			remoteServerUrl,
			// 确保局域网IP地址正确配置
			`ws://${serverIp}:8080/tank-war`,
			// 本地连接地址
			`${protocol}://${host}:8080/tank-war`,
			`${protocol}://127.0.0.1:8080/tank-war`,
			currentPort ? `${protocol}://${host}:${currentPort}/tank-war` : null,
			currentPort ? `${protocol}://127.0.0.1:${currentPort}/tank-war` : null
		].filter(Boolean);
		console.log('连接候选地址列表:', candidates);

		let attemptIndex = 0;
		const tryConnect = () => {
			if (attemptIndex >= candidates.length) {
				this.showLoading(false);
				this.showStatus('无法连接到服务器，请确认后端是否已启动并检查端口设置', 'error');
				return;
			}
			const url = candidates[attemptIndex++];
			console.log('尝试连接 WebSocket:', url);
			this.socket = new WebSocket(url);

			this.socket.onopen = () => {
				console.log('连接到服务器成功:', url);
				this.showStatus('连接成功，正在加入游戏...', 'success');
				this.reconnectAttempts = 0;
				this.sendMessage({
					type: 'join',
					data: this.playerName
				});
			};

			this.socket.onmessage = (event) => {
				const message = JSON.parse(event.data);
				this.handleServerMessage(message);
			};

			this.socket.onclose = (event) => {
				console.log('与服务器连接断开');
				this.showLoading(false);
				if (event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
					this.showStatus('连接断开，尝试重连...', 'error');
					this.reconnectAttempts++;
					setTimeout(() => {
						this.connectToServer();
					}, 2000);
				} else {
					this.showReconnectDialog();
				}
			};

			this.socket.onerror = (error) => {
				console.error('WebSocket连接失败:', error, '，尝试的地址:', url);
				console.log('错误详情:', error.message || '未知错误');
				// 延迟尝试下一个候选地址，避免快速重试阻塞 UI
				setTimeout(tryConnect, 300);
			};
		};

		tryConnect();
    }
    
    sendMessage(message) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(message));
        }
    }
    
    handleServerMessage(message) {
        switch (message.type) {
            case 'playerJoined':
                this.handlePlayerJoined(message);
                break;
            case 'playerLeft':
                this.handlePlayerLeft(message);
                break;
            case 'gameState':
                this.updateGameState(message.data);
                break;
            case 'chatMessage':
                this.handleChatMessage(message);
                break;
            case 'playerId':
                this.playerId = message.playerId;
                this.startGame();
                break;
            case 'playerDied':
                this.handlePlayerDied(message);
                break;
            case 'powerUpCollected':
                this.handlePowerUpCollected(message);
                break;
        }
    }
    
    handlePlayerJoined(message) {
        this.addChatMessage(`${message.playerName} 加入了游戏`);
        this.updatePlayerList();
    }
    
    handlePlayerLeft(message) {
        this.addChatMessage(`${message.playerName} 离开了游戏`);
        this.gameState.players.delete(message.playerId);
        this.updatePlayerList();
    }
    
    handleChatMessage(message) {
        this.addChatMessage(`${message.playerName}: ${message.text}`);
    }
    
    startGame() {
        this.showLoading(false);
        this.showStatus(`欢迎 ${this.playerName}！准备战斗吧！`, 'success');
        
        document.getElementById('loginPanel').classList.add('hidden');
        document.getElementById('gameCanvas').classList.remove('hidden');
        document.getElementById('gameUI').classList.remove('hidden');
        this.gameState.gameRunning = true;
        
        // 提示操作说明
        setTimeout(() => {
            this.showStatus('使用WASD或方向键移动，空格键射击，Enter发送消息', 'info', 5000);
        }, 2000);
    }
    
    updateGameState(data) {
        if (data && data.players) {
            this.gameState.players = new Map(Object.entries(data.players));
        }
        if (data && data.bullets) {
            this.gameState.bullets = data.bullets;
        }
        if (data && data.powerUps) {
            this.gameState.powerUps = data.powerUps;
        }
        if (data && data.obstacles) {
            this.gameState.obstacles = data.obstacles;
        }
        this.updatePlayerList();
        this.updateScoreBoard();
    }
    
    updatePlayerList() {
        const playerList = document.getElementById('playerList');
        playerList.innerHTML = '';
        
        // 按分数排序
        const sortedPlayers = Array.from(this.gameState.players.entries())
            .sort(([id1, player1], [id2, player2]) => player2.score - player1.score);
        
        sortedPlayers.forEach(([id, player]) => {
            const playerItem = document.createElement('div');
            playerItem.className = 'player-item';
            
            // 状态指示器（兼容alive和isAlive两种字段名）
            const isDead = player.alive === false || player.isAlive === false;
            const statusColor = isDead ? '#666' : player.color;
            const statusIcon = isDead ? '✖' : '●';
            
            // 特殊样式（自己）
            if (id === this.playerId) {
                playerItem.style.fontWeight = 'bold';
                playerItem.style.backgroundColor = 'rgba(78, 205, 196, 0.1)';
                playerItem.style.paddingLeft = '8px';
                playerItem.style.borderRadius = '4px';
            }
            
            playerItem.innerHTML = `
                <span style="color: ${statusColor}">${statusIcon}</span> 
                ${player.name} 
                <span style="float: right; font-weight: bold;">${player.score}</span>
            `;
            
            playerList.appendChild(playerItem);
        });
    }
    
    updateScoreBoard() {
        const scoreList = document.getElementById('scoreList');
        scoreList.innerHTML = '';
        
        const sortedPlayers = Array.from(this.gameState.players.values())
            .sort((a, b) => b.score - a.score);
        
        sortedPlayers.forEach((player, index) => {
            const scoreItem = document.createElement('div');
            scoreItem.className = 'score-item';
            
            // 排名标记颜色
            const rankColor = index === 0 ? '#f1c40f' : index === 1 ? '#95a5a6' : index === 2 ? '#cd7f32' : 'white';
            
            // 特殊样式（自己）
            const isSelf = this.gameState.players.get(this.playerId)?.name === player.name;
            const selfStyle = isSelf ? ' style="color: #4ecdc4; font-weight: bold;"' : '';
            
            scoreItem.innerHTML = `
                <span><span style="color: ${rankColor}; font-weight: bold;">${index + 1}.</span> <span${selfStyle}>${player.name}</span></span>
                <span${selfStyle}>${player.score}</span>
            `;
            
            scoreList.appendChild(scoreItem);
        });
    }
    
    addChatMessage(message) {
        const chatMessages = document.getElementById('chatMessages');
        const messageDiv = document.createElement('div');
        
        // 添加时间戳
        const timestamp = new Date().toLocaleTimeString();
        const isSystemMessage = message.includes('加入了游戏') || message.includes('离开了游戏');
        
        if (isSystemMessage) {
            messageDiv.style.color = '#ff6b6b';
            messageDiv.style.fontStyle = 'italic';
            messageDiv.innerHTML = `<span style="opacity: 0.6; font-size: 11px; margin-right: 8px;">[${timestamp}]</span>${message}`;
        } else if (message.startsWith(this.playerName + ':')) {
            messageDiv.style.color = '#4ecdc4';
            messageDiv.innerHTML = `<span style="opacity: 0.6; font-size: 11px; margin-right: 8px;">[${timestamp}]</span>${message}`;
        } else {
            messageDiv.innerHTML = `<span style="opacity: 0.6; font-size: 11px; margin-right: 8px;">[${timestamp}]</span>${message}`;
        }
        
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
    
    sendChatMessage() {
        const chatInput = document.getElementById('chatInput');
        const message = chatInput.value.trim();
        if (message) {
            this.sendMessage({
                type: 'chat',
                playerId: this.playerId,
                playerName: this.playerName,
                text: message
            });
            chatInput.value = '';
        }
    }
    
    // 处理玩家死亡
    handlePlayerDied(message) {
        if (message.playerId === this.playerId) {
            this.showStatus('你被消灭了！页面即将自动刷新...', 'error');
            this.addChatMessage('你被消灭了！页面即将自动刷新...');
            
            // 延迟1秒后自动刷新页面
            setTimeout(() => {
                console.log('玩家死亡，自动刷新页面');
                location.reload();
            }, 1000);
        } else {
            const killerName = message.killerName || '未知玩家';
            const victimName = message.victimName || '未知玩家';
            this.addChatMessage(`${victimName} 被 ${killerName} 消灭了！`);
            
            // 如果是自己消灭了敌人
            if (killerName === this.playerName) {
                this.showStatus('消灭敌人！+10分', 'success');
            }
        }
    }
    
    // 处理道具收集
    handlePowerUpCollected(message) {
        if (message.playerId === this.playerId) {
            const powerUpNames = {
                'speed': '速度提升',
                'damage': '攻击力提升',
                'health': '生命值恢复',
                'shield': '获得护盾'
            };
            this.showStatus(`获得${powerUpNames[message.type] || '未知道具'}！`, 'success');
        }
    }
    
    shoot() {
        const now = Date.now();
        if (now - this.lastShotTime < this.shotCooldown) {
            return;
        }
        
        const player = this.gameState.players.get(this.playerId);
        if (!player) return;
        
        this.lastShotTime = now;
        
        // 计算炮弹发射位置（从炮管末端射出）
        let bulletX = player.x + 10; // 坦克中心x
        let bulletY = player.y + 10; // 坦克中心y
        const barrelLength = 10; // 炮管长度（根据炮管绘制代码调整为10像素）
        
        // 根据方向计算炮弹初始位置（从炮管末端）
        switch(player.direction) {
            case 0: // 上
                bulletY -= barrelLength;
                break;
            case 1: // 右
                bulletX += barrelLength;
                break;
            case 2: // 下
                bulletY += barrelLength;
                break;
            case 3: // 左
                bulletX -= barrelLength;
                break;
        }
        
        this.sendMessage({
            type: 'shoot',
            playerId: this.playerId,
            data: {
                x: bulletX,
                y: bulletY,
                direction: player.direction
            }
        });
    }
    
    gameLoop() {
        this.update();
        this.render();
        requestAnimationFrame(() => this.gameLoop());
    }
    
    update() {
        if (!this.gameState.gameRunning) return;
        
        const player = this.gameState.players.get(this.playerId);
        if (!player) return;
        
        let moved = false;
        let newX = player.x;
        let newY = player.y;
        let newDirection = player.direction;
        
        // 处理移动 - 恢复原始速度，让坦克移动更快
        if (this.keys['w'] || this.keys['arrowup']) {
            newY = Math.max(20, player.y - player.speed); // 使用原始速度，让坦克移动更快
            newDirection = 0;
            moved = true;
        }
        if (this.keys['s'] || this.keys['arrowdown']) {
            newY = Math.min(this.canvas.height - 20, player.y + player.speed);
            newDirection = 2;
            moved = true;
        }
        if (this.keys['a'] || this.keys['arrowleft']) {
            newX = Math.max(20, player.x - player.speed);
            newDirection = 3;
            moved = true;
        }
        if (this.keys['d'] || this.keys['arrowright']) {
            newX = Math.min(this.canvas.width - 20, player.x + player.speed);
            newDirection = 1;
            moved = true;
        }
        
        // 检查碰撞
        if (this.checkCollision(newX, newY)) {
            return;
        }
        
        // 发送移动信息
        if (moved) {
            this.sendMessage({
                type: 'move',
                playerId: this.playerId,
                data: {
                    x: newX,
                    y: newY,
                    direction: newDirection
                }
            });
        }
    }
    
    checkCollision(x, y) {
        // 检查与障碍物的碰撞
        for (const obstacle of this.gameState.obstacles) {
            if (this.isColliding(x, y, 20, obstacle.x, obstacle.y, obstacle.width, obstacle.height)) {
                return true;
            }
        }
        
        // 检查与其他玩家的碰撞
        for (const [id, player] of this.gameState.players) {
            if (id !== this.playerId && this.isColliding(x, y, 20, player.x, player.y, 20, 20)) {
                return true;
            }
        }
        
        return false;
    }
    
    isColliding(x1, y1, r1, x2, y2, w2, h2) {
        return x1 < x2 + w2 && x1 + r1 > x2 && y1 < y2 + h2 && y1 + r1 > y2;
    }
    
    render() {
        // 清空画布
        this.ctx.fillStyle = '#2c3e50';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        
        // 绘制网格
        this.drawGrid();
        
        // 绘制障碍物
        this.drawObstacles();
        
        // 绘制道具
        this.drawPowerUps();
        
        // 绘制玩家
        this.drawPlayers();
        
        // 绘制子弹
        this.drawBullets();
        
        // 绘制UI信息
        this.drawUI();
    }
    
    drawGrid() {
        this.ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
        this.ctx.lineWidth = 1;
        
        for (let x = 0; x < this.canvas.width; x += 40) {
            this.ctx.beginPath();
            this.ctx.moveTo(x, 0);
            this.ctx.lineTo(x, this.canvas.height);
            this.ctx.stroke();
        }
        
        for (let y = 0; y < this.canvas.height; y += 40) {
            this.ctx.beginPath();
            this.ctx.moveTo(0, y);
            this.ctx.lineTo(this.canvas.width, y);
            this.ctx.stroke();
        }
    }
    
    drawObstacles() {
        this.ctx.fillStyle = '#8B4513';
        this.gameState.obstacles.forEach(obstacle => {
            this.ctx.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            
            // 绘制边框
            this.ctx.strokeStyle = '#654321';
            this.ctx.lineWidth = 2;
            this.ctx.strokeRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        });
    }
    
    drawPowerUps() {
        this.gameState.powerUps.forEach(powerUp => {
            this.ctx.fillStyle = powerUp.color;
            this.ctx.beginPath();
            this.ctx.arc(powerUp.x, powerUp.y, powerUp.radius, 0, Math.PI * 2);
            this.ctx.fill();
            
            // 绘制闪烁效果
            this.ctx.strokeStyle = '#fff';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        });
    }
    
    drawPlayers() {
        this.gameState.players.forEach((player, id) => {
            // 如果玩家死亡，不绘制（兼容alive和isAlive两种字段名）
            if (player.alive === false || player.isAlive === false) return;
            
            this.ctx.save();
            this.ctx.translate(player.x + 10, player.y + 10);
            this.ctx.rotate(player.direction * Math.PI / 2);
            
            // 绘制坦克阴影效果
            this.ctx.fillStyle = 'rgba(0, 0, 0, 0.3)';
            this.ctx.fillRect(-8, -8, 16, 16);
            
            // 绘制坦克主体
            this.ctx.fillStyle = player.color;
            this.ctx.fillRect(-10, -10, 20, 20);
            
            // 绘制坦克边框
            this.ctx.strokeStyle = '#333';
            this.ctx.lineWidth = 1;
            this.ctx.strokeRect(-10, -10, 20, 20);
            
            // 绘制坦克装饰
            this.ctx.fillStyle = 'rgba(255, 255, 255, 0.2)';
            this.ctx.fillRect(-5, -5, 10, 10);
            
            // 绘制坦克炮管
            this.ctx.fillStyle = '#333';
            this.ctx.fillRect(-2, -15, 4, 10);
            
            // 炮管装饰
            this.ctx.fillStyle = 'rgba(255, 255, 255, 0.3)';
            this.ctx.fillRect(-1, -14, 2, 6);
            
            // 绘制坦克履带
            this.ctx.fillStyle = '#666';
            this.ctx.fillRect(-8, -12, 16, 4);
            this.ctx.fillRect(-8, 8, 16, 4);
            
            // 自己的坦克添加发光效果
            if (id === this.playerId) {
                this.ctx.strokeStyle = 'rgba(78, 205, 196, 0.6)';
                this.ctx.lineWidth = 2;
                this.ctx.strokeRect(-11, -11, 22, 22);
            }
            
            this.ctx.restore();
            
            // 绘制玩家名称
            this.ctx.fillStyle = '#fff';
            this.ctx.font = '12px Arial';
            this.ctx.textAlign = 'center';
            this.ctx.fillText(player.name, player.x + 10, player.y - 5);
            
            // 绘制生命值（移到玩家正上方）
            // 移除条件判断，确保血条始终显示
            const health = player.health !== undefined ? player.health : 100; // 默认满血
            
            // 生命值背景
            this.ctx.fillStyle = 'rgba(0, 0, 0, 0.5)';
            this.ctx.fillRect(player.x - 15, player.y - 25, 30, 6);
            
            // 生命值条
            let healthColor = '#e74c3c'; // 红色
            if (health > 70) healthColor = '#2ecc71'; // 绿色
            else if (health > 30) healthColor = '#f1c40f'; // 黄色
            
            this.ctx.fillStyle = healthColor;
            this.ctx.fillRect(player.x - 15, player.y - 25, (health / 100) * 30, 6);
        });
    }
    
    drawBullets() {
        this.ctx.fillStyle = '#FFD700';
        this.gameState.bullets.forEach(bullet => {
            // 绘制子弹发光效果
            this.ctx.shadowColor = '#FFD700';
            this.ctx.shadowBlur = 8;
            
            this.ctx.beginPath();
            this.ctx.arc(bullet.x, bullet.y, 3, 0, Math.PI * 2);
            this.ctx.fill();
            
            this.ctx.shadowBlur = 0;
            
            // 绘制子弹轨迹
            this.ctx.strokeStyle = 'rgba(255, 165, 0, 0.5)';
            this.ctx.lineWidth = 2;
            this.ctx.beginPath();
            this.ctx.moveTo(bullet.x, bullet.y);
            this.ctx.lineTo(bullet.x - bullet.vx * 5, bullet.y - bullet.vy * 5);
            this.ctx.stroke();
        });
    }
    
    drawUI() {
        // 绘制小地图 - 移动到左下角，避免被排行榜遮挡
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
        this.ctx.fillRect(10, this.canvas.height - 110, 140, 100);
        
        this.ctx.strokeStyle = '#fff';
        this.ctx.lineWidth = 1;
        this.ctx.strokeRect(10, this.canvas.height - 110, 140, 100);
        
        // 绘制小地图标题
        this.ctx.fillStyle = '#4ecdc4';
        this.ctx.font = '12px Arial';
        this.ctx.textAlign = 'center';
        this.ctx.fillText('小地图', 80, this.canvas.height - 95);
        
        // 在小地图上绘制玩家位置
        const scaleX = 140 / this.canvas.width;
        const scaleY = 100 / this.canvas.height;
        
        this.gameState.players.forEach(player => {
            // 只在小地图上显示存活的玩家
            if (player.alive === false || player.isAlive === false) return;
            
            this.ctx.fillStyle = player.color;
            this.ctx.beginPath();
            this.ctx.arc(
                10 + player.x * scaleX,
                this.canvas.height - 110 + player.y * scaleY,
                2, 0, Math.PI * 2
            );
            this.ctx.fill();
            
            // 标记自己的位置
            if (player.id === this.playerId || player.name === this.playerName) {
                this.ctx.strokeStyle = '#fff';
                this.ctx.lineWidth = 1;
                this.ctx.beginPath();
                this.ctx.arc(
                    10 + player.x * scaleX,
                    this.canvas.height - 110 + player.y * scaleY,
                    4, 0, Math.PI * 2
                );
                this.ctx.stroke();
            }
        });
        
        // 绘制障碍物在小地图上
        this.ctx.fillStyle = '#8B4513';
        this.gameState.obstacles.forEach(obstacle => {
            this.ctx.fillRect(
                10 + obstacle.x * scaleX,
                this.canvas.height - 110 + obstacle.y * scaleY,
                obstacle.width * scaleX,
                obstacle.height * scaleY
            );
        });
    }
    
    generateObstacles() {
        const obstacles = [];
        
        // 生成随机障碍物
        for (let i = 0; i < 15; i++) {
            obstacles.push({
                x: Math.random() * (this.canvas.width - 40),
                y: Math.random() * (this.canvas.height - 40),
                width: 30 + Math.random() * 20,
                height: 30 + Math.random() * 20
            });
        }
        
        this.gameState.obstacles = obstacles;
    }
    
    generatePowerUps() {
        const powerUps = [];
        
        // 生成随机道具
        const powerUpTypes = [
            { color: '#FF6B6B', type: 'speed' },
            { color: '#4ECDC4', type: 'damage' },
            { color: '#45B7D1', type: 'health' },
            { color: '#96CEB4', type: 'shield' }
        ];
        
        for (let i = 0; i < 5; i++) {
            powerUps.push({
                x: Math.random() * (this.canvas.width - 20),
                y: Math.random() * (this.canvas.height - 20),
                radius: 8,
                type: powerUpTypes[Math.floor(Math.random() * powerUpTypes.length)].type,
                color: powerUpTypes[Math.floor(Math.random() * powerUpTypes.length)].color
            });
        }
        
        this.gameState.powerUps = powerUps;
    }
    
    showReconnectDialog() {
        this.showStatus('连接断开', 'error');
        if (confirm('与服务器连接断开，是否重新连接？')) {
            this.connectToServer();
        }
    }
}

// 启动游戏
document.addEventListener('DOMContentLoaded', () => {
    // 初始化游戏
    new TankWarGame();
    
    // 添加ESC键显示操作提示
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            const gameInstance = window.tankWarGame || {};
            if (gameInstance.showStatus) {
                gameInstance.showStatus('操作说明：\nWASD或方向键移动\n空格键射击\nEnter发送消息\nESC显示此提示', 'info', 5000);
            }
        }
    });
});
