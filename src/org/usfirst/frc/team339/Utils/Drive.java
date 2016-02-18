
package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.KilroyCamera;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;
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
 * brake.
 * 
 * @param brakeSpeed
 *            The speed with which to brake, recommended to be rather low.
 * @return true if we're done braking, false otherwise.
 * @author Alex Kneipp
 */
private boolean brake (final double brakeSpeed)
{
    // TODO maybe make argument a constant in the class.
    // TODO find out ideal brakespeed.
    return (this.transmission.brake(brakeSpeed));
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
        compensateForReversedLeftJoystick (final double joystickInput)
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
        compensateForReversedRightJoystick (final double joystickInput)
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
 * @return - the corrected joystick value after it is reduced and made
 *         sure it is meeting a minimum speed
 * @author Robert Brown
 * @date 13 February 2016
 */
private double
        determineCorrectedJoystickValue (final double rawJoystickValue)
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
    if (this.transmission.isLeftJoystickReversed() == true)
        return (Math.max(this.getMinMotorSpeed(),
                rawJoystickValue * this.getDrivingCorrectionFactor()));
    return (Math.min(-this.getMinMotorSpeed(), rawJoystickValue *
            this.getDrivingCorrectionFactor()));
} // end determineCorrectJoystickValue

/**
 * Drives forward forever (almost). (calls driveForwardInches(9999, false,
 * defaultMaxSpeed (1.0), defaultMaxSpeed (1.0)))
 * 
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveContinuous ()
{
    this.driveForwardInches(9999.0, false,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed());
} // end driveContinuous()

/**
 * Drives forward forever (almost). (calls driveForwardInches(9999, false,
 * leftJoystickInputValue, rightJoystickInputValue))
 * 
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @author Robert Brown
 * @date 13 February 2016
 */
public void driveContinuous (final double leftJoystickInputValue,
        final double rightJoystickInputValue)

{
    this.driveForwardInches(9999.0, false, leftJoystickInputValue,
            rightJoystickInputValue);
} // end driveContinuous()

/**
 * Drives forward distance inches with correction. (calls
 * driveForwardInches(distance, true, defaultMaxSpeed (1.0),
 * defaultMaxSpeed (1.0)))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @return True if done driving, false otherwise.
 * @author Robert Brown
 * @date 13 February 2016
 */
