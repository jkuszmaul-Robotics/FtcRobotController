package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Autonomous program for Blue Team 1 based on StarterBotTeleOp.
 *
 * Hardware Configuration:
 * - 4 Drive Motors: "leftfront", "rightfront", "leftrear", "rightrear"
 * - 1 Intake Motor: "intake"
 * - 2 CRServos: "leftintake", "rightintake"
 * - 1 Indexer Servo: "carousel" (Pin 1 Expansion Hub)
 * - 1 Launcher Motor: "launcher" (Port 2 Control Hub)
 */

@Autonomous(name="Blue Autonomous Team 1", group="Autonomous")
public class BlueAutonomousTeam1 extends LinearOpMode {

    // --- Hardware Declarations ---
    private DcMotor leftfront = null;
    private DcMotor rightfront = null;
    private DcMotor leftrear = null;
    private DcMotor rightrear = null;
    private DcMotor intake = null;
    private CRServo leftintake = null;
    private CRServo rightintake = null;
    private CRServo indexServo = null;
    private DcMotor launcherMotor = null;

    // --- Power & Speed Variables ---
    private double drivePower = 0.4;
    private double turnPower = 0.4;
    private double launcherPower = 0.5;
    private double indexerPower = -1.0;

    // --- Timing & Calibration Variables ---
    private double msPerInchDrive = 50.0;     // Milliseconds per inch for straight drive
    private double msPerInchStrafe = 60.0;    // Milliseconds per inch for strafing
    private double msPerDegreeTurn = 10.0;    // Milliseconds per degree for turning
    private long indexerDurationMs = 1500;     // Servo pulse time per !90-degree index
    private long indexerPauseMs = 1500;        // Pause between index shots
    private long launcherRampWaitMs = 3000;   // Wait time for launcher to reach full speed

    // --- Autonomous Routine Values ---
    private int shootCount = 4;
    private double initialBackwardFeet = 3.0/12;
    private double initialForwardFeet = 2.0/12;
    private double strafeRightFeet = 2.0;
    private double strafeFinalFeet = 1.1;
    private double finalBackwardFeet = 5.75;
    private double inchesPerFoot = 12.0;
    private double inches = 0.0;

