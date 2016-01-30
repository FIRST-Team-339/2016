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
    DELAY, // waits, depending on settings.
    FORWARDS_TO_TAPE, // drives forwards until detection of the gaffers' tape.
    ALIGN, // aligns its self on the gaffers' tape based of IR sensors.
    MOVE_TO_SHOOTING_POSITION,  // moves towards a good shooting angle based on
                              // settings.
    SHOOT, // ajusts its self (?) and fires the cannonball.
    DONE
    }

private static enum MoveToShootingPositionStep
    {
    INIT, // beginning
    ROTATE_ZERO, // rotates given number before moving forwards.
    FORWARDS_ONE, // moves forwards.
    ROTATE_ONE, // pauses to rotate.
    FORWARDS_TWO, // continues to move forwards.
    DONE
    }

private static enum StartingPosition
    {
    ONE, TWO, THREE, FOUR, FIVE

    }

private static enum AlignmentState
    {
    NEITHER_ON_TAPE, LEFT_ON_TAPE, RIGHT_ON_TAPE, BOTH_ON_TAPE
    }

private static boolean leftSensorIsOnTape;

private static boolean rightSensorIsOnTape;

// distances we'll be driving in autonomous
private static class DriveDistance
{
}

// ==========================================
// AUTO STATES
// ==========================================
private static MainState mainState = MainState.INIT;
private static MoveToShootingPositionStep moveToShootingPositionStep =
        MoveToShootingPositionStep.INIT;
private static StartingPosition startingPosition = StartingPosition.ONE;
private static AlignmentState alignmentState =
        AlignmentState.NEITHER_ON_TAPE;

// ==================================
// VARIABLES
// ==================================
private static double delay; // time to delay before begining.


private static double rotate0; // amount to rotate in ROTATE_ZERO sub-state.
private static double forwards1; // amount to move forwards in FORWARDS_ONE
                                // sub-state.
private static double rotate1; // amount to rotate in ROTATE_ONE sub-state.
private static double forwards2; // amount to move forwards in FORWARDS_TWO
                                // sub-state.


// ==========================================
// TUNEABLES
// ==========================================

/*
 * User-Initialization code for autonomous mode should go here. Will be
 * called once when the robot enters autonomous mode.
 *
 * @author Nathanial Lydick
 *
 * @written Jan 13, 2015
 */
public static void init ()
{

    // set the delay time based on potentiometer.
    initDelayTime();

    // set Starting Position based on Six-Position Switch.
    initStartingPosition();

    // set the drive values for MOVE_TO_SHOOTING_POSITION
    initGoalPath();




    // -------------------------------------
    // close both of the cameras in case they
    // were previously started in a previous
    // run. Then, change the camera to one that
    // will eventually process images.
    // ------------------------------------

    // ---------------------------------------
    // turn the timer off and reset the counter
    // so that we can use it in autonomous
    // ---------------------------------------
    Hardware.kilroyTimer.stop();
    Hardware.kilroyTimer.reset();
    Hardware.delayTimer.start();
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
    // runs the overarching state machine.
    runMainStateMachine();
} // end Periodic


/**
 * Sets the delay time in full seconds based on potentiometer.
 */
private static void initDelayTime ()
{
    delay = (int) MAXIMUM_DELAY * Hardware.delayPot.get()
            / ONE_THOUSAND;
}

/**
 * Sets startingPosition based on six-position switch.
 */
private static void initStartingPosition ()
{
    switch (Hardware.startingPositionDial.getPosition())
        {
        case 0:
            startingPosition = StartingPosition.ONE;
            break;
        case 1:
            startingPosition = StartingPosition.TWO;
            break;
        case 2:
            startingPosition = StartingPosition.THREE;
            break;
        case 3:
            startingPosition = StartingPosition.FOUR;
            break;
        case 4:
            startingPosition = StartingPosition.FIVE;
            break;
        default:
            // why?
            break;
        }
}

/**
 * Sets distances to be traveled during FORWARDS_TO_SHOOTING_POSITION based on
 * the starting position of the robot.
 * Distances can be found in the <b>constants<\b> section.
 */
