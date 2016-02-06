package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

public class ManipulatorArm
{
public ManipulatorArm (SpeedController armMotorController,
        SpeedController starboardIntakeMotor,
        SpeedController portIntakeMotor,
        Encoder armEncoder)
{
    this.motor = armMotorController;
    this.armEncoder = armEncoder;
    this.starboardIntakeMotor = starboardIntakeMotor;
    this.portIntakeMotor = portIntakeMotor;
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
    this.move(direction * this.maxArmSpeed);
}

public void move (double speed)
{
    if (this.armEncoder.getDistance() <= ARM_SOFT_MAX
            && this.armEncoder.getDistance() >= 0.0)
        {
        this.motor.set(speed);
        }
    else
        {
        this.motor.set(0.0);
        }

}

public void pullInBall ()
{
    this.portIntakeMotor.set(-1.0);
    this.starboardIntakeMotor.set(1.0);
}

public void stopIntakeArms ()
{
    this.portIntakeMotor.set(0.0);
    this.starboardIntakeMotor.set(0.0);
}

private SpeedController starboardIntakeMotor = null;
private SpeedController portIntakeMotor = null;
private SpeedController motor = null;
private Encoder armEncoder = null;
//default maximum arm turn speed proportion
private double maxArmSpeed = .75;
//default slow arm turn speed proportion
private double slowSpeed = .5;
//TODO entirely arbitrary value until we can actually test
private final double ARM_SOFT_MAX = 20.0;

}
