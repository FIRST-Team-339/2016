package org.usfirst.frc.team339.HardwareInterfaces.transmission;

import org.usfirst.frc.team339.HardwareInterfaces.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;

/** This is base transmission class that is used to drive the robot.
 * It represents the drive train subsystem/assembly on the robot.
 *
 * All other transmission classes/methods derive from this class.
 * It sets up important variables and has a basic drive method for
 * a tank drive.
 *
 * @author Noah Golmant
 * @date 2 July 201 */
public class Transmission extends Subsystem
{
    /** This stores whether or not we want to spit out debug data to the console
     * or whatever error processing we're doing.
     *
     * @author Noah Golmant
     * @written 2 July 201 */
    public static enum DebugState
{
        DEBUG_NONE, DEBUG_ONLY_NON_ZERO, DEBUG_PID_DATA, DEBUG_MOTOR_DATA,
        DEBUG_ALL
        }

    /** This stores the direction of a given motor and allows us
     * to more easily set all the motors to a value at once.
     *
     * @author Noah Golmant
     * @written 2 July 201 */
    public static enum MotorDirection
        {
        FORWARD(1.0), REVERSED(-1.0);

        // Default to forward position
        double val = 1;

        MotorDirection (double i)
            {
            this.val = i;
            }

        public double value ()
            {
            return this.val;
            }
        }

    /** If we're using a joystick, we don't want to use the first +/- 0.1
     * because it's too close to zero. So if we read a value around 0.1 from
     * the joystick, we probably don't want to actually send values. */
    private final double deadbandPercentageZone = 0.1;

    /** If we want to print out extra debug info from Transmission, we can
     * set what kind of info we need, i.e. motor, PID, non-zero data, etc. */
    private DebugState debugState = DebugState.DEBUG_NONE;

/** Whether or not the joysticks are reversed, in which case we send
     * the opposite value that we originally intended */
    private boolean leftJoystickReversed = false;
    private boolean rightJoystickReversed = false;

    /** -------------------------------------------------------
     *
     * @description keeps track of which gear we are presently in.
     *              it refers to an index of the gearPercentages array
     *              (plus one, so that the value at position zero refers
     *              to the first gear value)
     * @author Bob Brown
     * @written Sep 19, 2009
     * @modified by Noah Golmant, 2 July 201
     *           Changed to reference an index of a gear percentage array
     *           ------------------------------------------------------- */
    private int gear = 3;

    /** @description the max number of the physical gear if we are using one.
     *              after this number, any gear shift will be in the software.
     *
     * @author Noah Golmant
     * @written 16 July 201 */
    private final int MAX_PHYSICAL_GEAR = 2;

    /** @description the physical solenoids that control the physical gears
     *              of the transmission.
     *
     * @author Noah Golmant
     * @written 16 July 201 */
    private DoubleSolenoid transmissionSolenoids = null;

    /** -------------------------------------------------------
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
     *              to 0 (percent).
     *
     *              Keep in mind that a hardware transmission is in effect
     *              between first and second gears. Therefore, it is not
     *              unusual to have a higher percentage constant for first
     *              gear than second gear.
     * @author Bob Brown
     * @written 17 January 2010
     *
     * @modified by Noah Golmant, 2 July 201
     *           Changed to an array of percentages
     *           to be modified with the array index(+1)
     *           as an argument, specifying the gear
     *           we want to change.
     *           ------------------------------------------------------- */
    private final double[] gearPercentages =
    { 0.0, 0.70, 0.80, 0.90, 1.00 };

    private final int MAX_GEAR = this.gearPercentages.length;

    /** -----------------------------------------------------------
     *
     * @description Create the motors and the object to store whether
     *              or not they are reversed. For the base class, we start with
     *              two
     *              controllers; one per side. This would work with a robot that
     *              has two
     *              motors per side, each pair wired with one motor controller
     *              on the robot.
     *
     *              If this class is extended to a 4-wheel drive robot,
     *              these two refer to the front motors on the robot.
     *              The rear motors will be referred to explicitly as
     *              <direction>RearSpeedController.
     *
     *
     *
     * @author Noah Golmant
     * @written 2 July 201
     *          ------------------------------------------------------------ */
    private SpeedController rightSpeedController = null;
    private MotorDirection rightMotorDirection = MotorDirection.FORWARD;
    private SpeedController leftSpeedController = null;
    private MotorDirection leftMotorDirection = MotorDirection.FORWARD;

