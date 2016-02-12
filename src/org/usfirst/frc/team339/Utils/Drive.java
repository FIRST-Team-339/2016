
package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;


public class Drive
{
/**
 * Constructor for a Drive object. Should only be called once.
 * 
 * @param transmission
 *            Transmission object the class uses to control the motors.
 * @param rightRearMotor
 *            Motor controller used to drive.
 * @param rightFrontMotor
 *            Motor controller used to drive.
 * @param leftRearMotor
 *            Motor controller used to drive.
 * @param leftFrontMotor
 *            Motor controller used to drive.
 * @author Alex Kneipp
 */
public Drive (Transmission_old transmission)
{
    this.transmission = transmission;
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
public boolean brake (final double brakeSpeed)
{
    // TODO maybe make argument a constant in the class.
    // TODO find out ideal brakespeed.
    if (this.transmission.brake(brakeSpeed))
        {
        return true;
        }
    return false;
} // end brake()

/**
 * Drives forward forever (almost).
 * (calls driveForwardInches(9999, false,
 * maxSpeedScalingFactor (-1), maxSpeedScalingFactor (-1)))
 * 
 * @author Robert Brown
 */
public void driveContinuous ()
{
    this.driveForwardInches(9999.0, false,
            this.maxSpeedScalingFactor, this.maxSpeedScalingFactor);
} // end driveContinuous()

/**
 * Drives forward forever (almost).
 * (calls driveForwardInches(9999, false,
 * leftJoystickInputValue, rightJoystickInputValue))
 * 
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @author Robert Brown
 */
public void driveContinuous (
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)

{
    this.driveForwardInches(9999.0, false,
            leftJoystickInputValue, rightJoystickInputValue);
} // end driveContinuous()

/**
 * Drives forward distance inches with correction.
 * (calls driveForwardInches(distance, true,
 * maxSpeedScalingFactor (-1), maxSpeedScalingFactor (-1)))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @return True if done driving, false otherwise.
 * @author Alex Kneipp
 */
public boolean driveForwardInches (final double distance)
{
    return (this.driveForwardInches(distance, true,
            this.maxSpeedScalingFactor, this.maxSpeedScalingFactor));
} // end driveForwardInches()

/**
 * Drives forward distance inches with correction.
 * (calls driveForwardInches(distance, brakeAtEnd,
 * maxSpeedScalingFactor (-1), maxSpeedScalingFactor (-1)))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param brakeAtEnd
 *            - true - brake when finished
 *            false - don't brake
 * @return True if done driving, false otherwise.
 * @author Alex Kneipp
 */
public boolean driveForwardInches (final double distance,
        final boolean brakeAtEnd)
{
    return (this.driveForwardInches(distance, brakeAtEnd,
            this.maxSpeedScalingFactor, this.maxSpeedScalingFactor));
} // end driveForwardInches()

/**
 * Drives forward distance inches with correction.
 * (calls driveForwardInches(distance, true,
 * leftJoystickInputValue, rightJoystickInputValue))
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Alex Kneipp
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
 *            - true - brake when finished
 *            false - don't brake
 * @param leftJoystickInputValue
 *            - value of speed to drive left motors
 * @param rightJoystickInputValue
 *            - value of speed to drive right motors
 * @return True if done driving, false otherwise.
 * @author Alex Kneipp
 */
//TODO make correction progressive.  i.e. the further off we get, the more we correct.  Use encoder differences for delta value
public boolean driveForwardInches (final double distance,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    // stop if the average value of either drive train is greater than
    // the desired distance traveled.
    if (this.hasDrivenInches(distance) == true)
        {
        // if requested to brake, stop
        if (brakeAtEnd == true)
            {
            return (this.brake(this.BRAKE_SPEED));
            }
        return true;
        }
    // if we are presently going straight - keep the
    // speeds equal
    if (this.transmission
            .getRightRearEncoderDistance() == this.transmission
                    .getRightFrontEncoderDistance())
        this.transmission.controls(
                (leftJoystickInputValue * this.maxSpeedScalingFactor),
                (rightJoystickInputValue * this.maxSpeedScalingFactor));
    // if the left drive train is ahead of the right drive train (on a
    // four wheel drive)
    else if ((this.transmission
            .getRightRearEncoderDistance()) < (this.transmission
                    .getLeftRearEncoderDistance()))
        this.transmission.controls(
                leftJoystickInputValue * this.maxSpeedScalingFactor
                        * this.CORRECTION_FACTOR,
                (rightJoystickInputValue * this.maxSpeedScalingFactor));
    // if the right drive train is ahead of the left drive train (on a
    // four wheel drive)
    else
        this.transmission.controls(
                (leftJoystickInputValue * this.maxSpeedScalingFactor),
                rightJoystickInputValue * this.maxSpeedScalingFactor
                        * this.CORRECTION_FACTOR);
    return false;
} // end driveForwardInches()

/**
 * returns the brake speed when we execute a braking maneuver
 * 
 * @return the present brake speed
 * @author Robert Brown
 */
public double getBrakeSpeed ()
{
    return this.BRAKE_SPEED;
} // end getBrakeSpeed()

/**
 * Gets forward velocity based on the difference in distance over the difference
 * in time from the last time the method was called.
 * 
 * @return The current velocity of the robot.
 * @author Michael Andrzej Klaczynski
 */
public double getForwardVelocity ()
{
    double speed = (((this.transmission.getLeftRearEncoderDistance()
            + this.transmission.getRightRearEncoderDistance()) / 2
            - (this.prevLeftDistance + this.prevRightDistance) / 2))
            / (Hardware.kilroyTimer.get() - this.prevTime);

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
    double speed = ((this.transmission.getRightRearEncoderDistance() -
            this.prevRightDistance) / 2) / (Hardware.kilroyTimer.get() -
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
    double speed = ((this.transmission.getLeftRearEncoderDistance() -
            this.prevLeftDistance) / 2) / (Hardware.kilroyTimer.get() -
                    this.prevTime);

    this.prevLeftDistance =
            this.transmission.getLeftRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return speed;
} // end getLeftMotorVelocity()

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
            .abs(this.transmission.getLeftRearEncoderDistance())
            + Math.abs(
                    this.transmission.getRightRearEncoderDistance())
                    / 2
            - ((Math.abs(this.prevLeftDistance)
                    + Math.abs(this.prevRightDistance)) / 2)
                    / (Hardware.kilroyTimer.get() - this.prevTime)));