private static void initGoalPath ()
{
    switch (startingPosition)
        {
        case ONE:
            rotate0 = ROTATE_ZERO_ONE;
            forwards1 = FORWARDS_ONE_ONE;
            rotate1 = ROTATE_ONE_ONE;
            forwards2 = FORWARDS_TWO_ONE;
            break;
        case TWO:
            rotate0 = ROTATE_ZERO_TWO;
            forwards1 = FORWARDS_ONE_TWO;
            rotate1 = ROTATE_ONE_TWO;
            forwards2 = FORWARDS_TWO_TWO;
            break;
        case THREE:
            rotate0 = ROTATE_ZERO_THREE;
            forwards1 = FORWARDS_ONE_THREE;
            rotate1 = ROTATE_ONE_THREE;
            forwards2 = FORWARDS_TWO_THREE;
            break;
        case FOUR:
            rotate0 = ROTATE_ZERO_FOUR;
            forwards1 = FORWARDS_ONE_FOUR;
            rotate1 = ROTATE_ONE_FOUR;
            forwards2 = FORWARDS_TWO_FOUR;
            break;
        case FIVE:
            rotate0 = ROTATE_ZERO_FIVE;
            forwards1 = FORWARDS_ONE_FIVE;
            rotate1 = ROTATE_ONE_FIVE;
            forwards2 = FORWARDS_TWO_FIVE;
            break;
        }
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
            mainStateMachineInit();
        case DELAY:
            delay();
            break;
        case FORWARDS_TO_TAPE:
            forwardsToTape();
            break;
        case ALIGN:
            align();
            break;
        case MOVE_TO_SHOOTING_POSITION:
            moveToShootingPosition();
            break;
        case SHOOT:
            shoot();
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

public static void mainStateMachineInit ()
{

    if (Hardware.autonomousEnabled.isOn() == true)
        {

        mainState = MainState.DELAY;

        }
    else
        {
        mainState = MainState.DONE;
        }
    //testing
    //TODO: remove
    mainState = MainState.MOVE_TO_SHOOTING_POSITION;
}


/**
 * Waits.
 * Continues to FORWARDS_TO_TAPE when time is up.
 * One of the overarching states.
 */
private static void delay ()
{
    if (Hardware.delayTimer.get() > delay)
        {
        mainState = MainState.FORWARDS_TO_TAPE;
        Hardware.delayTimer.stop();
        Hardware.delayTimer.reset();
        }
}

private static void forwardsToTape ()
{

    Hardware.drive.driveForwardInches(0.0);
    if (Hardware.leftIR.isOn() && Hardware.rightIR.isOn())
        {
        alignmentState = AlignmentState.BOTH_ON_TAPE;
        mainState = MainState.ALIGN;
        }
    else if (Hardware.leftIR.isOn())
        {
        alignmentState = AlignmentState.LEFT_ON_TAPE;
        mainState = MainState.ALIGN;
        }
    else if (Hardware.rightIR.isOn())
        {
        alignmentState = AlignmentState.RIGHT_ON_TAPE;
        mainState = MainState.ALIGN;
        }

}



/**
 * Aligns robot on gaffers' tape based on IR sensors.
 * One of the overarching states.
 */
private static void align ()
{
    System.out.println("Alignment State: " + alignmentState);
    switch (alignmentState)
        {
        case NEITHER_ON_TAPE:
            alignFind();
            break;
        case LEFT_ON_TAPE:
            alignRightSide();
            break;
        case RIGHT_ON_TAPE:
            alignLeftSide();
            break;
        case BOTH_ON_TAPE:
            alignFinish();
            break;
        }
}

/**
 * Moves the robot from the gaffers' tape to a position in front of a goal.
 * Movements are based on the robot's initial position.
 * Guided by the Drive utility class.
 * TODO: write Drive utility class.
 * One of the overarching states.
 */
private static void moveToShootingPosition ()
{
    System.out.println(
            "MoveToShoot State: " + moveToShootingPositionStep);
    switch (moveToShootingPositionStep)
        {
        case INIT:
            moveToShootingPositionInit();
            break;
        case ROTATE_ZERO:
            // if not needed, set rotate0 to 0.
            rotateZero();
            break;
        case FORWARDS_ONE:
            forwardsOne();
            break;
        case ROTATE_ONE:
            rotateOne();
            break;
        case FORWARDS_TWO:
            // if not needed, set forwards2 to 0.
            forwardsTwo();
            break;
        case DONE:
            break;
        }
}

private static void shoot ()
{
    // TODO: write method to shoot cannonball.
}

/*
 * =============================================
 * END OF MAIN AUTONOMOUS STATE METHODS
 * =========================================
 */


/*
 * ==============================================
 * MOVE_TO_SHOOTING_POSITION SUB-STATE METHODS
 * ==============================================
 */

private static void moveToShootingPositionInit ()
{
    moveToShootingPositionStep = MoveToShootingPositionStep.ROTATE_ZERO;
}

private static void rotateZero ()
{
    if (Hardware.drive.turnLeftDegrees(rotate0))
        {
        moveToShootingPositionStep =
                MoveToShootingPositionStep.FORWARDS_ONE;
        }
}

private static void forwardsOne ()
{
    if (Hardware.drive.driveForwardInches(forwards1))
        {
        moveToShootingPositionStep =
                MoveToShootingPositionStep.ROTATE_ONE;
        }
}

private static void rotateOne ()
{
    if (Hardware.drive.turnLeftDegrees(rotate1))
        ;
        {
        moveToShootingPositionStep =
                MoveToShootingPositionStep.FORWARDS_TWO;
        }
}

private static void forwardsTwo ()
{
    if (Hardware.drive.driveForwardInches(forwards2))
        {
        moveToShootingPositionStep = MoveToShootingPositionStep.DONE;
        }
}



/*
 * ==============================================
 * END OF MOVE_TO_SHOOTING_POSITION SUB-STATE METHODS
 * ==============================================
 */


/*
 * ==============================================
 * ALIGN SUB-STATE METHODS
 * ==============================================
 */

/**
 * Used in the unlikely disastrous scenario that the robot is in ALIGN, but does
 * not see any tape.
 * In theory, we should never need this.
 * TODO: Write find method.
 */
private static void alignFind ()
{

}

/**
 * Moves right side to tape when left side is on.
 */
private static void alignRightSide ()
{
    double leftAlignmentSpeed = 0.0;
    double rightAlignmentSpeed = 0.0;

    rightAlignmentSpeed = ALIGNMENT_SPEED;

    if (Hardware.rightIR.isOn() && Hardware.leftIR.isOn())
        {
        rightAlignmentSpeed = 0.0;
        alignmentState = AlignmentState.BOTH_ON_TAPE;
        }
    else if (Hardware.leftIR.isOn() == false)
        {
        leftAlignmentSpeed = -ALIGNMENT_SPEED;
        }

    Hardware.transmissionFourWheel.drive(rightAlignmentSpeed,
            leftAlignmentSpeed);
}

/**
 * Moves left side to tape when right side is on.
 * Moves right side back if right side turns off.
 */
private static void alignLeftSide ()
{

    double leftAlignmentSpeed = 0.0;
    double rightAlignmentSpeed = 0.0;

    leftAlignmentSpeed = ALIGNMENT_SPEED;

    if (Hardware.rightIR.isOn() && Hardware.leftIR.isOn())
        {
        leftAlignmentSpeed = 0.0;
        alignmentState = AlignmentState.BOTH_ON_TAPE;
        }
    else if (Hardware.rightIR.isOn() == false)
        {
        rightAlignmentSpeed = -ALIGNMENT_SPEED;
        }

    Hardware.transmissionFourWheel.drive(rightAlignmentSpeed,
            leftAlignmentSpeed);
}

private static void alignFinish ()
{
    mainState = MainState.MOVE_TO_SHOOTING_POSITION;
}
/*
 * ==============================================
 * END ALIGN SUB-STATE METHODS
 * ==============================================
 */


// __ _ __
// .................../ \
// ................./ --0 --- 0-- \
// .........++++.. |- - - | | - - -| ..++++
/*---------//||\\--|     /   \     |--//||\\-------
//Constants\\||//       |     |       \\||//
//------ ---------------|     |--------------------*/
// ----------------------\___/
// ...............................|<!(r% ~@$ #3r3


private static final double MAXIMUM_DELAY = 4.0;
private static final int ONE_THOUSAND = 1000;

private static final double ALIGNMENT_SPEED = 0.1;

/*------------------------------------------------------------------------------
Below are the distances traveled by the robot in
FORWARDS_TO_SHOOTING_POSITION. The order
of execution is stated by the first number. The
starting position is denoted by the second.
------------------------------------------------------------------------------*/

private static final double ROTATE_ZERO_ONE = 0.0;
private static final double FORWARDS_ONE_ONE = 74.7;
private static final double ROTATE_ONE_ONE = -60;
private static final double FORWARDS_TWO_ONE = 62.7;

private static final double ROTATE_ZERO_TWO = 0.0;
private static final double FORWARDS_ONE_TWO = 82.0;
private static final double ROTATE_ONE_TWO = -60;
private static final double FORWARDS_TWO_TWO = 52.92;

private static final double ROTATE_ZERO_THREE = -20.0;
private static final double FORWARDS_ONE_THREE = 64.0;
private static final double ROTATE_ONE_THREE = 20.0;
private static final double FORWARDS_TWO_THREE = 0.0;

private static final double ROTATE_ZERO_FOUR = 24.8;
private static final double FORWARDS_ONE_FOUR = 66.1;
private static final double ROTATE_ONE_FOUR = -24.8;
private static final double FORWARDS_TWO_FOUR = 0.0;

private static final double ROTATE_ZERO_FIVE = 0.0;
private static final double FORWARDS_ONE_FIVE = 86.5;
private static final double ROTATE_ONE_FIVE = 60.0;
private static final double FORWARDS_TWO_FIVE = 12.0;

} // end class
