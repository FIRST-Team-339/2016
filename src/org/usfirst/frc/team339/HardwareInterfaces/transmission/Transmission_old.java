
package org.usfirst.frc.team339.HardwareInterfaces.transmission;

/**
 * sets the percentage of the joysticks range for the
 * Third gear for either only one motor configuration or
 * for the right motor only
 * // ====================================================================
 * // FILE NAME: Transmission.java (Team 339 - Kilroy)
 * //
 * // CREATED ON: Sep 19, 2009
 * // CREATED BY: Bob Brown
 * // Converted from C++ code written by Josh Shields - 2009 season
 * // MODIFIED ON:
 * // MODIFIED BY:
 * // ABSTRACT:
 * // This class represents a transmission object. A transmission
 * // object keeps track of a software gear number and can alter the
 * // speed of a motor based on the current gear number. A hardware
 * // piston is also supported and will change state between gears
 * // one and two.
 * //
 * // NOTE: Please do not release this code without permission from
 * // Team 339.
 * // ====================================================================
 * 
 * package org.usfirst.frc.team339.HardwareInterfaces.transmission;
 * 
 * import java.util.Vector;
 * import
 * org.usfirst.frc.team339.HardwareInterfaces.PIDVelocityController;
 * import edu.wpi.first.wpilibj.DoubleSolenoid;
 * import edu.wpi.first.wpilibj.Encoder;
 * import edu.wpi.first.wpilibj.Relay;
 * import edu.wpi.first.wpilibj.SpeedController;
 * 
 * // -------------------------------------------------------
 * /**
 * This class represents a transmission object. A transmission
 * object keeps track of a software gear number and can alter the
 * speed of a motor based on the current gear number. A hardware
 * piston is also supported and will change state between gears
 * one and two.
 *
 * @class Transmission
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
import java.util.Vector;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.PIDVelocityController;
import org.usfirst.frc.team339.Utils.ErrorMessage.PrintsTo;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;

// -------------------------------------------------------
/**
 * This class represents a transmission object. A transmission
 * object keeps track of a software gear number and can alter the
 * speed of a motor based on the current gear number. A hardware
 * piston is also supported and will change state between gears
 * one and two.
 *
 * @class Transmission
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public final class Transmission_old
{
/**
 * -------------------------------------------------------
 *
 * @description This is the debug state. Debugging can be either
 *              DEBUG_NONE, DEBUG_ALL, DEBUG_ONLY_NON_ZERO. This is used
 *              to output information to the screen
 *              -------------------------------------------------------
 */
public static enum debugStateValues
    {
    DEBUG_NONE, DEBUG_ALL, DEBUG_ONLY_NON_ZERO, DEBUG_ONLY_PID_DATA
    }

// -------------------------------------
// not supported in the ME type Java, so
// use a contrived class to simulate the
// following enum
// private enum JoystickDirection {NORMAL, REVERSED};
// -------------------------------------
private class JoystickDirection
{
static final int REVERSED = -1;
static final int NORMAL = 1;
int value;

public JoystickDirection (int initialValue)
{
    this.value = initialValue;
} // end constructor

public int get ()
{
    return (this.value);
} // end get()

public int set (int changedValue)
{
    return (this.value = changedValue);
} // end set()
} // end class

// -------------------------------------
// not supported in the ME type Java, so
// use a contrived class to simulate the
// following enum
// private enum WhichJoystick {ONE_JOYSTICK,
// LEFT_JOYSTICK, RIGHT_JOYSTICK};
// -------------------------------------
public class WhichJoystick
{
static final int ONE_JOYSTICK = 1;
static final int LEFT_JOYSTICK = 2;
static final int RIGHT_JOYSTICK = 3;
int value;

public WhichJoystick (int initialValue)
{
    this.value = initialValue;
} // end constructor

public int get ()
{
    return (this.value);
} // end get()

public int set (int changedValue)
{
    return (this.value = changedValue);
} // end set()
} // end class

/**
 * -------------------------------------------------------
 *
 * @description
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
private static final int MAX_GEARS_ALLOWED = 5;
// inches to allow the encoders to differ by for correction
static final double DISTANCE_THRESHOLD = 0.7;

/**
 * -------------------------------------------------------
 *
 * @description holds the percentage of deadband zone that is
 *              off limits on the joystick
 * @author Bob Brown
 * @written Feb 19, 2010
 *          -------------------------------------------------------
 */
private double deadbandPercentageZone = 0.0;

private debugStateValues debugState = debugStateValues.DEBUG_NONE;

/**
 * -------------------------------------------------------
 *
 * @description This is array of digital outputs on the driver station
 *              with each one corresponding to the gear that we are in.
 *              -------------------------------------------------------
 */
private final Vector<Integer> digitalChannelNumberForGearLight =
        new Vector<Integer>();

/**
 * -------------------------------------------------------
 *
 * @description keeps track of which gear we are presently in
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private int gear;

/**
 * -------------------------------------------------------
 *
 * @description These constants are used when the controls() function
 *              for the transmission is called. The transmission will
 *              use the percentage constant for the current gear to
 *              control the output power. For example, constant of
 *              40 will reduce the output power in that gear to 40%
 *              of its value prior to calling controls().
 *
 *              In other words, if you want your drive motors to run at
 *              half speed in first gear, set firstGearPercentage equal
 *              to 50 (percent).
 *
 *              Keep in mind that a hardware transmission is in effect
 *              between first and second gears. Therefore, it is not
 *              unusual to have a higher percentage constant for first
 *              gear than second gear.
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
private double oneOrRightFirstGearPercentage = 0.50;

private double leftFirstGearPercentage = 0.50;

private double oneOrRightSecondGearPercentage = 0.70;

private double leftSecondGearPercentage = 0.70;

private double oneOrRightThirdGearPercentage = 0.80;

private double leftThirdGearPercentage = 0.80;

private double oneOrRightFourthGearPercentage = 0.90;

private double leftFourthGearPercentage = 0.90;

private double oneOrRightFifthGearPercentage = 1.00;

private double leftFifthGearPercentage = 1.00;

/**
 * -------------------------------------------------------
 *
 * @description The denotes whether or not the joysticks respond
 *              correctly in their inputs. WPI wants the joystick
 *              to go from 1.0, which is full reverse, to -1.0, which
 *              is full forward. This denotes that the left joystick
 *              is reversed or normal and if reversed, goes from -1.0,
 *              denoting full reverse, to 1.0 denoting full forward
 * @author Bob Brown
 * @written Jan 21, 2011
 *          -------------------------------------------------------
 */
private final JoystickDirection leftJoystickIsReversed =
        new JoystickDirection(
                JoystickDirection.NORMAL);

/**
 * -------------------------------------------------------
 *
 * @description This is the left SpeedController
 *              that the transmission controls. It can be
 *              either the Jaguar or a Victor. Most
 *              methods don't care if you specify the
 *              speed controller or not.
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
private SpeedController leftSpeedController = null;

/**
 * -------------------------------------------------------
 *
 * @description This is the maximum gear number allowed.
 * @TODO: AT PRESENT THERE IS ONLY CODE FOR UP TO 5 GEARS.
 *        (See MAX_GEARS_ALLOWED)
 *        TO CORRECT THIS SEE THE Controls() FUNCTION.
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private int maxGears = 3;

/**
 * -------------------------------------------------------
 *
 * @description This is the only or right SpeedController
 *              that the transmission controls. It can be
 *              either the Jaguar or a Victor. Most
 *              methods don't care if you specify the
 *              speed controller or not.
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
private SpeedController oneOrRightSpeedController = null;

/**
 * -------------------------------------------------------
 *
 * @description This is the gear number to shift if a hardware
 *              transmission is in place. When we try to set a
 *              gear that is higher than this number, we will
 *              move the solenoid first.
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private int physicalGearToChange = 1;

/**
 * -------------------------------------------------------
 *
 * @description This stores the present state of the downshift button.
 *              If it is in the process of being pushed it will be
 *              true, otherwise it will be false. The downshift button
 *              is considered to be a momentary button and only changes
 *              gears when it is pushed for the first time in the
 *              momentary set.
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private boolean presentDownshiftState = false;

/**
 * -------------------------------------------------------
 *
 * @description This stores the present state of the upshift button.
 *              If it is in the process of being pushed it will be
 *              true, otherwise it will be false. The upshift button
 *              is considered to be a momentary button and only changes
 *              gears when it is pushed for the first time in the
 *              momentary set.
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private boolean presentUpshiftState = false;

/**
 * -------------------------------------------------------
 *
 * @description The denotes whether or not the joysticks respond
 *              correctly in their inputs. WPI wants the joystick
 *              to go from 1.0, which is full reverse, to -1.0, which
 *              is full forward. This denotes that the right joystick
 *              is reversed or normal and if reversed, goes from -1.0,
 *              denoting full reverse, to 1.0 denoting full forward
 * @author Bob Brown
 * @written Jan 21, 2011
 *          -------------------------------------------------------
 */
private final JoystickDirection rightJoystickIsReversed =
        new JoystickDirection(
                JoystickDirection.NORMAL);

/**
 * -------------------------------------------------------
 *
 * @description This is the relay that controls the
 *              gear that the transmission is in. We can have
 *              either a double solenoid or a relay to control the
 *              transmission
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private Relay transmissionRelay = null;

/**
 * -------------------------------------------------------
 *
 * @description This is the solenoid that controls the
 *              the gear that the transmission is in. We can have
 *              either a double solenoid or a relay to control the
 *              transmission
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private DoubleSolenoid transmissionSolenoids = null;

/**
 * -------------------------------------------------------
 *
 * @description This denotes to the transmission to use the hardware
 *              gears only. This means that we will not use the
 *              software percentages that we may store. This may
 *              be used to denote that we are using this like an
 *              automatic and we don't want to software detune the
 *              gears.
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private boolean useHardwareGearsOnly = false;

// deadband for x axis on 3D joystick
private double directionalXOrthagonalZone = 0.0;

// deadband for y axis on 3D joystick
private double directionalYOrthagonalZone = 0.0;

// left rear speed controller for 4-wheel drive
// or mecanum
private SpeedController leftRearSpeedController = null;

// right rear speed controller for 4-wheel drive
// or mecanum
private SpeedController rightRearSpeedController = null;

// Motor encoder of the front left wheel
private Encoder leftMotorEncoder = null;

// Motor encoder of the rear left wheel
private Encoder leftRearMotorEncoder = null;

// Motor encoder of the front right wheel
private Encoder oneOrRightMotorEncoder = null;

// Motor encoder of the rear right wheel
private Encoder rightRearMotorEncoder = null;

// ------------------------
// PID loop and constants
// ------------------------
public PIDVelocityController pidLeftController = null;

public PIDVelocityController pidLeftRearController = null;

public PIDVelocityController pidRightController = null;

public PIDVelocityController pidRightRearController = null;

// Proportional to the current error value.
private double pidProportionalGain = 0.00;

// Proportional to the magnitude and duration of the error.
private double pidIntegralGain = 0.00;

// Proportional to the change over time of the error.
private double pidDerivativeGain = 0.00;

// Percent error. With this, if the difference between our
// PID encoder rate input and setpoint is at most x% of our
// total encoder rate range.
// e.g. with a range of -90..90 (180) and a tolerance
// of 5%, we can at most have an error of 9 in/sec.

// base value for our motor to prevent oscillations of the motor.
// stops jerkiness.
private double pidFeedForward = 0.00;

private double pidTolerance = 5.0;

// min range of encoders (our PID input)
private double minInputRange = -90.0;

// max speed to take in as rate from
// (50 ms for one PID cycle)
private double maxInputRange = 90.0;

// min range of the motor controllers (PID output)
private double minOutputRange = -1.0;

// max allowable range of the motor controllers
private double maxOutputRange = 1.0;

// determines whether or not we are in mecanum drive
// vs a 4 wheel drive
private boolean isMecanum = false;

// sets whether to use PID controls from outside of Transmission.
// this sets whether we ever need to enable the loops in the first
// place.
private boolean usePID = false;

// conversion from joystick input to encoder rate to
// use joystick input as a setpoint. this is because setpoint
// is compared to the encoder pidGet(), which is the rate of the encoder.
// rate is in inches/second.
private double maxEncoderRate = 75.0;

// how many ticks we take into account the total error in our PID
// controllers before just using the current error
private int integralUseCounter = 0;

// Previous measurements of the left/right encoders for use with the brake()
// method.
private double brakePreviousDistanceL = 0.0;

private double brakePreviousPreviousDistanceL = 0.0;

private double brakePreviousDistanceR = 0.0;

private double brakePreviousPreviousDistanceR = 0.0;

// distance we've moved in two reads where we consider ourselves braked in
// the brake() method.
private final double AUTO_ENCODER_THRESHOLD_INCHES = 0.25;

private double mecanumRotationCorrection = 0.0;

// ------------------------------------------------------------------
/**
 * This function controls a 4 motor controller mecanum drive with
 * a single joystick's input. It can be used to correct, based on the
 * encoders (you must have called initEncoders(...) already)
 *
 * @param magnitude
 *            magnitude of the joystick
 * @param direction
 *            direction of the joystick in degrees
 * @param rotation
 *            rotation of the joystick
 * @param driveStraight
 *            whether we should do encoder based correction of straightness
 * @author Nathan Lydick
 * @written Feb 4, 2014
 *          ----------------------------------------------------------------
 *          --
 */

