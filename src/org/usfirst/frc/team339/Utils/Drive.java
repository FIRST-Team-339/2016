
package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;
import org.usfirst.frc.team339.Utils.ManipulatorArm.ArmPosition;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.image.NIVisionException;

public class Drive
{
/**
 * Constructor for a Drive object. Should only be called once.
 * 
 * @param transmission
 *            Transmission object the class uses to control the motors.
 * @author Alex Kneipp
 */
public Drive (Transmission_old transmission)
{
    this.transmission = transmission;
}

public Drive (Transmission_old transmission, KilroyCamera camera)
{
    this(transmission);
    this.camera = camera;
}

public Drive (Transmission_old transmission, KilroyCamera camera,
        Relay ringLightRelay)
{
    this(transmission, camera);
    this.ringLightRelay = ringLightRelay;
}

/**
 * Stops the robot actively. Basically just a wrapper for the transmission's
 * brake. Uses the default brakeSpeed in this class
 * 
 * @return true if we're done braking, false otherwise.
 * @author Alex Kneipp
 */
public boolean brake ()
{
    // TODO maybe make argument a constant in the class.
    // TODO find out ideal brakespeed.
    return (this.brake(this.brakeSpeed));
} // end brake()

/**
 * Stops the robot actively. Basically just a wrapper for the transmission's
 * brake.
 * 
 * @param toBrakeSpeed
 *            The speed with which to brake, recommended to be rather low.
 * @return true if we're done braking, false otherwise.
 * @author Alex Kneipp
 */
public boolean brake (final double toBrakeSpeed)
{
    return (this.brake(toBrakeSpeed, toBrakeSpeed));
} // end brake()

/**
 * Stops the robot actively. Basically just a wrapper for the transmission's
 * brake.
 * 
 * @param lBrakeSpeed
 *            The speed with which to brake for the left motors,
 *            recommended to be rather low.
 * @param rBrakeSpeed
 *            The speed with which to brake for the right motors,
 *            recommended to be rather low.
 * @return true if we're done braking, false otherwise.
 * @author Alex Kneipp
 */
public boolean brake (final double lBrakeSpeed,
        final double rBrakeSpeed)
{
    return (this.transmission.brake(lBrakeSpeed, rBrakeSpeed));
} // end brake()

/**
 * determines whether or not we need to compensate for the fact that
 * the Left Joystick is reversed. If it is, return the joystick input
 * value as negated. Otherwise, return exactly what we got sent in
 * 
 * @method compensateForReversedLeftJoystick
 * @author Robert Brown
 * @date 16 February 2016
 * @param joystickInput
 *            - joystick value as sent in from the caller
 * @return - double - modified joystick input value
 */
private double
        compensateForReversedLeftJoystick (
                final double joystickInput)
{
    if (this.transmission.isLeftJoystickReversed() == true)
        return (-joystickInput);
    return joystickInput;
} // end compensateForReversedLeftJoystick

/**
 * determines whether or not we need to compensate for the fact that
 * the Right Joystick is reversed. If it is, return the joystick input
 * value as negated. Otherwise, return exactly what we got sent in
 * 
 * @method compensateForReversedRightJoystick
 * @author Robert Brown
 * @date 16 February 2016
 * @param joystickInput
 *            - joystick value as sent in from the caller
 * @return - double - modified joystick input value
 */
private double
        compensateForReversedRightJoystick (
                final double joystickInput)
{
    if (this.transmission.isRightJoystickReversed() == true)
        return (-joystickInput);
    return joystickInput;
} // end compensateForReversedRightJoystick

/**
 * Determines the correct Joystick value. We get the value of the
 * joystick as input. We need to correct it because this side of the
 * robot has gone more than the other side, so we multiple it by
 * the correct factor that we have preset/stored. Then we verify
 * that we have a minimum speed. First we determine whether or not
 * the joysticks are reversed. If they are, the now corrected speed is
 * compared to the minimum speed and the maximum is used. If the
 * joysticks are not reversed, then we take the minimum speed of
 * either the negative of the minimum motor speed or the now
 * corrected speed.
 * 
 * @method - determineCorrectedJoystickValue
 * @param rawJoystickValue
 *            - contains the joystick value as we received it
 *            from the joystick before it is reduced and made sure it is
 *            meeting a minimum speed
 * @param drivingForward
 *            - we are driving forward (true or false)
 * @return - the corrected joystick value after it is reduced and made
 *         sure it is meeting a minimum speed
 * @author Robert Brown
 * @date 13 February 2016
 */
private double
        determineCorrectedJoystickValue (
                final double rawJoystickValue,
                boolean drivingForward)
{
    // ------------------------------------
    // Since we are correcting the speed on this particular
    // joystick (it means that this side of the robot is
    // ahead of the other side), then we need to multiple
    // the speed by a correction factor and then make
    // sure that the resulting value is more than the
    // minimum speed that the motors need to actually
    // move. that we get from the getMinMotorSpeed()
    // that the user can actually specify. Also, take
    // into account the if the joystick is reversed
    // (as denoted in the transmission class), then
    // we use the minimum speed and not the negative
    // minimum speed.
    // -------------------------------------
    if (drivingForward == true)
        {
        if (this.transmission.isLeftJoystickReversed() == true)
            return (Math.max(this.getMinMotorSpeed(),
                    rawJoystickValue *
                            this.getDrivingCorrectionFactor()));
        return (Math.min(-this.getMinMotorSpeed(),
                rawJoystickValue *
                        this.getDrivingCorrectionFactor()));
        }
    if (this.transmission.isLeftJoystickReversed() == true)
        return (Math.min(-this.getMinMotorSpeed(),
                rawJoystickValue *
                        this.getDrivingCorrectionFactor()));
    return (Math.max(this.getMinMotorSpeed(),
            rawJoystickValue * this.getDrivingCorrectionFactor()));
} // end determineCorrectJoystickValue

/**
 * Drives forever (almost). (calls driveByInches(9999, false,
 * defaultMaxSpeed (1.0), defaultMaxSpeed (1.0))) with NO correction
 * to keep you straight
 * 
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveContinuous ()
{
    this.driveByInches(9999.0, false,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed());
} // end driveContinuous()

/**
 * Drives forever (almost). (calls driveByInches(9999, false,
 * leftJoystickInputValue, rightJoystickInputValue)) with NO correction
 * to keep you straight
 * 
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveContinuous (
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)

{
    this.driveByInches(9999.0, false, leftJoystickInputValue,
            rightJoystickInputValue);
} // end driveContinuous()

/**
 * Drives a specified distance (inches) with no correction. (calls
 * driveByInches(distance, true, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0))) with no correction to keep straight
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveByInches (final double distance)
{
    return (this.driveByInches(distance, true,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveByInches()

/**
 * Drives a specified distance (inches) with no correction. (calls
 * driveByInches(distance, brakeAtEnd, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0))) with no correction to keep straight
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param brakeAtEnd
 *            - true - brake when finished false - don't brake
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveByInches (final double distance,
        final boolean brakeAtEnd)
{
    return (this.driveByInches(distance, brakeAtEnd,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveByInches()

/**
 * Drives a specified distance (inches) with no correction. (calls
 * driveByInches(distance, true, leftJoystickInputValue,
 * rightJoystickInputValue)) with no correction to keep straight
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveByInches (final double distance,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.driveByInches(distance, true,
            leftJoystickInputValue, rightJoystickInputValue));
} // end driveByInches()

/**
 * Drives a specified distance (inches) with no correction.
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param brakeAtEnd
 *            - true - brake when finished false - don't brake
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveByInches (final double distance,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{

    if (this.debugOn == true)
        {
        // PRINT STATEMENTS:
        // TODO: remove
        // Prints out encoder values and the values we are sending to the
        // motors.
        System.out.println("Left Distance: " +
                Hardware.leftRearEncoder.getDistance());
        System.out.println("Right Distance: " +
                Hardware.rightRearEncoder.getDistance());

        if (this.transmission.getRightRearEncoderDistance() == this.transmission.getLeftRearEncoderDistance())
            {
            System.out.println(
                    "Left Joystick: " + leftJoystickInputValue);
            System.out.println("Right Joystick: " +
                    rightJoystickInputValue);
            }
        else if ((this.transmission.getRightRearEncoderDistance()) < (this.transmission.getLeftRearEncoderDistance()))
            {
            System.out.println("Left Joystick: " +
                    leftJoystickInputValue);
            System.out.println("Right Joystick: " +
                    rightJoystickInputValue);
            }
        else
            {
            System.out.println(
                    "Left Joystick: " + leftJoystickInputValue);
            System.out.println(
                    "Right Joystick: " + rightJoystickInputValue);
            }
        } // if debug = on
         // -----------------------------------
         // stop if the average value of either drive train
         // is greater than the desired distance traveled.
         //------------------------------------
    if (this.hasDrivenInches(distance) == true)
        {
        // if requested to brake, stop
        if (brakeAtEnd == true)
            {
            return (this.brake());
            }
        // -----------------------------------
        // otherwise we are not braking, but we
        // are now finished driving
        // -----------------------------------
        return true;
        }
    // drive as per the joystick inputs
    this.transmission.controls(leftJoystickInputValue,
            rightJoystickInputValue);

    // ------------------------------------
    // still have more driving to do
    // ------------------------------------
    return false;
} // end driveByInches()

/**
 * Drives straight forever (almost). (calls driveStraightByInches(9999,
 * false,
 * defaultMaxSpeed (1.0), defaultMaxSpeed (1.0)))
 * 
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveStraightContinuous ()
{
    this.driveStraightByInches(9999.0, false,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed());
} // end driveStraightContinuous()

/**
 * Drives straight forever (almost). (calls driveStraightByInches(9999,
 * false,
 * leftJoystickInputValue, rightJoystickInputValue))
 * 
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveStraightContinuous (
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)

{
    this.driveStraightByInches(9999.0, false,
            leftJoystickInputValue,
            rightJoystickInputValue);
} // end driveStraightContinuous()

/**
 * Drives straight distance inches with correction. (calls
 * driveStraightByInches(distance, true, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0)))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveStraightByInches (final double distance)
{
    return (this.driveStraightByInches(distance, true,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveStraightByInches()

/**
 * Drives straight distance inches with correction. (calls
 * driveStraightByInches(distance, brakeAtEnd, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0)))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param brakeAtEnd
 *            - true - brake when finished false - don't brake
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveStraightByInches (final double distance,
        final boolean brakeAtEnd)
{
    return (this.driveStraightByInches(distance, brakeAtEnd,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveStraightByInches()

/**
 * Drives straight distance inches with correction. (calls
 * driveStraightByInches(distance, true, leftJoystickInputValue,
 * rightJoystickInputValue))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveStraightByInches (final double distance,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.driveStraightByInches(distance, true,
            leftJoystickInputValue, rightJoystickInputValue));
} // end driveStraightByInches()

/**
 * Drives straight distance inches with correction.
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param brakeAtEnd
 *            - true - brake when finished false - don't brake
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
// TODO make correction progressive. i.e. the further off we get, the more
// we correct. Use encoder differences for delta value
public boolean driveStraightByInches (final double distance,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    // if we are presently going straight - keep the
    // speeds equal
    double leftJoystickValue = leftJoystickInputValue;
    double rightJoystickValue = rightJoystickInputValue;

    if (this.transmission.getRightRearEncoderDistance() == this.transmission.getLeftRearEncoderDistance())
        return (this.driveByInches(distance, brakeAtEnd,
                leftJoystickInputValue, rightJoystickInputValue));
    // if the left drive train is ahead of the right drive train
    else if ((this.transmission.getRightRearEncoderDistance()) < (this.transmission.getLeftRearEncoderDistance()))
        {
        if (this.transmission.getRightRearEncoderDistance() > 0)
            leftJoystickValue = determineCorrectedJoystickValue(
                    leftJoystickInputValue, true);
        else
            rightJoystickValue = determineCorrectedJoystickValue(
                    rightJoystickInputValue, false);
        return (this.driveByInches(distance, brakeAtEnd,
                leftJoystickValue,
                rightJoystickValue));
        }
    else
        {
        if (this.transmission.getRightRearEncoderDistance() > 0)
            rightJoystickValue = determineCorrectedJoystickValue(
                    rightJoystickInputValue, true);
        else
            leftJoystickValue = determineCorrectedJoystickValue(
                    leftJoystickInputValue, false);
        return (this.driveByInches(distance, brakeAtEnd,
                leftJoystickValue,
                rightJoystickValue));
        }

} // end driveStraightByInches()

/**
 * returns the state of the debug vrbl for this class
 * 
 * @return the present state of the debug vrbl for this class
 * @author Robert Brown
 * @date 9 March 2016
 */
public boolean getDebugState ()
{
    return (this.debugOn);
} // end getDebugState()

/**
 * returns the brake speed when we execute a braking maneuver
 * 
 * @return the present brake speed
 * @author Robert Brown
 * @date 13 February 2016
 */
public double getBrakeSpeed ()
{
    return (this.brakeSpeed);
} // end getBrakeSpeed()

/**
 * returns the driving correction factor that is used to keep the robot in a
 * straight line when we are driving and one side or the other of the robot
 * gets ahead of the other side
 * 
 * @return the present correction factor
 * @author Robert Brown
 * @date 13 February 2016
 */
public double getDrivingCorrectionFactor ()
{
    return this.drivingCorrectionFactor;
} // end getDrivingCorrectionFactor()

/**
 * Gets forward velocity based on the difference in distance over the
 * difference in time from the last time the method was called.
 * 
 * @return The current velocity of the robot.
 * @author Michael Andrzej Klaczynski
 */
public double getForwardVelocity ()
{
    double speed =
            (((this.transmission.getLeftRearEncoderDistance() +
                    this.transmission.getRightRearEncoderDistance()) /
                    2 -
                    (this.prevLeftDistance + this.prevRightDistance) /
                            2)) /
                    (Hardware.kilroyTimer.get() - this.prevTime);

    this.prevLeftDistance =
            this.transmission.getLeftRearEncoderDistance();
    this.prevRightDistance =
            this.transmission.getRightRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return speed;
} // end getForwardVelocity()

/**
 * Gets the velocity of the right rear motor in a two motor drive train
 * 
 * @return The current velocity of the right rear motor
 * @author Alex Kneipp
 */
public double getRightMotorVelocity ()
{
    // based on the "getForwardVelocity()" method
    double speed =
            ((this.transmission.getRightRearEncoderDistance() -
                    this.prevRightDistance) / 2) /
                    (Hardware.kilroyTimer.get() -
                            this.prevTime);

    this.prevRightDistance =
            this.transmission.getRightRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return speed;
} // end getRightMotorVelocity()

/**
 * Gets the velocity of the left rear motor in a two motor drive train
 * 
 * @return The current velocity of the left rear motor.
 * @author Alex Kneipp
 */
public double getLeftMotorVelocity ()
{
    // based on the "getForwardVelocity()" method
    double speed =
            ((this.transmission.getLeftRearEncoderDistance() -
                    this.prevLeftDistance) / 2) /
                    (Hardware.kilroyTimer.get() -
                            this.prevTime);

    this.prevLeftDistance =
            this.transmission.getLeftRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return speed;
} // end getLeftMotorVelocity()

/**
 * Returns the default maximum speed. Used in Autonomous/Teleop Init.
 * 
 * @return - returns the present maximum speed
 *         between 0 and -1.0
 * @author Robert Brown
 * @date 13 February 2016
 */
public double getDefaultMaxSpeed ()
{
    return this.defaultMaxSpeed;
} // end getDefaultMaxSpeed()

/**
 * Returns the default maximum turn speed. Used in Autonomous/Teleop Init.
 * 
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the present maximum turn speed
 *         between 0 and 1.0. This represents the forward and reverse
 *         values from the joysticks and
 *         ranges from 0.0 to 1.0 (100%) and will be
 *         calculated based on whether or not the transmission
 *         has reversed joysticks and which way we are turning
 */
public double getDefaultTurnSpeed ()
{
    return this.defaultTurnSpeed;
} // end getDefaultTurnSpeed()

/**
 * Gets the minimum speed our motors are allowed to go. Used in
 * Autonomous/Teleop Init.
 * 
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the present minimum speed
 *         allowed. This represents the forward
 *         values from the joysticks and
 *         ranges from 0.0 to 1.0 (100%) and will be
 *         calculated based on whether or not the transmission
 *         has reversed joysticks
 */
public double getMinMotorSpeed ()
{
    return this.minimumMotorSpeed;
} // end getMinMotorSpeed()

/**
 * Returns the normalized default maximum speed. Used in
 * Autonomous/Teleop Init. This will return the speed based
 * upon how the joysticks are. If they are reversed, the values
 * are 0 to 1.0. If they are not reversed, the values are 0 and -1.0
 * 
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the present normalized default maximum speed
 *         between 1.0 and -1.0
 */
public double getNormalizedDefaultMaxSpeed ()
{
    // ----------------------------------------
    // if the joysticks have been declared to be
    // be reversed in the transmission class, then
    // return the default Max speed. Otherwise,
    // we need to return a negative max speed to
    // match the joystick values
    // ----------------------------------------
    if (this.transmission.isLeftJoystickReversed() == true)
        return this.getDefaultMaxSpeed();
    return (-this.getDefaultMaxSpeed());
} // end getNormalizedDefaultMaxSpeed()

/**
 * Returns the normalized default turn speed. Used in
 * Autonomous/Teleop Init. This will return the turn speed based
 * upon how the joysticks are. If they are reversed, the values
 * are 0 to 1.0. If they are not reversed, the values are 0 and -1.0
 * 
 * @author Robert Brown
 * @date 13 February 2016
 * @param joystickValue
 *            the joystick's initial value that may be changed
 *            based on whether or not the joysticks are reversed in the
 *            transmission class
 * @return - returns the present normalized default turn speed
 *         between 1.0 and -1.0
 */
public double getNormalizedTurnSpeed (final double joystickValue)
{
    // ----------------------------------------
    // if the joysticks have been declared to be
    // be reversed in the transmission class, then
    // return the turn speed. Otherwise,
    // we need to return a negative turn speed to
    // match the joystick values
    // ----------------------------------------
    if (this.transmission.isLeftJoystickReversed() == true)
        return -joystickValue;
    return joystickValue;
} // end getNormalizedTurnSpeed()

/**
 * Gets rotational velocity based on the difference in distances over the
 * difference in time from the last time the method was called.
 * 
 * @return rotational speed
 * @author Michael Andrzej Klaczynski
 */
public double getRotationalVelocity ()
{
    double rotationalVelocity = ((Math.abs(
            this.transmission.getLeftRearEncoderDistance()) +
            Math.abs(
                    this.transmission.getRightRearEncoderDistance()) /
                    2 - ((Math.abs(this.prevLeftDistance) +
                            Math.abs(this.prevRightDistance)) /
                            2) / (Hardware.kilroyTimer.get() -
                                    this.prevTime)));

    this.prevLeftDistance =
            this.transmission.getLeftRearEncoderDistance();
    this.prevRightDistance =
            this.transmission.getRightRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return rotationalVelocity;
} // end getRotationalVelocity()

/**
 * Checks to see if we have driven a certain distance since the last time
 * the encoders were reset.
 * 
 * @param targetDistance
 *            The distance to check our actual distance against.
 * @return True if we have driven targetDistance inches, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean hasDrivenInches (final double targetDistance)
{
    // if either drive train is beyond the targetDistance
    if (Math.abs(
            this.transmission.getRightRearEncoderDistance()) >= Math.abs(
                    targetDistance) ||
            Math.abs(
                    this.transmission.getLeftRearEncoderDistance()) >= Math.abs(
                            targetDistance))
        // we're done
        return true;
    return false;
} // end hasDrivenInches()

/**
 * returns the brake speed when we execute a braking maneuver that we just
 * set it to
 * 
 * @param newBrakeSpeed
 *            - new speed to use for braking
 * @return the new brake speed
 * @author Robert Brown
 * @date 13 February 2016
 */
public double setBrakeSpeed (final double newBrakeSpeed)
{
    return (this.brakeSpeed = newBrakeSpeed);
} // end setBrakeSpeed()

/**
 * sets and returns the state of the debug vrbl for this class
 * 
 * @param newDebugState
 *            - value you wish to change the Debug state to
 * @return the newly changed state of the debug vrbl for this class
 * @author Robert Brown
 * @date 9 March 2016
 */
public boolean setDebugState (boolean newDebugState)
{
    return (this.debugOn = newDebugState);
} // end getDebugState()

/**
 * set a new default maximum turn speed and then returns the
 * newly set value. Used in Autonomous/Teleop Init.
 * 
 * @method setDefaultTurnSpeed
 * @param newTurnSpeed
 *            represents the default speed that you would like to use
 *            whenever you make a turn and don't pass in the
 *            joysticks value.
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the present maximum turn speed
 *         between 0 and 1.0
 */
public double setDefaultTurnSpeed (final double newTurnSpeed)
{
    // -----------------------------
    // make sure that the new default turn speed
    // is between 0 and 1.0
    // -----------------------------
    this.defaultTurnSpeed =
            Math.min(1.0, Math.max(0.0, newTurnSpeed));
    return (this.getDefaultTurnSpeed());
} // end setDefaultTurnSpeed()

/**
 * returns the driving correction factor that is used to keep the robot in a
 * straight line when we are driving and one side or the other of the robot
 * gets ahead of the other side that we just set it to
 * 
 * @param newCorrectionFactor
 *            - send in a new correction factor between 0.0 and 1.0
 * @return the newly set correction factor
 * @author Robert Brown
 * @date 13 February 2016
 */
public double
        setDrivingStraightCorrectionFactor (
                final double newCorrectionFactor)
{
    // -----------------------------
    // make sure that the new correction factor for
    // normal forward driving is between 0 and 1.0
    // -----------------------------
    this.drivingCorrectionFactor = Math.min(1.0,
            Math.max(0.0, newCorrectionFactor));
    return (this.getDrivingCorrectionFactor());
} // end setDrivingStraightCorrectionFactor()

/**
 * Sets maximum speed. Used in Autonomous/Teleop Init.
 * 
 * @param max
 *            is a double between 0.0 and 1.0
 *            The default max motor speed allowed. This represents
 *            the forward values from the joysticks and
 *            ranges from 0.0 to 1.0 (100%) and will be
 *            calculated based on whether or not the transmission
 *            has reversed joysticks
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the new maximum speed just set
 */
public double setMaxSpeed (final double max)
{
    // -----------------------------
    // make sure that the new default max forward speed
    // is between 0 and 1.0
    // -----------------------------
    this.defaultMaxSpeed = Math.min(1.0, Math.max(0.0, max));
    return (this.getDefaultMaxSpeed());
} // end setMaxSpeed()

/**
 * Sets the minimum speed our motors should go. Used in Autonomous/Teleop
 * Init.
 * 
 * @param min
 *            is a double between 0.0 and 1.0
 *            This represents the forward values
 *            from the joysticks and
 *            ranges from 0.0 to 1.0 (100%) and will be
 *            calculated based on whether or not the transmission
 *            has reversed joysticks
 * @author Robert Brown
 * @date 13 February 2016
 * @return - returns the new minimum speed just set
 */
public double setMinMotorSpeed (final double min)
{
    // -----------------------------
    // make sure that the new minimum speed
    // for the drive motors is between 0 and 1.0
    // -----------------------------
    this.minimumMotorSpeed = Math.min(1.0, Math.max(0.0, min));
    return (this.getMinMotorSpeed());
} // end setMinMotorSpeed()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end.
 * 
 * @param whichWay
 *            - enum which represents either TURN_RIGHT or TURN_LEFT (enum
 *            type = turnWhichWay)
 * @param degrees
 *            The number of degrees to turn. Range is 0 - 180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnByDegrees (final turnWhichWay whichWay,
        final double degrees, final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    // -----------------------------------------
    // Make sure that the degrees requested stays
    // within 0-180
    // -----------------------------------------
    double turnDegrees = Math.min(180, Math.max(0.0, degrees));

    double turnInRadians = Math.toRadians(turnDegrees);

    // ----------------------------------------
    // are we turning right
    // ----------------------------------------
    if (whichWay == turnWhichWay.TURN_RIGHT)
        {
        if (this.transmission.getRightRearEncoderDistance() <= -(turnInRadians *
                this.ROBOT_TURNING_RADIUS) ||
                this.transmission.getLeftRearEncoderDistance() >= (turnInRadians *
                        this.ROBOT_TURNING_RADIUS))
            {
            // brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                System.out.println("BREAKING...");
                return (this.brake(-this.brakeSpeed,
                        this.brakeSpeed));
                }
            return true;
            }
        }
    // ----------------------------------------
    // we are turning left
    // ----------------------------------------
    else
        {
        if (this.transmission.getRightRearEncoderDistance() >= (turnInRadians *
                this.ROBOT_TURNING_RADIUS) ||
                this.transmission.getLeftRearEncoderDistance() <= -(turnInRadians *
                        this.ROBOT_TURNING_RADIUS))
            {
            // brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                System.out.println("BREAKING...");
                return (this.brake(this.brakeSpeed,
                        -this.brakeSpeed));
                }
            return true;
            }
        }
    // turn the robot
    this.transmission.controls(leftJoystickInputValue,
            rightJoystickInputValue);
    // We're not done driving yet!!
    return false;
} // end turnByDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Assumed to be true.
 * CALLS turnLeftDegrees(degrees, true)
 * 
 * @param degrees
 *            The number of degrees to turn left. Range is from 0-180.
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnLeftDegrees (final double degrees)
{
    return (this.turnLeftDegrees(degrees, true));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Calls turnLeftDegrees
 * (degrees, brakeAtEnd, defaultMaxSpeed (1.0), defaultMaxSpeed (1.0))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnLeftDegrees (final double degrees,
        final boolean brakeAtEnd)
{
    return (this.turnLeftDegrees(degrees, brakeAtEnd,
            this.getNormalizedTurnSpeed(
                    -this.getDefaultTurnSpeed()),
            this.getNormalizedTurnSpeed(
                    this.getDefaultTurnSpeed())));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Assumed to be true.
 * Calls turnLeftDegrees (degrees, true, leftJoystickInputValue,
 * rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnLeftDegrees (final double degrees,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnLeftDegrees(degrees, true,
            leftJoystickInputValue,
            rightJoystickInputValue));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Calls turnByDegrees
 * (turnWhichWay.TURN_LEFT, degrees, brakeAtEnd, leftJoystickInputValue,
 * rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnLeftDegrees (final double degrees,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnByDegrees(turnWhichWay.TURN_LEFT, degrees,
            brakeAtEnd, leftJoystickInputValue,
            rightJoystickInputValue));
} // end turnLeftDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Assumed to be true.
 * (Just calls turnRightDegrees(degrees, true))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnRightDegrees (final double degrees)
{
    return (this.turnRightDegrees(degrees, true));
} // end turnLeftDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Calls
 * turnRightDegrees (degrees, brakeAtEnd, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnRightDegrees (final double degrees,
        final boolean brakeAtEnd)
{
    return (this.turnRightDegrees(degrees, brakeAtEnd,
            this.getNormalizedTurnSpeed(this.getDefaultTurnSpeed()),
            this.getNormalizedTurnSpeed(
                    -this.getDefaultTurnSpeed())));
} // end turnRightDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Assumed to be true.
 * Calls turnRightDegrees (degrees, true, leftJoystickInputValue,
 * rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnRightDegrees (final double degrees,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnRightDegrees(degrees, true,
            leftJoystickInputValue,
            rightJoystickInputValue));
} // end turnLeftDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter from other
 * method controls whether or not we brake at the end. Calls turnByDegrees
 * (turnWhichWay.TURN_RIGHT, degrees, brakeAtEnd, leftJoystickInputValue,
 * rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean turnRightDegrees (final double degrees,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnByDegrees(turnWhichWay.TURN_RIGHT, degrees,
            brakeAtEnd, leftJoystickInputValue,
            rightJoystickInputValue));
} // end turnRightDegrees()

/**
 * Turn the robot until it has the largest blob in its vision processing
 * array at roughly the center of its vision.
 * 
 * @param percentageDeadBand
 *            -The percentage from the center to the edge of the image that
 *            the blob must rest within. Splits the deadband across the
 *            adjutedProportionalCenter. For example, a deadband of .2
 *            (20%) with an adjustedProportionalCenter of -.5 would
 *            return true if the center of mass of the largest blob is
 *            between relative positions of -.6 and -.4.
 * @param correctionSpeed
 *            -The speed at which the robot should turn to get the target in
 *            the center. Be careful though, if the deadband is too narrow
 *            and the speed to high, the robot will oscillate around the
 *            center or stop on the other side of the deadband.
 * @param adjustedProportionalXCenter
 *            -Double to tell to the code about where in the image we want
 *            the
 *            largest blob to be. Proportional across the image, with the
 *            left
 *            edge as -1.0, the center as 0.0, and the right edge as 1.0
 * @param savePictures
 *            -Boolean to determine whether we want to save up to 10 images
 *            taken for manual processing later. No guarantee that they
 *            will not be overridden by another call to saveImagesSafely
 *            somewhere else, so they may not actually be on the drive
 *            after the match.
 * @return
 *         -True if we're done centering, false otherwise. Especially
 *         false if we don't have a camera or ringlight. How does one
 *         test for an "especially false" value you ask, well that's for
 *         me to not know and you to laugh at.
 * @author Alex Kneipp
 */
//public boolean testingAlignByCamera (double percentageDeadBandX,
//        double percentageDeadBandY,
//        double correctionSpeed, double adjustedProportionalXCenter,
//        double adjustedProportionalYCenter,
//        boolean savePictures)
//{
//    //If the stupid programmers didn't give me a camera or relay before
//    //calling this, don't even try to align, it would kill me and all my
//    //friend classes.  Trying to align by the camera without a camera...
//    //How stupid can you get, programmers?
//    if (this.camera != null && this.ringLightRelay != null)
//        {
//        //actually start
//        if (firstTimeAlign == true)
//            {
//            this.cameraTimer.start();
//            //TODO removed to minimize problems with the method in the
//            //future
//            //            savedCameraValue = Hardware.cameraSolenoid.get(); //TODO delete this for more elegant flow
//            //            //TODO don't reference hardware in general purpose class.
//            //            Hardware.cameraSolenoid
//            //                    .set(DoubleSolenoid.Value.kReverse);
//            //turn down the lights
//            this.camera.writeBrightness(
//                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
//            //Woah, that's too dark! Someone turn on the ringlight!
//            this.ringLightRelay.set(Value.kOn);
//            Hardware.rightRearEncoder.reset();
//            Hardware.leftRearEncoder.reset();
//            firstTimeAlign = false;
//            }
//        //If we claim to be driving by camera and we've waited long enough
//        //(a quarter second) for someone to brighten up the darkness with 
//        //the ringlight.
//        if (isDoneAligning == false)
//            {
//            if (this.cameraTimer.get() >= .50)
//                {
//                //try to take a picture and save it in memory and on the "hard disk"
//                try
//                    {
//                    //Only bother taking and processing 
//                    //an image if we have a new one
//                    //to take.
//                    if (Hardware.axisCamera.freshImage() == true)
//                        {
//                        Hardware.imageProcessor.updateImage(
//                                Hardware.axisCamera.getImage());
//                        //tell imageProcessor to use the image we just took to look for 
//                        //blobs
//                        Hardware.imageProcessor
//                                .updateParticleAnalysisReports();
//                        }
//                    }
//                //This is NI yelling at us for something being wrong
//                catch (NIVisionException e)
//                    {
//                    //if something wrong happens, tell the stupid programmers 
//                    //who let it happen more information about where it came from
//                    e.printStackTrace();
//                    }
//
//                /*
//                 * Tell the programmers the absolute and relative x coordinates
//                 * of the center of mass of the largest blob.
//                 */
//                if (Hardware.imageProcessor
//                        .getParticleAnalysisReports().length > 0)
//                    {
//                    System.out.println("Relative x center of Mass :"
//                            + (getRelativeXCoordinate(
//                                    Hardware.imageProcessor
//                                            .getParticleAnalysisReports()[0].center_mass_x)));
//                    System.out.println("Relative y center of Mass:"
//                            + getRelativeYCoordinate(
//                                    Hardware.imageProcessor
//                                            .getParticleAnalysisReports()[0].center_mass_y));
//                    System.out.println(
//                            "isInTurningSection = "
//                                    + isInTurningSection);
//                    }
//
//                //if we're below where we want to be on the y axis
//                if ((Hardware.rightOperator.getRawButton(10) == false
//                        && Hardware.rightOperator
//                                .getRawButton(11) == false)
//                        && isInTurningSection == false
//                        && Hardware.imageProcessor
//                                .getParticleAnalysisReports().length > 0
//                        && getRelativeYCoordinate(
//                                Hardware.imageProcessor
//                                        .getParticleAnalysisReports()[0].center_mass_y) >= ((percentageDeadBandY
//                                                / 2)
//                                                + adjustedProportionalYCenter))
//                    {
//
//                    //drive forward
//                    //if (isInTurningSection == false)
//                        {
//                        if (this.transmission
//                                .isLeftJoystickReversed() == true
//                                || this.transmission
//                                        .isRightJoystickReversed() == true)
//                            {
//                            this.driveContinuous(correctionSpeed,
//                                    correctionSpeed);
//                            }
//                        else
//                            {
//                            this.driveContinuous(-correctionSpeed,
//                                    -correctionSpeed);
//                            }
//                        }
//                    }
//                //If we're above where we want to be in the Y axis
//                else if ((Hardware.rightOperator
//                        .getRawButton(10) == false
//                        && Hardware.rightOperator
//                                .getRawButton(11) == false)
//                        && isInTurningSection == false
//                        && Hardware.imageProcessor
//                                .getParticleAnalysisReports().length > 0
//                        && getRelativeYCoordinate(
//                                Hardware.imageProcessor
//                                        .getParticleAnalysisReports()[0].center_mass_y) <= ((-percentageDeadBandY
//                                                / 2)
//                                                + adjustedProportionalYCenter))
//                    {
//                    //if (isInTurningSection == false)
//                    //back up
//                        {
//                        if (this.transmission
//                                .isLeftJoystickReversed() == false
//                                || this.transmission
//                                        .isRightJoystickReversed() == false)
//                            {
//                            this.driveContinuous(-correctionSpeed,
//                                    -correctionSpeed);
//                            }
//                        else
//                            {
//                            this.driveContinuous(correctionSpeed,
//                                    correctionSpeed);
//                            }
//                        }
//                    }
//                else
//                    {
//                    this.isInTurningSection = true;
//                    }
//
//                //if the center of the largest blob is to the left of our 
//                //acceptable zone around the center
//                //TODO Hardware references for canceling, fix this.
//                if ((Hardware.rightOperator.getRawButton(10) == false
//                        && Hardware.rightOperator
//                                .getRawButton(11) == false)
//                        && isInTurningSection == true
//                        && Hardware.imageProcessor
//                                .getParticleAnalysisReports().length > 0
//                        && getRelativeXCoordinate(
//                                Hardware.imageProcessor
//                                        .getParticleAnalysisReports()[0].center_mass_x) >= ((-percentageDeadBandX
//                                                / 2)
//                                                + adjustedProportionalXCenter))
//                    {
//                    //turn left until it is in the zone (will be called over and
//                    //over again until the blob is within the acceptable zone)
//                    //if (isInTurningSection == true)
//                        {
//                        this.turnLeftDegrees(9999.0, false,
//                                correctionSpeed,
//                                -correctionSpeed);
//                        }
//                    }
//                //if the center of the largest blob is to the right of our 
//                //acceptable zone around the center
//                else if ((Hardware.rightOperator
//                        .getRawButton(10) == false
//                        && Hardware.rightOperator
//                                .getRawButton(11) == false)
//                        && isInTurningSection == true
//                        && Hardware.imageProcessor
//                                .getParticleAnalysisReports().length > 0
//                        && getRelativeXCoordinate(
//                                Hardware.imageProcessor
//                                        .getParticleAnalysisReports()[0].center_mass_x) >= ((percentageDeadBandX
//                                                / 2)
//                                                + adjustedProportionalXCenter))
//                    {
//                    //turn right until it is in the zone (will be called over and
//                    //over again until the blob is within the acceptable zone)
//                    //if (isInTurningSection == true)
//                        {
//                        this.turnRightDegrees(9999.0, false,
//                                -correctionSpeed,
//                                correctionSpeed);
//                        }
//                    }
//                //if the blob is in our X deadzone and we're trying to
//                //turn, tell the method we're done aligning.
//                else
//                    {
//                    if (isInTurningSection == true)
//                        isDoneAligning = true;
//                    }
//                //If the center of the blob is nestled happily in our deadzone
//                }
//            }
//        else
//            {
//            //Set up for next call
//            firstTimeAlign = true;
//            this.isInTurningSection = false;
//            this.isDoneAligning = false;
//            //Stop and reset the camera timer for next call delay
//            this.cameraTimer.stop();
//            this.cameraTimer.reset();
//
//            Hardware.cameraSolenoid.set(savedCameraValue);//TODO hardware reference.
//
//            //stop the robot
//            Hardware.transmission.controls(0.0, 0.0);
//            //save the image of us when we're in alignment.
//            if (savePictures == true)
//                Hardware.axisCamera.saveImagesSafely();
//            //Let the coach see normally again.
//            this.camera.writeBrightness(
//                    Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
//            //Turn off the ringlight because we don't need it.
//            this.ringLightRelay.set(Value.kOff);
//            //tell the programmers we're done.
//            return true;
//            }
//        }
//    //final return just so we always make sure we tell the programmers 
//    //we're not done if no other instance catches it
//    return false;
//}//end alignByCamera()
//
//boolean isDoneAligning = false;
//boolean isInTurningSection = false;
/**
 * Aligns the robot to the goal based on camera input. First turns to face
 * the
 * goal and then drives forward or back to bring the goal into the desired Y
 * deadband.
 * 
 * @param percentageDeadbandX
 *            -The deadband around "adjustedProportionalCenterX" that the
 *            blob
 *            is allowed to be in so the robot doesn't infinitely oscillate
 *            around the goal. Splits the deadband around the adjusted
 *            center,
 *            so a call with center 0.0 and deadband percentage .20 would
 *            allow
 *            the blob to be between -.10 and .10.
 * @param percentageDeadbandY
 *            -The deadband around "adjustedProportionalCenterY" that the
 *            blob
 *            is allowed to be within so the robot doesn't oscillate forward
 *            and
 *            back infinitely, like it's doing some strange robotic
 *            charlston.
 *            Splits the deadband across the adjustedProportionalYCenter, so
 *            a
 *            call with center 0.0 and deadband .20 would allow the blob to
 *            be
 *            within (-0.1,0.1).
 * @param adjustedProportionalCenterX
 *            -the
 * @param adjustedProportionalCenterY
 * @param turningCorrectionSpeed
 * @param cancelAlign
 * @param savePictures
 * @param printDebugInfo
 * @return
 */
public alignByCameraReturn alignByCameraStateMachine (
        double percentageDeadbandX,
        double percentageDeadbandY,
        double adjustedProportionalCenterX,
        double adjustedProportionalCenterY,
        double turningCorrectionSpeed, double driveCorrectionSpeed,
        boolean cancelAlign, boolean savePictures,
        boolean printDebugInfo)
{
    if (cancelAlign == true)
        {
        currentState = alignByCameraStates.QUIT;
        }
    if (printDebugInfo == true)
        {
        System.out.println("Align State:" + currentState);
        }
    switch (currentState)
        {
        case BEGINNING_SETUP:
            this.savedGear = this.transmission.getGear();
            this.transmission.setGear(2);
            this.cameraTimer.start();
            //turn down the lights
            this.camera.writeBrightness(
                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
            //Woah, that's too dark! Someone turn on the ringlight!
            this.ringLightRelay.set(Value.kOn);
            this.transmission.resetAllEncoders();
            //TODO I don't think this is actually necessary.
            //Hardware.rightRearEncoder.reset();
            //Hardware.leftRearEncoder.reset();
            firstTimeAlign = false;
            currentState =
                    alignByCameraStates.LOAD_BALL;
            break;
        case LOAD_BALL:
            if (Hardware.pickupArm.ballIsOut() == false)
                {
                if (Hardware.pickupArm.moveToPosition(
                        ArmPosition.DEPOSIT) == true)//TODO Hardware reference
                    {
                    Hardware.pickupArm.pullInBall(true);
                    }
                }
            else
                {
                Hardware.pickupArm.stopIntakeMotors();
                currentState =
                        alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
                }
            if (Hardware.cameraSolenoid.get() != DoubleSolenoid.Value.kReverse)
                {
                Hardware.cameraSolenoid.set(
                        DoubleSolenoid.Value.kReverse);
                Hardware.cameraToggleButton.update(true); //TODO HARDWARE REFERENCES
                currentState =
                        alignByCameraStates.WAIT_FOR_LIGHT_AND_CAMERA;//
                }
            break;
        case WAIT_FOR_LIGHT_AND_CAMERA:
            if (this.cameraTimer.get() >= 2.5)//The camera solenoid is SOOOO SLOOOWWW
                {
                currentState =
                        alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
                }
            break;
        case TAKE_AND_PROCESS_IMAGE:
            try
                {
                if (camera.freshImage() == true)
                    {
                    Hardware.imageProcessor.updateImage(
                            Hardware.axisCamera.getImage());
                    //tell imageProcessor to use the image we just took to look for 
                    //blobs
                    Hardware.imageProcessor.updateParticleAnalysisReports();
                    if (savePictures)
                        {
                        this.camera.saveImagesSafely();
                        }
                    }
                }
            catch (NIVisionException e)
                {
                e.printStackTrace();
                }
            finally
                {
                if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0)
                    {
                    if (printDebugInfo == true)
                        {
                        System.out.println(
                                "Largest blob X center of mass" +
                                        this.getRelativeXCoordinate(
                                                Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x));
                        System.out.println(
                                "Largest blob Y center of mass" +
                                        this.getRelativeYCoordinate(
                                                Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_y));
                        }
                    for (int i =
                            0; i < Hardware.imageProcessor.getParticleAnalysisReports().length; i++)
                        {
                        if (Hardware.imageProcessor.getParticleAnalysisReports()[i].boundingRectWidth > widestBlobWidth)
                            {
                            widestBlobWidth =
                                    Hardware.imageProcessor.getParticleAnalysisReports()[i].boundingRectWidth;
                            widestBlobIndex = i;
                            }
                        }
                    }
                //starts off as CHECK_Y_AXIS_CORRECTNESS, then changes when we start turn correcting
                currentState = returnToState;
                }
            break;
        case CHECK_Y_AXIS_CORRECTNESS:
            //if the blob is below our y deadzone
            if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeYCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_y) >= ((percentageDeadbandY /
                                    2) + adjustedProportionalCenterY))
                {
                currentState = alignByCameraStates.DRIVE_FORWARD;
                }
            //if the blob is above our y deadzone
            else if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeYCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_y) <= ((-percentageDeadbandY /
                                    2) + adjustedProportionalCenterY))
                {
                currentState = alignByCameraStates.DRIVE_BACKWARD;
                }
            //if the blob is within our Y deadzone
            else
                {
                currentState =
                        alignByCameraStates.QUIT;
                if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0)
                    {
                    System.out.println(
                            "Y Deadzone = (" + (((-percentageDeadbandY /
                                    2) + adjustedProportionalCenterY) *
                                    120 + 120) + ", " +
                                    (((percentageDeadbandY / 2) +
                                            adjustedProportionalCenterY) *
                                            120 + 120) + ")");
                    System.out.println("Y axis center of mass:" +
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_y);
                    }
                }
            break;
        case CHECK_X_AXIS_CORRECTNESS:
            //if the center of our largest blob is to the left of our desired deadzone
            //TODO: This is wrong. Fix. --MAK
            //			if (Hardware.imageProcessor
            //			        .getParticleAnalysisReports().length > 0
            //			        && getRelativeXCoordinate(
            //			                Hardware.imageProcessor
            //			                        .getParticleAnalysisReports()[widestBlobIndex].center_mass_x) >= ((-percentageDeadbandX
            //			                                / 2)
            //			                                + adjustedProportionalCenterX))
            //--MAK
            if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x) <= ((-percentageDeadbandX /
                                    2) + adjustedProportionalCenterX))
                {
                currentState = alignByCameraStates.ALIGN_LEFT;
                }
            //if the center of the largest blob is to the right of our 
            //acceptable zone around the center
            else if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x) >= ((percentageDeadbandX /
                                    2) + adjustedProportionalCenterX))
                {
                currentState = alignByCameraStates.ALIGN_RIGHT;
                }
            //if the blob is in our x deadzone
            else
                {
                if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0)
                    {
                    System.out.println(
                            "X Deadzone = (" + (((-percentageDeadbandX /
                                    2) + adjustedProportionalCenterX) *
                                    160 + 160) + ", " +
                                    (((percentageDeadbandX / 2) +
                                            adjustedProportionalCenterX) *
                                            160 + 160) + ")");
                    System.out.println("X axis center of mass:" +
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x);
                    }
                currentState =
                        alignByCameraStates.CHECK_Y_AXIS_CORRECTNESS;
                }
            break;
        case DRIVE_FORWARD:
            //drive forward
            //We need to check to make sure that we travel in the correct direction, regardless of joystick direction
            //TODO make sure transmission actually takes into account the joystick direction in the driveContinuous
            //execution chain
            if (this.transmission.isLeftJoystickReversed() == true ||
                    this.transmission.isRightJoystickReversed() == true)
                {
                this.driveContinuous(driveCorrectionSpeed,
                        driveCorrectionSpeed);
                }
            else
                {
                this.driveContinuous(-driveCorrectionSpeed,
                        -driveCorrectionSpeed);
                }
            //We've started moving, now we need to take another image to see where we are now.
            currentState = alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
            returnToState =
                    alignByCameraStates.CHECK_Y_AXIS_CORRECTNESS;
            break;
        case DRIVE_BACKWARD:
            //We need to check to make sure that we travel in the correct direction, regardless of joystick direction
            if (this.transmission.isLeftJoystickReversed() == false ||
                    this.transmission.isRightJoystickReversed() == false)
                {
                this.driveContinuous(-driveCorrectionSpeed,
                        -driveCorrectionSpeed);
                }
            else
                {
                this.driveContinuous(driveCorrectionSpeed,
                        driveCorrectionSpeed);
                }
            //We've started moving, now we need to take another image to see where we are now.
            currentState = alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
            returnToState =
                    alignByCameraStates.CHECK_Y_AXIS_CORRECTNESS;
            break;
        case ALIGN_RIGHT:
            //turn right until it is in the zone (will be called over and
            //over again until the blob is within the acceptable zone)
            this.turnRightDegrees(9999.0, false,
                    -turningCorrectionSpeed,
                    turningCorrectionSpeed);
            currentState = alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
            returnToState =
                    alignByCameraStates.CHECK_X_AXIS_CORRECTNESS;
            break;
        case ALIGN_LEFT:
            //turn left until it is in the zone (will be called over and
            //over again until the blob is within the acceptable zone)
            this.turnLeftDegrees(9999.0, false,
                    turningCorrectionSpeed,
                    -turningCorrectionSpeed);
            currentState = alignByCameraStates.TAKE_AND_PROCESS_IMAGE;
            returnToState =
                    alignByCameraStates.CHECK_X_AXIS_CORRECTNESS;
            break;
        default:
        case QUIT:
            //Set up for next call
            //Stop and reset the camera timer for next call delay
            if (savePictures == true)
                this.camera.saveImagesSafely();
            this.transmission.setGear(savedGear);
            currentState = alignByCameraStates.BEGINNING_SETUP;
            returnToState =
                    alignByCameraStates.CHECK_X_AXIS_CORRECTNESS;
            this.cameraTimer.stop();
            this.cameraTimer.reset();
            //stop the robot
            this.transmission.controls(0.0, 0.0);
            //save the image of us when we're in alignment.
            //Let the coach see normally again.
            //            this.camera.writeBrightness(
            //                    Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
            //Turn off the ringlight because we don't need it.
            //this.ringLightRelay.set(Value.kOff);
            //tell the programmers we're done.
            if (cancelAlign == true)
                {
                return alignByCameraReturn.CANCELLED;
                }
            return alignByCameraReturn.DONE;
        }
    return alignByCameraReturn.WORKING;
}

