
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
public boolean brake (double brakeSpeed)
{
    // TODO maybe make argument a constant in the class.
    // TODO find out ideal brakespeed.
    if (this.transmission.brake(brakeSpeed))
        {
        return true;
        }
    return false;
}

/**
 * Sets maximum speed.
 * Used in Autonomous/Teleop Init.
 * 
 * @author Michael Andrzej Klaczynski
 * @param max
 *            is a double between 0.0 and 1.0
 */
public void setMaxSpeed (double max)
{
    maxSpeedScalingFactor = max;
}

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
public boolean hasDrivenInches (double targetDistance)
{
    //if either drive train is beyond the targetDistance
    if (this.transmission
            .getRightRearEncoderDistance() >= targetDistance
            || this.transmission
                    .getLeftRearEncoderDistance() >= targetDistance)
        {
        //we're done
        return true;
        }
    return false;
}

/**
 * Turns left 'degrees' degrees. Negative values make it turn right.
 * 
 * @param degrees
 *            The number of degrees to turn. Positive values turn
 *            the robot left, negative ones right.
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnLeftDegrees (double degrees)
{
    double turnInRadians = Math.toRadians(degrees);
    boolean turningRight = turnInRadians < 0;
    //If we're turning right and the right drive train is above the limit or the left is below the negative limit
    if (turningRight == true)
        {
        if (this.transmission
                .getRightRearEncoderDistance() <= (turnInRadians
                        * ROBOT_TURNING_RADIUS)
                || this.transmission
                        .getLeftRearEncoderDistance() >= -(turnInRadians
                                * ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            return (brake(BRAKE_SPEED));
            }
        this.transmission.controls(
                -(maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED),
                (maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    //If we're turning left and the right drive train is below the negative limit or the left is above the limit
    else
        {
        if (this.transmission
                .getRightRearEncoderDistance() >= (turnInRadians
                        * ROBOT_TURNING_RADIUS)
                ||
                this.transmission
                        .getLeftRearEncoderDistance() <= -(turnInRadians
                                * ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            return (brake(BRAKE_SPEED));
            }
        this.transmission.controls(
                (maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED),
                -(maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    //We're not done driving yet!!
    return false;
}

/**
 * Turns left 'degrees' degrees. Negative values make it turn right. Additional
 * boolean parameter from other method controls whether or not we brake at the
 * end.
 * 
 * @param degrees
 *            The number of degrees to turn. Positive values turn
 *            the robot left, negative ones right.
 * @param brakeAtEnd
 *            True if we want to brake at the end of the turn, false if we
 *            don't.
 * @return True if we're done turning, false otherwise.
 * @author Alex Kneipp
 */
