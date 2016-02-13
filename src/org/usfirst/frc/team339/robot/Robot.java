/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Kilroy.java (Team 339 - Kilroy)
//
// CREATED ON: Oct 19, 2012
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. All of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// autonomousInit() - Initialization code for autonomous mode
// should go here. Will be called each time the robot enters
// autonomous mode.
// disabledInit() - Initialization code for disabled mode should
// go here. This function will be called one time when the
// robot first enters disabled mode.
// robotInit() - Robot-wide initialization code should go here.
// It will be called exactly 1 time.
// teleopInit() - Initialization code for teleop mode should go here.
// Will be called each time the robot enters teleop mode.
// -----------------------------------------------------
// autonomousPeriodic() - Periodic code for autonomous mode should
// go here. Will be called periodically at a regular rate while
// the robot is in autonomous mode.
// disabledPeriodic() - Periodic code for disabled mode should go here.
// Will be called periodically at a regular rate while the robot
// is in disabled mode.
// teleopPeriodic() - Periodic code for teleop mode should go here.
// Will be called periodically at a regular rate while the robot
// is in teleop mode.
// -----------------------------------------------------
// autonomousContinuous() - Continuous code for autonomous mode should
// go here. Will be called repeatedly as frequently as possible
// while the robot is in autonomous mode.
// disabledContinuous() - Continuous code for disabled mode should go
// here. Will be called repeatedly as frequently as possible while
// the robot is in disabled mode.
// teleopContinuous() - Continuous code for teleop mode should go here.
// Will be called repeatedly as frequently as possible while the
// robot is in teleop mode.
// -----------------------------------------------------
// Other functions not normally used
// startCompetition() - This function is a replacement for the WPI
// supplied 'main loop'. This should not normally be written or
// used.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package org.usfirst.frc.team339.robot;