int widestBlobWidth = 0;
int widestBlobIndex = 0;

alignByCameraStates currentState =
        alignByCameraStates.BEGINNING_SETUP;
alignByCameraStates returnToState =
        alignByCameraStates.CHECK_X_AXIS_CORRECTNESS;

public static enum alignByCameraReturn
    {
    //returns this if the user cancelled the align
    //returning this makes the method sad :'(
    CANCELLED,
    //returns this if we're still working on aligning,
    //Basically the method saying "JESUS!  Be patient!"
    WORKING,
    //returns this if the method is done!
    //Basically: "ARRAY!"
    DONE;
    }

//TODO rewrite alignByCamera to use a state machine, the way it is right
//now is pretty messy.
private enum alignByCameraStates
    {
    //Sets up everything to align
    BEGINNING_SETUP,
    //Loads the ball into the firing mechanism automatically, but only if we have one
    LOAD_BALL,
    //Waits long enough for the rightLight to turn on and the camera to lower its brightness
    WAIT_FOR_LIGHT_AND_CAMERA,
    //Takes and image and processes it
    TAKE_AND_PROCESS_IMAGE,
    //Checks whether or not the blob is where we want it on the Y axis
    CHECK_Y_AXIS_CORRECTNESS,
    //Checks whether or not the blob is where we want it on the X axis
    CHECK_X_AXIS_CORRECTNESS,
    //start driving forward
    DRIVE_FORWARD,
    //start driving backward
    DRIVE_BACKWARD,
    //start turning right in order to align
    ALIGN_RIGHT,
    //start turning left in order to align
    ALIGN_LEFT,
    //set up everything for next run and return true
    QUIT;
    }

