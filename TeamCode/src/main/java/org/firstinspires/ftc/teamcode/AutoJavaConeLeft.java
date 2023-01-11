package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(name = "AutoJavaConeLeft", group = "Auto")
public class AutoJavaConeLeft extends AutoJavaCone {

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

        /*
         moveBot(95, 45, false, false);
                telemetry.addData("x:", position.get(0));
                telemetry.addData("y:", position.get(1));
                telemetry.update();
                sleep(2000);
                moveBot(101, 18, false, true);
                telemetry.addData("x:", position.get(0));
                telemetry.addData("y:", position.get(1));
                telemetry.update();
                */
        clawBot();
        //liftCone(0);
        moveBot(32, 1, 0, 0);
        moveBot(4, -1, 0, 0);
        sleep(1000);
        moveBot(17, 0, 0, 1);
        //liftCone(2);
        powerFactor = 0.25;
        //moveBot(5, 1, 0, 0);
        sleep(1000);
        //liftCone(1);
        sleep(500);
        //clawBot();
        //moveBot(2, -1, 0, 0);
        //liftCone(-1);
        powerFactor = startingPF;
        switch (pos) {
            case RIGHT: {
                moveBot(1, 1, 0, 0);
                moveBot(11, 0, 0, 1);
                break;
            }
            case CENTER: {
                moveBot(13, 0, 0, -1);
                break;
            }
            case LEFT: {
                moveBot(43, 0, 0, -1);
                break;
            }
        }



    }


}
