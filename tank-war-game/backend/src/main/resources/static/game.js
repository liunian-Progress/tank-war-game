// 多人在线坦克大战游戏 - 前端JavaScript代码
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
        
        this.init();
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
            alert('请输入您的昵称！');
            return;
        }
        
        this.playerName = playerName;
        this.connectToServer();
    }
    
    connectToServer() {
        // 连接到WebSocket服务器
        this.socket = new WebSocket('ws://localhost:8080/tank-war');
        
        this.socket.onopen = () => {
            console.log('连接到服务器成功');
            this.sendMessage({
                type: 'join',
                data: this.playerName
            });
        };
        
        this.socket.onmessage = (event) => {
            const message = JSON.parse(event.data);
            this.handleServerMessage(message);
        };
        
        this.socket.onclose = () => {
            console.log('与服务器连接断开');
            this.showReconnectDialog();
        };
        
        this.socket.onerror = (error) => {
            console.error('WebSocket错误:', error);
            alert('无法连接到服务器，请检查服务器是否启动');
        };
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
        document.getElementById('loginPanel').classList.add('hidden');
        document.getElementById('gameCanvas').classList.remove('hidden');
        document.getElementById('gameUI').classList.remove('hidden');
        this.gameState.gameRunning = true;
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
        
        this.gameState.players.forEach((player, id) => {
            const playerItem = document.createElement('div');
            playerItem.className = 'player-item';
            playerItem.innerHTML = `
                <span style="color: ${player.color}">●</span> 
                ${player.name} 
                <span style="float: right;">${player.score}</span>
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
            scoreItem.innerHTML = `
                <span>${index + 1}. ${player.name}</span>
                <span>${player.score}</span>
            `;
            scoreList.appendChild(scoreItem);
        });
    }
    
    addChatMessage(message) {
        const chatMessages = document.getElementById('chatMessages');
        const messageDiv = document.createElement('div');
        messageDiv.textContent = message;
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
    
    shoot() {
        const now = Date.now();
        if (now - this.lastShotTime < this.shotCooldown) {
            return;
        }
        
        const player = this.gameState.players.get(this.playerId);
        if (!player) return;
        
        this.lastShotTime = now;
        
        this.sendMessage({
            type: 'shoot',
            playerId: this.playerId,
            data: {
                x: player.x,
                y: player.y,
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
        
        // 处理移动
        if (this.keys['w'] || this.keys['arrowup']) {
            newY = Math.max(20, player.y - player.speed);
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
            this.ctx.save();
            this.ctx.translate(player.x + 10, player.y + 10);
            this.ctx.rotate(player.direction * Math.PI / 2);
            
            // 绘制坦克主体
            this.ctx.fillStyle = player.color;
            this.ctx.fillRect(-10, -10, 20, 20);
            
            // 绘制坦克炮管
            this.ctx.fillStyle = '#333';
            this.ctx.fillRect(-2, -15, 4, 10);
            
            // 绘制坦克履带
            this.ctx.fillStyle = '#666';
            this.ctx.fillRect(-8, -12, 16, 4);
            this.ctx.fillRect(-8, 8, 16, 4);
            
            this.ctx.restore();
            
            // 绘制玩家名称
            this.ctx.fillStyle = '#fff';
            this.ctx.font = '12px Arial';
            this.ctx.textAlign = 'center';
            this.ctx.fillText(player.name, player.x + 10, player.y - 5);
        });
    }
    
    drawBullets() {
        this.ctx.fillStyle = '#FFD700';
        this.gameState.bullets.forEach(bullet => {
            this.ctx.beginPath();
            this.ctx.arc(bullet.x, bullet.y, 3, 0, Math.PI * 2);
            this.ctx.fill();
            
            // 绘制子弹轨迹
            this.ctx.strokeStyle = '#FFA500';
            this.ctx.lineWidth = 2;
            this.ctx.beginPath();
            this.ctx.moveTo(bullet.x, bullet.y);
            this.ctx.lineTo(bullet.x - bullet.vx * 5, bullet.y - bullet.vy * 5);
            this.ctx.stroke();
        });
    }
    
    drawUI() {
        // 绘制小地图
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.7)';
        this.ctx.fillRect(this.canvas.width - 150, 10, 140, 100);
        
        this.ctx.strokeStyle = '#fff';
        this.ctx.lineWidth = 1;
        this.ctx.strokeRect(this.canvas.width - 150, 10, 140, 100);
        
        // 在小地图上绘制玩家位置
        const scaleX = 140 / this.canvas.width;
        const scaleY = 100 / this.canvas.height;
        
        this.gameState.players.forEach(player => {
            this.ctx.fillStyle = player.color;
            this.ctx.beginPath();
            this.ctx.arc(
                this.canvas.width - 150 + player.x * scaleX,
                10 + player.y * scaleY,
                2, 0, Math.PI * 2
            );
            this.ctx.fill();
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
        if (confirm('与服务器连接断开，是否重新连接？')) {
            this.connectToServer();
        }
    }
}

// 启动游戏
document.addEventListener('DOMContentLoaded', () => {
    new TankWarGame();
});