    this.prevLeftDistance =
            this.transmission.getLeftRearEncoderDistance();
    this.prevRightDistance =
            this.transmission.getRightRearEncoderDistance();
    this.prevTime = Hardware.kilroyTimer.get();

    return rotationalVelocity;
} // end getRotationalVelocity()

/**
 * Checks to see if we have driven a certain distance since the last time the
 * encoders were reset.
 * 
 * @param targetDistance
 *            The distance to check our actual distance against.
 * @return
 *         True if we have driven targetDistance inches, false otherwise.
 * @author Alex Kneipp
 */
public boolean hasDrivenInches (final double targetDistance)
{
    // if either drive train is beyond the targetDistance
    if (this.transmission
            .getRightRearEncoderDistance() >= targetDistance ||
            this.transmission
                    .getLeftRearEncoderDistance() >= targetDistance)
        {
        //we're done
        return true;
        }
    return false;
} // end hasDrivenInches()

/**
 * returns the brake speed when we execute a braking maneuver
 * that we just set it to
 * 
 * @param newBrakeSpeed
 * @return the new brake speed
 * @author Robert Brown
 */
public double setBrakeSpeed (final double newBrakeSpeed)
{
    return (this.BRAKE_SPEED = newBrakeSpeed);
} // end setBrakeSpeed()

public double getMaxSpeed ()
{
    return this.maxSpeedScalingFactor;
}

/**
 * Sets maximum speed.
 * Used in Autonomous/Teleop Init.
 * 
 * @author Michael Andrzej Klaczynski
 * @param max
 *            is a double between 0.0 and 1.0
 */
public void setMaxSpeed (final double max)
{
    this.maxSpeedScalingFactor = max;
} // end setMaxSpeed()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end.
 * 
 * @param whichWay
 *            - enum which represents either TURN_RIGHT or
 *            TURN_LEFT (enum type = turnWhichWay)
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
 * @author Alex Kneipp
 */
