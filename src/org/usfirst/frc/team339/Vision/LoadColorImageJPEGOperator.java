/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class LoadColorImageJPEGOperator implements VisionOperatorInterface
{

    private final String fileName;

    /**
 *
 */
    public LoadColorImageJPEGOperator (String fileName)
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
        // @TODO We may have to create the image instead of using the source.
        NIVision.imaqReadFile(Source, this.fileName);
        System.out.println("Loaded Image from: " + this.fileName);
        return Source;
        }

}
