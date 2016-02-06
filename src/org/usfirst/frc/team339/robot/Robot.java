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

import org.usfirst.frc.team339.Hardware.Hardware;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Relay.Direction;

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
// ----------------------------------------------------
// new watchdog object
// ----------------------------------------------------
MotorSafetyHelper watchdog;

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

	// Dims the brightness level so that we can take a better picture
	// of the retroreflective tape.

	// of the retroreflective tape.
	Hardware.axisCamera.writeBrightness(5);
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
	Hardware.leftRearMotorSafety.setSafetyEnabled(false);
	Hardware.rightRearMotorSafety.setSafetyEnabled(false);
	Hardware.leftFrontMotorSafety.setSafetyEnabled(false);
	Hardware.rightFrontMotorSafety.setSafetyEnabled(false);
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

	// =========================================================
	// User code goes above here
	// =========================================================

} // end disabledPeriodic

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
	Hardware.leftRearEncoder.setDistancePerPulse(0.019706);
	Hardware.leftRearEncoder.reset();

	Hardware.rightRearEncoder.setDistancePerPulse(0.019706);
	Hardware.rightRearEncoder.reset();

	Hardware.transmission.initEncoders(Hardware.rightRearEncoder,
	        Hardware.leftRearEncoder);
	Hardware.leftRearEncoder
	        .setDistancePerPulse(distancePerTickForMotorEncoders);
	Hardware.leftRearEncoder.reset();

	Hardware.rightRearEncoder
	        .setDistancePerPulse(distancePerTickForMotorEncoders);
	Hardware.rightRearEncoder.reset();

	Hardware.transmission.initEncoders(Hardware.rightRearEncoder,
	        Hardware.leftRearEncoder);
	        // -------------------------------------
	        // USB camera initialization
	        // -------------------------------------

    Hardware.transmission.initEncoders(Hardware.rightRearEncoder,
            Hardware.leftRearEncoder);
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

	// Starts streaming video
	Hardware.cameraServer.startAutomaticCapture(Hardware.cam0);

	// Sets FPS and Resolution of camera
	Hardware.axisCamera.writeMaxFPS(Hardware.AXIS_FPS);
	Hardware.axisCamera.writeResolution(Hardware.AXIS_RESOLUTION);
	Hardware.axisCamera
	        .writeBrightness(Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
	        // Hardware.axisCamera
	        // .writeWhiteBalance(AxisCamera.WhiteBalance.kHold);


	// Tells the relay which way is on (kBackward is unable to be used)
	Hardware.ringLightRelay.setDirection(Direction.kForward);

	// -------------------------------------
	// motor initialization
	// -------------------------------------

	Hardware.leftRearMotorSafety.setSafetyEnabled(true);
	Hardware.rightRearMotorSafety.setSafetyEnabled(true);
	Hardware.leftFrontMotorSafety.setSafetyEnabled(true);
	Hardware.rightFrontMotorSafety.setSafetyEnabled(true);
	Hardware.rightRearMotor.setInverted(true);

	// --------------------------------------
	// Compressor Initialization
	// --------------------------------------
	Hardware.compressor.setClosedLoopControl(true);

	// --------------------------------------
	// Encoder Initialization
	// --------------------------------------
	Hardware.leftRearEncoder.reset();
	Hardware.leftRearEncoder
	        .setDistancePerPulse(distancePerTickForMotorEncoders);

	Hardware.rightRearEncoder.reset();
	Hardware.rightRearEncoder
	        .setDistancePerPulse(distancePerTickForMotorEncoders);

	// ---------------------------------------
	// Solenoid Initialization
	// ---------------------------------------
	// initializes the solenoids...duh duh duh...
	Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
	Hardware.catapultSolenoid0.set(false);
	Hardware.catapultSolenoid1.set(false);
	Hardware.catapultSolenoid2.set(false);
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
