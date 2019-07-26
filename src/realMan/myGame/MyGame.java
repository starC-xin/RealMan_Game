package realMan.myGame;

import realMan.myConf.MyParameter;
import realMan.mySprite.*;
import realMan.myUtil.MyUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @program: MyGame
 * @description:
 * @auther: ZhaoXin
 * @create: 2018-12-23 10:43
 **/
public class MyGame extends JPanel implements Runnable {
    /**
     * DEBUG
     */
    public static final boolean DEBUG = false;
    /**
     * 刷新频率
     */
    public static final int FLUSH_TIME = 20;

    /**
     * 屏幕宽度和高度
     */
    public static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 100;
    public static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100;

    /**
     * 版本号
     */
    private final String version = "v 2.1.2";

    public static MyGame myGame;

    /**
     * 获取参数对象
     */
    public static final MyParameter myParameter = MyParameter.createParameterConfiguration();

    /**
     * 面板自身坐标位置（左上角）
     */
    final int panelX = 10;
    final int panelY = 10;
    /**
     * 设置边框粗细
     */
    final int border = 6;

    /**
     * 随机器
     */
     Random ran;
    /**
     * 游戏状态
     */
    public static final int GAME_START = 0;
    public static final int GAME_INFORMATION = 1;
    public static final int GAME_RUNNING = 2;
    public static final int GAME_PAUSE = 3;
    public static final int GAME_OVER = 4;
    public static final int GAME_VICTORY = 5;
    /**
     * 当前游戏状态
     */
    int currentGameState;

    /**
     * 弧度控制
     */
    double angle;

    /**
     * 鼠标坐标
     */
    public int mouseX;
    public int mouseY;

    /**
     * 开始界面的UI敌人坐标
     */
    List<MyPoint> start;
    /**
     * 鼠标是否在某个范围内
     */
    boolean mouseUpIn;
    boolean mouseDownIn;

    /**
     * 颜色
     */
    List<Color> colors;
    /**
     * 颜色数量
     */
    int colorCount;
    /**
     * 颜色刷新间隔
     */
    int colorTime;
    /**
     * 颜色刷新冷却
     */
    int colorCoolDown;
    /**
     * 字体初始化
     */
    Font font;
    /**
     * 设置系统字体兼容
     */
    public static final String[] fontName = {"迷你简卡通", "华文琥珀"};
    /**
     * 游戏总时间 s
     */
    double gameTime;
    /**
     * 游戏花费时间
     */
    double gameSpendTime;
    /**
     * 游戏开始时间
     */
    public double gameStartTime;
    /**
     * 游戏开始暂停时刻
     */
    double gamePauseStart;

    /**
     * 主角，英雄对象
     */
    public MyHero hero;
    /**
     * 英雄色
     */
    Color heroColor;
    /**
     * 四个方向，控制英雄四个方向上的状态，是否有移动倾向
     */
    boolean up;
    boolean down;
    boolean left;
    boolean right;

    /**
     * 敌人群/道具群
     */
    List<MySprite> sprite;
    /**
     * 提取敌人群
     */
    List<MyEnemy> enemysCopy;
    /**
     * 子弹类
     */
    List<MyBullet> bullets;
    /**
     * 是否发射子弹
     */
    private boolean isShoot;

    /**
     * 构造器
     */
    public MyGame() {
        myGame = this;
//        初始化基本属性
        initBasicAttribute();
//        初始化自身面板属性
        initMine();
//        初始化键盘监听器
        initKeyAdapter();
//        初始化鼠标监听器
        initMouseAdapter();
//        初始化焦点监听器
        initFocusAdapter();
//        初始化精灵类
        initSprite();
    }

    /**
     * 初始化基本属性
     */
    private void initBasicAttribute() {
        currentGameState = GAME_START;
        angle = 0;
//        start = new MyPoint[MyUtil.getCount()];
        mouseX = SCREEN_WIDTH / 2;
        mouseY = SCREEN_HEIGHT / 2;
        ran = MyParameter.ran;
//        初始化鼠标标识符
        mouseUpIn = false;
        mouseDownIn = false;

//        初始化颜色
        colors = myParameter.getSpriteColors();
        colorCount = colors.size();
        colorTime = 0;
        colorCoolDown = 200;
        startColor = colors.get(0);
        mousePressedColor = colors.get(7);
        mouseReleasedColor = new Color(0x7FFFFFFF, true);

//        设置双重缓冲
        this.setDoubleBuffered(true);

        initStartUI();
    }

