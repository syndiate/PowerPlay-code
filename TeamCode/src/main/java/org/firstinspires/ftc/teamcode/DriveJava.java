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
        powerFactor = 0.75;
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
            while (opModeIsActive()) {
                lift.setPower((gamepad2.right_trigger - gamepad2.left_trigger) * 0.5);
                clawBot(gamepad2.right_bumper, gamepad2.left_bumper);
                moveBot(0, gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_stick_y);
                // Put loop blocks here.
                telemetry.update();
            }
        }
    }

    /**
     * Describe this function...
     */
    private void moveBot(int timeMili, float vertical, float pivot, float horizontal) {

        right_drive1.setPower(powerFactor * (-pivot + (vertical - horizontal)));
        right_drive2.setPower(powerFactor * (-pivot + vertical + horizontal));
        left_drive1.setPower(powerFactor * (pivot + vertical + horizontal));
        left_drive2.setPower(powerFactor * (pivot + (vertical - horizontal)));
        sleep(timeMili);
        right_drive1.setPower(0);
        right_drive2.setPower(0);
        left_drive1.setPower(0);
        left_drive2.setPower(0);
    }

    private void clawBot(boolean right_bumper, boolean left_bumper)
    {
        if(right_bumper)
        {
            claw1.setPosition(1);
            claw2.setPosition(-1);
        }
        else if (left_bumper)
        {
            claw1.setPosition(0);
            claw2.setPosition(0);
        }
    }
}
