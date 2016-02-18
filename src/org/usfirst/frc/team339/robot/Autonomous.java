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
// Init() - Initialization code for autonomous mode
// should go here. Will be called each time the robot enters
// autonomous mode.
// -----------------------------------------------------
// Periodic() - Periodic code for autonomous mode should
// go here. Will be called periodically at a regular rate while
// the robot is in autonomous mode.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package org.usfirst.frc.team339.robot;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.Utils.ErrorMessage;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;

/**
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */

/**
 * A new and improved Autonomous class.
 * The class <b>beautifully</b> uses nested state machines in order to execute
 * methods during the Autonomous period.
 * I really hope the autoformatting doesn't mess it up.
 * TODO: "make it worky".
 * 
 * @author Michael Andrzej Klaczynski
 * @written ath the eleventh stroke of midnight, the 28th of January, Year of
 *          our LORD 2015
 */
public class Autonomous
{

/**
 * The overarching states of autonomous mode.
 */
private static enum MainState
	{
	/**
	 * The first state.
	 * Initializes things if necessary,
	 * though most things are initialized
	 * in init().
	 */
	INIT, // beginning, check conditions
	/**
	 * Sets arm to head downward.
	 */
	BEGIN_LOWERING_ARM,
	/**
	 * Moves at a low speed while lowering arm.
	 * If it reaches the end of the distance, and the arm is not fully down,
	 * skips to DONE.
	 */
	LOWER_ARM_AND_MOVE,//
	/**
	 * Resets and starts delay timer.
	 */
	INIT_DELAY,// sets delay timer.
	/**
	 * Waits.
	 * Waits until the delay is up.
	 */
	DELAY, // waits, depending on settings.
	/**
	 * This state checks to see if we are in lane 1.
	 * If so, we go until we reach an encoder distance (set to distance to
	 * alignment tape),
	 * Else, we go util the sensors find the Alignment tape.
	 */
	FORWARDS_BASED_ON_ENCODERS_OR_IR, // decides based on lane whether to move
										// to tape based on encoders or IR
	/**
	 * Go the distance over the outer works.
	 */
	FORWARDS_OVER_OUTER_WORKS,

