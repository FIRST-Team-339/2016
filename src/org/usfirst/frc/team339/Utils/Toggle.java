package org.usfirst.frc.team339.Utils;

/**
 * @author WillStuckey
 * @date 3/14/14
 *       <p>
 *       This class allows a momentary switch to act as a toggle button.
 *       </p>
 */
public class Toggle
{
    private boolean isToggleOn = false;
    private final boolean[] iterativeInstances;

    /**
     * Initializes a toggle button with a value of 4.
     */
    public Toggle ()
        {
        this(4);
        }

    /**
     * Initializes a toggle button. The toggle button holds a boolean and
     * toggles its value after the specified number
     * true update calls. For example, if 4 is passed, the function update()
     * must be called with the value of true 4
     * times consecutively before the value is toggled. If at any time false is
     * passed, or the button value is toggled
     * the counter is reset to 0.
     *
     * @param numIterationsToCheck
     *            how many consecutive iterations must be true before the button
     *            is toggled
     */
    public Toggle (int numIterationsToCheck)
        {
        this.iterativeInstances = new boolean[numIterationsToCheck];
        }

    /**
     * @return the toggle button value
     */
    public boolean getValue ()
        {
        return this.isToggleOn;
        }

    /**
     * sets the toggle button value
     *
     * @param value
     *            new value
     */
    public void setValue (boolean value)
        {
        this.isToggleOn = value;
        }

    /**
     * Passes the iterative update value
     *
     * @param value
     */
    public void update (boolean value)
        {
        for (int i = 0; i < (this.iterativeInstances.length - 1); i++)
        {
        this.iterativeInstances[i] = this.iterativeInstances[i + 1];
        }

        this.iterativeInstances[this.iterativeInstances.length - 1] = value;

        for (int i = 0; i < (this.iterativeInstances.length - 1); i++)
            if (this.iterativeInstances[i] == false)
                return;

        this.isToggleOn = !this.isToggleOn;

        for (int i = 0; i < (this.iterativeInstances.length - 1); i++)
        {
        this.iterativeInstances[i] = false;
        }
        }
}