public boolean turnByDegrees (final turnWhichWay whichWay,
        final double degrees,
        final boolean brakeAtEnd,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    //-----------------------------------------
    // Make sure that the degrees requested stays
    // within 0-180
    //-----------------------------------------
    double turnDegrees = Math.min(Math.max(degrees, 0.0), 180);

    double turnInRadians = Math.toRadians(degrees);

    //----------------------------------------
    // are we turning right
    //----------------------------------------
    if (whichWay == turnWhichWay.TURN_RIGHT)
        {
        if (this.transmission
                .getRightRearEncoderDistance() <= -(turnInRadians
                        * this.ROBOT_TURNING_RADIUS)
                || this.transmission
                        .getLeftRearEncoderDistance() >= (turnInRadians
                                * this.ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (this.brake(this.BRAKE_SPEED));
                }
            return true;
            }
        //drive the robot, right train backwards, left train forwards
        this.transmission.controls(
                -(this.maxTurningSpeedScalingFactor
                        * leftJoystickInputValue),
                (this.maxTurningSpeedScalingFactor
                        * rightJoystickInputValue));
        }
    //----------------------------------------
    // we are turning left
    //----------------------------------------
    else
        {
        if (this.transmission
                .getRightRearEncoderDistance() >= (turnInRadians
                        * this.ROBOT_TURNING_RADIUS)
                ||
                this.transmission
                        .getLeftRearEncoderDistance() <= -(turnInRadians
                                * this.ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (this.brake(this.BRAKE_SPEED));
                }
            return true;
            }
        //drive the robot, right train forwards, left train backwards
        this.transmission.controls(
                (this.maxTurningSpeedScalingFactor
                        * leftJoystickInputValue),
                -(this.maxTurningSpeedScalingFactor
                        * rightJoystickInputValue));
        }
    //We're not done driving yet!!
    return false;
} // end turnByDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end. Assumed to be true.
 * CALLS turnLeftDegrees(degrees, true)
 * 
 * @param degrees
 *            The number of degrees to turn left. Range is from 0-180.
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnLeftDegrees (final double degrees)
{
    return (this.turnLeftDegrees(degrees, true));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end.
 * Calls turnLeftDegrees (degrees, brakeAtEnd,
 * maxSpeedScalingFactor (-1), maxSpeedScalingFactor (-1))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnLeftDegrees (final double degrees,
        final boolean brakeAtEnd)
{
    return (this.turnLeftDegrees(degrees, brakeAtEnd,
            this.maxSpeedScalingFactor, this.maxSpeedScalingFactor));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end. Assumed to be true.
 * Calls turnLeftDegrees (degrees, true,
 * leftJoystickInputValue, rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnLeftDegrees (final double degrees,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnLeftDegrees(degrees, true,
            leftJoystickInputValue, rightJoystickInputValue));
} // end turnLeftDegrees()

/**
 * Turns left 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end.
 * Calls turnByDegrees (turnWhichWay.TURN_LEFT, degrees, brakeAtEnd,
 * leftJoystickInputValue, rightJoystickInputValue)
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
 * @author Alex Kneipp
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
 * Turns right 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end. Assumed to be true.
 * (Just calls turnRightDegrees(degrees, true))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnRightDegrees (final double degrees)
{
    return (this.turnRightDegrees(degrees, true));
} // end turnLeftDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end.
 * Calls turnRightDegrees (degrees, brakeAtEnd,
 * maxSpeedScalingFactor (-1), maxSpeedScalingFactor (-1))
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
 */
public boolean turnRightDegrees (final double degrees,
        final boolean brakeAtEnd)
{
    return (this.turnRightDegrees(degrees, brakeAtEnd,
            this.maxSpeedScalingFactor, this.maxSpeedScalingFactor));
} // end turnRightDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end. Assumed to be true.
 * Calls turnRightDegrees (degrees, true,
 * leftJoystickInputValue, rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnRightDegrees (final double degrees,
        final double leftJoystickInputValue,
        final double rightJoystickInputValue)
{
    return (this.turnRightDegrees(degrees, true,
            leftJoystickInputValue, rightJoystickInputValue));
} // end turnLeftDegrees()

/**
 * Turns right 'degrees' degrees. Additional boolean parameter
 * from other method controls whether or not we brake at the
 * end.
 * Calls turnByDegrees (turnWhichWay.TURN_RIGHT, degrees, brakeAtEnd,
 * leftJoystickInputValue, rightJoystickInputValue)
 * 
 * @param degrees
 *            The number of degrees to turn. Range is from 0-180.
 * @param leftJoystickInputValue
 *            - input from the L joystick - simulates speed
 * @param rightJoystickInputValue
 *            - input from the R joystick - simulates speed
 * @return True if we're done turning, false otherwise.
 * @author Robert Brown
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
 * enum which describes which way to turn
 * 
 * {@value} TURN_RIGHT
 * {@value} TURN_LEFT
 * 
 * @author rbrown2
 *
 */
public enum turnWhichWay
    {
    TURN_RIGHT, TURN_LEFT;
    }

/*
 * Constants
 */

private final double DEFAULT_MAX_SPEED = 1.0;

private final double CORRECTION_FACTOR = -0.75;

// TODO - get Kilroys new turning radius
private final double ROBOT_TURNING_RADIUS = 12.0;

private boolean isFourWheel = true;

private Transmission_old transmission;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

/**
 * The scaling factor upon which all speeds will be scaled.
 */
private double maxSpeedScalingFactor = DEFAULT_MAX_SPEED;

//prevents us from turning uncontrollably
private double maxTurningSpeedScalingFactor = .5;

//TODO tweak for the most effective brake method
private double BRAKE_SPEED = .2;

} // end class
