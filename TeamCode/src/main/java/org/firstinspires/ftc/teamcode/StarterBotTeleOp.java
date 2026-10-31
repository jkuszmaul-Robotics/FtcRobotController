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
 * - 4 Drive Motors: "leftfront", "rightfront", "leftrear", "rightrear"
 * - 1 Intake Motor: "intake"
 * - 2 CRServos: "leftintake", "rightintake"
 * - 1 Indexer Servo: "carousel" (Pin 1 Expansion Hub)
 * - 1 Launcher Motor: "launcher" (Port 2 Control Hub)
 */

@TeleOp(name="Starter Bot TeleOp", group="Linear OpMode")
public class StarterBotTeleOp extends LinearOpMode {

    private DcMotor leftfront = null;
    private DcMotor rightfront = null;
    private DcMotor leftrear = null;
    private DcMotor rightrear = null;
    private DcMotor intake = null;
    private CRServo leftintake = null;
    private CRServo rightintake = null;

    // New Hardware for Indexer and Launcher
    private CRServo indexServo = null;
    private DcMotor launcherMotor = null;
    // servo power variable
    private double servoPower = -1.0;

    // Indexer State
    // private double indexPos = 0.0;
    private boolean lastRightTrigger = false;

    // Launcher State
    private double launcherTargetPower = 0.0;
    private double launcherCurrentPower = 0.0;
    private final double RAMP_RATE = 0.02; // Power increment per loop iteration for acceleration/deceleration

    @Override
    public void runOpMode() {
        leftfront  = hardwareMap.get(DcMotor.class, "leftfront");
        rightfront = hardwareMap.get(DcMotor.class, "rightfront");
        leftrear   = hardwareMap.get(DcMotor.class, "leftrear");
        rightrear  = hardwareMap.get(DcMotor.class, "rightrear");
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "leftintake");
        rightintake = hardwareMap.get(CRServo.class, "rightintake");
        
        // Initialize indexer and launcher hardware
        indexServo = hardwareMap.get(CRServo.class, "carousel");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcher");

        leftfront.setDirection(DcMotor.Direction.REVERSE);
        leftrear.setDirection(DcMotor.Direction.REVERSE);
        rightfront.setDirection(DcMotor.Direction.FORWARD);
        rightrear.setDirection(DcMotor.Direction.FORWARD);

        // Directions for intake servos
        // Reversing one ensures they spin together to pull items IN
        leftintake.setDirection(CRServo.Direction.REVERSE);
        
        // Launcher direction: clockwise facing forward (Standard Forward is CW)
        launcherMotor.setDirection(DcMotor.Direction.FORWARD);

        // Initial servo position (orthogonal to motor)
        // not compatible with continuous mode:     indexServo.setPosition(indexPos);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // --- 1. DRIVE ---
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 0.5);
            leftfront.setPower((y + x + rx) / denominator);
            leftrear.setPower((y - x + rx) / denominator);
            rightfront.setPower((y - x - rx) / denominator);
            rightrear.setPower((y + x - rx) / denominator);

            // --- 2. INTAKE CONTROL ---
            // Note: right_trigger is now used for the indexer servo. 
            // Intake IN is moved to right_bumper to avoid conflict.
            if (gamepad1.right_bumper) {
                intake.setPower(-1.0);
                leftintake.setPower(-1.0);
                rightintake.setPower(-1.0);
            } else if (gamepad1.a) {
                intake.setPower(servoPower);
                leftintake.setPower(servoPower);
                rightintake.setPower(servoPower);
            } else if (gamepad1.b) {
                intake.setPower(-1.0);
                leftintake.setPower(-1.0); // Synchronize for full power
                rightintake.setPower(-1.0);
            } else {
                // STOP everything when no input is active
                intake.setPower(0.0);
                leftintake.setPower(0.0);
                rightintake.setPower(0.0);
            }

            // --- 3. INDEX SERVO CONTROL (90 degree increments) ---
            // Detect when right trigger is pushed (threshold > 0.5)
            boolean currentRightTrigger = gamepad1.right_trigger > 0.5;
            
            // Edge detection: only increment once per trigger press
            if (currentRightTrigger && !lastRightTrigger) {
                indexServo.setPower(-1.0); // Turn servo ON
                sleep(250);               // Adjust milliseconds (e.g., 200-300ms) until it rotates ~90 degrees
                indexServo.setPower(0.0); // Turn servo OFF
            }

            lastRightTrigger = currentRightTrigger;

            // --- 4. LAUNCHER MOTOR CONTROL (Slew Rate Ramping) ---
            if (gamepad1.dpad_down) {
                launcherTargetPower = 0.45; // Enable continuous spin
            } else if (gamepad1.dpad_up) {
                launcherTargetPower = 0.0; // Stop
            }

            // Ramping logic for start-up acceleration and wind-down deceleration
            if (launcherCurrentPower < launcherTargetPower) {
                launcherCurrentPower += RAMP_RATE;
                if (launcherCurrentPower > launcherTargetPower) launcherCurrentPower = launcherTargetPower;
            } else if (launcherCurrentPower > launcherTargetPower) {
                launcherCurrentPower -= RAMP_RATE;
                if (launcherCurrentPower < launcherTargetPower) launcherCurrentPower = launcherTargetPower;
            }
            launcherMotor.setPower(launcherCurrentPower);

            // --- 5. TELEMETRY ---
            telemetry.addData("Status", "Running");
            telemetry.addData("Launcher Power", "Target: %.2f, Current: %.2f", launcherTargetPower, launcherCurrentPower);
            telemetry.update();
        }
    }
}
/**
 * carousel
 * leave in CR
 * // Read the analog value of the right trigger (0.0 to 1.0)
 * double triggerPower = gamepad1.right_trigger;
 *
 * // Directly feed the trigger value to control the speed dynamically
 * crServo.setPower(triggerPower);
 * double triggerPower = gamepad1.right_trigger;
 *
 * if (triggerPower > 0.05) {
 *     crServo.setPower(triggerPower);
 * } else {
 *     crServo.setPower(0.0); // Force a hard stop if it's just resting
 * }
 */