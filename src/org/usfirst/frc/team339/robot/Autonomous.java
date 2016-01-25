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
public class Autonomous
{

// states we'll be going through in autonomous periodic
private static enum AutoState
    {
    INITIAL, DONE
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
private static AutoState autoState = AutoState.INITIAL;

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
} // end class
