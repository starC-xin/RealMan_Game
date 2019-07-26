package realMan.mySprite;

import realMan.myConf.MyParameter;
import realMan.myGame.MyGame;
import realMan.myUtil.MyUtil;

import java.awt.*;
import java.util.Random;

/**
 * @program: MyGame
 * @description: 子弹类
 * @auther: ZhaoXin
 * @create: 2018-12-25 14:39
 **/
public class MyBullet extends MySprite {
    /**
     * 直线型，跟踪型
     */
    public static final int DEFAULT = 0;
    public static final int LINE = 1;
    public static final int TRACK = 2;
    /**
     * 保存子弹轨迹类型，自主控制轨迹
     */
    private int type;
    /**
     * 直线弹是否设置弹道（直线弹只设置一次弹道）
     */
    private boolean isExchange;
    /**
     * 保存子弹方向，自主控制
     */
    boolean X;
    boolean Y;
    /**
     * 子弹存在时间限制（按帧计算）
     */
    private int liveTime;
    /**
     * 转动角度
     */
    private int angle;

    public MyBullet(int centerX, int centerY, int[][] location, int radius, double acceleration, int maxSizeX, int maxSizeY, Color color) {
        super(centerX, centerY, location, radius, acceleration, maxSizeX, maxSizeY, color);
        this.angle = 0;
        this.isExchange = true;
        liveTime = 100;
    }

    /**
     * 重载转向方法
     */
    public void direction() {
        double tempAngel = angle / 180 * Math.PI;
//        自动旋转
        int targetX = (int)(this.shape.getRadius() * Math.cos(tempAngel));
        int targetY = (int)(this.shape.getRadius() * Math.sin(tempAngel));

        super.direction(targetX, targetY);

//        角度递增，并检测，一圈重置
        angle += 1;
        if(angle > 360){
            angle -= 360;
        }
    }

    /**
     * 直线弹需设置方向
     * (即设置x，y轴最大速度)
     */
    public void direction(int targetX, int targetY){
        switch (type){
            case DEFAULT:
            case LINE:
                if(isExchange){
                    initMove(targetX, targetY);
                    this.move.setAcceleration(3);
//                    设置直线弹弹道无法修改
                    isExchange = false;
                }
                break;
            case TRACK:
//                保存最后逻辑
                X = (targetX - getCenterX()) > 0;
                Y = (targetY - getCenterY()) > 0;
//                跟踪弹一直追踪
                super.direction(targetX, targetY);
                if(isExchange){
                    this.move.setAcceleration(MyParameter.ran.nextDouble() * 2 + 2);
                    this.move.setSpeed(X, Y);
                    isExchange = false;
                }
                break;
        }
    }

    /**
     * 设置初始方向
     * @param targetX
     * @param targetY
     */
    private void initMove(int targetX, int targetY){
        double acceleration = move.getAcceleration();
        Random ran = MyParameter.ran;
        int centerX = MyGame.myGame.hero.getCenterX();
        int centerY = MyGame.myGame.hero.getCenterY();
//        计算方向和偏移量
        double angle = MyUtil.adjustmentDirection(targetX, targetY, centerX, centerY);
//        如果道具有效，则设置随机偏转角度
        if(MyHero.propExist) {
            if (ran.nextBoolean()) {
                angle += ran.nextInt(10) / 180.0 * Math.PI;
            } else {
                angle -= ran.nextInt(10) / 180.0 * Math.PI;
            }
        }

        double speedMaxX = acceleration * Math.cos(angle) * 3;
        double speedMaxY = acceleration * Math.sin(angle) * 3;
//        保存方向
        X = speedMaxX > 0;
        Y = speedMaxY > 0;
//        设置最大值
        this.move.setSpeedMax(Math.abs(speedMaxX), Math.abs(speedMaxY));
    }

    /**
     * 移动逻辑
     */
    public void move(){
        move(!Y, Y, !X, X);
    }

    /**
     * 重写移动方法
     * @param up
     * @param down
     * @param left
     * @param right
     */
    @Override
    public void move(boolean up, boolean down, boolean left, boolean right) {
        super.move(up, down, left, right);
//        减少生存时间
        liveTime--;
        if(liveTime <= 0){
            goDie();
        }
    }

    /**
     * 设置子弹轨迹类型
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取子弹类型
     * @return
     */
    public int getType(){
        return this.type;
    }
}
