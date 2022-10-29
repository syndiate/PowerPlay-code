// FINISH UP BOX CLASS!!!!!!

package org.firstinspires.ftc.teamcode.tensorUtilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class Box {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    /*
    public static Bitmap drawBoxes(Bitmap image, List<Box> boxes) {
        graph.setColor(Color.green);

        for (Box box : boxes) {
            graph.drawRect(box.x, box.y, box.width, box.height);
        }

        graph.dispose();
        return image;
    }

    public static Bitmap drawBox(Bitmap image, int x, int y, int width, int height) {
        Canvas graph = image.prepareToDraw();
        Graphics2D graph = image.createGraphics();
        graph.setColor(Color.GREEN);
//        graph.fill(new Rectangle(x, y, width, height));
        graph.drawRect(x, y, width, height);
//        graph.setStroke();
        graph.dispose();
        return image;
    }*/
}
