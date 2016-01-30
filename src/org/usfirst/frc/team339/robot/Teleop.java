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
import org.usfirst.frc.team339.HardwareInterfaces.transmission.TransmissionFourWheel;
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
    TransmissionFourWheel testTrans = Hardware.transmissionFourWheel;

    Hardware.transmissionFourWheel.drive(Hardware.rightDriver.getY(),
            Hardware.leftDriver.getY());
    printStatements();

} // end Periodic

/**
 * stores print statements for future use, statements are commented out when
 * not in use
 * 
 * @author Ashley Espeland
 * @written 1/28/16
 * 
 *          1/30/16: added 6 position switch -McGee
 * 
 */
public static void printStatements ()
{
    // IR sensors
    // System.out.println("left IR = " + Hardware.leftIR.isOn());
    //    System.out.println("right IR = " + Hardware.rightIR.isOn());//Not Working :(
    // pots
    //    System.out.println("delay pot = " + (int) Hardware.delayPot.get());
    //Motor controllers
    System.out.println("RR Motor V = " + Hardware.rightRearMotor.get());
    System.out.println("LR Motor V = " + Hardware.leftRearMotor.get());
    System.out
            .println("RF Motor V = " + Hardware.rightFrontMotor.get());
    System.out.println("LF Motor V = " + Hardware.leftFrontMotor.get());
    //    System.out.println(
    //            "RR distance = " + Hardware.rightRearEncoder.getDistance());
    //    System.out.println(
    //            "LR distance = " + Hardware.leftFrontEncoder.getDistance());
    //    System.out.println("RF distance = "
    //            + Hardware.rightFrontEncoder.getDistance());
    //    System.out.println(
    //            "LF distance = " + Hardware.leftFrontEncoder.getDistance());

    //print the position the 6 position switch
    //    System.out.println("Position: " + Hardware.startingPositionDial.getPosition());

} // end printStatements 
} // end class
