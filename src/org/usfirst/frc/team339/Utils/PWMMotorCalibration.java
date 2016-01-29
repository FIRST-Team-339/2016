package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

public class PWMMotorCalibration
{
// TODO make this work
// TODO print something to screen to notify driver of the completion of
// calibration
public static void calibrateMotor (SpeedController motor)
{
    if (hasRunVictorOnce == false)
        {
            time.stop();
            time.reset();
            time.start();
            hasRunVictorOnce = true;
        }
    if (time.get() <= 2.0)
        motor.set(1.0);
    else if (time.get() >= 2.0 && time.get() <= 3.0)
        motor.set(0.0);
    else if (time.get() >= 3.0 && time.get() <= 5.0)
        motor.set(-1.0);
    else
        motor.set(0.0);

}

private static boolean hasRunVictorOnce = false;

private static Timer time = new Timer();
}
