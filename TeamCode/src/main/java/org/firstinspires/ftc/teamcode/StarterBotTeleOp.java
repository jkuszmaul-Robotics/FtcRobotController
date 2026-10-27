package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Starter Bot TeleOp
 * 
 * Hardware Configuration:
 * - Drive: "leftfront", "rightfront", "leftrear", "rightrear"
 * - Intake: "intake" (Motor), "leftintake", "rightintake" (CRServos)
 * - Indexer: "indexServo" (Positional Servo, Pin 1 Expansion Hub)
 * - Launcher: "launcherMotor" (Motor, Port 2 Control Hub)
 * 
 * Controls:
 * - Sticks: Mecanum Drive
 * - Right Bumper: Synchronized Intake IN
 * - Left Trigger: Synchronized Intake OUT
 * - A / B Buttons: Intake Motor Only (Manual override)
 * - Right Trigger: Indexer Increment (90 degree CW steps)
 * - D-pad Down: Launcher START (Continuous with Ramping)
 * - D-pad Up: Launcher STOP
 */

@TeleOp(name="Starter Bot TeleOp", group="Linear OpMode")
public class StarterBotTeleOp extends LinearOpMode {

    // Drive Motors
    private DcMotor leftfront = null;
    private DcMotor rightfront = null;
    private DcMotor leftrear = null;
    private DcMotor rightrear = null;
    
    // Intake
    private DcMotor intake = null;
    private CRServo leftintake = null;
    private CRServo rightintake = null;
    
    // Indexer and Launcher
    private Servo indexServo = null;
    private DcMotor launcherMotor = null;

    // Indexer State
    private double indexPos = 0.0;
    private boolean lastRightTrigger = false;

    // Launcher State
    private double launcherTargetPower = 0.0;
    private double launcherCurrentPower = 0.0;
    private final double RAMP_RATE = 0.02; // Power increment per loop iteration

    @Override
    public void runOpMode() {
        // Initialize Hardware
        leftfront  = hardwareMap.get(DcMotor.class, "leftfront");
        rightfront = hardwareMap.get(DcMotor.class, "rightfront");
        leftrear   = hardwareMap.get(DcMotor.class, "leftrear");
        rightrear  = hardwareMap.get(DcMotor.class, "rightrear");
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "leftintake");
        rightintake = hardwareMap.get(CRServo.class, "rightintake");
        
        indexServo = hardwareMap.get(Servo.class, "indexServo");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcherMotor");

        // Directions
        leftfront.setDirection(DcMotor.Direction.REVERSE);
        leftrear.setDirection(DcMotor.Direction.REVERSE);
        rightfront.setDirection(DcMotor.Direction.FORWARD);
        rightrear.setDirection(DcMotor.Direction.FORWARD);

        // Reverse left intake for coordinated inward rotation
        leftintake.setDirection(CRServo.Direction.REVERSE);
        
        // Launcher direction: clockwise facing forward
        launcherMotor.setDirection(DcMotor.Direction.FORWARD);

        // Initial servo position (orthogonal to motor)
        indexServo.setPosition(indexPos);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // --- 1. DRIVE ---
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
            leftfront.setPower((y + x + rx) / denominator);
            leftrear.setPower((y - x + rx) / denominator);
            rightfront.setPower((y - x - rx) / denominator);
            rightrear.setPower((y + x - rx) / denominator);

            // --- 2. INTAKE CONTROL ---
            if (gamepad1.right_bumper) {
                intake.setPower(1.0);
                leftintake.setPower(1.0);
                rightintake.setPower(1.0);
            } else if (gamepad1.left_trigger > 0.1) {
                intake.setPower(-1.0);
                leftintake.setPower(-1.0);
                rightintake.setPower(-1.0);
            } else if (gamepad1.a) {
                intake.setPower(1.0);
                leftintake.setPower(0.0); // Stop servos if only using motor button
                rightintake.setPower(0.0);
            } else if (gamepad1.b) {
                intake.setPower(-1.0);
                leftintake.setPower(0.0);
                rightintake.setPower(0.0);
            } else {
                intake.setPower(0.0);
                leftintake.setPower(0.0);
                rightintake.setPower(0.0);
            }

            // --- 3. INDEX SERVO CONTROL (90 degree increments) ---
            boolean currentRightTrigger = (gamepad1.right_trigger > 0.5);
            if (currentRightTrigger && !lastRightTrigger) {
                // Increment by 90 degrees (0.25 assuming 1.0 range = 360 degrees)
                indexPos += 0.25;
                if (indexPos >= 1.0) indexPos = 0.0;
                indexServo.setPosition(indexPos);
            }
            lastRightTrigger = currentRightTrigger;

            // --- 4. LAUNCHER MOTOR CONTROL (Slew Rate Ramping) ---
            if (gamepad1.dpad_down) {
                launcherTargetPower = 1.0; 
            } else if (gamepad1.dpad_up) {
                launcherTargetPower = 0.0;
            }

            // Acceleration and Deceleration logic
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
            telemetry.addData("Indexer Position", indexPos);
            telemetry.addData("Launcher Pwr", "Actual: %.2f / Target: %.2f", launcherCurrentPower, launcherTargetPower);
            telemetry.update();
        }
    }
}