	/**
	 * Goes forward until it reaches the set distance to the Alignment tape.
	 */
	FORWARDS_TO_TAPE_BY_DISTANCE, // drives the distance required to the tape.
	/**
	 * Goes forward until it senses the Alignment tape.
	 */
	FORWARDS_UNTIL_TAPE, // drives forwards until detection of the gaffers'
						// tape.
	/**
	 * Upon reaching the Alignment line, sometimes we must rotate.
	 */
	ROTATE_ON_ALIGNMENT_LINE, // rotates on the alignment line.
	/**
	 * After reaching the A-line, or after rotating upon reaching it, drives
	 * towards a position in front of the goal.
	 */
	FORWARDS_FROM_ALIGNMENT_LINE, // drives from the alignment line.
	/**
	 * After reaching a spot in front of the goal, we turn to face it.
	 */
	TURN_TO_FACE_GOAL, // rotates toward the goal.
	/**
	 * Once we are facing the goal, we may sometimes drive forwards.
	 */
	DRIVE_UP_TO_GOAL, // drives up the goal.
	/**
	 * Once we are in the shooting position, we align based on the chimera.
	 */
	ALIGN_IN_FRONT_OF_GOAL,
	/**
	 * We shoot the cannon ball.
	 */
	SHOOT, // adjusts its self (?) and fires the cannon ball.
	/**
	 * We stop, and do nothing else.
	 */
	DONE
	}



private static enum MoveWhileLoweringArmReturn
	{
	NOT_DONE, DONE, FAILED
	}

/**
 * 
 * States to run arm movements in parallel.
 *
 */
private static enum ArmState
	{
	/**
	 * Begins moving the arm in a downwards/down-to-the-floor action fashion.
	 */
	INIT_DOWN,
	/**
	 * Czecks to see if the arm is all the way down.
	 */
	CHECK_DOWN,
	/**
	 * Begins moving the arm in a upwards/up-to-the-shooter action fashion.
	 */
	INIT_UP,
	/**
	 * Czecks to see if the arm is all the way up.
	 */
	CHECK_UP,
	/**
	 * Begins moving up, with full intention of releasing the ball.
	 */
	INIT_UP_AND_DEPOSIT,
	/**
	 * Czecks to see if the arm is all the way up, so that we may deposit.
	 */
	CHECK_UP_TO_DEPOSIT,
	/**
	 * Begins spinning its wheels so as to spit out the cannon ball.
	 */
	INIT_DEPOSIT,
	/**
	 * Have we spit out the cannon ball? If so, INIT_DOWN.
	 */
	DEPOSIT,
	/**
	 * Do nothing, but set armStatesOn to false.
	 */
	DONE
	}


// ==========================================
// AUTO STATES
// ==========================================
private static MainState mainState = MainState.INIT;

/**
 * Used to run arm movements in parallel to the main state machine.
 */
private static ArmState armState = ArmState.DONE;

// ==================================
// VARIABLES
// ==================================
private static boolean enabled;

/**
 * Time to delay at beginning. 0-3 seconds
 */
private static double delay; // time to delay before beginning.

/**
 * Number of our starting position, and path further on.
 */
private static int lane;

/**
 * Run the arm state machine only when necessary (when true).
 */
private static boolean runArmStates = false;
/**
 * Prints print that it prints prints while it prints true.
 */
private static boolean debug;

// ==========================================
// TUNEABLES
// ==========================================

/**
 * User-Initialization code for autonomous mode should go here. Will be
 * called once when the robot enters autonomous mode.
 *
 * @author Nathanial Lydick
 *
 * @written Jan 13, 2015
 */
public static void init ()
{

	enabled = Hardware.autonomousEnabled.isOn();

	// set the delay time based on potentiometer.
	delay = initDelayTime();

	// get the lane based off of startingPositionPotentiometer
	lane = getLane();

	debug = DEBUGGING_DEFAULT;

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
	Hardware.leftRearEncoder.setDistancePerPulse(0.019706);
	Hardware.rightRearEncoder.setDistancePerPulse(0.019706);
	Hardware.rightRearEncoder.reset();

	// -------------------------------------
	// close both of the cameras in case they
	// were previously started in a previous
	// run. Then, change the camera to one that
	// will eventually process images.
	// ------------------------------------

	// Sets FPS and Resolution of camera
	Hardware.axisCamera.writeMaxFPS(15);
	Hardware.axisCamera.writeResolution(Resolution.k320x240);
	Hardware.axisCamera
	        .writeBrightness(Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);

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
	// Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);

	Hardware.errorMessage.clearErrorlog();

} // end Init

/**
 * User Periodic code for autonomous mode should go here. Will be called
 * periodically at a regular rate while the robot is in autonomous mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void periodic ()
{

	// test
	// TransmissionFourWheel debugTrans = Hardware.transmissionFourWheel;
	// moveToShootingPositionStep = MoveToShootingPositionStep.FORWARDS_ONE;
	// System.out.println("Time: " + Hardware.kilroyTimer.get());

	// Checks the "enabled" switch.
	if (enabled == true)
	{
	// runs the overarching state machine.
	runMainStateMachine();
	}

	// Czecks if we are running any arm functions.
	if (runArmStates == true)
	//run the arm state machine.
	{
	runArmStates();
	}


} // end Periodic


/**
 * Sets the delay time in full seconds based on potentiometer.
 */
private static int initDelayTime ()
{
	return (int) (MAXIMUM_DELAY * Hardware.delayPot.get()
	        / Hardware.DELAY_POT_DEGREES);
}


/**
 * Called periodically to run the overarching states.
 */
private static void runMainStateMachine ()
{

	if (debug == true)
	// print out states.
	{
	System.out.println("Main State: " + mainState);
	Teleop.printStatements();
	Hardware.errorMessage.printError(
	        "Main State: " + mainState,
	        ErrorMessage.PrintsTo.roboRIO);
	Hardware.errorMessage.printError(
	        "Left:" + Hardware.leftRearEncoder.getDistance(),
	        ErrorMessage.PrintsTo.roboRIO);
	Hardware.errorMessage.printError(
	        "Right:" + Hardware.rightRearEncoder.getDistance(),
	        ErrorMessage.PrintsTo.roboRIO);
	}

	// Temporary. Print gear percentage. TODO: Remove.
	// System.out.println("First gear percentage = "
	// + Hardware.transmission.getFirstGearPercentage());

	switch (mainState)
	{
		case INIT:
			// Doesn't do much.
			// Just a Platypus.
			mainInit();

			if (lane == 1)
			// lower the arm to pass beneath the bar.
			{
			mainState = MainState.BEGIN_LOWERING_ARM;
			}
			else
			// lowering the arm would get in the way. Skip to delay.
			{
			mainState = MainState.INIT_DELAY;
			}

			// temporary; for testing. TODO: remove.
			// mainState = MainState.ROTATE_ON_ALIGNMENT_LINE;
			break;


		case BEGIN_LOWERING_ARM:
			// starts the arm motor
			beginLoweringArm();
			// goes into initDelay
			mainState = MainState.INIT_DELAY;
			break;

		case LOWER_ARM_AND_MOVE:
			// goes forwards to outer works.
			switch (hasLoweredArmAndMoved())
			{

				case DONE:
					// Goes to Accelerate when done
					mainState =
					        MainState.FORWARDS_OVER_OUTER_WORKS;
					resetEncoders();
					Hardware.kilroyTimer.stop();
					break;
				case FAILED:
					// Unless arm is not down. In that case, stop everything.
					mainState = MainState.DONE;
					break;

				default:
				case NOT_DONE:
					// continue
					mainState = MainState.LOWER_ARM_AND_MOVE;
					break;
			}
			break;

		case INIT_DELAY:
			// reset and start timer
			initDelay();
			// run DELAY state.
			mainState = MainState.DELAY;
			break;

		case DELAY:
			// check whether done or not until done.
			if (delayIsDone() == true)
			// go to move forwards while lowering arm when finished.
			{
			mainState = MainState.LOWER_ARM_AND_MOVE;
			}
			break;

		case FORWARDS_OVER_OUTER_WORKS:
			//Drive over Outer Works.
			if (Hardware.drive.driveForwardInches(
			        DriveInformation.DISTANCE_OVER_OUTER_WORKS, false,
			        DriveInformation.OUTER_WORKS_MOTOR_RATIO,
			        DriveInformation.OUTER_WORKS_MOTOR_RATIO) == true)
			//put up all the things we had to put down under the low bar.
			//begin loading the catapult.
			{

			//put up camera.
			Hardware.cameraSolenoid.set(Value.kForward);

			//initiate the arm motion.
			runArmStates = true;
			armState = ArmState.INIT_UP_AND_DEPOSIT;

			mainState = MainState.FORWARDS_BASED_ON_ENCODERS_OR_IR;
			}
			break;

		case FORWARDS_BASED_ON_ENCODERS_OR_IR:
			// Check if we are in lane One.
			if (isInLaneOne() == true)
			// If so, move forwards the distance to the A-tape.
			{
			mainState = MainState.FORWARDS_TO_TAPE_BY_DISTANCE;
			}
			else
			// If in another lane, move forwards until we detect the A-tape.
			{
			mainState = MainState.FORWARDS_UNTIL_TAPE;
			}
			break;

		case FORWARDS_TO_TAPE_BY_DISTANCE:
			// Drive the distance from outer works to A-Line.
			if (hasDrivenToTapeByDistance() == true)
			// when done, proceed from Alignment line.
			{
			//reset Encoders to prepare for next state.
			resetEncoders();



			//We definitely don't need to rotate.
			mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			}
			break;


		case FORWARDS_UNTIL_TAPE:
			// Drive until IR sensors pick up tape.
			if (hasMovedToTape() == true)
			{
			//reset Encoders to prepare for next state.
			resetEncoders();

			// When done, possibly rotate.
			mainState = MainState.ROTATE_ON_ALIGNMENT_LINE;
			}
			break;

		case ROTATE_ON_ALIGNMENT_LINE:
			//Rotates until]l we are pointed at the place where we want to shoot.
			if (hasRotatedTowardsShootingPosition() == true)
			{
			//reset Encoders to prepare for next state.
			resetEncoders();
			//then move.
			mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			}
			break;

		case FORWARDS_FROM_ALIGNMENT_LINE:
			if (hasMovedFowardsFromTape() == true)
			{
			//reset Encoders to prepare for next state.
			resetEncoders();
			mainState = MainState.TURN_TO_FACE_GOAL;
			}
			break;

		case TURN_TO_FACE_GOAL:
			//Turns until we are facing the goal.
			if (hasTurnedToFaceGoal() == true)
			//when done move up to the batter.
			{
			//reset Encoders to prepare for next state
			resetEncoders();
			//then drive.
			mainState = MainState.DRIVE_UP_TO_GOAL;
			}
			break;

		case DRIVE_UP_TO_GOAL:
			//Moves to goal. Stops to align.
			if (hasDrivenUpToGoal() == true)
			//Go to align.
			{
			//reset Encoders to prepare for next state.
			resetEncoders();
			//go to align.
			mainState = MainState.ALIGN_IN_FRONT_OF_GOAL;
			}
			break;

		case ALIGN_IN_FRONT_OF_GOAL:
		//align based on the camera until we are facing the goal. head-on.
		//TODO: uncomment once this method exists.
		//	if (Hardware.drive.alignByCamera())
		//Once we are in position, we shoot!
			{
			mainState = MainState.SHOOT;
			}
			break;

		case SHOOT:
			//FIRE!!!
			shoot();
			mainState = MainState.DONE;
			break;

		default:
		case DONE:
			done();
			break;
	}
}

/*
 * ======================================
 * MAIN AUTONOMOUS STATE METHODS
 * ======================================
 */

private static void mainInit ()
{

	Hardware.kilroyTimer.reset();
	Hardware.kilroyTimer.start();
}

/**
 * Starts the arm motor downwards and resets the associated encoder.
 */
private static void beginLoweringArm ()
{
	Hardware.armMotor.set(1.0);

}

// TODO move to manipulatorArm class
/**
 * Checks whether or not the pickup arm has been lowered.
 * TODO: Move to ManipulatorArm class?
 * 
 * @return true if it is down.
 */
private static boolean armIsLowered ()
{
	// false by default.
	boolean done = false;

	if (Hardware.armPot.get() >= ARM_DOWN_DEGREES)
	// The arm IS down.
	{
	done = true;
	}

	return done;
}

/**
 * Moves forwards returns DONE when has reached the distance, however,
 * if the arm is not down by the distance it returns FAILED.
 * 
 * @return
 */
private static MoveWhileLoweringArmReturn hasLoweredArmAndMoved ()
{
	MoveWhileLoweringArmReturn returnStatus =
	        MoveWhileLoweringArmReturn.NOT_DONE;

	// the status of the arm being down
	boolean armIsDown = armIsLowered();

	if (armIsDown == true)
	{
	// stop the arm.
	Hardware.pickupArm.move(0.0);
	}


	// We can do cat D and B no problem. C is out. A may require extra arm code.

	// Go forth.
	if ((Hardware.drive.driveForwardInches(
	        DriveInformation.DISTANCE_TO_OUTER_WORKS
	                * LAB_SCALING_FACTOR,
	        false,
	        DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane],
	        DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane])) == true)
	// The distance has been reached. Now check the arm's status.
	{

	if (lane == 1 && armIsDown == false)
	// We want the arm to be down, but it is not.
	// Return FAILED, just to be safe.
	{
	returnStatus = MoveWhileLoweringArmReturn.FAILED;
	}
	else
	// The arm is down and we have reached the distance. Go on.
	{
	returnStatus = MoveWhileLoweringArmReturn.DONE;
	}
	}


	return returnStatus;
}


