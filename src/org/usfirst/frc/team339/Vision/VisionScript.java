package org.usfirst.frc.team339.Vision;

import java.util.ArrayList;
import org.usfirst.frc.team339.Vision.operators.NullOperator;
import org.usfirst.frc.team339.Vision.operators.VisionOperatorInterface;

public class VisionScript extends ArrayList<VisionOperatorInterface>
{
/**
 * 
 */
private static final long serialVersionUID = 1L;

public VisionScript ()
{
    //@AHK TODO empty constructor
}

@Override
public void add (int position, VisionOperatorInterface operator)
{
    if (super.get(position) == null)
        super.set(position, operator);
    else
        super.add(position, operator);
}

@Override
public boolean add (VisionOperatorInterface operator)
{
    return super.add(operator);
}

@Override
public VisionOperatorInterface remove (int position)
{
    return this.set(position, null);
}

@Override
public VisionOperatorInterface get (int i)
{
    if (this.get(i) == null)
        return new NullOperator();
    return super.get(i);
}
}
