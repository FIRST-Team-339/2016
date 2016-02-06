package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

public class ManipulatorArm
{
public ManipulatorArm (SpeedController armMotorController,
        Encoder armEncoder)
{
    this.motor = armMotorController;
    this.armEncoder = armEncoder;
}

public void move (double speed)
{

}

private SpeedController motor = null;
private Encoder armEncoder = null;
}
