package realMan.mySprite;

import realMan.myConf.MyParameter;
import realMan.myGame.MyGame;
import realMan.myUtil.MyUtil;

import java.awt.*;

/**
 * @program: MyGame
 * @description: 道具类
 * @auther: ZhaoXin
 * @create: 2018-12-26 21:25
 **/
public class MyProp extends MySprite{
    /**
     * 方向
     */
    private boolean X;
    private boolean Y;
    private int targetX;
    private int targetY;
    /**
     * 奖励类型
     */
    private int type;

    public MyProp(int centerX, int centerY, int[][] location, int radius, double acceleration, int maxSizeX, int maxSizeY, Color color) {
        super(centerX, centerY, location, radius, acceleration, maxSizeX, maxSizeY, color);
        if(location[0].length == 9){
//            直线弹道具初始化
            type = MyBullet.LINE;
            this.myColor = MyGame.myParameter.getBulletColors().get(0);
        }else{
//            跟踪弹道具初始化
            if(location[1][0] == 1){
                type = MyBullet.TRACK;
                this.myColor = MyGame.myParameter.getBulletColors().get(1);
            }else{
//                无敌道具初始化
                type = MyParameter.SUPER_ME;
            }
        }
    }

    /**
     * 重写转向方法
     * @param targetX 目标点坐标 X
     * @param targetY 目标点坐标 Y
     */
    @Override
    public void direction(int targetX, int targetY) {
        MyPoint temp = shape.getThis();
//        不理睬返回值，根据引用类型特征设置视图模型和碰撞模型
        MyUtil.drawTankAngle(temp, targetX, targetY);
        shape.setViewShape(temp.getView());
//        单独设置碰撞模型
        temp.setLocation(MyGame.myParameter.getShape(1, true));
        shape.setTouchShape(temp.getTouch());
    }

    /**
     * 保存目标坐标
     * @param target
     */
    @Override
    public void setTarget(MySprite target) {
        super.setTarget(target);
        targetX = this.target.getCenterX();
        targetY = this.target.getCenterY();
    }

    /**
     * 无敌道具不一样，需重新绘制
     * @param g 画笔
     */
    @Override
    public void draw(Graphics g) {
        Polygon shape = this.shape.getView();
        Graphics2D g2d = (Graphics2D) g;

//        重写上色逻辑
        if(type == MyParameter.SUPER_ME){
            g2d.setColor(MyGame.myGame.getRandomColor());
        }else{
            g2d.setColor(myColor);
        }

        g2d.setStroke(new BasicStroke(8.0f));
        g.drawPolygon(shape);
    }

    /**
     * 封装移动方法
     */
    public void move(){
        direction(targetX, targetY);
        X = targetX - this.getCenterX() > 0;
        Y = targetY - this.getCenterY() > 0;
        int acceleration = (int)this.move.getAcceleration();
        targetX += X ? acceleration * 3 : -acceleration * 3;
        targetY += Y ? acceleration * 3 : -acceleration * 3;
        move(!Y, Y, !X, X);
    }

    /**
     * 获取奖励类型
     * @return
     */
    public int getType() {
        return type;
    }
}
