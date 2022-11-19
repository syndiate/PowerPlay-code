package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

;

@Autonomous(name = "AutoJavaConeflipped", group = "Auto")
public class AutoJavaConeFlipped extends LinearOpMode {
    private DcMotorEx right_drive1;
    private DcMotorEx right_drive2;
    private DcMotorEx left_drive1;
    private DcMotorEx left_drive2;
    private DcMotor lift;
    private Servo claw1;
    private Servo claw2;
    private volatile SleeveDetection.ParkingPosition pos;

    SleeveDetection sleeveDetection;
    OpenCvCamera camera;
    String webcamName = "Webcam 1";


    double powerFactor = 0.75;
    double startingPF = 0;
    boolean startPressed = false;
    boolean clawClosed = false;
    //we may need some additional variables here ^^

    private void initMotors() {
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
    }


    @Override
    public void runOpMode()
    {
        initMotors();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
        sleeveDetection = new SleeveDetection();
        camera.setPipeline(sleeveDetection);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(320,240, OpenCvCameraRotation.SIDEWAYS_LEFT);
            }

            @Override
            public void onError(int errorCode) {}
        });

        while (!isStarted()) {

            telemetry.addData("YCM: ", sleeveDetection.getYelPercent() + " " +
                    sleeveDetection.getCyaPercent() + " " + sleeveDetection.getMagPercent());
            telemetry.addData("ROTATION1: ", sleeveDetection.getPosition());
            telemetry.update();
            pos = sleeveDetection.getPosition();
        }


        telemetry.addLine("Waiting for start");
        telemetry.update();
        waitForStart();

        startPressed = true;
        boolean stop = false;
        camera.closeCameraDevice();
        while (opModeIsActive())
        {
            if(!stop) {
                clawBot();
                liftCone(1);
                moveBot(12, 1, 0, 0);
                liftCone(1);
                moveBot(9, 1, 0, 0);
                liftCone(1);
                moveBot(13, 0, 0, 1);
                liftCone(2);
                powerFactor = 0.25;
                moveBot(1, 1, 0, 0);
                liftCone(2);
                moveBot(1, 1, 0, 0);
                sleep(1000);
                clawBot();
                moveBot(2, -1, 0, 0);
                liftCone(-1);
                powerFactor = startingPF;
                switch (pos) {
                    case LEFT: {
                        moveBot(11, 0, 0, -1);
                        break;
                    }
                    case CENTER:
                    {
                        moveBot(13, 0, 0, 1);
                        break;
                    }
                    case RIGHT: {
                        moveBot(43, 0, 0, 1);
                        break;
                    }
                }

                stop = true;


                //autonomous code here
            }

        }


    }


    private void moveBot(float distIN, float vertical, float pivot, float horizontal)
    {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;
        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        if(horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (int)((distIN * 23)* posNeg);
            if(posNeg == -1)
            {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    telemetry.addData("pos:", left_drive1.getCurrentPosition());
                    telemetry.update();
                }
            }else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    telemetry.addData("pos:", left_drive1.getCurrentPosition());
                    telemetry.update();
                }
            }
        }else {
            motorTics = right_drive1.getCurrentPosition() + (int)((distIN * 23)* posNeg);
            while ((right_drive1.getCurrentPosition() < motorTics) && opModeIsActive())
            {
                telemetry.addData("pos:", left_drive1.getCurrentPosition());
                telemetry.update();
            }
        }
        right_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive1.setPower(0);
        left_drive2.setPower(0);
        telemetry.update();

    }

    private void clawBot() {
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


    private void liftCone(int level) {
        int liftPos = 0;
        switch (level)
        {
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
        while (lift.isBusy()) {
            idle();
        }
        lift.setPower(0);
    }


}