public boolean turnLeftDegrees (double degrees, boolean brakeAtEnd)
{
    double turnInRadians = Math.toRadians(degrees);
    boolean turningRight = turnInRadians < 0;
    //If we're turning right and the right drive train is above the limit or the left is below the negative limit
    if (turningRight == true)
        {
        if (this.transmission
                .getRightRearEncoderDistance() <= (turnInRadians
                        * ROBOT_TURNING_RADIUS)
                || this.transmission
                        .getLeftRearEncoderDistance() >= -(turnInRadians
                                * ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (brake(BRAKE_SPEED));
                }
            return true;
            }
        //drive the robot, right train backwards, left train forwards
        this.transmission.controls(
                -(maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED),
                (maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    //If we're turning left and the right drive train is below the negative limit or the left is above the limit
    else
        {
        if (this.transmission
                .getRightRearEncoderDistance() >= (turnInRadians
                        * ROBOT_TURNING_RADIUS)
                ||
                this.transmission
                        .getLeftRearEncoderDistance() <= -(turnInRadians
                                * ROBOT_TURNING_RADIUS))
            {
            //brake and if we're done braking, tell caller we're done
            if (brakeAtEnd == true)
                {
                return (brake(BRAKE_SPEED));
                }
            return true;
            }
        //drive the robot, right train forwards, left train backwards
        this.transmission.controls(
                (maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED),
                -(maxTurningSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    //We're not done driving yet!!
    return false;
}

/**
 * Drives forward distance inches with correction.
 * 
 * @param distance
 *            The desired distance to be traveled in inches.
 * @return True if done driving, false otherwise.
 * @author Alex Kneipp
 */
public boolean driveForwardInches (double distance)
{
    // stop if the average value of either drive train is greater than
    // the desired distance traveled.
    if (this.hasDrivenInches(distance) == true)
        {
        // stop
        if (brake(BRAKE_SPEED) == true)
            {
            return true;
            }
        return false;
        }
    // if the right drive train is ahead of the left drive train (on a
    // four wheel drive)
    if ((this.transmission
            .getRightRearEncoderDistance()) > (transmission
                    .getLeftRearEncoderDistance()))
        {
        this.transmission.controls(
                maxSpeedScalingFactor * AUTO_CORRECTION_SPEED,
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    // if the left drive train is ahead of the right drive train (on a
    // four wheel drive)
    else if ((this.transmission
            .getRightRearEncoderDistance()) < (this.transmission
                    .getLeftRearEncoderDistance()))
        {
        this.transmission.controls(
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                maxSpeedScalingFactor * AUTO_CORRECTION_SPEED);
        }
    // if the right Drive train is ahead of the left drive train (2
    // motor)
    else if (this.transmission
            .getRightRearEncoderDistance() > this.transmission
                    .getLeftRearEncoderDistance())
        {
        this.transmission.controls(
                maxSpeedScalingFactor * AUTO_CORRECTION_SPEED,
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    // if the left Drive train is ahead of the right drive train (2
    // motor)
    else if (this.transmission
            .getRightRearEncoderDistance() < this.transmission
                    .getLeftRearEncoderDistance())
        {
        this.transmission.controls(
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                maxSpeedScalingFactor * AUTO_CORRECTION_SPEED);
        }
    // if they're both equal
    else
        {
        this.transmission.controls(
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    return false;
}

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
            - (prevLeftDistance + prevRightDistance) / 2))
            / (Hardware.kilroyTimer.get() - prevTime);

    prevLeftDistance = this.transmission.getLeftRearEncoderDistance();
    prevRightDistance = this.transmission.getRightRearEncoderDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

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
            prevRightDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevRightDistance = this.transmission.getRightRearEncoderDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

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
            prevLeftDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevLeftDistance = this.transmission.getLeftRearEncoderDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

/**
 * Sets a desired speed at which to drive forwards, for which we will correct.
 * 
 * @param desiredVelocity
 * @author Michael Andrzej Klaczynski
 */
public void setForwardVelocity (double desiredVelocity)
{
    this.desiredForwardVelocity = desiredVelocity;
}

/**
 * Gets rotational velocity based on the difference in distances over the
 * difference in time from the last time the method was called.
 * 
 * @return
 * @author Michael Andrzej Klaczynski
 */
public double getRotationalVelocity ()
{
    double rotationalVelocity = ((Math
            .abs(this.transmission.getLeftRearEncoderDistance())
            + Math.abs(
                    this.transmission.getRightRearEncoderDistance())
                    / 2
            - ((Math.abs(prevLeftDistance)
                    + Math.abs(prevRightDistance)) / 2)
                    / (Hardware.kilroyTimer.get() - prevTime)));

    prevLeftDistance = this.transmission.getLeftRearEncoderDistance();
    prevRightDistance = this.transmission.getRightRearEncoderDistance();
    prevTime = Hardware.kilroyTimer.get();

    return rotationalVelocity;
}

private static final double AUTO_CORRECTION_SPEED = -0.75;

private static final double ROBOT_TURNING_RADIUS = 12.0;

private boolean isFourWheel = true;

private Transmission_old transmission;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

private double desiredForwardVelocity = 0.0;

/**
 * The scaling factor upon which all speeds will be scaled.
 */
private double maxSpeedScalingFactor = 1.0;

//prevents us from turning uncontrollably
private double maxTurningSpeedScalingFactor = .5;

/*
 * Constants
 */

private final double DEFAULT_MAX_SPEED = -1.0;

// TODO tweak for the most effective brake method
private final double BRAKE_SPEED = .2;




} // end class