/**
 * Drives forward the specified number of inches aligning to a blob sighted
 * by
 * the camera.
 * 
 * @param driveDistanceInches
 *            How far you want to drive in inches. Will stop as soon as
 *            either
 *            side of the drive train reaches this value.
 * @param percentageDeadBand
 *            The deadband around the "adjustedProportionalCenter" the blob
 *            is
 *            allowed to be in before we start correcting.
 *            The deadband is split around the center, so a deadband of .20
 *            (20%) will allow the proportional X coordinate
 *            of the blob to be .10 to the right or .10 to the left.
 * @param correctionSpeed
 *            The speed to correct with. Either speed up the drivetrain
 *            opposite
 *            the way we want to go if greater than
 *            the defaultTurningValue, or slow down the drivetrain on the
 *            same
 *            side if less than the defualtTurningValue.
 * @param adjustedProportionalCenter
 *            The proportional coordinate center where we want the blobs to
 *            sit.
 *            To find proportional coordinate take the (absoluteCoordinate -
 *            midpoint)/midpoint
 * @param savePictures
 *            Save the pictures taken to the RoboRio's "Hard Drive"
 * @return
 *         True if we're done driving, false otherwise.
 * @author Alex Kneipp
 */

public boolean driveByCamera (double driveDistanceInches,
        double percentageDeadBand,
        double correctionSpeed, double adjustedProportionalCenter,
        boolean savePictures)
{

    if (this.camera != null && this.ringLightRelay != null)
        {
        if (this.firstRunDriveByCamera == true)
            {
            this.cameraTimer.start();
            //turn down the lights
            this.camera.writeBrightness(
                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
            this.savedGear = this.transmission.getGear(); //TODO shared var
            this.transmission.setGear(2);
            //Woah, that's too dark! Someone turn on the ringlight!
            this.ringLightRelay.set(Value.kOn);
            firstRunDriveByCamera = false;
            }
        //If we claim to be driving by camera and we've waitied long enough
        //(a quarter second) for someone to brighten up the darkness with 
        //the ringlight.
        if (this.cameraTimer.get() >= .25)
            {
            //try to take a picture and save it in memory and on the "hard disk"
            try
                {
                Hardware.imageProcessor.updateImage(
                        Hardware.axisCamera.getImage());
                if (savePictures == true)
                    Hardware.axisCamera.saveImagesSafely();
                }
            //This is NI yelling at us for something being wrong
            catch (NIVisionException e)
                {
                //if something wrong happens, tell the stupid programmers 
                //who let it happen more information about where it came from
                e.printStackTrace();
                }
            //tell imageProcessor to use the image we just took to look for 
            //blobs
            Hardware.imageProcessor.updateParticleAnalysisReports();
            //tell the programmers where the X coordinate of the center of 
            //mass of the largest blob
            //        System.out.println("CenterOfMass: " + Hardware.imageProcessor
            //                .getParticleAnalysisReports()[0].center_mass_x);
            //if the center of the largest blob is to the left of our 
            //acceptable zone around the center
            if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x) <= ((-percentageDeadBand /
                                    2) + adjustedProportionalCenter))
                {
                //turn left until it is in the zone (will be called over and
                //over again until the blob is within the acceptable zone)
                //TODO check and make sure this still doesn't work, then 
                //change it back or write turn continuous method
                this.driveContinuous(
                        Math.min(correctionSpeed, defaultTurnSpeed),
                        Math.max(defaultTurnSpeed,
                                correctionSpeed));
                }
            //if the center of the largest blob is to the right of our 
            //acceptable zone around the center
            else if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[0].center_mass_x) >= ((percentageDeadBand /
                                    2) + adjustedProportionalCenter))
                {
                //turn right until it is in the zone (will be called over and
                //over again until the blob is within the acceptable zone)
                this.driveContinuous(
                        Math.max(defaultTurnSpeed, correctionSpeed),
                        Math.min(correctionSpeed,
                                defaultTurnSpeed));
                }
            //If the blob is where we want it to be right now...
            else
                {
                //drive forward as fast as we can
                this.driveContinuous(defaultMaxSpeed,
                        defaultMaxSpeed);
                }
            //If the either of the encoders are beyond our drive distance, stop and tell
            //the code that we're done.
            if ((this.transmission.getRightRearEncoderDistance() >= driveDistanceInches ||
                    this.transmission.getLeftRearEncoderDistance() >= driveDistanceInches))
                {
                //Set up for next call
                firstTimeAlign = true;
                this.cameraTimer.stop();
                this.cameraTimer.reset();
                //stop
                Hardware.transmission.controls(0.0, 0.0);
                this.transmission.setGear(savedGear);
                //say we're done
                return true;
                }
            }
        }
    return false;

}

