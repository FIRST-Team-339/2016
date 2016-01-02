/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class SaveBinaryImagePNGOperator implements VisionOperatorInterface
{

private final String fileName;
private final int compressionSpeed;

/**
 *
 */
public SaveBinaryImagePNGOperator (String fileName)
    {
        this.fileName = fileName;
        this.compressionSpeed = 1;
    }

    /**
 *
 */
public SaveBinaryImagePNGOperator (String fileName, int compressionSpeed)
    {
        this.fileName = fileName;
        this.compressionSpeed = compressionSpeed;
    }

/* (non-Javadoc)
     * 
     * @see
     * org.usfirst.frc.team339.Vision.VisionOperatorInterface#operate(com.ni
     * .vision.NIVision.Image) */
@Override
public Image operate (Image Source)
    {
        NIVision.imaqWritePNGFile2(Source, this.fileName,
            this.compressionSpeed, NIVision.RGB_WHITE, 24);// blobs are black
    System.out.println("Printed Image to: " + this.fileName);
    return Source;
    }

}
