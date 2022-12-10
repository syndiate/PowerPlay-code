package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoJavaConeLeft", group = "Auto")
public class AutoJavaConeLeft extends AutoJavaCone {

    @Override
    public void runOpMode()
    {
        initMotors();
        initCamera();

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
            if (!stop) {
                clawBot();
                liftCone(0);
                moveBot(26, 1, 0, 0);
                moveBot(17, 0, 0, 1);
                liftCone(2);
                powerFactor = 0.25;
                moveBot(3, 1, 0, 0);
                sleep(1000);
                liftCone(1);
                sleep(500);
                clawBot();
                moveBot(2, -1, 0, 0);
                liftCone(-1);
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

                stop = true;
            }

        }


    }


}
