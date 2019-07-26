package realMan.myUtil;

import realMan.mySprite.MyPoint;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @program: MyGame
 * @description: 绘制多边形
 * @auther: ZhaoXin
 * @create: 2018-12-24 16:18
 **/
public class MyUtil {

    /**
     * 根据已有参数绘制多边形
     * @param myPoint 点类
     * @param targetX 目标点 X 坐标
     * @param targetY 目标点 Y 坐标
     */
    public static Polygon drawTankAngle(MyPoint myPoint, int targetX, int targetY) {
//        初始化数据
        int[] xPoints = myPoint.getLocation()[0];
        int[] yPoints = myPoint.getLocation()[1];
        int centerX = myPoint.getCenterX();
        int centerY = myPoint.getCenterY();
        double angle = adjustmentDirection(targetX, targetY, centerX, centerY);
        int radius = myPoint.getRadius();

//        保存计算后的坐标
        int[][] location = new int[4][xPoints.length];

//        第一个坐标用于指向鼠标
        angle = getAngleByPoint(xPoints[0], yPoints[0]) + angle - Math.PI;
        for(int j = 0; j < location.length / 2; j++){
            for (int i = 0; i < xPoints.length; i++) {
                double x1 = xPoints[i];
                double y1 = yPoints[i];
//                d = 预计角度 + 旋转角度
                double d = getAngleByPoint(x1, y1) + angle;
//                计算实际坐标，公式参考三角函数
//                视图碰撞模型相对坐标
                location[j * 2][i] = (int) (radius * Math.cos(d));
                location[j * 2 + 1][i] = (int) (radius * Math.sin(d));
//                视图、碰撞模型绝对坐标
                location[j * 2][i] += centerX;
                location[j * 2 + 1][i] += centerY;
            }
            radius += 11;
        }
//        设置视图与碰撞模型
        myPoint.setViewShape(new Polygon(location[0], location[1], location[0].length));
        myPoint.setTouchShape(new Polygon(location[2], location[3], location[0].length));
        return myPoint.getView();
    }

    /**
     * 计算弧度
     * @param x
     * @param y
     * @return
     */
    private static double getAngleByPoint(double x, double y) {
        return y > 0 ?
                Math.acos(x / Math.sqrt(x * x + y * y)) :
                2 * Math.PI - Math.acos(x / Math.sqrt(x * x + y * y));
    }

    /**
     * 调整角度（转向）
     * @param targetX
     * @param targetY
     * @return 弧度
     */
    public static double adjustmentDirection(int targetX, int targetY, int x, int y) {
        return getAngleByPoint(targetX - x, targetY - y);
    }

    /**
     * 获取自定义外部字体文件
     * @param size
     * @return
     */
    public static Font getFont(int size){
        String fontName = "迷你简卡通.TTF";
        InputStream is = null;
        BufferedInputStream bis = null;
        Font font = null;
        try{
            is = MyUtil.class.getResourceAsStream(fontName);
            bis = new BufferedInputStream(is);
            font = Font.createFont(Font.TRUETYPE_FONT, bis);
            font = font.deriveFont(Font.BOLD, size);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
//            关闭输入流
            try{
                if(bis != null){
                    bis.close();
                }
                if(is != null){
                    is.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return font;
    }
}