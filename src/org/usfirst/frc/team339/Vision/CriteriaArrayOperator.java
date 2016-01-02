package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.MeasurementType;
import com.ni.vision.NIVision.ParticleFilterCriteria2;

public class CriteriaArrayOperator implements VisionOperatorInterface
{

// The array that stores the criteria to filter blobs on.
private ParticleFilterCriteria2[] criteriaArray =
new ParticleFilterCriteria2[0];

// adds a criteria of the specified measurement type in the range
public MeasurementType parameter; // The morphological measurement that the
                                      // function uses for filtering.

// If the array is not null, then it is set to be an array.
public CriteriaArrayOperator (ParticleFilterCriteria2[] criteriaArray)
    {
    if (criteriaArray != null)
        {
        this.criteriaArray = criteriaArray;
        }
    }

    // public float lower; // The lower bound of the criteria range.
    // public float upper; // The upper bound of the criteria range.
    // public int calibrated; // Set this element to TRUE to take calibrated
    // measurements.
    // public int exclude; // Set this element to TRUE to indicate that a match
    // occurs when the measurement is outside the criteria range.
public void addCriteria (NIVision.MeasurementType type, float lower,
    float upper, boolean calibrated, boolean exclude)
    {
    int calibratedAsInteger;
    if (calibrated == true)
        {
            calibratedAsInteger = 1;
            }
    else
        {
            calibratedAsInteger = 0;
            }
    int excludeAsInteger;
    if (exclude == true)
        {
            excludeAsInteger = 1;
            }
    else
        {
            excludeAsInteger = 0;
            }
    // Create a new criteria object from our arguments
    final ParticleFilterCriteria2 criteria =
        new ParticleFilterCriteria2(type, lower, upper,
                calibratedAsInteger, excludeAsInteger);
    // Create a new criteria array, the size of our old array + 1 to
    // accomodate the new one
    final ParticleFilterCriteria2[] tempCriteria =
        new ParticleFilterCriteria2[this.criteriaArray.length + 1];
    // Copy the old criteria array into our new one
    System.arraycopy(this.criteriaArray, 0, tempCriteria, 0,
        this.criteriaArray.length);
    // set the last value of this new array to the new criteria
    tempCriteria[tempCriteria.length - 1] = criteria;
    // set the class criteria array to our temporary one that has the new
    // entry
    this.criteriaArray = tempCriteria;
    // this.criteriaCollection.addCriteria(type, lower, upper,
    // outsideRange);
    }

public void clearCriteriaCollection ()
    {
    this.criteriaArray = new ParticleFilterCriteria2[0];
    }

@Override
public Image operate (Image Source)
    {
    final NIVision.ParticleFilterOptions2 filterOptions =
        new NIVision.ParticleFilterOptions2(0, 0, 0, 0);
    NIVision.imaqParticleFilter4(Source, Source, this.criteriaArray,
        filterOptions, null);
    return Source;
    }

}
