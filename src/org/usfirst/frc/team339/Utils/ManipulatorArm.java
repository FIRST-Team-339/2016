package org.usfirst.frc.team339.Utils;

import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.IRSensor;
import org.usfirst.frc.team339.HardwareInterfaces.RobotPotentiometer;
import edu.wpi.first.wpilibj.SpeedController;

// TODO fix everything when we have a physical arm
public class ManipulatorArm
{

public ManipulatorArm (SpeedController armMotorController,
        SpeedController intakeMotor,
        RobotPotentiometer armPot, IRSensor ballIsInArmSensor)
{
    this.motor = armMotorController;
    this.armPot = armPot;
    this.intakeMotor = intakeMotor;
    this.hasBallSensor = ballIsInArmSensor;
}

//TODO change so it doens't move beyond soft limit from encoder.
/**
 * Moves the arm at its current slow speed.
 * 
 * @param direction
 *            Positive one for forward and negative one for backwards
 */
public void moveSlow (int direction, boolean override)
{

    direction *= -1;
    this.move(direction * this.slowSpeed, override);
}

/**
 * Moves the arm at its current fast speed.
 * 
 * @param direction
 *            Positive one for forward and negative one for backwards
 */
public void moveFast (int direction, boolean override)
{
    direction *= -1;
    this.move(direction * this.MAX_ARM_SPEED, override);
}

/**
 * Method used to smoothly raise and lower arm at a humanly managable speed.
 * Changes speed after a certain tipping point.
 * 
 * @param direction
 * @param override
 */
public void moveReasonably (int direction, boolean override)
{
    direction *= -1;
    if (direction > 0)
    //Going UP!
        {
        if (armPot.get() < REASONABLE_DECELERATION_ANGLE)
        //Starting up, has to work hard.
            {
            move(REASONABLE_UP_FACTOR, override);
            }
        else
        //We are over the hump, slow down.
            {
            move(REASONABLE_UP_AND_OVER_FACTOR, override);
            }
        }
    else
    //going down.
        {
        if (armPot.get() > REASONABLE_DECELERATION_ANGLE)
            {
            move(REASONABLE_DOWN_FACTOR, override);
            }
        else
        //now gravity is on our side. Slow down a bit.
            {
            move(REASONABLE_DOWN_UNDER_FACTOR, override);
            }
        }

}

/**
 * Moves the arm at the given speed. Positive brings it up, negative down.
 * 
 * @param speed
 *            The speed at which to move the arm.
 */
public void move (double speed, boolean override)
{

    //If we're currently beyond our soft limits, don't do anything that would 
    //bring up further out of them.  Otherwise do what the user wants.
    if ((speed > 0 && this.armPot.get() < this.MIN_SOFT_ARM_STOP)
            || (speed < 0
                    && this.armPot.get() > this.MAX_SOFT_ARM_STOP))
        {
        //we have to give a little bit of voltage to stop the motor.
        this.stopArmMotor();
        }
    else
        {
        this.motor.set(-speed);
        }
}

public void move (double speed)
{
    this.move(speed, false);
}

public void stopArmMotor ()
{
    if (armPot.get() > STOP_DOWN_ANGLE)
        {
        this.motor.set(0.15);
        }
    else
        {
        this.motor.set(0.0);
        }

}

/**
 * Starts the intake motor to suck in a ball; stopIntakeArms() needs to be
 * called to stop them.
 */
public void pullInBall (boolean override)
{
    if (Hardware.armIR.isOn() == true && override == false
            && armPot.get() <= DEPOSIT_POSITION)
        {
        //If we already have a ball, no need to pull one in.
        //TODO check to make sure -1 pulls in and not the reverse.
        this.intakeMotor.set(0.0);
        }
    else
        {
        this.intakeMotor.set(-INTAKE_SPEED);
        }

}

/**
 * Starts the intake motor to push out a ball; stopIntakeArms() needs to be
 * called to stop them.
 */
public void pushOutBall ()
{

    //TODO check to make sure 1 pushes out and not the reverse.
    this.intakeMotor.set(1.0);

}

/**
 * 
 * @return true if ball is not within its clutches.
 */
public boolean ballIsOut ()
{
    return !this.hasBallSensor.get();
}

/**
 * Stops the intake arms.
 */
public void stopIntakeArms ()
{
    this.intakeMotor.set(0.0);
}

public void setIntakeArmsSpeed (double speed)
{
    this.intakeMotor.set(speed);
}

/**
 * 
 * @return true if arm is down.
 */
public boolean isDown ()
{
    if (this.armPot.get() <= this.MIN_SOFT_ARM_STOP)
        {
        return true;
        }
    else
        {
        return false;
        }
}

/**
 * 
 * @return true if arm is up.
 */
public boolean isUp ()
{
    if (this.armPot.get() >= MAX_SOFT_ARM_STOP)
        {
        return true;
        }
    else
        {
        return false;
        }
}


/**
 * 
 * @return true if arm is out of the way.
 */
public boolean isClearOfArm ()
{
    if (armPot.get() <= this.ARM_OUT_OF_WAY_DEGREES)
        {
        return true;
        }

    return false;
}

public boolean isInDepositPosition ()
{
    if (armPot.get() > DEPOSIT_POSITION - DEPOSIT_POSITION_THRESHOLD
            && armPot.get() < DEPOSIT_POSITION
                    + DEPOSIT_POSITION_THRESHOLD)
        {
        return true;
        }
    return false;
}

public void holdInHoldingPosition ()
{
    if (armPot.get() < HOLDING_POSITION - HOLDING_POSITION_THRESHOLD)
        {
        move(MAX_ARM_SPEED);
        }
    else if (armPot.get() > HOLDING_POSITION
            + HOLDING_POSITION_THRESHOLD)
        {
        move(-MAX_ARM_SPEED);
        }
    else
        {
        move(HOLDING_SPEED);
        }
}

/**
 * Moves the arm at full speed to the desired position.
 * 
 * @param position
 *            desired.
 * @return true when positioning is complete.
 */
public boolean moveToPosition (ArmPosition position)
{
    boolean done = false;

    switch (position)
        {
        case FULL_DOWN:

            move(-MAX_ARM_SPEED);
            if (this.isDown())
                {
                move(0.0);
                done = true;
                }
            break;
        case FULL_UP:
            move(MAX_ARM_SPEED);
            if (this.isUp())
                {
                move(0.0);
                done = true;
                }
            break;
        case DEPOSIT:
            if (armPot.get() < DEPOSIT_POSITION
                    - DEPOSIT_POSITION_THRESHOLD)
                {
                move(MAX_ARM_SPEED);
                }
            else if (armPot.get() > DEPOSIT_POSITION
                    + DEPOSIT_POSITION_THRESHOLD)
                {
                move(-MAX_ARM_SPEED);
                }
            else
                {
                move(0.0);
                done = true;
                }
            break;
        case CLEAR_OF_FIRING_ARM:
            move(-MAX_ARM_SPEED);
            if (this.isClearOfArm() == true)
                {
                move(0.0);
                done = true;
                }
            break;
        default:
        case HOLD:
            holdInHoldingPosition();
            break;
        }

    return done;
}

/**
 * 
 * A set of positions the arm can be in.
 *
 */
public static enum ArmPosition
    {
    /**
     * All the way down, as in down-to-the-floor down.
     */
    FULL_DOWN,
    /**
     * Folded up all the way.
     */
    FULL_UP,
    /**
     * Within a rang from which we can pu the ball into the catapult.
     */
    DEPOSIT,
    /**
     * Out of the way of the catapult.
     */
    CLEAR_OF_FIRING_ARM,
    /**
     * Stay off the ground, yet out of the way,.
     */
    HOLD
    }

private SpeedController intakeMotor = null;
private SpeedController motor = null;
private RobotPotentiometer armPot = null;
private IRSensor hasBallSensor = null;
//default maximum arm turn speed proportion
private final double MAX_ARM_SPEED = -1.0;
//default slow arm turn speed proportion
private double slowSpeed = .2;

private final double MAX_SOFT_ARM_STOP = 170.0;
private final double MIN_SOFT_ARM_STOP = 21.0;

private final double ARM_OUT_OF_WAY_DEGREES = 130.0;//TODO change back to 130

private final double DEPOSIT_POSITION = 145.0;
private final double DEPOSIT_POSITION_THRESHOLD = 5.0;

private final double REASONABLE_UP_FACTOR = -1.0;
private final double REASONABLE_UP_AND_OVER_FACTOR = -0.40;
private final double REASONABLE_DOWN_FACTOR = 0.35;
private final double REASONABLE_DOWN_UNDER_FACTOR = 0.20;
private final double REASONABLE_DECELERATION_ANGLE = 111.1;

private final double INTAKE_SPEED = 0.5;

private static final int HOLDING_POSITION = 70;
private static final int HOLDING_POSITION_THRESHOLD = 5;
private static final double HOLDING_SPEED = 0.1;

private static final int STOP_DOWN_ANGLE = 45;

}
