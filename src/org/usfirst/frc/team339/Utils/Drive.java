package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission;
import edu.wpi.first.wpilibj.Encoder;

// TODO: COMMENT YOUR CODE!!!!!!!!!!!
public class Drive
{

/**
 * The scaling factor upon which all speeds will be scaled.
 */
private double maxSpeedScalingFactor = 1.0;

public Drive (Transmission transmission, Encoder rightRearEncoder,
        Encoder rightFrontEncoder, Encoder leftRearEncoder,
        Encoder leftFrontEncoder)
{
    this.transmission = transmission;
    this.rightRearEncoder = rightRearEncoder;
    this.rightFrontEncoder = rightFrontEncoder;
    this.leftRearEncoder = leftRearEncoder;
    this.leftFrontEncoder = leftFrontEncoder;
    this.isFourWheel = true;
}

public Drive (Transmission transmission, Encoder rightEncoder,
        Encoder leftEncoder)
{
    this.rightRearEncoder = rightEncoder;
    this.leftRearEncoder = leftEncoder;
    this.isFourWheel = false;
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

public boolean turnLeftDegrees (double degrees)
{
    double turnInRadians = Math.toRadians(degrees);
    boolean turningRight = turnInRadians < 0;
    if (turningRight && rightRearEncoder.getDistance() >= Math
            .abs(turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES)
            || leftRearEncoder.getDistance() <= -Math.abs(
                    turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES))
        {
        transmission.drive(0.0, 0.0);
        return true;
        }
    else if (rightRearEncoder.getDistance() >= -Math
            .abs(turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES)
            ||
            leftRearEncoder.getDistance() <= Math.abs(
                    turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES))
        {
        transmission.drive(0.0, 0.0);
        return true;
        }
    else
        {
        if (!turningRight)
            transmission.drive(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    -(maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
        else
            transmission.drive(
                    -(maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
        }
    return false;
}

public boolean driveForwardInches (double distance)
{
    if (isFourWheel && (rightRearEncoder.getDistance()
            + rightFrontEncoder.getDistance())
            / 2 >= distance ||
            leftRearEncoder.getDistance()
                    + leftFrontEncoder.getDistance() / 2 >= distance)
        {
        transmission.drive(0.0, 0.0);
        return true;
        }
    else if (rightRearEncoder.getDistance() >= distance
            || leftRearEncoder.getDistance() >= distance)
        {
        transmission.drive(0.0, 0.0);
        return true;
        }
    else
        {
        // if the right drive train is ahead of the left drive train (on a
        // four wheel drive)
        if (isFourWheel
                && (rightRearEncoder.get() + rightFrontEncoder.get())
                        / 2 > (leftRearEncoder.get()
                                + leftFrontEncoder.get()) / 2)
            {
            transmission.drive(AUTO_CORRECTION_SPEED,
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
            }
        // if the left drive train is ahead of the right drive train (one a
        // four wheel drive)
        else if (isFourWheel
                && (rightRearEncoder.get() + rightFrontEncoder.get())
                        / 2 < (leftRearEncoder.get()
                                + leftFrontEncoder.get()) / 2)
            {
            transmission.drive(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    AUTO_CORRECTION_SPEED);
            }
        // if the right Drive train is ahead of the left drive train (2
        // motor)
        else if (rightRearEncoder.get() > leftRearEncoder.get())
            {
            transmission.drive(AUTO_CORRECTION_SPEED,
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
            }
        // if the left Drive train is ahead of the right drive train (2
        // motor)
        else if (rightRearEncoder.get() < leftRearEncoder.get())
            {
            transmission.drive(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    AUTO_CORRECTION_SPEED);
            }
        // if they're both equal
        else
            {
            transmission.drive(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED));
            }
        }
    return false;
}



/*
 * public boolean brake (final double brakeVoltage)
 * {
 * 
 * // UNDER PENALTY OF DEATH - don't use without calling initEncoders()
 * // AND setting the distancePerPulse on them via
 * // setEncodersDistancePerPulse()
 * // in that order.
 * 
 * if ((this.leftMotorEncoder == null) ||
 * (this.oneOrRightMotorEncoder == null))
 * return true; // just stop if we don't even have encoders.
 * 
 * // If the current distance on all of our encoders is
 * // close enough to the previous values
 * //
 * // OR
 * //
 * // the measurement before THAT is greater than our
 * // previous measurement, indicating we go backwards...
 * //
 * // then we're done and we stop the motors.
 * if (((Math.abs(this.leftMotorEncoder.getDistance()) >=
 * (this.brakePreviousDistanceL - this.AUTO_ENCODER_THRESHOLD_INCHES)) &&
 * (Math.abs(this.leftMotorEncoder.getDistance()) <=
 * (this.brakePreviousDistanceL + this.AUTO_ENCODER_THRESHOLD_INCHES)) &&
 * (Math.abs(this.leftMotorEncoder.getDistance()) >=
 * (this.brakePreviousPreviousDistanceL - this.AUTO_ENCODER_THRESHOLD_INCHES))
 * &&
 * (Math.abs(this.leftMotorEncoder.getDistance()) <=
 * (this.brakePreviousPreviousDistanceL + this.AUTO_ENCODER_THRESHOLD_INCHES))
 * &&
 * (Math.abs(this.oneOrRightMotorEncoder.getDistance()) >=
 * (this.brakePreviousDistanceR - this.AUTO_ENCODER_THRESHOLD_INCHES)) &&
 * (Math.abs(this.oneOrRightMotorEncoder.getDistance()) <=
 * (this.brakePreviousDistanceR + this.AUTO_ENCODER_THRESHOLD_INCHES)) &&
 * (Math.abs(this.oneOrRightMotorEncoder.getDistance()) >=
 * (this.brakePreviousPreviousDistanceR - this.AUTO_ENCODER_THRESHOLD_INCHES))
 * && (Math
 * .abs(this.oneOrRightMotorEncoder.getDistance()) <=
 * (this.brakePreviousPreviousDistanceR + this.AUTO_ENCODER_THRESHOLD_INCHES)))
 * ||
 * ((this.brakePreviousPreviousDistanceL >= this.brakePreviousDistanceL) &&
 * (this.brakePreviousPreviousDistanceR >= this.brakePreviousDistanceR) &&
 * (this.brakePreviousDistanceL >= this.leftMotorEncoder
 * .getDistance()) && (this.brakePreviousDistanceR >=
 * this.oneOrRightMotorEncoder
 * .getDistance())))
 * {
 * // System.out.println("DONE!");
 * this.brakePreviousDistanceL = 0.0;
 * this.brakePreviousDistanceR = 0.0;
 * this.brakePreviousPreviousDistanceL = 0.0;
 * this.brakePreviousPreviousDistanceR = 0.0;
 * return true;
 * } // if
 * else
 * {
 * this.brakePreviousPreviousDistanceR = this.brakePreviousDistanceR;
 * this.brakePreviousPreviousDistanceL = this.brakePreviousDistanceL;
 * this.brakePreviousDistanceR =
 * Math.abs(this.oneOrRightMotorEncoder.getDistance());
 * this.brakePreviousDistanceL =
 * Math.abs(this.leftMotorEncoder.getDistance());
 * // continue braking
 * // if we are in mecanum, call the appropriate method to
 * // send the braking voltage backwards.
 * if (this.isMecanumDrive() == true)
 * {
 * this.controls(brakeVoltage, 180.0, //
 * 0.0);
 * }
 * else
 * {
 * // otherwise, use our 2 or 4 wheel braking method.
 * this.controls(brakeVoltage, brakeVoltage);
 * }
 * return false;
 * }
 * } // end brake
 */

private static final double AUTO_CORRECTION_SPEED = 0.95;

private static final double ROBOT_SEMI_MAJOR_RADIUS_INCHES = 12.0;

private boolean isFourWheel = true;

private Encoder rightRearEncoder;

private Encoder rightFrontEncoder;

private Encoder leftRearEncoder;

private Encoder leftFrontEncoder;

private Transmission transmission;

/*
 * Constants
 */

private final double DEFAULT_MAX_SPEED = 1.0;

}