    /**
     * 初始化自身JPanle属性
     */
    private void initMine() {
//        设置位置和大小
        this.setBounds(panelX, panelY, SCREEN_WIDTH - panelX * 3, SCREEN_HEIGHT - panelY * 5);
    }

    /**
     * 设置键盘监听器
     */
    private void initKeyAdapter() {
        KeyAdapter keyAdapter = new KeyAdapter() {
            /**
             * 按键按下
             * @param e
             */
            @Override
            public void keyPressed(KeyEvent e) {
//                super.keyPressed(e);
//                控制主角移动
                if (currentGameState == GAME_RUNNING) {
                    exchangeMoveState(e.getKeyCode(), true);
                }
//                R 键开启/关闭无敌模式，如果有机会的话
                if(e.getKeyCode() == 82 && hero.getSuperMeCount() > 0){
                    hero.openSuperMe();
                }
            }

            /**
             * 按键弹起
             * @param e
             */
            @Override
            public void keyReleased(KeyEvent e) {
//                控制主角移动
                if (currentGameState == GAME_RUNNING) {
                    exchangeMoveState(e.getKeyCode(), false);
                }


//                是否按下空格，实现游戏暂停
                if (currentGameState == GAME_RUNNING && e.getKeyCode() == 32) {
                    currentGameState = GAME_PAUSE;
//                    存储开始暂停的时间，用于计算暂停耗时
                    gamePauseStart = System.currentTimeMillis();
                } else if (currentGameState == GAME_PAUSE && e.getKeyCode() == 32) {
                    currentGameState = GAME_RUNNING;
//                    加上游戏耗时
                    gameStartTime += System.currentTimeMillis() - gamePauseStart;
                }else if(currentGameState == GAME_PAUSE && e.getKeyCode() == 27){
//                    暂停中按esc回到主界面
                    currentGameState = GAME_START;
                    reSetGameData();
                }else if(currentGameState == GAME_INFORMATION && e.getKeyCode() == 27){
//                    实现玩法介绍界面后退至游戏主界面
//                    如果翻页，则返回上一页
                    if(next){
                        next = false;
                    }else{
                        currentGameState = GAME_START;
                    }
                }
            }
        };
        this.addKeyListener(keyAdapter);
    }

    /**
     * 初始化鼠标监听器
     */
    private void initMouseAdapter() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            /**
             * 点击事件
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
//                控制游戏状态
                if (mouseUpIn && currentGameState == GAME_START) {
//                    游戏开始 > 游戏简介
                    currentGameState = GAME_INFORMATION;
                    font = null;
                    mouseUpIn = false;
                    heroColor = colors.get(ran.nextInt(colors.size()));
                    next = false;
                } else if (mouseUpIn && currentGameState == GAME_INFORMATION) {
//                    游戏简介 > 游戏中
                    if(next){
                        currentGameState = GAME_RUNNING;
                        font = null;
                        mouseUpIn = false;
                        gameStartTime = System.currentTimeMillis();
                        gameTime = 100;
                        gameSpendTime = 100;
                    }else{
                        next = true;
                    }
                } else if (currentGameState == GAME_VICTORY) {
//                    游戏胜利 > 游戏开始
                    currentGameState = GAME_START;
                }else if(currentGameState == GAME_OVER){
//                    游戏失败 > 游戏开始 | 游戏中
                    if(mouseUpIn){
                        currentGameState = GAME_START;
                        reSetGameData();
                    }else if(mouseDownIn){
                        currentGameState = GAME_RUNNING;
                        gameStartTime = System.currentTimeMillis();
                        reSetGameData();
                    }
                }
            }

            /**
             * 鼠标按下
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e) {
                mouseHoldOn = true;
                isShoot = true;
            }

            /**
             * 鼠标弹起
             * @param e
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseHoldOn = false;
                isShoot = false;
            }

            /**
             * 按住并拖动鼠标
             * @param e
             */
            @Override
            public void mouseDragged(MouseEvent e) {
//                super.mouseDragged(e);
                mouseX = e.getX();
                mouseY = e.getY();

            }