/**
 * Turn the robot until it has the largest blob in its vision processing
 * array at roughly the center of its vision.
 * 
 * @param percentageDeadBand
 *            -The percentage from the center to the edge of the image that
 *            the blob must rest within. Splits the deadband across the
 *            adjutedProportionalCenter. For example, a deadband of .2
 *            (20%) with an adjustedProportionalCenter of -.5 would
 *            return true if the center of mass of the largest blob is
 *            between relative positions of -.6 and -.4.
 * @param correctionSpeed
 *            -The speed at which the robot should turn to get the target in
 *            the center. Be careful though, if the deadband is too narrow
 *            and the speed to high, the robot will oscillate around the
 *            center or stop on the other side of the deadband.
 * @param adjustedProportionalCenter
 *            -Double to tell to the code about where in the image we want
 *            the
 *            largest blob to be. Proportional across the image, with the
 *            left
 *            edge as -1.0, the center as 0.0, and the right edge as 1.0
 * @param savePictures
 *            -Boolean to determine whether we want to save up to 10 images
 *            taken for manual processing later. No guarantee that they
 *            will not be overridden by another call to saveImagesSafely
 *            somewhere else, so they may not actually be on the drive
 *            after the match.
 * @return
 *         -True if we're done centering, false otherwise. Especially
 *         false if we don't have a camera or ringlight. How does one
 *         test for an "especially false" value you ask, well that's for
 *         me to not know and you to laugh at.
 * @author Alex Kneipp
 */
