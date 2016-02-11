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


// ==========================================
// AUTO STATES
// ==========================================
private static MainState mainState = MainState.INIT;

// ==================================
// VARIABLES
// ==================================
private static boolean enabled;

/**
 * Time to delay at beginning. 0-3 seconds
 */
private static double delay; // time to delay before beginning.

private static int lane;

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

	Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);


	// -------------------------------------
	// motor initialization
	// -------------------------------------
	Hardware.leftRearMotorSafety.setSafetyEnabled(true);
	Hardware.rightRearMotorSafety.setSafetyEnabled(true);
	Hardware.leftFrontMotorSafety.setSafetyEnabled(true);
	Hardware.rightFrontMotorSafety.setSafetyEnabled(true);

	Hardware.transmission
	        .setFirstGearPercentage(MAXIMUM_AUTONOMOUS_SPEED);
	Hardware.transmission.setGear(1);
	Hardware.transmission.setJoysticksAreReversed(true);

	// --------------------------------------
	// Encoder Initialization
	// --------------------------------------
	Hardware.leftRearEncoder.reset();
	Hardware.leftRearEncoder.setDistancePerPulse(0.019706);
	Hardware.rightRearEncoder.setDistancePerPulse(0.019706);
	Hardware.rightRearEncoder.reset();
	Hardware.armEncoder.reset();

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
	Hardware.portArmIntakeMotor.set(0.0);
	Hardware.starboardArmIntakeMotor.set(0.0);

	Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);
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

	//Checks the "enabled" switch.
	if (enabled)
	{
	// runs the overarching state machine.
	runMainStateMachine();
	}
	// feed all motor safeties
	Hardware.leftRearMotorSafety.feed();
	Hardware.rightRearMotorSafety.feed();
	Hardware.leftFrontMotorSafety.feed();
	Hardware.rightFrontMotorSafety.feed();
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
	//print out states.
	{
	System.out.println("Main State: " + mainState);
	}

	switch (mainState)
	{
		case INIT:
			//Doesn't do much.
			mainInit();
			//mainState = MainState.BEGIN_LOWERING_ARM;
			//temporary; for testing
			mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			break;

		case BEGIN_LOWERING_ARM:
			//starts the arm motor
			beginLoweringArm();
			//goes into initDelay
			mainState = MainState.INIT_DELAY;
			break;

		case LOWER_ARM_AND_MOVE:
			//goes forwards to outer works.
			switch (hasLoweredArmAndMoved())
			{
				case NOT_DONE:
					//continue
					mainState = MainState.LOWER_ARM_AND_MOVE;
					break;
				case DONE:
					//Goes to Accelerate when done
					mainState =
					        MainState.FORWARDS_BASED_ON_ENCODERS_OR_IR;
					resetEncoders();
					break;
				case FAILED:
					//Unless arm is not down. In that case, stop everything.
					mainState = MainState.DONE;
					break;
			}
			break;

		case INIT_DELAY:
			//reset and start timer
			initDelay();
			//run DELAY state.
			mainState = MainState.DELAY;
			break;

		case DELAY:
			//check whether done or not until done.
			if (delayIsDone())
			// go to move forwards while lowering arm when finished.
			{
			mainState = MainState.LOWER_ARM_AND_MOVE;
			}
			break;

		case FORWARDS_BASED_ON_ENCODERS_OR_IR:
			//Check if we are in lane One.
			if (isInLaneOne())
			//If so, move forwards the distance to the A-tape.
			{
			mainState = MainState.FORWARDS_TO_TAPE_BY_DISTANCE;
			}
			else
			//If in another lane, move forwards until we detect the A-tape.
			{
			mainState = MainState.FORWARDS_UNTIL_TAPE;
			}
			break;

		case FORWARDS_TO_TAPE_BY_DISTANCE:
			// Drive the distance from outer works to A-Line.
			if (hasDrivenToTapeByDistance())
			//when done, proceed from Alignment line.
			{
			resetEncoders();
			mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			}
			break;

		case FORWARDS_UNTIL_TAPE:
			//Drive until IR sensors pick up tape.
			if (hasMovedToTape())
			{
			//When done, possibly rotate.
			resetEncoders();
			mainState = MainState.ROTATE_ON_ALIGNMENT_LINE;
			}
			break;

		case ROTATE_ON_ALIGNMENT_LINE:
			if (hasRotatedTowardsShootingPosition())
			{
			resetEncoders();
			mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			}
			break;

		case FORWARDS_FROM_ALIGNMENT_LINE:
			if (hasMovedFowardsFromTape())
			{
			resetEncoders();
			mainState = MainState.TURN_TO_FACE_GOAL;
			}
			break;

		case TURN_TO_FACE_GOAL:
			if (hasTurnedToFaceGoal())
			{
			resetEncoders();
			mainState = MainState.DRIVE_UP_TO_GOAL;
			}
			break;

		case DRIVE_UP_TO_GOAL:
			if (hasDrivenUpToGoal())
			{
			resetEncoders();
			mainState = MainState.SHOOT;
			}
			break;

		case SHOOT:
			shoot();
			mainState = MainState.DONE;
			break;

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

}

/**
 * Starts the arm motor downwards and resets the associated encoder.
 */
