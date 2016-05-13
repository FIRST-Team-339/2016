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
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old.debugStateValues;
import org.usfirst.frc.team339.Utils.Drive;
import org.usfirst.frc.team339.Utils.ErrorMessage.PrintsTo;
import org.usfirst.frc.team339.Utils.Guidance;
import org.usfirst.frc.team339.Utils.ManipulatorArm;
import org.usfirst.frc.team339.Utils.ManipulatorArm.ArmPosition;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
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
		// Initial set up so the screen doesn't start green after Teleop starts
		Guidance.updateBallStatus(false);
		// Tell USB camera handler that we only have one USB camera
		CameraServer.getInstance().setSize(1);// AHK @cameratesting
		// Make sure the camera is really dark
		Hardware.axisCamera.writeBrightness(
		        Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
		// set max speed.
		Hardware.drive.setMaxSpeed(MAXIMUM_TELEOP_SPEED);
		// Set up the transmission class so it knows how to drive. Kind of
		// like driver's ed for Robots. I wish my drivers ed class was this
		// short and painless...
		Hardware.transmission.setGear(2);
		Hardware.transmission
		        .setFirstGearPercentage(Robot.FIRST_GEAR_PERCENTAGE);
		Hardware.transmission
		        .setSecondGearPercentage(Robot.SECOND_GEAR_PERCENTAGE);
		Hardware.transmission.setJoystickDeadbandRange(.20);
		Hardware.transmission.setJoysticksAreReversed(false);
		// make sure we don't start Teleop off with the ringlight on
		Hardware.ringLightRelay.set(Value.kOff);
		// Make sure we don't start off aligningByCamera, firing, or taking
		// a picture. That would be a nasty suprise:
		// "ENABLING"
		// *SHOOM*
		// *Newly severed head flies out of pit from firing arm*
		// *SPLAT*
		// "I guess the Industrial Safety Award is out the window"
		isAligningByCamera = false;
		fireRequested = false;
		prepPic = false;
		// Hardware.drive.alignByCameraStateMachine(0.0, 0.0, 0.0, 0.0,
		// 0.0,
		// 0.0, true, false, false);
		// currentCameraReturn = Drive.alignByCameraReturn.WORKING;
		// Make sure when we enable we're not telling the drivers to do
		// anything yet
		Hardware.arrowDashboard
		        .setDirection(Guidance.Direction.neutral);
		        // Hardware.arrowDashboard.update();

		//Starts testing speed.


		// Turn off all the solenoids before we really start anything
		Hardware.catapultSolenoid0.set(false);
		Hardware.catapultSolenoid1.set(false);
		Hardware.catapultSolenoid2.set(false);

		// Reset all timers, encoders, and stop all the motors.
		Hardware.delayTimer.reset();
		Hardware.rightRearEncoder.reset();
		Hardware.leftRearEncoder.reset();
		Hardware.leftFrontMotor.set(0.0);
		Hardware.leftRearMotor.set(0.0);
		Hardware.rightFrontMotor.set(0.0);
		Hardware.rightRearMotor.set(0.0);
		Hardware.armMotor.set(0.0);
		Hardware.armIntakeMotor.set(0.0);

		// Allows us to edit the speed of the robot using the 
		// potentiometer on the control switch mount (Alex's fancy name)
		// Essentially, we multiply the percentage given to the motors in
		// second gear by maaath (the value of the delayPot adjusted out 
		// of the 0-270 spectrum and into a 10-100 percentage range). If
		// we're not in demo, we're just in regular ol' second gear.
		if (Hardware.inDemo.isOn() == true)
		{
			Hardware.transmission.setSecondGearPercentage(
			        (Robot.SECOND_GEAR_PERCENTAGE
			                * (Hardware.delayPot.get() *
			                        (Robot.SECOND_GEAR_PERCENTAGE
			                                - Hardware.MINIMUM_POT_SCALING_VALUE)
			                        /
			                        (Hardware.DELAY_POT_DEGREES))
			                + Hardware.MINIMUM_POT_SCALING_VALUE));
		}
		else
		{
			Hardware.transmission
			        .setSecondGearPercentage(
			                Robot.SECOND_GEAR_PERCENTAGE);
		}
	} // end Init


	// private char[] reports;
	private static boolean done = false;
	// private static boolean done2 = false;
	private static edu.wpi.first.wpilibj.DoubleSolenoid.Value Reverse;
	private static edu.wpi.first.wpilibj.DoubleSolenoid.Value Forward;


	private static boolean testAuto = true;
	private static boolean testMove1IsDone = true;
	private static boolean testMove2IsDone = false;
	private static boolean testMove3IsDone = true;
	private static boolean testCameraIsDone = true;
	private static boolean isTurning180Degrees = false;
	// private static boolean testingAlignByCamera = false;//@DELETE
	//
	//static Timer speedTesterTimer = new Timer();
	//static SpeedTester speedTester = new SpeedTester(
	//        Hardware.rightRearEncoder, speedTesterTimer);
	//static double speedTestValue;
	//static boolean speedTesting = true;

	/**
	 * User Periodic code for teleop mode should go here. Will be called
	 * periodically at a regular rate while the robot is in teleop mode.
	 *
	 * @author Nathanial Lydick
	 * @written Jan 13, 2015
	 */
	static double val;

	public static void periodic ()
	{
		// Print out any data we want from the hardware elements.
		printStatements();

		//		if (Hardware.leftDriver.getTrigger() == true)
		//		{
		//			Hardware.axisCamera.writeExposureControl(
		//			        AxisCamera.ExposureControl.kHold);
		//		}//@AHK TODO remove

		//		if (Hardware.leftDriver.getRawButton(6) == true)
		//		{
		//			for (int i = 0; i < 5; i++)
		//			{
		//				for (int j = 0; j < 5; j++)
		//				{
		//					switch (j)
		//					{
		//					case 0:
		//						Hardware.axisCamera
		//						        .saveImageWithTypeName("A");
		//						break;
		//					case 1:
		//						Hardware.axisCamera
		//						        .saveImageWithTypeName("B");
		//						break;
		//					case 2:
		//						Hardware.axisCamera
		//						        .saveImageWithTypeName("C");
		//						break;
		//					case 3:
		//						Hardware.axisCamera
		//						        .saveImageWithTypeName("D");
		//						break;
		//					case 4:
		//						Hardware.axisCamera
		//						        .saveImageWithTypeName("E");
		//						break;
		//					}
		//					try
		//					{
		//						Thread.sleep(20);
		//					}
		//					catch (InterruptedException e)
		//					{
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
		//				}
		//			}
		//		}

		//		val = Hardware.leftDriver.getThrottle();
		//		Hardware.axisCamera.writeBrightness((int) val * 100);
		//		System.out.println("Camera brightness: " + (int) val * 100);//@AHK remove.

		//    Hardware.transmission.upshift(1);
		//    driveRobot();
		//    speedTester.watchJoystick(Hardware.rightDriver.getY()); //@AHK REMOVE



		Hardware.errorMessage.printError("test12", PrintsTo.roboRIO);

		// If we're running tests in the lab, NOT at competition
		if (Hardware.runningInLab == true)
		{
			Hardware.transmission
			        .setDebugState(debugStateValues.DEBUG_NONE);
			Hardware.drive.setBrakeSpeed(.30);
			Hardware.transmission.setJoysticksAreReversed(true);
			Hardware.transmission.setFirstGearPercentage(1.0);
			Hardware.axisCamera.setHaveCamera(false);


			// System.out.println("t1: " + testMove1IsDone);
			// System.out.println("t2: " + testMove2IsDone);
			// System.out.println("t3: " + testMove3IsDone);
			// if we hit the left driver trigger in the lab
			// if (Hardware.leftDriver.getTrigger() == true)
			// {
			// // tell the code to start testing autonomous
			// testAuto = true;
			// }
			// // Test certain aspects of autonomous
			// if (testAuto == true)
			// {
			// if (!testMove1IsDone)
			// {
			// System.out.print("\n" + 1 + "\n");
			// if (Hardware.drive.driveStraightByInches(12.0, true,
			// .9, .9))
			// {
			// Autonomous.resetEncoders();
			// testMove1IsDone = true;
			// }
			// }
			// else if (!testMove2IsDone && (Hardware.leftIR.isOn()
			// || Hardware.rightIR.isOn()))
			// {
			// System.out.println("IR Detected.");
			// // if (Hardware.drive.turnLeftDegrees(60.0, true,
			// // -.55, .55))
			// // {
			// // //Autonomous.resetEncoders();
			// // Hardware.transmission.controls(0.0, 0.0);
			// testMove2IsDone = true;
			// // }
			// }
			// else if (!testMove3IsDone)
			// {
			// System.out.print("\n" + 3 + "\n");
			// if (Hardware.drive.driveStraightByInches(12.0, true,
			// .3, .3))
			// {
			// Autonomous.resetEncoders();
			// testMove3IsDone = true;
			// }
			// }
			// else if (!testCameraIsDone)
			// {
			// // if (Hardware.drive.driveByCamera(999.0, .2, .25,
			// // 0.0, true));
			// }
			// else
			// {
			// // Stop
			// Hardware.transmission.controls(0.0, 0.0);
			// }
			// }
		}
		// If we don't have the runningInLab flag set to true
		else
		{
			if (Hardware.leftOperator.getRawButton(8))
			{
				testingAlignByCamera = true;//@FALSE
			}
			if (testingAlignByCamera == true)
			{
				currentCameraReturn =
				        Hardware.drive.alignByCameraStateMachine(
				                CAMERA_ALIGN_X_DEADBAND,
				                CAMERA_ALIGN_Y_DEADBAND,
				                CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
				                CAMERA_Y_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
				                ALIGN_BY_CAMERA_TURNING_SPEED,
				                ALIGN_BY_CAMERA_DRIVE_SPEED,
				                (Hardware.rightOperator
				                        .getRawButton(10) == true
				                        && Hardware.rightOperator
				                                .getRawButton(
				                                        11) == true),
				                true, true);
				if (currentCameraReturn == Drive.alignByCameraReturn.DONE)
				{
					//isFiringByCamera = false;
					testingAlignByCamera = false;
					//@AHK added from align call 
					// Tell the code to align us to the camera
					isAligningByCamera = true;
					// Tell the code we want to fire when we're done
					isFiringByCamera = true;

					//fireRequested = true; //@AHK Removed for align-drive-align
					Hardware.armOutOfWayTimer.stop();
					Hardware.armOutOfWayTimer.reset();
					Hardware.armOutOfWayTimer.start();
					currentCameraReturn =
					        Drive.alignByCameraReturn.WORKING;
				}
				else if (currentCameraReturn == Drive.alignByCameraReturn.CANCELLED
				        || currentCameraReturn == Drive.alignByCameraReturn.NO_BLOBS_FOUND)//@AHK test low priority
				{
					isFiringByCamera = false;
					testingAlignByCamera = false;
					fireRequested = false;
					Hardware.armOutOfWayTimer.stop();
					Hardware.armOutOfWayTimer.reset();
					currentCameraReturn =
					        Drive.alignByCameraReturn.WORKING;
				}
			}
			// @DELETE

			// Begin arm movement code
			if (Math.abs(Hardware.rightOperator
			        .getY()) >= PICKUP_ARM_CONTROL_DEADZONE)
			{
				// use the formula for the sign (value/abs(value)) to get the
				// direction
				// we want the motor to go in,
				// and round it just in case it isn't exactly 1, then cast to an int
				// to
				// make the compiler happy
				Hardware.pickupArm.moveReasonably(
				        -(int) Math.round(Hardware.rightOperator.getY()
				                / Math.abs(
				                        Hardware.rightOperator.getY())),
				        Hardware.rightOperator.getRawButton(2));

			}
			else if (isAligningByCamera == false
			/* && testingAlignByCamera == false */)
			{
				// If the arm control joystick isn't beyond our deadzone, stop the
				// arm.
				Hardware.pickupArm.stopArmMotor();
			}
			// End arm movement code

			// When the driver hits button 2, the robot will turn 180
			// degrees to the right so we can drive back through the Sally
			// Port.
			if (Hardware.leftDriver.getRawButton(2) == true)
			{
				Hardware.leftRearEncoder.reset();
				Hardware.rightRearEncoder.reset();
				isTurning180Degrees = false;
				System.out.println("Turning 180 Degrees? " +
				        isTurning180Degrees);
				Hardware.transmission.setJoysticksAreReversed(true);
			}

			// If we've turned 180 degrees (going at 60% power and braking
			// at the end), we set the boolean back to false and reset
			// the encoders.
			if (isTurning180Degrees == true)
			{
				if (Hardware.drive.turnLeftDegrees(180.0, true, .6,
				        -.6) == true)
				{
					isTurning180Degrees = false;
					Hardware.leftRearEncoder.reset();
					Hardware.rightRearEncoder.reset();
					Hardware.transmission.controls(0.0, 0.0);
					Hardware.transmission
					        .setJoysticksAreReversed(false);
				}
			}

			// Begin Ball manipulation code
			// pull in the ball if the pull in button is pressed.
			if (Hardware.rightOperator
			        .getRawButton(TAKE_IN_BALL_BUTTON) == true)
			{
				// if they press the 3rd button on the rightOperator joystick
				// override the pickup mechanism
				Hardware.pickupArm
				        .pullInBall(
				                Hardware.rightOperator.getRawButton(3));
			}
			// push out the ball if the push out button is pressed
			else if (Hardware.rightOperator
			        .getRawButton(PUSH_OUT_BALL_BUTTON) == true)
			{
				Hardware.pickupArm.pushOutBall();
			}
			// If neither the pull in or the push out button are pressed, stop the
			// intake motors
			else if (isAligningByCamera == false
			/* && testingAlignByCamera == false */)
			{
				Hardware.pickupArm.stopIntakeMotors();
			}
			// ----------------------------
			// block of code to fire
			// ----------------------------
			if (Hardware.leftOperator.getTrigger() == true)
			{
				// Tell the code to start firing
				fireRequested = true;
				Hardware.axisCamera.saveImagesSafely();
				Hardware.armOutOfWayTimer.start();
			}
			// if the override button is pressed and we want to fire
			if (Hardware.leftOperator
			        .getRawButton(FIRE_OVERRIDE_BUTTON) == true
			        && fireRequested == true)
			{
				// FIRE NO MATTER WHAT!!!!!
				if (fire(3, true) == true)
				{
					// We've shot our ball, we don't want to fire anymore.
					fireRequested = false;
				}
			}
			// If the drivers decided they were being stupid and we don't want to
			// fire anymore
			if (Hardware.leftOperator
			        .getRawButton(FIRE_CANCEL_BUTTON) == true
			        || (Hardware.rightOperator.getRawButton(10) == true
			                && Hardware.rightOperator
			                        .getRawButton(11) == true))
			{
				// Stop asking the code to fire
				fireRequested = false;
				//or cancel turning 180.
				//I'm commondeering this code for a GPCB (general purpose cancel button)
				isTurning180Degrees = false;
			}
			// if we want to fire, the arm is out of the way, and we have enough
			// pressure so we don't hurt ourselves.
			if (fireRequested == true
			        && Hardware.pickupArm.moveToPosition(
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

			// Begin raise/lower camera block
			// If the camera is down and we press the button.
			if (Hardware.cameraToggleButton.isOnCheckNow() == false
			        && isAligningByCamera == false
			/* && testingAlignByCamera == false */)
			{
				// raise the camera
				Hardware.cameraSolenoid
				        .set(DoubleSolenoid.Value.kForward);
				// Hardware.ringLightRelay.set(Value.kOn);
			}
			// If the camera is up and we press the toggle button.
			if (Hardware.cameraToggleButton.isOnCheckNow() == true
			        && isAligningByCamera == false
			/* && testingAlignByCamera == false */)
			{
				// Drop the camera
				Hardware.cameraSolenoid
				        .set(DoubleSolenoid.Value.kReverse);
				// Hardware.ringLightRelay.set(Value.kOff);
			}
			// end raise/lower camera block



			// Block of code to align us on the goal using the camera
			// Will fire the boulder when done.
			if (Hardware.rightOperator.getTrigger() == true)
			{
				// Tell the code to align us to the camera
				isAligningByCamera = true;
				// Tell the code we want to fire when we're done
				isFiringByCamera = true;
			}

			// Align, but do not fire.
			if (Hardware.leftOperator.getRawButton(5))
			{
				isAligningByCamera = true;
			}

			// If we want to point at the goal using the camera
			if (isAligningByCamera == true)
			{
				// check if there is a ball in the arm
				if (Hardware.armIR.isOn() == true)
				{
					// move the arm to deposit position
					if (Hardware.pickupArm
					        .moveToPosition(
					                ArmPosition.DEPOSIT) == true)
					{
						// put the ball in the catapult
						Hardware.pickupArm.pullInBall(true);
					}
				}

				// Keep trying to point at the goal
				currentCameraReturn = Hardware.drive.alignByCamera(
				        PERCENT_IMAGE_PROCESSING_DEADBAND,
				        CAMERA_ALIGNMENT_TURNING_SPEED,
				        CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
				        true);//-.375
				// if (Hardware.drive.alignByCamera(
				// PERCENT_IMAGE_PROCESSING_DEADBAND,
				// CAMERA_ALIGNMENT_TURNING_SPEED, -.30, //-.483,
				// false) == true)
				if (currentCameraReturn == Drive.alignByCameraReturn.DONE)
				{
					// Once we're in the center, tell the code we no longer care
					// about
					// steering towards the goal
					isAligningByCamera = false;

					// If using right trigger. FIRE.
					if (isFiringByCamera == true
					        && Hardware.rightOperator
					                .getRawButton(10) == false
					        && Hardware.rightOperator
					                .getRawButton(11) == false)
					{
						fireRequested = true;
						Hardware.armOutOfWayTimer.reset();
						Hardware.armOutOfWayTimer.start();
						isFiringByCamera = false;
					}
					currentCameraReturn =
					        Drive.alignByCameraReturn.WORKING;
				}
				// cancel the align request if the right operator presses buttons 10 and
				// 11 at the same time.
				if (currentCameraReturn == Drive.alignByCameraReturn.CANCELLED
				        || currentCameraReturn == Drive.alignByCameraReturn.NO_BLOBS_FOUND)//@AHK test low priority
				{
					isAligningByCamera = false;
					currentCameraReturn =
					        Drive.alignByCameraReturn.WORKING;
					//testingAlignByCamera = false;
					fireRequested = false;
					isFiringByCamera = false;
				}
			}
			// end alignByCameraBlock

			// ----------------------------
			// block of code to fire
			// ----------------------------
			if (Hardware.leftOperator.getTrigger() == true)
			{
				// Tell the code to start firing
				fireRequested = true;

				Hardware.armOutOfWayTimer.start();

			}
			// if the override button is pressed and we want to fire
			if (Hardware.leftOperator
			        .getRawButton(FIRE_OVERRIDE_BUTTON) == true
			        && fireRequested == true)
			{
				// FIRE NO MATTER WHAT!!!!!
				if (fire(3, true) == true)
				{
					// We've shot our ball, we don't want to fire anymore.
					// isFiringByCamera = false;
					fireRequested = false;
				}
			}
			// If the drivers decided they were being stupid and we don't want to
			// fire anymore
			if (Hardware.leftOperator
			        .getRawButton(FIRE_CANCEL_BUTTON) == true)
			{
				// Stop asking the code to fire
				isFiringByCamera = false;
				fireRequested = false;
			}
			// if we want to fire, the arm is out of the way, and we have enough
			// pressure so we don't hurt ourselves.
			if (fireRequested == true
			        && Hardware.pickupArm.moveToPosition(
			                ManipulatorArm.ArmPosition.CLEAR_OF_FIRING_ARM) == true
			        && Hardware.armOutOfWayTimer
			                .get() >= ARM_IS_OUT_OF_WAY_TIME
			        && Hardware.leftOperator
			                .getRawButton(FIRE_OVERRIDE_BUTTON) != true)
			{
				if (Hardware.armIR.isOn() == true)
				{
					if (Hardware.pickupArm
					        .moveToPosition(ArmPosition.DEPOSIT))
					{
						Hardware.pickupArm.pullInBall(true);
					}
				}
				else
				{// fire, if we're ready to
					if (fire(3, false) == true)
					{
						// if we're done firing, drop the request
						fireRequested = false;
						isFiringByCamera = false;
						Hardware.armOutOfWayTimer.stop();
						Hardware.armOutOfWayTimer.reset();
					}
				}
			}


			// end fire block

			// block of code to tell the drivers where to go
			// TODO finish based on camera input and IR sensors
			// if the rightIR detects HDPE and the left one doesn't
			// if one of the IR's detect HDPE
			// if (Hardware.rightIR.isOn() == true
			// || Hardware.leftIR.isOn() == true)
			// {
			// //Tell the drivers to stop and hopefully alignByCamera
			//// Hardware.arrowDashboard
			//// .setDirection(Guidance.Direction.linedUp);
			// if (processingImage == false)
			// {
			// Hardware.cameraInTeleopTimer.start();
			// Hardware.axisCamera.writeBrightness(
			// Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
			// //Woah, that's too dark! Someone turn on the ringlight!
			// Hardware.ringLightRelay.set(Value.kOn);
			// processingImage = true;
			// }
			// if (processingImage == true
			// && Hardware.cameraInTeleopTimer.get() >= .25)
			// {
			// try
			// {
			// Hardware.imageProcessor
			// .updateImage(Hardware.axisCamera.getImage());
			// }
			// //This is NI yelling at us for something being wrong
			// catch (NIVisionException e)
			// {
			// //if something wrong happens, tell the stupid programmers
			// //who let it happen more information about where it came from
			// e.printStackTrace();
			// }
			// //tell imageProcessor to use the image we just took to look for
			// //blobs
			// Hardware.imageProcessor.updateParticleAnalysisReports();
			// if(Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x
			// <=70)
			// {
			// Hardware.arrowDashboard.setDirection(Guidance.Direction.left);
			// }
			// else
			// if(Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x
			// >= 90)
			// {
			// Hardware.arrowDashboard.setDirection(Guidance.Direction.right);
			// }
			// else
			// {
			// Hardware.arrowDashboard.setDirection(Guidance.Direction.linedUp);
			// }
			// }
			// }
			// //If neither IR detects anything on the ground
			// else
			// {
			// //trust the camera
			// //TODO base these ones on the camera if we have one.
			// Hardware.arrowDashboard
			// .setDirection(Guidance.Direction.neutral);
			// }
			// //put the arrows on the screen
			// Hardware.arrowDashboard.update();


			// If the ball is in the robot, update the driver station with
			// that info. This will also light up the Driver Station green
			// when it updates the status.
			if (Hardware.armIR.isOn() == true)
			{
				Guidance.updateBallStatus(true);
			}
			else
			{
				Guidance.updateBallStatus(false);
			}


			// End driver direction block



			// Print statements to test Hardware on the Robot
			// printStatements();


			// Takes Pictures based on Operator Button stuff.
			takePicture();


			// Driving the Robot

			// If we press the brake button, robot brakes
			/*
			 * if (Hardware.leftDriver
			 * .getRawButton(BRAKE_JOYSTICK_BUTTON_FIVE) == true)
			 * {
			 * Hardware.transmission.setJoystickDeadbandRange(0.0);
			 * Hardware.drive.driveContinuous(LEFT_MOTOR_BRAKE_SPEED,
			 * RIGHT_MOTOR_BRAKE_SPEED);
			 * }
			 * 
			 * else if (Hardware.leftDriver
			 * .getRawButton(BRAKE_JOYSTICK_BUTTON_FOUR) == true)
			 * {
			 * Hardware.transmission.setJoystickDeadbandRange(0.0);
			 * Hardware.drive.driveContinuous(LEFT_MOTOR_BRAKE_SPEED_TWO,
			 * RIGHT_MOTOR_BRAKE_SPEED_TWO);
			 * }
			 */

			//when brake button is pressed motor values reverse
			loopCounter++; //adds one every time teleop loops

			//checks to see if the left driver button 5 is being pressed
			if (Hardware.leftDriver
			        .getRawButton(BRAKE_JOYSTICK_BUTTON_FOUR) == true)
			{
				//determines what number loop teleop is in, sets motors to 
				//a positive number, and sets deadband to zero
				//				if (loopCounter % BRAKING_INTERVAL < BRAKING_INTERVAL
				//				        / 2)
				{
					Hardware.transmission.setJoystickDeadbandRange(0.0);
					//					Hardware.drive.driveContinuous(MOTOR_HOLD_SPEED,
					//					        MOTOR_HOLD_SPEED);
					Hardware.rightRearMotor.set(-.14);//TODO check
					Hardware.rightFrontMotor.set(-.14);
					Hardware.leftRearMotor.set(-.14);
					Hardware.leftFrontMotor.set(-.14);
				}
			}
			//determines what number loop teleop is in then sets motors 
			//to a negative number
			//else
			else if (Hardware.leftDriver
			        .getRawButton(BRAKE_JOYSTICK_BUTTON_FIVE))
			{
				Hardware.transmission.setJoystickDeadbandRange(0.0);
				//					Hardware.drive.driveContinuous(-MOTOR_HOLD_SPEED,
				//					        -MOTOR_HOLD_SPEED);
				Hardware.rightRearMotor.set(.14);//TODO check directions.
				Hardware.rightFrontMotor.set(.14);
				Hardware.leftRearMotor.set(.14);
				Hardware.leftFrontMotor.set(.14);
			}
			//}
			//sets deadband back to 20%
			else
			{
				Hardware.transmission.setJoystickDeadbandRange(.20);
			}

			//drive the robot with the joysticks

			// TODO delete all conditionals. Fix brake
			if (/* speedTesting == false && */ isAligningByCamera == false
			        && testingAlignByCamera == false
			        && fireRequested == false && Hardware.leftDriver
			                .getRawButton(
			                        BRAKE_JOYSTICK_BUTTON_FIVE) == false
			        && Hardware.leftDriver.getRawButton(
			                BRAKE_JOYSTICK_BUTTON_FOUR) == false
			        && isTurning180Degrees == false)
			{
				driveRobot();
			}

			if (Hardware.leftOperator.getRawButton(9))
			{
				Hardware.axisCamera.writeBrightness(
				        Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
			}
			//@AHK uncomment
			//			else
			//			{
			//				Hardware.axisCamera.writeBrightness(
			//				        Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
			//			}


			// If the camera is up,
			if (Hardware.cameraSolenoid
			        .get() == DoubleSolenoid.Value.kReverse)
			// the light is on.
			{
				Hardware.ringLightRelay.set(Relay.Value.kOn);
			}
			else
			{
				Hardware.ringLightRelay.set(Relay.Value.kOff);
			}

		}
	} // end Periodic

	/* private static boolean isSpeedTesting = false */;



	/**
	 * Hand the transmission class the joystick values and motor controllers for
	 * four wheel drive.
	 * 
	 */
	public static void driveRobot ()
	{
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
	 *            When in toggle mode, one boolean raises the arm and one
	 *            lowers.
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
	// public static void runCameraSolenoid (boolean upState,
	// boolean downState, boolean holdState, boolean toggle)
	// {
	// if (upState && toggle == true && armIsUp == false)
	// {
	// Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
	// armIsUp = true;
	// }
	// else if (downState && toggle == true && armIsUp == true)
	// {
	// Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kReverse);
	// armIsUp = false;
	// }
	// else if (holdState && toggle == false)
	// {
	// Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kForward);
	// }
	// else
	// {
	// Hardware.cameraSolenoid.set(DoubleSolenoid.Value.kReverse);
	// }
	// }

	/**
	 * Fires the catapult.
	 * 
	 * @param power
	 *            -Can be 1, 2, or 3; corresponds to the amount of solenoids
	 *            used to
	 *            fire.
	 * @return
	 *         -False if we're not yet done firing, true otherwise.
	 */
	public static boolean fire (int power, boolean override)
	{
		if (Hardware.transducer.get() >= FIRING_MIN_PSI
		        || override == true)
		{
			// if (Hardware.pickupArm.moveToPosition(
			// ManipulatorArm.ArmPosition.CLEAR_OF_FIRING_ARM) == true)
			// {
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
			// }
		}
		// TODO reduce time to minimum possible
		if (Hardware.fireTimer.get() >= .5)// .5
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
				//				Hardware.axisCamera.writeBrightness(
				//				        Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
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
			// Hardware.axisCamera.writeBrightness(
			// Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
			// Hardware.ringLightRelay.set(Value.kOff);
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
		// if (Hardware.leftOperator.getTrigger() == true)
		// {
		// if (processingImage == true)
		// {
		// processImage();
		// processingImage = false;
		// }
		// }
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
	 * stores print statements for future use in the print "bank", statements
	 * are
	 * commented out when
	 * not in use, when you write a new print statement, "deposit" the statement
	 * in
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
		// System.out.println("AligningByCamera = " + isAligningByCamera);
		// checks to see if the robot is aligning by camera

		// Joysticks------------
		// System.out.println("Left Joystick: " + Hardware.leftDriver.getY());
		// System.out
		// .println("Right Joystick: " + Hardware.rightDriver.getY());
		// System.out.println("Left Operator: " + Hardware.leftOperator.getY());
		// System.out.println("Right Operator: " + Hardware.rightOperator.getY());

		// IR sensors-----------
		// System.out.println("left IR = " + Hardware.leftIR.isOn());
		// System.out.println("right IR = " + Hardware.rightIR.isOn());
		// System.out.println("Has ball IR = " + Hardware.armIR.isOn());




		// pots-----------------
		// System.out.println(
		// "delay pot = " + (int) Hardware.delayPot.get());
		// prints the value of the transducer- (range in code is 50)
		// hits psi of 100 accurately
		// System.out.println("transducer = " + Hardware.transducer.get());
		//System.out.println("Arm Pot = " + Hardware.armPot.get());

		// Motor controllers-----
		// prints value of the motors
		// System.out.println("RR Motor T = " + Hardware.rightRearMotor.get());
		// System.out.println("LR Motor T = " + Hardware.leftRearMotor.get());
		// System.out
		// .println("RF Motor T = " + Hardware.rightFrontMotor.get());
		// System.out.println("LF Motor T = " + Hardware.leftFrontMotor.get());
		// System.out.println("Arm Motor: " + Hardware.armMotor.get());
		// System.out
		// .println("Intake Motor: " + Hardware.armIntakeMotor.get());

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

		// System.out.println(
		// "Right Rear Encoder Tics: "
		// + Hardware.rightRearEncoder.get());
		// System.out.println(
		// "Left Rear Encoder Tics: "
		//    // + Hardware.leftRearEncoder.get());
		//		System.out.println(
		//		        "RR distance = "
		//		                + Hardware.rightRearEncoder.getDistance());
		//		System.out.println(
		//		        "LR distance = "
		//		                + Hardware.leftRearEncoder.getDistance());


		// Encoders-------------
		// System.out.println(
		// "RR distance = " + Hardware.rightRearEncoder.getDistance());
		// System.out.println(
		// "LR distance = " + Hardware.leftRearEncoder.getDistance());
		// System.out.println("Arm Motor = " + Hardware.armMotor.getDistance());

		// Switches--------------
		// prints state of switches
		// System.out.println("Autonomous Enabled Switch: " +
		// Hardware.autonomousEnabled.isOn());
		//		System.out.println(
		//		        "Comp Switch: " + Hardware.inCompetition.isOn());
		//		System.out.println("Demo Switch: " + Hardware.inDemo.isOn());


		// print the position of the 6 position switch------------
		// System.out.println("Position: " +
		// Hardware.startingPositionDial.getPosition());

		// print the position of the 6 position switch------------
		// System.out.println("Position: " +
		// Hardware.startingPositionDial.getPosition());

		// Relay-----------------
		// System.out.println(Hardware.ringLightRelay.get());

		// ImageProcessing-------
		// System.out.println("Number of seen blobs:"
		// + Hardware.imageProcessor.getNumBlobs());

		//Ultrasonic
		//		System.out.println("Ultrasonic Dist: "
		//		        + Hardware.ultrasonic.getRefinedDistanceValue());
	} // end printStatements


	/*
	 * ===============================================
	 * Constants
	 * ===============================================
	 */

	private static final double MAXIMUM_TELEOP_SPEED = 1.0;

	private static final double CAMERA_ALIGN_Y_DEADBAND = .10;

	private static final double CAMERA_ALIGN_X_DEADBAND = .10;

	public static final double CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER =
	        -.365;//-.375

	public static final double CAMERA_Y_AXIS_ADJUSTED_PROPORTIONAL_CENTER =
	        -.182;//-.192

	private static final double ALIGN_BY_CAMERA_TURNING_SPEED = .5;//@AHK .5

	private static final double ALIGN_BY_CAMERA_DRIVE_SPEED = .45;
	// right driver 3
	private static final int GEAR_UPSHIFT_JOYSTICK_BUTTON = 3;
	// right driver 2
	private static final int GEAR_DOWNSHIFT_JOYSTICK_BUTTON = 2;
	// left driver 4
	private static final int BRAKE_JOYSTICK_BUTTON_FOUR = 4;
	// left driver 5
	private static final int BRAKE_JOYSTICK_BUTTON_FIVE = 5;
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

	private final static double PERCENT_IMAGE_PROCESSING_DEADBAND = .13;

	private final static double CAMERA_ALIGNMENT_TURNING_SPEED = .50;// .55

	private final static double ARM_IS_OUT_OF_WAY_TIME = .10;

	private final static int BRAKING_INTERVAL = 4;

	private final static double MOTOR_HOLD_SPEED = 0.1;

	// minimum pressure when allowed to fire
	private static final int FIRING_MIN_PSI = 90;

	// ==========================================
	// TUNEABLES
	// ==========================================

	private static boolean testingAlignByCamera = false;

	private static boolean isAligningByCamera = false;

	private static Drive.alignByCameraReturn currentCameraReturn =
	        Drive.alignByCameraReturn.WORKING;

	private static boolean isFiringByCamera = false;

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

	// Stores temporarily whether firingState is true, for use in whether the arm is
	// in the way
	private static boolean storeFiringState;

	private static int loopCounter = 0;

} // end class
