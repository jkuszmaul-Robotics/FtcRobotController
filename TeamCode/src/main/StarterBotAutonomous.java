
/**
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class starterBotAutonomous extends Autonomous {
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
    // keep track if intake is on or off
    private boolean intakeStatus = false;

    // Indexer State
    // private double indexPos = 0.0;
    private boolean lastRightTrigger = false;

    // Launcher State
    private double launcherTargetPower = 0.0;
    private double launcherCurrentPower = 0.0;
    private final double RAMP_RATE = 0.02; // Power increment per loop iteration for acceleration/deceleration
    private double maxDrivePower = 0.4;

}

private void motorDrive () {
    leftfront  = hardwareMap.get(DcMotor.class, "leftfront");
    rightfront = hardwareMap.get(DcMotor.class, "rightfront");
    leftrear   = hardwareMap.get(DcMotor.class, "leftrear");
    rightrear  = hardwareMap.get(DcMotor.class, "rightrear");
    intake  = hardwareMap.get(DcMotor.class, "intake");
    leftintake = hardwareMap.get(CRServo.class, "leftintake");
    rightintake = hardwareMap.get(CRServo.class, "rightintake");

    // Initialize indexer and launcher hardware
    indexServo = hardwareMap.get(CRServo.class, "carousel");
    launcherMotor = hardwareMap.get(DcMotor.class, "launcher");

    leftfront.setDirection(DcMotor.Direction.REVERSE);
    leftrear.setDirection(DcMotor.Direction.REVERSE);
    rightfront.setDirection(DcMotor.Direction.FORWARD);
    rightrear.setDirection(DcMotor.Direction.FORWARD);
}
 */