    @Override
    public void runOpMode() {
        // --- Hardware Mapping ---
        leftfront   = hardwareMap.get(DcMotor.class, "leftfront");
        rightfront  = hardwareMap.get(DcMotor.class, "rightfront");
        leftrear    = hardwareMap.get(DcMotor.class, "leftrear");
        rightrear   = hardwareMap.get(DcMotor.class, "rightrear");
        intake      = hardwareMap.get(DcMotor.class, "intake");
        leftintake  = hardwareMap.get(CRServo.class, "leftintake");
        rightintake = hardwareMap.get(CRServo.class, "rightintake");
        indexServo  = hardwareMap.get(CRServo.class, "carousel");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcher");

        // --- Motor and Servo Direction Configuration ---
        leftfront.setDirection(DcMotor.Direction.REVERSE);
        leftrear.setDirection(DcMotor.Direction.REVERSE);
        rightfront.setDirection(DcMotor.Direction.FORWARD);
        rightrear.setDirection(DcMotor.Direction.FORWARD);

        leftintake.setDirection(CRServo.Direction.REVERSE);
        launcherMotor.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // 1. Drive backward
            driveBackward(initialBackwardFeet);

            driveForward(initialForwardFeet);

            // 2. Start launcher and wait 2 seconds
            startLauncher(launcherPower);
            sleep(launcherRampWaitMs);

            // 3. Index servo to shoot 4 times
            shoot(shootCount);

            // Turn off launcher after shooting
            stopLauncher();

            // 4. Move to the right
            strafeRight(strafeRightFeet);

            // 5. Go backward
            driveBackward(finalBackwardFeet);

            strafeRight(strafeFinalFeet);

            // Ensure robot stops at the end of the autonomous routine
            stopRobot();
        }
    }

    // ==========================================
    //           ROBOT ACTION METHODS
    // ==========================================

    /**
     * Drives the robot forward by the specified distance in feet.
     */
    public void driveForward(double feet) {
        if (!opModeIsActive()) return;
        inches = feet * inchesPerFoot;
        long driveTimeMs = Math.round(inches * msPerInchDrive);
        leftfront.setPower(drivePower);
        leftrear.setPower(drivePower);
        rightfront.setPower(drivePower);
        rightrear.setPower(drivePower);
        sleep(driveTimeMs);
        stopRobot();
    }

    /**
     * Drives the robot backward by the specified distance in feet.
     */
    public void driveBackward(double feet) {
        if (!opModeIsActive()) return;
        inches = feet * inchesPerFoot;
        long driveTimeMs = Math.round(inches * msPerInchDrive);
        leftfront.setPower(-drivePower);
        leftrear.setPower(-drivePower);
        rightfront.setPower(-drivePower);
        rightrear.setPower(-drivePower);
        sleep(driveTimeMs);
        stopRobot();
    }

    /**
     * Strafes the robot to the right by the specified distance in feet.
     */
    public void strafeRight(double feet) {
        if (!opModeIsActive()) return;
        inches = feet * inchesPerFoot;
        long strafeTimeMs = Math.round(inches * msPerInchStrafe);
        leftfront.setPower(drivePower);
        leftrear.setPower(-drivePower);
        rightfront.setPower(-drivePower);
        rightrear.setPower(drivePower);
        sleep(strafeTimeMs);
        stopRobot();
    }

    /**
     * Strafes the robot to the left by the specified distance in feet.
     */
    public void strafeLeft(double feet) {
        if (!opModeIsActive()) return;
        inches = feet * inchesPerFoot;
        long strafeTimeMs = Math.round(inches * msPerInchStrafe);
        leftfront.setPower(-drivePower);
        leftrear.setPower(drivePower);
        rightfront.setPower(drivePower);
        rightrear.setPower(-drivePower);
        sleep(strafeTimeMs);
        stopRobot();
    }

    /**
     * Turns the robot to the left (counter-clockwise) by the specified angle in degrees.
     */
    public void turnLeft(double degrees) {
        if (!opModeIsActive()) return;
        long turnTimeMs = Math.round(degrees * msPerDegreeTurn);
        leftfront.setPower(-turnPower);
        leftrear.setPower(-turnPower);
        rightfront.setPower(turnPower);
        rightrear.setPower(turnPower);
        sleep(turnTimeMs);
        stopRobot();
    }

    /**
     * Turns the robot to the right (clockwise) by the specified angle in degrees.
     */
    public void turnRight(double degrees) {
        if (!opModeIsActive()) return;
        long turnTimeMs = Math.round(degrees * msPerDegreeTurn);
        leftfront.setPower(turnPower);
        leftrear.setPower(turnPower);
        rightfront.setPower(-turnPower);
        rightrear.setPower(-turnPower);
        sleep(turnTimeMs);
        stopRobot();
    }

    /**
     * Stops all drive motors.
     */
    public void stopRobot() {
        leftfront.setPower(0.0);
        leftrear.setPower(0.0);
        rightfront.setPower(0.0);
        rightrear.setPower(0.0);
    }

    /**
     * Starts the launcher motor at the given power level.
     */
    public void startLauncher(double power) {
        launcherMotor.setPower(power);
    }

    /**
     * Stops the launcher motor.
     */
    public void stopLauncher() {
        launcherMotor.setPower(0.0);
    }

    /**
     * Indexes the launcher carousel servo a given number of times to shoot.
     */
    public void shoot(int times) {
        for (int i = 0; i < times && opModeIsActive(); i++) {

            if (i < times/2) {
                indexServo.setPower(indexerPower);
                sleep(indexerDurationMs);
                indexServo.setPower(0.0);
                sleep(indexerPauseMs);
            } else {
                indexServo.setPower(0.75*indexerPower);
                sleep(indexerDurationMs/2);
                indexServo.setPower(indexerPower);
                sleep(indexerDurationMs/2);
                indexServo.setPower(0.0);
                sleep(indexerPauseMs/2);
            }

        }
    }
}