            /**
             * 鼠标控制转向
             * @param e
             */
            @Override
            public void mouseMoved(MouseEvent e) {
//                saveMouseLocation(e.getX(), e.getY());
                mouseX = e.getX();
                mouseY = e.getY();
                if (currentGameState == GAME_START && mouseX > startWidth && mouseX < startWidth + 700 &&
                        mouseY > startHeight - 120 && mouseY < startHeight) {
                    mouseUpIn = true;
                } else if (currentGameState == GAME_INFORMATION && mouseX > informationWidth && mouseX < informationWidth + 400 &&
                        mouseY > informationHeight - 40 && mouseY < informationHeight) {
                    mouseUpIn = true;
                } else if (currentGameState == GAME_OVER && mouseX > overUpWidth && mouseX < overUpWidth + 1050 &&
                        mouseY > overUpHeight - 150 && mouseY < overUpHeight){
                    mouseUpIn = true;
                }else if(currentGameState == GAME_OVER && mouseX > overDownWidth && mouseX < overUpWidth + 890 &&
                        mouseY > overDownHeight - 150 && mouseY < overDownHeight){
                    mouseDownIn = true;
                }else {
                    mouseUpIn = false;
                    mouseDownIn = false;
                }
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    /**
     * 初始化焦点监听器
     */
    private void initFocusAdapter() {
        FocusAdapter focusAdapter = new FocusAdapter() {
            /**
             * 获得焦点时调用
             * @param e
             */
            @Override
            public void focusGained(FocusEvent e) {
//                super.focusGained(e);
                try {
                    Robot robot = new Robot();
//                    切换输入法全半角
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                } catch (AWTException e1) {
                    e1.printStackTrace();
                    System.out.println("机器人创建失败");
                }
            }
        };
        this.addFocusListener(focusAdapter);
    }

    /**
     * 初始化精灵类
     */
    private void initSprite() {
//        初始化英雄类
        heroColor = colors.get(ran.nextInt(colors.size()));
        int[][] location = {
                {0, -3, 2, 3, 3, 0, -3, -3, -2, 3, 0},
                {4, -2, 3, 2, -2, -3, -2, 2, 3, -2, 4}
        };
        int heroX = SCREEN_WIDTH / 2;
        int heroY = SCREEN_HEIGHT / 2;
        int radius = 35;
        int acceleration = 2;
        hero = new MyHero(heroX, heroY, location, radius, acceleration, SCREEN_WIDTH, SCREEN_HEIGHT, heroColor);

        up = false;
        down = false;
        left = false;
        right = false;

//        初始化敌人/道具
        sprite = new LinkedList<MySprite>();
        enemysCopy = new LinkedList<MyEnemy>();
//        初始化子弹集合
        bullets = new LinkedList<MyBullet>();
    }

    /**
     * 添加开始界面的装饰敌人
     */
    private void initStartUI() {
        int[] centerX = myParameter.getArrayFloat(MyParameter.CENTER_X);
        int[] centerY = myParameter.getArrayFloat(MyParameter.CENTER_Y);

        int[][] pointX = myParameter.getArrayDouble(MyParameter.POINT_X);
        int[][] pointY = myParameter.getArrayDouble(MyParameter.POINT_Y);

        int[] radius = myParameter.getArrayFloat(MyParameter.RADIUS);

        start = new ArrayList<MyPoint>();
        for (int i = 0; i < centerX.length; i++) {
            int[][] location = new int[][]{
                    pointX[i],
                    pointY[i]
            };
            start.add(new MyPoint(centerX[i], centerY[i], location, radius[i]));
        }
    }

    /**
     * 改变主角移动方向状态值
     *
     * @param keyCode 按键值
     * @param state   状态值
     */
    private void exchangeMoveState(int keyCode, boolean state) {
        switch (keyCode) {
            case 87:
            case KeyEvent.VK_UP:
                up = state;
                break;
            case 83:
            case KeyEvent.VK_DOWN:
                down = state;
                break;
            case 65:
            case KeyEvent.VK_LEFT:
                left = state;
                break;
            case 68:
            case KeyEvent.VK_RIGHT:
                right = state;
                break;
        }
    }

    /**
     * 游戏开始
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * 重写线程方法
     */
    @Override
    public void run() {
        while (true) {
            try {
//                获取系统当前时间（开始）
                long startTime = System.currentTimeMillis();

//                游戏逻辑，所有需要自动执行的操作
                logic();
//                重新绘制画面
                repaint();

//                获取系统当前时间（结束）
                long endTime = System.currentTimeMillis();
//                获取间隔时间
                long runTime = endTime - startTime;
//                实现匀速刷新（相对匀速，每帧刷新间隔）
                if (runTime < FLUSH_TIME) {
                    Thread.sleep(FLUSH_TIME - runTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 游戏所有逻辑
     */
    private void logic() {
        switch (currentGameState) {
            case GAME_RUNNING:
//                英雄逻辑
                heroLogic();
//                生成飞行物
                generateEnemy();
//                敌人/道具移动
                enemysMove();
//                子弹移动
                bulletsMove();
//                英雄与敌人/道具接触
                heroTouchSprite();
//                敌人与子弹接触
                enemyTouchBullet();
//                飞行物越界处理
                bulletOutOfBounds();
//                已死亡飞行物删除
                removeSprite();
                break;
            case GAME_OVER:
                break;
            case GAME_VICTORY:
                break;
        }
    }

    /**
     * 子弹移动
     */
    private void bulletsMove() {
//            子弹移动逻辑
        for(MyBullet temp : bullets){
//            自转
            temp.direction();
            switch(temp.getType()){
//                直线弹逻辑
                case MyBullet.DEFAULT:
                case MyBullet.LINE:
                    temp.move();
                    break;
//                    跟踪弹逻辑
                case MyBullet.TRACK:
//                    检测目标状态，存活则追踪，否则换目标
                    if(temp.checkTargetLife()){
                        temp.trackTarget();
                    }else{
//                        如果有敌人，随机获取一个目标敌人，并追踪
                        int size = enemysCopy.size();
                        if(size > 0){
                            int randomNum = ran.nextInt(size);
                            MySprite target = enemysCopy.get(randomNum);
                            temp.setTarget(target);
                            temp.trackTarget();
                        }else{
//                            否则，执行预置方向的移动
                            temp.move();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 英雄逻辑
     */
    private void heroLogic() {
        hero.direction(mouseX, mouseY);
        hero.move(up, down, left, right);
//        发射子弹
        List<MyBullet> bulletsTemp = hero.shoot(isShoot);
        if(bulletsTemp != null && bulletsTemp.size() > 0){
            bullets.addAll(bulletsTemp);
        }
    }

    /**
     * 敌人生成时间和CD
     */
    int enemyTime = 0;
    int enemyCount = 50;
    int enemyCD = FLUSH_TIME * enemyCount;

    /**
     * 生成飞行物
     */
    private void generateEnemy() {
//        每刷新 count 帧，才生成飞行物
        if(enemyTime % enemyCD == 0){
            MySprite obj = nextOne();
            sprite.add(obj);
            if(obj instanceof MyEnemy){
                enemysCopy.add((MyEnemy) obj);
            }
        }
        enemyTime += FLUSH_TIME;
//          重置，防止溢出
        if(enemyTime > enemyCD){
            enemyTime -= enemyCD;
        }
//        随时间提升生成频率
        enemyCount = 50 - (int)((gameTime - gameSpendTime) / 5) * 3;
        enemyCD = FLUSH_TIME * enemyCount;
    }

    /**
     * 获取一个飞行物
     * @return
     */
    private MySprite nextOne() {
//        获取概率
        int probability = ran.nextInt(100);
//        生成飞行物类型：97%敌人/3%道具
        boolean genType = probability > 10;

//        计算基本属性
        int radius;
        Color color;
        if(genType){
            radius = 20 + ran.nextInt(20);
            color = getRandomColor();
        }else{
            radius = 40;
            color = null;
        }
        int centerX = radius + 1;
        int centerY = radius + 1;
//        计算出生方向
        int randomDirection = ran.nextInt(4);
        switch (randomDirection){
            case 0:
                centerX++;
                centerY += ran.nextInt(SCREEN_HEIGHT - radius * 3 - 3);
                break;
            case 1:
                centerX += ran.nextInt(SCREEN_WIDTH - radius * 3 - 3);
                centerY++;
                break;
            case 2:
                centerX = SCREEN_WIDTH - radius * 2 - 2;
                centerY += ran.nextInt(SCREEN_HEIGHT - radius * 3 - 3);
                break;
            case 3:
                centerX += ran.nextInt(SCREEN_WIDTH - radius * 3 - 3);
                centerY = SCREEN_HEIGHT - radius * 2 - 2;
                break;
        }
//        设置形状
        int[][] shape;
//        随时间提升速度（敌人）/固定速度（道具）
        double acceleration;
        MySprite obj = null;
        if(genType){
            shape = myParameter.getShape(MyParameter.ENEMY, false);
            acceleration = ran.nextDouble() * (gameTime - gameSpendTime) / 25 + 1;
            obj = new MyEnemy(centerX, centerY, shape, radius, acceleration, SCREEN_WIDTH + radius, SCREEN_HEIGHT + radius, color);
        }else{
            shape = myParameter.getShape(MyParameter.PROP, false);
            acceleration = 1;
            obj = new MyProp(centerX, centerY, shape, radius, acceleration, SCREEN_WIDTH + radius, SCREEN_HEIGHT + radius, color);
        }
        obj.setTarget(hero);
        return obj;
    }

    /**
     * 飞行物移动和转向
     */
    private void enemysMove() {

        for(int i = 0; i < sprite.size(); i++){
            MySprite temp = sprite.get(i);
            if(temp instanceof MyEnemy){
//                跟踪逻辑
                temp.trackTarget();
            }else{
                MyProp prop = (MyProp)temp;
                prop.move();
            }
        }
    }

    /**
     * 超人模式
     * true 为关闭
     */
    boolean superMe = true;

    /**
     * 英雄与敌人或者道具接触
     */
    private void heroTouchSprite() {
//        遍历所有敌人/道具
//        碰撞检测
        for(MySprite temp : sprite){
            if(superMe && hero.hit(temp)){
                if(temp instanceof MyEnemy){
//                    无敌判定，是则杀死敌人，否则检测是否有无敌道具，有则开启，无则结束
                    if(hero.getSuperMe()){
                        temp.goDie();
                    }else if(hero.getSuperMeCount() > 0){
                        hero.openSuperMe();
                        temp.goDie();
                    }else{
                        currentGameState = GAME_OVER;
                        tempColorTime = getRandomColor();
                    }
//                    测试
                    if(DEBUG){
                        System.out.println("碰撞检测");
                    }
                    return;
                }else{
                    MyProp myProp = (MyProp) temp;
                    hero.setBulletType(myProp.getType());
                    temp.goDie();
                }
            }
        }
    }

    /**
     * 敌人与子弹接触
     */
    private void enemyTouchBullet() {
//        遍历所有子弹和敌人
        for(int i = 0; i < sprite.size(); i++){
            MySprite temp = sprite.get(i);
            for(int j = 0; j < bullets.size(); j++){
                MyBullet bullet = bullets.get(j);
                if(temp instanceof MyEnemy){
                    if(temp.hit(bullet)){
                        temp.goDie();
                        bullet.goDie();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 子弹/道具越界处理
     */
    private void bulletOutOfBounds() {
        MySprite temp;
//        子弹
        for(int i = 0; i < bullets.size(); i++){
            temp = bullets.get(i);
            if(temp.checkBounds()){
//                越界去世
                temp.goDie();
            }
        }
//        飞行物
        for(int i = 0; i < sprite.size(); i++){
            temp = sprite.get(i);
            if(temp.checkBounds()){
//                越界去世
                temp.goDie();
            }
        }
//        敌人（copy）
        for(int i = 0; i < enemysCopy.size(); i++){
            temp = enemysCopy.get(i);
            if(temp.checkBounds()){
//                越界去世
                temp.goDie();
            }
        }
    }

    /**
     * 删除已死亡飞行物
     */
    private void removeSprite() {
//        删除死亡敌人/道具
        for(int i = 0; i < sprite.size(); i++){
            if(!sprite.get(i).checkLife()){
                sprite.remove(i);
            }
        }
//        删除已死亡敌人
        for(int i = 0; i < enemysCopy.size();i++){
            if(enemysCopy.get(i).checkLife()){
                enemysCopy.remove(i);
            }
        }
//        删除死亡子弹
        for(int i = 0; i < bullets.size(); i++){
            if(!bullets.get(i).checkLife()){
                bullets.remove(i);
            }
        }
    }

    /**
     * 重置游戏数据，为下次游戏准备
     */
    private void reSetGameData() {
//        敌人生成频率重置
        enemyCount = 50;
        enemyCD = enemyCount * FLUSH_TIME;

        up = false;
        down = false;
        left = false;
        right = false;
        hero.reSet(getRandomColor());

//        敌人和子弹清理
        sprite.clear();
        bullets.clear();
    }

    /**
     * 重写绘制方法
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
//        super.paint(g);
//        绘制背景，覆盖
        g.setColor(new Color(0x4C4A48));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        drawGrid(g);
//        全局颜色设置
        g.setColor(new Color(0xF6FCFD));

//        状态机，界面选择
        switch (currentGameState) {
            case GAME_START:
                font = initFont(150);
                drawStart(g);
                break;
            case GAME_INFORMATION:
                font = initFont(100);
                howToPlay(g);
                break;
            case GAME_RUNNING:
//                绘制英雄
                hero.draw(g, isShoot);
//                绘制敌人
                for(int i = 0; i < sprite.size(); i++){
                    sprite.get(i).draw(g);
                }
//                绘制子弹
                for(int i = 0; i < bullets.size(); i++){
                    bullets.get(i).draw(g);
                }
//                绘制UI
                font = initFont(50);
                gamePlaying(g);
                break;
            case GAME_PAUSE:
                font = initFont(200);
                gamePause(g);
                break;
            case GAME_OVER:
                gameOver(g);
                break;
            case GAME_VICTORY:
                font = initFont(200);
                gameVictory(g);
                break;
        }
//        隐藏鼠标，绘制准心
        drawCursor(g);
    }

    /**
     * 鼠标是否按住，状态值
     */
    boolean mouseHoldOn = false;
    /**
     * 按住和松开之后的准心颜色
     */
    Color mousePressedColor;
    Color mouseReleasedColor;

    /**
     * 隐藏鼠标，绘制准心
     * @param g
     */
    private void drawCursor(Graphics g) {
//        隐藏鼠标
        URL classUrl = this.getClass().getResource("");
        Image imageCursor = Toolkit.getDefaultToolkit().createImage(classUrl);
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(imageCursor, new Point(0, 0), "cursor"));

//        绘制准心
        Graphics2D g2d = (Graphics2D) g;
        if (mouseHoldOn) {
            g2d.setColor(mousePressedColor);
        } else {
            g2d.setColor(mouseReleasedColor);
        }
        g2d.setStroke(new BasicStroke(3.0f));
        mouseAimX = mouseX - mouseAimWidth / 2;
        mouseAimY = mouseY - mouseAimHeight / 2;
        g2d.drawOval(mouseAimX, mouseAimY, mouseAimWidth, mouseAimHeight);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int x1 = mouseAimX + mouseAimWidth * i;
                int y1 = mouseAimY + mouseAimHeight * j;
                int x2 = mouseAimX + mouseAimWidth / 3 * (i + 1);
                int y2 = mouseAimY + mouseAimHeight / 3 * (j + 1);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * 绘制背景网格
     *
     * @param g
     */
    private void drawGrid(Graphics g) {
//        设置画笔，32位表示RGB及其alpha通道（最高位）
        g.setColor(new Color(0x2A070C22, true));
//        计算初始坐标
        int x1 = panelX - border;
        int y1 = panelY - border;
        int x2 = SCREEN_WIDTH - panelX * 3 - border;
        int y2 = SCREEN_HEIGHT - panelY * 5 - border;
//        画横线
        for (int row = 0; row < SCREEN_HEIGHT; row += SCREEN_HEIGHT / 40) {
            g.drawLine(x1, y1 + row, x2, y1 + row);
        }
//        画竖线
        for (int col = 0; col < SCREEN_WIDTH; col += SCREEN_WIDTH / 50) {
            g.drawLine(x1 + col, y1, x1 + col, y2);
        }
    }

    /**
     * 开始游戏字段左下角坐标
     */
    int startWidth = 500;
    int startHeight = 500;

    Color startColor;

    /**
     * 绘制开始界面
     *
     * @param g
     */
    private void drawStart(Graphics g) {
        Color temp = null;
//        DEBUG
        if (DEBUG) {
            g.drawString("x:" + mouseX + ",y:" + mouseY, 200, 400);
        }
//        标题
        g.setFont(font);
        g.drawString("是男人就坚持一百秒", 100, 200);
        if (mouseUpIn) {
            temp = g.getColor();
            g.setColor(new Color(0x486860));
        }
        g.drawString("开始游戏！", startWidth, startHeight);

//        绘制版本号
        if(temp != null){
            g.setColor(temp);
        }
        g.setFont(initFont(50));
        g.drawString(version, 760, 590);

//        开始界面的敌人图案
        Graphics2D g2d = (Graphics2D) g;
//        随机颜色设置逻辑
        if(colorTime % colorCoolDown == 0){
            startColor = getRandomColor();
        }
        colorTime += FLUSH_TIME;
//        重置，防止溢出
        if(colorTime >= 10000){
            colorTime = 0;
        }

        g.setColor(startColor);
        g2d.setStroke(new BasicStroke(4.0f));
        for (int i = 0; i < start.size(); i++) {
//            获取随机颜色，间隔一段时间
            g.drawPolygon(MyUtil.drawTankAngle(start.get(i).getThis(), this.mouseX, this.mouseY));
        }
    }

    /**
     * 介绍页面点击范围
     */
    int informationWidth = 1210;
    int informationHeight = 840;
    /**
     * 是否翻页
     */
    boolean next;

    /**
     * 游戏玩法介绍
     */
    private void howToPlay(Graphics g) {
//        玩法介绍
        if(next){
            g.setFont(initFont(70));
            Color tempColor = g.getColor();
            MyPoint temp;
            List<Color> tempArrColor = myParameter.getBulletColors();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(8.0f));
            int i, j;
            int[][] x = myParameter.getArrayDouble(MyParameter.POINT_X);
            int[][] y = myParameter.getArrayDouble(MyParameter.POINT_Y);
            for(i = x.length - 3, j = 1; i < x.length; i++, j++){
                int centerY = j * 200;
                Color tempSpriteColor = j < 3 ? tempArrColor.get(j - 1) : getRandomColor();
                temp = new MyPoint(200, centerY - 50, new int[][]{x[i], y[i]}, 80);
                g.setColor(tempSpriteColor);
                g.drawPolygon(MyUtil.drawTankAngle(temp, mouseX, mouseY));
            }
            g.setColor(tempColor);
            g.drawString("触碰后获得             子弹", 360, 190);
            g.drawString("触碰后获得             子弹", 360, 370);
            g.drawString("触碰后获得         状态，按 R 可使用", 360, 570);
            g.setColor(tempArrColor.get(0));
            g.drawString("散弹型", 740, 190);
            g.setColor(tempArrColor.get(1));
            g.drawString("跟踪型", 740, 370);

            g.setColor(tempArrColor.get(2));
            g.drawString("注 ：道具有时间限制", 360, 690);
            g.drawString("默认为直线型子弹", 530, 760);
            g.setColor(getRandomColor());
            g.drawString("无敌", 740, 570);
        }else{
            g.setFont(font);
            g.drawString("玩法介绍：", 50, 100);
            g.drawString("本游戏采用键鼠联合控制", 100, 250);
            g.drawString("鼠标移动控制射击方向", 100, 350);
            g.drawString("鼠标左键射击", 100, 450);
            g.drawString("W,A,S,D(或上下左右)控制主角移动", 100, 550);
            g.drawString("(PS：推荐使用上下左右控制英雄)", 100, 650);
            g.drawString("空格键可暂停", 100, 750);
        }
        g.setFont(initFont(50));
        g.setColor(colors.get(3));
        g.drawString("按Esc返回", 100, 850);

        if (mouseUpIn) {
            g.setColor(getRandomColor());
        }
        String str = next ? "少BB，直接开始！" : "少BB，翻下一页！";
        g.drawString(str, informationWidth, informationHeight);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(10.0f));
//        绘制箭头
        int[] x = {0, 3, 1, 1, -1, -1, -3};
        int[] y = {10, 0, 1, -1, -1, 1, 0};
//        g.fillPolygon(x, y, x.length);
        g.drawPolygon(MyUtil.drawTankAngle(new MyPoint(1150, 820, new int[][]{x, y}, 30), mouseX, mouseY));
//        调试用
        if (DEBUG) {
            g.drawString("x:" + mouseX + ",y:" + mouseY, 100, 850);
        }
    }

    /**
     * 准心坐标和高宽
     */
    int mouseAimX;
    int mouseAimY;
    int mouseAimWidth = 36;
    int mouseAimHeight = 36;

    /**
     * 正式游戏画面
     *
     * @param g
     */
    private void gamePlaying(Graphics g) {
        g.setColor(new Color(0xF6FCFD));
        g.setFont(font);
        gameSpendTime = gameTime - (System.currentTimeMillis() - gameStartTime) / 1000;
        if (gameSpendTime < 0) {
            tempColorTime = getRandomColor();
            currentGameState = GAME_VICTORY;
        }
        g.drawString("游戏剩余时间：" + String.format("%.3f", gameSpendTime), 600, 50);
    }

    /**
     * 绘制游戏暂停画面
     *
     * @param g
     */
    private void gamePause(Graphics g) {
        g.setFont(font);
        g.drawString("PAUSE!", 450, 350);
        g.drawString("空格返回游戏", 250, 550);
        g.drawString("esc返回主界面", 180, 750);
    }

    /**
     * 绘制胜利画面
     *
     * @param g
     */
    private void gameVictory(Graphics g) {
        g.setFont(font);
        g.drawString("CHEERS！", 400, 400);
        g.setFont(initFont(150));
        g.setColor(tempColorTime);
        g.drawString("You are a real man!", 50, 600);
    }

    /**
     * 保存临时颜色，防止按钮闪烁
     */
    Color tempColorButton;
    Color tempColorTime;
    /**
     * 设置结束界面的按钮左下角坐标
     */
    int overUpWidth = SCREEN_WIDTH / 5;
    int overUpHeight = SCREEN_HEIGHT / 2 + 100;
    int overDownWidth = SCREEN_WIDTH / 4 - 50;
    int overDownHeight = SCREEN_HEIGHT / 4 * 3 + 100;

    /**
     * 绘制游戏结束画面
     * @param g
     */
    private void gameOver(Graphics g) {
        Color temp = g.getColor();
        g.setFont(initFont(150));
        g.drawString("小老弟8行!才坚持", 20, 180);

        g.setColor(tempColorTime);
        g.drawString(String.format("%.3f", gameTime - gameSpendTime) + "s", 1260, 180);
        g.setColor(temp);

        g.setFont(initFont(200));
        g.drawString("GAME OVER!", SCREEN_WIDTH / 8, SCREEN_HEIGHT / 4 + 130);
//        刷新临时按钮画笔
        if(!mouseUpIn && !mouseDownIn){
            this.tempColorButton = getRandomColor();
        }
//        保存当前颜色，并设置画笔颜色
//        绘制第一个选项
        if(mouseUpIn){
            g.setColor(this.tempColorButton);
        }
        g.drawString("算了，算了", overUpWidth, overUpHeight);
//        恢复画笔颜色
        g.setColor(temp);
//        绘制第二个选项
        if(mouseDownIn){
            g.setColor(this.tempColorButton);
        }
        g.drawString("再来一把！", overDownWidth, overDownHeight);
        g.setColor(temp);
//        调试用
        if (DEBUG) {
            g.drawString("x:" + mouseX + ",y:" + mouseY, 100, 850);
        }

    }

    /**
     * 获取随机颜色
     */
    public Color getRandomColor() {
        Color temp = colors.get(ran.nextInt(colors.size()));
//        不允许和英雄色相同
        while(temp == heroColor){
            temp = colors.get(ran.nextInt(colors.size()));
        }
        return temp;
    }

    /**
     * 获取字体
     *
     * @param size
     */
    public Font initFont(int size) {
        if (this.font != null) {
            if(this.font.getSize() == size){
                return this.font;
            }else{
                return this.font.deriveFont((float)size);
            }
        }else{
            Font font = null;
            font = MyUtil.getFont(size);
            return font;
        }
    }
}
