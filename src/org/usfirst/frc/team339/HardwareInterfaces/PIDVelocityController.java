
// ====================================================================
// FILE NAME: PIDVelocityController.java (Team 339 - Kilroy)
//
// CREATED ON: Feb. 11, 2014
// CREATED BY: Noah Golmant
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class acts as a PID controller when regulating a motor's
// speed based on the *rate* of the encoder instead of the
// distance. This is required because the implementation of
// a PID loop with velocity is different than a position loop.
// Instead, we *add* the calculated velocity to our current motor
// value.
// (the time derivative of the initial equation)
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

// ====================================================================
// FILE NAME: PIDVelocityController.java (Team 339 - Kilroy)
//
// CREATED ON: Feb. 11, 2014
// CREATED BY: Noah Golmant
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class acts as a PID controller when regulating a motor's
// speed based on the *rate* of the encoder instead of the
// distance. This is required because the implementation of
// a PID loop with velocity is different than a position loop.
// Instead, we *add* the calculated velocity to our current motor
// value.
// (the time derivative of the initial equation)
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================
package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 * This class acts as a PID controller when regulating a motor's
 * speed based on the *rate* of the encoder instead of the
 * distance. This is required because the implementation of
 * a PID loop with velocity is different than a position loop.
 * Instead, we *add* the calculated velocity to our current motor
 * value.
 * (the time derivative of the initial equation)
 *
 * @class PIDVelocityController
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
public class PIDVelocityController extends PIDSubsystem
{

// The motor that will be controlled by the PID controller & loop.
private final SpeedController motor;

// The encoder that acts as the input for the PID controller.
private final Encoder encoder;

// The speed of the motor to be adjusted based on
// the PID-calculated error.
private double setSpeed = 0.0;

// Min output we can send to the motor
private double minRange = -1.0;

// Max output we can send to the motor
private double maxRange = 1.0;

// whether or not we are reversed
private double motorDirection = 1.0;

private double maxOutputDelta = .25;

/**
 * Initializes the PID controller, the output motor, and the input
 * encoder without a feedforward coefficient (Kf).
 *
 * @param motor
 *            motor to use as PID output
 * @param encoder
 *            encoder to use as the PID rate input
 * @param Kp
 *            Proportional coefficient
 * @param Ki
 *            Integral coefficient
 * @param Kd
 *            Derivative coefficient
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
PIDVelocityController (final SpeedController motor,
        final Encoder encoder,
        double Kp, double Ki, double Kd)
{
    // Initialize the PIDController using the base PIDSubsystem constructor
    super(Kp, Ki, Kd);

    // Set the PID input, output, and initial speed.
    this.motor = motor;
    this.encoder = encoder;
    this.setSpeed = motor.get();
}

/**
 * Initializes the PID controller, the output motor, and the input
 * encoder with a feedforward coefficent.
 *
 * @param motor
 *            motor to use as PID output
 * @param encoder
 *            encoder to use as the PID rate input
 * @param Kp
 *            Proportional coefficient
 * @param Ki
 *            Integral coefficient
 * @param Kd
 *            Derivative coefficient
 * @param Kf
 *            Feedforward coefficient
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
public PIDVelocityController (final SpeedController motor,
        final Encoder encoder,
        double Kp, double Ki, double Kd, double Kf)
{
    super(Kp, Ki, Kd);
    this.getPIDController().setPID(Kp, Ki, Kd, Kf);
    this.motor = motor;
    this.encoder = encoder;
    this.setSpeed = motor.get();
}

/**
 * Gets the speed we're setting the motor to based on our output
 *
 * @return the current set speed
 */
public double getSetSpeed ()
{
    return this.setSpeed;
}

/**
 * Called when the subsystem is initially created as a command.
 * Has to be implemented in any Subsystem-extended class.
 *
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
@Override
protected void initDefaultCommand ()
{
}

/**
 * Checks against the maximum change we can send to the motors
 *
 * @return modified output change
 * @author Noah Golmant
 * @date Aug 19 2014
 */
protected double limit (double output)
{
    double limitedOutput = output;
    if (output < -this.maxOutputDelta)
        {
        limitedOutput = -this.maxOutputDelta;
        }
    else if (output > this.maxOutputDelta)
        {
        limitedOutput = this.maxOutputDelta;
        }

    return limitedOutput;
}

/**
 * Call to reset the PIDController object from the subsystem
 *
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
public void reset ()
{
    this.getPIDController().reset();
}

/**
 * Overrides the pidGet of our controller's input.
 * We still plan to use the current rate of our encoder as the input.
 *
 * @return Current rate of the encoder in inches/second.
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
@Override
protected double returnPIDInput ()
{
    return this.encoder.getRate();
}

/**
 * Sets the maximum change we can send to the motors
 *
 * @param maxDelta
 *            maximum change we can send
 * @author Noah Golmant
 * @date Aug 19 2014
 */
public void setMaxOutputDelta (double maxDelta)
{
    this.maxOutputDelta = maxDelta;
}

/**
 * Sets whether or not we have to inverse the motor when setting the speed
 *
 * @param isReversed
 *            whether or not we are reversed
 */
public void setMotorReversed (boolean isReversed)
{
    if (isReversed == true)
        {
        this.motorDirection = -1.0;
        }
    else
        {
        this.motorDirection = 1.0;
        }
}

/**
 * Set the output range of the PID controller and our output.
 *
 * @param minRange
 *            minimum motor value to send
 * @param maxRange
 *            maximum motor value to send
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
@Override
public void setOutputRange (double minRange, double maxRange)
{
    this.minRange = minRange;
    this.maxRange = maxRange;
}

/**
 * Overrides the pidWrite method of our controller's output.
 * Sets the output with the calculated PID output value.
 *
 * @param output
 *            The output of the PIDController calculations
 * @see PIDController calculate()
 * @author Noah Golmant
 * @written 11 Feb 2014
 */
@Override
protected void usePIDOutput (double output)
{
    // Add the error speed to the motor controller.

    this.setSpeed = this.motor.get() + this.limit(output);

    // TODO: Debug flag
    System.out
            .println("MOTOR.GET " + this.motor.get() + " / " + "OUT: " +
                    output);
    // Check against our max and min motor voltages
    if (this.setSpeed > this.maxRange)
        {
        this.setSpeed = this.maxRange;
        }
    else if (this.setSpeed < this.minRange)
        {
        this.setSpeed = this.minRange;
        }

    // this.setSpeed *= this.motorDirection;
    System.out.println("SETSPEED: " + this.setSpeed);
    this.motor.set(this.setSpeed);
}

}
