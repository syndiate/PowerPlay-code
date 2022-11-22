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

@Autonomous(name = "AutoJavaCone", group = "Auto")
@Disabled
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
    }



    public void initCamera() {
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
            public void onError(int errorCode) {
                telemetry.addData("Camera error code:", errorCode);
                telemetry.update();
            }
        });
    }



    public void moveBot(float distIN, float vertical, float pivot, float horizontal)
    {

        // 23 motor tics = 1 IN
        int motorTics;
        int posNeg = (vertical >= 0) ? 1 : -1;
        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        if (horizontal >= 0) {
            motorTics = left_drive1.getCurrentPosition() + (int)((distIN * 23)* posNeg);
            if (posNeg == -1)
            {
                while ((left_drive1.getCurrentPosition() > motorTics) && opModeIsActive()) {
                    telemetry.addData("pos:", left_drive1.getCurrentPosition());
                    telemetry.update();
                }
            } else {
                while ((left_drive1.getCurrentPosition() < motorTics) && opModeIsActive()) {
                    telemetry.addData("pos:", left_drive1.getCurrentPosition());
                    telemetry.update();
                }
            }
        } else {
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


    public void runOpMode() {

    }

}
