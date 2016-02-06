package org.usfirst.frc.team339.Utils;

/**
 * 
 * Contains distances and speeds to be traveled.
 * A terminator can also be set to indicate the end of a set of instructions.
 * 
 * @author Michael Andrzej Klaczynski
 *
 */
public class DriveInstruction
{
private double forwardDistance = 0.0;
private double forwardSpeedRatio = 0.0;
private double rotationalDistance = 0.0;
private double rotationalSpeedRatio = 0.0;

boolean terminator = false;

/**
 * Give distances and speeds to drive forwards and to rotate.
 * Usually, we will only go forwards OR rotate.
 * 
 * @param forwardSpeedRatio
 * @param velocity
 * @param rotation
 * @param rotationalSpeedRatio
 */
public DriveInstruction (double forwardSpeedRatio, double velocity,
        double rotation, double rotationalSpeedRatio)
{
    this.forwardDistance = forwardSpeedRatio;
    this.forwardSpeedRatio = velocity;
    this.rotationalDistance = rotation;
    this.rotationalSpeedRatio = rotationalSpeedRatio;
}

/**
 * Flag as the end of a path.
 * 
 * @param terminator
 */
public DriveInstruction (boolean terminator)
{
    this.terminator = terminator;
}



public double getForwardDistance ()
{
    return this.forwardDistance;
}

public void setForwardDistance (double forwardDistance)
{
    this.forwardDistance = forwardDistance;
}

public double getForwardSpeedRatio ()
{
    return this.forwardSpeedRatio;
}

public void setForwardSpeedRatio (double forwardSpeedRatio)
{
    this.forwardSpeedRatio = forwardSpeedRatio;
}

public double getRotationalDistance ()
{
    return this.rotationalDistance;
}

public void setRotationalDistance (double rotationalDistance)
{
    this.rotationalDistance = rotationalDistance;
}

public double getRotationalSpeedRatio ()
{
    return this.rotationalSpeedRatio;
}

public void setRotationalSpeedRatio (double rotationalSpeedRatio)
{
    this.rotationalSpeedRatio = rotationalSpeedRatio;
}

public boolean isTerminator ()
{
    return this.terminator;
}

public void setTerminator (boolean terminator)
{
    this.terminator = terminator;
}
}