private double rotationCorrection = 0;

private boolean isFourWheel = false;

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public Transmission_old ()
{
    this.init();
    this.setMaxGear(1);
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionSolenoids
 *            - DoubleSolenoid used to change
 *            gears if we are using hardware gears
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public Transmission_old (final DoubleSolenoid transmissionSolenoids)
{
    this.transmissionSolenoids = transmissionSolenoids;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionSolenoids
 *            - DoubleSolenoid used to change
 *            gears if we are using hardware gears
 * @param firstSpeedController
 *            - speed controller for one motor only
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public Transmission_old (final DoubleSolenoid transmissionSolenoids,
        final SpeedController firstSpeedController)
{
    this.transmissionSolenoids = transmissionSolenoids;
    this.oneOrRightSpeedController = firstSpeedController;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionSolenoids
 *            - DoubleSolenoid used to change
 *            gears if we are using hardware gears
 * @param rightSpeedController
 *            - right speed controller
 * @param leftSpeedController
 *            - left speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public Transmission_old (final DoubleSolenoid transmissionSolenoids,
        final SpeedController rightSpeedController,
        final SpeedController leftSpeedController)
{
    this.isFourWheel = false;
    this.transmissionSolenoids = transmissionSolenoids;
    this.oneOrRightSpeedController = rightSpeedController;
    this.leftSpeedController = leftSpeedController;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionRelay
 *            - Relay used to change
 *            gears if we are using hardware gears
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public Transmission_old (final Relay transmissionRelay)
{
    this.transmissionRelay = transmissionRelay;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionRelay
 *            - Relay used to change
 *            gears if we are using hardware gears
 * @param firstSpeedController
 *            - speed controller for one motor only
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public Transmission_old (final Relay transmissionRelay,
        final SpeedController firstSpeedController)
{
    this.transmissionRelay = transmissionRelay;
    this.oneOrRightSpeedController = firstSpeedController;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param transmissionRelay
 *            - Relay used to change
 *            gears if we are using hardware gears
 * @param rightSpeedController
 *            - right speed controller
 * @param leftSpeedController
 *            - left speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public Transmission_old (final Relay transmissionRelay,
        final SpeedController rightSpeedController,
        final SpeedController leftSpeedController)
{
    this.isFourWheel = false;
    this.transmissionRelay = transmissionRelay;
    this.oneOrRightSpeedController = rightSpeedController;
    this.leftSpeedController = leftSpeedController;
    this.init();
} // end Transmission

// -------------------------------------------------------
/**
 * constructor for the transmission class
 *
 * @method Transmission
 * @param rightSpeedController
 *            - right speed controller
 * @param leftSpeedController
 *            - left speed controller
 * @author Chris V-Hizzle
 * @written 08 January 2014
 *          -------------------------------------------------------
 */
public Transmission_old (final SpeedController rightSpeedController,
        final SpeedController leftSpeedController)
{
    this.isFourWheel = false;
    this.oneOrRightSpeedController = rightSpeedController;
    this.leftSpeedController = leftSpeedController;
    this.init();
} // end Transmission

public Transmission_old (
        final SpeedController rightFrontSpeedController,
        final SpeedController rightRearSpeedController,
        final SpeedController leftFrontSpeedController,
        final SpeedController leftRearSpeedController)
{
    this.isFourWheel = true;
    this.oneOrRightSpeedController = rightFrontSpeedController;
    this.leftSpeedController = leftFrontSpeedController;
    this.rightRearSpeedController = rightRearSpeedController;
    this.leftRearSpeedController = leftRearSpeedController;

    // initialize mecanum drive
    /*
     * this.mecanumDrive = new RobotDrive(this.leftSpeedController,
     * this.leftRearSpeedController,
     * this.oneOrRightSpeedController,
     * this.rightRearSpeedController);
     * this.mecanumDrive.setSafetyEnabled(false);
     */

    this.initPIDControllers();
    this.init();
} // end Transmission

/**
 * Constructor for a four-wheel drive transmission class with
 * already-initialized encoders
 *
 * @param rightFrontSpeedController
 * @param rightRearSpeedController
 * @param leftFrontSpeedController
 * @param leftRearSpeedController
 * @param rightFrontEncoder
 * @param rightRearEncoder
 * @param leftFrontEncoder
 * @param leftRearEncoder
 */
public Transmission_old (
        final SpeedController rightFrontSpeedController,
        final SpeedController rightRearSpeedController,
        final SpeedController leftFrontSpeedController,
        final SpeedController leftRearSpeedController,
        Encoder rightFrontEncoder, Encoder rightRearEncoder,
        Encoder leftFrontEncoder, Encoder leftRearEncoder)
{
    this.isFourWheel = true;
    this.oneOrRightSpeedController = rightFrontSpeedController;
    this.leftSpeedController = leftFrontSpeedController;
    this.rightRearSpeedController = rightRearSpeedController;
    this.leftRearSpeedController = leftRearSpeedController;

    this.initEncoders(rightFrontEncoder, rightRearEncoder,
            leftFrontEncoder, leftRearEncoder);

    this.initPIDControllers();

    this.init();

} // end Transmission

//-------------------------------------------------------
/**
 * Check to see if we are stopped via reversing the motors
 * by a small amount. We determine stopped in that the last
 * 3 times to this function we have the same distance on both
 * wheel encoders. If that is true then we set up for next
 * time we are called and return a true, otherwise we keep
 * track of everything and return a false. Makes a call to
 * brake(bothMotorBrakeVoltages, bothMotorBrakeVoltages);
 *
 * must initialize transmission instance's encoders via initEncoders()
 *
 * @see initEncoders()
 *
 *      distancePerPulse on the encoders MUST be set for this to work.
 * @see Transmission.setEncodersDistancePerPulse(float distance)
 * @see 2014 brake() in Kilroy.java for original implementation.
 * @method brake
 * @param bothMotorBrakeVoltages
 *            - brakeVoltage to set the left and motor(s) (in a reverse
 *            manner)
 *            to "stop" the motors. (Usually this is -.1)
 * @return boolean - is or isn't stopped yet
 * @author Bob Brown
 * @written March 15, 2016
 *          -------------------------------------------------------
 */
public boolean brake (final double bothMotorBrakeVoltages)
{
    return (brake(bothMotorBrakeVoltages, bothMotorBrakeVoltages));
} // end of brake()

// -------------------------------------------------------
/**
 * Attempts to stop the robot via reversing the motors
 * by a small amount. We determine stopped in that the last
 * 3 times to this function we have the same distance on both
 * wheel encoders. If that is true then we set up for next
 * time we are called and return a true, otherwise we keep
 * track of everything and return a false.
 *
 * must initialize transmission instance's encoders via initEncoders()
 *
 * @see initEncoders()
 *
 *      distancePerPulse on the encoders MUST be set for this to work.
 * @see Transmission.setEncodersDistancePerPulse(float distance)
 * @see 2014 brake() in Kilroy.java for original implementation.
 * @method brake
 * @param lMotorBrakeVoltage
 *            - brakeVoltage to set the left motor(s) (in a reverse
 *            manner)
 *            to "stop" the motors. (Usually this is -.1)
 * @param rMotorBrakeVoltage
 *            - brakeVoltage to set the right motor(s) (in a reverse
 *            manner)
 *            to "stop" the motors. (Usually this is -.1)
 * @return boolean - is or isn't stopped yet
 * @author Bob Brown
 * @written Feb 2, 2011
 *          -------------------------------------------------------
 */
public boolean brake (final double lMotorBrakeVoltage,
        final double rMotorBrakeVoltage)
{
    // UNDER PENALTY OF DEATH - don't use without calling initEncoders()
    // AND setting the distancePerPulse on them via
    // setEncodersDistancePerPulse()
    // in that order.
    this.savedDeadBandRange = this.getJoystickDeadbandRange();
    if ((this.leftMotorEncoder == null) ||
            (this.oneOrRightMotorEncoder == null))
        {
        this.setJoystickDeadbandRange(savedDeadBandRange);
        return true; // just stop if we don't even have encoders.
        }
    this.setJoystickDeadbandRange(0.0);
    // If the current distance on all of our encoders is
    // close enough to the previous values
    //
    // OR
    //
    // the measurement before THAT is greater than our
    // previous measurement, indicating we go backwards...
    //
    // then we're done and we stop the motors.
    if (((Math.abs(
            this.leftMotorEncoder
                    .getDistance()) >= (this.brakePreviousDistanceL
                            -
                            this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.leftMotorEncoder
                            .getDistance()) <= (this.brakePreviousDistanceL
                                    +
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.leftMotorEncoder
                            .getDistance()) >= (this.brakePreviousPreviousDistanceL
                                    -
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.leftMotorEncoder
                            .getDistance()) <= (this.brakePreviousPreviousDistanceL
                                    +
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.oneOrRightMotorEncoder
                            .getDistance()) >= (this.brakePreviousDistanceR
                                    -
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.oneOrRightMotorEncoder
                            .getDistance()) <= (this.brakePreviousDistanceR
                                    +
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.oneOrRightMotorEncoder
                            .getDistance()) >= (this.brakePreviousPreviousDistanceR
                                    -
                                    this.AUTO_ENCODER_THRESHOLD_INCHES))
            &&
            (Math.abs(
                    this.oneOrRightMotorEncoder
                            .getDistance()) <= (this.brakePreviousPreviousDistanceR
                                    +
                                    this.AUTO_ENCODER_THRESHOLD_INCHES)))
            ||
            ((this.brakePreviousPreviousDistanceL >= this.brakePreviousDistanceL)
                    &&
                    (this.brakePreviousPreviousDistanceR >= this.brakePreviousDistanceR)
                    &&
                    (this.brakePreviousDistanceL >= this.leftMotorEncoder
                            .getDistance())
                    &&
                    (this.brakePreviousDistanceR >= this.oneOrRightMotorEncoder
                            .getDistance())))
        {
        // System.out.println("DONE!");
        this.brakePreviousDistanceL = 0.0;
        this.brakePreviousDistanceR = 0.0;
        this.brakePreviousPreviousDistanceL = 0.0;
        this.brakePreviousPreviousDistanceR = 0.0;
        this.controls(0.0, 0.0);
        this.setJoystickDeadbandRange(savedDeadBandRange);
        return true;
        } // if
    this.brakePreviousPreviousDistanceR =
            this.brakePreviousDistanceR;
    this.brakePreviousPreviousDistanceL =
            this.brakePreviousDistanceL;
    this.brakePreviousDistanceR = Math.abs(
            this.oneOrRightMotorEncoder.getDistance());
    this.brakePreviousDistanceL = Math.abs(
            this.leftMotorEncoder.getDistance());
    // continue braking
    // if we are in mecanum, call the appropriate method to
    // send the braking voltage backwards.
    if (this.isMecanumDrive() == true)
        {
        this.controls(lMotorBrakeVoltage, 180.0, //
                0.0);
        }
    else
        {
        // otherwise, use our 2 or 4 wheel braking method.
        this.controls(lMotorBrakeVoltage, rMotorBrakeVoltage);
        }
    this.setJoystickDeadbandRange(savedDeadBandRange);
    return false;
} // end brake

private double savedDeadBandRange;

// -------------------------------------------------------
/**
 * This function passes in the state of the downshift button.
 * It will be stored and compared to the previous state to
 * determine if the user has just pushed the downshift button
 * and wants the gears to go down by one.
 *
 * @method checkDownshiftButton
 * @param buttonOn
 *            - what the button state should become
 * @return returns the state of button at this time
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public boolean checkDownshiftButton (final boolean buttonOn)
{
    // -------------------------------------
    // if the button state has changed from
    // the last call then do something. Only
    // change gears if the button was off before
    // and now is suddenly on
    // -------------------------------------
    if (this.presentDownshiftState != buttonOn)
        {
        if (buttonOn == true)
            {
            this.downshift(1);
            }
        this.presentDownshiftState = buttonOn;
        } // if
    return (this.presentDownshiftState);
} // end checkDownshiftButton

// -------------------------------------------------------
/**
 * This function passes in the state of both upshift
 * and the downshift buttons. These will be stored and
 * compared to the previous state to determine if
 * the user has just pushed any button and wants the
 * gears to change by one.
 *
 * @method checkShiftButtons
 * @param upShiftButtonOn
 *            - shows the present state of
 *            the upShiftButton
 * @param downShiftButtonOn
 *            - shows the present state of
 *            the downShiftButton
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void checkShiftButtons (final boolean upShiftButtonOn,
        // pass in the button that will
        // control any upshifts
        final boolean downShiftButtonOn)
// pass in the button that will
// control any downshifts
{
    this.checkUpshiftButton(upShiftButtonOn);
    this.checkDownshiftButton(downShiftButtonOn);
} // end checkShiftButtons

// -------------------------------------------------------
/**
 * This function passes in the state of the upshift button.
 * It will be stored and compared to the previous state to
 * determine if the user has just pushed the upshift button
 * and wants the gears to go up by one.
 *
 * @method checkUpshiftButton
 * @param buttonOn
 *            - the present state of the upshift button
 * @return returns the present state of the upshift button
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public boolean checkUpshiftButton (final boolean buttonOn)// pass in the
// button that
// will
// control any upshifts
{
    // -------------------------------------
    // if the button state has changed from
    // the last call then do something. Only
    // change gears is the button was off before
    // and now is suddenly on
    // -------------------------------------
    if (this.presentUpshiftState != buttonOn)
        {
        if (buttonOn == true)
            {
            this.upshift(1);
            }
        this.presentUpshiftState = buttonOn;
        } // if
    return (this.presentUpshiftState);
} // end checkUpshiftButton

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of two speed controllers based on the current
 * gear. This is used with the left and right
 * drive motors, left and right joysticks and the two
 * buttons that denote whether or not to upshift or downshift.
 *
 * @method Controls
 * @param upShiftSwitch
 *            - boolean - used to denote whether or not to
 *            upshift at this time
 * @param downShiftSwitch
 *            - boolean - used to denote whether or not to
 *            downshift at this time
 * @param leftJoystickInputValue
 *            - float - used to set
 *            the left motor speed to that value
 * @param leftSpeedController
 *            - SpeedController - controls the left motor
 * @param rightJoystickInputValue
 *            - float - used to set
 *            the right motor speed to that value
 * @param rightSpeedController
 *            - SpeedController - controls the right motor
 * @author Bob Brown
 * @written Jan 13, 2011
 *          -------------------------------------------------------
 */
public void controls (final boolean upShiftSwitch,
        // the switch that denotes an upShift
        final boolean downShiftSwitch,
        // the switch that denotes an downShift
        final double leftJoystickInputValue,
        // the input value from the left
        // joystick that controls the
        // left speed controller
        final SpeedController leftSpeedController,
        // the left Motor speed controller
        final double rightJoystickInputValue,
        // the input value from the right
        // joystick that controls the
        // right speed controller
        final SpeedController rightSpeedController)
// the right Motor speed controller
{
    // -------------------------------------
    // make the gears correct as the user wants
    // them now.
    // -------------------------------------
    this.checkShiftButtons(upShiftSwitch, downShiftSwitch);
    // -------------------------------------
    // since we have two motors to control,
    // call controls() to process the input
    // -------------------------------------
    this.controls(leftJoystickInputValue, leftSpeedController,
            rightJoystickInputValue, rightSpeedController);
} // end Controls

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of a speed controller based on the current
 * gear. This applies to any of the speed controllers
 * (either Jaguar or Victor) Note that it must be the
 * right joystick. The one speed controller must have
 * previously been set-up, either in the constructor
 * of via a setSpeedController() function
 *
 * @method controls
 * @param joystickInputValue
 *            - a float value which is used to set
 *            the motor speed to that value
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void controls (final double joystickInputValue)
// the input value from the
// joystick that controls this
// speed controller
{

    // -------------------------------------
    // call the controls() with
    // the the stored speed controller
    // -------------------------------------
    if (this.oneOrRightSpeedController != null)
        {
        this.controls(joystickInputValue,
                this.oneOrRightSpeedController);
        }
} // end controls

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of two speed controllers based on the current
 * gear. This is most often used with the left and right
 * drive motors. This applies to any of the speed controllers
 * (either Jaguar or Victor). That is the
 * joystick that can be reversed if it is necessary.
 * The speed controllers must have
 * previously been set-up, either in the constructor
 * of via a setSpeedController() functions
 *
 * @method Controls
 * @param leftJoystickInputValue
 *            - a float value which is used to set
 *            the left motor speed to that value
 * @param rightJoystickInputValue
 *            - a float value which is used to set
 *            the right motor speed to that value
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void controls (final double leftJoystickInputValue,
        // the input value from the left
        // joystick that controls the
        // left speed controller
        final double rightJoystickInputValue)
// the input value from the right
// joystick that controls the
// right speed controller
{
    // -------------------------------------
    // since we have two motors to control,
    // call controls() with the stored
    // speed controllers
    // -------------------------------------
    if ((this.oneOrRightSpeedController != null) &&
            (this.leftSpeedController != null))
        // Control all four wheels if all four are initialized
        // and we are in mecanum drive.
        if (isFourWheel // &&
        /* (this.isMecanumDrive() == true) */)// TODO commented out until it
                                              // makes sense
        {
        this.controls(leftJoystickInputValue,
                rightJoystickInputValue,
                this.leftSpeedController,
                this.leftRearSpeedController,
                this.oneOrRightSpeedController,
                this.rightRearSpeedController);
        }
        else
        {
        this.controls(leftJoystickInputValue,
                this.leftSpeedController,
                rightJoystickInputValue,
                this.oneOrRightSpeedController);
        }

} // end Controls

// ------------------------------------------------------------------
/**
 * This function controls a 4 motor controller mecanum drive with
 * a single joystick's input.
 *
 * @param magnitude
 *            magnitude of the joystick
 * @param direction
 *            direction of the joystick in degrees
 * @param rotation
 *            rotation of the joystick
 * @author Noah Golmant
 * @written Jan 15, 2014
 *          ----------------------------------------------------------------
 *          --
 */
public void controls (final double magnitude,
        final double direction,
        final double rotation)
{
    if ((this.leftSpeedController == null) ||
            (this.leftRearSpeedController == null) ||
            (this.oneOrRightSpeedController == null) ||
            (this.rightRearSpeedController == null))
        {
        if (this.getDebugState() == debugStateValues.DEBUG_ALL)
            {
            System.out.println("MECANUM controls(): motor is null");
            }
        return;
        }

    double tempRotation = rotation, tempMagnitude = magnitude,
            tempDirection = direction;

    // Rotation deadzone
    if (Math.abs(rotation) < this.deadbandPercentageZone)
        {
        tempRotation = 0.0;
        }
    if (Math.abs(magnitude) < this.deadbandPercentageZone)
        {
        tempMagnitude = 0.0;
        }

    // directional deadband code
    // Deadband for "up" angle on joystick
    if ((tempDirection >= (0.0 - this.directionalXOrthagonalZone))
            &&
            (tempDirection <= (0.0
                    + this.directionalXOrthagonalZone)))
        {
        tempDirection = 0.0;
        }
    else if ((tempDirection <= (-180.0 +
            this.directionalXOrthagonalZone)) &&
            (tempDirection >= (180.0 -
                    this.directionalXOrthagonalZone)))
        {
        tempDirection = 180.0;
        }
    else if ((tempDirection >= (-90.0 -
            this.directionalYOrthagonalZone)) &&
            (tempDirection <= (-90.0 +
                    this.directionalYOrthagonalZone)))
        {
        tempDirection = -90.0;
        }
    else if ((tempDirection <= (90.0 -
            this.directionalYOrthagonalZone)) &&
            (tempDirection >= (90.0
                    + this.directionalYOrthagonalZone)))
        {
        tempDirection = 90.0;
        }

    tempRotation = this.limit(rotation);

    // The following section of code is basically
    // stolen verbatum from the mecanumDrive_Polar method
    // of the RobotDrive class provided by WPI.

    // map the magnitude along our deadband range
    if ((this.usePID() == true) && (this.isPIDEnabled() == true))
        {
        tempMagnitude =
                this.mapSoftwareJoystickValues(tempMagnitude,
                        this.getFirstGearPercentage());
        }

    // limit magnitude to -1.0..1.0
    // Normalized along the cartesian axes
    tempMagnitude = this.limit(tempMagnitude) * Math.sqrt(2.0);

    // Only re-enable pid if we are actually moving and want to use PID.
    if ((Math.abs(tempMagnitude) >= this.deadbandPercentageZone) &&
            (this.isPIDEnabled() == false)
            && (this.usePID() == true))
        {
        this.enablePID();
        }

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("counter: " + this.integralUseCounter);
        System.out.println("F: " +
                this.pidLeftController.getPIDController().getF());
        }

    if ((Math.abs(tempMagnitude) >= this.deadbandPercentageZone) &&
            (this.isPIDEnabled() == true)
            && (this.usePID() == true))
        if (this.integralUseCounter == 10)
            {
            this.setPIDValues(this.pidProportionalGain, 0,
                    this.pidDerivativeGain, this.pidFeedForward,
                    this.pidTolerance);
            this.integralUseCounter++;
            }
        else if (this.integralUseCounter < 10)
            {
            this.integralUseCounter++;
            }

    // Add 45 to account for the angle of the rollers
    // on the mecanum wheels.
    final double dirInRad =
            ((tempDirection + 45.0) * 3.14159) / 180.0;
    final double cosD = Math.cos(dirInRad);
    final double sinD = Math.sin(dirInRad);

    // hold the correct values to send to the speed controllers.
    // we can then use them for PID.
    double leftFrontSpeed = (sinD * tempMagnitude) + tempRotation;
    double rightFrontSpeed = (cosD * tempMagnitude) - tempRotation;
    double leftRearSpeed = (cosD * tempMagnitude) + tempRotation;
    double rightRearSpeed = (sinD * tempMagnitude) - tempRotation;

    // limit the values to our motor range of -1..1
    leftFrontSpeed = this.limit(leftFrontSpeed);
    leftRearSpeed = this.limit(leftRearSpeed);
    rightFrontSpeed = this.limit(rightFrontSpeed);
    rightRearSpeed = this.limit(rightRearSpeed);
    // TODO: scale these, dividing them by the highest greater than 1 so as
    // to keep the
    // see
    // http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf
    // page 2
    final double highestSpeed = Math.max(
            Math.max(leftFrontSpeed, leftRearSpeed),
            Math.max(rightFrontSpeed, rightRearSpeed));

    // PID Controls and debug
    if ((this.usePID() == true) && (this.isPIDEnabled() == true) &&
            (this.pidRightController != null) &&
            (this.pidLeftController != null) &&
            (this.pidRightRearController != null) &&
            (this.pidLeftRearController != null))
        {

        System.out.println("PID is enabled");

        // If we have are stopped, disable PID to prevent us
        // from moving due to cumulative error.
        if (Math.abs(tempMagnitude) <= this.deadbandPercentageZone)
            {
            this.disablePID();
            this.resetPID();
            }
        else
            {
            // this.pidLeftController.setSetpoint(leftFrontSpeed *
            // this.maxEncoderRate *
            // this.leftMotorDirection.get());
            //
            // this.pidRightController.setSetpoint(rightFrontSpeed *
            // this.maxEncoderRate *
            // this.rightMotorDirection.get());
            //
            // this.pidRightRearController.setSetpoint(rightRearSpeed *
            // this.maxEncoderRate *
            // this.rightRearMotorDirection.get());
            //
            // this.pidLeftRearController.setSetpoint(leftRearSpeed *
            // this.maxEncoderRate *
            // this.leftRearMotorDirection.get());

            // If we need to, debug the PID data
            if ((this
                    .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
                    ||
                    (this.getDebugState() == debugStateValues.DEBUG_ALL))
                {
                System.out.println("[PID] SETPOINTS");
                System.out.println("[PID] " +
                        this.pidLeftController.getSetpoint() + " / "
                        +
                        this.pidLeftRearController.getSetpoint() +
                        " / " +
                        this.pidRightController.getSetpoint()
                        + " / " +
                        this.pidRightRearController.getSetpoint());

                System.out.println("[PID] GET");
                System.out.println("[PID] " +
                        this.leftMotorEncoder.pidGet() + " / " +
                        this.leftRearMotorEncoder.pidGet() + " / " +
                        this.oneOrRightMotorEncoder.pidGet() + " / "
                        +
                        this.rightRearMotorEncoder.pidGet());
                }
            }
        }
    else
        { // if we aren't using PID at all

        // Send the different vcltages to the speed controllers
        this.controlSpeedController(leftFrontSpeed *
                this.leftJoystickIsReversed.get(),
                this.leftSpeedController,
                WhichJoystick.ONE_JOYSTICK);

        this.controlSpeedController(leftRearSpeed *
                this.leftJoystickIsReversed.get(),
                this.leftRearSpeedController,
                WhichJoystick.ONE_JOYSTICK);

        this.controlSpeedController(rightRearSpeed *
                this.rightJoystickIsReversed.get(),
                this.rightRearSpeedController,
                WhichJoystick.ONE_JOYSTICK);

        this.controlSpeedController(rightFrontSpeed *
                this.rightJoystickIsReversed.get(),
                this.oneOrRightSpeedController,
                WhichJoystick.ONE_JOYSTICK);

        /*
         * System.out.println("LF: " + leftFrontSpeed + ", LR: "
         * + leftRearSpeed +
         * ", RF: " + rightFrontSpeed + ", RR: " + rightRearSpeed);
         */
        }
}

public void controls (double magnitude, double direction,
        double rotation,
        boolean driveStraight)
{

    if (driveStraight == true)
        {

        final double encoderSidesDelta = (Math.abs(
                this.leftMotorEncoder.getDistance()) + Math.abs(
                        this.leftRearMotorEncoder.getDistance()))
                -
                (Math.abs(this.oneOrRightMotorEncoder.getDistance())
                        +
                        Math.abs(
                                this.rightRearMotorEncoder
                                        .getDistance()));

        // final double diagonalEncoderPairDelta =
        // (this.oneOrRightMotorEncoder.getDistance() -
        // this.leftRearMotorEncoder
        // .getDistance());
        //
        // final double otherDiagonalEncoderPairDelta =
        // (this.rightRearMotorEncoder.getDistance() - this.leftMotorEncoder
        // .getDistance());
        //
        // double encoderPairDelta = 0.0;
        // if (Math.abs(diagonalEncoderPairDelta) > Math
        // .abs(otherDiagonalEncoderPairDelta))
        // {
        // encoderPairDelta = diagonalEncoderPairDelta;
        // }
        // else
        // {
        // encoderPairDelta = otherDiagonalEncoderPairDelta;
        // }

        this.rotationCorrection = encoderSidesDelta *
                this.mecanumRotationCorrection;

        if (this.getDebugState() == debugStateValues.DEBUG_ALL)
            {
            System.out.println("Rotation correction: " +
                    this.rotationCorrection + " pair delta: " +
                    encoderSidesDelta);
            }

        // Solve for magnitude of resultant vector of the encoders
        // get the sum of the vectors' x components, then y components...
        final double resultantY =
                ((this.leftMotorEncoder.getDistance()
                        +
                        this.leftRearMotorEncoder.getDistance()) +
                        this.oneOrRightMotorEncoder.getDistance() +
                        this.rightRearMotorEncoder.getDistance());

        final double resultantX =
                (((this.leftMotorEncoder.getDistance()
                        -
                        this.leftRearMotorEncoder.getDistance()) -
                        this.oneOrRightMotorEncoder.getDistance()) +
                        this.rightRearMotorEncoder.getDistance());

        // Calculate the resultant vector's direction with arctan
        // Math.atan returns in radians, so we have to convert to degrees
        // subtract it from 90.0 to account for how the location of our
        // calculated
        // is along the unit circle.
        final double resultantThetaInDegrees = 90.0 - ((Math.atan2(
                resultantY, resultantX) * 180.0) / 3.1415);

        double deltaTheta = (direction - resultantThetaInDegrees);

        // Limit to a max compensation of about 30 degrees
        if (deltaTheta > 30.0)
            {
            deltaTheta = 30.0;
            }
        else if (deltaTheta < -30.0)
            {
            deltaTheta = -30.0;
            }

        if (this.getDebugState() == debugStateValues.DEBUG_ALL)
            {
            System.out.println(
                    "Resultant angle" + resultantThetaInDegrees +
                            " delta theta * 1: "
                            + (1.0 * deltaTheta));
            // System.out.println("Rotation correction: " +
            // this.rotationCorrection);
            }

        this.controls(magnitude, direction + (deltaTheta * 1.4),
                this.rotationCorrection);
        }
    else
        {
        this.controls(magnitude, direction, rotation);
        }
}

// ------------------------------------------------------------------
/**
 * Controls a 4 motor controller drive system with two joysticks.
 *
 * @param leftJoystickValue
 *            left joystick y axis value
 * @param rightJoystickValue
 *            right joystick y axis value
 * @param leftSpeedController
 *            front left motor controller
 * @param leftRearSpeedController
 *            rear left motor controller
 * @param rightSpeedController
 *            front right motor controller
 * @param rightRearSpeedController
 *            rear right motor controller
 * @author Noah Golmant
 * @written Jan 15 2014
 *          ----------------------------------------------------------------
 *          --
 */
public void controls (double leftJoystickValue,
        double rightJoystickValue,
        SpeedController leftSpeedController,
        SpeedController leftRearSpeedController,
        SpeedController rightSpeedController,
        SpeedController rightRearSpeedController)
{

    if ((this.isPIDEnabled() == true) &&
            (this.pidRightController != null) &&
            (this.pidLeftController != null))
        if ((this
                .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
                ||
                (this.getDebugState() == debugStateValues.DEBUG_ALL))
            {
            System.out.println("[PID] RIGHT S" +
                    this.pidRightController.getSetpoint() + "/"
                    + "P" +
                    this.pidRightController.getPIDController()
                            .getP()
                    +
                    "/" +
                    "I" +
                    this.pidRightController.getPIDController()
                            .getI()
                    +
                    "/" + "D" +
                    this.pidRightController.getPIDController()
                            .getD());
            System.out.println("[PID] LEFT S" +
                    this.pidLeftController.getSetpoint() + "/" + "P"
                    +
                    this.pidLeftController.getPIDController().getP()
                    +
                    "/" +
                    "I" +
                    this.pidLeftController.getPIDController().getI()
                    +
                    "/" + "D" +
                    this.pidLeftController.getPIDController()
                            .getD());

            }

    // left front motor
    this.controlSpeedController(leftJoystickValue *
            this.leftJoystickIsReversed.get(), leftSpeedController,
            WhichJoystick.LEFT_JOYSTICK);

    // left rear motor
    this.controlSpeedController(leftJoystickValue *
            this.leftJoystickIsReversed.get(),
            leftRearSpeedController,
            WhichJoystick.LEFT_JOYSTICK);

    // right front motor
    this.controlSpeedController(rightJoystickValue *
            this.rightJoystickIsReversed.get(),
            this.oneOrRightSpeedController,
            WhichJoystick.RIGHT_JOYSTICK);

    // right rear motor
    this.controlSpeedController(rightJoystickValue *
            this.rightJoystickIsReversed.get(),
            rightRearSpeedController,
            WhichJoystick.RIGHT_JOYSTICK);
}

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of a speed controller based on the current
 * gear. This applies to any of the speed controllers
 * (either Jaguar or Victor) Note that it must be the
 * right joystick.
 *
 * @method controls
 * @param joystickInputValue
 *            - a float value which is used to set
 *            the motor speed to that value
 * @param rightSpeedController
 *            - the right or ONLY motor to
 *            control
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void controls (final double joystickInputValue,
        // the input value from the
        // joystick that controls this
        // speed controller
        final SpeedController rightSpeedController)
// if we only have one Motor
// to control then we consider
// it to be the right Motor
{
    // -------------------------------------
    // call the ControlSpeedController() with
    // the correct motor direction
    // -------------------------------------
    this.controlSpeedController(joystickInputValue *
            this.rightJoystickIsReversed.get(),
            rightSpeedController,
            WhichJoystick.ONE_JOYSTICK);
} // end controls

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of two speed controllers based on the current
 * gear. This is most often used with the left and right
 * drive motors. This applies to any of the speed controllers
 * (either Jaguar or Victor). That is the
 * joystick that can be reversed if it is necessary.
 *
 * @method Controls
 * @param leftJoystickInputValue
 *            - a float value which is used to set
 *            the left motor speed to that value
 * @param leftSpeedController
 *            - controls the left motor
 * @param rightJoystickInputValue
 *            - a float value which is used to set
 *            the right motor speed to that value
 * @param rightSpeedController
 *            - controls the right motor
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void controls (final double leftJoystickInputValue,
        // the input value from the left
        // joystick that controls the
        // left speed controller
        final SpeedController leftSpeedController,
        // the left Motor speed controller
        final double rightJoystickInputValue,
        // the input value from the right
        // joystick that controls the
        // right speed controller
        final SpeedController rightSpeedController)
// the right Motor speed controller
{
    // -------------------------------------
    // since we have two motors to control,
    // call the ControlSpeedController() with
    // the correct motor direction
    // -------------------------------------

    this.controlSpeedController(leftJoystickInputValue *
            this.leftJoystickIsReversed.get(), leftSpeedController,
            WhichJoystick.LEFT_JOYSTICK);
    this.controlSpeedController(rightJoystickInputValue *
            this.rightJoystickIsReversed.get(),
            rightSpeedController,
            WhichJoystick.RIGHT_JOYSTICK);
} // end Controls

// -------------------------------------------------------
/**
 * This function allows a transmission to control the
 * power of a speed controller based on the current
 * gear. This applies to any of the speed controllers
 * (either Jaguar or Victor). We multiply the motorDirection
 * by the actual speed to get the finished product
 *
 * @method controlSpeedController
 * @param joystickInputValue
 *            - the value that we will set this motor
 *            controller to
 * @param oneSpeedController
 *            - SpeedController - we are to control at this time
 * @param motorDirection
 *            - int - the value of either forward or reversed
 * @param whichJoystick
 *            - denotes whether or not this is a right motor
 *            a left motor or a single motor
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
private void controlSpeedController (
        final double joystickInputValue,
        // value to set this motor
        // controller to
        final SpeedController oneSpeedController,
        // control one Motor
        final int whichJoystick)
// character used to denote which joystick
// was input
{

    double controllerInput = 0.0;

    // --------------------------------------
    // if a legitimate joystick input value,
    // save it to be later modified by the
    // gear ratios
    // --------------------------------------
    if (this.isHardwareGearsOnlyOn() == true)
        {
        if ((joystickInputValue >= -1.0)
                && (joystickInputValue <= 1.0))
            {
            oneSpeedController.set(joystickInputValue);
            } // if
        } // if
    else
        {
        // --------------------------------------
        // Apply proper speed percentage constant
        // for the gear we are in.
        // --------------------------------------
        switch (this.getGear())
            {
            case 5:
                controllerInput = this.mapSoftwareJoystickValues(
                        joystickInputValue,
                        this.getFifthGearPercentage(
                                whichJoystick));
                oneSpeedController.set(controllerInput);
                break;

            case 4:
                controllerInput = this.mapSoftwareJoystickValues(
                        joystickInputValue,
                        this.getFourthGearPercentage(
                                whichJoystick));
                oneSpeedController.set(controllerInput);
                break;

            case 3:
                controllerInput = this.mapSoftwareJoystickValues(
                        joystickInputValue,
                        this.getThirdGearPercentage(
                                whichJoystick));
                oneSpeedController.set(controllerInput);
                break;

            case 2:
                controllerInput = this.mapSoftwareJoystickValues(
                        joystickInputValue,
                        this.getSecondGearPercentage(
                                whichJoystick));
                oneSpeedController.set(controllerInput);
                break;

            default:
                controllerInput = this.mapSoftwareJoystickValues(
                        joystickInputValue,
                        this.getFirstGearPercentage(
                                whichJoystick));
                oneSpeedController.set(controllerInput);
                break;

            } // switch
        }
    if ((this.getDebugState() == debugStateValues.DEBUG_ALL) ||
            ((this.getDebugState() == debugStateValues.DEBUG_ONLY_NON_ZERO)
                    &&
                    (Math.abs(
                            joystickInputValue) >= this.deadbandPercentageZone)))
        {
        char joystickSide = ' ';
        char controllerSide = ' ';
        if (whichJoystick == WhichJoystick.LEFT_JOYSTICK)
            {
            joystickSide = 'L';
            }
        if (whichJoystick == WhichJoystick.RIGHT_JOYSTICK)
            {
            joystickSide = 'R';
            }

        if (oneSpeedController.equals(this.leftSpeedController) ||
                oneSpeedController.equals(
                        this.oneOrRightSpeedController))
            {
            controllerSide = 'F';
            }
        if (oneSpeedController.equals(this.leftRearSpeedController)
                ||
                oneSpeedController.equals(
                        this.rightRearSpeedController))
            {
            controllerSide = 'R';
            }
        //TODO: remove errorMessage. Actually, burn ErrorMessages, and let it rise like a pheonix.
        Hardware.errorMessage.printError(
                joystickSide + controllerSide + " Joy = " +
                        joystickInputValue,
                PrintsTo.roboRIO,
                false);
        System.out
                .println(joystickSide + controllerSide + " Joy = " +
                        joystickInputValue);
        System.out.println(joystickSide + controllerSide +
                " Contrs = " +
                controllerInput);
        } // if

} // end controlSpeedController

/**
 * Disable all PID controllers.
 *
 * @author Noah Golmant
 * @written 12 Feb 2014
 */
public void disablePID ()
{
    this.pidLeftController.disable();
    this.pidLeftRearController.disable();
    this.pidRightController.disable();
    this.pidRightRearController.disable();

    this.integralUseCounter = 0;

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("[PID] Disabled PID.");
        }
}

// -------------------------------------------------------
/**
 * This function downshifts by a specified number of gears.
 *
 * @method downshift
 * @param gearsToShift
 *            - what gear do we want to go to
 * @return int - gear shifted into
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public int downshift (final int gearsToShift)
// The number of gears to
// shift down
{
    // --------------------------------------
    // Shift down the desired number of
    // gears.
    // --------------------------------------
    return (this.setGear(this.getGear() - gearsToShift));
} // end downshift

/**
 * Enable all PID controllers.
 * Resets our counter so that the total error won't be taken into
 * account for the first ~500 milliseconds
 *
 * @author Noah Golmant
 * @written 12 Feb 2014
 */
public void enablePID ()
{
    this.pidLeftController.enable();
    this.pidLeftRearController.enable();
    this.pidRightController.enable();
    this.pidRightRearController.enable();

    this.setPIDOutputRange(-this.oneOrRightFirstGearPercentage,
            this.oneOrRightFirstGearPercentage);
    this.setPIDInputRange(-this.minInputRange, this.maxInputRange);

    this.setPIDValues(this.pidProportionalGain,
            this.pidIntegralGain,
            this.pidDerivativeGain, this.pidFeedForward,
            this.pidTolerance);
    this.integralUseCounter = 0;

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("[PID] Enabled PID.");
        }
}

// -------------------------------------------------------
/**
 * gets the debug state of this class
 *
 * @method getDebugState
 * @return int - returns the debug state
 * @author Bob Brown
 * @written 9 February 2013
 *          -------------------------------------------------------
 */
public debugStateValues getDebugState ()
{
    return (this.debugState);
} // end getDebugState

/*
 * getting the value for the x deadband on the 3D joystick
 * 
 * @return double - the value
 */
public double getDirectionalXOrthagonalZone ()
{
    return (this.directionalXOrthagonalZone);
}

/*
 * getting the value for the y deadband on the 3D joystick
 * 
 * @return double - the value
 */
public double getDirectionalYOrthagonalZone ()
{
    return (this.directionalYOrthagonalZone);
}

// -------------------------------------------------------
/**
 * returns the present Fifth gear percentage for only
 * only joystick/motor pair or the right motor
 *
 * @method getFifthGearPercentage
 * @return double - the Fifth gear percentage
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double getFifthGearPercentage ()
{
    return (this.oneOrRightFifthGearPercentage);
} // end getFifthGearPercentage

/**
 * returns the present Fifth gear percentage for which
 * ever motor the caller request
 *
 * @method getFifthGearPercentage
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the Fifth gear percentage
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public double getFifthGearPercentage (int whichJoystick)
{
    if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.ONE_JOYSTICK))
        return (this.getFifthGearPercentage());
    else
        return (this.leftFifthGearPercentage);
} // end getFifthGearPercentage

// -------------------------------------------------------

/**
 * returns the present first gear percentage for only
 * only joystick/motor pair or the right motor
 *
 * @method getFirstGearPercentage
 * @return double - the first gear percentage
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double getFirstGearPercentage ()
{
    return (this.oneOrRightFirstGearPercentage);
} // end getFirstGearPercentage

// -------------------------------------------------------

/**
 * returns the present first gear percentage for which
 * ever motor the caller request
 *
 * @method getFirstGearPercentage
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the first gear percentage
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public double getFirstGearPercentage (int whichJoystick)
{
    if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.ONE_JOYSTICK))
        return (this.getFirstGearPercentage());
    return (this.leftFirstGearPercentage);
} // end getFirstGearPercentage

// -------------------------------------------------------

/**
 * returns the present Fourth gear percentage for only
 * only joystick/motor pair or the right motor
 *
 * @method getFourthGearPercentage
 * @return double - the Fourth gear percentage
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double getFourthGearPercentage ()
{
    return (this.oneOrRightFourthGearPercentage);
} // end getFourthGearPercentage

// -------------------------------------------------------

// -------------------------------------------------------
/**
 * returns the present Fourth gear percentage for which
 * ever motor the caller request
 *
 * @method getFourthGearPercentage
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the Fourth gear percentage
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public double getFourthGearPercentage (int whichJoystick)
{
    if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.ONE_JOYSTICK))
        return (this.getFourthGearPercentage());
    return (this.leftFourthGearPercentage);
} // end getFourthGearPercentage

// -------------------------------------------------------
/**
 * This function returns the current gear number.
 *
 * @method getGear
 * @return int - gear number
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public int getGear ()
{
    // --------------------------------------
    // Return the gear number.
    // --------------------------------------
    return (this.gear);
} // end getGear


public double getJoystickDeadbandRange ()
{
    return this.deadbandPercentageZone;
}

// ----------------------------------------
/**
 * Gets the left rear speed controller used in mecanum drive.
 *
 * @return left rear speed controller.
 *         -----------------------------------------
 */
public SpeedController getLeftRearSpeedController ()
{
    return (this.leftRearSpeedController);
}

// -------------------------------------------------------
/**
 * This function returns the left speed controller.
 *
 * @method getLeftSpeedController
 * @return left speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController getLeftSpeedController ()
{
    // --------------------------------------
    // Return the left Speed Controller.
    // --------------------------------------
    return (this.leftSpeedController);
} // end getLeftSpeedController

/**
 * Gets the maximum rate of our encoder in inches/second.
 * Determined by distance per pulse of the encoder.
 * Used to convert joystick input to a PID setpoint w/kRate.
 *
 * @return double - max rate of the encoder in in/second.
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public double getMaxEncoderRate ()
{
    return this.maxEncoderRate;
}

// -------------------------------------------------------
/**
 * returns to the caller the maxGear value
 *
 * @method getMaxGear
 * @return int - returns to the caller the maxGear value
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public int getMaxGear ()
{
    return (this.maxGears);
} // end getMaxGear

/**
 * returns the logical gear that we will switch the actual
 * physical transmission between upper and lower
 *
 * @method getPhysicalGearToChange
 * @return int - logical gear number where we will switch the
 *         actual physical transmission between upper and lower
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 * 
 */
public int getPhysicalGearToChange ()
{
    return (this.physicalGearToChange);
} // end getPhysicalGearToChange

// -------------------------------------------------------

/**
 * This function returns the primary speed controller.
 *
 * @method getPrimarySpeedController
 * @return primary speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController getPrimarySpeedController ()
{
    // --------------------------------------
    // Return the primary Speed Controller.
    // --------------------------------------
    return (this.oneOrRightSpeedController);
} // end getPrimarySpeedController

// -------------------------------------------------------
//TODO comment well.
public void resetAllEncoders ()
{
    if (this.rightRearMotorEncoder != null)
        this.rightRearMotorEncoder.reset();
    if (this.leftRearMotorEncoder != null)
        this.leftRearMotorEncoder.reset();
    if (this.oneOrRightMotorEncoder != null)
        this.oneOrRightMotorEncoder.reset();
    if (this.leftMotorEncoder != null)
        this.leftMotorEncoder.reset();
}

// TODO put in order
// TODO consider get encoder methods
/**
 * Returns the total distance on the right rear motor encoder.
 * 
 * @return double - the distance traveled from the Right Rear encoder
 * @author Alex Kneipp
 */
public double getRightRearEncoderDistance ()
{
    return this.rightRearMotorEncoder.getDistance();
}

/**
 * Returns the total distance on the right front motor encoder.
 * 
 * @return double - the distance traveled on the Right Front encoder
 * @author Alex Kneipp
 */
public double getRightFrontEncoderDistance ()
{
    return this.oneOrRightMotorEncoder.getDistance();
}

/**
 * Returns the total distance on the left rear motor encoder.
 * 
 * @return double - the distance traveled on the Left Rear Encoder
 * @author Alex Kneipp
 */
public double getLeftRearEncoderDistance ()
{
    return this.leftRearMotorEncoder.getDistance();
}

/**
 * Returns the total distance on the left front motor encoder.
 * 
 * @return double - the distance traveled on the Left Front encoder
 * @author Alex Kneipp
 */
public double getLeftFrontEncoderDistance ()
{
    return this.leftMotorEncoder.getDistance();
}

/**
 * Gets the right rear speed controller used in mecanum drive.
 *
 * @return right rear speed controller.
 *         -----------------------------------------
 */
public SpeedController getRightRearSpeedController ()
{
    return (this.rightRearSpeedController);
}

// ----------------------------------------

// -------------------------------------------------------
/**
 * This function returns the right speed controller.
 *
 * @method getRightSpeedController
 * @return right speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController getRightSpeedController ()
{
    // --------------------------------------
    // Return the right Speed Controller.
    // --------------------------------------
    return (this.oneOrRightSpeedController);
} // end getRightSpeedController

/**
 * returns the present Second gear percentage for only
 * only joystick/motor pair or the right motor
 *
 * @method getSecondGearPercentage
 * @return double - the Second gear percentage
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double getSecondGearPercentage ()
{
    return (this.oneOrRightSecondGearPercentage);
} // end getSecondGearPercentage

// -------------------------------------------------------

/**
 * returns the present Second gear percentage for which
 * ever motor the caller request
 *
 * @method getSecondGearPercentage
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the Second gear percentage
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public double getSecondGearPercentage (int whichJoystick)
{
    if ((whichJoystick == WhichJoystick.LEFT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.ONE_JOYSTICK))
        return (this.getSecondGearPercentage());
    return (this.leftSecondGearPercentage);
} // end getSecondGearPercentage

// -------------------------------------------------------

// -------------------------------------------------------
/**
 * returns the present Third gear percentage for only
 * only joystick/motor pair or the right motor
 *
 * @method getThirdGearPercentage
 * @return double - the Third gear percentage
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double getThirdGearPercentage ()
{
    return (this.oneOrRightThirdGearPercentage);
} // end getThirdGearPercentage

// -------------------------------------------------------
/**
 * returns the present Third gear percentage for which
 * ever motor the caller request
 *
 * @method getThirdGearPercentage
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the Third gear percentage
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public double getThirdGearPercentage (int whichJoystick)
{
    if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK) ||
            (whichJoystick == WhichJoystick.ONE_JOYSTICK))
        return (this.getThirdGearPercentage());
    else
        return (this.leftThirdGearPercentage);
} // end getThirdGearPercentage

// -------------------------------------------------------
/**
 * This function initializes all of the class local data
 * when a constructor is called
 *
 * @method init
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
private void init ()
{
    this.setGear(1);
} // end init

/**
 * Initializes our two encoders
 * Used if we didn't initialize them in the constructor.
 *
 * @param rightEncoder
 * @param leftEncoder
 */
public void initEncoders (Encoder rightEncoder, Encoder leftEncoder)
{
    this.leftMotorEncoder = leftEncoder;
    this.oneOrRightMotorEncoder = rightEncoder;
}

/**
 * Initializes our four encoders for PID use.
 * Used if we didn't initialize them in the constructor.
 *
 * @param leftFrontEncoder
 * @param leftRearEncoder
 * @param rightFrontEncoder
 * @param rightRearEncoder
 */
public void initEncoders (Encoder rightFrontEncoder,
        Encoder rightRearEncoder, Encoder leftFrontEncoder,
        Encoder leftRearEncoder)
{
    this.leftMotorEncoder = leftFrontEncoder;
    this.leftRearMotorEncoder = leftRearEncoder;
    this.oneOrRightMotorEncoder = rightFrontEncoder;
    this.rightRearMotorEncoder = rightRearEncoder;
}

/**
 * If the PID controllers have not been initialized yet,
 * this initializes them with our constant values.
 *
 * If they have already been initialized, update the controllers with
 * new constant values.
 *
 * @author Noah Golmant
 * @written 7 Feb 2014
 */
public void initPIDControllers ()
{
    // Check if encoders have been initialized. If not, return.
    if ((this.leftMotorEncoder == null) ||
            (this.leftRearMotorEncoder == null) ||
            (this.oneOrRightMotorEncoder == null) ||
            (this.rightRearMotorEncoder == null))
        return;

    // Left front PID controller
    if (this.pidLeftController == null)
        {
        this.pidLeftController = new PIDVelocityController(
                this.leftSpeedController,
                this.leftMotorEncoder, this.pidProportionalGain,
                this.pidIntegralGain, this.pidDerivativeGain,
                this.pidFeedForward);
        }

    // Left rear PID controller
    if (this.pidLeftRearController == null)
        {
        this.pidLeftRearController = new PIDVelocityController(
                this.leftRearSpeedController,
                this.leftRearMotorEncoder,
                this.pidProportionalGain,
                this.pidIntegralGain, this.pidDerivativeGain,
                this.pidFeedForward);
        }

    // Right front PID controller
    if (this.pidRightController == null)
        {
        this.pidRightController = new PIDVelocityController(
                this.oneOrRightSpeedController,
                this.oneOrRightMotorEncoder,
                this.pidProportionalGain,
                this.pidIntegralGain, this.pidDerivativeGain,
                this.pidFeedForward);
        }

    // Right rear PID controller
    if (this.pidRightRearController == null)
        {
        this.pidRightRearController = new PIDVelocityController(
                this.rightRearSpeedController,
                this.rightRearMotorEncoder,
                this.pidProportionalGain,
                this.pidIntegralGain, this.pidDerivativeGain,
                this.pidFeedForward);
        }

    // set input and output ranges
    this.setPIDInputRange(this.minInputRange, this.maxInputRange);

    // PID output range
    this.setPIDOutputRange(this.minOutputRange,
            this.maxOutputRange);

    // set error tolerance for the controllers
    this.pidLeftController.setPercentTolerance(this.pidTolerance);
    this.pidLeftRearController
            .setPercentTolerance(this.pidTolerance);
    this.pidRightController.setPercentTolerance(this.pidTolerance);
    this.pidRightRearController
            .setPercentTolerance(this.pidTolerance);

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("[PID] Initialized PID.");
        }
}

// -------------------------------------------------------
/**
 * This function denotes whether or not we are using only
 * the hardware gears and not using the software manipulation
 * of the gearing strength.
 *
 * @method isHardwareGearsOnlyOn
 * @return boolean - is hardware gears only on
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public boolean isHardwareGearsOnlyOn ()
{
    return (this.useHardwareGearsOnly);
} // end isHardwareGearsOnlyOn

// ------------------------------------------------
/**
 * @return boolean - whether or not we are using mecanum
 * @author Noah Golmant
 * @written Feb 6, 2014
 *          ------------------------------------------------
 */
public boolean isMecanumDrive ()
{
    return this.isMecanum;
} // end isMecanumDrive()

// -----------------------------------------------------------------
/**
 * @return boolean - whether or not we are currently using all PID
 *         controllers.
 *         Does not refer to whether PID is used on the robot in general.
 * @author Noah Golmant
 * @written Feb 8, 2014
 *          ----------------------------------------------------------------
 *          -
 */
public boolean isPIDEnabled ()
{
    return (this.pidLeftController.getPIDController().isEnable() &&
            this.pidRightController.getPIDController().isEnable() &&
            this.pidLeftRearController.getPIDController().isEnable()
            &&
            this.pidRightRearController.getPIDController()
                    .isEnable());
} // end isPIDEnabled()

// -------------------------------------------------------
/**
 * This function returns whether or not the left joystick responds
 * correctly in their inputs. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to check that
 * the left joystick is reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method isLeftJoystickReversed
 * @return boolean - Left Joystick is presently reversed or not
 * @author Bob Brown
 * @written 13 February 2016
 *          -------------------------------------------------------
 */
public boolean isLeftJoystickReversed ()
{
    if (this.leftJoystickIsReversed
            .get() == JoystickDirection.REVERSED)
        return (true);
    return (false);
} // end isLeftJoystickReversed()

// -------------------------------------------------------
/**
 * This function returns whether or not the right joystick responds
 * correctly in their inputs. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to check that
 * the left joystick is reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method isRightJoystickReversed
 * @return boolean - Right Joystick is presently reversed or not
 * @author Bob Brown
 * @written 13 February 2016
 *          -------------------------------------------------------
 */
public boolean isRightJoystickReversed ()
{
    if (this.rightJoystickIsReversed
            .get() == JoystickDirection.REVERSED)
        return (true);
    return (false);
} // end isRightJoystickReversed()

// -------------------------------------------------------
/**
 * This function returns whether or not both joysticks are set to be
 * reversed. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to check that
 * the both joysticks are reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method isRightJoystickReversed
 * @return boolean - Right Joystick is presently reversed or not
 * @author Bob Brown
 * @written 13 February 2016
 *          -------------------------------------------------------
 */
public boolean joystickAreReversed ()
{
    if (this.rightJoystickIsReversed
            .get() == JoystickDirection.REVERSED
            && this.leftJoystickIsReversed
                    .get() == JoystickDirection.REVERSED)
        return (true);
    return (false);
} // end isRightJoystickReversed()

/**
 * A mini function to limit motor values to a range of -1.0..1.0
 *
 * @param input
 *            number to limit
 * @return double - number between -1.0 and 1.0
 */
private double limit (double input)
{
    if (input > 1.0)
        return 1.0;
    else if (input < -1.0)
        return -1.0;
    return input;
} // end limit()

// -------------------------------------------------------
/**
 * this function maps the joystick input value against
 * the range that the motors can handle taking into
 * account the dead-band declared for the joysticks
 *
 * @method mapSoftwareJoystickValues
 * @param joystickInputValue
 *            - the present value of the joystick
 * @param gearPercentage
 *            - percentage of the power that this gear
 *            will perform at
 * @return double - computed mapped value
 * @author Bob Brown
 * @written Feb 21, 2010
 *          -------------------------------------------------------
 */
private double mapSoftwareJoystickValues (
        final double joystickInputValue,
        // value to map against
        // the allowable range
        final double gearPercentage)
// gear percentage for this
// motor and gearing
{
    // --------------------------------------
    // temp computational value for joysticks
    // to make the math easier. Absolutize the
    // value and remove the deadband range
    // --------------------------------------
    final double absJoystickInputValue = Math.max(
            (Math.abs(joystickInputValue) -
                    this.deadbandPercentageZone),
            0.0);
    // --------------------------------------
    // compute the range of the joysticks. This
    // excludes the deadband range
    // --------------------------------------
    final double deadbandRange = 1.0 - this.deadbandPercentageZone;
    // --------------------------------------
    // computed mapped value
    // --------------------------------------
    final double mappedValue =
            absJoystickInputValue * (gearPercentage /
                    deadbandRange);
    // --------------------------------------
    // return the correct value - if the original
    // input was negative, return negative, else positive
    // --------------------------------------
    if (joystickInputValue < 0)
        return (-mappedValue);
    return (mappedValue);
} // end mapSoftwareJoystickValues

/**
 * Reset all the PID controllers.
 *
 * @author Noah Golmant
 * @written 12 Feb 2014
 */
public void resetPID ()
{
    this.pidLeftController.reset();
    this.pidLeftRearController.reset();
    this.pidRightController.reset();
    this.pidRightRearController.reset();

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("[PID] Reset PID.");
        }
} // end resetPID()

// -------------------------------------------------------
/**
 * turn off all gear lights
 *
 * @method setAllGearLightsOff
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */

/*
 * private
 * void setAllGearLightsOff ()
 * {
 * //-------------------------------------
 * // loop for lights that have previously been declared
 * // first make sure that we have declared a light
 * // which is not required. then loop for all declared
 * // and turn them off
 * //-------------------------------------
 * if (this.digitalChannelNumberForGearLight.isEmpty() == false)
 * for (int lightIterator = 0; lightIterator <
 * this.digitalChannelNumberForGearLight.size();
 * lightIterator++)
 * DriverStation.getInstance().setDigitalOut(lightIterator, false);
 * } // end setAllGearLightsOff
 */
// -------------------------------------------------------
/**
 * sets the debug state of this class so that it can
 * prints out debug information if requested
 *
 * @method setDebugState
 * @param debugState
 *            - the debug state that the user is
 *            requesting
 * @return int - returns the newly changed debug state
 * @author Bob Brown
 * @written 9 February 2013
 *          -------------------------------------------------------
 */
public debugStateValues
        setDebugState (final debugStateValues debugState)
{
    switch (debugState)
        {
        case DEBUG_NONE:
        case DEBUG_ALL:
        case DEBUG_ONLY_NON_ZERO:
        case DEBUG_ONLY_PID_DATA:
            this.debugState = debugState;
            break;
        } // switch

    return (this.getDebugState());
} // end debugStateValues()

/*
 * Sets the Directional X Orthagonal. This value determines the tolerance,
 * off
 * of the absolute Y axis, in which diagonal movement begins. (e.g. make
 * the driver push the joy more than xPercentage degrees off the Y axis
 * before axis only movement ends)
 * 
 * @param the percentage
 * 
 * @author Alex and Rachael
 * 
 * @written 7 Feb 2014
 */
public void setDirectionalXOrthagonalZone (double xPercentage)
{
    this.directionalXOrthagonalZone = xPercentage;
} // end setDirectionalXOrthagonalZone()

/*
 * Sets the Directional Y Orthagonal. This value determines the tolerance,
 * off
 * of the absolute X axis, in which diagonal movement begins. (e.g. make
 * the driver push the joy more than xPercentage degrees off the X axis
 * before axis only movement ends)
 * 
 * @param the percentage
 * 
 * @author Alex and Rachael
 * 
 * @written 7 Feb 2014
 */
public void setDirectionalYOrthagonalZone (double yPercentage)
{
    this.directionalYOrthagonalZone = yPercentage;
}

/**
 * This function sets the distance per pulse of each encoder in the
 * transmission instance. WIth this, we can call the encoder's
 * getDistance() method to get the distance it's gone in inches.
 *
 * @param distancePerTick
 *            distance in inches we travel per tick of
 *            the encoder.
 * @author Noah Golmant
 * @written 6 November 2014
 */
public void setEncodersDistancePerPulse (double distancePerTick)
{
    if (this.oneOrRightMotorEncoder != null)
        {
        this.oneOrRightMotorEncoder.setDistancePerPulse(
                distancePerTick);
        }
    if (this.rightRearMotorEncoder != null)
        {
        this.rightRearMotorEncoder
                .setDistancePerPulse(distancePerTick);
        }
    if (this.leftMotorEncoder != null)
        {
        this.leftMotorEncoder.setDistancePerPulse(distancePerTick);
        }
    if (this.leftRearMotorEncoder != null)
        {
        this.leftRearMotorEncoder
                .setDistancePerPulse(distancePerTick);
        }
}

// ----------
// ENCODERS
// ----------

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * Fifth gear for either only one motor configuration or
 * for the right motor only
 *
 * @method setFifthGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Fifth gear to be set to
 * @return double - the percentage that Fifth gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setFifthGearPercentage (final double gearPercentage)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        this.oneOrRightFifthGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightFifthGearPercentage = gearPercentage;
        }
    return (this.getFifthGearPercentage());
} // end setFifthGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for both
 * motors (left and right)
 *
 * @method setFifthGearPercentage
 * @param leftGearPercentage
 *            - the percentage you would like
 *            Fifth gear left motor to be set to
 * @param rightGearPercentage
 *            - the percentage you would like
 *            Fifth gear right motor to be set to
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public void setFifthGearPercentage (final double leftGearPercentage,
        final int rightGearPercentage)
{
    if ((leftGearPercentage > 1.00) || (leftGearPercentage < 0.0))
        {
        this.leftFifthGearPercentage = 1.00;
        }
    else
        {
        this.leftFifthGearPercentage = leftGearPercentage;
        }
    if ((rightGearPercentage > 1.00) || (rightGearPercentage < 0.0))
        {
        this.oneOrRightFifthGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightFifthGearPercentage = rightGearPercentage;
        }
} // end setFifthGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * first gear for either only one motor configuration or
 * for the right motor only
 *
 * @method setFirstGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            first gear to be set to
 * @return double - the percentage that first gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setFirstGearPercentage (final double gearPercentage)
{
    this.setFirstGearPercentage(gearPercentage, gearPercentage);
    return (this.getFirstGearPercentage());
} // end setFirstGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for both
 * motors (left and right)
 *
 * @method setFirstGearPercentage
 * @param leftGearPercentage
 *            - the percentage you would like
 *            first gear left motor to be set to
 * @param rightGearPercentage
 *            - the percentage you would like
 *            first gear right motor to be set to
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public void setFirstGearPercentage (final double leftGearPercentage,
        final double rightGearPercentage)
{
    if ((leftGearPercentage > 1.00) || (leftGearPercentage < 0.0))
        {
        this.leftFirstGearPercentage = 1.00;
        }
    else
        {
        this.leftFirstGearPercentage = leftGearPercentage;
        }
    if ((rightGearPercentage > 1.00) || (rightGearPercentage < 0.0))
        {
        this.oneOrRightFirstGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightFirstGearPercentage = rightGearPercentage;
        }
} // end setFirstGearPercentage

/**
 * sets the percentage of the joysticks range for the
 * Fourth gear for either only one motor configuration or
 * for the right motor only
 *
 * @method setFourthGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Fourth gear to be set to
 * @return double - the percentage that Fourth gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setFourthGearPercentage (final double gearPercentage)
{
    this.setFourthGearPercentage(gearPercentage, gearPercentage);
    return (this.getFourthGearPercentage());
} // end setFourthGearPercentage

// -------------------------------------------------------

/**
 * sets the percentage of the joysticks range for both
 * motors (left and right)
 *
 * @method setFourthGearPercentage
 * @param leftGearPercentage
 *            - the percentage you would like
 *            Fourth gear left motor to be set to
 * @param rightGearPercentage
 *            - the percentage you would like
 *            Fourth gear right motor to be set to
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public void setFourthGearPercentage (
        final double leftGearPercentage,
        final double rightGearPercentage)
{
    if ((leftGearPercentage > 1.00) || (leftGearPercentage < 0.0))
        {
        this.leftFourthGearPercentage = 1.00;
        }
    else
        {
        this.leftFourthGearPercentage = leftGearPercentage;
        }
    if ((rightGearPercentage > 1.00) || (rightGearPercentage < 0.0))
        {
        this.oneOrRightFourthGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightFourthGearPercentage = rightGearPercentage;
        }
} // end setFourthGearPercentage

// -------------------------------------------------------

/**
 * This function sets a new gear number. It also controls
 * the hardware piston when moving between the desired
 * hardware change gears. Goes from one to maxGears.
 *
 * @method setGear
 * @param gear
 *            - gear number you would like to set the transmission to
 *            - Starts at one, not zero.
 * @return int - newly adjusted gear number
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public int setGear (final int gear)
{
    // --------------------------------------
    // Check bounds of the gear number.
    // --------------------------------------
    if (gear > this.maxGears)
        {
        this.setGear(this.maxGears);
        }
    else if (gear <= 0)
        {
        this.setGear(1);
        }
    else
        {
        // --------------------------------------
        // only do the following if we have a hardware
        // transmission controlled by a solenoid.
        // This is denoted by whether or
        // not we have a double solenoid to control
        // --------------------------------------
        if (this.transmissionSolenoids != null)
            {
            // --------------------------------------
            // If moving beyond physical gear change, move
            // hardware piston out.
            // --------------------------------------
            if (gear > this.getPhysicalGearToChange())
                {
                this.transmissionSolenoids.set(
                        DoubleSolenoid.Value.kForward);
                }
            else if (gear <= this.getPhysicalGearToChange())
                {
                this.transmissionSolenoids.set(
                        DoubleSolenoid.Value.kReverse);
                }
            } // if
             // -------------------------------------
             // if we didn't have a double solenoid we
             // might a relay instead. check to see
             // if this is populated
             // -------------------------------------
        else if (this.transmissionRelay != null)
            // --------------------------------------
            // If moving from first gear, move
            // hardware piston out.
            // --------------------------------------
            if (gear > this.getPhysicalGearToChange())
            {
            this.transmissionRelay.set(Relay.Value.kForward);
            }
            else if (gear <= this.getPhysicalGearToChange())
            {
            this.transmissionRelay.set(Relay.Value.kReverse);
            }
        // --------------------------------------
        // Store the new gear number. and turn the
        // correct light ON if we have an associated
        // light to turn on
        // --------------------------------------
        this.gear = gear;
        /**
         * ------------------------------------
         * enable the user chosen light to denote the
         * gear that is ON.
         * --------------------------------------
         */
        // this.setLightOn(this.getGear());
        } // else

    // ----------------------------------
    // Return the new gear number.
    // ----------------------------------
    return (this.getGear());
} // end setGear

