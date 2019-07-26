package realMan.myConf;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * @program: MyGame
 * @description: 参数配置类
 * @auther: ZhaoXin
 * @create: 2018-12-24 19:56
 **/
public class MyParameter {
    public static final int LOCATION = 0;
    public static final int CENTER_X = 10;
    public static final int CENTER_Y = 20;
    public static final int POINT_X = 1;
    public static final int POINT_Y = 2;
    public static final int RADIUS = 3;
    public static final int ENEMY = 4;
    public static final int BULLET = 5;
    public static final int HERO = 6;
    public static final int PROP = 7;
    public static final int PROP_LINE = 70;
    public static final int PROP_TRACK = 71;
    public static final int SUPER_ME = 10;

    public static final Random ran = new Random();

    /**
     * 设置单例
     */
    private static MyParameter myParameter = new MyParameter();

    /**
     * 颜色参数
     */
    private List<Color> spriteColors;
    private List<Color> bulletColors;
    /**
     * 中心坐标参数
     */
    private int[][] location;
    /**
     * 坐标
     */
    private int[][] pointX;
    private int[][] pointY;
    /**
     * 半径
     */
    private int[] radius;

    /**
     * 敌人形状总数
     */
    int enemyCount = 6;

    /**
     * 道具形状总数
     */
    int propCount = 3;

    /**
     * 初始化单例
     */
    private MyParameter(){
        spriteColors = new ArrayList<Color>();
        bulletColors = new ArrayList<Color>();
        initSpriteColors();
        initBulletColor();
        initBasicAttribute();
    }

    /**
     * 初始化英雄/敌人颜色
     */
    private void initSpriteColors() {
        spriteColors.add(new Color(0xBFFFB900, true));
        spriteColors.add(new Color(0xBFB146C2, true));
        spriteColors.add(new Color(0xBFF7630C, true));
        spriteColors.add(new Color(0xBF00B7C3, true));
        spriteColors.add(new Color(0xBF0078D7, true));
        spriteColors.add(new Color(0xBFEF6950, true));
        spriteColors.add(new Color(0xBFD13438, true));//准心颜色
        spriteColors.add(new Color(0xBFFF4343, true));
        spriteColors.add(new Color(0xBF00CC6A, true));
        spriteColors.add(new Color(0xBFE81123, true));
        spriteColors.add(new Color(0xBFE3008C, true));
        spriteColors.add(new Color(0xBF9A0089, true));
        spriteColors.add(new Color(0xBF744DA9, true));
    }

    /**
     * 初始化子弹颜色
     */
    private void initBulletColor(){
        bulletColors.add(new Color(0x7FF4F4E8, true));
        bulletColors.add(new Color(0xBFF6FF05, true));
        bulletColors.add(new Color(0xFF6FFC));
    }

    /**
     * 初始化基本属性
     */
    private void initBasicAttribute() {
        location = new int[][] {
                {200, 350, 550, 850, 1200, 1350, 810},
                {450, 650, 800, 780, 680, 500, 300}
        };
        pointX = new int[][]{
//                敌人
                {0, -2, 2},
                {-1, 1, 1, -1},
                {0, 2, 1, -1, -2},
                {0, -2, -2, 0, 2, 2},
                {0, -1, -1, 0, 1, 1},
                {0, -1, 1, -1, 1},
//                英雄坐标
                {0, -3, 2, 3, 3, 0, -3, -3, -2, 3},
//                子弹坐标
                {0, 1, -1, 1, 0, -1, 1, -1},
//                道具坐标，依次为直线9，追踪7
                {0, 1, 0, -1, 0, 1, 1, -1, -1},
                {0, 1, 0, -1, 1, 2, -2},
//                无敌道具
                {0, 3, 1, 1, -1, -1, -3}
        };
        pointY = new int[][]{
                {2, -2, -2},
                {1, 1, -1, -1},
                {2, 0, -1, -1, 0},
                {2, 1, -1, -2, -1, 1},
                {2, 1, -1, -2, -1, 1},
                {2, -3, 0, 0, -3},
                {4, -2, 3, 2, -2, -3, -2, 2, 3, -2},
                {1, -1, 0, 1, -1, 1, 0, -1},
                {1, 0, -1, 0, 1, 1, -1, -1, 1},
                {1, 0, -1, 0, 0, -1, -1},
                {10, 0, 1, -1, -1, 1, 0}
        };
        radius = new int[]{90, 105, 100, 80, 95, 110, 50};
    }

    /**
     * 获取单例
     * @return
     */
    public static MyParameter createParameterConfiguration() {
        return myParameter;
    }

    /**
     * 初始化敌人/道具颜色
     * @return
     */
    public List<Color> getSpriteColors(){
        return spriteColors;
    }

    /**
     * 初始化子弹颜色
     * @return
     */
    public List<Color> getBulletColors(){
        return bulletColors;
    }

    /**
     * 获取参数，双数组
     * @param mode 模式
     * @return
     */
    public int[][] getArrayDouble(int mode){
        int[][] temp = null;
        switch(mode){
            case POINT_X:
                temp = pointX;
                break;
            case POINT_Y:
                temp = pointY;
                break;
        }
        return temp;
    }

    /**
     * 获取参数，单数组，无下标
     * @param mode 模式
     * @return
     */
    public int[] getArrayFloat(int mode){
        int[] temp = null;
        switch(mode){
            case CENTER_X:
                temp = location[0];
                break;
            case CENTER_Y:
                temp = location[1];
                break;
            case RADIUS:
                temp = radius;
                break;
        }
        return temp;
    }

    /**
     * 用于获取形状相对点集坐标
     * @param modeOrIndex
     * @param type 是否按照下标获取
     * @return
     */
    public int[][] getShape(int modeOrIndex, boolean type){
        int[][] temp = null;
        if(type){
            temp = new int[][] {pointX[modeOrIndex], pointY[modeOrIndex]};
        }else{
            int ranIndex;
            switch(modeOrIndex){
                case ENEMY:
                    ranIndex = ran.nextInt(enemyCount);
                    temp = new int[][]{pointX[ranIndex], pointY[ranIndex]};
                    break;
                case BULLET:
                    temp = new int[][]{pointX[7], pointY[7]};
                    break;
                case HERO:
                    temp = new int[][]{pointX[6], pointY[6]};
                    break;
                case PROP:
//                    获取道具，95子弹/5无敌
                    int ranNum = ran.nextInt(100);
                    int index = ranNum < 35 ? 2 : ran.nextInt(2);
                    temp = new int[][]{pointX[8 + index], pointY[8 + index]};
                    break;
//                case SUPER_ME:
//                    temp = new int[][]{pointX[10], pointY[10]};
//                    break;
//            case PROP_LINE:
//                temp = new int[][]{pointX[8], pointY[8]};
//                break;
//            case PROP_TRACK:
//                temp = new int[][]{pointX[9], pointY[9]};
//                break;
            }
        }
        return temp;
    }
}
