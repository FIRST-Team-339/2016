package org.usfirst.frc.team339.Vision;

import java.util.ArrayList;
import java.util.Iterator;
import org.usfirst.frc.team339.Vision.operators.VisionOperatorInterface;

public class VisionScript implements Iterable// extends
                                             // ArrayList<VisionOperatorInterface>
{
/**
 * 
 */
private static final long serialVersionUID = 1L;

/**
 * Creates a vision script with
 * 
 * @param operators
 *            The operators to add to the VisionScript, IN ORDER
 */
public VisionScript (VisionOperatorInterface... operators)
{
    for (VisionOperatorInterface o : operators)
        {
            this.add(o);
        }
    this.getPosition = 0;
    this.putPosition = 0;
}

public VisionScript ()
{
    this(new org.usfirst.frc.team339.Vision.operators.HSLColorThresholdOperator(
            0, 0, 0, 0, 0, 0));
}

public void add (int position, VisionOperatorInterface op)
{
    this.operators.add(position, op);
}

public void add (VisionOperatorInterface op)
{
    this.operators.add(op);
}

public void remove (int pos)
{
    this.operators.remove(pos);
}

public void remove (VisionOperatorInterface op)
{
    this.operators.remove(op);
}

public int size ()
{
    return this.operators.size();
}

public VisionOperatorInterface get (int pos)
{
    return this.operators.get(pos);
}

public void clear ()
{
    this.operators.clear();
}


// Next four methods probably totally unnecessary, but may be potentially
// useful sometimes, so it stays!
public VisionOperatorInterface get ()
{
    VisionOperatorInterface temp = this.operators.get(getPosition);
    getPosition++;
    return temp;
}

public void put (VisionOperatorInterface operator)
{
    this.add(putPosition, operator);
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

private ArrayList<VisionOperatorInterface> operators = new ArrayList<VisionOperatorInterface>(
        0);

@Override
public Iterator<VisionOperatorInterface> iterator ()
{
    return operators.iterator();
}

}
