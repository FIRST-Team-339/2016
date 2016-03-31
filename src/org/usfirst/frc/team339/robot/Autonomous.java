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
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old.debugStateValues;
import org.usfirst.frc.team339.Utils.ManipulatorArm;
import org.usfirst.frc.team339.Utils.ManipulatorArm.ArmPosition;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Relay;

/**
 * This class contains all of the user code for the Autonomous part of the
 * match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */

/**
 * A new and improved Autonomous class.
 * The class <b>beautifully</b> uses state machines in order to periodically
 * execute instructions during the Autonomous period.
 * 
 * TODO: "make it worky".
 * 
 * @author Michael Andrzej Klaczynski
 * @written at the eleventh stroke of midnight, the 28th of January,
 *          Year of our LORD 2016. Rewritten ever thereafter.
 * 
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
     * slowly increase speed from 0;
     */
    ACCELERATE_FROM_ZERO,

    /**
     * Moves at a low speed while lowering arm.
     * If it reaches the end of the distance, and the arm is not fully down,
     * skips to DONE.
     */
    MOVE_TO_OUTER_WORKS,//
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
     * Else, we go until the sensors find the Alignment tape.
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
     * Drives up 16 inches to put the center of the robot over the Aline.
     */
    CENTER_TO_TAPE,

    /**
     * If we are in backup plan (lane 6), start a delay so that we can
     * reverse.
     */
    DELAY_IF_REVERSE,

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
     * An attempt to fix turning problem.
     */
    STOP_ALL_MOTORS,
    /**
     * Once we are facing the goal, we may sometimes drive forwards.
     */
    DRIVE_UP_TO_GOAL, // drives up the goal.
    /**
     * Brakes once we are in front of the goal.
     */
    STOP_IN_FRONT_OF_GOAL,
    /**
     * Once we are in the shooting position, we align based on the chimera.
     */
    ALIGN_IN_FRONT_OF_GOAL,
    /**
     * We shoot the cannon ball.
     */
    SHOOT, // adjusts its self (?) and fires the cannon ball.

    /**
     * Wait to close the solenoids.
     */
    DELAY_AFTER_SHOOT,

    /**
     * Wait for the arm to come down before crossing the outer works.
     */
    WAIT_FOR_ARM_DESCENT,

    /**
     * We stop, and do nothing else.
     */
    DONE
    }

/**
 * 
 * States to run arm movements in parallel.
 *
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


// ==========================================
// AUTO STATES
// ==========================================

/**
 * The state to be executed periodically throughout Autonomous.
 */
private static MainState mainState = MainState.INIT;

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


private static int accelerationStage = 0;

private static double totalDistance = 0;

/**
 * Run the arm state machine only when necessary (when true).
 */
private static boolean runArmStates = false;
/**
 * Prints print that it prints prints while it prints true.
 */
public static boolean debug;

private static boolean oneTimePrint1 = false;
private static boolean oneTimePrint2 = false;
private static boolean oneTimePrint3 = false;

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

        debug = DEBUGGING_DEFAULT;

        initAutoState();

        Hardware.transmission
                .setDebugState(debugStateValues.DEBUG_NONE);

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
        {
        Hardware.pickupArm.stopArmMotor();
        }


    //Teleop.printStatements();

} // end Periodic


/**
 * Sets the delay time in full seconds based on potentiometer.
 */
public static int initDelayTime ()
{
    return (int) (MAXIMUM_DELAY * Hardware.delayPot.get()
            / Hardware.DELAY_POT_DEGREES);
}

public static void initAutoState ()
{
    mainState = MainState.INIT;
}

/**
 * Called periodically to run the overarching states.
 */
