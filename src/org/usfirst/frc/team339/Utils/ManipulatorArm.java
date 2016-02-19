package org.usfirst.frc.team339.Utils;

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
public void moveSlow (int direction)
{
	this.move(direction * this.slowSpeed);
}

/**
 * Moves the arm at its current fast speed.
 * 
 * @param direction
 *            Positive one for forward and negative one for backwards
 */
public void moveFast (int direction)
{
	this.move(direction * this.MAX_ARM_SPEED);
}

/**
 * Moves the arm at the given speed. Positive brings it up, negative down.
 * 
 * @param speed
 *            The speed at which to move the arm.
 */
public void move (double speed)
{
	//If we're currently beyond our soft limits, don't do anything.  Otherwise do what the user wants.
	if (this.armPot.get() >= this.ARM_SOFT_MAX_DEGREES
	        || this.armPot.get() <= this.ARM_SOFT_MIN_DEGREES)
	{
	this.motor.set(0.0);
	}
	else
	{
	this.motor.set(speed);
	}
}

/**
 * Starts the intake motor to suck in a ball; stopIntakeArms() needs to be
 * called to stop them.
 */
public void pullInBall ()
{
	//If we already have a ball, no need to pull one in.
	if (this.hasBallSensor.get() != true)
	{
	//TODO check to make sure -1 pulls in and not the reverse.
	this.intakeMotor.set(-1.0);
	}
	else
	{
	this.stopIntakeArms();
	}
}

/**
 * Starts the intake motor to push out a ball; stopIntakeArms() needs to be
 * called to stop them.
 */
public void pushOutBall ()
{
	//Only bother pushing the ball out if we have a ball
	if (this.hasBallSensor.get() == true)
	{
	//TODO check to make sure 1 pushes out and not the reverse.
	this.intakeMotor.set(1.0);
	}
	else
	{
	this.stopIntakeArms();
	}
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
	if (this.armPot.get() <= this.ARM_SOFT_MIN_DEGREES)
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
	if (this.armPot.get() >= this.ARM_SOFT_MAX_DEGREES)
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
		default:
		case CLEAR_OF_FIRING_ARM:
			move(-MAX_ARM_SPEED);
			if (this.isClearOfArm() == true)
			{
			move(0.0);
			done = true;
			}
			break;

	}

	return false;
}

/**
 * 
 * A set of positions the arm can be in.
 *
 */
public static enum ArmPosition
	{
	FULL_DOWN, FULL_UP, CLEAR_OF_FIRING_ARM;
	}

private SpeedController intakeMotor = null;
private SpeedController motor = null;
private RobotPotentiometer armPot = null;
private IRSensor hasBallSensor = null;
//default maximum arm turn speed proportion
private final double MAX_ARM_SPEED = .75;
//default slow arm turn speed proportion
private double slowSpeed = .5;
//TODO entirely arbitrary values until we can actually test
private final double ARM_SOFT_MAX_DEGREES = 20.0;
private final double ARM_SOFT_MIN_DEGREES = 0.0;
private final double ARM_OUT_OF_WAY_DEGREES = 10.0;

}