/**
 * Starts the delay timer.
 */
private static void initDelay ()
{
	Hardware.delayTimer.reset();
	Hardware.delayTimer.start();

}

/**
 * Waits.
 * One of the overarching states.
 */
private static boolean delayIsDone ()
{
	boolean done = false;

	// stop.
	Hardware.transmission.controls(0.0, 0.0);

	// check timer
	if (Hardware.delayTimer.get() > delay)
	// return true. stop and reset timer.
	{
	done = true;
	Hardware.delayTimer.stop();
	Hardware.delayTimer.reset();
	}

	if (armIsLowered() == true)
	{
	Hardware.pickupArm.move(0.0);
	}

	return done;

}



/**
 * Returns the Oneness of the lane.
 * 
 * @return true if in lane one.
 */
private static boolean isInLaneOne ()
{
	// the state of being in lane 1.
	boolean oneness;


	if (lane == 1)
	// we are in lane 1
	{
	oneness = true;
	}
	else
	// we are not
	{
	oneness = false;
	}


	return oneness;
}

/**
 * Drives the distance from outer works to alignment line.
 * 
 * @return when done.
 */
private static boolean hasDrivenToTapeByDistance ()
{

	boolean hasReachedDistance = false;


	// Drive forwards.
	if (Hardware.drive.driveForwardInches(
	        DriveInformation.DISTANCE_TO_TAPE * LAB_SCALING_FACTOR,
	        false, DriveInformation.MOTOR_RATIO_TO_A_LINE[lane],
	        DriveInformation.MOTOR_RATIO_TO_A_LINE[lane]) == true)
	// If we have reached the desired distance, return true.
	{
	hasReachedDistance = true;
	}


	return hasReachedDistance;
}

