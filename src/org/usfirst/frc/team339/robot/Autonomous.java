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

private static class States
{

private class StateInformation
{
double forwardDistance;
double velocity;

double rotationalDistance;

public StateInformation (double forwardDistance, double velocity)
{
    this.forwardDistance = forwardDistance;
}
}

public StateInformation[][] states =
        {
                {
                        new StateInformation(0, 0)
                }
        };//TODO: set up

public double[][] distances =
        {
                {0.0, 74.7, -60, 62.7},
                {0.0, 82.0, -60.0, 52.92},
                {-20.0, 64.0, 20.0, 0.0},
                {24.8, 66.1, -24.8, 0.0},
                {0.0, 86.5, 60.0, 12.0}
        };
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

    // set the delay time based on potentiometer.
    initDelayTime();

    // set Starting Position based on Six-Position Switch.
    initStartingPosition();

    // set the drive values for MOVE_TO_SHOOTING_POSITION
    initGoalPath();

    Hardware.drive.setMaxSpeed(MAXIMUM_AUTONOMOUS_SPEED);


    // -------------------------------------
    // motor initialization
    // -------------------------------------
    Hardware.leftRearMotor.enableBrakeMode(true);
    Hardware.rightRearMotor.enableBrakeMode(true);
    Hardware.leftFrontMotor.enableBrakeMode(true);
    Hardware.rightFrontMotor.enableBrakeMode(true);
    Hardware.leftRearMotorSafety.setSafetyEnabled(true);
    Hardware.rightRearMotorSafety.setSafetyEnabled(true);
    Hardware.leftFrontMotorSafety.setSafetyEnabled(true);
    Hardware.rightFrontMotorSafety.setSafetyEnabled(true);
    //    Hardware.transmissionFourWheel.setLeftMotorDirection(
    //            Transmission.MotorDirection.REVERSED);

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

    //test
    //TransmissionFourWheel debugTrans = Hardware.transmissionFourWheel;
    //moveToShootingPositionStep = MoveToShootingPositionStep.FORWARDS_ONE;
    Hardware.transmission.controls(Hardware.leftDriver.getY(),
            Hardware.rightDriver.getY());

    // runs the overarching state machine.
    //runMainStateMachine();