public alignByCameraReturn alignByCamera (double percentageDeadBand,
        double correctionSpeed, double adjustedProportionalCenter,
        boolean savePictures)
{
    //If the stupid programmers didn't give me a camera or relay before
    //calling this, don't even try to align, it would kill me and all my
    //friend classes.  Trying to align by the camera without a camera...
    //How stupid can you get, programmers?
    if (this.camera != null && this.ringLightRelay != null)
        {
        //actually start
        if (firstTimeAlign == true)
            {
            this.cameraTimer.start();
            this.savedGear = this.transmission.getGear();
            this.transmission.setGear(2);
            //turn down the lights
            this.camera.writeBrightness(
                    Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
            //Woah, that's too dark! Someone turn on the ringlight!
            this.ringLightRelay.set(Value.kOn);
            Hardware.rightRearEncoder.reset();
            Hardware.leftRearEncoder.reset();
            firstTimeAlign = false;
            //				if (Hardware.cameraSolenoid
            //				        .get() != DoubleSolenoid.Value.kReverse)
            //				{
            //
            //				}
            }
        //If we claim to be driving by camera and we've waitied long enough
        //(a quarter second) for someone to brighten up the darkness with 
        //the ringlight.
        if (this.cameraTimer.get() >= .50)
            {
            //try to take a picture and save it in memory and on the "hard disk"
            try
                {
                //if (Hardware.axisCamera.freshImage() == true)
                    {
                    Hardware.imageProcessor.updateImage(
                            Hardware.axisCamera.getImage());

                    }
                }
            //This is NI yelling at us for something being wrong
            catch (NIVisionException e)
                {
                //if something wrong happens, tell the stupid programmers 
                //who let it happen more information about where it came from
                e.printStackTrace();
                }
            //tell imageProcessor to use the image we just took to look for 
            //blobs
            Hardware.imageProcessor.updateParticleAnalysisReports();
            for (int i =
                    0; i < Hardware.imageProcessor.getParticleAnalysisReports().length; i++)
                {
                if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                        this.widestBlobWidth < Hardware.imageProcessor.getParticleAnalysisReports()[i].boundingRectWidth)
                    {
                    this.widestBlobWidth =
                            Hardware.imageProcessor.getParticleAnalysisReports()[i].boundingRectWidth;
                    this.widestBlobIndex = i;
                    }
                }
            /*
             * Tell the programmers the absolute and relative x coordinates
             * of the center of mass of the largest blob.
             */
            if (Hardware.imageProcessor.getParticleAnalysisReports().length > 0)
                {
                System.out.println("CenterOfMass: " +
                        Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x);
                System.out.println("Relative x center of Mass :" +
                        (getRelativeCameraCoordinate(
                                Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x,
                                true)));
                System.out.println(
                        "Relative Center range: (" +
                                (-percentageDeadBand / 2 +
                                        adjustedProportionalCenter) +
                                ", " + (percentageDeadBand / 2 +
                                        adjustedProportionalCenter) +
                                ")");
                }
            //if the center of the largest blob is to the left of our 
            //acceptable zone around the center			//--MAK
            //			if (Hardware.imageProcessor
            //			        .getParticleAnalysisReports().length > 0
            //			        && getRelativeXCoordinate(
            //			                Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x) <= ((-percentageDeadbandX
            //			                                / 2)
            //			                                + adjustedProportionalCenterX))

            if ((Hardware.rightOperator.getRawButton(10) == true &&
                    Hardware.rightOperator.getRawButton(
                            11) == true) == false &&
                    Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x) <= ((-percentageDeadBand /
                                    2) + adjustedProportionalCenter))
                {
                //turn left until it is in the zone (will be called over and
                //over again until the blob is within the acceptable zone)
                this.turnLeftDegrees(9999.0, false, correctionSpeed,
                        -correctionSpeed);
                //this.transmission.controls(.5, -.5);
                }
            //if the center of the largest blob is to the right of our 
            //acceptable zone around the center
            else if ((Hardware.rightOperator.getRawButton(10) == true &&
                    Hardware.rightOperator.getRawButton(
                            11) == true) == false &&
                    Hardware.imageProcessor.getParticleAnalysisReports().length > 0 &&
                    getRelativeXCoordinate(
                            Hardware.imageProcessor.getParticleAnalysisReports()[widestBlobIndex].center_mass_x) >= ((percentageDeadBand /
                                    2) + adjustedProportionalCenter))
                {
                //turn right until it is in the zone (will be called over and
                //over again until the blob is within the acceptable zone)
                this.turnRightDegrees(9999.0, false,
                        -correctionSpeed,
                        correctionSpeed);
                //this.transmission.controls(-.5, .5);
                }
            //If the center of the blob is nestled happily in our deadzone
            else
                {
                //					System.out.println("Center of Mass X: "
                //					        + Hardware.imageProcessor
                //					                .getParticleAnalysisReports()[0].center_mass_x);
                //					System.out.println("Deadzone = ("
                //					        + (((adjustedProportionalCenter
                //					                + percentageDeadBand / 2) * 160)
                //					                + 160)
                //					        + ", "
                //					        + (((adjustedProportionalCenter
                //					                - percentageDeadBand / 2) * 160)
                //					                + 160));
                //Set up for next call
                firstTimeAlign = true;
                needToRaiseCamera = false;
                //Stop and reset the camera timer for next call delay
                this.cameraTimer.stop();
                this.cameraTimer.reset();
                this.transmission.setGear(savedGear);
                //Hardware.cameraSolenoid.set(savedCameraValue);//TODO hardware reference.

                //stop the robot
                Hardware.transmission.controls(0.0, 0.0);
                //save the image of us when we're in alignment.
                if (savePictures == true)
                    Hardware.axisCamera.saveImagesSafely();
                //Let the coach see normally again.
                //                this.camera.writeBrightness(
                //                        Hardware.NORMAL_AXIS_CAMERA_BRIGHTNESS);
                //Turn off the ringlight because we don't need it.
                //this.ringLightRelay.set(Value.kOff);
                //tell the programmers we're done.
                return alignByCameraReturn.DONE;
                }
            }
        }
    if (Hardware.rightOperator.getRawButton(10) == true &&
            Hardware.rightOperator.getRawButton(11) == true)
        {
        return alignByCameraReturn.CANCELLED;
        }
    //final return just so we always make sure we tell the programmers 
    //we're not done if no other instance catches it
    return alignByCameraReturn.WORKING;

}//end alignByCamera()

