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
import org.usfirst.frc.team339.Utils.DriveInstruction;
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
    INIT, // beginning, check conditions
    BEGIN_LOWERING_ARM, LOWER_ARM_AND_MOVE,//
    INIT_DELAY,// sets delay timer.
    DELAY, // waits, depending on settings.
    ACCELERATE, // Accelerates at beginning.
    FORWARDS_BASED_ON_ENCODERS_OR_IR, // decides based on lane whether to move
                                     // to tape based on encoders or IR
    FORWARDS_TO_TAPE_BY_DISTANCE, // drives the distance required to the tape.
    FORWARDS_UNTIL_TAPE, // drives forwards until detection of the gaffers'
                        // tape.
    MOVE_TO_SHOOTING_POSITION,  // moves towards a good shooting angle based on
                              // settings.
    SHOOT, // ajusts its self (?) and fires the cannonball.
    DONE
    }


private static enum MoveWhileLoweringArmReturn
    {
    NOT_DONE, DONE, FAILED
    }


private static enum AlignmentState
    {
    NEITHER_ON_TAPE, LEFT_ON_TAPE, RIGHT_ON_TAPE, BOTH_ON_TAPE
    }



/**
 * A collection of distances we'll be driving in autonomous.
 */
private static class DriveDistances
{


/*
 * Below are the distances traveled by the robot in
 * FORWARDS_TO_SHOOTING_POSITION. The order
 * of execution is stated by the first number. The
 * starting position is denoted by the second.
 */
public static final double ROTATE_ZERO_ONE = 0.0;
public static final double FORWARDS_ONE_ONE = 74.7;
public static final double ROTATE_ONE_ONE = -60.0;
public static final double FORWARDS_TWO_ONE = 62.7;

public static final double ROTATE_ZERO_TWO = 0.0;
public static final double FORWARDS_ONE_TWO = 82.0;
public static final double ROTATE_ONE_TWO = -60.0;
public static final double FORWARDS_TWO_TWO = 52.92;

public static final double ROTATE_ZERO_THREE = -20.0;
public static final double FORWARDS_ONE_THREE = 64.0;
public static final double ROTATE_ONE_THREE = 20.0;
public static final double FORWARDS_TWO_THREE = 0.0;

public static final double ROTATE_ZERO_FOUR = 24.8;
public static final double FORWARDS_ONE_FOUR = 66.1;
public static final double ROTATE_ONE_FOUR = -24.8;
public static final double FORWARDS_TWO_FOUR = 0.0;

public static final double ROTATE_ZERO_FIVE = 0.0;
public static final double FORWARDS_ONE_FIVE = 86.5;
public static final double ROTATE_ONE_FIVE = 60.0;
public static final double FORWARDS_TWO_FIVE = 12.0;
}


private static final DriveInstruction[] driveOverDefencesInstuctions =
        {
                new DriveInstruction(106.0, 1.0, 0.0, 0.0)
        };

/**
 * Contains information for driving to a goal from A-tape.
 * Rows indicate starting position.
 * Columns contain steps for each path.
 */
public static final DriveInstruction[][] driveToGoalInstructions =
        {
                {// From Starting Position 1
                        new DriveInstruction(74.7, 1.0, 0.0, 0.0), // drive out
                        new DriveInstruction(0.0, 0.0, -60.0, 1.0), // turn
                        // perpendicular
                        // to goal
                        new DriveInstruction(62.7, 1.0, 0.0, 0.0), // drive up to
                        // goal
                        new DriveInstruction(true) // continue to shoot
                },
                {// From Starting Position 2
                        new DriveInstruction(82.0, 1.0, 0.0, 0.0), // drive out
                        new DriveInstruction(0.0, 0.0, -60.0, 1.0), // turn
                        // perpendicular
                        // to goal
                        new DriveInstruction(52.92, 1.0, 0.0, 0.0), // drive up to
                        // goal
                        new DriveInstruction(true)// continue to shoot
                },
                {// From Starting Position 3
                        new DriveInstruction(0.0, 0.0, -20.0, 1.0), // turn towards
                        // end position
                        new DriveInstruction(64.0, 1.0, 0.0, 0.0), // drive to end
                        // position
                        new DriveInstruction(0.0, 0.0, 60.0, 1.0), // turn towards
                        // goal
                        new DriveInstruction(true) // continue to shoot
                },
                {// From Starting Position 4
                        new DriveInstruction(0.0, 0.0, -24.8, 1.0), // turn towards
                        // end position
                        new DriveInstruction(64.0, 1.0, 0.0, 0.0), // drive to end
                        // position
                        new DriveInstruction(0.0, 0.0, 64.8, 1.0), // turn towards
                        // goal
                        new DriveInstruction(true) // continue to shoot
                },
                {// From Starting Position 5
                        new DriveInstruction(86.5, 1.0, 0.0, 0.0),// drive out
                        new DriveInstruction(0.0, 0.0, 60.0, 0.0),// turn
                        // perpendicular
                        // to goal
                        new DriveInstruction(12.0, 1.0, 0.0, 0.0),// drive up to
                        // goal
                        new DriveInstruction(true) // continue to shoot
                }
        };