/**
 * Drives, and
 * Checks to see if the IRSensors detect Alignment tape.
 * 
 * @return true when it does.
 */
private static boolean hasMovedToTape ()
{

	boolean tapeness = false;

	// Move forwards.
	// TODO: make/use method to drive continuously.
	// TODO: set a good speed.
	Hardware.drive.driveContinuous();

	// simply check if we have detected the tape on either side.
	if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
	// we are done here.
	{
	tapeness = true;
	}


	return tapeness;

}

/**
 * Moves from the alignment line towards the goal.
 * 
 * @return
 */
private static boolean hasMovedFowardsFromTape ()
{
	boolean done = false;

	// Drive the distance from the tape to the line normal to the goal.
	if (Hardware.drive.driveForwardInches(
	        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE[lane]
	                * LAB_SCALING_FACTOR,
	        true,
	        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane],
	        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane]) == true)
	{
	done = true;
	}

	return done;
}

/**
 * Rotates on the alignment line to face the final shooting position.
 * 
 * @return
 */
private static boolean hasRotatedTowardsShootingPosition ()
{
	boolean done = false;

	// Turn the degrees specified.
	// TODO: Add more parameters to this method.
	done = hasTurnedBasedOnSign(
	        DriveInformation.ROTATE_ON_ALIGNMENT_LINE_DISTANCE[lane]
	                * LAB_SCALING_FACTOR);

	return done;
}

