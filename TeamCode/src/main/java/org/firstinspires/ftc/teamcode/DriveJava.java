package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "JavaDrive")
public class DriveJava extends LinearOpMode {
    private DcMotor right_drive1;
    private DcMotor right_drive2;
    private DcMotor left_drive1;
    private DcMotor left_drive2;
    private DcMotor lift;
    private Servo claw1;
    private Servo claw2;
    double powerFactor = 0;
    int speedChangeValue = 1;
    boolean speedChangedUp = false;
    boolean speedChangedDown = false;
    private void initMotors() {
        right_drive1 = hardwareMap.get(DcMotor.class, "right_drive1");
        right_drive2 = hardwareMap.get(DcMotor.class, "right_drive2");
        left_drive1 = hardwareMap.get(DcMotor.class, "left_drive1");
        left_drive2 = hardwareMap.get(DcMotor.class, "left_drive2");
        lift = hardwareMap.get(DcMotor.class, "lift");
        claw1 = hardwareMap.get(Servo.class, "claw1");
        claw2 = hardwareMap.get(Servo.class, "claw2");
        // Put initialization blocks here
        right_drive1.setDirection(DcMotorSimple.Direction.REVERSE);
        right_drive2.setDirection(DcMotorSimple.Direction.REVERSE);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);
        powerFactor = 0.4;
        claw1.setPosition(0.25);
        claw2.setPosition(0.75);

    }

    @Override
    public void runOpMode() {
        initMotors();

        telemetry.addLine("Waiting for start");
        telemetry.update();

        /*
         * Wait for the user to press start on the Driver Station
         */
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            telemetry.update();
            while (opModeIsActive()) {

                callSpeedChange();
                lift.setPower((gamepad2.right_trigger - gamepad2.left_trigger) * 0.5);
                clawBot(gamepad2.right_bumper, gamepad2.left_bumper);
                moveBot( -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
                // Put loop blocks here.
            }
        }
    }

    /**
     * Describe this function...
     */
    void callSpeedChange()
    {
        if(this.gamepad1.right_trigger > 0 && !speedChangedUp)
        {
            speedChangedUp = true;
            speedChange(true);
        }else if (this.gamepad1.right_trigger <= 0 && speedChangedUp)
        {
            speedChangedUp = false;
        }
        if(this.gamepad1.left_trigger > 0 && !speedChangedDown)
        {
            speedChangedDown = true;
            speedChange(false);
        }else if (this.gamepad1.left_trigger <= 0 && speedChangedDown)
        {
            speedChangedDown = false;
        }
    }
    void speedChange(boolean faster)
    {

        if(faster && speedChangeValue < 2)
        {
            speedChangeValue++;
        }
        if(!faster && speedChangeValue > 0)
        {
            speedChangeValue--;
        }
        telemetry.addData("speed change", speedChangeValue);
        telemetry.update();
        switch (speedChangeValue)
        {
            case 0:
                powerFactor = 0.2 ;
            case 1:
                powerFactor = 0.4;
            case 2:
                powerFactor = 0.6;
            case 3:
                powerFactor = 0.8;
        }

    }
    private void moveBot( float vertical, float pivot, float horizontal) {

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        right_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive1.setPower(0);
        left_drive2.setPower(0);
    }

    private void clawBot(boolean right_bumper, boolean left_bumper)
    {
        if(right_bumper)
        {

            claw1.setPosition(0.15);
            claw2.setPosition(0.75);
        }
        else if (left_bumper)
        {
            claw1.setPosition(0.04);
            claw2.setPosition(0.85);
        }
    }
}