// -------------------------------------------------------

// -------------------------------------------------------
/**
 * This function sets whether or not we are using only
 * the hardware gears and not using the software manipulation
 * of the gearing strength.
 *
 * @method setHardwareGearsOnlyOn
 * @param on
 *            - sets the status of hardware only
 * @return boolean - the status of the hardware gears only
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public boolean setHardwareGearsOnlyOn (final boolean on)
{
    return (this.useHardwareGearsOnly = on);
} // end setHardwareGearsOnlyOn

/**
 * Sets whether or not we are in mecanum drive
 * vs a 4 wheel drive.
 *
 * @param isMecanum
 *            whether or not we are mecanum
 * @author Noah Golmant
 * @written 6 Feb 2014
 */
public void setIsMecanum (boolean isMecanum)
{
    this.isMecanum = isMecanum;
}

// -------------------------------------------------------
/**
 * set the dead-band range for the joystick(s)
 *
 * @method setAllGearLightsOff
 * @param percentage
 *            the percentage of the range that we want to make
 *            into a dead-band The range is 0.0 - 1.0
 * @return boolean - denotes whether or not the dead-band range was set
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public boolean setJoystickDeadbandRange (double percentage)
{
    if ((percentage >= 0) && (percentage <= 1.0))
        {
        this.deadbandPercentageZone = percentage;
        return (true);
        } // end if
    return (false);
} // setJoystickDeadbandRange

// -------------------------------------------------------
/**
 * This function sets whether or not the joysticks respond
 * correctly in their inputs. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to set that
 * the both joysticks are reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method setJoysticksAreReversed
 * @param reversed
 *            - denotes reversed or not for both joysticks
 * @author Bob Brown
 * @written 21 January 2011
 *          -------------------------------------------------------
 */