/**
 * Rotates to face the goal.
 * 
 * @return true when complete.
 */
private static boolean hasTurnedToFaceGoal ()
{
	boolean done = false;

	// Turn the degrees specified.
	// TODO: Add more parameters to this method.
	done = hasTurnedBasedOnSign(
	        DriveInformation.TURN_TO_FACE_GOAL_DEGREES[lane]
	                * LAB_SCALING_FACTOR);

	return done;
}

/**
 * Drives to the final shooting position.
 * 
 * @return true when complete.
 */
private static boolean hasDrivenUpToGoal ()
{
	boolean done = false;

	// Have we reached the distance according to drawings.
	// OR
	// Have we seen if we have reached cleats of the tower according to IR?
	if ((Hardware.drive.driveForwardInches(
	        DriveInformation.DRIVE_UP_TO_GOAL[lane]
	                * LAB_SCALING_FACTOR,
	        true, DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane],
	        DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane]) == true)
	        ||
	        (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
	// We are done here.
	{
	done = true;
	}

	// TEMPORARY PRINTS.
	// see if we have stopped based on IR or Encoders.
	if (done == true
	        && (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
	{
	System.out.println("Stopped by Sensors");
	}
	else
	{
	System.out.println("Stopped by distance.");
	}
	return done;
}

/**
 * Shoots the ball. May want to add states/methods to align.
 * 
 */
private static void shoot ()
{

	// TODO: write method to shoot cannonball.

}

/**
 * Stop everything.
 */
private static void done ()
{
	enabled = false;
	debug = false;
	Hardware.transmission.controls(0.0, 0.0);
	Hardware.armMotor.set(0.0);
	Hardware.delayTimer.stop();
}

/*
 * =============================================
 * END OF MAIN AUTONOMOUS STATE METHODS
 * =========================================
 */

/**
 * A separate state machine, used to run arm movements in parallel.
 */
private static void runArmStates ()
{
	switch (armState)
	{
		case INIT_DOWN:
			//begin moving arm down
			Hardware.pickupArm.move(1.0);
			//go to periodically check.
			armState = ArmState.CHECK_DOWN;
			break;
		case CHECK_DOWN:
			//check if down.
			if (Hardware.pickupArm.isDown() == true)
			//stop.
			{
			Hardware.pickupArm.move(0.0);
			armState = ArmState.DONE;
			}
			break;
		case INIT_UP:
			//begin moving arm up.
			Hardware.pickupArm.move(-1.0);
			//go to periotically check.
			armState = ArmState.CHECK_UP;
			break;
		case CHECK_UP:
			//check if up.
			if (Hardware.pickupArm.isUp() == true)
			{
			//stop.
			Hardware.pickupArm.move(0.0);
			armState = ArmState.DONE;
			}
			break;
		case INIT_UP_AND_DEPOSIT:
			//begin moving arm to depositing position.
			Hardware.pickupArm.move(-1.0);
			armState = ArmState.CHECK_UP_TO_DEPOSIT;
			break;
		case CHECK_UP_TO_DEPOSIT:
			//check is in up position so that we may deposit the ball.
			if (Hardware.pickupArm.isUp() == true)
			//stop, and go to deposit.
			{
			Hardware.pickupArm.move(0.0);
			armState = ArmState.INIT_DEPOSIT;
			}
			break;
		case INIT_DEPOSIT:
			//spin wheels to release ball.
			Hardware.pickupArm.pushOutBall();
			armState = ArmState.DEPOSIT;
			break;
		case DEPOSIT:
			//check if the ball is out.
			if (Hardware.pickupArm.ballIsOut())
			//stop rollers, and move down.
			{
			Hardware.pickupArm.stopIntakeArms();
			//get out of the way.
			armState = ArmState.INIT_DOWN;
			}
			break;
		default:
		case DONE:
			//stop running state machine.
			runArmStates = false;
			break;


	}
}



/**
 * Return the starting position based on 6-position switch on the robot.
 * 
 * @return lane/starting position
 */
private static int getLane ()
{
	int position = Hardware.startingPositionDial.getPosition();

	if (position == -1)
	{
	position = 0;
	}

	position = position + 1;

	return position;
}

/**
 * Reset left and right encoders.
 * To be called at the end of any state that uses Drive.
 */
private static void resetEncoders ()
{
	Hardware.leftRearEncoder.reset();
	Hardware.rightRearEncoder.reset();
}

/**
 * For turning in drive based on array of positive and negative values.
 * Use to turn a number of degrees
 * COUNTERCLOCKWISE.
 * Kilroy must turn along different paths.
 * You must use this to be versatile.
 * TODO: add parameters for break and speeds.
 */
private static boolean hasTurnedBasedOnSign (double degrees)
{
	boolean done = false;

	if (degrees < 0)
	//Turn right. Make degrees positive.
	{
	done = Hardware.drive.turnRightDegrees(-degrees, false, 0.28,
	        -0.28);
	}
	else
	//Turn left the given number of degrees.
	{
	done = Hardware.drive.turnLeftDegrees(degrees, false, -0.28, 0.28);
	}
	return done;

}

/**
 * Contains distances and speeds at which to drive.
 *
 */
private static final class DriveInformation
{

/**
 * The motor controller values for moving to the outer works.
 * As these are initial speeds, keep them low, to go easy on the motors.
 * Lane is indicated by index.
 */
static final double[] MOTOR_RATIO_TO_OUTER_WORKS =
        {
                0.0, // nothing. Not used. Arbitrary; makes it work.
                0.25, // lane 1, should be extra low.
                1.0, // lane 2
                0.4, // lane 3
                0.4, // lane 4
                0.4 // lane 5
        };

/**
 * "Speeds" at which to drive from the Outer Works to the Alignment Line.
 */
static final double[] MOTOR_RATIO_TO_A_LINE =
        {
                0.0,
                0.5,
                0.6,
                0.4,
                0.4,
                0.6
        };

/**
 * Distances to rotate upon reaching alignment line.
 * Lane is indicated by index.
 * Set to Zero for 1, 2, and 5.
 */
static final double[] ROTATE_ON_ALIGNMENT_LINE_DISTANCE =
        {
                0.0, // nothing. Not used. Arbitrary; makes it work.
                0.0, // lane 1 (not neccesary)
                0.0, // lane 2 (not neccesary)
                -20, // lane 3
                24.8, // lane 4
                0.0 // lane 5 (not neccesary)
        };

/**
 * Distances to drive after reaching alignment tape.
 * Lane is indicated by index.
 */
static final double[] FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE =
        {
                0.0, // nothing. Not used. Arbitrary; makes it work.
                74.7,// lane 1
                82.0,// lane 2
                64.0, // lane 3
                66.1,// lane 4
                86.5 // lane 5
        };

/**
 * "Speeds" at which to drive from the A-Line to the imaginary line normal to
 * the goal.
 */
static final double[] FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO =
        {
                0.0,
                0.4,
                0.4,
                0.3,
                0.3,
                0.4
        };

/**
 * Distances to rotate to face goal.
 */
static final double[] TURN_TO_FACE_GOAL_DEGREES =
        {
                0.0, // nothing. Not used. Arbitrary; makes it work.
                -60.0,// lane 1
                -60.0,// lane 2
                20.0,// lane 3
                -24.85,// lane 4
                60 // lane 5
        };


/**
 * Distances to travel once facing the goal.
 * Not neccesary for lanes 3 and 4; set to zero.
 * Actually, we may not use this much at all, given that we will probably just
 * use the IR to sense the cleets at the bottom of the tower.
 */
static final double[] DRIVE_UP_TO_GOAL =
        {
                0.0, // nothing. Not used. Arbitrary; makes it work.
                62.7,// lane 1
                52.9,// lane 2
                0.0,// lane 3 (not neccesary)
                0.0,// lane 4 (not neccesary)
                12.0 // lane 5
        };

/**
 * "Speeds" at which to drive to the Batter.
 */
static final double[] DRIVE_UP_TO_GOAL_MOTOR_RATIO =
        {
                0.0,
                0.35,
                0.4,
                0.4,
                0.4,
                0.4
        };

/**
 * Distance from Outer Works checkpoint to Alignment Line
 */
private static final double DISTANCE_TO_TAPE = 83.75;


/**
 * Distance to get the front of the robot to the Outer Works.
 */
private static final double DISTANCE_TO_OUTER_WORKS = 22.75;

/**
 * Distance to travel to get over the Outer Works.
 */
private static final double DISTANCE_OVER_OUTER_WORKS = 96.25;

private static final double OUTER_WORKS_MOTOR_RATIO = 0.4;

}


/*
 * . . . . . . . . . . . . __ _ __
 * // .................../ . . . . \
 * // ................./ --0 --- 0-- \
 * // .........++++.. |- - - | | - - -| ..++++
 * ===========//||\\==| . . / . \ . . |==//||\\======
 * //Constants\\||// .| . .| . . |. . |. \\||//
 * //======================| . . |==================
 * // ----------------------\___/
 * // ...............................|<!(r% ~@$ #3r3
 */

/**
 * Always 1.0. Do not change. The code depends on it.
 */
private static final double MAXIMUM_AUTONOMOUS_SPEED = 1.0;

/**
 * The maximum time to wait at the beginning of the match.
 * Used to scale the ratio given by the potentiometer.
 */
private static final double MAXIMUM_DELAY = 3.0;

/**
 * Potentiometer degrees for arm to be down.
 * TODO: set to empirical value.
 */
private static final double ARM_DOWN_DEGREES = 0.0;

/**
 * Set to true to print out print statements.
 */
private static final boolean DEBUGGING_DEFAULT = true;

/**
 * Factor by which to scale all distances for testing in our small lab space.
 */
private static final double LAB_SCALING_FACTOR = 0.5;

} // end class
