package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * This is a basic TeleOp program for a goBILDA Starter Bot with Mecanum wheels.
 * 
 * Hardware Configuration:
 * - 4 Drive Motors: "leftFront", "rightFront", "leftBack", "rightBack"
 * - 1 Intake Motor: "intakeMotor"
 * - 2 Servos: "servo1", "servo2"
 * 
 * Controls:
 * - Left Stick: Move robot (Forward, Backward, Strafe Left/Right)
 * - Right Stick: Rotate robot (Turn Left/Right)
 * - Right Trigger: Intake (Spin motor forward)
 * - Left Trigger: Outtake (Spin motor backward)
 * - A Button: Move servos to position 1
 * - B Button: Move servos to position 0
 */

@TeleOp(name="Starter Bot TeleOp", group="Linear OpMode")
public class StarterBotTeleOp extends LinearOpMode {

    // Declare OpMode members
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightBack = null;
   // private DcMotor intakeMotor = null;
    //private Servo servo1 = null;
    //private Servo servo2 = null;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables from the hardware map.
        // The strings here must match the names you set on the Robot Configuration in the Driver Station.
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");
        //intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
       // servo1 = hardwareMap.get(Servo.class, "servo1");
        //servo2 = hardwareMap.get(Servo.class, "servo2");

        // Direction Mapping:
        // Most robots need the left side motors reversed to drive forward.
        // goBILDA mecanum wheels should be oriented in an 'X' pattern from above.
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            
            // --- 1. MECANUM DRIVE CALCULATIONS ---
            // y = forward/backward, x = strafe, rx = rotation
            double y = -gamepad1.left_stick_y; // Stick up is negative, so we negate it
            double x = gamepad1.left_stick_x * 1.1; // Multiplier to improve strafing
            double rx = gamepad1.right_stick_x;

            // Calculate motor powers
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
            double leftFrontPower = (y + x + rx) / denominator;
            double leftBackPower = (y - x + rx) / denominator;
            double rightFrontPower = (y - x - rx) / denominator;
            double rightBackPower = (y + x - rx) / denominator;

            // Send power to the motors
            leftFront.setPower(leftFrontPower);
            leftBack.setPower(leftBackPower);
            rightFront.setPower(rightFrontPower);
            rightBack.setPower(rightBackPower);

            // --- 2. INTAKE MOTOR CONTROL ---
            // Right trigger intakes, Left trigger outtakes
            if (gamepad1.right_trigger > 0.1) {
                //intakeMotor.setPower(1.0);
            } else if (gamepad1.left_trigger > 0.1) {
               // intakeMotor.setPower(-1.0);
            } else {
                //intakeMotor.setPower(0.0);
            }

            // --- 3. SERVO CONTROL ---
            // Press A to open/move, B to close/move
            if (gamepad1.a) {
                //servo1.setPosition(1.0);
               // servo2.setPosition(0.0); // Often servos for a claw move in opposite directions
            } else if (gamepad1.b) {
               // servo1.setPosition(0.0);
               // servo2.setPosition(1.0);
            }

            // --- 4. TELEMETRY ---
            telemetry.addData("Status", "Running");
            telemetry.addData("Drive Motors", "LF:%.2f, RF:%.2f, LB:%.2f, RB:%.2f", 
                leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
            //telemetry.addData("Intake Power", intakeMotor.getPower());
           // telemetry.addData("Ser,nvo Positions", "S1:%.2f, S2:%.2f",
              //  servo1.getPosition(), servo2.getPosition());
            telemetry.update();
        }
    }
}