public void setJoysticksAreReversed (boolean reversed)
{
    this.setLeftJoystickIsReversed(reversed);
    this.setRightJoystickIsReversed(reversed);
} // end setJoysticksAreReversed

// -------------------------------------------------------
/**
 * This function sets whether or not the joysticks respond
 * correctly in their inputs. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to set that
 * the left joystick is reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method setLeftJoystickIsReversed
 * @param reversed
 *            - denotes reversed or not
 * @return boolean - Joystick is now reversed or not
 * @author Bob Brown
 * @written 21 January 2011
 *          -------------------------------------------------------
 */
public boolean setLeftJoystickIsReversed (boolean reversed)
{
    if (reversed == true)
        {
        this.leftJoystickIsReversed.set(JoystickDirection.REVERSED);
        }
    else
        {
        this.leftJoystickIsReversed.set(JoystickDirection.NORMAL);
        }
    return (reversed);
} // end setLeftJoystickIsReversed

// -------------------------------------------------
/**
 * Sets the left rear speed controller to use in mecanum drive.
 *
 * @param controller
 *            controller to use.
 *            -------------------------------------------------
 */
public SpeedController setLeftRearSpeedController (
        SpeedController controller)
{
    return (this.leftRearSpeedController = controller);
}

// ---------------------
// PID Range variables
// ---------------------

