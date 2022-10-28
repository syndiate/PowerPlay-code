package org.firstinspires.ftc.teamcode.tensorUtilities;


public class RectProperties {
    private final float x, y, width, height;

    public RectProperties(RectProperties rectProperties) {
        this.x = rectProperties.getX();
        this.y = rectProperties.getY();
        this.width = rectProperties.getWidth();
        this.height = rectProperties.getHeight();
    }

    public RectProperties(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}