    /** Initialize a new transmission with two base speed controllers.
     * No encoders are set up with this constructor.
     *
     * @param rightSpeedController
     *            Right (front) speed controller
     * @param leftSpeedController
     *            Left (front) speed controller
     *
     * @author Noah Golmant
     * @written 2 July 201 */
    public Transmission (SpeedController rightSpeedController,
        SpeedController leftSpeedController)
        {
        this.rightSpeedController = rightSpeedController;
        this.leftSpeedController = leftSpeedController;
        }

    /** Initialize a new transmission with two base speed controllers.
     * No encoders are set up with this constructor.
     * This includes a physical transmission for gear control.
     *
     * @param rightSpeedController
     *            Right (front) speed controller
     * @param leftSpeedController
     *            Left (front) speed controller
     *
     * @param transmissionSolenoids
     *            physical transmission for gear control
     *
     * @author Noah Golmant
     * @written 2 July 201 */
    public Transmission (SpeedController rightSpeedController,
        SpeedController leftSpeedController,
        DoubleSolenoid transmissionSolenoids)
        {
        this.transmissionSolenoids = transmissionSolenoids;
        this.rightSpeedController = rightSpeedController;
        this.leftSpeedController = leftSpeedController;
        }

    /** Move the transmission gear down one.
     *
     * @see setGear()
     *
     * @author Noah Golmant
     * @written 16 July 201 */
    public void downshift ()
        {
        this.setGear(this.gear - 1);
        }

    /** Drives the transmission in a tank drive .
     * rightJoystickVal controls both right motors, and vice versa for the left.
     * It scales it according to our deadband and the current gear, then
     * makes sure we're not out of our allowed motor value ranges.
     *
     * @param rightJoystickVal
     *            joystick input for the right motor(s)
     * @param leftJoystickVal
     *            joystick input for the left motor(s)
     *
     * @author Noah Golmant
     * @written 9 July 201 */
    public void drive (double rightJoystickVal, double leftJoystickVal)
        {

        // Get the scaled versions of our joystick values
        double scaledRightVal = this.scaleJoystickValue(rightJoystickVal);
        double scaledLeftVal = this.scaleJoystickValue(leftJoystickVal);

        // Make sure they fit within our allowed motor ranges (just in case)
        // make them a max/min of +1.0/-1.0 to send to the motor
        scaledRightVal = this.limit(scaledRightVal);
        scaledLeftVal = this.limit(scaledLeftVal);

        // check if either joystick is reversed
        if (this.isLeftJoystickReversed() == true)
            {
        scaledRightVal *= -1.0;
        }
        if (this.isRightJoystickReversed() == true)
            {
        scaledLeftVal *= -1.0;
        }

        if ((this.getDebugState() == DebugState.DEBUG_MOTOR_DATA) ||
            (this.getDebugState() == DebugState.DEBUG_ALL))
            {
            System.out.println("drive():\tRF: " + scaledRightVal + "\tLF: " +
                scaledLeftVal);
            }

        // send the scaled values to the motors
        this.driveRightMotor(scaledRightVal);
        this.driveLeftMotor(scaledLeftVal);
        }

    /** Sets the left motor to the given value based on
     * its given direction.
     *
     * @param motorValue
     *            The motor value we want to send
     *
     * @author Noah Golmant
     * @date 9 July 201 */
    protected void driveLeftMotor (double motorValue)
        {

        if (this.leftSpeedController == null)
            {
            if ((this.getDebugState() == DebugState.DEBUG_MOTOR_DATA) ||
                (this.getDebugState() == DebugState.DEBUG_ALL))
                {
                System.out
                    .println("Left (front) motor is null in driveLeftMotor()");
                }

            return;
            }

        motorValue = this.limit(motorValue);
        this.leftSpeedController.set(motorValue * this.leftMotorDirection.val);
        }