// -------------------------------------------------------
/**
 * This function sets and returns the left speed controller.
 *
 * @method getLeftSpeedController
 * @param leftSpeedController
 *            - pass in the speed controller object
 * @return newly set left speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController setLeftSpeedController (
        SpeedController leftSpeedController)
{
    // --------------------------------------
    // Return the newly left Speed Controller.
    // --------------------------------------
    return (this.leftSpeedController = leftSpeedController);
} // end getLeftSpeedController

// -------------------------------------------------------
/**
 * Turns the gear lights on
 *
 * @method setLightOn
 * @param gearNumber
 *            - int - which gear do we want to turn the
 *            light on
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */

/*
 * private
 * void setLightOn (final int gearNumber)
 * {
 * //-------------------------------------
 * // if we have previously declared an association
 * // with any lights - first clear all lights
 * //-------------------------------------
 * if (this.digitalChannelNumberForGearLight.isEmpty() == false)
 * {
 * this.setAllGearLightsOff();
 * 
 * //-------------------------------------
 * // if we have specified that we want a light
 * // ON for this gear and it is a legit gear
 * // number, then set it ON
 * //-------------------------------------
 * if ((gearNumber < (this.digitalChannelNumberForGearLight.size() - 1)) &&
 * (((Integer)
 * (this.digitalChannelNumberForGearLight.elementAt(gearNumber))).intValue()
 * >=
 * 1) &&
 * (((Integer)
 * (this.digitalChannelNumberForGearLight.elementAt(gearNumber))).intValue()
 * <=
 * 8))
 * Hardware.driverStation.setDigitalOut(
 * ((Integer)
 * (this.digitalChannelNumberForGearLight.elementAt(gearNumber))).intValue()
 * ,
 * true);
 * } // if
 * } // end setLightOn
 */
