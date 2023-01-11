package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(name = "AutoJavaConeRight", group = "Auto")
public class AutoJavaConeRight extends AutoJavaCone {

    @Override
    public void runOpMode()
    {
        initMotors();
        initCamera();

        while (!isStarted()) updateParkingPos();
        camera.closeCameraDevice();


        telemetry.addLine("Waiting for start");
        telemetry.update();
        waitForStart();

        startPressed = true;
        if (!opModeIsActive()) return;


        //clawBot();
        //liftCone(0);
        moveBot(32, 1, 0, 0);
        sleep(1000);
        moveBot(2, -1, 0, 0);
        sleep(1000);
        moveBot(13, 0, 0, -1);
        //liftCone(2);
        powerFactor = 0.25;
        //moveBot(5, 1, 0, 0);
        sleep(1000);
        //liftCone(1);
        sleep(500);
        //clawBot();
        //moveBot(3, -1, 0, 0);
        //liftCone(-1);
        powerFactor = startingPF;
        switch (pos) {
            case LEFT: {
                moveBot(1, 1, 0, 0);
                moveBot(11, 0, 0, -1);
                break;
            }
            case CENTER: {
                moveBot(13, 0, 0, 1);
                break;
            }
            case RIGHT: {
                moveBot(43, 0, 0, 1);
                break;
            }
        }



    }


}