    /** Sets the right motor to the given value based on
     * its given direction.
     *
     * @param motorValue
     *            The motor value we want to send
     *
     * @author Noah Golmant
     * @date 9 July 201 */
    protected void driveRightMotor (double motorValue)
        {

        if (this.rightSpeedController == null)
            {
            if ((this.getDebugState() == DebugState.DEBUG_MOTOR_DATA) ||
                (this.getDebugState() == DebugState.DEBUG_ALL))
                {
                System.out
                    .println("Right (front) motor is null in driveRightMotor()");
                }

            return;
            }

        motorValue = this.limit(motorValue);
        this.rightSpeedController
            .set(motorValue * this.rightMotorDirection.val);
        }

    public double getCurrentGearPercentage ()
        {
        return this.getGearPercentage(this.getGear());
        }

    /** Gets the current deadband of the joystick; if a joystick motor val is
     * inside
     * this range (e.g. -.1 to +.1) we just send 0 to the motor.
     *
     * @return */
    public double getDeadbandPercentageZone ()
        {
        return this.deadbandPercentageZone;
        }

    /** Gets the current debug state (what data we want to print out)
     *
     * @return Current debug state as a member of the DebugState enum
     *
     * @author Noah Golmant
     * @written 9 July 201 */
    public DebugState getDebugState ()
        {
        return this.debugState;
        }

    /** Gets the current gear of the transmission.
     *
     * @return the current gear */
    public int getGear ()
        {
        return this.gear;
        }

    /** Gets the maximum motor value of the given gear.
     * The gear should be within the range of gears we have (1 to )
     *
     * @param gear
     *            The gear we want to get the maximum value of
     * @return The maximum value for the given percentage
     *
     * @author Noah Golmant
     * @written 9 July 201 */
    public double getGearPercentage (int gear)
        {
        if ((gear < 1) || (gear > this.MAX_GEAR))
            {
            if (this.debugState == DebugState.DEBUG_MOTOR_DATA)
                {
                System.out
                    .println("Invalid gear to set in getGearPercentage()");
                }
            return 0.0;
            }

        return this.gearPercentages[gear - 1];
        }

    /** Gets whether or not the left motor is reversed
     *
     * @return the direction of the left (front) motor */
    public MotorDirection getLeftMotorDirection ()
        {
        return this.leftMotorDirection;
        }

    /** Gets whether or not the right motor is reversed
     *
     * @return the direction of the right (front) motor */
    public MotorDirection getRightMotorDirection ()
        {
        return this.rightMotorDirection;
        }

@Override
    public void initDefaultCommand ()
        {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
        }

/** Gets whether or not the left joystick is reversed
     *
 * @return true if the left joystick is reversed */
    public boolean isLeftJoystickReversed ()
    {
    return this.leftJoystickReversed;
    }

    /** Gets whether or not the right joystick is reversed
     *
 * @return true if the right joystick is reversed */
    public boolean isRightJoystickReversed ()
    {
    return this.rightJoystickReversed;
    }

    /** Limits any motor value between -1.0 and 1.0
     *
     * @param val
     *            Motor value to limit
     * @return limited motor value
     *
     * @author Noah
     * @written 9 July 201 */
    protected double limit (double val)
        {
        if (val > 1.0)
            {
            val = 1.0;
            }
        if (val < -1.0)
            {
            val = -1.0;
            }

        return val;
        }

