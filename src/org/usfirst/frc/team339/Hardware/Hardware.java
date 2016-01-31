// ====================================================================
// FILE NAME: Hardware.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 2, 2011
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This file contains all of the global definitions for the
// hardware objects in the system
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.Hardware;

import org.usfirst.frc.team339.HardwareInterfaces.IRSensor;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.HardwareInterfaces.RobotPotentiometer;
import org.usfirst.frc.team339.HardwareInterfaces.SingleThrowSwitch;
import org.usfirst.frc.team339.HardwareInterfaces.SixPositionSwitch;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.TransmissionFourWheel;
import org.usfirst.frc.team339.Utils.Drive;
import org.usfirst.frc.team339.Utils.ErrorMessage;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.vision.USBCamera;

// -------------------------------------------------------
/**
 * puts all of the hardware declarations into one place. In addition, it makes
 * them available to both autonomous and teleop.
 *
 * @class HardwareDeclarations
 * @author Bob Brown
 * @written Jan 2, 2011
 *          -------------------------------------------------------
 */
public class Hardware
{
// ------------------------------------
// Public Constants
// ------------------------------------

// -------------------------------------
// Private Constants
// -------------------------------------
private static final int rightRearMotorCANID = 15;
private static final int leftRearMotorCANID = 11;
private static final int rightFrontMotorCANID = 17;
private static final int leftFrontMotorCANID = 12;

// ---------------------------------------
// Hardware Tunables
// ---------------------------------------

// **********************************************************
// DIGITAL I/O CLASSES
// **********************************************************
// ====================================
// PWM classes
// ====================================

// ------------------------------------
// Jaguar classes
// ------------------------------------

// ------------------------------------
// Talon classes
// ------------------------------------
public static CANTalon rightRearMotor =
        new CANTalon(rightRearMotorCANID);
public static CANTalon leftRearMotor =
        new CANTalon(leftRearMotorCANID);
public static CANTalon rightFrontMotor =
        new CANTalon(rightFrontMotorCANID);
public static CANTalon leftFrontMotor =
        new CANTalon(leftFrontMotorCANID);

// ------------------------------------
// Victor classes
// ------------------------------------

// ------------------------------------
// CAN classes
// ------------------------------------

// ====================================
// Relay classes
// ====================================

public static Relay ringLightRelay = new Relay(0);

// ------------------------------------
// Compressor class - runs the compressor
// with a single relay
// ------------------------------------

// ====================================
// Digital Inputs
// ====================================
// ------------------------------------
// Single and double throw switches
// ------------------------------------

//Turns autonomous on or off.
/**
 * A physical switch that decides whether or not to run autonomous.
 */
public static SingleThrowSwitch autonomousEnabled =
        new SingleThrowSwitch(19);

/**
 * Displays the starting position.
 * Position 0 on the switch corresponds to position 1, 1 to 2, etc.
 */
public static SixPositionSwitch startingPositionDial =
        new SixPositionSwitch(14, 15, 16, 17, 18, 21);

// ------------------------------------
// Gear Tooth Sensors
// ------------------------------------

// ------------------------------------
// Encoders
// ------------------------------------
public static Encoder leftFrontEncoder = new Encoder(10, 11);
public static Encoder rightFrontEncoder = new Encoder(12, 13);
public static Encoder leftRearEncoder = new Encoder(0, 1);
public static Encoder rightRearEncoder = new Encoder(2, 3);
// -----------------------
// Wiring diagram
// -----------------------
// Orange - Red PWM 1
// Yellow - White PWM 1 Signal
// Brown - Black PWM 1 (or PWM 2)
// Blue - White PWM 2 Signal
// For the AMT103 Encoders UNVERIFIED
// B - White PWM 2
// 5V - Red PWM 1 or 2
// A - White PWM 1
// X - index channel, unused
// G - Black PWM 1 or 2
// see http://www.cui.com/product/resource/amt10-v.pdf page 4 for Resolution
// (DIP Switch) Settings (currently all are off)

// -------------------------------------
// Red Light/IR Sensor class
// -------------------------------------

public static IRSensor rightIR = new IRSensor(6);
public static IRSensor leftIR = new IRSensor(22);

// ====================================
// I2C Classes
// ====================================

// **********************************************************
// SOLENOID I/O CLASSES
// **********************************************************
// ====================================
// Solenoids
// ====================================
// ------------------------------------
// Double Solenoids
// ------------------------------------

// ------------------------------------
// Single Solenoids
// ------------------------------------

// **********************************************************
// ANALOG I/O CLASSES
// **********************************************************
// ====================================
// Analog classes
// ====================================
// ------------------------------------
// Gyro class
// ------------------------------------

// -------------------------------------
// Potentiometers
// -------------------------------------
// -------------------------------------
public static RobotPotentiometer delayPot =
        new RobotPotentiometer(3, 270);

// -------------------------------------
// Sonar/Ultrasonic
// -------------------------------------

// **********************************************************
// roboRIO CONNECTIONS CLASSES
// **********************************************************
// -------------------------------------
// Axis/USB Camera class
// -------------------------------------
// -------------------------------------
// declare the USB camera server and the
// USB camera it serves
// -------------------------------------
public static CameraServer cameraServer = CameraServer.getInstance();
public static USBCamera cam0 = new USBCamera("cam0");

// Declares the Axis camera
public static KilroyCamera axisCamera = new KilroyCamera(true);

// **********************************************************
// DRIVER STATION CLASSES
// **********************************************************
// ------------------------------------
// DriverStations class
// ------------------------------------
public static final DriverStation driverStation =
        DriverStation.getInstance();

// ------------------------------------
// Joystick classes
// ------------------------------------
public static Joystick leftDriver = new Joystick(0);
public static Joystick rightDriver = new Joystick(1);
public static Joystick leftOperator = new Joystick(2);
public static Joystick rightOperator = new Joystick(3);

// ------------------------------------
// Drive system
// ------------------------------------
//

// **********************************************************
// Kilroy's Ancillary classes
// **********************************************************

// -------------------------------------
// PID tuneables
// -------------------------------------

// -------------------------------------
// PID classes
// -------------------------------------

// ------------------------------------
// Transmission class
// ------------------------------------

public static TransmissionFourWheel transmissionFourWheel =
        new TransmissionFourWheel(rightFrontMotor, leftFrontMotor,
                rightRearMotor, leftRearMotor);

public static Drive drive =
        new Drive(transmissionFourWheel, rightRearEncoder,
                rightFrontEncoder, leftRearEncoder, leftFrontEncoder);

// -------------------
// Assembly classes (e.g. forklift)
// -------------------

// ------------------------------------
// Utility classes
// ------------------------------------
public static final Timer kilroyTimer = new Timer();
public static final Timer autoTimer = new Timer();
public static final Timer delayTimer = new Timer();
public static final ErrorMessage errorMessage = new ErrorMessage(
        true /* append timelog */);

public static final MotorSafetyHelper leftRearMotorSafety =
        new MotorSafetyHelper(leftRearMotor);
public static final MotorSafetyHelper rightRearMotorSafety =
        new MotorSafetyHelper(rightRearMotor);
public static final MotorSafetyHelper leftFrontMotorSafety =
        new MotorSafetyHelper(leftFrontMotor);
public static final MotorSafetyHelper rightFrontMotorSafety =
        new MotorSafetyHelper(rightFrontMotor);



} // end class
