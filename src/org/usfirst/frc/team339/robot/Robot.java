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
import org.usfirst.frc.team339.HardwareInterfaces.DoubleSolenoid;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old.debugStateValues;
import org.usfirst.frc.team339.Utils.ImageProcessing.ObjectRemoval;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.vision.AxisCamera;

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


    // feed all motor safeties
    Hardware.leftRearMotorSafety.feed();
    Hardware.rightRearMotorSafety.feed();
    Hardware.leftFrontMotorSafety.feed();
    Hardware.rightFrontMotorSafety.feed();

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
    try
        {
            // check the Autonomous ENABLED/DISABLED switch.
			Autonomous.autonomousEnabled =
			        Hardware.autonomousEnabled.isOn();

            // set the delay time based on potentiometer.
            Autonomous.delay = Autonomous.initDelayTime();

            // get the lane based off of startingPositionPotentiometer
            Autonomous.lane = Autonomous.getLane();

            Autonomous.debug = Autonomous.DEBUGGING_DEFAULT;

            Hardware.transmission
                    .setDebugState(debugStateValues.DEBUG_NONE);

            Autonomous.initAutoState();
            // Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);

            // -------------------------------------
            // motor initialization
            // -------------------------------------

            Hardware.transmission.setFirstGearPercentage(1.0);
            Hardware.transmission.setGear(1);
            Hardware.transmission.setJoysticksAreReversed(true);
            Hardware.transmission.setJoystickDeadbandRange(0.0);

            // --------------------------------------
            // Encoder Initialization
            // --------------------------------------
            Hardware.leftRearEncoder.reset();
            Hardware.rightRearEncoder.reset();

            // Sets Resolution of camera
            Hardware.ringLightRelay.set(Relay.Value.kOff);

            Hardware.axisCamera
                    .writeBrightness(
                            Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);

            // ---------------------------------------
            // turn the timer off and reset the counter
            // so that we can use it in autonomous
            // ---------------------------------------
            Hardware.kilroyTimer.stop();
            Hardware.kilroyTimer.reset();

            Hardware.leftRearEncoder.reset();
            Hardware.rightRearEncoder.reset();
            Hardware.leftFrontMotor.set(0.0);
            Hardware.leftRearMotor.set(0.0);
            Hardware.rightFrontMotor.set(0.0);
            Hardware.rightRearMotor.set(0.0);
            Hardware.armMotor.set(0.0);
            Hardware.armIntakeMotor.set(0.0);

            Hardware.catapultSolenoid0.set(false);
            Hardware.catapultSolenoid1.set(false);
            Hardware.catapultSolenoid2.set(false);

        }
    catch (Exception e)
        {
            System.out.println("Disabled init died");
        }
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

    // Allows us to edit the speed of the robot using the
    // potentiometer on the control switch mount (Alex's fancy name)
    // Essentially, we multiply the percentage given to the motors in
    // second gear by maaath (the value of the delayPot adjusted out
    // of the 0-270 spectrum and into a 10-100 percentage range). If
    // we're not in demo, we're just in regular ol' second gear.

    if (Hardware.inDemo.isOn() == true)
        {
            Hardware.transmission.setSecondGearPercentage(
                    SECOND_GEAR_PERCENTAGE
                            * ((double) (Hardware.delayPot.get()
                                    - Hardware.DELAY_POT_MIN_DEGREES)
                                    / (double) (Hardware.DELAY_POT_DEGREES
                                            - Hardware.DELAY_POT_MIN_DEGREES)));
        }
    else
        {
            Hardware.transmission
                    .setSecondGearPercentage(SECOND_GEAR_PERCENTAGE);
        }
    // --------------------------------------
    // Encoder Initialization
    // --------------------------------------
    if (Hardware.runningInLab == true)
        {
            // ---------------------------------
            // for old Kilroy 16
            // ---------------------------------
            // Hardware.leftRearEncoder.setDistancePerPulse(.0197);
            // Hardware.rightRearEncoder.setDistancePerPulse(.0197);
            Hardware.leftRearEncoder.setDistancePerPulse(
                    this.distancePerTickForMotorEncoders);
            Hardware.rightRearEncoder.setDistancePerPulse(
                    this.distancePerTickForMotorEncoders);
            Hardware.leftRearEncoder.reset();
            Hardware.rightRearEncoder.reset();

        }
    else
        {
            // ---------------------------------
            // Kilroy 17
            // ---------------------------------
            Hardware.leftRearEncoder.setDistancePerPulse(
                    this.distancePerTickForMotorEncoders);
            Hardware.leftRearEncoder.reset();
            Hardware.rightRearEncoder.setDistancePerPulse(
                    this.distancePerTickForMotorEncoders);
            Hardware.rightRearEncoder.reset();
            Hardware.rightRearEncoder.setReverseDirection(true);
        }

    // --------------------------------------
    // initialize all things with the drive system
    // --------------------------------------
    Hardware.transmission.setMaxGear(2);

    Hardware.transmission.setJoystickDeadbandRange(
            JOYSTICK_DEADBAND_ZONE);

    Hardware.transmission
            .setFirstGearPercentage(FIRST_GEAR_PERCENTAGE);


    // ---------------------------------------
    // denote which motors are wired backwards
    // from what we want - setup based on whether
    // or not we are in the lab
    // ---------------------------------------
    if (Hardware.runningInLab == true)
        {
            Hardware.rightFrontMotor.setInverted(true);
            Hardware.rightRearMotor.setInverted(false);
            Hardware.leftFrontMotor.setInverted(true);
            Hardware.leftRearMotor.setInverted(true);
            Hardware.axisCamera.setHaveCamera(false);
        }
    else
        {
            Hardware.rightFrontMotor.setInverted(true);
            Hardware.rightRearMotor.setInverted(false);
            Hardware.leftFrontMotor.setInverted(true);
            Hardware.leftRearMotor.setInverted(true);
        }



    Hardware.armMotor.setInverted(false);
    Hardware.armIntakeMotor.setInverted(false);
    // -------------------------------------
    // AXIS camera initialization
    // -------------------------------------
    // Set the axis camera brightness to dark
    // Hardware.axisCamera.writeBrightness(
    // Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
    // Sets FPS and Resolution of camera
    Hardware.axisCamera.writeBrightness(
            Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
    // Hardware.axisCamera.writeExposureControl(
    // AxisCamera.ExposureControl.kAutomatic);
//		Hardware.axisCamera
//		        .writeExposureControl(AxisCamera.ExposureControl.kHold);
    Hardware.axisCamera.writeMaxFPS(Hardware.AXIS_FPS);
    Hardware.axisCamera.writeResolution(Hardware.AXIS_RESOLUTION);
    Hardware.axisCamera
            .writeWhiteBalance(AxisCamera.WhiteBalance.kFixedIndoor);

    switch (Hardware.axisCamera.getResolution())
        {
        case k640x480:
            Hardware.drive.setXResolution(640.0);
            Hardware.drive.setYResolution(480.0);
            break;
        case k480x360:
            Hardware.drive.setXResolution(480.0);
            Hardware.drive.setYResolution(360.0);
            break;
        case k320x240:
            Hardware.drive.setXResolution(320.0);
            Hardware.drive.setYResolution(240.0);
            break;
        case k240x180:
            Hardware.drive.setXResolution(240.0);
            Hardware.drive.setYResolution(180.0);
            break;
        case k176x144:
            Hardware.drive.setXResolution(176.0);
            Hardware.drive.setYResolution(144.0);
            break;
        default:
        case k160x120:
            Hardware.drive.setXResolution(160.0);
            Hardware.drive.setYResolution(120.0);
            break;
        }

    // -------------------------------------
    // USB camera initialization
    // -------------------------------------
    // Settings for the USB Camera
    // TODO readd
    // Hardware.cam0.setBrightness(50);
    // Hardware.cam0.setExposureAuto();
    // Hardware.cam0.setSize(160, 120);
    // Hardware.cam0.setFPS(20);
    // Hardware.cam0.setWhiteBalanceAuto();
    // Hardware.cam0.setWhiteBalanceHoldCurrent();
    // Hardware.cam0.updateSettings();
    // Starts streaming video
    // TODO add back in
    Hardware.cameraServer.startAutomaticCapture(Hardware.cam1);
    // Sets the hue, saturation, and luminance values for the vision
    // processing.
    // Hardware.imageProcessor.setHSLValues(0, 255, 0, 75, 5, 141);
    // Hardware.imageProcessor.setHSLValues(0, 115, 0, 69, 17, 44);
    Hardware.imageProcessor.setHSLValues(55, 147, 14, 255, 78, 255);
    // Has us remove small objects at the intensity of 5. May have to
    // change those values.
    // Hardware.imageProcessor.setObjectRemoval(ObjectRemoval.BORDER);
    Hardware.imageProcessor.setObjectRemoval(ObjectRemoval.SMALL,
            2);// 3
    // Has us convex hull our image so that the goal becomes a rectangle.
    Hardware.imageProcessor.setUseConvexHull(true);

    // we could also crop the image to only include blobs in our
    // good height range, which removes the possibility of
    // convex hull connecting the two totes when we carry more than one
    // info on cropping image here:
    // http://www.chiefdelphi.com/forums/showthread.php?t=134264
    // Removed criteria that drops blobs outside the center that was in
    // this general area, we need to keep them so we can tell where it is
    // on the screen if it isn't in the center
    // Tells the relay which way is on (kBackward is unable to be used)
    Hardware.ringLightRelay.setDirection(Direction.kForward);


    // -------------------------------------
    // motor initialization
    // -------------------------------------
    Hardware.leftRearMotorSafety.setSafetyEnabled(false);
    Hardware.rightRearMotorSafety.setSafetyEnabled(false);
    Hardware.leftFrontMotorSafety.setSafetyEnabled(false);
    Hardware.rightFrontMotorSafety.setSafetyEnabled(false);

    Hardware.leftRearMotorSafety.setExpiration(.25);
    Hardware.rightRearMotorSafety.setExpiration(.25);
    Hardware.leftFrontMotorSafety.setExpiration(.25);
    Hardware.rightFrontMotorSafety.setExpiration(.25);


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

    if (Hardware.runningInLab == true)
        {
            Autonomous.labScalingFactor = 0.25;
        }
    else
        {
            Autonomous.labScalingFactor = 1.0;
        }

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
    // setupe c+-
    // ---------------------------------------
    System.out.println("Started teleopInit().");

    // -------------------------------------
    // Call the Teleop class's Init function,
    // which contains the user code.
    // -------------------------------------
    Hardware.cameraServer.startAutomaticCapture(Hardware.cam1);

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

    // feed all motor safeties
    Hardware.leftRearMotorSafety.feed();
    Hardware.rightRearMotorSafety.feed();
    Hardware.leftFrontMotorSafety.feed();
    Hardware.rightFrontMotorSafety.feed();

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

    Hardware.transmission.setJoysticksAreReversed(true);

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

    boolean runTest = false;
    if (Hardware.leftDriver.getTrigger() == true)
        {
            runTest = true;
        }

    if (runTest == true)
        {
            Hardware.drive.driveStraightByInches(999, -.3, -.3);

            System.out.println("Left\tRight");
            System.out.println(Hardware.leftFrontMotor.get() + "\t"
                    + Hardware.rightFrontMotor.get());
            System.out.println(Hardware.leftRearMotor.get() + "\t"
                    + Hardware.rightRearMotor.get());
            System.out.println();

        }

    // =========================================================
    // User code goes above here
    // =========================================================

} // end testPeriodic

// ==========================================
// TUNEABLES
// ==========================================
// The inches/tics ratio
private final double distancePerTickForMotorEncoders = 0.0745033113;

// was 0.0745614
//
public static final double JOYSTICK_DEADBAND_ZONE = 0.20;

public static final double FIRST_GEAR_PERCENTAGE = 0.5;

public static final double SECOND_GEAR_PERCENTAGE = 1.0;// previously 0.7;//.85
} // end class
