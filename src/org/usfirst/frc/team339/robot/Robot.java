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
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;

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
    //--------------------------------------
    // Encoder Initialization
    //--------------------------------------
    Hardware.leftRearEncoder.reset();
    Hardware.leftRearEncoder.setDistancePerPulse(0.019706);

    Hardware.leftFrontEncoder.reset();
    Hardware.leftFrontEncoder.setDistancePerPulse(0.019706);

    Hardware.rightRearEncoder.reset();
    Hardware.rightRearEncoder.setDistancePerPulse(0.019706);

    Hardware.rightFrontEncoder.reset();
    Hardware.rightFrontEncoder.setDistancePerPulse(0.019706);




    // -------------------------------------
    // USB camera initialization
    // -------------------------------------
    Hardware.cam0.setBrightness(50);
    Hardware.cam0.setExposureAuto();
    Hardware.cam0.setSize(160, 120);
    Hardware.cam0.setFPS(20);
    Hardware.cam0.setWhiteBalanceAuto();
    Hardware.cam0.setWhiteBalanceHoldCurrent();
    Hardware.cam0.updateSettings();

    // Hardware.cameraServer.setQuality(0);
    // Hardware.cameraServer.setSize(0);
    Hardware.cameraServer.startAutomaticCapture(Hardware.cam0);

    Hardware.axisCamera.writeMaxFPS(15);
    Hardware.axisCamera.writeResolution(Resolution.k320x240);

    Hardware.ringLightRelay.setDirection(Direction.kForward);

    // -------------------------------------
    // motor initialization
    // -------------------------------------
    Hardware.leftRearMotor.enableBrakeMode(true);
    Hardware.rightRearMotor.enableBrakeMode(true);
    Hardware.leftFrontMotor.enableBrakeMode(true);
    Hardware.rightFrontMotor.enableBrakeMode(true);
    Hardware.leftRearMotor.setSafetyEnabled(true);
    Hardware.rightRearMotor.setSafetyEnabled(true);
    Hardware.leftFrontMotor.setSafetyEnabled(true);
    Hardware.rightFrontMotor.setSafetyEnabled(true);
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

    Hardware.axisCamera.writeBrightness(50);
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

} // end class