import com.ni.vision.NIVision.MeasurementType;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.Utils.ImageProcessing.ObjectRemoval;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
// -------------------------------------------------------
/**
 * declares all the code necessary to extend the IterativeRobot class. These are
 * all the methods needed to run Kilroy during a match
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
public class Robot extends IterativeRobot
{

// =================================================
// private data for the class
// =================================================

// -------------------------------------------------------
/**
 * Initialization code for autonomous mode should go here. Will be called
 * once when the robot enters autonomous mode.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void autonomousInit ()
{
    // ---------------------------------------
    // start setup - tell the user we are beginning
    // setup
    // ---------------------------------------
    System.out.println("Started AutonousInit().");

    // -------------------------------------
    // Call the Autonomous class's Init function,
    // which contains the user code.
    // -------------------------------------
    Autonomous.init();

    // ---------------------------------------
    // done setup - tell the user we are complete
    // setup
    // ---------------------------------------
    System.out.println("Completed AutonousInit().");
} // end autonomousInit

// -------------------------------------------------------
/**
 * Non-User Periodic code for autonomous mode should go here.
 * Will be called periodically at a regular rate while the robot
 * is in autonomous mode. This in turn calls the Autonomous class's
 * Periodic function, which is where the user code should be placed.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void autonomousPeriodic ()
{
    // -------------------------------------
    // Call the Autonomous class's Periodic function,
    // which contains the user code.
    // -------------------------------------\
    Autonomous.periodic();

} // end autonomousPeriodic

// -------------------------------------------------------
/**
 * Initialization code for disabled mode should go here. Will be called once
 * when the robot enters disabled mode.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void disabledInit ()
{
    // ---------------------------------------
    // start setup - tell the user we are beginning
    // setup
    // ---------------------------------------
    System.out.println("Started DisabledInit().");

    // =========================================================
    // User code goes below here
    // =========================================================

    // =========================================================
    // User code goes above here
    // =========================================================

    // ---------------------------------------
    // done setup - tell the user we are complete
    // setup
    // ---------------------------------------
    System.out.println("Completed DisabledInit().");
} // end disabledInit

// -------------------------------------------------------
/**
 * Periodic code for disabled mode should go here. Will be called
 * periodically at a regular rate while the robot is in disabled mode. Code
 * that can be "triggered" by a joystick button can go here. This can set up
 * configuration things at the driver's station for instance before a match.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void disabledPeriodic ()
{
    // -------------------------------------
    // Watch dog code used to go here.
    // -------------------------------------
    // =========================================================
    // User code goes below here
    // =========================================================
    // Temporary SmartDashboard testing
    runGuidanceSystem();

    // =========================================================
    // User code goes above here
    // =========================================================

} // end disabledPeriodic
  // Guidance system, need to fix Guidance class!!

public static void runGuidanceSystem ()
{
    if (Hardware.rightOperator.getRawButton(8)
            && !Hardware.rightOperator.getRawButton(9))
        {
        // Guidance.setDirection(Guidance.Direction.left);
        SmartDashboard.putBoolean("Left", true);
        SmartDashboard.putBoolean("Right", false);
        SmartDashboard.putBoolean("Straight", false);
        }
    else if (Hardware.rightOperator.getRawButton(9)
            && !Hardware.rightOperator.getRawButton(8))
        {
        // Guidance.setDirection(Guidance.Direction.right);
        SmartDashboard.putBoolean("Right", true);
        SmartDashboard.putBoolean("Left", false);
        SmartDashboard.putBoolean("Straight", false);
        }
    else if (Hardware.rightOperator.getRawButton(8)
            && Hardware.rightOperator.getRawButton(9))
        {
        // Guidance.setDirection(Guidance.Direction.linedUp);
        SmartDashboard.putBoolean("Straight", true);
        SmartDashboard.putBoolean("Right", false);
        SmartDashboard.putBoolean("Left", false);
        }
    else
        {
        // Guidance.setDirection(Guidance.Direction.neutral);
        SmartDashboard.putBoolean("Right", false);
        SmartDashboard.putBoolean("Left", false);
        SmartDashboard.putBoolean("Straight", false);
        }
}

// -------------------------------------------------------
/**
 * This function is run when the robot is first started up and should be
 * used for any initialization code for the robot.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void robotInit ()
{
    // -------------------------------------
    // Watch dog code used to go here.
    // -------------------------------------
    // =========================================================
    // User code goes below here
    // =========================================================

    // --------------------------------------
    // Encoder Initialization
    // --------------------------------------
    Hardware.leftRearEncoder
            .setDistancePerPulse(distancePerTickForMotorEncoders);
    Hardware.leftRearEncoder.reset();
    Hardware.rightRearEncoder
            .setDistancePerPulse(distancePerTickForMotorEncoders);
    Hardware.rightRearEncoder.reset();

    // --------------------------------------
    // initialize all things with the drive system
    // --------------------------------------
    Hardware.transmission.setMaxGear(2);

    Hardware.transmission.setJoystickDeadbandRange(.20);

    // -------------------------------------
    // USB camera initialization
    // -------------------------------------

    // -------------------------------------
    // USB camera initialization
    // -------------------------------------
    // Settings for the USB Camera
    Hardware.cam0.setBrightness(50);
    Hardware.cam0.setExposureAuto();
    Hardware.cam0.setSize(160, 120);
    Hardware.cam0.setFPS(20);
    Hardware.cam0.setWhiteBalanceAuto();
    Hardware.cam0.setWhiteBalanceHoldCurrent();
    Hardware.cam0.updateSettings();

    // Sets FPS and Resolution of camera
    Hardware.axisCamera.writeMaxFPS(Hardware.AXIS_FPS);
    Hardware.axisCamera.writeResolution(Hardware.AXIS_RESOLUTION);
    Hardware.axisCamera
            .writeBrightness(Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
            // Hardware.axisCamera
            // .writeWhiteBalance(AxisCamera.WhiteBalance.kHold);

    // Starts streaming video
    Hardware.cameraServer.startAutomaticCapture(Hardware.cam0);

    // Sets the hue, saturation, and luminance values for the vision
    // processing.
    Hardware.imageProcessor.setHSLValues(0, 153, 0, 75, 5, 141);
    // Has us remove small objects at the intensity of 5. May have to
    // change those values.
    Hardware.imageProcessor.setObjectRemoval(ObjectRemoval.SMALL, 5);
    // Has us convex hull our image so that the goal becomes a rectangle.
    Hardware.imageProcessor.setUseConvexHull(true);
    // we could also crop the image to only include blobs in our
    // good height range, which removes the possibility of
    // convex hull connecting the two totes when we carry more than one
    // info on cropping image here:
    // http://www.chiefdelphi.com/forums/showthread.php?t=134264
    // Finds the center of the rectangle on the x axis.
    Hardware.imageProcessor.addCriteria(
            MeasurementType.MT_CENTER_OF_MASS_X, 65, 85, 0, 0);

    // Tells the relay which way is on (kBackward is unable to be used)
    Hardware.ringLightRelay.setDirection(Direction.kForward);

    // -------------------------------------
    // motor initialization
    // -------------------------------------
    Hardware.leftRearMotorSafety.setSafetyEnabled(false);
    Hardware.rightRearMotorSafety.setSafetyEnabled(false);
    Hardware.leftFrontMotorSafety.setSafetyEnabled(false);
    Hardware.rightFrontMotorSafety.setSafetyEnabled(false);
    Hardware.rightRearMotor.setInverted(true);

    // --------------------------------------
    // Compressor Initialization
    // --------------------------------------
    Hardware.compressor.setClosedLoopControl(true);

    // ---------------------------------------
    // Solenoid Initialization
    // ---------------------------------------
    // initializes the solenoids...duh duh duh...
    Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
    Hardware.catapultSolenoid0.set(false);
    Hardware.catapultSolenoid1.set(false);
    Hardware.catapultSolenoid2.set(false);
    Hardware.rightRearEncoder.setReverseDirection(true);

    // =========================================================
    // User code goes above here
    // =========================================================
    // ---------------------------------------
    // done setup - tell the user we are complete
    // setup
    // ---------------------------------------
    System.out.println(
            "Kilroy XVII is started.  All hardware items created.");
    System.out.println();
    System.out.println();
} // end robotInit

// -------------------------------------------------------
/**
 * Non-User initialization code for teleop mode should go here. Will be
 * called once when the robot enters teleop mode, and will call the
 * Teleop class's Init function, where the User code should be placed.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void teleopInit ()
{
    // ---------------------------------------
    // start setup - tell the user we are beginning
    // setup
    // ---------------------------------------
    System.out.println("Started teleopInit().");

    // -------------------------------------
    // Call the Teleop class's Init function,
    // which contains the user code.
    // -------------------------------------
    Teleop.init();

    // ---------------------------------------
    // done setup - tell the user we are complete
    // setup
    // ---------------------------------------
    System.out.println("Completed TeleopInit().");
} // end teleopInit

// -------------------------------------------------------
/**
 * Non-User Periodic code for teleop mode should go here. Will be
 * called periodically at a regular rate while the robot is in teleop
 * mode, and will in turn call the Teleop class's Periodic function.
 *
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
@Override
public void teleopPeriodic ()
{
    // -------------------------------------
    // Watch dog code used to go here.
    // -------------------------------------



    // -------------------------------------
    // Call the Teleop class's Periodic function,
    // which contains the user code.
    // -------------------------------------
    Teleop.periodic();

} // end teleopPeriodic

// -------------------------------------------------------
/**
 * Initialization code for test mode should go here. Will be called
 * once when the robot enters test mode.
 *
 * @author Bob Brown
 * @written Jan 2, 2015
 *          -------------------------------------------------------
 */
@Override
public void testInit ()
{
    // =========================================================
    // User code goes below here
    // =========================================================

    // =========================================================
    // User code goes above here
    // =========================================================

} // end testInit

// -------------------------------------------------------
/**
 * Periodic code for test mode should go here. Will be called
 * periodically at a regular rate while the robot is in test mode.
 *
 * @author Bob Brown
 * @written Jan 2, 2015
 *          -------------------------------------------------------
 */
@Override
public void testPeriodic ()
{
    // =========================================================
    // User code goes below here
    // =========================================================

    // =========================================================
    // User code goes above here
    // =========================================================

} // end testPeriodic

// ==========================================
// TUNEABLES
// ==========================================
private final double distancePerTickForMotorEncoders = 0.019706;
} // end class
