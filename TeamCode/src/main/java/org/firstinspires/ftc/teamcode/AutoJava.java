package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.slf4j.event.Level;

import java.util.ArrayList;

@Autonomous(name = "AutoJava", group = "Auto")
public class AutoJava extends LinearOpMode {
    private DcMotorEx right_drive1;
    private DcMotorEx right_drive2;
    private DcMotorEx left_drive1;
    private DcMotorEx left_drive2;
    private DcMotor lift;
    private Servo claw1;
    private Servo claw2;
    private ArrayList<String> movements = new ArrayList<>();
    private volatile SleeveDetection.ParkingPosition pos;
    
    SleeveDetection sleeveDetection;
    OpenCvCamera camera;
    String webcamName = "Webcam 1";


    double powerFactor = 0;
    double speed = 105;
    boolean startMovement = false;
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
        powerFactor = 1;
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

                    moveBot(30, 1, 0, 0);
                    //moveBot(13, 0, 0, -1);
                    switch (pos) {
                        case LEFT: {
                            moveBot(24, 0, 0, -1);
                            break;
                        }
                        case RIGHT: {
                            moveBot(24, 0, 0, 1);
                            break;
                        }
                    }
                    stop = true;


                    //autonomous code here
                }

        }


    }


    private void moveBot(int distIN, float vertical, float pivot, float horizontal)
    {

        // 23 motor tics = 1 IN
        int motorTics;
        if(horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (distIN * 23);
        }else
        {
            motorTics = right_drive1.getCurrentPosition() + (distIN * 23);
        }

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        if(horizontal >= 0) {
           while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive())
           {
               telemetry.addData("pos:", left_drive1.getCurrentPosition());
               telemetry.update();
           }
        }else {
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

    private void clawBot()
    {
        // other claw function didn't work cause you would always need to call it to be closed
        clawClosed = !clawClosed;
        if (clawClosed)
        {
            claw1.setPosition(1);
            claw2.setPosition(-1);
        } else
        {
            claw1.setPosition(0);
            claw2.setPosition(0);
        }

    }



    private void liftFreight(int level) {
        int liftPos = 10;
        if(lift.getCurrentPosition() < 20)
        {
            switch (level)
            {

                case 0: {
                    liftPos = 1500;
                }
                case 1: {
                    liftPos = 2500;
                }
                case 2: {
                    liftPos = 3040;
                }

            }
        }
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setTargetPosition(liftPos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.5);
        while (lift.isBusy()) {
            idle();
        }
        lift.setPower(0);
    }


}
