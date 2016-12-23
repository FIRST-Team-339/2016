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

import org.usfirst.frc.team339.HardwareInterfaces.DoubleThrowSwitch;
import org.usfirst.frc.team339.HardwareInterfaces.IRSensor;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.HardwareInterfaces.MomentarySwitch;
import org.usfirst.frc.team339.HardwareInterfaces.RobotPotentiometer;
import org.usfirst.frc.team339.HardwareInterfaces.SingleThrowSwitch;
import org.usfirst.frc.team339.HardwareInterfaces.SixPositionSwitch;
import org.usfirst.frc.team339.HardwareInterfaces.UltraSonic;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;
import org.usfirst.frc.team339.Utils.Drive;
import org.usfirst.frc.team339.Utils.ErrorMessage;
import org.usfirst.frc.team339.Utils.Guidance;
import org.usfirst.frc.team339.Utils.ManipulatorArm;
import org.usfirst.frc.team339.Vision.ImageProcessor;
import org.usfirst.frc.team339.Vision.VisionScript;
import org.usfirst.frc.team339.Vision.operators.ConvexHullOperator;
import org.usfirst.frc.team339.Vision.operators.HSLColorThresholdOperator;
import org.usfirst.frc.team339.Vision.operators.RemoveSmallObjectsOperator;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;
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
public static final int DELAY_POT_MIN_DEGREES = 0;

public static final int DELAY_POT_DEGREES = 270;

public static final int TRANSDUCER_MAX_VALUE = 200;

public static final int ARM_POT_MAX_VALUE = 270;// 360 is a placeholder

// The amount of time the camera is delayed for picture taking, so the light
// isn't too bright.
public static final double CAMERA_DELAY_TIME = .25;

// Makes the brightness to a visible level so our drivers can see.
public static final int NORMAL_AXIS_CAMERA_BRIGHTNESS = 60;

// Crazy dark brightness for retroreflective pictures
public static final int MINIMUM_AXIS_CAMERA_BRIGHTNESS = 6;

public static final int AXIS_FPS = 15;

public static final Resolution AXIS_RESOLUTION = AxisCamera.Resolution.k320x240;

public static final double ULTRASONIC_SCALING_FACTOR = 0.050548;

public static final double MINIMUM_POT_SCALING_VALUE = .1;

public static final double CAMERA_FOCAL_LENGTH_MM = 4.4;

public static final double CAMERA_FOCAL_LENGTH_PIXELS = 390.099;
// =focal_pixel = (image_width_in_pixels * 0.5) / tan(Horiz_FOV * 0.5 * PI/180)

public static final double VISION_GOAL_HEIGHT_FT = 5.5;

public static final double CAMERA_MOUNT_ANGLE_ABOVE_HORIZONTAL_RADIANS = .9599;

// ---------------------------------
// denote whether we are running in
// the lab or not. This will allow
// us to test in the lab once the
// robot is bagged
// ---------------------------------
public static boolean runningInLab = false;
// -------------------------------------
// Private Constants
// -------------------------------------

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

public static TalonSRX rightRearMotor = new TalonSRX(2);

public static TalonSRX leftRearMotor = new TalonSRX(3);

// Switched the bottom two ports
public static TalonSRX rightFrontMotor = new TalonSRX(4);

public static TalonSRX leftFrontMotor = new TalonSRX(1);

// ------------------------------------
// Victor classes
// ------------------------------------
public static VictorSP armMotor = new VictorSP(0);

public static VictorSP armIntakeMotor = new VictorSP(5);

// ------------------------------------
// CAN classes
// ------------------------------------
public static PowerDistributionPanel pdp = new PowerDistributionPanel();

// ====================================
// Relay classes
// ====================================
// Relay that controls the RingLight
public static Relay ringLightRelay = new Relay(0);

// ------------------------------------
// Compressor class - runs the compressor
// with a single relay
// ------------------------------------
public static Compressor compressor = new Compressor();

// ====================================
// Digital Inputs
// ====================================
// ------------------------------------
// Single and double throw switches
// ------------------------------------

// Turns autonomous on or off.
/**
 * A physical switch that decides whether or not to run autonomous.
 */
public static SingleThrowSwitch autonomousEnabled = new SingleThrowSwitch(
        19);

// THis is when the switch is switched up
public static SingleThrowSwitch inCompetition = new SingleThrowSwitch(
        7);

// This is when the switch is switched down
public static SingleThrowSwitch inDemo = new SingleThrowSwitch(8);

// Shoot high/low switch
public static DoubleThrowSwitch inLab = new DoubleThrowSwitch(
        inCompetition, inDemo);

/**
 * Displays the starting position.
 * Position 0 on the switch corresponds to position 1, 1 to 2, etc.
 */
public static SixPositionSwitch startingPositionDial = new SixPositionSwitch(
        14, 15, 16, 17, 18, 21, true);

// ------------------------------------
// Gear Tooth Sensors
// ------------------------------------