int savedGear = 1;
DoubleSolenoid.Value savedCameraValue;
private boolean needToRaiseCamera = false;
/**
 * 3 argument override method of
 * alignByCamera(double,double,double,boolean),
 * presumes that the caller doesn't want to save images taken to the
 * "Hard drive." If that sounds like something you do want to do, try
 * alignByCamera(double,double,double,boolean)
 * 
 * @param percentageDeadBand
 *            -See alignByCamera(double, double, double, boolean)
 * @param correctionSpeed
 *            -See alignByCamera(double, double, double, boolean)
 * @param proportionalCenter
 *            -See alignByCamera(double, double, double, boolean)
 * @return
 *         -See alignByCamera(double, double, double, boolean)
 * @author Alex Kneipp
 */
//	public boolean alignByCamera (double percentageDeadBand,
//	        double correctionSpeed, double proportionalCenter)
//	{
//		return alignByCamera(percentageDeadBand, correctionSpeed,
//		        proportionalCenter, false);
//	}

/**
 * 2 argument override method of
 * alignByCamera(double,double,double,boolean),
 * presumes that the caller doesn't want to save images taken to the
 * "Hard drive," and that the you don't want to align to something not
 * in the center of the image. If that sounds like something you do want to
 * do, try
 * alignByCamera(double,double,double,boolean)
 * 
 * @param percentageDeadBand
 *            -See alignByCamera(double, double, double, boolean)
 * @param correctionSpeed
 *            -See alignByCamera(double, double, double, boolean)
 * @return
 *         -See alignByCamera(double, double, double, boolean)
 *         // * @author Alex Kneipp
 *         //
 */