private static void runMainStateMachine ()
{

    //Teleop.printStatements();

    if (debug == true)
    // print out states.
        {
        System.out.println("Main State: " + mainState);
        //	System.out.println("LeftIR: " + Hardware.leftIR.isOn());
        //	System.out.println("RightIR: " + Hardware.rightIR.isOn());

        //	if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
        //	{
        ////	Hardware.errorMessage.printError(
        ////	        (mainState + ": An IR has turned on."), PrintsTo.roboRIO,
        ////	        false);
        //	}

        //System.out.println("Arm Pot: " + Hardware.armPot.get());
        //	Teleop.printStatements();
        //	Hardware.errorMessage.printError(
        //	        "Main State: " + mainState,
        //	        ErrorMessage.PrintsTo.roboRIO);
        //	Hardware.errorMessage.printError(
        //	        "Left:" + Hardware.leftRearEncoder.getDistance(),
        //	        ErrorMessage.PrintsTo.roboRIO);
        //	Hardware.errorMessage.printError(
        //	        "Right:" + Hardware.rightRearEncoder.getDistance(),
        //	        ErrorMessage.PrintsTo.roboRIO);
        // System.out.println("Time: " + Hardware.kilroyTimer.get());
        }

    switch (mainState)
        {
        case INIT:
            // Doesn't do much.
            // Just a Platypus.
            mainInit();

            if (lane == 1 || lane == 6)
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
                Hardware.delayTimer.stop();
                Hardware.delayTimer.reset();
                mainState = MainState.MOVE_TO_OUTER_WORKS;
                }

            break;

        case MOVE_TO_OUTER_WORKS:

            // goes forwards to outer works.
            if ((Hardware.drive.driveStraightByInches(
                    DriveInformation.DISTANCE_TO_OUTER_WORKS
                            * labScalingFactor,
                    false,
                    DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane],
                    DriveInformation.MOTOR_RATIO_TO_OUTER_WORKS[lane])) == true)
            //continue over the outer works unless the arm is going to get in the way.
                {

                Hardware.axisCamera.saveImagesSafely();
                //Teleop.printStatements();

                //continue over the Outer Works
                mainState = MainState.FORWARDS_OVER_OUTER_WORKS;
                resetEncoders();

                //UNLESS...
                //When going under the low bar (lane 1), the arm must be down.
                if ((lane == 1 || lane == 6)
                        && (Hardware.pickupArm.isUnderBar() == false))
                //arm is not down in time. STOP.
                    {
                    mainState = MainState.WAIT_FOR_ARM_DESCENT;
                    }
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
                mainState = MainState.ACCELERATE_FROM_ZERO;
                Hardware.delayTimer.reset();
                Hardware.delayTimer.start();
                }
            break;

        case FORWARDS_OVER_OUTER_WORKS:
            //Drive over Outer Works.
            if (Hardware.drive.driveStraightByInches(
                    DriveInformation.DISTANCE_OVER_OUTER_WORKS
                            * labScalingFactor,
                    false,
                    DriveInformation.DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS[lane],
                    DriveInformation.DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS[lane]) == true)
            //put up all the things we had to put down under the low bar.
            //begin loading the catapult.
                {


                //put up camera.
                Hardware.cameraSolenoid.set(Value.kReverse);



                //Teleop.printStatements();
                resetEncoders();



                //We are over the outer works. Start the arm back up.
                armState = ArmState.MOVE_UP_TO_DEPOSIT;
                runArmStates = true;

                mainState = MainState.FORWARDS_BASED_ON_ENCODERS_OR_IR;


                //temporary; stops after over outer works.
                //3 is chosen for this arbitrarily.
                if (lane == 3)
                    {
                    mainState = MainState.DONE;
                    }

                }
            break;

        case WAIT_FOR_ARM_DESCENT:
            //Stop during the wait. We do not want to ram the bar.
            Hardware.transmission.controls(0.0, 0.0);
            //System.out.println(Hardware.armPot.get());
            if (Hardware.pickupArm.moveToPosition(
                    ManipulatorArm.ArmPosition.FULL_DOWN) == true)
                mainState = MainState.FORWARDS_OVER_OUTER_WORKS;
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
                    DriveInformation.DISTANCE_TO_TAPE
                            * labScalingFactor,
                    false, DriveInformation.MOTOR_RATIO_TO_A_LINE[lane],
                    DriveInformation.MOTOR_RATIO_TO_A_LINE[lane]) == true))
            // when done, proceed from Alignment line.
                {

                //Teleop.printStatements();

                //reset Encoders to prepare for next state.
                resetEncoders();


                //We definitely don't need to rotate.
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
                mainState = MainState.FORWARDS_FROM_ALIGNMENT_LINE;

                //Teleop.printStatements();


                Hardware.delayTimer.reset();
                Hardware.delayTimer.start();
                }
            break;

        case DELAY_IF_REVERSE:

            if (Hardware.delayTimer
                    .get() >= DriveInformation.DELAY_IF_REVERSE[lane])
                {
                mainState = MainState.ROTATE_ON_ALIGNMENT_LINE;
                }
            break;

        case ROTATE_ON_ALIGNMENT_LINE:
            //Rotates until we are pointed at the place from whence we want to shoot.
            if (hasTurnedBasedOnSign(
                    DriveInformation.ROTATE_ON_ALIGNMENT_LINE_DISTANCE[lane]
                            * labScalingFactor) == true)
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
                            * labScalingFactor,
                    true, //breaking here is preferable.
                    DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane],
                    DriveInformation.FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO[lane]) == true)
                {
                //Teleop.printStatements();
                //reset Encoders to prepare for next state.
                resetEncoders();
                mainState = MainState.TURN_TO_FACE_GOAL;
                }
            break;

        case TURN_TO_FACE_GOAL:
            //Turns until we are facing the goal.
            if (hasTurnedBasedOnSign(
                    DriveInformation.TURN_TO_FACE_GOAL_DEGREES[lane]) == true)
            //when done move up to the batter.
                {
                //Teleop.printStatements();
                //reset Encoders to prepare for next state
                resetEncoders();

                //Hardware.ringLightRelay.set(Relay.Value.kOn);

                armState = ArmState.HOLD;

                Hardware.transmission.controls(0.0, 0.0);

                //then drive.
                mainState = MainState.DRIVE_UP_TO_GOAL;
                }
            break;



        case DRIVE_UP_TO_GOAL:

            if (oneTimePrint1 == false)
                {
                oneTimePrint1 = true;
                System.out.println(mainState + "\n\tLeft Encoder: "
                        + Hardware.leftRearEncoder.getDistance()
                        + "\n\tLeft Encoder: "
                        + Hardware.leftRearEncoder.getDistance());
                }

            //Moves to goal. Stops to align.
            if (((Hardware.drive.driveStraightByInches(
                    DriveInformation.DRIVE_UP_TO_GOAL[lane]
                            * labScalingFactor,
                    false,
                    DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane],
                    DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane]) == true)
            //TODO: see if IRs are a problem.
            //			        || (Hardware.leftIR.isOn()
            //			                || Hardware.rightIR.isOn())
            ))
            //Go to align.
                {

                if (oneTimePrint2 == false)
                    {
                    oneTimePrint2 = true;
                    System.out.println(mainState + "\n\tLeft Encoder: "
                            + Hardware.leftRearEncoder.getDistance()
                            + "\n\tLeft Encoder: "
                            + Hardware.leftRearEncoder.getDistance());
                    }

                //reset Encoders to prepare for next state.
                resetEncoders();

                //go to align.
                mainState = MainState.DONE;
                }

            if (oneTimePrint3 == false &&
                    (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
                {
                oneTimePrint3 = true;
                System.out.println("An IR found something.");
                }
            break;

        case STOP_IN_FRONT_OF_GOAL:
        //if (Hardware.drive.brake(.1) == true)
            {
            mainState = MainState.DONE;//MainState.SHOOT;
            }
            break;

        case ALIGN_IN_FRONT_OF_GOAL:
            //align based on the camera until we are facing the goal. head-on.
            Hardware.transmission.controls(0.0, 0.0);

            //TODO: Demystify magic numbers
            if (Hardware.drive.alignByCamera(0.2, 0.2, .4, -.325, -.483,
                    false) == true)
            //Once we are in position, we shoot!
                {
                mainState = MainState.SHOOT;
                }
            break;

        case SHOOT:
            //FIRE!!!
            shoot();
            mainState = MainState.DELAY_AFTER_SHOOT;
            Hardware.axisCamera.saveImagesSafely();
            break;

        case DELAY_AFTER_SHOOT:
            //Check if enough time has passed for the air to have been released.
            if (hasShot() == true)
                {
                Hardware.catapultSolenoid0.set(false);
                Hardware.catapultSolenoid1.set(false);
                Hardware.catapultSolenoid2.set(false);

                mainState = MainState.DONE;
                }
            break;

        case STOP_ALL_MOTORS:
            Hardware.transmission.controls(0.0, 0.0);
            mainState = MainState.DRIVE_UP_TO_GOAL;
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
 * MAIN AUTONOMOUS STATE METHODS
 * ======================================
 */

private static void mainInit ()
{

    resetEncoders();

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

    if (Hardware.pickupArm.isDown() == true)
        {
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
    if ((Hardware.drive.driveStraightByInches(
            DriveInformation.DRIVE_UP_TO_GOAL[lane]
                    * labScalingFactor,
            false,
            DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane],
            DriveInformation.DRIVE_UP_TO_GOAL_MOTOR_RATIO[lane]) == true)
            || (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
    // We are done here.
        {
        done = true;
        }

    // TEMPORARY PRINTS.
    // see if we have stopped based on IR or Encoders.
    //	if (done == true
    //	        && (Hardware.leftIR.isOn() || Hardware.rightIR.isOn()))
    //	{
    //	System.out.println("Stopped by Sensors");
    //	}
    //	else if (Hardware.leftRearEncoder
    //	        .getDistance() >= DriveInformation.DRIVE_UP_TO_GOAL[lane] ||
    //	        Hardware.rightRearEncoder
    //	                .getDistance() >= DriveInformation.DRIVE_UP_TO_GOAL[lane])
    //	{
    //	System.out.println("Stopped by distance.");
    return done;

}

/**
 * <b> FIRE!!! </b>
 * <p>
 * Shoots the ball.
 * 
 */
private static void shoot ()
{

    Hardware.transmission.controls(0.0, 0.0);

    //Make sure the arm is out of the way.
    if (Hardware.pickupArm.isClearOfArm())
        {

        //RELEASE THE KRACKEN! I mean, the pressurized air...
        Hardware.catapultSolenoid0.set(true);
        Hardware.catapultSolenoid1.set(true);
        Hardware.catapultSolenoid2.set(true);

        }
    else
        {
        //TODO: move arm out of way.
        //armState = ArmState.
        }

    //set a timer so that we know when to close the solenoids.
    Hardware.kilroyTimer.reset();
    Hardware.kilroyTimer.start();
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
    autonomousEnabled = false;
    debug = false;
    Hardware.transmission.controls(0.0, 0.0);
    Hardware.armMotor.set(0.0);
    Hardware.delayTimer.stop();
    Hardware.delayTimer.reset();

    Hardware.ringLightRelay.set(Relay.Value.kOff);

    armState = ArmState.DONE;

    System.out.println("Total Distance: " + totalDistance);

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
            //check if down.
            if (Hardware.pickupArm
                    .moveToPosition(ArmPosition.FULL_DOWN) == true)
            //stop.
                {
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
            if (Hardware.pickupArm
                    .moveToPosition(ArmPosition.DEPOSIT) == true)
            //stop, and go to deposit.
                {
                Hardware.pickupArm.move(0.0);
                armState = ArmState.INIT_DEPOSIT;
                }
            break;
        case INIT_DEPOSIT:
            //spin wheels to release ball.
            Hardware.pickupArm.pullInBall(true);
            //armState = ArmState.DEPOSIT;
            break;
        case DEPOSIT:
            //check if the ball is out.
            if (Hardware.pickupArm.ballIsOut())
            //stop rollers, and move down.
                {
                Hardware.pickupArm.stopIntakeArms();
                //get out of the way.
                armState = ArmState.DONE;
                }
            break;
        case HOLD:
            Hardware.pickupArm.stopIntakeArms();
            Hardware.pickupArm.moveToPosition(ArmPosition.HOLD);
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
    int position = Hardware.startingPositionDial.getPosition();

    //-1 is returned when there is no signal. 
    if (position == -1)
    //Go to lane 1 by default.
        {
        position = 0;
        }

    position++;

    return position;
}

/**
 * Reset left and right encoders.
 * To be called at the end of any state that uses Drive.
 */
public static void resetEncoders ()
{
    totalDistance += (Hardware.leftRearEncoder.getDistance()
            + Hardware.rightRearEncoder.get()) / 2;
    Hardware.leftRearEncoder.reset();
    Hardware.rightRearEncoder.reset();
}

/**
 * For turning in drive based on array of positive and negative values.
 * Use to turn a number of degrees
 * COUNTERCLOCKWISE.
 * Kilroy must turn along different paths.
 * You must use this to be versatile.
 */
private static boolean hasTurnedBasedOnSign (double degrees,
        double turnSpeed)
{
    boolean done = false;

    if (degrees < 0)
    //Turn right. Make degrees positive.
        {
        done = Hardware.drive.turnRightDegrees(-degrees, false,
                turnSpeed,
                -turnSpeed);
        }
    else
    //Turn left the given number of degrees.
        {
        done = Hardware.drive.turnLeftDegrees(degrees, false,
                -turnSpeed,
                turnSpeed);
        }
    return done;

}

/**
 * For turning in drive based on array of positive and negative values.
 * Use to turn a number of degrees
 * COUNTERCLOCKWISE.
 * Kilroy must turn along different paths.
 * You must use this to be versatile.
 */
private static boolean hasTurnedBasedOnSign (double degrees)
{
    return hasTurnedBasedOnSign(degrees,
            DriveInformation.DEFAULT_TURN_SPEED);
}

/**
 * Contains distances and speeds at which to drive.
 *
 * TODO: Figure out reasonable speeds, etc.
 */
private static final class DriveInformation
{

private static final double[] ACCELERATION_RATIOS =
    {
            0.1,
            0.2,
            0.3
    };

private static final double[] ACCELERATION_TIMES =
    {
            0.2,
            0.4,
            0.6
    };

private static final double[] DRIVE_OVER_OUTER_WORKS_MOTOR_RATIOS =
    {
            0.0,
            0.4,
            0.7,
            0.7,
            0.7,
            0.7,
            0.4
    };

/**
 * For each lane, decides whether or not to break on the Alignment Line
 */
private static final boolean[] BREAK_ON_ALIGNMENT_LINE =
    {
            false, // A placeholder, allowing lane to line up with index.
            false, //lane 1
            false, //lane 2
            true, // lane 3
            true, // lane 4
            false, // lane 5
            true //backup plan
    };

/**
 * The motor controller values for moving to the outer works.
 * As these are initial speeds, keep them low, to go easy on the motors.
 * Lane is indicated by index.
 */
static final double[] MOTOR_RATIO_TO_OUTER_WORKS =
    {
            0.0, // nothing. Not used. Arbitrary; makes it work.
            0.40,//0.25, // lane 1, should be extra low.
            1.0, // lane 2
            0.4, // lane 3
            0.4, // lane 4
            0.4, // lane 5
            0.4 //backup plan
    };

/**
 * "Speeds" at which to drive from the Outer Works to the Alignment
 * Line.
 */
static final double[] MOTOR_RATIO_TO_A_LINE =
    {
            0.0, //PLACEHOLDER
            0.4, //lane 1
            0.6, //lane 2
            0.4, //lane 3
            0.4, //lane 4
            0.6,  //lane 5
            0.0 //backup plan
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
            0.0, // lane 5 (not neccesary)
            0.0 //backup plan
    };

/**
 * Distances to drive after reaching alignment tape.
 * Lane is indicated by index.
 * 16 inchworms added inevitably, to start from centre of robot.
 */
static final double[] FORWARDS_FROM_ALIGNMENT_LINE_DISTANCE =
    {
            0.0, // nothing. Not used. Arbitrary; makes it work.
            48.57,// lane 1
            77.44,//68.0,// lane 2
            64.0, // lane 3
            66.1,// lane 4
            86.5, // lane 5
            -169.0 //backup plan
    };

static final double[] CENTRE_TO_ALIGNMENT_LINE_MOTOR_RATIO =
    {
            0.0,
            0.4, //1
            0.4, //2
            .25, //3
            .25, //4
            0.4, //5
            0.25 //backup plan
    };

/**
 * "Speeds" at which to drive from the A-Line to the imaginary line
 * normal to
 * the goal.
 */
static final double[] FORWARDS_FROM_ALIGNMENT_LINE_MOTOR_RATIO =
    {
            0.0, // nothing. A Placeholder.
            0.4, //lane 1
            0.4, //lane 2
            0.3, //lane 3
            0.3, //lane 4
            0.4,  //lane 5
            -0.5 //backup plan
    };

/**
 * Distances to rotate to face goal.
 */
static final double[] TURN_TO_FACE_GOAL_DEGREES =
    {
            0.0, // makes it so the indexes line up with the lane #
            -60.0,// lane 1
            -60.0,// lane 2
            20.0,// lane 3
            -24.85,// lane 4
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
static final double[] DRIVE_UP_TO_GOAL =
    {
            0.0, // nothing. Not used. Arbitrary; makes it work.
            65.85,//previously 62.7,// lane 1
            18.1,//52.9,// lane 2
            0.0,// lane 3 (not neccesary)
            0.0,// lane 4 (not neccesary)
            12.0, // lane 5
            0.0 //backup plan
    };

/**
 * "Speeds" at which to drive to the Batter.
 */
static final double[] DRIVE_UP_TO_GOAL_MOTOR_RATIO =
    {
            0.0,
            0.4,
            0.4,
            0.4,
            0.4,
            0.4,
            0.0
    };

/**
 * Time to delay at A-line. Only used if reversing.
 */
static final double[] DELAY_IF_REVERSE =
    {
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            1.0
    };


/**
 * Distance from Outer Works checkpoint to Alignment Line.
 * The front of the robot will be touching the Lion.
 */
private static final double DISTANCE_TO_TAPE = 27.5;


/**
 * Distance to get the front of the robot to the Outer Works.
 */
private static final double DISTANCE_TO_OUTER_WORKS = 22.75;

/**
 * Distance to travel to get over the Outer Works.
 */
private static final double DISTANCE_OVER_OUTER_WORKS = 98.86;

/**
 * The distance to the central pivot point from the front of the robot.
 * We will use this so that we may rotate around a desired point at the
 * end of a
 * distance.
 */
private static final double DISTANCE_TO_CENTRE_OF_ROBOT = 16.0;

/**
 * Speed at which to make turns by default.
 * TODO: figure out a reasonable speed.
 */
private static final double DEFAULT_TURN_SPEED = 0.4; //previously 0.28

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
 * TODO: Actually, we are not currently using this.
 */
private static final double MAXIMUM_AUTONOMOUS_SPEED = 1.0;

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

} // end class