// ==========================================
// AUTO STATES
// ==========================================
private static MainState mainState = MainState.INIT;

// ==================================
// VARIABLES
// ==================================
private static boolean enabled;

private static double delay; // time to delay before begining.

private static int lane;

private static int accelerationIndex = 0;

/**
 * The index at which moveToShootingPosition looks for drive information.
 */
private static int driveToShootingPositionStep = 0;

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





    // set the drive values for MOVE_TO_SHOOTING_POSITION
    // set the drive values for MOVE_TO_SHOOTING_POSITION

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
    Hardware.transmission
            .setFirstGearPercentage(MAXIMUM_AUTONOMOUS_SPEED);

    // --------------------------------------
    // Encoder Initialization
    // --------------------------------------
    Hardware.leftRearEncoder.reset();
    Hardware.leftRearEncoder.setDistancePerPulse(0.019706);

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










    System.out.println(enabled);
    if (enabled)
        {
        // runs the overarching state machine.
        runMainStateMachine();
        }
    // feed all motor safties
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

    System.out.println("Main State: " + mainState);
    switch (mainState)
        {
        case INIT:
            mainInit();
            mainState = MainState.BEGIN_LOWERING_ARM;
            break;
        case BEGIN_LOWERING_ARM:
            beginLoweringArm();

            break;
        case LOWER_ARM_AND_MOVE:
            switch (lowerArmAndMove())
                {
                case NOT_DONE:
                    mainState = MainState.DONE;
                    break;
                case DONE:
                    mainState = MainState.INIT_DELAY;
                    break;
                case FAILED:
                    mainState = MainState.DONE;
                    break;
                }
            break;
        case INIT_DELAY:
            initDelay();
            mainState = MainState.DELAY;
            break;
        case DELAY:
            if (delayIsDone())
                {
                mainState = MainState.ACCELERATE;
                }
            break;
        case ACCELERATE:
            if (accelerationIsDone())
                {
                mainState = MainState.FORWARDS_BASED_ON_ENCODERS_OR_IR;
                }
            break;
        case FORWARDS_BASED_ON_ENCODERS_OR_IR:
            if (isInLaneOne())
                {
                mainState = MainState.FORWARDS_TO_TAPE_BY_DISTANCE;
                }
            else
                {
                mainState = MainState.FORWARDS_UNTIL_TAPE;
                }
            break;
        case FORWARDS_TO_TAPE_BY_DISTANCE:
            if (hasGoneToTapeByDistance())
                {
                mainState = MainState.MOVE_TO_SHOOTING_POSITION;
                }
            break;
        case FORWARDS_UNTIL_TAPE:
            if (hasMovedToTape())
                {
                mainState = MainState.MOVE_TO_SHOOTING_POSITION;
                }
            break;
        case MOVE_TO_SHOOTING_POSITION:
            if (hasMovedToShootingPostion())
                {

                }
            break;
        case SHOOT:
            mainState = shoot();
            break;
        case DONE:
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

private static void beginLoweringArm ()
{
    Hardware.armEncoder.reset();
    Hardware.armMotor.set(1.0);

}


private static MoveWhileLoweringArmReturn lowerArmAndMove ()
{
    MoveWhileLoweringArmReturn returnState =
            MoveWhileLoweringArmReturn.NOT_DONE;
    boolean armIsDown = false;

    Hardware.transmission.controls(1.0, 1.0, Hardware.leftFrontMotor,
            Hardware.leftRearMotor, Hardware.rightFrontMotor,
            Hardware.rightRearMotor);
    ;

    if (Hardware.armEncoder.get() > ARM_DOWN_TICKS)// TODO: set this to a known
                                                   // distance
        {
        armIsDown = true;
        Hardware.armMotor.set(0.0);
        }
    if (Hardware.armEncoder.getDistance() > 8)// TODO: set this to a known
                                              // distance
        {
        armIsDown = true;
        Hardware.armMotor.set(0.0);
        }

    if (Hardware.drive.driveForwardInches(22.75))// TODO: make constant
        {
        if (armIsDown)
            {
            returnState = MoveWhileLoweringArmReturn.DONE;
            }
        else
            {
            returnState = MoveWhileLoweringArmReturn.FAILED;
            }
        }
    if (Hardware.drive.driveForwardInches(22.75))// TODO: make constant
        {
        if (armIsDown)
            {
            returnState = MoveWhileLoweringArmReturn.DONE;
            }
        else
            {
            returnState = MoveWhileLoweringArmReturn.FAILED;
            }
        }

    return returnState;
}






/**
 * Starts the delay timer.
 */
private static MainState initDelay ()
{
    MainState returnState = MainState.DELAY;


    Hardware.delayTimer.reset();
    Hardware.delayTimer.start();
    Hardware.delayTimer.reset();
    Hardware.delayTimer.start();

    if (Hardware.drive.driveForwardInches(22.75))
        {
        returnState = MainState.DELAY;
        }

    return returnState;
}

/**
 * Waits.
 * Continues to ACCELERATE when time is up.
 * One of the overarching states.
 */
private static boolean delayIsDone ()
{
    boolean done = false;

    if (Hardware.delayTimer.get() > delay)
        {
        done = true;
        Hardware.delayTimer.stop();
        Hardware.delayTimer.reset();
        }
    return done;

}

/**
 * Increases power to the motors step-by-step,
 * based on ACCELERATE_SPEEDS and ACCELERATE_TIMES.
 * 
 * @return
 */
private static boolean accelerationIsDone ()
{
    boolean done = false;


    // If there are no more acceleration steps, go to next.
    if (accelerationIndex == StateInformation.ACCELERATE_SPEEDS.length)
        {
        done = true;
        }
    else
        {
        Hardware.transmission.controls(
                StateInformation.ACCELERATE_SPEEDS[accelerationIndex],
                StateInformation.ACCELERATE_SPEEDS[accelerationIndex]);



        if (Hardware.kilroyTimer
                .get() > StateInformation.ACCELERATE_TIMES[accelerationIndex])
            ;
            {
            Hardware.kilroyTimer.reset();
            accelerationIndex++;
            }
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
    boolean oneness;


    if (lane == 1)
        {
        oneness = true;
        }
    else
        {
        oneness = false;
        }


    return oneness;
}

private static boolean hasGoneToTapeByDistance ()
{

    boolean hasReachedDistance = false;

    MainState returnState = MainState.FORWARDS_TO_TAPE_BY_DISTANCE;

    Hardware.transmission.controls(1.0, 1.0);

    if (Hardware.drive.driveForwardInches(DISTANCE_TO_TAPE))
        {
        return true;
        }

    return hasReachedDistance;
}

private static boolean hasMovedToTape ()
{

    boolean tapeness = false;

    MainState returnState = MainState.FORWARDS_UNTIL_TAPE;

    Hardware.transmission.controls(1.0, 1.0);// TODO: set constants

    if (Hardware.leftIR.isOn() || Hardware.rightIR.isOn())
        {
        tapeness = true;
        }


    return tapeness;

}

/**
 * Aligns robot on gaffers' tape based on IR sensors.
 * One of the overarching states.
 */


/**
 * Moves the robot from the gaffers' tape to a position in front of a goal.
 * Movements are based on the robot's initial position.
 * Guided by the Drive utility class.
 * One of the overarching states.
 */
private static boolean hasMovedToShootingPostion ()
{
    boolean done = false;

    // The required distance to drive is taken from the pathToGoalInformation 2d
    // Array.
    DriveInstruction currentInstruction =
            driveToGoalInstructions[Hardware.startingPositionDial
                    .getPosition()][driveToShootingPositionStep];

    if (Hardware.drive.driveForwardInches(
            currentInstruction.getForwardDistance()) // Drive, and if we have
            // driven the distance
            // required
            || Hardware.drive.driveForwardInches(
                    currentInstruction.getRotationalDistance())) // Or the
                                                                                                     // rotation...
        {

            {



            driveToShootingPositionStep++; // go to next step.

            if (currentInstruction.isTerminator())// If at end of path, go to
                                                  // next state.
                {
                done = true;// The next state should be to
                            // shoot, or possibly to align
                            // with vision processing.
                }
            }
        if (currentInstruction.isTerminator())// If at end of path, go to next
                                              // state.
            {
            done = true;// The next state should be to shoot,
                        // or possibly to align with vision
                        // processing.
            }
        }


    return done;
}

private static MainState shoot ()
{
    // TODO: write method to shoot cannonball.

    return MainState.DONE;
}

private static void done ()
{
    Hardware.transmission.controls(0.0, 0.0);
}

/*
 * =============================================
 * END OF MAIN AUTONOMOUS STATE METHODS
 * =========================================
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


private static final class StateInformation
{

// Each index refers to a higher starting speed.
// TODO: different based on lanes.
static final double[] ACCELERATE_SPEEDS =
        {.20, .40, .70};
static final double[] ACCELERATE_TIMES =
        {.5, .5, .5};

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


// .....................__ _ __
// .................../ . . . \
// ................./ --0 --- 0-- \
// .........++++.. |- - - | | - - -| ..++++
/*
 * =========//||\\==| / \ |==//||\\======
 * //Constants\\||// | | \\||//
 * //======================| |==================
 */
// ----------------------\___/
// ...............................|<!(r% ~@$ #3r3


private static final double MAXIMUM_AUTONOMOUS_SPEED = 0.2;

private static final double MAXIMUM_DELAY = 3.0;
private static final int ONE_THOUSAND = 1000;

private static final double ALIGNMENT_SPEED = 0.1;

private static final double DISTANCE_TO_TAPE = 0; // TODO: set to known value

/**
 * Encoder distance for arm.
 * TODO: set
 */
private static final double ARM_DOWN_TICKS = 10.0;

} // end class
