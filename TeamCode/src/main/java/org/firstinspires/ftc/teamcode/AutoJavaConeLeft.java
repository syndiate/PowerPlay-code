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
                telemetry.addData("clawClosed", clawClosed);
                telemetry.update();
                clawBot();
                powerFactor = 0.7;
                startingPF = powerFactor;
                sleep(1000);
                liftCone(-1);
                moveBot(2, 0, 0, 1);
                moveBot(35, 1, 0, 0);
                moveBot(5, 1, 0, 0);
                sleep(500);
                moveBot(1.5f, -1, 0, 0);
                //moveBot(4, -1, 0, 0);
                sleep(1000);
                moveBot(56.5f, 0, 0, 1);
                liftCone(3);
                powerFactor = 0.25;
                moveBot(5, 1, 0, 0);
                sleep(1000);
//                liftCone(1);
                sleep(500);
                clawBot();
                moveBot(2, -1, 0, 0);
                liftCone(-1);
//                liftCone(-1);
                powerFactor = startingPF;
                switch (pos) {
                    case RIGHT: {
 //                       moveBot(1, 1, 0, 0);
                        moveBot(10, 0, 0, -1);
                        break;
                    }
                    case CENTER: {
                        moveBot(9, 0, 0, -1);
                        moveBot(1, -1, 0, 0);
                        moveBot(43, 0, 0, -1);
                        break;
                    }
                    case LEFT: {
                        moveBot(9, 0, 0, -1);
                        moveBot(1, -1, 0, 0);
                        moveBot(86, 0, 0, -1);
                        break;
                    }
                }
                moveBot(1, 1, 0, 0);
                stop = true;
            }

        }


    }


}
