package realMan.mySprite;

/**
 * @program: MyGame
 * @description: 移动类，控制坐标移动
 * @auther: ZhaoXin
 * @create: 2018-12-22 21:33
 **/
public class MyMove{
    /**
     * 减速时加速度
     */
    private final double unAcceleration;
    /**
     * 加速时加速度
     */
    private double acceleration;
    /**
     * 移动初速度
     */
    private double moveStartSpeedX;
    private double moveStartSpeedY;
    /**
     * 移动末速度
     */
    private double moveEndSpeedX;
    private double moveEndSpeedY;
    /**
     * 速度最大值
     */
    private double moveSpeedMaxX;
    private double moveSpeedMaxY;
    /**
     * 对应坐标的高度/宽度
     */
    private int bodySize;

    /**
     * 构造器，初始化必要数值
     * @param acceleration 设置加速度
     * @param bodySize 设置自身大小（半径）
     */
    public MyMove(double acceleration, int bodySize) {
        this.acceleration = acceleration;
        this.unAcceleration = acceleration / 6;
        this.moveStartSpeedX = 0;
        this.moveEndSpeedX = 0;
        this.moveStartSpeedY = 0;
        this.moveEndSpeedY = 0;
        this.moveSpeedMaxX = 3 * acceleration;
        this.moveSpeedMaxY = 3 * acceleration;
        this.bodySize = bodySize;
    }

    /**
     * 移动控制
     * @param direction_0 加速度方向 上左
     * @param direction_1 加速度方向 下右
     * @param XorY X 或者 Y 轴
     * @param moveLocation 当前坐标
     * @param maxSize 边界限制
     * @return 移动后的坐标
     */
    public int move(boolean direction_0, boolean direction_1, boolean XorY, int moveLocation, int maxSize) {
        double speedStart;
        double speedEnd;
        double moveSpeedMax;
//        判断 X 轴还是 Y 轴
        if(XorY){
            speedStart = moveStartSpeedX;
            speedEnd = moveEndSpeedX;
            moveSpeedMax = moveSpeedMaxX;
        }else{
            speedStart = moveStartSpeedY;
            speedEnd = moveEndSpeedY;
            moveSpeedMax = moveSpeedMaxY;
        }
//        速度增量
        if(direction_0 && speedStart > -moveSpeedMax){
            speedEnd -= acceleration;
        }else if(speedStart < 0){
            speedEnd += unAcceleration;
        }
        if(direction_1 && speedStart < moveSpeedMax){
            speedEnd += acceleration;
        }else if(speedStart > 0){
            speedEnd -= unAcceleration;
        }

//        浮点数计算存在误差，一旦在误差范围内，则归零
        if(speedEnd > -0.0001 && speedEnd < 0.0001){
            speedEnd = 0;
        }
//        计算位移后坐标
        moveLocation = moveDistance(moveLocation, maxSize, speedStart, speedEnd);
//        重置初速度与末速度
        if(XorY){
            moveStartSpeedX = speedEnd;
            moveEndSpeedX = speedEnd;
        }else{
            moveStartSpeedY = speedEnd;
            moveEndSpeedY = speedEnd;
        }
        return moveLocation;
    }

    /**
     * 运动距离计算
     * @param moveLocation
     * @param moveMaxSize
     * @return
     */
    private int moveDistance(int moveLocation, int moveMaxSize, double moveSpeedStart, double moveSpeedEnd){
//        不允许越界
        if(moveLocation > bodySize && moveLocation < moveMaxSize - bodySize * 2){
            moveLocation += (moveSpeedStart + moveSpeedEnd) / 2;
        }
        if(moveLocation <= bodySize){
            moveLocation = bodySize + 1;
        }
        if(moveLocation >= moveMaxSize - bodySize * 2){
            moveLocation = moveMaxSize - bodySize * 2 - 1;
        }
        return moveLocation;
    }

    /**
     * 重置速度
     */
    public void reSet() {
        this.moveStartSpeedX = 0;
        this.moveStartSpeedY = 0;
        this.moveEndSpeedX = 0;
        this.moveEndSpeedY = 0;
    }

    /**
     * 设置速度最大值
     * @param speedMaxX
     * @param speedMaxY
     */
    public void setSpeedMax(double speedMaxX, double speedMaxY){
        this.moveSpeedMaxX = speedMaxX;
        this.moveSpeedMaxY = speedMaxY;
    }

    /**
     * 获取加速度大小
     * @return
     */
    public double getAcceleration(){
        return acceleration;
    }

    /**
     * 设置加速度
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * 设置速度
     * @param x
     * @param y
     */
    public void setSpeed(boolean x, boolean y){
        this.moveStartSpeedX = this.moveEndSpeedX = x ? moveSpeedMaxX / 4 * 3 : -moveSpeedMaxX / 4 * 3;
        this.moveStartSpeedY = this.moveEndSpeedY = y ? moveSpeedMaxY / 4 * 3 : -moveSpeedMaxY / 4 * 3;
    }
}
