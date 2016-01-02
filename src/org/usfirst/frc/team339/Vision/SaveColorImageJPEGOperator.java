/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class SaveColorImageJPEGOperator implements VisionOperatorInterface
{

private final String fileName;

/**
 *
 */
public SaveColorImageJPEGOperator (String fileName)
    {
    this.fileName = fileName;
    }

/* (non-Javadoc)
     * 
     * @see
     * org.usfirst.frc.team339.Vision.VisionOperatorInterface#operate(com.ni
     * .vision.NIVision.Image) */
@Override
public Image operate (Image Source)
    {
        NIVision.imaqWriteJPEGFile(Source, this.fileName, 1000, null);
    System.out.println("Printed Image to: " + this.fileName);
    return Source;
    }

}
