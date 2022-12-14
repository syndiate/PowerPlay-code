
/*

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.Iterator;

@Autonomous(name = "AutoJava", group = "Auto")
public class AutoJava extends LinearOpMode
{
    private DcMotor lift;
    private DcMotor right_drive1;
    private DcMotor right_drive2;
    private Servo deliver;
    private DcMotor duckL; //@@change
    private DcMotor left_drive1;
    private DcMotor left_drive2;
    OpenCvWebcam webcam;
    boolean startPressed = false;
    boolean startMovement = false;
    double powerFactor = 0;
    int liftPos;
    int LiftLevel;
    int move;
    private ArrayList<String> movements = new ArrayList<>();

    private void initMotors() {
        lift = hardwareMap.get(DcMotor.class, "lift");
        right_drive1 = hardwareMap.get(DcMotor.class, "right_drive1");
        right_drive2 = hardwareMap.get(DcMotor.class, "right_drive2");
        deliver = hardwareMap.get(Servo.class, "deliver");
        duckL = hardwareMap.get(DcMotor.class, "duckL"); //@@change
        left_drive1 = hardwareMap.get(DcMotor.class, "left_drive1");
        left_drive2 = hardwareMap.get(DcMotor.class, "left_drive2");

        // Put initialization blocks here.
        right_drive1.setDirection(DcMotorSimple.Direction.REVERSE);
        right_drive2.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        deliver.setDirection(Servo.Direction.REVERSE);
        deliver.setPosition(0);
        powerFactor = 0.5;
    }

    void updateLevel(int level) {
        telemetry.addData("level", level);
        telemetry.update();
        webcam.stopStreaming();

        telemetry.addData("complete", level);
        telemetry.update();
        startMovement = true;
    }

    @Override
    public void runOpMode()
    {
        initMotors();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        webcam.setPipeline(new SamplePipeline());
        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
            }
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();

        waitForStart();
        startPressed = true;

        while (opModeIsActive())
        {
            if (startMovement) {
                // Put run blocks here.
                moveBot(1550, 0, 0, 1); //@@change
                moveBot(750, -1, 0, 0);
                liftFreight(LiftLevel);
                dropFreight();
                unLiftFreight();
                // Land on freight station
                moveBot(2700, 0, 0, -1); //@@change
                // Land on freight station
                moveBot(600, 1, 0, 0);
                duck(0, 3500);
                moveBot(750, -1, 0, 0);
                int i =0;
                for (Iterator<String> move1 = movements.iterator(); move1.hasNext(); ) {
                    telemetry.addData("move" + ++i, move1.next());
                    telemetry.update();
                }
                sleep(5000);
                while (opModeIsActive()) {
                }
            }
        }
    }

    private void unLiftFreight() {
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        liftPos = 10;
        lift.setTargetPosition(liftPos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.5);
        while (opModeIsActive() && lift.isBusy()) {
            idle();
        }
        lift.setPower(0);
        if (LiftLevel == 0) {
            moveBot(505, -1, 0, 0);
        } else if (LiftLevel == 1) {
            moveBot(330, -1, 0, 0);
        }
    }

    private void liftFreight(int level) {
        liftPos = 1074;
        if (level == 0) {
            moveBot(505, 1, 0, 0);
        } else if (level == 1) {
            moveBot(330, 1, 0, 0);
        }
        sleep(500);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setTargetPosition(liftPos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.5);
        while (lift.isBusy()) {
            idle();
        }
        lift.setPower(0);
    }


    private void dropFreight() {
        sleep(500);
        deliver.setPosition(0.2);
        sleep(1000);
        deliver.setPosition(0.3);
        sleep(1500);
        deliver.setPosition(0);
        sleep(600);
    }

    private void duck(int direction, int timeMili) {
        duckL.setPower(0.5); //@@change
        sleep(timeMili);
        duckL.setPower(0); //@@change
        sleep(500);
    }

    private void moveBot(int timeMili, int vertical, int pivot, int horizontal) {
        int enc1  = left_drive1.getCurrentPosition();
        timeMili = (int) Math.round(GetPowerChange() * timeMili);
        telemetry.addData("power change", GetPowerChange());
        telemetry.addData("power factor", timeMili);
        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        sleep(timeMili);
        right_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive1.setPower(0);
        left_drive2.setPower(0);
        int enc2  = left_drive1.getCurrentPosition();
        movements.add(""+enc1+"->"+enc2+"="+(enc2-enc1));
        telemetry.addData("move" + ++move, ""+enc1+"->"+enc2+"="+(enc2-enc1));
        telemetry.update();
    }

    private double GetPowerChange() {
        return 1 + 13.3 / 13.4;
    }

    class SamplePipeline extends OpenCvPipeline
    {
        boolean viewportPaused;
        int count = 0;

        @Override
        public Mat processFrame(Mat input)
        {
            Size s = input.size();
            int mid1 = (int)(s.width/2);
            int mid2 = (int)(s.width);

            Rect left = new Rect(new Point(0, 0), new Point(mid1, s.height));
            Rect mid = new Rect(new Point(mid1,0),new Point(mid2, s.height));
//            Rect right = new Rect(new Point(mid2,0),new Point(s.width, s.height));

            Mat mat = new Mat();
            Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGBA2RGB);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2HSV);
            Scalar lowerBound = new Scalar(60 - 15, 100, 50);
            Scalar upperBound = new Scalar(60 + 15, 255, 255);
            Core.inRange(mat, lowerBound, upperBound, mat);
//            // -- DIVIDE --
            Mat leftMat = mat.submat(left);
            Mat midMat = mat.submat(mid);
//            Mat rightMat = mat.submat(right);

//            // -- AVERAGE --
            double leftValue = Math.round(Core.mean(leftMat).val[0]);
            double midValue = Math.round(Core.mean(midMat).val[0] );
//            double rightValue = Math.round(Core.mean(rightMat).val[0] );
            leftMat.release();
            midMat.release();
//            rightMat.release();
            mat.release();

            int position = 2;

            if (midValue < leftValue)
                position = 0;
            else
                position = 1;

            telemetry.addData("position", position);
            telemetry.update();
            if (startPressed)
                count++;
            if (count == 1) updateLevel(position);

            return input;
        }

        @Override
        public void onViewportTapped()
        {
            viewportPaused = !viewportPaused;

            if(viewportPaused)
            {
                webcam.pauseViewport();
            }
            else
            {
                webcam.resumeViewport();
            }
        }
    }
}
*/