    /** Scales the input joystick value based on the actual range of values
     * we accept from the joystick. i.e. if we have a deadband that prevents
     * values between -0.1 and +0.1, then it will scale the value so that
     * an input of .1 to 1.0 will provide the same range as 0 to 1.
     *
     * @param joystickValue
     *            original, unscaled input joystick value
     * @return a new input for motors, scaled based on the deadband range
     *         and the current maximum gear percentage.
     *
     * @author Noah Golmant
     * @written 9 July 201 */
    public double scaleJoystickValue (double joystickValue)
        {
        final double maxCurrentGearValue = this.getCurrentGearPercentage();

        // ---------------------------------------------------------------
        // This gets the difference between our
        // absolutized input value and the deadband zone.
        // If it's lower than zero, this means that it's too small
        // to do anything with. Otherwise, we have a good value to scale.
        // ---------------------------------------------------------------
        final double absJoystickInputValue =
            java.lang.Math.max(
                (Math.abs(joystickValue) - this.deadbandPercentageZone), 0.0);

        // -----------------------------------------
        // The range of values we will actually use
        // -----------------------------------------
        final double deadbandRange = 1.0 - this.deadbandPercentageZone;

        // -------------------------------------------------
        // Compute the final scaled joystick value.
        // Scales it by the ratio of the max gear value to
        // the deadband range.
        // -------------------------------------------------
        final double scaledJoystickInput =
            absJoystickInputValue * (maxCurrentGearValue / deadbandRange);

        // -------------------------------------------------------
        // Since we previously absolutized it, return a negative
        // motor value if the original input was negative.
        // -------------------------------------------------------
        if (joystickValue < 0)
            return -scaledJoystickInput;

        return scaledJoystickInput;
        }

/** Sets the debug state of transmission. This determines what info
     * we want to print out. See the DebugState enum.
     *
     * @param newState
     *            The new debug state we want to use.
     *
     * @author Noah Golmant
     * @written 9 July 201 */
    public void setDebugState (DebugState newState)
        {
        this.debugState = newState;
        }

/** Set the current gear of the transmission. If we have a
     * physical transmission and we set it within that gear range,
     * we adjust the solenoid accordingly.
     *
     * @param gear
     *            new gear to set the transmission to
     *
     * @author Noah Golmant
     * @written 16 July 201 */
    public void setGear (int gear)
        {
        if ((gear < 1) || (gear > this.MAX_GEAR))
            {
            if ((this.getDebugState() == DebugState.DEBUG_MOTOR_DATA) ||
                (this.getDebugState() == DebugState.DEBUG_ALL))
                {
                System.out.println("Failed to set gear " + gear +
                    "in setGear()");
                }
            return;
            }

        this.gear = gear;

        // check for a physical transmission
        if (this.transmissionSolenoids != null)
            {
            switch (gear)
                {
                case 1:
                    this.transmissionSolenoids.set(Value.kForward);
                    break;
                case 2:
                    this.transmissionSolenoids.set(Value.kReverse);
                    break;
                default:
                    this.transmissionSolenoids.set(Value.kOff);
                    break;
                }

            }
        }

    /** @description Sets the gear in the gears array to a new value.
     *              If the gear is out of the range, we don't do anything.
     *              We limit the values between -1 and +1.
     *
     * @param gear
     *            The gear we want to set (1=first, 2=second ... =fifth)
     * @param value
     *            The maximum motor value of the given gear.
     *
     * @written Noah Golmant
     * @date 9 July 201 */
    public void setGearPercentage (int gear, double value)
        {
        // Make sure we're not outside the actual range of gears we have
        if ((gear < 1) || (gear > this.MAX_GEAR))
            {
            if (this.debugState == DebugState.DEBUG_MOTOR_DATA)
                {
                System.out.println("Failed to set gear " + gear +
                    "in setGearPercentage()");
                }
            return;
            }

        // Check max/min gear percentage values we will allow
        if (value > 1.0)
            {
            value = 1.0;
            }
        else if (value < this.deadbandPercentageZone)
            {
            value = this.deadbandPercentageZone;
            }

        // Set the new gear percentage in our gear array
        this.gearPercentages[gear - 1] = value;
        }

    /** Sets whether or not the left joystick is reversed
     *
 * @param isReversed
 *            true if the joystick is reversed
 *
     * @author Noah Golmant
     * @written 30 July 2015 */
    public void setLeftJoystickReversed (boolean isReversed)
    {
    this.leftJoystickReversed = isReversed;
    }

    /** Sets whether or not the left (front) motor is reversed
     *
     * @param direction
     *            new direction of the left (front) motor */
    public void setLeftMotorDirection (MotorDirection direction)
        {
        this.leftMotorDirection = direction;
        }

    /** Sets whether or not the left joystick is reversed
     *
 * @param isReversed
 *            true if the joystick is reversed
 *
     * @author Noah Golmant
     * @written 30 July 2015 */
    public void setRightJoystickReversed (boolean isReversed)
    {
    this.rightJoystickReversed = isReversed;
    }

    /** Sets whether or not the right (front) motor is reversed
     *
     * @param direction
     *            new direction of the right (front) motor */
    public void setRightMotorDirection (MotorDirection direction)
        {
        this.rightMotorDirection = direction;
        }

    /** Move the transmission gear up one.
     *
     * @see setGear()
     *
     * @author Noah Golmant
     * @written 16 July 201 */
    public void upshift ()
        {
        this.setGear(this.gear + 1);
        }

}
