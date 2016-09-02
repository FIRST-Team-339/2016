package org.usfirst.frc.team339.Vision;

import java.util.ArrayList;
import org.usfirst.frc.team339.Vision.operators.VisionOperatorInterface;

public class VisionScript extends ArrayList<VisionOperatorInterface>
{
/**
 * 
 */
private static final long serialVersionUID = 1L;

public VisionScript ()
{
    this.getPosition = 0;
    this.putPosition = 0;
}


// Next four methods probably totally unnecessary, but may be potentially
// useful sometimes, so it stays!
public VisionOperatorInterface get ()
{
    VisionOperatorInterface temp = super.get(getPosition);
    getPosition++;
    return temp;
}

public void put (VisionOperatorInterface operator)
{
    super.add(putPosition, operator);
}

public void seekg (int position)
{
    this.getPosition = position;
}

public void seekp (int position)
{
    this.putPosition = position;
}

private int getPosition;

private int putPosition;

}
