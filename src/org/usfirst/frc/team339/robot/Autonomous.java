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

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;
import org.usfirst.frc.team339.Hardware.Hardware;
import edu.wpi.first.wpilibj.CameraServer;

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
    INIT, DELAY, // waits, depending on settings.
    FORWARDS_TO_TAPE, // drives forwards until detection of the gaffers' tape.
    ALIGN, // aligns its self on the gaffers' tape based of IR sensors.
    MOVE_TO_SHOOTING_POSITION,  // moves towards a good shooting angle based on
                              // settings.
    SHOOT, // ajusts its self (?) and fires the cannonball.
    DONE
    }

private static enum MoveToShootingPositionStep
    {
    INIT, ROTATE_ZERO, // rotates given number before moving forwards.
    FORWARDS_ONE, // moves forwards.
    ROTATE_ONE, // pauses to rotate.
    FORWARDS_TWO, // continues to move forwards.
    DONE
    }

private static enum StartingPosition
    {
    ONE, TWO, THREE, FOUR, FIVE

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
private static StartingPosition startingPosition;

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

    // set the drive values for MOVE_TO_SHOOTING_POSITION
    initGoalPath();

    // -------------------------------------
    // close both of the cameras in case they
    // were previously started in a previous
    // run. Then, change the camera to one that
    // will eventually process images.
    // ------------------------------------
    Hardware.cam0.stopCapture();
    Hardware.cam0.closeCamera();
    Hardware.cam1.stopCapture();
    Hardware.cam1.closeCamera();
    capturedFrame = NIVision
            .imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

    sessionNumber = NIVision.IMAQdxOpenCamera("cam1",
            NIVision.IMAQdxCameraControlMode.CameraControlModeController);
    NIVision.IMAQdxConfigureGrab(sessionNumber);
    frameRect = new NIVision.Rect(10, 10, 100, 100);

    // ---------------------------------------
    // turn the timer off and reset the counter
    // so that we can use it in autonomous
    // ---------------------------------------
    Hardware.kilroyTimer.stop();
    Hardware.kilroyTimer.reset();
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
    // ------------------------------
    // process images from the USB camera
    // FOr one time only, process an image.
    // NOTE: processing an image takes
    // considerable time, so ONLY do this
    // when you really need to. At this point
    // you would normally do normal processing
    // of the image (filtering, etc.)
    // ------------------------------
    if (processImage == true)
        {
        processImage = false;

        NIVision.IMAQdxGrab(sessionNumber, capturedFrame, 1);
        NIVision.imaqDrawShapeOnImage(capturedFrame,
                capturedFrame, frameRect, DrawMode.DRAW_VALUE,
                ShapeMode.SHAPE_OVAL, 0.0f);
        CameraServer.getInstance().setImage(capturedFrame);
        NIVision.IMAQdxStopAcquisition(sessionNumber);
        }
} // end Periodic

// ---------------------------------
// Image processing data items
// ---------------------------------
static boolean processImage = true;
static Image capturedFrame;
static int sessionNumber = 0;
static NIVision.Rect frameRect;


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
    switch (mainState)
        {
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
            // shoot();
            break;
        }
}


/*
 * ======================================
 * MAIN AUTONOMOUS STATE METHODS
 * ======================================
 */

/**
 * Waits.
 * Continues to FORWARDS_TO_TAPE when time is up.
 * One of the overarching states.
 */
private static void delay ()
{
    // TODO: write code for delay
    // if(Timer.get() > delay)
        {
        // State = FORWARDS_TO_TAPE;
        }
}

private static void forwardsToTape ()
{

    // TODO: drive until tape is detected

}



/**
 * Aligns robot on gaffers' tape based on IR sensors.
 * One of the overarching states.
 */
private static void align ()
{
    // TODO: create new sub-state machine based around each sensor's status of
    // being on the tape.
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
    switch (moveToShootingPositionStep)
        {
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

private static void rotateZero ()
{
    // TODO: write turn
}

private static void forwardsOne ()
{
    // TODO: write move
}

private static void rotateOne ()
{
    // TODO: write turn
}

private static void forwardsTwo ()
{
    // TODO: write move
}



/*
 * ==============================================
 * END OF MOVE_TO_SHOOTING_POSITION SUB-STATE METHODS
 * ==============================================
 */

//                      __ _ __
// .................../         \
// ................./ --0 --- 0-- \
// .........++++.. |- - - | | - - -| ..++++
/*---------//||\\--|     /   \     |--//||\\-------
//Constants\\||//       |     |       \\||//
//------ ---------------|     |--------------------*/
// ----------------------\___/
// ...............................|<!(r% ~@$ #3r3
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
