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
// Init() - Initialization code for teleop mode
// should go here. Will be called each time the robot enters
// teleop mode.
// -----------------------------------------------------
// Periodic() - Periodic code for teleop mode should
// go here. Will be called periodically at a regular rate while
// the robot is in teleop mode.
// -----------------------------------------------------
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package org.usfirst.frc.team339.robot;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.Vision.ImageProcessor;
import org.usfirst.frc.team339.Utils.Guidance.Direction;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 * This class contains all of the user code for the Autonomous
 * part of the match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop
{
/**
 * User Initialization code for teleop mode should go here. Will be
 * called once when the robot enters teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void init ()
{
    CameraServer.getInstance().setSize(1);
    Hardware.axisCamera
            .writeBrightness(Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
    // set max speed. change by gear?
    Hardware.drive.setMaxSpeed(MAXIMUM_TELEOP_SPEED);
    Hardware.transmission.setFirstGearPercentage(FIRST_GEAR_PERCENTAGE);
    Hardware.transmission
            .setSecondGearPercentage(SECOND_GEAR_PERCENTAGE);
    Hardware.transmission.setGear(1);

    Hardware.delayTimer.reset();
    Hardware.rightRearEncoder.reset();
    Hardware.leftRearEncoder.reset();
    Hardware.leftFrontMotor.set(0.0);
    Hardware.leftRearMotor.set(0.0);
    Hardware.rightFrontMotor.set(0.0);
    Hardware.rightRearMotor.set(0.0);
    Hardware.armMotor.set(0.0);
    Hardware.portArmIntakeMotor.set(0.0);
    Hardware.starboardArmIntakeMotor.set(0.0);
} // end Init


private char[] reports;

/**
 * User Periodic code for teleop mode should go here. Will be called
 * periodically at a regular rate while the robot is in teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void periodic ()
// we changed this from a static for testing purposes-Heather :)
{
    //Print statements to test Hardware on the Robot
    printStatements();

    // Smartdashboard arrow test
    if (Hardware.rightOperator.getRawButton(8))
    {
        Hardware.arrowDashboard.setDirection(Direction.left);
    }
    else if (Hardware.rightOperator.getRawButton(9))
    {
        Hardware.arrowDashboard.setDirection(Direction.right);
    }
    else
    {
        Hardware.arrowDashboard.setDirection(Direction.neutral);
    }


    // If we click buttons 6+7 on the left operator joystick, we dim the
    // brightness a lot, turn the ringlight on, and then if we haven't
    // already taken an image then we do and set the boolean to true to
    // prevent us taking more images. Otherwise we don't turn on the
    // ringlight and we don't take a picture. We added a timer to delay
    // taking the picture for the brightness to dim and for the ring
    // light to turn on.
    if (Hardware.leftOperator.getRawButton(6) == true &&
            Hardware.leftOperator.getRawButton(7) == true)
        {
        if (prepPic == false)
            {
            Hardware.axisCamera.writeBrightness(
                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
            Hardware.ringLightRelay.set(Value.kOn);
            Hardware.delayTimer.start();
            prepPic = true;
            takingLitImage = true;
            }
        }

    // Once the brightness is down and the ring light is on then the
    // picture is taken, the brightness returns to normal, the ringlight
    // is turned off, and the timer is stopped and reset.
    // @TODO Change .25 to a constant
    if (Hardware.delayTimer.get() >= .25 && prepPic == true
            && takingLitImage == true)
        {
        Hardware.axisCamera.saveImagesSafely();
        prepPic = false;
        takingLitImage = false;
        }

    if (takingLitImage == false && Hardware.delayTimer.get() >= 1)
        {
        Hardware.axisCamera
                .writeBrightness(
                        Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
        Hardware.ringLightRelay.set(Value.kOff);
        Hardware.delayTimer.stop();
        Hardware.delayTimer.reset();
        }

    // If we click buttons 10+11, we take a picture without the
    // ringlight and set the boolean to true so we don't take a bunch of
    // other pictures.
    if (Hardware.leftOperator.getRawButton(10) == true &&
            Hardware.leftOperator.getRawButton(11) == true)
        {
        if (takingUnlitImage == false)
            {
            takingUnlitImage = true;
            Hardware.axisCamera.saveImagesSafely();
            }
        }
    else
        takingUnlitImage = false;

    // if the left operator trigger is pressed, then we check to see if
    // we're taking a processed picture through the boolean. If we are
    // not currently taking a processed picture, then it lets us take a
    // picture and sets the boolean to true so we don't take multiple
    // pictures. If it is true, then it does nothing. If we don't click
    // the trigger, then the boolean resets itself to false to take
    // pictures again.
    if (Hardware.leftOperator.getTrigger() == true)
        {
        if (processingImage == false)
            {
            processImage();
            }
        }
    else
        {
        processingImage = false;
        }

    //Driving the Robot
    // Hand the transmission class the joystick values and motor controllers for
    // four wheel drive.
    Hardware.transmission.controls(Hardware.rightDriver.getY(),
            Hardware.leftDriver.getY());
    // If we're pressing the upshift button, shift up.
    if (Hardware.rightDriver.getRawButton(
            GEAR_UPSHIFT_JOYSTICK_BUTTON) == true)
        Hardware.transmission.upshift(1);
    // If we press the downshift button, shift down.
    if (Hardware.rightDriver.getRawButton(
            GEAR_DOWNSHIFT_JOYSTICK_BUTTON) == true)
        Hardware.transmission.downshift(1);
} // end Periodic

static boolean hasBegunTurning = true;

/**
 * 
 * Processes images with the Axis Camera for use in autonomous when
 * trying to score. Will eventually be moved to a Shoot class when
 * one is made.
 * 
 * @author Marlene McGraw
 * @written 2/6/16
 * 
 */
public static void processImage ()
{

    // If we took a picture, we set the boolean to true to prevent
    // taking more pictures and create an image processor to process
    // images.
    processingImage = true;
    ImageProcessor imageProcessor = new ImageProcessor(
            Hardware.axisCamera);

        }
// End processImage


/**
 * stores print statements for future use in the print "bank", statements are
 * commented out when
 * not in use, when you write a new print statement, "deposit" the statement in
 * the "bank"
 * do not "withdraw" statements, unless directed to
 * 
 * @author Ashley Espeland
 * @written 1/28/16
 * 
 *          Edited by Ryan McGee
 * 
 */
public static void printStatements ()
{
    // Joysticks------------
    // System.out.println("Left Joystick: " + Hardware.leftDriver.getY());
    // System.out
    // .println("Right Joystick: " + Hardware.rightDriver.getY());
    // System.out.println("Left Operator: " + Hardware.leftOperator.getY());
    // System.out.println("Right Operator: " + Hardware.rightOperator.getY());

    // IR sensors-----------
    //   System.out.println("left IR = " + Hardware.leftIR.isOn());
    //   System.out.println("right IR = " + Hardware.rightIR.isOn());

    // pots-----------------
    //    System.out.println("delay pot = " + (int) Hardware.delayPot.get());
    //prints the value of the transducer- (range in code is 50)
    //hits psi of 100 accurately
    //System.out.println("transducer = " + Hardware.transducer.get());

    //Motor controllers-----
    //prints value of the motors
    //	    System.out.println("RR Motor T = " + Hardware.rightRearMotor.get());
    //	    System.out.println("LR Motor T = " + Hardware.leftRearMotor.get());
    //	    System.out.println("RF Motor T = " + Hardware.rightFrontMotor.get());
    //	    System.out.println("LF Motor T = " + Hardware.leftFrontMotor.get());
    //          System.out.println("Arm Motor V = " + Hardware.armMotor.get());
    //    	    System.out.println("Starboard Intake Motor V = " + Hardware.starboardArmIntakeMotor.get());
    //    	    System.out.println("Port Intake Motor V = " + Hardware.portArmIntakeMotor.get());

    //Solenoids-------------
    //prints the state of the solenoids 
    //    System.out.println("cameraSolenoid = " + Hardware.cameraSolenoid.get());
    // System.out.println("catapultSolenoid0 = " +
    // Hardware.catapultSolenoid0.get());
    // System.out.println("catapultSolenoid1 = " +
    // Hardware.catapultSolenoid1.get());
    // System.out.println("catapultSolenoid2 = " +
    // Hardware.catapultSolenoid2.get());

    // Encoders-------------
    //System.out.println(
    //        "RR distance = " + Hardware.rightRearEncoder.getDistance());
    //System.out.println(
    //        "LR distance = " + Hardware.leftRearEncoder.getDistance());
    //    System.out.println("Arm Motor = " + Hardware.armMotor.getDistance());

    //Switches--------------
    //prints state of switches
    //    System.out.println("Autonomous Enabled Switch: " + Hardware.autonomousEnabled.isOn());
    //    System.out.println("Shoot High Switch: " + Hardware.shootHigh.isOn());
    //    System.out.println("Shoot Low Switch: " + Hardware.shootLow.isOn());

    //print the position of the 6 position switch------------
    //    System.out.println("Position: " + Hardware.startingPositionDial.getPosition());

    //Relay-----------------
    //    System.out.println(Hardware.ringLightRelay.get());
} // end printStatements 


/*
 * ===============================================
 * Constants
 * ===============================================
 */

private static final double MAXIMUM_TELEOP_SPEED = 1.0;

private static final double FIRST_GEAR_PERCENTAGE = 0.5;

private static final double SECOND_GEAR_PERCENTAGE =
        MAXIMUM_TELEOP_SPEED;

//TODO change based on driver request
private static final int GEAR_UPSHIFT_JOYSTICK_BUTTON = 3;

private static final int GEAR_DOWNSHIFT_JOYSTICK_BUTTON = 2;

//==========================================
//TUNEABLES
//==========================================

private static boolean processingImage = false;

//Boolean to check if we're taking a lit picture
private static boolean takingLitImage = false;

//Boolean to check if we're taking an unlit picture
private static boolean takingUnlitImage = false;

//this is for preparing to take a picture with the timer; changes
//brightness, turns on ringlight, starts timer
private static boolean prepPic = false;

} // end class
