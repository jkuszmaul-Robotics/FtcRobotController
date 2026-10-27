package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * This is a basic TeleOp program for a goBILDA Starter Bot with Mecanum wheels.
 * 
 * Hardware Configuration:
 * - 4 Drive Motors: "leftfront", "rightfront", "leftrear", "rightrear"
 * - 1 Intake Motor: "intake"
 * - 2 CRServos: "leftintake", "rightintake"
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

    @Override
    public void runOpMode() {
        leftfront  = hardwareMap.get(DcMotor.class, "leftfront");
        rightfront = hardwareMap.get(DcMotor.class, "rightfront");
        leftrear   = hardwareMap.get(DcMotor.class, "leftrear");
        rightrear  = hardwareMap.get(DcMotor.class, "rightrear");
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftintake = hardwareMap.get(CRServo.class, "leftintake");
        rightintake = hardwareMap.get(CRServo.class, "rightintake");

        leftfront.setDirection(DcMotor.Direction.REVERSE);
        leftrear.setDirection(DcMotor.Direction.REVERSE);
        rightfront.setDirection(DcMotor.Direction.FORWARD);
        rightrear.setDirection(DcMotor.Direction.FORWARD);

        // --- FIX 1: REVERSE LEFT SERVO ---
        // If they are mounted facing each other, they need opposite directions to pull IN.
        leftintake.setDirection(CRServo.Direction.REVERSE);

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

            // --- 2. INTAKE CONTROL (Combined Motor and Servos) ---
            // Trigger > 0.1 checks if it's pressed. Using 'else' to STOP the motors.
            if (gamepad1.right_trigger > 0.1) {
                intake.setPower(1.0);
                leftintake.setPower(1.0);
                rightintake.setPower(1.0);
            } else if (gamepad1.left_trigger > 0.1) {
                intake.setPower(-1.0);
                leftintake.setPower(-1.0);
                rightintake.setPower(-1.0);
            } else if (gamepad1.a) {
                intake.setPower(1.0); // Simple button backup
            } else if (gamepad1.b) {
                intake.setPower(-1.0);
            } else {
                // --- FIX 2: STOP LOGIC ---
                // If no buttons are pressed, set power to 0 to prevent stalling/weakness.
                intake.setPower(0.0);
                leftintake.setPower(0.0);
                rightintake.setPower(0.0);
            }

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
