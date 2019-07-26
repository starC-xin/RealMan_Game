package realMan.mySprite;

import java.awt.*;

/**
 * @program: MyGame
 * @description: 敌人类
 * @auther: ZhaoXin
 * @create: 2018-12-25 08:40
 **/
public class MyEnemy extends MySprite{
    public MyEnemy(int centerX, int centerY, int[][] location, int radius, double acceleration, int maxSizeX, int maxSizeY, Color color) {
        super(centerX, centerY, location, radius, acceleration, maxSizeX, maxSizeY, color);
    }
}
