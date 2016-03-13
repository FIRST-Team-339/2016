/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/
// ====================================================================
// FILE NAME: Autonomous.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 13, 2015
// CREATED BY: Nathanial Lydick
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file is where almost all code for Kilroy will be
// written. All of these functions are functions that should
// override methods in the base class (IterativeRobot). The
// functions are as follows:
// -----------------------------------------------------
// Init() - Initialization code for teleop mode
// should go here. Will be called each time the robot enters
// teleop mode.
// -----------------------------------------------------
// Periodic() - Periodic code for teleop mode should
// go here. Will be called periodically at a regular rate while
// the robot is in teleop mode.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package org.usfirst.frc.team339.robot;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.Utils.Guidance;
import org.usfirst.frc.team339.Utils.ManipulatorArm;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 * This class contains all of the user code for the Autonomous
 * part of the match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop
{
/**
 * User Initialization code for teleop mode should go here. Will be
 * called once when the robot enters teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void init ()
{
	CameraServer.getInstance().setSize(1);
	Hardware.axisCamera
	        .writeBrightness(Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
	// set max speed. change by gear?
	Hardware.drive.setMaxSpeed(MAXIMUM_TELEOP_SPEED);

	Hardware.transmission.setGear(1);
	Hardware.transmission
	        .setFirstGearPercentage(Robot.FIRST_GEAR_PERCENTAGE);
	Hardware.transmission
	        .setSecondGearPercentage(Robot.SECOND_GEAR_PERCENTAGE);
	Hardware.transmission.setJoystickDeadbandRange(.20);
	Hardware.transmission.setJoysticksAreReversed(false);
	Hardware.ringLightRelay.set(Value.kOff);

	isAligningByCamera = false;
	fireRequested = false;
	prepPic = false;
	Hardware.arrowDashboard.setDirection(Guidance.Direction.neutral);
	Hardware.arrowDashboard.update();

	Hardware.catapultSolenoid0.set(false);
	Hardware.catapultSolenoid1.set(false);
	Hardware.catapultSolenoid2.set(false);

	// armEncoder needs to be set to 0
	Hardware.delayTimer.reset();
	Hardware.rightRearEncoder.reset();
	Hardware.leftRearEncoder.reset();
	Hardware.leftFrontMotor.set(0.0);
	Hardware.leftRearMotor.set(0.0);
	Hardware.rightFrontMotor.set(0.0);
	Hardware.rightRearMotor.set(0.0);
	Hardware.armMotor.set(0.0);
	Hardware.armIntakeMotor.set(0.0);
} // end Init


//private char[] reports;
private static boolean done = false;
//private static boolean done2 = false;
private static edu.wpi.first.wpilibj.DoubleSolenoid.Value Reverse;
private static edu.wpi.first.wpilibj.DoubleSolenoid.Value Forward;

private static boolean testAuto = false;

/**
 * User Periodic code for teleop mode should go here. Will be called
 * periodically at a regular rate while the robot is in teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void periodic ()
{

	printStatements();


	if (Hardware.runningInLab == true)
	{
	Hardware.transmission.setJoysticksAreReversed(true);
	Hardware.transmission.setFirstGearPercentage(1.0);
	Hardware.axisCamera.setHaveCamera(false);
	printStatements();


	if (Hardware.leftDriver.getTrigger() == true)
	{
	testAuto = true;
	}

	if (testAuto == true)
	{
	if (Hardware.drive.driveStraightByInches(-20.0, true, -.5,
	        -.5))
	{
	testAuto = false;
	}
	}


	}
	else
	{


	// block of code to move the arm
	if (Math.abs(Hardware.rightOperator
	        .getY()) >= PICKUP_ARM_CONTROL_DEADZONE)
	{
	// use the formula for the sign (value/abs(value)) to get the direction
	// we want the motor to go in,
	// and round it just in case it isn't exactly 1, then cast to an int to
	// make the compiler happy
	Hardware.pickupArm.moveReasonably(
	        -(int) Math.round(Hardware.rightOperator.getY()
	                / Math.abs(Hardware.rightOperator.getY())),
	        Hardware.rightOperator.getRawButton(2));
	//        Hardware.pickupArm
	//                .moveFast((int) Math.round(Hardware.rightOperator.getY()
	//                        / Math.abs(Hardware.rightOperator.getY())),
	//                        Hardware.rightOperator.getRawButton(2));
	//	Hardware.pickupArm.moveFast(1);

	}
	else
	{
	Hardware.pickupArm.stopArmMotor();
	}
	//Block of code to toggle the camera up or down
	//If the camera is down and we press the button.

	if (Hardware.cameraToggleButton.isOnCheckNow() == false)
	{
	//raise the camera and tell the code that it's up
	Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	//If the camera is up and we press the toggle button.
	if (Hardware.cameraToggleButton.isOnCheckNow() == true)
	{
	//Drop the camera and tell the code that it's down
	Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kReverse);
	}

	//end raise/lower camera block

	//Block of code to align us on the goal using the camera
	if (Hardware.rightOperator.getTrigger() == true)
	{
	//Tell the code to align us to the camera
	isAligningByCamera = false;//TODO true
	}
	//If we want to point at the goal using the camera
	if (isAligningByCamera == true)
	{
	//Keep trying to point at the goal
	if (Hardware.drive.alignByCamera(
	        PERCENT_IMAGE_PROCESSING_DEADBAND,
	        CAMERA_ALIGNMENT_TURNING_SPEED, 0.0, false) == true)
	{
	// Once we're in the center, tell the code we no longer care about
	// steering towards the goal
	isAligningByCamera = false;
	}
	}

	//end alignByCameraBlock

	// Block of code to pick up ball or push it out
	//pull in the ball if the pull in button is pressed.
	if (Hardware.rightOperator
	        .getRawButton(TAKE_IN_BALL_BUTTON) == true)
	{
	//TODO demystify magic argument
	Hardware.pickupArm
	        .pullInBall(Hardware.rightOperator.getRawButton(3));
	}
	//push out the ball if the push out button is pressed
	else if (Hardware.rightOperator
	        .getRawButton(PUSH_OUT_BALL_BUTTON) == true)
	{
	Hardware.pickupArm.pushOutBall();
	}
	// If neither the pull in or the push out button are pressed, stop the
	// intake motors
	else
	{
	Hardware.pickupArm.stopIntakeArms();
	}
	//----------------------------
	// block of code to fire
	//----------------------------
	if (Hardware.leftOperator.getTrigger() == true)
	{
	//Tell the code to start firing
	fireRequested = true;
	Hardware.armOutOfWayTimer.start();
	}
	//if the override button is pressed and we want to fire
	if (Hardware.leftOperator
	        .getRawButton(FIRE_OVERRIDE_BUTTON) == true
	        && fireRequested == true)
	{
	//FIRE NO MATTER WHAT!!!!!
	if (fire(3, true) == true)
	{
	//We've shot our ball, we don't want to fire anymore.
	fireRequested = false;
	}
	}
	// If the drivers decided they were being stupid and we don't want to fire anymore
	if (Hardware.leftOperator
	        .getRawButton(FIRE_CANCEL_BUTTON) == true)
	{
	//Stop asking the code to fire
	fireRequested = false;
	}
	// if we want to fire, the arm is out of the way, and we have enough pressure so we don't hurt ourselves.
	if (fireRequested == true && Hardware.pickupArm.moveToPosition(
	        ManipulatorArm.ArmPosition.CLEAR_OF_FIRING_ARM) == true
	        && Hardware.armOutOfWayTimer
	                .get() >= ARM_IS_OUT_OF_WAY_TIME
	        && Hardware.leftOperator
	                .getRawButton(FIRE_OVERRIDE_BUTTON) != true)
	{
	// fire, if we're ready to
	if (fire(3, false) == true)
	{
	// if we're done firing, drop the request
	fireRequested = false;
	Hardware.armOutOfWayTimer.stop();
	Hardware.armOutOfWayTimer.reset();
	}
	}


	//end fire block

	//block of code to tell the drivers where to go
	//TODO finish based on camera input and IR sensors
	//if the rightIR detects HDPE and the left one doesn't
	//if one of the IR's detect HDPE
	//    if (Hardware.rightIR.isOn() == true
	//            || Hardware.leftIR.isOn() == true)
	//        {
	//        //Tell the drivers to stop and hopefully alignByCamera
	////        Hardware.arrowDashboard
	////                .setDirection(Guidance.Direction.linedUp);
	//        if (processingImage == false)
	//            {
	//            Hardware.cameraInTeleopTimer.start();
	//            Hardware.axisCamera.writeBrightness(
	//                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
	//            //Woah, that's too dark! Someone turn on the ringlight!
	//            Hardware.ringLightRelay.set(Value.kOn);
	//            processingImage = true;
	//            }
	//        if (processingImage == true
	//                && Hardware.cameraInTeleopTimer.get() >= .25)
	//            {
	//            try
	//                {
	//                Hardware.imageProcessor
	//                        .updateImage(Hardware.axisCamera.getImage());
	//                }
	//            //This is NI yelling at us for something being wrong
	//            catch (NIVisionException e)
	//                {
	//                //if something wrong happens, tell the stupid programmers 
	//                //who let it happen more information about where it came from
	//                e.printStackTrace();
	//                }
	//            //tell imageProcessor to use the image we just took to look for 
	//            //blobs
	//            Hardware.imageProcessor.updateParticleAnalysisReports();
	//            if(Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x <=70)
	//                {
	//                Hardware.arrowDashboard.setDirection(Guidance.Direction.left);
	//                }
	//            else if(Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x >= 90)
	//                {
	//                Hardware.arrowDashboard.setDirection(Guidance.Direction.right);
	//                }
	//            else
	//                {
	//                Hardware.arrowDashboard.setDirection(Guidance.Direction.linedUp);
	//                }
	//            }
	//        }
	//    //If neither IR detects anything on the ground
	//    else
	//        {
	//        //trust the camera
	//        //TODO base these ones on the camera if we have one.
	//        Hardware.arrowDashboard
	//                .setDirection(Guidance.Direction.neutral);
	//        }
	//    //put the arrows on the screen
	//    Hardware.arrowDashboard.update();

	//If the ball is in the robot, update the driver station with that info
	if (Hardware.armIR.get())
	{
	Guidance.updateBallStatus(true);
	}
	else
	{
	Guidance.updateBallStatus(false);
	}


	//End driver direction block


	// Print statements to test Hardware on the Robot
	printStatements();

	// Takes Pictures based on Operator Button stuff.
	takePicture();


	// Driving the Robot

	//TODO delete all conditionals.
	if (Hardware.leftDriver.getRawButton(8) == true)
	{
	isSpeedTesting = true;
	}
	if (isSpeedTesting == false)
		driveRobot();
	else
	{
	if (Hardware.drive.driveStraightByInches(140.0, true, -1.0,
	        -1.0) == true)
	{
	isSpeedTesting = false;
	}
	}

	//    runCameraSolenoid(Hardware.rightOperator.getRawButton(11),
	//            Hardware.rightOperator.getRawButton(10), false, true);

	}
} // end Periodic

private static boolean isSpeedTesting = false;



/**
 * Hand the transmission class the joystick values and motor controllers for
 * four wheel drive.
 * 
 */
public static void driveRobot ()
{
	//drive the robot with the joysticks
	Hardware.transmission.controls(Hardware.leftDriver.getY(),
	        Hardware.rightDriver.getY());
	// If we're pressing the upshift button, shift up.
	if (Hardware.rightDriver
	        .getRawButton(GEAR_UPSHIFT_JOYSTICK_BUTTON) == true)
	{
	Hardware.transmission.upshift(1);
	}
	// If we press the downshift button, shift down.
	if (Hardware.rightDriver
	        .getRawButton(GEAR_DOWNSHIFT_JOYSTICK_BUTTON) == true)
	{
	Hardware.transmission.downshift(1);
	}
}


public static boolean armIsUp = false;

/**
 * ^^^Bring the boolean armIsUp
 * if method is moved to a different class.^^^
 * 
 * @param upState
 * @param downState
 * @param holdState
 * @param toggle
 * 
 *            When in toggle mode, one boolean raises the arm and one lowers.
 *            When not in toggle mode, only use boolean holdState. This will
 *            keep the arm up for the duration that the holdState is true.
 * 
 *            NOTE: if a parameter is not applicable, set it to false.
 * 
 * 
 * 
 * @author Ryan McGee
 * @written 2/13/16
 * 
 */
//public static void runCameraSolenoid (boolean upState,
//        boolean downState, boolean holdState, boolean toggle)
//{
//    if (upState && toggle == true && armIsUp == false)
//        {
//        Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
//        armIsUp = true;
//        }
//    else if (downState && toggle == true && armIsUp == true)
//        {
//        Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kReverse);
//        armIsUp = false;
//        }
//    else if (holdState && toggle == false)
//        {
//        Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
//        }
//    else
//        {
//        Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kReverse);
//        }
//}

/**
 * Fires the catapult.
 * 
 * @param power
 *            -Can be 1, 2, or 3; corresponds to the amount of solenoids used to
 *            fire.
 * @return
 *         -False if we're not yet done firing, true otherwise.
 */
public static boolean fire (int power, boolean override)
{
	if (Hardware.transducer.get() >= FIRING_MIN_PSI || override == true)
	{
	//        if (Hardware.pickupArm.moveToPosition(
	//                ManipulatorArm.ArmPosition.CLEAR_OF_FIRING_ARM) == true)
	//            {
	if (firstTimeFireRun == true)
	{
	Hardware.fireTimer.start();
	firstTimeFireRun = false;
	}
	switch (power)
	{
		case 1:
			Hardware.catapultSolenoid1.set(true);
			break;
		case 2:
			Hardware.catapultSolenoid2.set(true);
			Hardware.catapultSolenoid0.set(true);
			break;
		default:
		case 3:
			Hardware.catapultSolenoid0.set(true);
			Hardware.catapultSolenoid1.set(true);
			Hardware.catapultSolenoid2.set(true);
			break;
	}
	//            }
	}
	//TODO reduce time to minimum possible
	if (Hardware.fireTimer.get() >= .5)
	{
	Hardware.catapultSolenoid0.set(false);
	Hardware.catapultSolenoid1.set(false);
	Hardware.catapultSolenoid2.set(false);
	Hardware.fireTimer.stop();
	Hardware.fireTimer.reset();
	firstTimeFireRun = true;
	return true;
	}
	return false;

}

private static boolean firstTimeFireRun = true;

/**
 * Takes a picture, processes it and saves it with left operator joystick
 * take unlit picture: 6&7
 * take lit picture: 10&11
 */
public static void takePicture ()
{


	// If we click buttons 6+7 on the left operator joystick, we dim the
	// brightness a lot, turn the ringlight on, and then if we haven't
	// already taken an image then we do and set the boolean to true to
	// prevent us taking more images. Otherwise we don't turn on the
	// ringlight and we don't take a picture. We added a timer to delay
	// taking the picture for the brightness to dim and for the ring
	// light to turn on.
	if (Hardware.leftOperator.getRawButton(6) == true
	        && Hardware.leftOperator.getRawButton(7) == true)
	{
	if (prepPic == false)
	{
	Hardware.axisCamera.writeBrightness(
	        Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
	Hardware.ringLightRelay.set(Value.kOn);
	Hardware.delayTimer.start();
	prepPic = true;
	takingLitImage = true;
	}
	}
	// --------------------------------------------------------------------------
	// ---CAMERA
	// TEST------------------------------------------------------------
	// Once the brightness is down and the ring light is on then the
	// picture is taken, the brightness returns to normal, the ringlight
	// is turned off, and the timer is stopped and reset.
	// @TODO Change .25 to a constant, see line 65 under Hardware
	// Replaced '.25' with Hardware.CAMERA_DELAY_TIME' change back if camera
	// fails
	if (Hardware.delayTimer.get() >= Hardware.CAMERA_DELAY_TIME
	        && prepPic == true && takingLitImage == true)
	{
	Hardware.axisCamera.saveImagesSafely();
	prepPic = false;
	takingLitImage = false;
	}

	if (takingLitImage == false && Hardware.delayTimer.get() >= 1.0)
	{
	Hardware.axisCamera.writeBrightness(
	        Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
	Hardware.ringLightRelay.set(Value.kOff);
	Hardware.delayTimer.stop();
	Hardware.delayTimer.reset();
	}

	// If we click buttons 10+11, we take a picture without the
	// ringlight and set the boolean to true so we don't take a bunch of
	// other pictures.
	if (Hardware.leftOperator.getRawButton(10) == true &&
	        Hardware.leftOperator.getRawButton(11) == true)
	{
	if (takingUnlitImage == false)
	{
	takingUnlitImage = true;
	Hardware.axisCamera.saveImagesSafely();
	}
	}
	else
		takingUnlitImage = false;

	// if the left operator trigger is pressed, then we check to see if
	// we're taking a processed picture through the boolean. If we are
	// not currently taking a processed picture, then it lets us take a
	// picture and sets the boolean to true so we don't take multiple
	// pictures. If it is true, then it does nothing. If we don't click
	// the trigger, then the boolean resets itself to false to take
	// pictures again.
	//    if (Hardware.leftOperator.getTrigger() == true)
	//        {
	//        if (processingImage == true)
	//            {
	//            processImage();
	//            processingImage = false;
	//            }
	//        }
} // end Periodic

static boolean hasBegunTurning = true;

/**
 * 
 * Processes images with the Axis Camera for use in autonomous when
 * trying to score. Will eventually be moved to a Shoot class when
 * one is made.
 * 
 * @author Marlene McGraw
 * @written 2/6/16
 * 
 */
public static void processImage ()
{

	// If we took a picture, we set the boolean to true to prevent
	// taking more pictures and create an image processor to process
	// images.
	// processingImage = true;
	// Hardware.imageProcessor.processImage();
	// System.out.println("Length: " +
	// Hardware.imageProcessor.reports.length);
	// System.out.println("Center of Mass Y: ");

}
// End processImage


/**
 * stores print statements for future use in the print "bank", statements are
 * commented out when
 * not in use, when you write a new print statement, "deposit" the statement in
 * the "bank"
 * do not "withdraw" statements, unless directed to
 * 
 * @author Ashley Espeland
 * @written 1/28/16
 * 
 *          Edited by Ryan McGee
 * 
 */
public static void printStatements ()
{

	// Align By Camera------
	//System.out.println("AligningByCamera = " + isAligningByCamera);
	//checks to see if the robot is aligning by camera

	// Joysticks------------
	//    System.out.println("Left Joystick: " + Hardware.leftDriver.getY());
	//    System.out
	//            .println("Right Joystick: " + Hardware.rightDriver.getY());
	//System.out.println("Left Operator: " + Hardware.leftOperator.getY());
	// System.out.println("Right Operator: " + Hardware.rightOperator.getY());

	// IR sensors-----------
	//System.out.println("left IR = " + Hardware.leftIR.isOn());
	//System.out.println("right IR = " + Hardware.rightIR.isOn());
	//	System.out.println("Has ball IR = " + Hardware.armIR.isOn());



	// pots-----------------
	// System.out.println("delay pot = " + (int) Hardware.delayPot.get());
	// prints the value of the transducer- (range in code is 50)
	//hits psi of 100 accurately
	//System.out.println("transducer = " + Hardware.transducer.get());
	System.out.println("Arm Pot = " + Hardware.armPot.get());
	;

	// Motor controllers-----
	// prints value of the motors
	//    System.out.println("RR Motor T = " + Hardware.rightRearMotor.get());
	//    System.out.println("LR Motor T = " + Hardware.leftRearMotor.get());
	//    System.out
	//            .println("RF Motor T = " + Hardware.rightFrontMotor.get());
	//    System.out.println("LF Motor T = " + Hardware.leftFrontMotor.get());
	//	System.out.println("Arm Motor: " + Hardware.armMotor.get());
	//	System.out
	//	        .println("Intake Motor: " + Hardware.armIntakeMotor.get());

	// Solenoids-------------
	// prints the state of the solenoids
	// System.out.println("cameraSolenoid = " + Hardware.cameraSolenoid.get());
	// System.out.println("catapultSolenoid0 = " +
	// Hardware.catapultSolenoid0.get());
	// System.out.println("catapultSolenoid1 = " +
	// Hardware.catapultSolenoid1.get());
	// System.out.println("catapultSolenoid2 = " +
	// Hardware.catapultSolenoid2.get());


	// Encoders-------------
	//    System.out.println(
	//            "RR distance = " + Hardware.rightRearEncoder.getDistance());
	//    System.out.println(
	//            "LR distance = " + Hardware.leftRearEncoder.getDistance());
	//    //    	 System.out.println("Arm Motor = " + Hardware.armMotor.getDistance());
	//    System.out.println(
	//            "Right Rear Encoder Tics: "
	//                    + Hardware.rightRearEncoder.get());
	//    System.out.println(
	//            "Left Rear Encoder Tics: "
	//                    + Hardware.leftRearEncoder.get());


	// Encoders-------------
	//System.out.println(
	//        "RR distance = " + Hardware.rightRearEncoder.getDistance());
	// System.out.println(
	//        "LR distance = " + Hardware.leftRearEncoder.getDistance());
	//	 System.out.println("Arm Motor = " + Hardware.armMotor.getDistance());

	// Switches--------------
	// prints state of switches
	// System.out.println("Autonomous Enabled Switch: " +
	// Hardware.autonomousEnabled.isOn());
	//	System.out
	//	        .println("Shoot High Switch: " + Hardware.shootHigh.isOn());
	// System.out.println("Shoot Low Switch: " + Hardware.shootLow.isOn());


	// print the position of the 6 position switch------------
	//	System.out.println("Position: " +
	//	        Hardware.startingPositionDial.getPosition());

	// print the position of the 6 position switch------------
	//System.out.println("Position: " +
	//Hardware.startingPositionDial.getPosition());


	// Relay-----------------
	// System.out.println(Hardware.ringLightRelay.get());
} // end printStatements


/*
 * ===============================================
 * Constants
 * ===============================================
 */

private static final double MAXIMUM_TELEOP_SPEED = 1.0;

// right driver 3
private static final int GEAR_UPSHIFT_JOYSTICK_BUTTON = 3;
// right driver 2
private static final int GEAR_DOWNSHIFT_JOYSTICK_BUTTON = 2;
// left operator 2
private static final int CAMERA_TOGGLE_BUTTON = 2;
// Right operator 2
private static final int FIRE_OVERRIDE_BUTTON = 4;
// Left operator 3
private static final int FIRE_CANCEL_BUTTON = 3;
// Right operator 4
private static final int TAKE_IN_BALL_BUTTON = 4;
// right operator 5
private static final int PUSH_OUT_BALL_BUTTON = 5;

private static final double PICKUP_ARM_CONTROL_DEADZONE = 0.2;

private final static double PERCENT_IMAGE_PROCESSING_DEADBAND = .20;

private final static double CAMERA_ALIGNMENT_TURNING_SPEED = .45;

private final static double ARM_IS_OUT_OF_WAY_TIME = .55;

//minimum pressure when allowed to fire
private static final int FIRING_MIN_PSI = 90;

// ==========================================
// TUNEABLES
// ==========================================

private static boolean isAligningByCamera = false;

private static boolean cameraIsUp = false;

private static boolean isDrivingByCamera = false;

private static boolean fireRequested = false;

private static boolean processingImage = true;

// Boolean to check if we're taking a lit picture
private static boolean takingLitImage = false;

// Boolean to check if we're taking an unlit picture
private static boolean takingUnlitImage = false;

// this is for preparing to take a picture with the timer; changes
// brightness, turns on ringlight, starts timer
private static boolean prepPic = false;

//Stores temporarily whether firingState is true, for use in whether the arm is in the way
private static boolean storeFiringState;

} // end class
