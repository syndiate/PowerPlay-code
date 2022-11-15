

package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
        import org.opencv.core.CvType;
        import org.opencv.core.Mat;
        import org.opencv.core.Point;
        import org.opencv.core.Rect;
        import org.opencv.core.Scalar;
        import org.opencv.core.Size;
        import org.opencv.imgproc.Imgproc;
        import org.openftc.easyopencv.OpenCvPipeline;

public class SleeveDetection extends OpenCvPipeline {
    /*
    YELLOW  = Parking Left
    CYAN    = Parking Middle
    MAGENTA = Parking Right
     */

    public enum ParkingPosition {
        LEFT,
        CENTER,
        RIGHT
    }

    // TOPLEFT anchor point for the bounding box
    private static Point SLEEVE_TOPLEFT_ANCHOR_POINT = new Point(145, 168);

    // Width and height for the bounding box
    public static int REGION_WIDTH = 30;
    public static int REGION_HEIGHT = 50;

    // Lower and upper boundaries for colors
    private static final Scalar
            lower_red_bounds  = new Scalar(114, 20, 36, 255),
            upper_red_bounds  = new Scalar(255, 13, 53, 255),
            //lower_cyan_bounds    = new Scalar(57, 87, 140, 255),
            //upper_cyan_bounds    = new Scalar(172, 234, 255, 255),
            lower_blue_bounds = new Scalar(13, 20, 80, 255),
            upper_blue_bounds = new Scalar(46, 137, 255, 255);

    // Color definitions
    private final Scalar
            RED  = new Scalar(255, 0, 0),
            BLUE    = new Scalar(0, 0, 255);

    // Percent and mat definitions
    private double redPercent, bluePercent;
    private Mat redMat = new Mat(REGION_WIDTH, REGION_HEIGHT, CvType.CV_16UC4), blueMat = new Mat(REGION_WIDTH, REGION_HEIGHT, CvType.CV_16UC4), blurredMat = new Mat(), kernel = new Mat();

    // Anchor point definitions
    Point sleeve_pointA = new Point(
            SLEEVE_TOPLEFT_ANCHOR_POINT.x,
            SLEEVE_TOPLEFT_ANCHOR_POINT.y);
    Point sleeve_pointB = new Point(
            SLEEVE_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
            SLEEVE_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);

    // Running variable storing the parking position
    private volatile ParkingPosition position = ParkingPosition.LEFT;

    @Override
    public Mat processFrame(Mat input) {
        // Noise reduction
        Imgproc.blur(input, blurredMat, new Size(5, 5));
        blurredMat = blurredMat.submat(new Rect(sleeve_pointA, sleeve_pointB));

        // Apply Morphology
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(blurredMat, blurredMat, Imgproc.MORPH_CLOSE, kernel);

        // Gets channels from given source mat
        Core.inRange(blurredMat, lower_red_bounds, upper_red_bounds, redMat);
        Core.inRange(blurredMat, lower_blue_bounds, upper_blue_bounds, blueMat);

        //Imgproc.cvtColor(input.rgba());
        // Gets color specific values

        redPercent = Core.countNonZero(redMat);
        bluePercent = Core.countNonZero(blueMat);

        // Calculates the highest amount of pixels being covered on each side
        double maxPercent = Math.max(redPercent, bluePercent);

        // Checks all percentages, will highlight bounding box in camera preview
        // based on what color is being detected
        if (maxPercent == redPercent) {
            position = ParkingPosition.LEFT;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    RED,
                    2
            );
        } else if (maxPercent == bluePercent) {
            position = ParkingPosition.CENTER;
            Imgproc.rectangle(
                    input,
                    sleeve_pointA,
                    sleeve_pointB,
                    BLUE,
                    2
            );
        }

        // Memory cleanup
        blurredMat.release();
        redMat.release();
        blueMat.release();
        kernel.release();

        return input;
    }

    // Returns an enum being the current position where the robot will park
    public ParkingPosition getPosition() {
        return position;
    }

    public double getRedPercent() {
        return redPercent;
    }

    public double getBluePercent() {
        return bluePercent;
    }

}