/**
 * Sets the max rate of the encoders for PID.
 * This is used to convert a motor/joystick value [-1.0..1.0]
 * to an encoder rate to use as a PID setpoint.
 *
 * @param encRate
 *            maximum rate of the encoder in inches/second
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public void setMaxEncoderRate (double encRate)
{
    this.maxEncoderRate = encRate;
}

// -------------------------------------------------------
/**
 * This function sets the maximum gear number
 *
 * @method setMaxGear
 * @param maxGear
 *            - maximum gear to use on the transmission
 * @return int - returns the gear number that was set
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public int setMaxGear (final int maxGear)
{
    if (maxGear >= MAX_GEARS_ALLOWED)
        {
        this.maxGears = MAX_GEARS_ALLOWED;
        }
    else if (maxGear <= 0)
        {
        this.maxGears = 1;
        }
    else
        {
        this.maxGears = maxGear;
        }
    return (this.getMaxGear());
} // end setMaxGear

// -------------------------------------------------------
/**
 * This function associates lights with each of the possible
 * gears that the transmission can have. The Digital Outputs
 * on the drivers Station are associated with each of the
 * gears. If a Digital output is set, then when we go into
 * that gear, we turn on it's light.
 *
 * @method setNotificationLights
 * @param firstGearDigitalLight
 *            - int - light to denote that we are in
 *            first gear
 * @param secondGearDigitalLight
 *            - int - light to denote that we are in
 *            second gear
 * @param thirdGearDigitalLight
 *            - int - light to denote that we are in
 *            third gear
 * @param fourthGearDigitalLight
 *            - int - light to denote that we are in
 *            fourth gear
 * @param fifthGearDigitalLight
 *            - int - light to denote that we are in
 *            fifth gear
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public void setNotificationLights (final int firstGearDigitalLight,
        // Digital light on the drivers
        // station that will light when
        // gear one is chosen
        final int secondGearDigitalLight,
        // Digital light on the drivers
        // station that will light when
        // gear two is chosen
        final int thirdGearDigitalLight,
        // Digital light on the drivers
        // station that will light when
        // gear three is chosen
        final int fourthGearDigitalLight,
        // Digital light on the drivers
        // station that will light when
        // gear four is chosen
        final int fifthGearDigitalLight)
// Digital light on the drivers
// station that will light when
// gear five is chosen
{
    // -------------------------------------
    // make sure that we clear everything first
    // to make sure that we didn't call this function
    // several times and we would then add this info
    // to the end of the list instead of replacing
    // it
    // -------------------------------------
    this.digitalChannelNumberForGearLight.removeAllElements();
    // -------------------------------------
    // if the light number passed in is good
    // then save it and check the others
    // -------------------------------------
    if ((firstGearDigitalLight >= 1)
            && (firstGearDigitalLight <= 8))
        {
        this.digitalChannelNumberForGearLight
                .addElement(new Integer(
                        firstGearDigitalLight));
        // -------------------------------------
        // check to make sure that we have at least
        // 2 gears and that the light number passed
        // in is good, then save it and check for
        // others
        // -------------------------------------
        if ((this.maxGears >= 2) && (secondGearDigitalLight >= 1) &&
                (secondGearDigitalLight <= 8))
            {
            this.digitalChannelNumberForGearLight.addElement(
                    new Integer(
                            secondGearDigitalLight));
            // -------------------------------------
            // check to make sure that we have at least
            // 3 gears and that the light number passed
            // in is good, then save it and check for
            // others
            // -------------------------------------
            if ((this.maxGears >= 3) && (thirdGearDigitalLight >= 1)
                    &&
                    (thirdGearDigitalLight <= 8))
                {
                this.digitalChannelNumberForGearLight.addElement(
                        new Integer(thirdGearDigitalLight));
                // -------------------------------------
                // check to make sure that we have at least
                // 4 gears and that the light number passed
                // in is good, then save it and check for
                // others
                // -------------------------------------
                if ((this.maxGears >= 4) &&
                        (fourthGearDigitalLight >= 1) &&
                        (fourthGearDigitalLight <= 8))
                    {
                    this.digitalChannelNumberForGearLight
                            .addElement(
                                    new Integer(
                                            fourthGearDigitalLight));
                    // -------------------------------------
                    // we have at least 5 gears so check
                    // that the light number passed
                    // in is good. For max number of gears
                    // coded see MAX_GEARS_ALLOWED.
                    // -------------------------------------
                    if ((fifthGearDigitalLight >= 1) &&
                            (fifthGearDigitalLight <= 8))
                        {
                        this.digitalChannelNumberForGearLight
                                .addElement(
                                        new Integer(
                                                fifthGearDigitalLight));
                        }
                    } // if - fourth
                } // if - third
            } // if - second
        } // if
} // end setNotificationLights

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * Fifth gear for the left motor only
 *
 * @method setFifthGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Fifth gear to be set to
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the percentage that Fifth gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setOneFifthGearPercentage (
        final double gearPercentage,
        final int whichJoystick)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
                (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
            {
            this.oneOrRightFifthGearPercentage = 1.00;
            }
        else
            {
            this.leftFifthGearPercentage = 1.00;
            }
        } // if
    else if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
        {
        this.oneOrRightFifthGearPercentage = gearPercentage;
        }
    else
        {
        this.leftFifthGearPercentage = gearPercentage;
        }
    return (this.getFifthGearPercentage(whichJoystick));
} // end setFifthGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * first gear for the left motor only
 *
 * @method setFirstGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            first gear to be set to
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the percentage that first gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setOneFirstGearPercentage (
        final double gearPercentage,
        final int whichJoystick)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
                (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
            {
            this.oneOrRightFirstGearPercentage = 1.00;
            }
        else
            {
            this.leftFirstGearPercentage = 1.00;
            }

        } // if
    else if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
        {
        this.oneOrRightFirstGearPercentage = gearPercentage;
        }
    else
        {
        this.leftFirstGearPercentage = gearPercentage;
        }
    return (this.getFirstGearPercentage(whichJoystick));
} // end setOneFirstGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * Fourth gear for the left motor only
 *
 * @method setFourthGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Fourth gear to be set to
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the percentage that Fourth gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setOneFourthGearPercentage (
        final double gearPercentage,
        final int whichJoystick)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
                (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
            {
            this.oneOrRightFourthGearPercentage = 1.00;
            }
        else
            {
            this.leftFourthGearPercentage = 1.00;
            }
        } // if
    else if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
        {
        this.oneOrRightFourthGearPercentage = gearPercentage;
        }
    else
        {
        this.leftFourthGearPercentage = gearPercentage;
        }
    return (this.getFourthGearPercentage(whichJoystick));
} // end setFourthGearPercentage

/**
 * sets the percentage of the joysticks range for the
 * Second gear for the left motor only
 *
 * @method setSecondGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Second gear to be set to
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the percentage that Second gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setOneSecondGearPercentage (
        final double gearPercentage,
        final int whichJoystick)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
                (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
            {
            this.oneOrRightSecondGearPercentage = 1.00;
            }
        else
            {
            this.leftSecondGearPercentage = 1.00;
            }
        } // if
    else if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
        {
        this.oneOrRightSecondGearPercentage = gearPercentage;
        }
    else
        {
        this.leftSecondGearPercentage = gearPercentage;
        }
    return (this.getSecondGearPercentage(whichJoystick));
} // end setOneSecondGearPercentage

// -------------------------------------------------------

/**
 * sets the percentage of the joysticks range for the
 * Third gear for the left motor only
 *
 * @method setThirdGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Third gear to be set to
 * @param whichJoystick
 *            - which joystick controls this motor
 *            either there is only one joystick of there are two
 *            joysticks and this is either the right or left
 *            joystick
 * @return double - the percentage that Third gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setOneThirdGearPercentage (
        final double gearPercentage,
        final int whichJoystick)
{
    if ((gearPercentage > 1.00) || (gearPercentage < 0.0))
        {
        if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
                (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
            {
            this.oneOrRightThirdGearPercentage = 1.00;
            }
        else
            {
            this.leftThirdGearPercentage = 1.00;
            }
        } // if
    else if ((whichJoystick == WhichJoystick.ONE_JOYSTICK) ||
            (whichJoystick == WhichJoystick.RIGHT_JOYSTICK))
        {
        this.oneOrRightThirdGearPercentage = gearPercentage;
        }
    else
        {
        this.leftThirdGearPercentage = gearPercentage;
        }
    return (this.getThirdGearPercentage(whichJoystick));
} // end setOneThirdGearPercentage

// -------------------------------------------------------

/**
 * sets and returns the logical gear that we will switch the actual
 * physical transmission between upper and lower
 *
 * @method setPhysicalGearToChange
 * @param physicalGearToChange
 *            - the gear where we will change the
 *            transmission from lower to upper gears
 * @return int - logical gear number where we will switch the
 *         actual physical transmission between upper and lower
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public int setPhysicalGearToChange (int physicalGearToChange)
{
    if ((physicalGearToChange < 0) ||
            (physicalGearToChange > this.maxGears))
        {
        this.physicalGearToChange = 1;
        }
    else
        {
        this.physicalGearToChange = physicalGearToChange;
        }
    return (this.getPhysicalGearToChange());
} // end setPhysicalGearToChange

// -------------------------------------------------------

/**
 * Sets the input range of all PID controllers
 *
 * @param min
 *            min input value we send from the encoders
 * @param max
 *            max input value we send from the encoders
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public void setPIDInputRange (double min, double max)
{

    // Only set the values if we can actually access
    // all of our PID controllers
    if ((this.pidLeftController == null) ||
            (this.pidLeftRearController == null) ||
            (this.pidRightController == null) ||
            (this.pidRightRearController == null))
        return;

    this.minInputRange = min;
    this.maxInputRange = max;

    this.pidLeftController.setInputRange(min, max);
    this.pidLeftRearController.setInputRange(min, max);
    this.pidRightController.setInputRange(min, max);
    this.pidRightRearController.setInputRange(min, max);
}

/**
 * Set output range of all PID controllers
 *
 * @param min
 *            min output we can send to the motors
 * @param max
 *            max output we can send to the motors
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public void setPIDOutputRange (double min, double max)
{
    // Only set the values if we can actually access
    // all of our PID controllers
    if ((this.pidLeftController == null) ||
            (this.pidLeftRearController == null) ||
            (this.pidRightController == null) ||
            (this.pidRightRearController == null))
        return;

    this.minOutputRange = min;
    this.maxOutputRange = max;

    this.pidLeftController.setOutputRange(min, max);
    this.pidLeftRearController.setOutputRange(min, max);
    this.pidRightController.setOutputRange(min, max);
    this.pidRightRearController.setOutputRange(min, max);
}

/**
 * Sets whether to ever enable the PID controllers while driving.
 * Used outside of transmission class.
 * For permanent use of PID, not the measurement based on whether or not
 * we are stopped and need to reset the encoders
 *
 * TO USE PID:
 *
 * - set the PID output of the respective motor controllers/encoders
 * - either the encoder *rate* or the *distance*
 *
 * - set the min/max for the PID input and output
 * - min/max for input based on encoder ranges
 * - min/max for output is based on motor output ranges
 *
 * - tune the PID parameters
 * - P until the robot oscillates around the target
 * - D until it stops about it
 * - I if necessary
 *
 * - SET THE PID SETPOINT FROM OUTSIDE OF THE CONTROLS METHOD
 *
 * @param enabled
 *            if PID will be enabled.
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public void setPIDUse (boolean enabled)
{
    this.usePID = enabled;
}

/**
 * Sets the tuning variables for our PID loop.
 *
 * @param proportional
 *            current error coefficient
 * @param integral
 *            total error coefficient
 * @param derivative
 *            change in error coefficient
 * @param feedForward
 *            previous motor value sent
 * @param tolerance
 *            percent tolerance of error allowed
 * @author Noah Golmant
 * @written 7 Feb 2014
 */
