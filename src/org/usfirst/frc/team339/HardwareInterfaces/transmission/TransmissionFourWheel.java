package org.usfirst.frc.team339.HardwareInterfaces.transmission;

import edu.wpi.first.wpilibj.SpeedController;



public class TransmissionFourWheel extends Transmission
{

private final SpeedController rightRearSpeedController;
private MotorDirection rightRearMotorDirection =
        MotorDirection.REVERSED;

private final SpeedController leftRearSpeedController;
private MotorDirection leftRearMotorDirection = MotorDirection.FORWARD;

/**
 * Transmission object to control a four-wheel drive.
 *
 * @param rightFrontSpeedController
 * @param rightRearSpeedController
 * @param leftFrontSpeedController
 * @param leftRearSpeedController
 *
 * @author Noah Golmant
 * @written 23 July 2015
 */
public TransmissionFourWheel (SpeedController rightFrontSpeedController,
        SpeedController rightRearSpeedController,
        SpeedController leftFrontSpeedController,
        SpeedController leftRearSpeedController)
{
    super(rightFrontSpeedController, leftFrontSpeedController);

    this.rightRearSpeedController = rightRearSpeedController;
    this.leftRearSpeedController = leftRearSpeedController;
}

/**
 * Drives the transmission in a four wheel drive .
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
 * @written 9 July 2015
 */
@Override
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
        System.out
                .println("drive():\tRF: " + scaledRightVal + "\tLF: " +
                        scaledLeftVal);
        }

    // send the scaled values to the motors
    this.driveRightMotor(scaledRightVal);
    this.driveRightRearMotor(scaledRightVal);
    this.driveLeftMotor(scaledLeftVal);
    this.driveLeftRearMotor(scaledLeftVal);
}

/**
 * Sets the left motor to the given value based on
 * its given direction.
 *
 * @param motorValue
 *            The motor value we want to send
 *
 * @author Noah Golmant
 * @date 9 July 2015
 */

//"Anything can be solved with a big enough hammer, if not elegantly." -Michael
protected void driveLeftRearMotor (double motorValue)
{

    if (this.leftRearSpeedController == null)
        {
        if (this.getDebugState() == DebugState.DEBUG_MOTOR_DATA)
            {
            System.out
                    .println(
                            "Left rear motor is null in driveLeftRearMotor()");
            }

        return;
        }

    motorValue = this.limit(motorValue);
    this.leftRearSpeedController.set(motorValue *
            this.leftRearMotorDirection.val);
}

/**
 * Sets the right motor to the given value based on
 * its given direction.
 *
 * @param motorValue
 *            The motor value we want to send
 *
 * @author Noah Golmant
 * @date 9 July 2015
 */
protected void driveRightRearMotor (double motorValue)
{

    if (this.rightRearSpeedController == null)
        {
        if (this.getDebugState() == DebugState.DEBUG_MOTOR_DATA)
            {
            System.out
                    .println(
                            "Right rear motor is null in driveRightRearMotor()");
            }

        return;
        }

    motorValue = this.limit(motorValue);
    this.rightRearSpeedController.set(motorValue *
            this.rightRearMotorDirection.val);
}

/**
 * Gets whether or not the left motor is reversed
 *
 * @return the direction of the left (front) motor
 */
public MotorDirection getLeftRearMotorDirection ()
{
    return this.leftRearMotorDirection;
}

/**
 * Gets whether or not the right motor is reversed
 *
 * @return the direction of the right (front) motor
 */
public MotorDirection getRightRearMotorDirection ()
{
    return this.rightRearMotorDirection;
}

/**
 * Sets whether or not the left (front) motor is reversed
 *
 * @param direction
 *            new direction of the left (front) motor
 */
public void setLeftRearMotorDirection (MotorDirection direction)
{
    this.leftRearMotorDirection = direction;
}

/**
 * Sets whether or not the right (front) motor is reversed
 *
 * @param direction
 *            new direction of the right (front) motor
 */
public void setRightRearMotorDirection (MotorDirection direction)
{
    this.rightRearMotorDirection = direction;
}

}