//	public boolean alignByCamera (double percentageDeadBand,
//	        double correctionSpeed)
//	{
//
//		return alignByCamera(percentageDeadBand, correctionSpeed, 0.0,
//		        false);
//	}


/**
 * 1 argument override method of
 * alignByCamera(double,double,double,boolean),
 * presumes that the caller doesn't want to save images taken to the
 * "Hard drive," that the you don't want to align to something not
 * in the center of the image, and that you don't want to set the turning
 * speed
 * for the correction. If that sounds like something you do want to do,
 * try
 * alignByCamera(double,double,double,boolean)
 * 
 * @param percentageDeadBand
 *            -See alignByCamera(double, double, double, boolean)
 * @return
 *         -See alignByCamera(double, double, double, boolean)
 * @author Alex Kneipp
 */
//	public boolean alignByCamera (double percentageDeadBand)
//	{
//		//I've decided .45 is a fair correction speed, can tweak later if need be.
//		return alignByCamera(percentageDeadBand,
//		        DEFAULT_CAMERA_ALIGNMENT_TURNING_SPEED);
//	}

/**
 * No argument override method of
 * alignByCamera(double,double,double,boolean),
 * for lazy programmers. Presumes you just want the default values,
 * cause you're lazy. If you're not lazy and you indeed do want to
 * control whether or not the alignByCamera method saves images taken,
 * the deadband percentage size, the turning speed of the alignment, or want
 * to
 * align to something not in the center see the other methods with the same
 * name.
 * 
 * @return
 *         -See alignByCamera(double, double, double, boolean)
 * @author Alex Kneipp
 */
