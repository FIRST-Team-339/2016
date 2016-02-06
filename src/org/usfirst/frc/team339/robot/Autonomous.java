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
    LOWER_ARM_AND_MOVE, DELAY, // waits, depending on settings.
    FORWARDS_TO_TAPE, // drives forwards until detection of the gaffers' tape.
    ALIGN, // aligns its self on the gaffers' tape based of IR sensors.
    MOVE_TO_SHOOTING_POSITION,  // moves towards a good shooting angle based on
                              // settings.
    SHOOT, // ajusts its self (?) and fires the cannonball.
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
                {//From Starting Position 1
                        new DriveInstruction(74.7, 1.0, 0.0, 0.0), // drive out
                        new DriveInstruction(0.0, 0.0, -60.0, 1.0), // turn perpendicular to goal
                        new DriveInstruction(62.7, 1.0, 0.0, 0.0), // drive up to goal
                        new DriveInstruction(true) //continue to shoot
                },
                {//From Starting Position 2
                        new DriveInstruction(82.0, 1.0, 0.0, 0.0), // drive out
                        new DriveInstruction(0.0, 0.0, -60.0, 1.0), // turn perpendicular to goal
                        new DriveInstruction(52.92, 1.0, 0.0, 0.0), // drive up to goal
                        new DriveInstruction(true)//continue to shoot
                },
                {//From Starting Position 3
                        new DriveInstruction(0.0, 0.0, -20.0, 1.0), //turn towards end position
                        new DriveInstruction(64.0, 1.0, 0.0, 0.0), // drive to end position
                        new DriveInstruction(0.0, 0.0, 60.0, 1.0), // turn towards goal
                        new DriveInstruction(true) //continue to shoot
                },
                {//From Starting Position 4
                        new DriveInstruction(0.0, 0.0, -24.8, 1.0), //turn towards end position
                        new DriveInstruction(64.0, 1.0, 0.0, 0.0), // drive to end position
                        new DriveInstruction(0.0, 0.0, 64.8, 1.0), // turn towards goal
                        new DriveInstruction(true) //continue to shoot
                },
                {//From Starting Position 5
                        new DriveInstruction(86.5, 1.0, 0.0, 0.0),// drive out
                        new DriveInstruction(0.0, 0.0, 60.0, 0.0),// turn perpendicular to goal
                        new DriveInstruction(12.0, 1.0, 0.0, 0.0),// drive up to goal
                        new DriveInstruction(true) //continue to shoot
                }
        };


// ==========================================
// AUTO STATES
// ==========================================
private static MainState mainState = MainState.INIT;
private static StartingPosition startingPosition = StartingPosition.ONE;
private static AlignmentState alignmentState =
        AlignmentState.NEITHER_ON_TAPE;

// ==================================
// VARIABLES
// ==================================
private static double delay; // time to delay before begining.

private static int lane;

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

    // set the delay time based on potentiometer.
    initDelayTime();



    // set the drive values for MOVE_TO_SHOOTING_POSITION

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

    Hardware.rightRearEncoder.reset();
    Hardware.rightRearEncoder.setDistancePerPulse(0.019706);

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
private static int initDelayTime ()
{
    return (int) MAXIMUM_DELAY * Hardware.delayPot.get()
            / ONE_THOUSAND;
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
            mainState = mainInit();
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

private static MainState mainInit ()
{
    MainState returnState;

    delay = initDelayTime();
    lane = getLane();

    if (Hardware.autonomousEnabled.isOn() == true)
        {

        returnState = MainState.DELAY;

        }
    else
        {
        returnState = MainState.DONE;
        }

    return returnState;
}


private static MainState lowerArmAndMove ()
{
    MainState returnState = MainState.LOWER_ARM_AND_MOVE;

    //    Hardware.drive.

    //    if(Hardware.armMotorEncoder.getDistance < ARM_DOWN_DISTANCE)
    //        {
    //        Hardware.armMotor.set(1.0);
    //        }
    //        else
    //  {
    //        returnState = MainState.DELAY;
    //  }

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

    //The required distance to drive is taken from the pathToGoalInformation 2d Array.
    DriveInstruction currentInstruction =
            driveToGoalInstructions[Hardware.startingPositionDial
                    .getPosition()][driveToShootingPositionStep];

    if (Hardware.drive.driveForwardInches(
            currentInstruction.getForwardDistance()) // Drive, and if we have driven the distance required
            || Hardware.drive.driveForwardInches(
                    currentInstruction.getRotationalDistance())) // Or the rotation...
        {

        driveToShootingPositionStep++; //go to next step.

        if (currentInstruction.isTerminator())//If at end of path, go to next state.
            {
            returnState = MainState.SHOOT;//The next state should be to shoot, or possibly to align with vision processing.
            }
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

private static int getLane ()
{
    return Hardware.startingPositionDial.getPosition();
}


private static final class StateInformation
{

//Each index refers to a higher starting speed.
static final double[] START_SPEEDS =
        {.20, .40, .70};
static final double[] START_TIMES =
        {.5, .5, .5};



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
