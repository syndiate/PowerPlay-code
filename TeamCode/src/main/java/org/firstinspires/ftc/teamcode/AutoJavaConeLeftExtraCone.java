package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutoJavaConeLeftExtraCone", group = "Auto")
public class AutoJavaConeLeftExtraCone extends AutoJavaCone {

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

        powerFactor = 1;
        startingPF = powerFactor;
//        liftTest();

        moveBot(35, 0, 0, 1);
        sleep(500);
        moveBot(72, 1, 0, 0);
        //liftCone(3);
        moveBot(19.5f, 0, 0, -1);


        //liftCone(3);
        //sleep(1500);
        //liftCone(1);
        //clawBot();
                /*
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
                moveBot(1, 1, 0, 0);*/


    }


}
