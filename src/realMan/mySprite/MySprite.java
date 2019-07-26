package realMan.mySprite;

import realMan.myGame.MyGame;
import realMan.myUtil.MyUtil;

import java.awt.*;

/**
 * @program: MyGame
 * @description: 精灵父类
 * @auther: ZhaoXin
 * @create: 2018-12-24 15:10
 **/
public class MySprite implements MyTarget{
    /**
     * 点集，碰撞模型
     */
    protected MyPoint shape;

    /**
     * 设置移动类，计算移动后坐标
     */
    protected MyMove move;

    /**
     * 移动边界
     */
    protected int maxSizeX;
    protected int maxSizeY;

    /**
     * 自身的颜色
     */
    protected Color myColor;

    /**
     * 生命状态
     */
    protected boolean lifeState;

    /**
     * 追踪目标
     */
    protected MySprite target;

    /**
     * 根据已有对象进行初始化
     * @param shape 设置相对点集
     * @param move 设置移动类
     * @param maxSizeX X 轴边界
     * @param maxSizeY Y 轴边界
     */
    public MySprite(MyPoint shape, MyMove move, int maxSizeX, int maxSizeY, Color color) {
        this.shape = shape;
        this.move = move;
        this.maxSizeX = maxSizeX;
        this.maxSizeY = maxSizeY;
        this.myColor = color;
    }

    /**
     * 根据传参进行初始化
     * @param centerX 中心坐标 X
     * @param centerY 中心坐标 Y
     * @param location 相对点集坐标
     * @param radius 半径
     * @param acceleration 加速度
     * @param maxSizeX X 轴边界
     * @param maxSizeY Y 周边界
     */
    public MySprite(int centerX, int centerY, int[][] location, int radius, double acceleration, int maxSizeX, int maxSizeY, Color color){
        this.shape = new MyPoint(centerX, centerY, location, radius);
        this.move = new MyMove(acceleration, radius);
        this.maxSizeX = maxSizeX;
        this.maxSizeY = maxSizeY;
        this.myColor = color;
        this.lifeState = true;
    }

    /**
     * 计算移动，移动逻辑
     * @param up
     * @param down
     * @param left
     * @param right
     */
    public void move(boolean up, boolean down, boolean left, boolean right){
        boolean xOrY = true;
        shape.setCenterX(move.move(left, right, xOrY, shape.getCenterX(), this.maxSizeX));
        shape.setCenterY(move.move(up, down, !xOrY, shape.getCenterY(), this.maxSizeY));
    }

    /**
     * 当前对象朝向，依据是目标点位置
     * @param targetX 目标点坐标 X
     * @param targetY 目标点坐标 Y
     */
    public void direction(int targetX, int targetY){
        MyPoint temp = shape.getThis();
//        不理睬返回值，根据引用类型特征设置视图模型和碰撞模型
        MyUtil.drawTankAngle(temp, targetX, targetY);
        shape.setViewShape(temp.getView());
        shape.setTouchShape(temp.getTouch());
    }

    /**
     * 跟踪目标（含移动和转向）
     */
    public void trackTarget(){
        boolean X;
        boolean Y;
        int targetCenterX = this.target.getCenterX();
        int targetCenterY = this.target.getCenterY();
        X = targetCenterX - this.getCenterX() > 0;
        Y = targetCenterY - this.getCenterY() > 0;
//        转向
        this.direction(targetCenterX, targetCenterY);
//        移动
        this.move(!Y, Y, !X, X);
    }

    /**
     * 检测目标生命状态
     * @return
     */
    public boolean checkTargetLife(){
        return target != null && target.checkLife();
    }

    /**
     * 碰撞检测，如果碰撞，返回true
     * @param other
     * @return
     */
    public boolean hit(MySprite other){
        return this.shape.hit(other.shape.getTouch()) || other.shape.hit(this.shape.getTouch());
    }

    /**
     * 设置坐标
     * @param centerX
     * @param centerY
     */
    public void setLocation(int centerX, int centerY){
        shape.setCenterX(centerX);
        shape.setCenterY(centerY);
    }

    /**
     * 设置跟踪对象
     * @param target
     */
    public void setTarget(MySprite target){
        this.target = target;
    }

    /**
     * 绘制当前图形
     * @param g 画笔
     */
    public void draw(Graphics g) {
        Polygon shape = this.shape.getView();
        Polygon touch = this.shape.getTouch();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(myColor);

        g2d.setStroke(new BasicStroke(8.0f));
        g.drawPolygon(shape);
//        测试用，绘制碰撞视图
        if(MyGame.DEBUG){
            g.drawPolygon(touch);
        }
    }

    /**
     * 获取 X 坐标
     * @return
     */
    public int getCenterX(){
        return this.shape.getCenterX();
    }

    /**
     * 获取 Y 坐标
     * @return
     */
    public int getCenterY(){
        return this.shape.getCenterY();
    }

    /**
     * 检查生命状态
     */
    public boolean checkLife(){
        return lifeState;
    }

    /**
     * 死亡设置
     */
    public void goDie(){
        this.lifeState = false;
    }

    /**
     * 越界检测
     * @return
     */
    public boolean checkBounds(){
        int centerX = getCenterX();
        int centerY = getCenterY();
        int radius = shape.getRadius();
        return centerX == radius + 1 || centerX == maxSizeX - radius * 2 - 1 ||
                centerY == radius + 1 || centerY == maxSizeY - radius * 2 - 1;
    }
}
