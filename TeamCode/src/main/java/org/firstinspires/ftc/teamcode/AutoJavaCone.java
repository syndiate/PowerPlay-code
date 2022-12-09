package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import java.util.ArrayList;

public class AutoJavaCone extends LinearOpMode {

    public DcMotorEx right_drive1;
    public DcMotorEx right_drive2;
    public DcMotorEx left_drive1;
    public DcMotorEx left_drive2;
    public DcMotor lift;
    public Servo claw1;
    public Servo claw2;
    public volatile SleeveDetection.ParkingPosition pos;

    SleeveDetection sleeveDetection;
    OpenCvCamera camera;
    String webcamName = "Webcam 1";

    double powerFactor = 0.75;
    double startingPF = 0;
    boolean startPressed = false;
    boolean clawClosed = false;
    ArrayList < Double > position = new ArrayList < Double > ();
    double intCon = 19.8375;

    public void initMotors() {
        right_drive1 = hardwareMap.get(DcMotorEx.class, "right_drive1");
        right_drive2 = hardwareMap.get(DcMotorEx.class, "right_drive2");
        left_drive1 = hardwareMap.get(DcMotorEx.class, "left_drive1");
        left_drive2 = hardwareMap.get(DcMotorEx.class, "left_drive2");
        lift = hardwareMap.get(DcMotor.class, "lift");
        claw1 = hardwareMap.get(Servo.class, "claw1");
        claw2 = hardwareMap.get(Servo.class, "claw2");

        right_drive1.setDirection(DcMotorSimple.Direction.REVERSE);
        right_drive2.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        clawBot();
        // stop and reset encoder goes in init motors don't change
        // claw things here
        powerFactor = 0.6;
        startingPF = powerFactor;
        position.add(101.0);
        position.add(18.0);
    }

    public void initCamera() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
        sleeveDetection = new SleeveDetection();
        camera.setPipeline(sleeveDetection);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera error code:", errorCode);
                telemetry.update();
            }
        });
    }


    private void logPosition() {
        telemetry.addData("pos:", left_drive1.getCurrentPosition());
        telemetry.update();
    }

    private void setPosition(int fixedVal, double offset) {
        logPosition();
        position.set(fixedVal, position.get(fixedVal) + (((left_drive1.getCurrentPosition()) - offset) / intCon));
    }


    public void moveBotAlt(double x, double y, boolean straight) {

        double yDif = (y > position.get(1)) ? y - position.get(1) : position.get(1) - y;
        double preY = left_drive1.getCurrentPosition();

        if (straight) {

            double xDif = (x > position.get(0)) ? x - position.get(0) : position.get(0) - x;
            double preX = left_drive1.getCurrentPosition();
            double percent = Math.atan(xDif / yDif);
            double vertical = (y > position.get(1)) ? 1 : -1;
            double horizontal = (x > position.get(0)) ? 1 : -1;

            vertical *= Math.abs(percent);
            horizontal *= Math.abs(90 - percent);
            double distIN = Math.sqrt(((yDif * yDif) + (xDif * xDif)));
            int posNeg = (vertical >= 0) ? 1 : -1;

            right_drive1.setPower(powerFactor * ((vertical - horizontal)));
            right_drive2.setPower(powerFactor * (vertical + horizontal));
            left_drive1.setPower(powerFactor * (vertical + horizontal));
            left_drive2.setPower(powerFactor * ((vertical - horizontal)));

            int motorTics = left_drive1.getCurrentPosition() + (int)((distIN * intCon) * posNeg);

            if (horizontal >= 0) {
                if (posNeg == -1) {
                    while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                        setPosition(1, preY);
                    }
                } else {
                    while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                        setPosition(1, preY);
                    }
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    setPosition(0, preX);
                }
            }

            right_drive1.setPower(0);
            right_drive2.setPower(0);
            left_drive1.setPower(0);
            left_drive2.setPower(0);
            telemetry.update();

        } else {

            int posNegY = ((y) > position.get(1)) ? 1 : -1;

            right_drive1.setPower(powerFactor * (posNegY));
            right_drive2.setPower(powerFactor * (posNegY));
            left_drive1.setPower(powerFactor * (posNegY));
            left_drive2.setPower(powerFactor * (posNegY));

            int motorTics = left_drive1.getCurrentPosition() + (int)((yDif * intCon) * posNegY);

            if (posNegY == -1) {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    setPosition(1, preY);
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    setPosition(1, preY);
                }
            }

            setPosition(1, preY);
            double xDif = Math.abs(x - position.get(0));
            int posNegX = (x > position.get(0)) ? 1 : -1;
            double preX = left_drive1.getCurrentPosition();

            right_drive1.setPower(powerFactor * (-posNegX));
            right_drive2.setPower(powerFactor * (posNegX));
            left_drive1.setPower(powerFactor * (posNegX));
            left_drive2.setPower(powerFactor * (-posNegX));

            motorTics = left_drive1.getCurrentPosition() + (int)((xDif * intCon) * posNegX);

            if (posNegX >= 0) {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    setPosition(0, preX);
                }

            } else {
                while ((right_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    setPosition(0, preX);
                }
                setPosition(0, preX);
            }

            right_drive1.setPower(0);
            right_drive2.setPower(0);
            left_drive1.setPower(0);
            left_drive2.setPower(0);
            telemetry.update();

        }
        position.set(0, x);
        position.set(1, y);
    }

    public void moveBot(float distIN, float vertical, float pivot, float horizontal) {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;
        setPosition(0, (distIN * horizontal));
        setPosition(1, (distIN * horizontal));

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));

        if (horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (int)((distIN * intCon) * posNeg);
            if (posNeg == -1) {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    logPosition();
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    logPosition();
                }
            }
        } else {
            motorTics = right_drive1.getCurrentPosition() + (int)((distIN * intCon) * posNeg);
            while ((right_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                logPosition();
            }
        }

        right_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive1.setPower(0);
        left_drive2.setPower(0);
        telemetry.update();

    }

    public void clawBot() {
        // other claw function didn't work cause you would always need to call it to be closed
        clawClosed = !clawClosed;
        if (clawClosed) {
            claw1.setPosition(0.15);
            claw2.setPosition(0.75);
        } else {
            claw1.setPosition(0.04);
            claw2.setPosition(0.8);

        }
    }

    public void liftCone(int level) {
        int liftPos = 0;
        switch (level) {
            case 0: {
                liftPos = 800;
                break;
            }
            case 1: {
                liftPos = 1500;
                break;
            }
            case 2: {
                liftPos = 2650;
                break;
            }
        }
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setTargetPosition(liftPos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.5);
        while (lift.isBusy()) idle();
        lift.setPower(0);
    }

    public void runOpMode() {

    }

}