private static void beginLoweringArm ()
{
	Hardware.armEncoder.reset();
	Hardware.armMotor.set(1.0);

}

/**
 * Checks whether or not the pickup arm has been lowered.
 * TODO: Move to ManipulatorArm class?
 * 
 * @return true if it is down.
 */
private static boolean armIsLowered ()
{
	//false by default.
	boolean done = false;

	if (Hardware.armEncoder.get() >= ARM_DOWN_TICKS)
	//The arm IS down.
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

	//the status of the arm being down
	boolean armIsDown = armIsLowered();

	if (armIsDown)
	{
	//stop the arm.
	Hardware.pickupArm.move(0.0);
	}


	//Go forth. TODO: set to a very low speed.
	if (Hardware.drive.driveForwardInches(DISTANCE_TO_OUTER_WORKS,
	        false, 0.3,
	        0.3))
	//The distance has been reached. Now check the arm's status.
	{
	//The arm is down and we have reached the distance. Go on.
	if (armIsDown == true)
	{
	returnStatus = MoveWhileLoweringArmReturn.DONE;
	}
	else
	//The arm is not down. Return FAILED, just to be safe.
	{
	returnStatus = MoveWhileLoweringArmReturn.FAILED;
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
 * Continues to ACCELERATE when time is up.
 * One of the overarching states.
 */
private static boolean delayIsDone ()
{
	boolean done = false;

	//stop.
	Hardware.transmission.controls(0.0, 0.0);

	//check timer
	if (Hardware.delayTimer.get() > delay)
	//return true. stop and reset timer.
	{
	done = true;
	Hardware.delayTimer.stop();
	Hardware.delayTimer.reset();
	}

	if (armIsLowered())
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
	//the state of being in lane 1.
	boolean oneness;


	if (lane == 1)
	//we are in lane 1
	{
	oneness = true;
	}
	else
	//we are not
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

	//Drive forwards.
	if (Hardware.drive.driveForwardInches(DISTANCE_TO_TAPE, false))
	//If we have reached the desired distance, return true.
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

	//Move forwards.
	//TODO: make/use method to drive continuously.
	//TODO: set a good speed.
	Hardware.drive.driveContinuous();

	//simply check if we have detected the tape on either side.
	if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
	//we are done here.
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

	if (Hardware.drive.driveForwardInches(
	        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE[lane
	                - 1],
	        true))
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

	if (Hardware.drive.turnLeftDegrees(
	        DriveInformation.ROTATE_ON_ALIGNMENT_LINE_DISTANCE[lane
	                - 1]))
	{
	done = true;
	}
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

	if (Hardware.drive.turnLeftDegrees(
	        DriveInformation.TURN_TO_FACE_GOAL_DISTANCE[lane - 1]))

	{
	done = true;
	}
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

	Hardware.drive.driveContinuous();

	//Distance according to drawings.
	//if (Hardware.drive.driveForwardInches(
	//       DriveInformation.DRIVE_UP_TO_GOAL[lane - 1]))

	//see if we have reached cleats of the tower.
	if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
	//Return true, proceed.
	{
	done = true;
	}
	return done;
}

/**
 * Shoots the ball. May want to add states/methods to align.
 * 
 * @return
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
	debug = false;
	Hardware.transmission.controls(0.0, 0.0);
	Hardware.armMotor.set(0.0);
}

/*
 * =============================================
 * END OF MAIN AUTONOMOUS STATE METHODS
 * =========================================
 */

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

private static void resetEncoders ()
{
	Hardware.leftRearEncoder.reset();
	Hardware.rightRearEncoder.reset();
}



/**
 * Contains distances to drive.
 *
 */
private static final class DriveInformation
{


/**
 * Distances to rotate upon reaching alignment line.
 * Lane is indicated by index.
 * Set to Zero for 1, 2, and 5.
 */
static final double[] ROTATE_ON_ALIGNMENT_LINE_DISTANCE =
        {
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
                74.7,// lane 1
                82.0,// lane 2
                64.0, // lane 3
                66.1,// lane 4
                86.5 // lane 5
        };

/**
 * Distances to rotate to face goal.
 */
static final double[] TURN_TO_FACE_GOAL_DISTANCE =
        {
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
                62.7,// lane 1
                52.9,// lane 2
                0.0,// lane 3 (not neccesary)
                0.0,// lane 4 (not neccesary)
                12.0 // lane 5
        };

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

private static final double MAXIMUM_AUTONOMOUS_SPEED = 0.65;

/**
 * The maximum time to wait at the beginning of the match.
 * Used to scale the ratio given by the potentiometer.
 */
private static final double MAXIMUM_DELAY = 3.0;

/**
 * Distance from Outer Works checkpoint to Alignment Line
 */
private static final double DISTANCE_TO_TAPE = 180.0;

/**
 * Distance between the front of the robot to the Outer Works.
 */
private static final double DISTANCE_TO_OUTER_WORKS = 22.75;

/**
 * Encoder distance for arm.
 * TODO: set
 */
private static final double ARM_DOWN_TICKS = 0.0;

/**
 * Set to true to print out print statements.
 */
private static final boolean DEBUGGING_DEFAULT = true;

} // end class
