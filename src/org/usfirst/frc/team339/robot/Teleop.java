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
import edu.wpi.first.wpilibj.CameraServer;

/**
 * This class contains all of the user code for the Autonomous
 * part of the match, namely, the Init and Periodic code
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public class Teleop
{

// ==========================================
// TUNEABLES
// ==========================================



/**
 * User Initialization code for teleop mode should go here. Will be
 * called once when the robot enters teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void init ()
{

    // <<<<<<< HEAD
    // =======
    // set max speed. change by gear?
    Hardware.drive.setMaxSpeed(MAXIMUM_TELEOP_SPEED);

    //>>>>>>> branch 'master' of https://github.com/FIRST-Team-339/2016

    // -----------------------------------
    // stop cam0 in case we have declared them
    // in Autonomous. Then declare a new cam0
    // and start it going automatically with the
    // camera server
    // -----------------------------------
    CameraServer.getInstance().setSize(1);
} // end Init

/**
 * User Periodic code for teleop mode should go here. Will be called
 * periodically at a regular rate while the robot is in teleop mode.
 *
 * @author Nathanial Lydick
 * @written Jan 13, 2015
 */
public static void periodic ()
{
    //Print statements to test Hardware on the Robot
    printStatements();

    if (Hardware.leftOperator.getTrigger() == true)
        {
        Hardware.catapultSolenoid0.set(true);
        Hardware.catapultSolenoid1.set(true);
        Hardware.catapultSolenoid2.set(true);

        }
    else
        {
        Hardware.catapultSolenoid0.set(false);
        Hardware.catapultSolenoid1.set(false);
        Hardware.catapultSolenoid2.set(false);
        }

    //Driving the Robot
    Hardware.transmissionFourWheel.drive(Hardware.rightDriver.getY(),
            Hardware.leftDriver.getY());


} // end Periodic

/**
 * stores print statements for future use, statements are commented out when
 * not in use
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
    //    System.out.println("Left Joystick: " + Hardware.leftDriver.getY());
    //    System.out
    //            .println("Right Joystick: " + Hardware.rightDriver.getY());
    //      System.out.println("Left Operator: " + Hardware.leftOperator.getY());
    //      System.out.println("Right Operator: " + Hardware.rightOperator.getY());

    // IR sensors-----------
    // System.out.println("left IR = " + Hardware.leftIR.isOn());
    // System.out.println("right IR = " + Hardware.rightIR.isOn());

    // pots-----------------
    //    System.out.println("delay pot = " + (int) Hardware.delayPot.get());
    //prints the value of the transducer- (range 130)
    // NOT TESTED!!!System.out.println("transducer = " + Hardware.transducer.get());

    //Motor controllers-----
    //    System.out.println("RR Motor V = " + Hardware.rightRearMotor.get());
    //    System.out.println("LR Motor V = " + Hardware.leftRearMotor.get());
    //    System.out
    //            .println("RF Motor V = " + Hardware.rightFrontMotor.get());
    //    System.out.println("LF Motor V = " + Hardware.leftFrontMotor.get());

    //Solenoids-------------
    //prints the state of the solenoids 
    //    System.out.println("cameraSolenoid = " + Hardware.cameraSolenoid.get());
    //    System.out.println("catapultSolenoid0 = " + Hardware.catapultSolenoid0.get());
    //    System.out.println("catapultSolenoid1 = " + Hardware.catapultSolenoid1.get());
    //    System.out.println("catapultSolenoid2 = " + Hardware.catapultSolenoid2.get());

    // Encoders-------------
    //    System.out.println(
    //            "RR distance = " + Hardware.rightRearEncoder.getDistance());
    //    System.out.println(
    //            "LR distance = " + Hardware.leftRearEncoder.getDistance());
    //    System.out.println("RF distance = "
    //            + Hardware.rightFrontEncoder.getDistance());
    //    System.out.println(
    //            "LF distance = " + Hardware.leftFrontEncoder.getDistance());

    //Switches--------------
    //    System.out.println("Autonomous Enabled Switch: " + Hardware.autonomousEnabled.isOn());

    //print the position the 6 position switch------------
    //    System.out.println("Position: " + Hardware.startingPositionDial.getPosition());

    //Relay-----------------
    //    System.out.println(Hardware.ringLightRelay.get());

    //<<<<<<< HEAD
} // end printStatements 
 //=======
 // end printStatements


/*
 * ===============================================
 * Constants
 * ===============================================
 */

private static final double MAXIMUM_TELEOP_SPEED = 1.0;

//>>>>>>> branch 'master' of https://github.com/FIRST-Team-339/2016
} // end class
