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
// written. Some of these functions are functions that should
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
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old.debugStateValues;
import org.usfirst.frc.team339.Utils.Drive;
import org.usfirst.frc.team339.Utils.ErrorMessage.PrintsTo;
import org.usfirst.frc.team339.Utils.ManipulatorArm;
import org.usfirst.frc.team339.Utils.ManipulatorArm.ArmPosition;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Relay;


/**
 * An Autonomous class.
 * This class <b>beautifully</b> uses state machines in order to periodically
 * execute instructions during the Autonomous period.
 * 
 * This class contains all of the user code for the Autonomous part
 * of the
 * match, namely, the Init and Periodic code
 * 
 * TODO: "make it worky".
 * 
 * 
 * @author Michael Andrzej Klaczynski
 * @written at the eleventh stroke of midnight, the 28th of January,
 *          Year of our LORD 2016. Rewritten ever thereafter.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Autonomous
{

	/**
	 * The overarching states of autonomous mode.
	 * Each state represents a set of instructions the robot must execute
	 * periodically at a given time.
	 * These states are mainly used in the runMainStateMachine() method
	 * 
	 */
	private static enum MainState
	{
		/**
		 * The first state.
		 * Initializes things if necessary,
		 * though most things are initialized
		 * in init().
		 * Proceeds to INIT_DELAY, or BEGIN_LOWERING_ARM, if in lane 1 or 6.
		 */
		INIT,
		/**
		 * Sets arm to head downward.
		 * Used at the beginning for going under the low bar.
		 * Proceeds to INIT_DELAY
		 */
		BEGIN_LOWERING_ARM,

		/**
		 * Resets and starts delay timer.
		 * Proceeds to DELAY.
		 */
		INIT_DELAY,

		/**
		 * Waits.
		 * Waits until the delay is up.
		 * Proceeds to ACCELERATE_FROM_ZERO.
		 */
		DELAY,

		/**
		 * Slowly increases speed from 0;
		 * Used at the start, as sudden movements can cause unpredictable
		 * turning.
		 * Proceeds to MOVE_TO_OUTER_WORKS.
		 */
		ACCELERATE_FROM_ZERO,

		/**
		 * Moves to outer works at a lane-specific speed.
		 * Proceeds to FORWARDS_OVER_OUTER_WORKS.
		 * <p>
		 * UNLESS we are in lanes 1 0r 6. Then,
		 * If it reaches the end of the distance, and the arm is not fully down,
		 * proceeds to WAIT_FOR_ARM_DESCENT.
		 */
		MOVE_TO_OUTER_WORKS,

		/**
		 * Wait for the arm to come down before crossing the outer works.
		 * Proceeds to FORWARDS_OVER_OUTER_WORKS.
		 */
		WAIT_FOR_ARM_DESCENT,

		/**
		 * Go the distance over the outer works at lane-specific speed.
		 * Proceeds to FORWARDS_BASED_ON_ENCODERS_OR_IR.
		 * TODO: Continuing after this point has been temporarily disabled for
		 * lane 3.
		 */
		FORWARDS_OVER_OUTER_WORKS,

		/**
		 * This state checks to see if we are in lane 1 (or "6").
		 * If so, we go until we reach an encoder distance (set to distance to
		 * alignment tape),
		 * Else, we go until the sensors find the Alignment tape.
		 * Proceeds to FORWARDS_TO_TAPE_BY_DISTANCE or FORWARDS_UNTIL_TAPE
		 */
		FORWARDS_BASED_ON_ENCODERS_OR_IR,


		/**
		 * Goes forward until it reaches the set distance to the Alignment tape.
		 * Proceeds to CENTER_TO_TAPE.
		 */
		FORWARDS_TO_TAPE_BY_DISTANCE,

		/**
		 * Goes forward until it senses the Alignment tape.
		 * Proceeds to CENTER_TO_TAPE.
		 */
		FORWARDS_UNTIL_TAPE,

		/**
		 * Drives up 16 inches to put the center of the robot over the Alignment
		 * line.
		 * Brakes in lanes 3 and 4 for turning.
		 * Proceeds to DELAY_IF_REVERSE.
		 */
		CENTER_TO_TAPE,

		/**
		 * If we are in backup plan (lane 6), start a delay so that we can
		 * reverse.
		 * TODO: this is untested.
		 * Proceeds to ROTATE_ON_ALIGNMENT_LINE.
		 */
		DELAY_IF_REVERSE,

		/**
		 * Upon reaching the Alignment line, sometimes we must rotate.
		 * The angle of rotation is lane-specific. In lanes 1, 2, and 5,
		 * this is set to zero.
		 * Proceeds to FORWARDS_FROM_ALIGNMENT_LINE.
		 */
		ROTATE_ON_ALIGNMENT_LINE,

		/**
		 * After reaching the A-line, or after rotating upon reaching it, drives
		 * towards a position in front of the goal.
		 * Distance is lane-specific.
		 * Proceeds to TURN_TO_FACE_GOAL.
		 */
		FORWARDS_FROM_ALIGNMENT_LINE,
		/**
		 * After reaching a spot in front of the goal, we turn to face it.
		 * Angle is pre-set and lane-specific.
		 * Proceeds to DRIVE_UP_TO_GOAL.
		 */
		TURN_TO_FACE_GOAL,

		/**
		 * Once we are facing the goal, we may sometimes drive forwards.
		 * Distance is lane-specific. In lanes 3 & 4, distance is zero.
		 */

		DRIVE_UP_TO_GOAL,
		/**
		 * Once we are in the shooting position, we align based on the chimera.
		 * Proceeds to SHOOT.
		 */
		ALIGN_IN_FRONT_OF_GOAL,

		/**
		 * Navigate the rest of the way by camera.
		 */
		DRIVE_BY_CAMERA,

		/**
		 * We shoot the cannon ball.
		 * Proceeds to DELAY_AFTER_SHOOT.
		 */
		SHOOT,

		/**
		 * Wait to close the solenoids, allowing the catapult to rest.
		 * Proceeds to DONE.
		 */
		DELAY_AFTER_SHOOT,

		/**
		 * We stop, and do nothing else.
		 */
		DONE
	}

	/**
	 * States to run arm movements in parallel.
	 * Used by the runArm() state machine.
	 */
	private static enum ArmState
	{
		/**
		 * Begins moving the arm in a downwards/down-to-the-floor action
		 * fashion.
		 */
		INIT_DOWN,
		/**
		 * Czecks to see if the arm is all the way down.
		 */
		MOVE_DOWN,
		/**
		 * Begins moving the arm in a upwards/up-to-the-shooter action fashion.
		 */
		INIT_UP,
		/**
		 * Czecks to see if the arm is all the way up.
		 */
		CHECK_UP,
		/**
		 * Moves, and czecks to see if the arm is all the way up, so that we may
		 * deposit.
		 */
		MOVE_UP_TO_DEPOSIT,
		/**
		 * Begins spinning its wheels so as to spit out the cannon ball.
		 */
		INIT_DEPOSIT,
		/**
		 * Have we spit out the cannon ball? If so, INIT_DOWN.
		 */
		DEPOSIT,
		/**
		 * Hold the ball out of the way.
		 */
		HOLD,
		/**
		 * Do nothing, but set armStatesOn to false.
		 */
		DONE
	}


	private static final double MAXIMUM_ULTRASONIC_SLOPE = 0;


	// ==========================================
	// AUTO STATES
	// ==========================================

	/**
	 * The state to be executed periodically throughout Autonomous.
	 */
	private static MainState mainState = MainState.INIT;

	private static MainState prevState = MainState.DONE;


	/**
	 * Used to run arm movements in parallel to the main state machine.
	 */
	private static ArmState armState = ArmState.DONE;

	// ==================================
	// VARIABLES
	// ==================================

	/**
	 * The boolean that decides whether or not we run autonomous.
	 */
	public static boolean autonomousEnabled;

	/**
	 * Time to delay at beginning. 0-3 seconds
	 */
	public static double delay; // time to delay before beginning.

	/**
	 * Number of our starting position, and path further on.
	 */
	public static int lane;

	/**
	 * The current index in the Acceleration arrays.
	 */
	private static int accelerationStage = 0;

	/**
	 * The sum of all encoder distances before reset.
	 */
	private static double totalDistance = 0;

	private static double[] ultrasonicDistances;

	private static int ultrasonicDistancesIndex = 0;

	private static int pointCollectionIntervalCheck = 0;

	/**
	 * Run the arm state machine only when necessary (when true).
	 */
	private static boolean runArmStates = false;

	/**
	 * Prints print that it prints prints while it prints true.
	 */
	public static boolean debug;

	//the following are for testing and debugging.
	//created to print a statement only once in the run of the program.
	//TODO: remove.
	private static boolean oneTimePrint1 = true;
	private static boolean oneTimePrint2 = true;
	private static boolean oneTimePrint3 = true;
	private static boolean oneTimePrint4 = false;

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


		try
		{
			//check the Autonomous ENABLED/DISABLED switch.
			autonomousEnabled = Hardware.autonomousEnabled.isOn();

			// set the delay time based on potentiometer.
			delay = initDelayTime();

			// get the lane based off of startingPositionPotentiometer
			lane = getLane();

			//Enable debugging prints.
			debug = DEBUGGING_DEFAULT; //true

			//set Auto state to INIT.
			initAutoState();

			ultrasonicDistances =
			        new double[ULTRASONIC_POINTS_REQUIRED];

			//do not print from transmission
			Hardware.transmission.setDebugState(
			        debugStateValues.DEBUG_NONE);

			// Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);

			// -------------------------------------
			// motor initialization
			// -------------------------------------

			Hardware.transmission.setFirstGearPercentage(1.0);
			Hardware.transmission.setGear(1);
			Hardware.transmission.setJoysticksAreReversed(true);
			Hardware.transmission.setJoystickDeadbandRange(0.0);

			Hardware.leftFrontMotor.set(0.0);
			Hardware.leftRearMotor.set(0.0);
			Hardware.rightFrontMotor.set(0.0);
			Hardware.rightRearMotor.set(0.0);
			Hardware.armMotor.set(0.0);
			Hardware.armIntakeMotor.set(0.0);

			// --------------------------------------
			// Encoder Initialization
			// --------------------------------------
			Hardware.leftRearEncoder.reset();
			Hardware.rightRearEncoder.reset();

			// Sets Resolution of camera
			Hardware.ringLightRelay.set(Relay.Value.kOff);

			Hardware.axisCamera.writeBrightness(
			        Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);

			// ---------------------------------------
			// turn the timer off and reset the counter
			// so that we can use it in autonomous
			// ---------------------------------------
			Hardware.kilroyTimer.stop();
			Hardware.kilroyTimer.reset();

			//----------------------------------------
			//Solenoid Initialization
			//----------------------------------------
			Hardware.catapultSolenoid0.set(false);
			Hardware.catapultSolenoid1.set(false);
			Hardware.catapultSolenoid2.set(false);

			//----------------------------------------
			// setup things to run on the field or in
			// the lab
			//----------------------------------------
			if (Hardware.runningInLab == true)
				labScalingFactor = 0.4;
			else
				labScalingFactor = 1.0;

			//clear error log, so as not to take up space needlessly.
			//this can be buggy, hence the try/catch.

			try
			{
				Hardware.errorMessage.clearErrorlog();
			}
			catch (Exception e)
			{
				System.out.println("clearing log is the problem");
			}
		}
		catch (Exception e)
		{
			System.out.println("Auto init died");
		}

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

		// Checks the "enabled" switch.
		if (autonomousEnabled == true)
		{
			//runs the overarching state machine.
			runMainStateMachine();
		}

		// Czecks if we are running any arm functions.
		if (runArmStates == true)
		//run the arm state machine.
		{
			//System.out.println("\t" + armState);
			runArmStates();
		}
		else
		//if not, we do not want the arm to move.
		{
			Hardware.pickupArm.stopArmMotor();
		}



		if (isGettingCloserToWall(
		        Hardware.ultrasonic.getRefinedDistanceValue()))
		{
			System.out.println("Closer...");
		}

	} // end Periodic


	/**
	 * Sets the delay time in full seconds based on potentiometer.
	 * 
	 * @return the seconds to delay.
	 */
	public static int initDelayTime ()
	{
		//scales ratio of degrees turned by our maximum delay.
		return (int) (MAXIMUM_DELAY * Hardware.delayPot.get() /
		        Hardware.DELAY_POT_DEGREES);
	}


	/**
	 * Sets the main state to INIT.
	 */
	public static void initAutoState ()
	{
		mainState = MainState.INIT;
	}


	/**
	 * Called periodically to run the overarching states.
	 * The state machine works by periodically executing a switch statement,
	 * which is based off of the value of the enum, mainState.
	 * A case that corresponds to a value of the enum is referred to as a
	 * "state."
	 * A state, therefore, is executed periodically until its end conditions are
	 * met, at which point it changes the value of mainState, thereby proceeding
	 * to the next state.
	 * <p>
	 * For information on the individual states, @see MainState.
	 */
	private static void runMainStateMachine ()
	{

		//use the debug flag 
		if (debug == true)
		{
			// print out states ONCE.
			if (mainState != prevState)
			{
				prevState = mainState;
				System.out.println(
				        "Legnth: " + Hardware.kilroyTimer.get() +
				                " seconds.\n");
				System.out.println("Main State: " + mainState);
				Hardware.errorMessage.printError(
				        "MainState" + mainState, PrintsTo.roboRIO,
				        false);
			}
		}

		switch (mainState)
		{
		case INIT:
			// Doesn't do much.
			// Just a Platypus.
			mainInit();

			//if we are in a lane that goes beneath the low bar,
			if (DriveInformation.BRING_ARM_DOWN_BEFORE_DEFENSES[lane] == true)
			// lower the arm to pass beneath the bar.
			{
				mainState = MainState.BEGIN_LOWERING_ARM;
			}
			else
			// lowering the arm would get in the way. Skip to delay.
			{
				mainState = MainState.INIT_DELAY;
			}

			break;

		case BEGIN_LOWERING_ARM:
			// starts the arm movement to the floor
			runArmStates = true;
			armState = ArmState.MOVE_DOWN;
			// goes into initDelay
			mainState = MainState.INIT_DELAY;
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
			// go to move forwards when finished.
			{
				mainState = MainState.ACCELERATE_FROM_ZERO;

				//start a timer for ACCELERATE.
				Hardware.delayTimer.reset();
				Hardware.delayTimer.start();
			}
			break;


		case ACCELERATE_FROM_ZERO:
			//Allows us to slowly accelerate from zero without turning

			//So long as there are more acceleration numbers in the series.
			if (accelerationStage < DriveInformation.ACCELERATION_RATIOS.length)
			{
				//Drive continuously at speed increments
				Hardware.drive.driveStraightByInches(99999, false,
				        DriveInformation.ACCELERATION_RATIOS[accelerationStage],
				        DriveInformation.ACCELERATION_RATIOS[accelerationStage]);

				//When the time has come,
				if (Hardware.delayTimer
				        .get() > DriveInformation.ACCELERATION_TIMES[accelerationStage])
				{
					//go to next stage.
					accelerationStage++;
				}
			}
			else
			//there are no stages left. continue onwards.
			{
				//stop timer.
				Hardware.delayTimer.stop();
				Hardware.delayTimer.reset();
				//continue forwards at regular speed.
				mainState = MainState.MOVE_TO_OUTER_WORKS;
			}

			break;

		case MOVE_TO_OUTER_WORKS:

			if (oneTimePrint4 == false)
			{
				Hardware.transmission.setDebugState(
				        debugStateValues.DEBUG_ALL);
				Hardware.errorMessage.printError("Left: " +
				        Hardware.leftRearEncoder.getDistance(),
				        PrintsTo.roboRIO,
				        false);
				Hardware.errorMessage.printError("Right: " +
				        Hardware.leftRearEncoder.getDistance(),
				        PrintsTo.roboRIO,
				        false);
				oneTimePrint4 = true;
			}
			else
			{
				Hardware.transmission.setDebugState(
				        debugStateValues.DEBUG_NONE);
			}

			// goes forwards to outer works.
			if (Hardware.drive.driveStraightByInches(
			        DriveInformation.DISTANCE_TO_OUTER_WORKS *
			                labScalingFactor,
			        false,
			        DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane],
			        DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane]) == true)
			//continue over the outer works unless the arm is going to get in the way.
			{

				//continue over the Outer Works
				mainState = MainState.FORWARDS_OVER_OUTER_WORKS;
				resetEncoders();

				//UNLESS...
				// if we are in a lane that goes beneath the low bar,
				if ((DriveInformation.BRING_ARM_DOWN_BEFORE_DEFENSES[lane] == true)
				        &&
				        (Hardware.pickupArm.isUnderBar() == false))
				//arm is not down in time. STOP.
				{
					mainState = MainState.WAIT_FOR_ARM_DESCENT;
				}
			}
			break;

		case WAIT_FOR_ARM_DESCENT:
			Hardware.transmission.setDebugState(
			        debugStateValues.DEBUG_NONE);

			//Stop during the wait. We do not want to ram the bar.
			Hardware.transmission.controls(0.0, 0.0);

			//Put the arm down,
			//and when that is done, continue.
			if (Hardware.pickupArm.moveToPosition(
			        ManipulatorArm.ArmPosition.FULL_DOWN) == true)
			{
				//drive over the outer works.
				mainState = MainState.FORWARDS_OVER_OUTER_WORKS;
			}
			break;


		case FORWARDS_OVER_OUTER_WORKS:
			//Drive over Outer Works.
			if (Hardware.drive.driveStraightByInches(
			        DriveInformation.DISTANCE_OVER_OUTER_WORKS *
			                labScalingFactor +
			                DriveInformation.ADDED_DISTANCE_FROM_OW[lane],
			        false,
			        DriveInformation.DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS[lane],
			        DriveInformation.DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS[lane]) == true)
			//put up all the things we had to put down under the low bar.
			//begin loading the catapult.
			{

				//put up camera.
				Hardware.cameraSolenoid.set(Value.kReverse);

				Hardware.ringLightRelay.set(Relay.Value.kOn);

				//Teleop.printStatements();
				//                System.out.println("Encoders: "
				//                        + Hardware.leftRearEncoder.getDistance() + "\t"
				//                        + Hardware.rightRearEncoder.getDistance());
				resetEncoders();

				//We are over the outer works. Start the arm back up.
				armState = ArmState.MOVE_UP_TO_DEPOSIT;
				runArmStates = true;

				//decide whether to stop next by encoder distance, or when IRs detect tape.
				mainState = MainState.FORWARDS_BASED_ON_ENCODERS_OR_IR;

				//temporary; stops after over outer works.
				//3 is chosen for this arbitrarily.
				if (lane == 3)
				{
					mainState = MainState.DONE;
				}

				if (DriveInformation.SKIP_TO_DRIVE_BY_CAMERA[lane] == true)
				{
					mainState = MainState.DRIVE_BY_CAMERA;
				}

			}
			break;




		case FORWARDS_BASED_ON_ENCODERS_OR_IR:

			// Check if we are in lane One.
			if (lane == 1 || lane == 6)
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
			if ((Hardware.drive.driveStraightByInches(
			        DriveInformation.DISTANCE_TO_TAPE *
			                labScalingFactor,
			        false, DriveInformation.MOTOR_RATIO_TO_A_LINE[lane],
			        DriveInformation.MOTOR_RATIO_TO_A_LINE[lane]) == true))
			// when done, proceed from Alignment line.
			{

				//Teleop.printStatements();

				//reset Encoders to prepare for next state.
				resetEncoders();


				//Move the center of the robot to the tape.
				mainState = MainState.CENTER_TO_TAPE;
			}
			break;


		case FORWARDS_UNTIL_TAPE:
			// Drive until IR sensors pick up tape.
			if (hasMovedToTape() == true)
			{
				//reset Encoders to prepare for next state.
				resetEncoders();

				// When done, possibly rotate.
				mainState = MainState.CENTER_TO_TAPE;
			}
			break;

		case CENTER_TO_TAPE:
			//Drive up from front of the Alignment Line to put the pivoting center of the robot on the Line.
			if (Hardware.drive.driveStraightByInches(
			        DriveInformation.DISTANCE_TO_CENTRE_OF_ROBOT,
			        DriveInformation.BREAK_ON_ALIGNMENT_LINE[lane],
			        DriveInformation.CENTRE_TO_ALIGNMENT_LINE_MOTOR_RATIO[lane],
			        DriveInformation.CENTRE_TO_ALIGNMENT_LINE_MOTOR_RATIO[lane]))
			{
				//Next, we will stop if necessary.
				mainState = MainState.DELAY_IF_REVERSE;


				//start timer for the coming delay.
				Hardware.delayTimer.reset();
				Hardware.delayTimer.start();
			}
			break;

		case DELAY_IF_REVERSE:

			//when the delay is up,
			if (Hardware.delayTimer
			        .get() >= DriveInformation.DELAY_IF_REVERSE[lane])
			//continue driving.
			{
				//stop the timer
				Hardware.delayTimer.stop();
				//rotate, possibly
				mainState = MainState.ROTATE_ON_ALIGNMENT_LINE;
			}
			break;


		case ROTATE_ON_ALIGNMENT_LINE:
			//Rotates until we are pointed at the place from whence we want to shoot.
			if (hasTurnedBasedOnSign(
			        DriveInformation.ROTATE_ON_ALIGNMENT_LINE_DISTANCE[lane]
			                *
			                labScalingFactor) == true)
			{
				//reset Encoders to prepare for next state.
				resetEncoders();
				//then move.
				mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;
			}
			break;

		case FORWARDS_FROM_ALIGNMENT_LINE:
			//Drive until we reach the line normal to the goal.
			if (Hardware.drive.driveStraightByInches(
			        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE[lane]
			                *
			                labScalingFactor,
			        true, //breaking here is preferable.
			        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane],
			        DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane]) == true)
			{
				//reset Encoders to prepare for next state.
				resetEncoders();
				//turn once more.
				mainState = MainState.TURN_TO_FACE_GOAL;
			}
			break;

		case TURN_TO_FACE_GOAL:
			//Turns until we are facing the goal.
			if (hasTurnedBasedOnSign(
			        DriveInformation.TURN_TO_FACE_GOAL_DEGREES[lane]) == true)
			//when done move up to the batter.
			{
				//reset Encoders to prepare for next state
				resetEncoders();

				//Hardware.ringLightRelay.set(Relay.Value.kOn);

				//Hold the arm up (Out of the Way).
				armState = ArmState.HOLD;


				Hardware.transmission.controls(0.0, 0.0);

				//then drive.
				mainState = MainState.DRIVE_UP_TO_GOAL;
			}
			break;

		case DRIVE_UP_TO_GOAL:

			//print encoders once
			//TODO: remove.
			if (oneTimePrint1 == false)
			{
				oneTimePrint1 = true;
				System.out.println(mainState + "\n\tLeft Encoder: " +
				        Hardware.leftRearEncoder.getDistance() +
				        "\n\tLeft Encoder: " +
				        Hardware.leftRearEncoder.getDistance());
			}

			//Moves to goal. Stops to align.
			if (((Hardware.drive.driveStraightByInches(
			        DriveInformation.DRIVE_UP_TO_GOAL[lane] *
			                labScalingFactor,
			        false,
			        DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane],
			        DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane]) == true)))
			//Go to align.
			{

				if (oneTimePrint2 == false)
				{
					oneTimePrint2 = true;
					System.out.println(mainState +
					        "\n\tLeft Encoder: " +
					        Hardware.leftRearEncoder.getDistance() +
					        "\n\tLeft Encoder: " +
					        Hardware.leftRearEncoder.getDistance());
				}

				//reset Encoders to prepare for next state.
				resetEncoders();

				//go to align.
				mainState = MainState.ALIGN_IN_FRONT_OF_GOAL;
			}

			//print IR values once if they are on.
			//            if (oneTimePrint3 == false &&
			//                    (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
			//                {
			//                oneTimePrint3 = true;
			//                System.out.println("An IR found something.");
			//                }
			break;

		case ALIGN_IN_FRONT_OF_GOAL:
			//align based on the camera until we are facing the goal. head-on.
			//Hardware.transmission.controls(0.0, 0.0);

			if (Hardware.drive.alignByCamera(
			        Autonomous.CAMERA_ALIGN_X_DEADBAND,
			        DriveInformation.ALIGNMENT_SPEED,
			        CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
			        true) == Drive.alignByCameraReturn.DONE)
			//Once we are in position, we shoot!
			{
				mainState = MainState.SHOOT;
			}
			break;

		case DRIVE_BY_CAMERA:

			if (Hardware.drive.alignByCameraStateMachine(
			        CAMERA_ALIGN_X_DEADBAND,
			        CAMERA_ALIGN_Y_DEADBAND,
			        CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
			        CAMERA_Y_AXIS_ADJUSTED_PROPORTIONAL_CENTER,
			        ALIGN_BY_CAMERA_TURNING_SPEED,
			        ALIGN_BY_CAMERA_DRIVE_SPEED,
			        false,
			        false, false) == Drive.alignByCameraReturn.DONE)
			{
				mainState = MainState.SHOOT;
			}

			break;

		case SHOOT:
			//FIRE!!!
			if (shoot() == true)
			{
				//go to wait a little bit, before turning off solenoids
				mainState = MainState.DELAY_AFTER_SHOOT;
			}
			break;

		case DELAY_AFTER_SHOOT:
			//Check if enough time has passed for the air to have been released.
			if (hasShot() == true)
			{
				//This block will close all solenoids,
				//allowing the catapult to go back down.
				Hardware.catapultSolenoid0.set(false);
				Hardware.catapultSolenoid1.set(false);
				Hardware.catapultSolenoid2.set(false);

				//finish it.
				mainState = MainState.DONE;
			}
			break;

		default:
		case DONE:
			//clean everything up;
			//the blood of our enemies stains quickly.
			done();
			break;
		}
	}


	/*
	 * ======================================
	 * AUTONOMOUS UTILITY METHODS
	 * ======================================
	 */
	private static void mainInit ()
	{

		//start at zero.
		resetEncoders();

		//start the timer.
		Hardware.kilroyTimer.reset();
		Hardware.kilroyTimer.start();
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
		return done;
	}

	/**
	 * Drives, and
	 * Checks to see if the IRSensors detect Alignment tape.
	 * 
	 * @return true when it does.
	 */
	private static boolean hasMovedToTape ()
	{
		//The stateness of being on the tape.
		boolean tapeness = false;

		// Move forwards.
		Hardware.drive.driveStraightContinuous();

		// simply check if we have detected the tape on either side.
		if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
		// we are done here.
		{
			tapeness = true;
		}
		return tapeness;
	}

	/**
	 * <b> FIRE!!! </b>
	 * <p>
	 * Shoots the ball.
	 * 
	 */
	private static boolean shoot ()
	{

		//to be returned true if done.
		boolean done;

		//Turn off motors. We do NOT want to move.
		Hardware.transmission.controls(0.0, 0.0);

		//Make sure the arm is out of the way.
		if (Hardware.pickupArm.isClearOfArm())
		{

			//RELEASE THE KRACKEN! I mean, the pressurized air...
			Hardware.catapultSolenoid0.set(true);
			Hardware.catapultSolenoid1.set(true);
			Hardware.catapultSolenoid2.set(true);

			//set a timer so that we know when to close the solenoids.
			Hardware.kilroyTimer.reset();
			Hardware.kilroyTimer.start();

			//stop arm motion.
			runArmStates = false;

			//is finished.
			done = true;
		}
		else
		{
			//get the arm out of the way.
			runArmStates = true;
			armState = ArmState.MOVE_DOWN;

			//continue until it is done.
			done = false;
		}

		//true if done.
		return done;
	}

	/**
	 * Wait a second...
	 * Close the solenoids.
	 * 
	 * @return true when delay is up.
	 */
	private static boolean hasShot ()
	{
		//Check the time.
		if (Hardware.kilroyTimer.get() > DELAY_TIME_AFTER_SHOOT)
		//Close the airways, and finish.
		{
			Hardware.catapultSolenoid0.set(false);
			Hardware.catapultSolenoid1.set(false);
			Hardware.catapultSolenoid2.set(false);
			return true;
		}
		return false;
	}

	/**
	 * Stop everything.
	 */
	private static void done ()
	{
		//after autonomous is disabled, the state machine will stop.
		//this code will run but once.
		autonomousEnabled = false;

		//stop printing debug statements.
		debug = false;

		//turn off all motors.
		Hardware.transmission.controls(0.0, 0.0);

		//including the arm.
		//end any surviving arm motions.
		armState = ArmState.DONE;
		Hardware.armMotor.set(0.0);

		//reset delay timer
		Hardware.delayTimer.stop();
		Hardware.delayTimer.reset();

		//turn off ringlight.
		Hardware.ringLightRelay.set(Relay.Value.kOff);

		Hardware.transmission
		        .setDebugState(debugStateValues.DEBUG_NONE);
	}

	/*
	 * =============================================
	 * END OF MAIN AUTONOMOUS STATE METHODS
	 * =============================================
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
			Hardware.pickupArm.move(-1.0);
			//go to periodically check.
			armState = ArmState.MOVE_DOWN;
			break;

		case MOVE_DOWN:
			//move to down position.
			if (Hardware.pickupArm.moveToPosition(
			        ArmPosition.FULL_DOWN) == true)
			//if done, stop.
			{
				//stop the motor.
				Hardware.pickupArm.move(0.0);
				armState = ArmState.DONE;
			}
			break;

		case INIT_UP:
			//begin moving arm up.
			Hardware.pickupArm.move(1.0);
			//go to periodically check.
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

		case MOVE_UP_TO_DEPOSIT:
			//check is in up position so that we may deposit the ball.
			if (Hardware.pickupArm.moveToPosition(
			        ArmPosition.DEPOSIT) == true)
			//stop, and go to deposit.
			{
				Hardware.pickupArm.move(0.0);
				armState = ArmState.INIT_DEPOSIT;
			}
			break;

		case INIT_DEPOSIT:
			//spin wheels to release ball. Uses override.
			Hardware.pickupArm.pullInBall(true);
			armState = ArmState.DEPOSIT;
			break;

		case DEPOSIT:
			//check if the ball is out.
			if (Hardware.pickupArm.ballIsOut())
			//stop rollers, and move down.
			{
				//stop the intake motors.
				Hardware.pickupArm.stopIntakeMotors();
				//TODO: get arms out of the way.
				armState = ArmState.DONE;
			}
			break;

		case HOLD:
			//do not move the intake motors.
			Hardware.pickupArm.stopIntakeMotors();
			//keep the arms in holding position
			Hardware.pickupArm.moveToPosition(ArmPosition.HOLD);
			//note: no end conditions, unless if the state is manually changed.
			break;

		default:
		case DONE:
			//stop running state machine.
			runArmStates = false;
			break;
		}
	}

	//TODO: Remove unecessary TODOs

	/**
	 * Return the starting position based on 6-position switch on the robot.
	 * 
	 * @return lane/starting position
	 */
	public static int getLane ()
	{
		//get the value from the six-position switch.
		int position = Hardware.startingPositionDial.getPosition();

		//-1 is returned when there is no signal. 
		if (position == -1)
		//Go to lane 1 by default.
		{
			position = 0;
		}

		//increment by one, to correspond to lane #.
		position++;

		return position;
	}

	/**
	 * Reset left and right encoders.
	 * To be called at the end of any state that uses Drive.
	 */
	public static void resetEncoders ()
	{
		//accumulate total distance. used for debugging.
		totalDistance += (Hardware.leftRearEncoder.getDistance() +
		        Hardware.rightRearEncoder.get()) / 2;

		//reset.
		Hardware.leftRearEncoder.reset();
		Hardware.rightRearEncoder.reset();
	}

	/**
	 * For turning in drive based on array of positive and negative values.
	 * Use to turn a number of degrees
	 * COUNTERCLOCKWISE.
	 * Kilroy must turn along different paths.
	 * You must use this to be versatile.
	 * 
	 * @param degrees
	 *            is the number of degrees to be turned. A positive value will
	 *            turn left, while a negative value will turn right.
	 * 
	 * @param turnSpeed
	 *            will be passed into both motors, one positive, one negative,
	 *            depending on the direction of the turn.
	 * 
	 * @return true when the turn is finished.
	 */
	private static boolean hasTurnedBasedOnSign (double degrees,
	        double turnSpeed)
	{
		//the value of done will be returned.
		boolean done = false;

		if (degrees < 0)
		//Turn right. Make degrees positive.
		{
			done = Hardware.drive.turnRightDegrees(-degrees, true,
			        turnSpeed,
			        -turnSpeed);
		}
		else
		//Turn left the given number of degrees.
		{
			done = Hardware.drive.turnLeftDegrees(degrees, true,
			        -turnSpeed,
			        turnSpeed);
		}
		return done;
	}

	/**
	 * Tells us if we are getting any closer to a thing after being given 55
	 * points.
	 * 
	 * 
	 * 
	 * @param point
	 *            is given one-at-a-time.
	 * @return true if closer.
	 */
	private static boolean isGettingCloserToWall (double point)
	{

		//return true if we are closer.
		boolean isCloser = false;

		//We do not take a point every iteration. Only every 11th point.
		//We end up with 5 point for each check.
		pointCollectionIntervalCheck++;
		if (pointCollectionIntervalCheck == ITERATIONS_PER_POINT)
		{
			//reset
			pointCollectionIntervalCheck = 0;

			//add the point to the array
			ultrasonicDistances[ultrasonicDistancesIndex] = (int) point;
			ultrasonicDistancesIndex++;

			//if the array is full, check the slope.
			if (ultrasonicDistancesIndex == ultrasonicDistances.length)
			{

				//if the slope is negative, we are getting closer.
				if (getRegressionSlopeOfArray(
				        ultrasonicDistances) < MAXIMUM_ULTRASONIC_SLOPE)
				{
					//and so, we will return true.
					isCloser = true;
				}
				//reset the array.
				ultrasonicDistances =
				        new double[ULTRASONIC_POINTS_REQUIRED];
				ultrasonicDistancesIndex = 0;
			}

		}


		return isCloser;
	}

	/**
	 * Used by isGettingCloserToWall to determine the direction of the robot.
	 * 
	 * @param data
	 *            in which index is the x axis.
	 * @return slope technically is in inches per fifth of a second.
	 */
	private static double getRegressionSlopeOfArray (double[] data)
	{

		/*
		 * Formula: (Sigma --> E)
		 * E ((x-(mean x))(y-(mean y))) / E ((x-(mean x))^2)
		 */

		int slope;
		int ySum = 0;
		int xSum = 0;
		int bigN = data.length;

		//sum all values for regression calculation
		for (int i = 0; i < data.length; i++)
		{
			ySum += data[i];
			xSum += i;
		}

		int xBar = xSum / bigN;
		int yBar = ySum / bigN;

		int numeratorSum = 0;
		int denomenatorSum = 0;
		for (int i = 0; i < data.length; i++)
		{
			numeratorSum += ((i - xBar) * (data[i] - yBar));
			denomenatorSum += ((i - xBar) * (i - xBar));
		}

		//calculate regression line slope
		slope = numeratorSum / denomenatorSum;

		return slope;

	}

	/**
	 * For turning in drive based on array of positive and negative values.
	 * Use to turn a number of degrees
	 * COUNTERCLOCKWISE.
	 * Kilroy must turn along different paths.
	 * You must use this to be versatile.
	 * 
	 * @param degrees
	 *            is the number of degrees to be turned. A positive value will
	 *            turn left, while a negative value will turn right.
	 * 
	 * @return true when the turn is finished.
	 */
	private static boolean hasTurnedBasedOnSign (double degrees)
	{
		return hasTurnedBasedOnSign(degrees,
		        DriveInformation.DEFAULT_TURN_SPEED);
	}

	/**
	 * Contains distances and speeds at which to drive.
	 * 
	 * Note that many of these are arrays.
	 * This is usually to determine different speeds and distances for each
	 * lane.
	 * Such arrays will follow the following format:
	 * 
	 * </p>
	 * [0]: placeholder, so that the index will match up with the lane number
	 * </p>
	 * [1]: lane 1
	 * </p>
	 * [2]: lane 2
	 * </p>
	 * [3]: lane 3
	 * </p>
	 * [4]: lane 4
	 * </p>
	 * [5]: lane 5
	 * </p>
	 * [6]: "lane 6" -- not a real lane, but can serve as an alternate strategy.
	 */
	private static final class DriveInformation
	{

		/**
		 * A set of motor ratios that will be used in succession
		 * when accelerating from zero.
		 * 
		 * Index here does not correspond to lane, but rather order of
		 * execution.
		 */
		private static final double[] ACCELERATION_RATIOS =
		        {
		                0.1,
		                0.2,
		                0.3
		        };

		/**
		 * The time at which each acceleration stage will end.
		 * 
		 * Index here does not correspond to lane, but rather order of
		 * execution.
		 */
		private static final double[] ACCELERATION_TIMES =
		        {
		                0.2,
		                0.4,
		                0.6
		        };

		/**
		 * The speed at which we want to go over the outer works, for each lane.
		 * note that it is to be low for lane 1, yet high for the others,
		 * so as to accommodate more difficult obstacles.
		 */
		private static final double[] DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS =
		        {
		                0.0,
		                0.5,//not much is required for the low bar.
		                0.5,
		                0.8,
		                0.5,
		                0.5,
		                0.4
		        };

		private static final boolean[] BRING_ARM_DOWN_BEFORE_DEFENSES =
		        {
		                false, // a placeholder, allowing lane to line up with index
		                true,  // lane 1 - bring arm down
		                false, // lane 2
		                false, // lane 3
		                false, // lane 4
		                false, // lane 5
		                true   // lane 6 - bring arm down
		        };
		/**
		 * For each lane, decides whether or not to break on the Alignment Line
		 */
		private static final boolean[] BREAK_ON_ALIGNMENT_LINE =
		        {
		                false, // A placeholder, allowing lane to line up with index.
		                false, //
		                false, //
		                true, // true in lanes 3 and 4, 
		                true, // because we mean to turn next.
		                false, // 
		                true //or go backwards.
		        };

		private static final boolean[] SKIP_TO_DRIVE_BY_CAMERA =
		        {
		                false,
		                false,
		                false,
		                false,
		                true, //lane 4.
		                false,
		                false
		        };

		/**
		 * The motor controller values for moving to the outer works.
		 * As these are initial speeds, keep them low, to go easy on the motors.
		 * Lane is indicated by index.
		 */
		private static final double[] MOTOR_RATIO_TO_OUTER_WORKS =
		        {
		                0.0, // nothing. Not used. Arbitrary; makes it work.
		                0.50,//0.25, // lane 1, should be extra low.
		                1.0, // lane 2
		                0.5, // lane 3
		                0.5, // lane 4
		                0.5, // lane 5
		                0.5 //backup plan
		        };

		/**
		 * "Speeds" at which to drive from the Outer Works to the Alignment
		 * Line.
		 */
		private static final double[] MOTOR_RATIO_TO_A_LINE =
		        {
		                0.0, //PLACEHOLDER
		                0.5, //lane 1
		                0.5, //lane 2
		                0.4, //lane 3
		                0.4, //lane 4
		                0.5,  //lane 5
		                0.0 //backup plan
		        };

		/**
		 * Distances to rotate upon reaching alignment line.
		 * Lane is indicated by index.
		 * Set to Zero for 1, 2, and 5.
		 */
		private static final double[] ROTATE_ON_ALIGNMENT_LINE_DISTANCE =
		        {
		                0.0, // nothing. Not used. Arbitrary; makes it work.
		                0.0, // lane 1 (not neccesary)
		                0.0, // lane 2 (not neccesary)
		                -72.85,//-20, // lane 3
		                64.24,//24.8, // lane 4
		                0.0, // lane 5 (not neccesary)
		                0.0 //backup plan
		        };

		/**
		 * Distances to drive after reaching alignment tape.
		 * Lane is indicated by index.
		 * 16 inchworms added inevitably, to start from centre of robot.
		 */
		private static final double[] FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE =
		        {
		                0.0, // nothing. Not used. Arbitrary; makes it work.
		                36.57,// lane 1 //48.57
		                77.44,//68.0,// lane 2
		                66.14,//64.0, // lane 3
		                63.2,//66.1,// lane 4
		                85.8, // lane 5
		                -169.0 //backup plan
		        };

		/**
		 * Speed when we move the centre of the robot to the Alignment line.,
		 */
		private static final double[] CENTRE_TO_ALIGNMENT_LINE_MOTOR_RATIO =
		        {
		                0.0,
		                0.5, //1
		                0.5, //2
		                .25, //3
		                .25, //4
		                0.5, //5
		                0.25 //backup plan
		        };

		/**
		 * "Speeds" at which to drive from the A-Line to the imaginary line
		 * normal to
		 * the goal.
		 */
		private static final double[] FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO =
		        {
		                0.0, // nothing. A Placeholder.
		                0.5, //lane 1
		                0.5, //lane 2
		                0.7, //lane 3
		                0.7, //lane 4
		                0.5,  //lane 5
		                -0.5 //backup plan
		        };

		/**
		 * Distances to rotate to face goal.
		 */
		private static final double[] TURN_TO_FACE_GOAL_DEGREES =
		        {
		                0.0, // makes it so the indexes line up with the lane #
		                -60.0,// lane 1  //-60
		                -60.0,// lane 2
		                72.85,//20.0,// lane 3
		                -64.24,//-24.85,// lane 4
		                60, // lane 5
		                -90.0 //backup plan
		        };


		/**
		 * Distances to travel once facing the goal.
		 * Not neccesary for lanes 3 and 4; set to zero.
		 * Actually, we may not use this much at all, given that we will
		 * probably just
		 * use the IR to sense the cleets at the bottom of the tower.
		 */
		private static final double[] DRIVE_UP_TO_GOAL =
		        {
		                0.0, // nothing. Not used. Arbitrary; makes it work.
		                53.85,//previously 62.7//65.85,// lane 1
		                18.1,//52.9,// lane 2
		                0.0,// lane 3 (not neccesary)
		                0.0,// lane 4 (not neccesary)
		                32.8,//12.0, // lane 5
		                0.0 //backup plan
		        };

		/**
		 * "Speeds" at which to drive to the Batter.
		 */
		private static final double[] DRIVE_UP_TO_GOAL_MOTOR_RATIO =
		        {
		                0.0,
		                0.50,
		                0.5,
		                0.5,
		                0.5,
		                0.5,
		                0.0
		        };

		/**
		 * Time to delay at A-line. Only used if reversing.
		 */
		private static final double[] DELAY_IF_REVERSE =
		        {
		                0.0,
		                0.0,
		                0.0,
		                0.0,
		                0.0,
		                0.0,
		                1.0//only used in "lane 6."
		        };

		/**
		 * Should we want to stop upon going over the outer works,
		 * this should add extra distance, for assurance.
		 */
		private static final double[] ADDED_DISTANCE_FROM_OW =
		        {
		                0.0,
		                0.0,
		                0.0,
		                48.0,// Further driving is disabled in this lane, 
		                // so this is to be sure we are all the way over.
		                60.0,
		                0.0,
		                0.0
		        };

		/**
		 * Distance from Outer Works checkpoint to Alignment Line.
		 * The front of the robot will be touching the Lion.
		 */
		private static final double DISTANCE_TO_TAPE = 27.5;


		/**
		 * Distance to get the front of the robot to the Outer Works.
		 */
		private static final double DISTANCE_TO_OUTER_WORKS = 16.75; //prev. 22.75;

		/**
		 * Distance to travel to get over the Outer Works.
		 */
		private static final double DISTANCE_OVER_OUTER_WORKS = 104.86; //prev. 98.86;

		/**
		 * The distance to the central pivot point from the front of the robot.
		 * We will use this so that we may rotate around a desired point at the
		 * end of a
		 * distance.
		 */
		private static final double DISTANCE_TO_CENTRE_OF_ROBOT = 16.0;

		/**
		 * Speed at which to make turns by default.
		 */
		private static final double DEFAULT_TURN_SPEED = 0.55;

		/**
		 * Speed at which we may align by camera.
		 */
		private static final double ALIGNMENT_SPEED = 0.5;
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
	 * The maximum time to wait at the beginning of the match.
	 * Used to scale the ratio given by the potentiometer.
	 */
	private static final double MAXIMUM_DELAY = 3.0;

	/**
	 * Set to true to print out print statements.
	 */
	public static final boolean DEBUGGING_DEFAULT = true;

	/**
	 * Factor by which to scale all distances for testing in our small lab
	 * space.
	 */
	public static double labScalingFactor = 1.0;

	/**
	 * Time to wait after releasing the solenoids before closing them back up.
	 */
	private static final double DELAY_TIME_AFTER_SHOOT = 1.0;


	private static final double CAMERA_ALIGN_Y_DEADBAND = .10;

	private static final double CAMERA_ALIGN_X_DEADBAND = .125;

	private static final double CAMERA_X_AXIS_ADJUSTED_PROPORTIONAL_CENTER =
	        -.375;

	private static final double CAMERA_Y_AXIS_ADJUSTED_PROPORTIONAL_CENTER =
	        -.192;

	private static final double ALIGN_BY_CAMERA_TURNING_SPEED = .55;

	private static final double ALIGN_BY_CAMERA_DRIVE_SPEED = .45;

	private static final int ULTRASONIC_POINTS_REQUIRED = 5;

	private static final int ITERATIONS_PER_POINT = 11;

} // end class