// ------------------------------------
// Encoders
// ------------------------------------
public static Encoder leftRearEncoder = new Encoder(10, 11); // @AHK ST
                                                             // LOUIS;5,6;12,13

public static Encoder rightRearEncoder = new Encoder(12, 13);// 3,4;10,11

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
public static IRSensor rightIR = new IRSensor(1);

public static IRSensor leftIR = new IRSensor(0);

public static IRSensor armIR = new IRSensor(2);

// ====================================
// I2C Classes
// ====================================

// **********************************************************
// SOLENOID I/O CLASSES
// **********************************************************
// ====================================
// Pnematic Control Module
// ====================================

// ====================================
// Solenoids
// ====================================
// ------------------------------------
// Double Solenoids
// ------------------------------------
// double solenoid that moves the camera

// TODO: Uncomment solenoids
public static DoubleSolenoid cameraSolenoid = new DoubleSolenoid(3, 4);

//
//// ------------------------------------
//// Single Solenoids
//// ------------------------------------
//// single solenoids that control the catapult
public static Solenoid catapultSolenoid0 = new Solenoid(0);

public static Solenoid catapultSolenoid1 = new Solenoid(1);

public static Solenoid catapultSolenoid2 = new Solenoid(2);

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
public static RobotPotentiometer delayPot = new RobotPotentiometer(1,
        DELAY_POT_DEGREES);

// transducer (written as a potentiometer)
// set to 50 to hit 100 psi accurately
public static RobotPotentiometer transducer = new RobotPotentiometer(2,
        TRANSDUCER_MAX_VALUE);

// to be used with the manipulator arm
public static RobotPotentiometer armPot = new RobotPotentiometer(3,
        ARM_POT_MAX_VALUE);


// -------------------------------------
// Sonar/Ultrasonic
// -------------------------------------
public static UltraSonic ultrasonic = new UltraSonic(4,
        ULTRASONIC_SCALING_FACTOR);



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

public static USBCamera usbCam = new USBCamera("cam0"); // AHK @cameratesting


// Declares the Axis camera
public static VisionScript doubleGreenRingScript = new VisionScript(
        new HSLColorThresholdOperator(55, 147, 14, 255, 78, 255),
        new RemoveSmallObjectsOperator(1, true),
        new ConvexHullOperator(false));

// public static VisionScript temp = new VisionScript();

public static KilroyCamera axisCamera = new KilroyCamera(true);

// public static ImageProcessing imageProcessor = new ImageProcessing(
// axisCamera);

public static ImageProcessor imageProcessor = new ImageProcessor(
        axisCamera, doubleGreenRingScript);


// **********************************************************
// DRIVER STATION CLASSES
// **********************************************************


// ------------------------------------
// DriverStations class
// ------------------------------------
public static final DriverStation driverStation = DriverStation
        .getInstance();

public static Guidance arrowDashboard = new Guidance();

// ------------------------------------
// Joystick classes
// ------------------------------------
public static Joystick leftDriver = new Joystick(0);

public static Joystick rightDriver = new Joystick(1);

public static Joystick leftOperator = new Joystick(2);

public static Joystick rightOperator = new Joystick(3);

public static MomentarySwitch cameraToggleButton = new MomentarySwitch(
        leftOperator, 2, false);

public static MomentarySwitch forwardToggleButton = new MomentarySwitch(
        leftDriver, 8, false);

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
public static Transmission_old transmission = new Transmission_old(
        rightFrontMotor, rightRearMotor, leftFrontMotor,
        leftRearMotor, rightRearEncoder, rightRearEncoder,
        leftRearEncoder, leftRearEncoder);

// ------------------------------------
// Drive system
// ------------------------------------
public static Drive drive = new Drive(transmission, axisCamera,
        ringLightRelay);

// -------------------
// Assembly classes (e.g. forklift)
// -------------------
public static ManipulatorArm pickupArm = new ManipulatorArm(armMotor,
        armIntakeMotor, armPot, armIR);


// ------------------------------------
// Utility classes
// ------------------------------------
public static final Timer kilroyTimer = new Timer();

public static final Timer autoTimer = new Timer();

public static final Timer delayTimer = new Timer();

public static final Timer fireTimer = new Timer();

public static final Timer armOutOfWayTimer = new Timer();

public static final Timer cameraInTeleopTimer = new Timer();

public static final Timer testingTimer = new Timer();

public static final ErrorMessage errorMessage = new ErrorMessage(
        false /* append timelog */);

public static final MotorSafetyHelper leftRearMotorSafety = new MotorSafetyHelper(
        leftRearMotor);

public static final MotorSafetyHelper rightRearMotorSafety = new MotorSafetyHelper(
        rightRearMotor);

public static final MotorSafetyHelper leftFrontMotorSafety = new MotorSafetyHelper(
        leftFrontMotor);

public static final MotorSafetyHelper rightFrontMotorSafety = new MotorSafetyHelper(
        rightFrontMotor);

} // end class
