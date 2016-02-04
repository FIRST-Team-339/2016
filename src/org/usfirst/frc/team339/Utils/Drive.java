<<<<<<< HEAD
package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.TransmissionFourWheel;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

// TODO: COMMENT YOUR CODE!!!!!!!!!!!
public class Drive
{

//Moved maxSpeedScaling factor to bottom with the rest of the variables
//if there's a reason for moving it back, do so, and leave justification
//so it doesn't get moved back down again
//-Alex Kneipp
public Drive (TransmissionFourWheel transmission,
        Encoder rightRearEncoder,
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

public Drive (TransmissionFourWheel transmission, Encoder rightEncoder,
        Encoder leftEncoder)
{
    this.rightRearEncoder = rightEncoder;
    this.leftRearEncoder = leftEncoder;
    this.isFourWheel = false;
}

public Drive(Transmission_old transmission, SpeedController rightRearEncoder, 
        SpeedController rightFrontEncoder,SpeedController leftRearEncoder, 
        SpeedController leftFrontEncoder)
{
    this
}

/**
 * Stops the robot actively.
 * 
 * @param brakeSpeed
 *            The speed with which to brake, recommended to be rather low.
 * @return true if we're done braking, false otherwise.
 */
public boolean brake (double brakeSpeed)
{
    //if both forward velocity and rotational velocity are outside a .1
    //threshold of 0, we're not done yet, keep driving reversal
    if (Math.abs(getForwardVelocity()) + .1 >= 0 &&
            Math.abs(getRotationalVelocity()) + .1 >= 0)
        {
        transmission.drive(-brakeSpeed * getRightMotorVelocity() /
                (Math.abs(getRightMotorVelocity())),
                -brakeSpeed * getLeftMotorVelocity() /
                        (Math.abs(getLeftMotorVelocity())));
        return false;
        }
    //we're done braking
    else
        {
        transmission.drive(0.0, 0.0);
        return true;
        }
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
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else if (rightRearEncoder.getDistance() >= -Math
            .abs(turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES)
            ||
            leftRearEncoder.getDistance() <= Math.abs(
                    turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES))
        {
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
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
    //stop if the average value of either drive train is greater than
    //the desired distance traveled.
    if (isFourWheel && (rightRearEncoder.getDistance()
            + rightFrontEncoder.getDistance())
            / 2 >= distance ||
            leftRearEncoder.getDistance()
                    + leftFrontEncoder.getDistance() / 2 >= distance)
        {
        //stop
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else if (!isFourWheel && rightRearEncoder.getDistance() >= distance
            || leftRearEncoder.getDistance() >= distance)
        {
        //Stop
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
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
        // if the left drive train is ahead of the right drive train (on a
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

/**
 * Gets forward velocity based on the difference in distance over the difference
 * in time from the last time the method was called.
 * 
 * @return
 */
public double getForwardVelocity ()
{
    double speed = (((leftRearEncoder.getDistance()
            + rightRearEncoder.getDistance()) / 2
            - (prevLeftDistance + prevRightDistance) / 2))
            / (Hardware.kilroyTimer.get() - prevTime);

    prevLeftDistance = leftRearEncoder.getDistance();
    prevRightDistance = leftRearEncoder.getDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

/**
 * Gets the velocity of the right rear motor in a two motor drive train
 * 
 * @return
 */
public double getRightMotorVelocity ()
{
    //based on the "getForwardVelocity()" method
    double speed = ((rightRearEncoder.getDistance() -
            prevRightDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevLeftDistance = leftRearEncoder.getDistance();
    prevRightDistance = leftRearEncoder.getDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

/**
 * Gets the velocity of the left rear motor in a two motor drive train
 * 
 * @return
 */
public double getLeftMotorVelocity ()
{
    //based on the "getForwardVelocity()" method
    double speed = ((leftRearEncoder.getDistance() -
            prevLeftDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevLeftDistance = leftRearEncoder.getDistance();
    prevRightDistance = leftRearEncoder.getDistance();
    prevTime = Hardware.kilroyTimer.get();

    return speed;
}

/**
 * Sets a desired speed at which to drive forwards, for which we will correct.
 * 
 * @param desiredVelocity
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
 */
public double getRotationalVelocity ()
{
    double rotationalVelocity =
            ((Math.abs(leftRearEncoder.getDistance())
                    + Math.abs(rightRearEncoder.getDistance())) / 2
                    - ((Math.abs(prevLeftDistance)
                            + Math.abs(prevRightDistance)) / 2)
                            / (Hardware.kilroyTimer.get() - prevTime));

    prevLeftDistance = leftRearEncoder.getDistance();
    prevRightDistance = leftRearEncoder.getDistance();
    prevTime = Hardware.kilroyTimer.get();

    return rotationalVelocity;
}

private static final double AUTO_CORRECTION_SPEED = 0.95;

private static final double ROBOT_SEMI_MAJOR_RADIUS_INCHES = 12.0;

private boolean isFourWheel = true;

private Encoder rightRearEncoder;

private Encoder rightFrontEncoder;

private Encoder leftRearEncoder;

private Encoder leftFrontEncoder;

private TransmissionFourWheel transmission;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

private double desiredForwardVelocity = 0.0;

/**
 * The scaling factor upon which all speeds will be scaled.
 */
private double maxSpeedScalingFactor = 1.0;

/*
 * Constants
 */

private final double DEFAULT_MAX_SPEED = 1.0;

//TODO tweak for the most effective brake method
private final double BRAKE_SPEED = .1;

}
=======
package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.transmission.Transmission_old;
import edu.wpi.first.wpilibj.SpeedController;

// TODO: COMMENT YOUR CODE!!!!!!!!!!!
// It was mostly commented, just missing javadoc. Learn to read green!
public class Drive
{
//TODO prefix the transmissions throughout the class with "this"
//Moved maxSpeedScaling factor to bottom with the rest of the variables
//if there's a reason for moving it back, do so, and leave justification
//so it doesn't get moved back down again
//-Alex Kneipp
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
public Drive (Transmission_old transmission,
        SpeedController rightRearMotor,
        SpeedController rightFrontMotor, SpeedController leftRearMotor,
        SpeedController leftFrontMotor)
{
    this.transmission = transmission;
    this.rightRearMotor = rightRearMotor;
    this.rightFrontMotor = rightFrontMotor;
    this.leftRearMotor = leftRearMotor;
    this.leftFrontMotor = leftFrontMotor;
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
    //TODO maybe make argument a constant in the class.
    //TODO find out ideal brakespeed.
    if (this.transmission.brake(brakeSpeed))
        return true;
    else
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
    if (turningRight
            && transmission.getRightRearEncoderDistance() >= Math
                    .abs(turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES)
            || transmission.getLeftRearEncoderDistance() <= -Math.abs(
                    turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES))
        {
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else if (transmission.getRightRearEncoderDistance() >= -Math
            .abs(turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES)
            ||
            transmission.getLeftRearEncoderDistance() <= Math.abs(
                    turnInRadians * ROBOT_SEMI_MAJOR_RADIUS_INCHES))
        {
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else
        {
        if (!turningRight)
            transmission.controls(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    -(maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
        else
            transmission.controls(
                    -(maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
        }
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
    //stop if the average value of either drive train is greater than
    //the desired distance traveled.
    if (isFourWheel && (transmission.getRightRearEncoderDistance()
            + transmission.getRightFrontEncoderDistance())
            / 2 >= distance ||
            transmission.getLeftRearEncoderDistance()
                    + transmission.getLeftFrontEncoderDistance()
                            / 2 >= distance)
        {
        //stop
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else if (!isFourWheel
            && transmission.getRightRearEncoderDistance() >= distance
            || transmission.getLeftRearEncoderDistance() >= distance)
        {
        //Stop
        if (brake(BRAKE_SPEED))
            {
            return true;
            }
        return false;
        }
    else
        {
        // if the right drive train is ahead of the left drive train (on a
        // four wheel drive)
        if (isFourWheel
                && (transmission.getRightRearEncoderDistance()
                        + transmission.getRightFrontEncoderDistance())
                        / 2 > (transmission.getLeftRearEncoderDistance()
                                + transmission
                                        .getLeftFrontEncoderDistance())
                                / 2)
            {
            transmission.controls(AUTO_CORRECTION_SPEED,
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
            }
        // if the left drive train is ahead of the right drive train (on a
        // four wheel drive)
        else if (isFourWheel
                && (transmission.getRightRearEncoderDistance()
                        + transmission.getRightFrontEncoderDistance())
                        / 2 < (transmission
                                .getRightRearEncoderDistance()
                                + transmission
                                        .getRightFrontEncoderDistance())
                                / 2)
            {
            transmission.controls(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    AUTO_CORRECTION_SPEED,
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
            }
        // if the right Drive train is ahead of the left drive train (2
        // motor)
        else if (transmission
                .getRightRearEncoderDistance() > transmission
                        .getLeftRearEncoderDistance())
            {
            transmission.controls(AUTO_CORRECTION_SPEED,
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
            }
        // if the left Drive train is ahead of the right drive train (2
        // motor)
        else if (transmission
                .getRightRearEncoderDistance() < transmission
                        .getLeftRearEncoderDistance())
            {
            transmission.controls(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    AUTO_CORRECTION_SPEED,
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
            }
        // if they're both equal
        else
            {
            transmission.controls(
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    (maxSpeedScalingFactor * DEFAULT_MAX_SPEED),
                    this.leftFrontMotor, this.leftRearMotor,
                    this.rightFrontMotor, this.rightRearMotor);
            }
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
    double speed = (((transmission.getLeftRearEncoderDistance()
            + transmission.getRightRearEncoderDistance()) / 2
            - (prevLeftDistance + prevRightDistance) / 2))
            / (Hardware.kilroyTimer.get() - prevTime);

    prevLeftDistance = transmission.getLeftRearEncoderDistance();
    prevRightDistance = transmission.getRightRearEncoderDistance();
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
    //based on the "getForwardVelocity()" method
    double speed = ((transmission.getRightRearEncoderDistance() -
            prevRightDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevRightDistance = transmission.getRightRearEncoderDistance();
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
    //based on the "getForwardVelocity()" method
    double speed = ((transmission.getLeftRearEncoderDistance() -
            prevLeftDistance) / 2) / (Hardware.kilroyTimer.get() -
                    prevTime);

    prevLeftDistance = transmission.getLeftRearEncoderDistance();
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
    double rotationalVelocity =
            ((Math.abs(transmission.getLeftRearEncoderDistance())
                    + Math.abs(
                            transmission.getRightRearEncoderDistance())
                            / 2
                    - ((Math.abs(prevLeftDistance)
                            + Math.abs(prevRightDistance)) / 2)
                            / (Hardware.kilroyTimer.get() - prevTime)));

    prevLeftDistance = transmission.getLeftRearEncoderDistance();
    prevRightDistance = transmission.getRightRearEncoderDistance();
    prevTime = Hardware.kilroyTimer.get();

    return rotationalVelocity;
}

private static final double AUTO_CORRECTION_SPEED = 0.95;

private static final double ROBOT_SEMI_MAJOR_RADIUS_INCHES = 12.0;

private boolean isFourWheel = true;

//Commented out because they should be included in the transmission class
//
//private Encoder rightRearEncoder;
//
//private Encoder rightFrontEncoder;
//
//private Encoder leftRearEncoder;
//
//private Encoder leftFrontEncoder;
//
private Transmission_old transmission;

private SpeedController rightRearMotor;
private SpeedController rightFrontMotor;
private SpeedController leftRearMotor;
private SpeedController leftFrontMotor;

private double prevTime = 0.0;
private double prevLeftDistance = 0.0;
private double prevRightDistance = 0.0;

private double desiredForwardVelocity = 0.0;

/**
 * The scaling factor upon which all speeds will be scaled.
 */
private double maxSpeedScalingFactor = 1.0;

/*
 * Constants
 */

private final double DEFAULT_MAX_SPEED = 1.0;

//TODO tweak for the most effective brake method
private final double BRAKE_SPEED = .1;

}
>>>>>>> branch 'master' of https://github.com/FIRST-Team-339/2016.git
