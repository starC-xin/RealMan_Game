package realMan.mySprite;

import com.sun.beans.editors.ColorEditor;
import realMan.myConf.MyParameter;
import realMan.myGame.MyGame;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: MyGame
 * @description: 英雄类，主角类
 * @auther: ZhaoXin
 * @create: 2018-12-24 15:00
 **/
public class MyHero extends MySprite{
    /**
     * 参数对象
     */
    private MyParameter parameter;

    /**
     * 子弹轨迹类型，默认为直线型
     * 每次射击子弹数量，默认为 5 发
     * 子弹颜色
     * 初始化子弹数量
     * 子弹最大数量
     * 子弹颜色
     * 子弹道具是否有效
     * 道具有效时间
     * 道具剩余时间
     * 无敌状态
     * 无敌数量
     */
    private int bulletType;
    private int bulletCount;
    private int initCount;
    private int bulletCountMax;
    private List<Color> bulletColor;
    public static boolean propExist;
    private final int propTime;
    private int propRemaining;
    private int superMeRemaining;
    private boolean superMe;
    private int superMeCount;

    public MyHero(int centerX, int centerY, int[][] location, int radius, double acceleration, int maxSizeX, int maxSizeY, Color color) {
        super(centerX, centerY, location, radius, acceleration, maxSizeX, maxSizeY, color);
        bulletType = MyBullet.DEFAULT;
        initCount = 3;
        bulletCount = 1;
        bulletCountMax = 6;
        parameter = MyParameter.createParameterConfiguration();
        bulletColor = parameter.getBulletColors();
        propExist = false;
//        坚持十秒
        propTime = 500;
        propRemaining = 0;
        superMeRemaining = 0;
//        无敌状态初始化
        superMe = false;
        superMeCount = 0;
    }

    /**
     * 重载英雄绘制逻辑
     * @param g 画笔
     */
    public void draw(Graphics g, boolean isShoot) {
        Polygon shape = this.shape.getView();
        Polygon touch = this.shape.getTouch();
        MyGame game = MyGame.myGame;
        Graphics2D g2d = (Graphics2D) g;
//        如果英雄正在开枪，则闪烁
        if(isShoot){
            g2d.setColor(game.getRandomColor());
        }else{
            g2d.setColor(myColor);
        }
        g2d.setStroke(new BasicStroke(8.0f));
        g.drawPolygon(shape);
//        绘制剩余时间条
        g2d.fillRect(610 , 100 , 500 * propRemaining / propTime, 20);

//        绘制无敌道具数量
        for(int i = 0; i < superMeCount; i ++){
            g2d.setFont(game.initFont(50));
            g2d.setColor(game.getRandomColor());
            g2d.drawString("S", 560 - i * 20, 50);
        }

//        如果处于无敌状态，绘制无敌视图
        if(superMe){
//            获取随机颜色并修正透明度
            Color color = new Color(game.getRandomColor().getRGB() - 0x50000000, true);
            int radius = this.shape.getRadius() * 2;
            g2d.setColor(color);
            g.drawPolygon(touch);
            g2d.fillOval(getCenterX() - radius, getCenterY() - radius,
                    radius * superMeRemaining  / propTime,
                    radius * superMeRemaining  / propTime);

//            减少无敌时间
            superMeRemaining--;
//            时间到，无敌结束
            if(superMeRemaining <= 0 ){
                superMe = false;
            }
        }
    }

    /**
     * 重置英雄部分基础属性
     * @param randomColor
     */
    public void reSet(Color randomColor) {
        this.setLocation(MyGame.SCREEN_WIDTH / 2, MyGame.SCREEN_HEIGHT / 2);
        this.move.reSet();
        this.myColor = randomColor;
        this.bulletType = MyBullet.DEFAULT;
        this.bulletCount = 1;
        propExist = false;
        propRemaining = 0;
        superMe = false;
        superMeCount = 0;
        superMeRemaining = 0;
    }

    /**
     * 修正子弹类型
     * @param type
     */
    public void setBulletType(int type){
//        判定是否为无敌道具
        if(type == MyParameter.SUPER_ME){
//            限制无敌道具存储数量
            if(superMeCount < 2){
                superMeCount ++;
            }
        }else{
//        相同子弹类型可增加子弹数量，有最大值限制
            propExist = true;
            propRemaining = propTime;
//            修正子弹
            if(this.bulletType == type && bulletCount <= bulletCountMax){
                bulletCount ++;
            }else{
//                不同子弹类型，修正子弹类型，重置子弹数量
                this.bulletType = type;
//                设置子弹数量
                switch (type){
                    case MyBullet.LINE:
                        bulletCount = initCount;
                        break;
                    case MyBullet.TRACK:
                        bulletCount = initCount;
                        break;
                }
            }
        }
    }

    /**
     * 子弹生成时间和CD
     */
    int bulletTime = 0;
    int bulletTimeCount = 5;
    int bulletCD = MyGame.FLUSH_TIME * bulletTimeCount;

    /**
     * 英雄射击
     */
    public List<MyBullet> shoot(boolean isShoot) {
        List<MyBullet> temp = null;
        if(isShoot){
            if(bulletTime % bulletCD == 0){
                if(propRemaining <= 0){
                    propExist = false;
//                    默认直线型子弹数量随时间增加
                    bulletCount = (int)((System.currentTimeMillis() - MyGame.myGame.gameStartTime) / 1000) / 20 + 1;
                    bulletType = MyBullet.DEFAULT;
                }
                temp = generateBullet();
            }
//            生成时间递增
            bulletTime += MyGame.FLUSH_TIME;
//            重置，防止溢出
            if(bulletTime > bulletCD){
                bulletTime -= bulletCD;
            }
        }
        propRemaining --;
        return temp;
    }

    /**
     * 生成子弹
     */
    private  List<MyBullet> generateBullet() {
        List<MyBullet> bullets = new LinkedList<MyBullet>();
//        子弹基本属性设置
        MyGame game = MyGame.myGame;
        int centerX = game.hero.getCenterX();
        int centerY = game.hero.getCenterY();
        int mouseX = game.mouseX;
        int mouseY = game.mouseY;
        int[][] location = MyGame.myParameter.getShape(MyParameter.BULLET, false);
        int radius = 5;
        int acceleration = 6;
        Color color = null;
        switch (bulletType){
            case MyBullet.LINE:
                color = bulletColor.get(0);
                break;
            case MyBullet.TRACK:
                color = bulletColor.get(1);
                break;
            default:
                color = bulletColor.get(2);
        }
//        子弹生成
        for(int i = 0; i < bulletCount; i++){
            MyBullet temp = new MyBullet(centerX, centerY, location, radius, acceleration, MyGame.SCREEN_WIDTH, MyGame.SCREEN_HEIGHT, color);
//            设置子弹轨迹类型
            temp.setType(bulletType);
//            设置初始方向
            temp.direction(mouseX, mouseY);
            bullets.add(temp);
        }
        return bullets;
    }

    /**
     * 获取无敌累积次数
     * @return
     */
    public int getSuperMeCount(){
        return superMeCount;
    }

    /**
     * 开启无敌状态
     */
    public void openSuperMe(){
        superMeCount--;
        superMe = true;
//        初始化有效时间
        superMeRemaining = propTime / 2;
    }

    /**
     * 获取无敌状态
     * @return
     */
    public boolean getSuperMe(){
        return this.superMe;
    }
}