    //feed all motor safties
    Hardware.leftRearMotorSafety.feed();
    Hardware.rightRearMotorSafety.feed();
    Hardware.leftFrontMotorSafety.feed();
    Hardware.rightFrontMotorSafety.feed();
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
            rotate0 = DriveDistances.ROTATE_ZERO_ONE;
            forwards1 = DriveDistances.FORWARDS_ONE_ONE;
            rotate1 = DriveDistances.ROTATE_ONE_ONE;
            forwards2 = DriveDistances.FORWARDS_TWO_ONE;
            break;
        case TWO:
            rotate0 = DriveDistances.ROTATE_ZERO_TWO;
            forwards1 = DriveDistances.FORWARDS_ONE_TWO;
            rotate1 = DriveDistances.ROTATE_ONE_TWO;
            forwards2 = DriveDistances.FORWARDS_TWO_TWO;
            break;
        case THREE:
            rotate0 = DriveDistances.ROTATE_ZERO_THREE;
            forwards1 = DriveDistances.FORWARDS_ONE_THREE;
            rotate1 = DriveDistances.ROTATE_ONE_THREE;
            forwards2 = DriveDistances.FORWARDS_TWO_THREE;
            break;
        case FOUR:
            rotate0 = DriveDistances.ROTATE_ZERO_FOUR;
            forwards1 = DriveDistances.FORWARDS_ONE_FOUR;
            rotate1 = DriveDistances.ROTATE_ONE_FOUR;
            forwards2 = DriveDistances.FORWARDS_TWO_FOUR;
            break;
        case FIVE:
            rotate0 = DriveDistances.ROTATE_ZERO_FIVE;
            forwards1 = DriveDistances.FORWARDS_ONE_FIVE;
            rotate1 = DriveDistances.ROTATE_ONE_FIVE;
            forwards2 = DriveDistances.FORWARDS_TWO_FIVE;
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
            mainState = mainStateMachineInit();
            break;
        case DELAY:
            mainState = delay();
            break;
        case FORWARDS_TO_TAPE:
            mainState = forwardsToTape();
            break;
        case ALIGN:
            mainState = align();
            break;
        case MOVE_TO_SHOOTING_POSITION:
            mainState = moveToShootingPosition();
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

private static MainState mainStateMachineInit ()
{
    MainState returnState;

    if (Hardware.autonomousEnabled.isOn() == true)
        {

        returnState = MainState.DELAY;

        }
    else
        {
        returnState = MainState.DONE;
        }
    //testing
    //TODO: remove
    mainState = MainState.MOVE_TO_SHOOTING_POSITION;
    return returnState;
}


/**
 * Waits.
 * Continues to FORWARDS_TO_TAPE when time is up.
 * One of the overarching states.
 */
private static MainState delay ()
{
    MainState returnState = MainState.DELAY;
    if (Hardware.delayTimer.get() > delay)
        {
        returnState = MainState.FORWARDS_TO_TAPE;
        Hardware.delayTimer.stop();
        Hardware.delayTimer.reset();
        }
    return returnState;

}

private static MainState forwardsToTape ()
{

    MainState returnState = MainState.FORWARDS_TO_TAPE;

    Hardware.drive.driveForwardInches(999.9);
    if (Hardware.leftIR.isOn() && Hardware.rightIR.isOn())
        {
        alignmentState = AlignmentState.BOTH_ON_TAPE;
        returnState = MainState.ALIGN;
        }
    else if (Hardware.leftIR.isOn())
        {
        alignmentState = AlignmentState.LEFT_ON_TAPE;
        returnState = MainState.ALIGN;
        }
    else if (Hardware.rightIR.isOn())
        {
        alignmentState = AlignmentState.RIGHT_ON_TAPE;
        returnState = MainState.ALIGN;
        }

    return returnState;

}



/**
 * Aligns robot on gaffers' tape based on IR sensors.
 * One of the overarching states.
 */
private static MainState align ()
{
    MainState returnState = MainState.ALIGN;

    System.out.println("Alignment State: " + alignmentState);
    switch (alignmentState)
        {
        case NEITHER_ON_TAPE:
            alignFind();
            break;
        case LEFT_ON_TAPE:
            alignmentState = alignRightSide();
            break;
        case RIGHT_ON_TAPE:
            alignmentState = alignLeftSide();
            break;
        case BOTH_ON_TAPE:
            returnState = alignFinish();
            break;
        }

    return returnState;
}

/**
 * Moves the robot from the gaffers' tape to a position in front of a goal.
 * Movements are based on the robot's initial position.
 * Guided by the Drive utility class.
 * One of the overarching states.
 */
private static MainState moveToShootingPosition ()
{
    MainState returnState = MainState.MOVE_TO_SHOOTING_POSITION;

    System.out.println(
            "MoveToShoot State: " + moveToShootingPositionStep);
    switch (moveToShootingPositionStep)
        {
        case INIT:
            moveToShootingPositionStep = moveToShootingPositionInit();
            break;
        case ROTATE_ZERO:
            // if not needed, set rotate0 to 0.
            moveToShootingPositionStep = rotateZero();
            break;
        case FORWARDS_ONE:
            moveToShootingPositionStep = forwardsOne();
            break;
        case ROTATE_ONE:
            moveToShootingPositionStep = rotateOne();
            break;
        case FORWARDS_TWO:
            // if not needed, set forwards2 to 0.
            moveToShootingPositionStep = forwardsTwo();
            break;
        case DONE:
            returnState = moveToShootingPositionDone();
            break;
        default:
            //this should not happen.
            break;
        }

    return returnState;
}

private static MainState shoot ()
{
    // TODO: write method to shoot cannonball.

    return MainState.DONE;
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

/**
 * Called at the beginning of MOVE_TO_SHOOTING_POSITION.
 * Begins movement sequence.
 */
private static MoveToShootingPositionStep moveToShootingPositionInit ()
{
    return MoveToShootingPositionStep.ROTATE_ZERO;
}

/**
 * The first rotation along the Alignment tape.
 * From StartingPositions 1, 2, and 5, rotate0 will be set to 0, and this will
 * do nothing.
 */
private static MoveToShootingPositionStep rotateZero ()
{
    MoveToShootingPositionStep returnState =
            MoveToShootingPositionStep.ROTATE_ZERO;
    if (Hardware.drive.turnLeftDegrees(rotate0))
        {
        returnState =
                MoveToShootingPositionStep.FORWARDS_ONE;
        }
    return returnState;
}

/**
 * First movement after first turn on Alignment tape.
 */
private static MoveToShootingPositionStep forwardsOne ()
{
    MoveToShootingPositionStep returnState =
            MoveToShootingPositionStep.FORWARDS_ONE;

    if (Hardware.drive.driveForwardInches(forwards1))
        {
        returnState =
                MoveToShootingPositionStep.ROTATE_ONE;
        }
    return returnState;
}

/**
 * Second rotation; turns to face goal.
 */
private static MoveToShootingPositionStep rotateOne ()
{
    MoveToShootingPositionStep returnState =
            MoveToShootingPositionStep.ROTATE_ONE;

    if (Hardware.drive.turnLeftDegrees(rotate1))
        ;
        {
        returnState =
                MoveToShootingPositionStep.FORWARDS_TWO;
        }
    return returnState;
}

/**
 * Final movement forwards to goal, used for positions 1, 2, and 5.
 */
private static MoveToShootingPositionStep forwardsTwo ()
{
    MoveToShootingPositionStep returnState =
            MoveToShootingPositionStep.FORWARDS_TWO;
    if (Hardware.drive.driveForwardInches(forwards2))
        {
        returnState = MoveToShootingPositionStep.DONE;
        }
    return returnState;
}

/**
 * Sets main state to SHOOT upon completion.
 */
private static MainState moveToShootingPositionDone ()
{
    return MainState.SHOOT;
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
 * 
 * @return
 */
private static AlignmentState alignRightSide ()
{

    AlignmentState returnState = AlignmentState.LEFT_ON_TAPE;

    double leftAlignmentSpeed = 0.0;
    double rightAlignmentSpeed = 0.0;

    rightAlignmentSpeed = ALIGNMENT_SPEED;

    if (Hardware.rightIR.isOn() && Hardware.leftIR.isOn())
        {
        rightAlignmentSpeed = 0.0;
        returnState = AlignmentState.BOTH_ON_TAPE;
        }
    else if (Hardware.leftIR.isOn() == false)
        {
        leftAlignmentSpeed = -ALIGNMENT_SPEED;
        }

    Hardware.transmission.controls(rightAlignmentSpeed,
            leftAlignmentSpeed);
    return returnState;
}

/**
 * Moves left side to tape when right side is on.
 * Moves right side back if right side turns off.
 * 
 * @return
 */
private static AlignmentState alignLeftSide ()
{

    AlignmentState returnState = AlignmentState.RIGHT_ON_TAPE;

    double leftAlignmentSpeed = 0.0;
    double rightAlignmentSpeed = 0.0;

    leftAlignmentSpeed = ALIGNMENT_SPEED;

    if (Hardware.rightIR.isOn() && Hardware.leftIR.isOn())
        {
        leftAlignmentSpeed = 0.0;
        returnState = AlignmentState.BOTH_ON_TAPE;
        }
    else if (Hardware.rightIR.isOn() == false)
        {
        rightAlignmentSpeed = -ALIGNMENT_SPEED;
        }

    Hardware.transmission.controls(rightAlignmentSpeed,
            leftAlignmentSpeed);

    return returnState;
}


private static MainState alignFinish ()
{
    return MainState.MOVE_TO_SHOOTING_POSITION;
}
/*
 * ==============================================
 * END ALIGN SUB-STATE METHODS
 * ==============================================
 */


// .....................__ _ __
// .................../ .  . .  \
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

private static final double MAXIMUM_DELAY = 4.0;
private static final int ONE_THOUSAND = 1000;

private static final double ALIGNMENT_SPEED = 0.1;

} // end class