public boolean driveForwardInches (final double distance)
{
    return (this.driveForwardInches(distance, true,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveForwardInches()

/**
 * Drives forward distance inches with correction. (calls
 * driveForwardInches(distance, brakeAtEnd, defaultMaxSpeed (1.0),
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
public boolean driveForwardInches (final double distance,
        final boolean brakeAtEnd)
{
    return (this.driveForwardInches(distance, brakeAtEnd,
            this.getNormalizedDefaultMaxSpeed(),
            this.getNormalizedDefaultMaxSpeed()));
} // end driveForwardInches()

/**
 * Drives forward distance inches with correction. (calls
 * driveForwardInches(distance, true, leftJoystickInputValue,
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
public boolean driveForwardInches (final double distance,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.driveForwardInches(distance, true,
            leftJoystickInputValue, rightJoystickInputValue));
} // end driveForwardInches()

/**
 * Drives forward distance inches with correction.
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
public boolean driveForwardInches (final double distance,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    // -----------------------------------
    // stop if the average value of either drive train
    // is greater than the desired distance traveled.
    if (this.hasDrivenInches(distance) == true)
        {
        // if requested to brake, stop
        if (brakeAtEnd == true)
            {
            return (this.brake(this.brakeSpeed));
            }
        // -----------------------------------
        // otherwise we are not braking, but we
        // are now finished driving
        // -----------------------------------
        return true;
        }
    // if we are presently going straight - keep the
    // speeds equal
    if (this.transmission
            .getRightRearEncoderDistance() == this.transmission
                    .getLeftRearEncoderDistance())
        this.transmission.controls(compensateForReversedLeftJoystick(
                leftJoystickInputValue),
                compensateForReversedRightJoystick(
                        rightJoystickInputValue));
    // if the left drive train is ahead of the right drive train
    else if ((this.transmission
            .getRightRearEncoderDistance()) < (this.transmission
                    .getLeftRearEncoderDistance()))
        this.transmission.controls(
                compensateForReversedLeftJoystick(
                        determineCorrectedJoystickValue(
                                leftJoystickInputValue)),
                compensateForReversedRightJoystick(
                        rightJoystickInputValue));
    // if the right drive train is ahead of the left drive train
    else
        this.transmission.controls(compensateForReversedLeftJoystick(
                leftJoystickInputValue),
                compensateForReversedRightJoystick(
                        determineCorrectedJoystickValue(
                                rightJoystickInputValue)));

    // PRINT STATEMENTS:
    // TODO: remove
    // Prints out encoder values and the values we are sending to the
    // motors.
    // System.out.println("Left Distance: " +
    // Hardware.leftRearEncoder.getDistance());
    // System.out.println("Right Distance: " +
    // Hardware.rightRearEncoder.getDistance());

    // if (this.transmission
    // .getRightRearEncoderDistance() == this.transmission
    // .getLeftRearEncoderDistance())
    // {
    // System.out.println("Left Joystick: " + -leftJoystickInputValue);
    // System.out.println("Right Joystick: " + -rightJoystickInputValue);
    // }
    // else if ((this.transmission
    // .getRightRearEncoderDistance()) < (this.transmission
    // .getLeftRearEncoderDistance()))
    // {
    // System.out.println("Left Joystick: " +
    // determineCorrectedJoystickValue(-leftJoystickInputValue));
    // System.out.println("Right Joystick: " + -rightJoystickInputValue);
    // }
    // else
    // {
    // System.out.println("Left Joystick: " + -leftJoystickInputValue);
    // System.out.println("Right Joystick: " +
    // determineCorrectedJoystickValue(-rightJoystickInputValue));
    // }

    // ------------------------------------
    // still have more driving to do
    // ------------------------------------
    return false;
} // end driveForwardInches()

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
    double speed = (((this.transmission.getLeftRearEncoderDistance() +
            this.transmission.getRightRearEncoderDistance()) / 2 -
            (this.prevLeftDistance + this.prevRightDistance) / 2)) /
            (Hardware.kilroyTimer.get() - this.prevTime);

    this.prevLeftDistance = this.transmission
            .getLeftRearEncoderDistance();
    this.prevRightDistance = this.transmission
            .getRightRearEncoderDistance();
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
    double speed = ((this.transmission.getRightRearEncoderDistance() -
            this.prevRightDistance) / 2) / (Hardware.kilroyTimer.get() -
                    this.prevTime);

    this.prevRightDistance = this.transmission
            .getRightRearEncoderDistance();
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
    double speed = ((this.transmission.getLeftRearEncoderDistance() -
            this.prevLeftDistance) / 2) / (Hardware.kilroyTimer.get() -
                    this.prevTime);

    this.prevLeftDistance = this.transmission
            .getLeftRearEncoderDistance();
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
    double rotationalVelocity = ((Math
            .abs(this.transmission.getLeftRearEncoderDistance()) +
            Math.abs(
                    this.transmission
                            .getRightRearEncoderDistance())
                    /
                    2
            - ((Math.abs(this.prevLeftDistance) +
                    Math.abs(this.prevRightDistance)) /
                    2) / (Hardware.kilroyTimer.get() -
                            this.prevTime)));

    this.prevLeftDistance = this.transmission
            .getLeftRearEncoderDistance();
    this.prevRightDistance = this.transmission
            .getRightRearEncoderDistance();
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
    if (this.transmission
            .getRightRearEncoderDistance() >= targetDistance ||
            this.transmission
                    .getLeftRearEncoderDistance() >= targetDistance)
        {
        // we're done
        return true;
        }
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
    this.defaultTurnSpeed = Math.min(1.0, Math.max(0.0, newTurnSpeed));
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
        setDrivingCorrectionFactor (final double newCorrectionFactor)
{
    // -----------------------------
    // make sure that the new correction factor for
    // normal forward driving is between 0 and 1.0
    // -----------------------------
    this.drivingCorrectionFactor = Math.min(1.0,
            Math.max(0.0, newCorrectionFactor));
    return (this.getDrivingCorrectionFactor());
} // end setDrivingCorrectionFactor()

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
 * Sets the minimum speed our motors should go. Used in Autonomous/Teleop Init.
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
        if (this.transmission
                .getRightRearEncoderDistance() <= -(turnInRadians *
                        this.ROBOT_TURNING_RADIUS)
                ||
                this.transmission
                        .getLeftRearEncoderDistance() >= (turnInRadians
                                *
                                this.ROBOT_TURNING_RADIUS))
            {
            // brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (this.brake(this.brakeSpeed));
                }
            return true;
            }
        }
    // ----------------------------------------
    // we are turning left
    // ----------------------------------------
    else
        {
        if (this.transmission
                .getRightRearEncoderDistance() >= (turnInRadians *
                        this.ROBOT_TURNING_RADIUS)
                ||
                this.transmission
                        .getLeftRearEncoderDistance() <= -(turnInRadians
                                *
                                this.ROBOT_TURNING_RADIUS))
            {
            // brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (this.brake(this.brakeSpeed));
                }
            return true;
            }
        }
    // turn the robot
    this.transmission.controls(
            compensateForReversedLeftJoystick(leftJoystickInputValue),
            compensateForReversedRightJoystick(
                    rightJoystickInputValue));
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
            this.getNormalizedTurnSpeed(-this.getDefaultTurnSpeed()),
            this.getNormalizedTurnSpeed(this.getDefaultTurnSpeed())));
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
    return (this.turnLeftDegrees(degrees, true, leftJoystickInputValue,
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
        final boolean brakeAtEnd, final double leftJoystickInputValue,
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
            this.getNormalizedTurnSpeed(-this.getDefaultTurnSpeed())));
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
    return (this.turnRightDegrees(degrees, true, leftJoystickInputValue,
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
        final boolean brakeAtEnd, final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnByDegrees(turnWhichWay.TURN_RIGHT, degrees,
            brakeAtEnd, leftJoystickInputValue,
            rightJoystickInputValue));
} // end turnRightDegrees()

public boolean alignByCamera (double percentageDeadBand,
        double correctionSpeed)
{
    if (isAligningByCamera == false)
        {
        //say we've started
        isAligningByCamera = true;
        //actually start
        this.cameraTimer.reset();
        this.cameraTimer.start();
        //turn down the lights
        Hardware.axisCamera.writeBrightness(
                Hardware.MINIMUM_AXIS_CAMERA_BRIGHTNESS);
        //Woah, that's too dark! Turn on the ringlight someone!
        Hardware.ringLightRelay.set(Value.kOn);
        return false;
        }
    //If we claim to be driving by camera and we've waitied long enough 
    //for someone to brighten up the darkness with the ringlight
    if (isAligningByCamera == true && Hardware.delayTimer.get() >= .75)
        {
        //try to take a picture and save it in memory and on the "hard disk"
        try
            {
            Hardware.imageProcessor
                    .updateImage(Hardware.axisCamera.getImage());
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
        if (Hardware.imageProcessor
                .getParticleAnalysisReports().length > 0
                && getRelativeCameraCoordinate(Hardware.imageProcessor
                        .getParticleAnalysisReports()[0].center_mass_x,
                        true) <= -percentageDeadBand)
            {
            //turn left until it is in the zone (will be called over and
            //over again until the blob is within the acceptable zone)
            Hardware.transmission.controls(-correctionSpeed,
                    correctionSpeed);
            }
        //if the center of the largest blob is to the right of our 
        //acceptable zone around the center
        else if (Hardware.imageProcessor
                .getParticleAnalysisReports().length > 0
                && getRelativeCameraCoordinate(Hardware.imageProcessor
                        .getParticleAnalysisReports()[0].center_mass_x,
                        true) >= percentageDeadBand)
            {
            //turn left until it is in the zone (will be called over and
            //over again until the blob is within the acceptable zone)
            Hardware.transmission.controls(correctionSpeed,
                    -correctionSpeed);
            }
        //If the center of the blob is nestled happily in our deadzone
        else
            {
            //We're done, no need to go again.
            isAligningByCamera = false;
            //Stop moving
            Hardware.transmission.controls(0.0, 0.0);
            return true;
            }
        }
    else
        {
        return false;
        }
    return false;

}//end alignByCamera()

public boolean alignByCamera (double percentageDeadBand)
{
    //I've decided .45 is a fair correction speed, can tweak later if need be.
    return alignByCamera(percentageDeadBand, .45);
}

public boolean alignByCamera ()
{
    //I've decided 10% is a fair deadband range for general alignment, can tweak later if need be.
    return alignByCamera(.1, .45);
}


public double getRelativeCameraCoordinate (
        double absoluteCoordinate,
        boolean isXCoordinate)
{
    if (isXCoordinate == true)
        return (absoluteCoordinate - (cameraXResolution / 2))
                / (cameraXResolution / 2);
    return (absoluteCoordinate - (cameraYResolution / 2))
            / (cameraYResolution / 2);
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
private final double ROBOT_TURNING_RADIUS = 12.0;

private Transmission_old transmission = null;

private KilroyCamera camera = null;

private Relay ringLightRelay = null;

private final Timer cameraTimer = new Timer();

private boolean isAligningByCamera = false;

private double cameraXResolution;

private double cameraYResolution;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

// TODO tweak for the most effective brake method
private double brakeSpeed = .1;

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
private double drivingCorrectionFactor = 0.75;

// -------------------------------------
// minimum motor speed allowed. This represents
// the forward values from the joysticks and
// ranges from 0.0 to 1.0 (100%) and will be
// calculated based on whether or not the transmission
// has reversed joysticks
// ------------------------------------
private double minimumMotorSpeed = .20;

} // end class