public void setPIDValues (double proportional, double integral,
        double derivative, double feedForward, double tolerance)
{
    this.pidProportionalGain = proportional;
    this.pidIntegralGain = integral;
    this.pidDerivativeGain = derivative;
    this.pidFeedForward = feedForward;
    this.pidTolerance = tolerance;

    if ((this.pidLeftController == null) ||
            (this.pidLeftRearController == null) ||
            (this.pidRightController == null) ||
            (this.pidRightRearController == null))
        return;

    // update the controllers with the new values
    this.pidLeftController.getPIDController().setPID(proportional,
            integral, derivative, feedForward);
    this.pidLeftRearController.getPIDController().setPID(
            proportional,
            integral, derivative, feedForward);
    this.pidRightController.getPIDController().setPID(proportional,
            integral, derivative, feedForward);
    this.pidRightRearController.getPIDController().setPID(
            proportional,
            integral, derivative, feedForward);

    this.pidLeftController.setPercentTolerance(tolerance);
    this.pidLeftRearController.setPercentTolerance(tolerance);
    this.pidRightController.setPercentTolerance(tolerance);
    this.pidRightRearController.setPercentTolerance(tolerance);

    if ((this
            .getDebugState() == debugStateValues.DEBUG_ONLY_PID_DATA)
            ||
            (this.getDebugState() == debugStateValues.DEBUG_ALL))
        {
        System.out.println("[PID] Set PID values.\n" + "P: " +
                proportional + "\nI: " + integral + "\nD: " +
                derivative +
                "\nF: " + feedForward);
        System.out.println("[PID] Set PID tolerance: " + tolerance);
        }

}