//	public boolean alignByCamera ()
//	{
//		//I've decided 10% is a fair deadband range for general alignment, can tweak later if need be.
//		return alignByCamera(DEFAULT_ALIGNMENT_DEADBAND,
//		        DEFAULT_CAMERA_ALIGNMENT_TURNING_SPEED);
//	}

/**
 * Returns the relative X coordinate given the absolute coordinate from
 * the image processing class and the resolution of the camera from this
 * class.
 * 
 * @param absoluteCoordinate
 *            -The actual pixel coordinate you want to map, somewhere
 *            between 0
 *            and the resolution of the camera in the X axis.
 * @return
 *         -A relative coordinate between -1.0 and 1.0, with 0.0 as the
 *         center
 *         of the image, -1.0 as the far left, and 1.0 as the far right.
 * @author Alex Kneipp
 */
public double getRelativeXCoordinate (double absoluteCoordinate)
{
    return (absoluteCoordinate - (cameraXResolution / 2)) /
            (cameraXResolution / 2);
}

/**
 * Returns the relative Y coordinate given the absolute coordinate from
 * the image processing class and the resolution of the camera from this
 * class.
 * 
 * @param absoluteCoordinate
 *            -The actual pixel coordinate you want to map, somewhere
 *            between 0
 *            and the resolution of the camera in the Y axis.
 * @return
 *         -A relative coordinate between -1.0 and 1.0, with 0.0 as the
 *         center
 *         of the image, -1.0 as the top, and 1.0 as the bottom.
 * @author Alex Kneipp
 */
public double getRelativeYCoordinate (double absoluteCoordinate)
{
    return (absoluteCoordinate - (cameraYResolution / 2)) /
            (cameraYResolution / 2);
}

/**
 * Returns the relative coordinate given the absolute coordinate from
 * the image processing class and the resolution of the camera from this
 * class.
 * 
 * @param absoluteCoordinate
 *            -The actual pixel coordinate you want to map, somewhere
 *            between 0
 *            and the resolution of the camera in the axis you want to map.
 * @param isXCoordinate
 *            -True if you want the relative coordinate relative to the x
 *            axis,
 *            false for y.
 * @return
 *         -A relative coordinate between -1.0 and 1.0, with 0.0 as the
 *         center
 *         of the image, -1.0 as the far left or top, and 1.0 as the far
 *         right
 *         or bottom.
 * @author Alex Kneipp
 * @deprecated 3/18/16 by Alex Kneipp for poor design.
 *             Use getRelativeXCoordinate(double) and
 *             getRelativeYCoordinate(double) instead.
 */
@Deprecated
public double getRelativeCameraCoordinate (
        double absoluteCoordinate,
        boolean isXCoordinate)
{
    if (isXCoordinate == true)
        return (absoluteCoordinate - (cameraXResolution / 2)) /
                (cameraXResolution / 2);
    return (absoluteCoordinate - (cameraYResolution / 2)) /
            (cameraYResolution / 2);
}

public void setXResolution (double res)
{
    this.cameraXResolution = res;
}

public void setYResolution (double res)
{
    this.cameraYResolution = res;
}

/**
 * enum which describes which way to turn
 * 
 * {@value} TURN_RIGHT {@value} TURN_LEFT
 * 
 * @author Robert Brown
 * @date 13 February 2016
 *
 */
public enum turnWhichWay
    {
    TURN_RIGHT, TURN_LEFT;
    }

/*
 * Constants
 */

// TODO - get Kilroy's new turning radius
private final double ROBOT_TURNING_RADIUS = 11.25;

private final double DEFAULT_ALIGNMENT_DEADBAND = .1;
private final double DEFAULT_CAMERA_ALIGNMENT_TURNING_SPEED = .45;

private Transmission_old transmission = null;
//Camera we use for vision processing.
private KilroyCamera camera = null;
//Relay to control the ring light surrounding the camera for vision 
//processing
private Relay ringLightRelay = null;
//Delay timer for the camera so we Don't try to take a picture before 
//the camera is ready.
private final Timer cameraTimer = new Timer();
//The horizontal resolution of the camera for the drive class
private double cameraXResolution;
//The vertical resolution of the camera used in the drive class.
private double cameraYResolution;

private boolean firstTimeAlign = true;

private boolean firstRunDriveByCamera = true;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

// TODO tweak for the most effective brake method
private double brakeSpeed = 0.15;//previously 0.1

// -------------------------------------
// default max motor speed allowed. This represents
// the forward values from the joysticks and
// ranges from 0.0 to 1.0 (100%) and will be
// calculated based on whether or not the transmission
// has reversed joysticks
// ------------------------------------
private double defaultMaxSpeed = 1.0;

// -------------------------------------
// default max motor speed allowed during a turn. This represents
// the forward values from the joysticks and
// ranges from 0.0 to 1.0 (100%) and will be
// calculated based on whether or not the transmission
// has reversed joysticks
// ------------------------------------
private double defaultTurnSpeed = .5;

// ------------------------------------
// correction factor used to slow the drive
// motor that is ahead of the other drive motor
// This represents a percentage of the
// driving speed passed in from the joysticks
// and ranges from 0 to 1.0 (100%)
// ------------------------------------
private double drivingCorrectionFactor = 0.75;//0.6;// TODO: changed around # :0.75;

// -------------------------------------
// minimum motor speed allowed. This represents
// the forward values from the joysticks and
// ranges from 0.0 to 1.0 (100%) and will be
// calculated based on whether or not the transmission
// has reversed joysticks
// ------------------------------------
private double minimumMotorSpeed = .20;

//--------------------------------------
// DebugOn state - true or false
//-------------------------------------
private boolean debugOn = false;
} // end class
