package realMan;

import realMan.myGame.MyGame;

import javax.swing.*;
import java.awt.*;

/**
 * 是男人就坚持一百秒！
 * @program: MyGame
 * @description: 程序入口
 * @auther: ZhaoXin
 * @create: 2018-12-23 10:40
 **/
public class RealManMain {

    public static void main(String[] args){
//        创建主窗体
        JFrame window = new JFrame("是男人就坚持一百秒");
//        游戏本体
        MyGame game = new MyGame();
//        设置游戏内容和窗体
        setJFrame(window, game);

//        游戏开始
        game.start();
    }

    private static void setJFrame(JFrame window, MyGame game) {
//        设置游戏大小
        window.setSize(MyGame.SCREEN_WIDTH, MyGame.SCREEN_HEIGHT);
//        设置布局模式
        window.setLayout(null);
//        设置窗体大小不可修改
        window.setResizable(false);
//        设置背景颜色
//        window.getContentPane().setBackground(new Color(0x3C3F41));
//        设置窗体一直在前，不会被覆盖
//        window.setAlwaysOnTop(true);
//        设置窗体关闭选项
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        设置窗体显示位置，此处为居中
        window.setLocationRelativeTo(null);
//        添加游戏画面
        window.add(game);
//        设置窗体可见
        window.setVisible(true);
//        设置窗体最大化
//        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        game.setFocusable(true);
    }
}
