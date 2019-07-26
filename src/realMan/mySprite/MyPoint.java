package realMan.mySprite;

import realMan.myGame.MyGame;

import java.awt.*;

/**
 * @program: MyGame
 * @description: 坐标父类
 * @auther: ZhaoXin
 * @create: 2018-12-23 16:39
 **/
public class MyPoint {
    /**
     * 坐标
     */
    private int centerX;
    private int centerY;
    /**
     * 所有坐标点
     */
    private int[][] location;
    /**
     * 半径
     */
    private int radius;

    /**
     * 保存当前转动后的模型
     */
    private Polygon shape;
    /**
     * 碰撞模型
     */
    private Polygon touch;

    /**
     * 构造器
     * @param centerX
     * @param centerY
     * @param location
     */
    public MyPoint(int centerX, int centerY, int[][] location, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.location = location;
        this.radius = radius;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int[][] getLocation() {
        return location;
    }

    public int getRadius() {
        return radius;
    }

    /**
     * 获取点集长度
     * @return
     */
    public int getLength(){
        return location[0].length;
    }

    /**
     * 根据已有数据返回多边形，视图模型
     * @return
     */
    public Polygon getView() {
        if(this.shape == null){
            return new Polygon(location[0], location[1], location[0].length);
        }else{
            return this.shape;
        }
    }

    /**
     * 获取碰撞模型
     * @return
     */
    public Polygon getTouch(){
        if(this.touch == null){
            return new Polygon(location[0], location[1], location[0].length);
        }else{
            return this.touch;
        }
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setLocation(int[][] location){
        this.location = location;
    }

    /**
     * 存储试图模型
     * @param view
     */
    public void setViewShape(Polygon view) {
        this.shape = view;
    }

    /**
     * 存储碰撞模型
     * @param touch
     */
    public void setTouchShape(Polygon touch){
        this.touch = touch;
    }

    /**
     * 检测其他多边的某一点是否被本多边形包含
     * @param polygon 其他多边形
     * @return
     */
    public boolean hit(Polygon polygon){
        for(int i = 0; i < location[0].length; i++){
            int x = location[0][i] + centerX;
            int y = location[1][i] + centerY;
//            包含该点，返回true
            if(polygon.contains(x, y)){
//                debug
                if(MyGame.DEBUG){
                    System.out.println(true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 以当前对象创建新的对象（深拷贝的简单实现）
     * @return
     */
    public MyPoint getThis(){
        int[][] location = new int[2][this.location[0].length];
        for(int row = 0; row < 2; row++){
            for(int col = 0; col < location[row].length; col++){
                location[row][col] = this.location[row][col];
            }
        }
        return new MyPoint(this.centerX, this.centerY, location, radius);
    }
}
