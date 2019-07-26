package realMan.mySprite;

/**
 * 目标接口，跟踪逻辑功能实现
 */
public interface MyTarget {
    /**
     * 设置目标
     * @param target
     */
    void setTarget(MySprite target);

    /**
     * 跟踪逻辑实现
     */
    void trackTarget();
}
