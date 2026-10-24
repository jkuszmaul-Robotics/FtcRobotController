package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
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
    private DcMotor leftfront = null;
    private DcMotor rightfront = null;
    private DcMotor leftrear = null;
    private DcMotor rightrear = null;
    private DcMotor intake = null;
    private CRServo leftintake = null;
    private CRServo rightintake = null;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables from the hardware map.
        // The strings here must match the names you set on the Robot Configuration in the Driver Station.
        leftfront  = hardwareMap.get(DcMotor.class, "leftfront");
        rightfront = hardwareMap.get(DcMotor.class, "rightfront");
        leftrear   = hardwareMap.get(DcMotor.class, "leftrear");
        rightrear  = hardwareMap.get(DcMotor.class, "rightrear");
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "leftintake");
        rightintake = hardwareMap.get(CRServo.class, "rightintake");

        // Direction Mapping:
        // Most robots need the left side motors reversed to drive forward.
        // goBILDA mecanum wheels should be oriented in an 'X' pattern from above.
        leftfront.setDirection(DcMotor.Direction.REVERSE);
        leftrear.setDirection(DcMotor.Direction.REVERSE);
        rightfront.setDirection(DcMotor.Direction.FORWARD);
        rightrear.setDirection(DcMotor.Direction.FORWARD);

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
            leftfront.setPower(leftFrontPower);
            leftrear.setPower(leftBackPower);
            rightfront.setPower(rightFrontPower);
            rightrear.setPower(rightBackPower);

            // --- 2. INTAKE MOTOR CONTROL ---
            // button A turn on intake, button B
            if (gamepad1.a) {
                intake.setPower(1.0);
            } else if (gamepad1.b) {
               intake.setPower(-1.0);
            } else if (gamepad1.y) {
                intake.setPower(0.0);
            }

            //turn on intake servos
            // Fixed: Trigger is a float, so we check if it's pressed past a threshold.
            // Also changed Servo to CRServo to support setPower().
            if (gamepad1.right_bumper) {
                rightintake.setPower(1.0);
            }

            if (gamepad1.left_bumper) {
                leftintake.setPower(1.0);
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