// -------------------------------------------------------
/**
 * This function sets and returns the primary speed controller.
 *
 * @method setPrimarySpeedController
 * @param primarySpeedController
 *            - pass in the speed controller object
 * @return newly set primary speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController setPrimarySpeedController (
        SpeedController primarySpeedController)
{
    // --------------------------------------
    // Return the newly set Speed Controller.
    // --------------------------------------
    return (this.oneOrRightSpeedController =
            primarySpeedController);
} // end setPrimarySpeedController

// -------------------------------------------------------
/**
 * This function sets whether or not the joysticks respond
 * correctly in their inputs. WPI wants the joystick
 * to go from 1.0, which is full reverse, to -1.0, which
 * is full forward. This method allows us to set that
 * the right joystick is reversed or not and if reversed,
 * goes from -1.0, denoting full reverse, to 1.0 denoting
 * full forward
 *
 * @method setRightJoystickIsReversed
 * @param reversed
 *            - denotes reversed or not
 * @return boolean - Joystick is now reversed or not
 * @author Bob Brown
 * @written 21 January 2011
 *          -------------------------------------------------------
 */
public boolean setRightJoystickIsReversed (boolean reversed)
{
    if (reversed == true)
        {
        this.rightJoystickIsReversed
                .set(JoystickDirection.REVERSED);
        }
    else
        {
        this.rightJoystickIsReversed.set(JoystickDirection.NORMAL);
        }
    return (reversed);
} // end setRightJoystickIsReversed

// -------------------------------------------------
/**
 * Sets the right rear speed controller to use in mecanum drive.
 *
 * @param controller
 *            controller to use.
 *            -------------------------------------------------
 */
public SpeedController setRightRearSpeedController (
        SpeedController controller)
{
    return (this.rightRearSpeedController = controller);
}

// -------------------------------------------------------
/**
 * This function returns the right speed controller.
 *
 * @method setRightSpeedController
 * @param rightSpeedController
 *            - pass in the speed controller object
 * @return newly set right speed controller
 * @author Bob Brown
 * @written 04 February 2011
 *          -------------------------------------------------------
 */
public SpeedController setRightSpeedController (
        SpeedController rightSpeedController)
{
    // --------------------------------------
    // Return the newly set right Speed Controller.
    // --------------------------------------
    return (this.oneOrRightSpeedController = rightSpeedController);
} // end setRightSpeedController

public void setRotationCorrection (double rotationCorrection)
{
    this.mecanumRotationCorrection = this.limit(rotationCorrection);
}

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * Second gear for either only one motor configuration or
 * for the right motor only
 *
 * @method setSecondGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Second gear to be set to
 * @return double - the percentage that Second gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setSecondGearPercentage (final double gearPercentage)
{
    this.setSecondGearPercentage(gearPercentage, gearPercentage);
    return (this.getSecondGearPercentage());
} // end setSecondGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for both
 * motors (left and right)
 *
 * @method setSecondGearPercentage
 * @param leftGearPercentage
 *            - the percentage you would like
 *            Second gear left motor to be set to
 * @param rightGearPercentage
 *            - the percentage you would like
 *            Second gear right motor to be set to
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public void setSecondGearPercentage (
        final double leftGearPercentage,
        final double rightGearPercentage)
{
    if ((leftGearPercentage > 1.00) || (leftGearPercentage < 0.0))
        {
        this.leftSecondGearPercentage = 1.00;
        }
    else
        {
        this.leftSecondGearPercentage = leftGearPercentage;
        }
    if ((rightGearPercentage > 1.00) || (rightGearPercentage < 0.0))
        {
        this.oneOrRightSecondGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightSecondGearPercentage = rightGearPercentage;
        }
} // end setSecondGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for the
 * Third gear for either only one motor configuration or
 * for the right motor only
 *
 * @method setThirdGearPercentage
 * @param gearPercentage
 *            - the percentage you would like
 *            Third gear to be set to
 * @return double - the percentage that Third gear was set to
 * @author Bob Brown
 * @written 17 January 2010
 *          -------------------------------------------------------
 */
public double setThirdGearPercentage (final double gearPercentage)
{
    this.setThirdGearPercentage(gearPercentage, gearPercentage);
    return (this.getThirdGearPercentage());
} // end setThirdGearPercentage

// -------------------------------------------------------
/**
 * sets the percentage of the joysticks range for both
 * motors (left and right)
 *
 * @method setThirdGearPercentage
 * @param leftGearPercentage
 *            the percentage you would like
 *            Third gear left motor to be set to
 * @param rightGearPercentage
 *            the percentage you would like
 *            Third gear right motor to be set to
 * @author Bob Brown
 * @written 15 February 2013
 *          -------------------------------------------------------
 */
public void setThirdGearPercentage (final double leftGearPercentage,
        final double rightGearPercentage)
{
    if ((leftGearPercentage > 1.00) || (leftGearPercentage < 0.0))
        {
        this.leftThirdGearPercentage = 1.00;
        }
    else
        {
        this.leftThirdGearPercentage = leftGearPercentage;
        }
    if ((rightGearPercentage > 1.00) || (rightGearPercentage < 0.0))
        {
        this.oneOrRightThirdGearPercentage = 1.00;
        }
    else
        {
        this.oneOrRightThirdGearPercentage = rightGearPercentage;
        }
} // end setThirdGearPercentage

// -------------------------------------------------------
/**
 * This function upshifts by a specified number of gears.
 *
 * @method upshift
 * @param gearsToShift
 *            - number of gears to go up
 * @return int - gear we are now in
 * @author Bob Brown
 * @written Sep 20, 2009
 *          -------------------------------------------------------
 */
public int upshift (final int gearsToShift)
{
    // --------------------------------------
    // Upshift the desired number of gears
    // and return the new gears number.
    // --------------------------------------
    return (this.setGear(this.getGear() + gearsToShift));
} // end upshift

/**
 * Returns whether or not we are planning to use PID.
 *
 * @return boolean - if PID can be enabled by enablePID().
 * @author Noah Golmant
 * @written 16 Feb 2014
 */
public boolean usePID ()
{
    return this.usePID;
}

// end class
}
