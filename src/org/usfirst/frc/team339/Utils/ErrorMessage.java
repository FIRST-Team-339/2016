package org.usfirst.frc.team339.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.usfirst.frc.team339.Hardware.Hardware;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author Michael Andrzej Klaczynski
 *         Prints errors to the Drivers' Station and/or saves onto the RoboRIO.
 *         Messages on the RoboRIO are saved to /home/lvuser/errors/errorlog.txt
 *         by default, though you may create other log locations from this
 *         class, by constructor or method <br>
 *         There is also an option to append a time stamp to the beginning of
 *         each message.
 */
public class ErrorMessage
{
	/**
	 * An enum designed for the ErrorMessage class.<br>
	 * driverStation prints the error to only the Drivers' Station<br>
	 * roboRIO prints the error to only the RoboRIO
	 */
	public static enum PrintsTo
	{
		driverStation, roboRIO, driverStationAndRoboRIO
	}

	/**
	 * The time, according to the RIO. <br>
	 * Appended onto the beginning of the error message. <br>
	 * Updated upon printError.
	 */
	private String rioTime;

	/**
	 * The match time, according to the DriverStation.getMatchTime() function.
	 * <br>
	 * May not be accurate. <br>
	 * Updated upon printError.
	 */
	private double matchTime;

	/** The location to which the errors are saved. */
	private String errorlogLocation =
	        "/home/lvuser/errors/errorlog.txt";

	/**
	 * The default choice of whether or not to append a time Stamp to the
	 * message.
	 */
	private boolean appendTimeStamp = false;

	/** The set of devices to which the message is printed by default. */
	private PrintsTo defaultPrintDevice =
	        PrintsTo.driverStationAndRoboRIO;

	/**
	 * Creates an ErrorMessage object. <br>
	 * Prints to both Drivers' Station and RoboRIO by default. <br>
	 * Alternative constructor allows for choice to send to either device.<br>
	 * Location defaults to /home/lvuser/errors/errorlog.txt<br>
	 * Appends a time stamp by default.
	 */




	/**
	 * Creates an ErrorMessage object. <br>
	 * Prints to both Drivers' Station and RoboRIO by default. <br>
	 * Alternative constructor allows for choice to send to either device.<br>
	 * Location defaults to /home/lvuser/errors/errorlog.txt<br>
	 * Appends a time stamp by default.
	 */
	public ErrorMessage ()
	{
		this.errorlogLocation = "/home/lvuser/errors/errorlog.txt";

		this.appendTimeStamp = true;

		this.defaultPrintDevice = PrintsTo.driverStationAndRoboRIO;

		/*
		 * if this directory does not exist, this should create it.
		 * 
		 * (This may encounter problems if directory may not be modified)
		 */
		final File location = new File(this.errorlogLocation);

		if (location.exists() == false)
		{
			final int lastSlash =
			        this.errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        this.errorlogLocation.substring(0, lastSlash);


			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime().exec(
				        "/bin/touch \"" + this.errorlogLocation + "\"");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Creates an ErrorMessage object and sends it to DS or RIO.
	 * * Prints to both Drivers' Station and RoboRIO by default. <br>
	 * Alternative constructor allows for choice to send to either device.<br>
	 * Location defaults to /home/lvuser/errors/errorlog.txt
	 * Appends time stamp by default
	 *
	 * @param PrintsTo
	 *            the default device to which the messages will be printed
	 */
	public ErrorMessage (PrintsTo PrintsTo)
	{
		this.errorlogLocation = "/home/lvuser/errors/errorlog.txt";

		this.appendTimeStamp = true;

		this.defaultPrintDevice = PrintsTo;

		/*
		 * if this directory does not exist, this should create it.
		 * 
		 * (This may encounter problems if directory may not be modified)
		 */
		final File location = new File(this.errorlogLocation);

		if (location.exists() == false)
		{
			final int lastSlash =
			        this.errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        this.errorlogLocation.substring(0, lastSlash);

			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime().exec(
				        "/bin/touch \"" + this.errorlogLocation + "\"");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}


	/**
	 * Creates an ErrorMessage object. <br>
	 * Prints to both Drivers' Station and RoboRIO by default. <br>
	 * Alternative constructor allows for choice to send to either device.<br>
	 * Location defaults to /home/lvuser/errors/errorlog.txt
	 *
	 * @param appendTimeStampByDefault
	 *            whether or not to append a time stamp to each message by
	 *            default.
	 */
	public ErrorMessage (boolean appendTimeStampByDefault)
	{
		errorlogLocation = "/home/lvuser/errors/errorlog.txt";

		appendTimeStamp = appendTimeStampByDefault;

		defaultPrintDevice = PrintsTo.driverStationAndRoboRIO;

		/*
		 * if this directory does not exist, this should create it.
		 * 
		 * (This may encounter problems if directory may not be modified)
		 */
		final File location = new File(errorlogLocation);

		if (location.exists() == false)
		{
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);

			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime().exec(
				        "/bin/touch \"" + errorlogLocation + "\"");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Creates an Error and decides to print to Drivers' Station
	 * and/or RoboRIO. <br>
	 * You may set the default print device,<br>
	 * and choose whether or not to append a time stamp by default.
	 *
	 * @param PrintsTo
	 *            is the default print device(s).
	 * @param appendTimeStampByDefault
	 *            is whether or not to append a time stamp to each message by
	 *            default.
	 */
	public ErrorMessage (PrintsTo PrintsTo,
	        boolean appendTimeStampByDefault)
	{
		errorlogLocation = "/home/lvuser/errors/errorlog.txt";

		defaultPrintDevice = PrintsTo;
		appendTimeStamp = appendTimeStampByDefault;

		/* if this directory does not exist, this should create it. */
		final File location = new File(errorlogLocation);

		try
		{

			final boolean created = location.createNewFile();
			System.out.println("Created? " + created + "!");
		}
		catch (final IOException e)
		{
			e.printStackTrace();

		}
		if (location.exists() == false)
		{
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);
			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime()
				        .exec("/bin/touch " + errorlogLocation);

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			PrintWriter newFile;
			try
			{
				newFile = new PrintWriter(errorlogLocation, "UTF-8");
				newFile.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (final UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

		}

	}

	/**
	 * Creates an Error with a message and decides to print to Drivers' Station
	 * and/or RoboRIO. <br>
	 * This constructor allows you to set the default log file location.
	 *
	 * @param errorlogLocation
	 *            the destination file of the errors.<br>
	 *            Default: /home/lvuser/errors/errorlog.txt
	 */
	public ErrorMessage (String errorlogLocation)
	{

		defaultPrintDevice = PrintsTo.driverStationAndRoboRIO;
		appendTimeStamp = true;

		/* if this directory does not exist, this should create it. */
		final File location = new File(errorlogLocation);

		try
		{

			final boolean created = location.createNewFile();
			System.out.println("Created? " + created + "!");
		}
		catch (final IOException e)
		{
			e.printStackTrace();

		}
		if (location.exists() == false)
		{
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);
			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime()
				        .exec("/bin/touch " + errorlogLocation);

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			PrintWriter newFile;
			try
			{
				newFile = new PrintWriter(errorlogLocation, "UTF-8");
				newFile.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (final UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

		}

		this.errorlogLocation = errorlogLocation;
	}

	/**
	 * Creates an Error with a message and decides to print to Drivers' Station
	 * and/or RoboRIO. <br>
	 * This constructor allows you to set the default log file location.<br>
	 * You may also choose whether or not to append a time stamp by default.
	 *
	 * @param errorlogLocation
	 *            the destination file of the errors.<br>
	 *            Default: /home/lvuser/errors/errorlog.txt
	 * @param appendTimeStampByDefault
	 *            is whether or not to append a time stamp to each message by
	 *            default.
	 */
	public ErrorMessage (String errorlogLocation,
	        boolean appendTimeStampByDefault)
	{

		defaultPrintDevice = PrintsTo.driverStationAndRoboRIO;
		appendTimeStamp = appendTimeStampByDefault;

		/* if this directory does not exist, this should create it. */
		final File location = new File(errorlogLocation);

		try
		{

			final boolean created = location.createNewFile();
			System.out.println("Created? " + created + "!");
		}
		catch (final IOException e)
		{
			e.printStackTrace();

		}
		if (location.exists() == false)
		{
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);
			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime()
				        .exec("/bin/touch " + errorlogLocation);

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			PrintWriter newFile;
			try
			{
				newFile = new PrintWriter(errorlogLocation, "UTF-8");
				newFile.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (final UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}

		this.errorlogLocation = errorlogLocation;
	}

	/**
	 * Creates an Error with a message and decides to print to Drivers' Station
	 * and/or RoboRIO. <br>
	 * This constructor allows you to set the default log file location.
	 *
	 * @param errorlogLocation
	 *            the destination file of the errors.<br>
	 *            Default: /home/lvuser/errors/errorlog.txt
	 *
	 * @param PrintsTo
	 *            is the default print device(s).
	 */
	public ErrorMessage (String errorlogLocation, PrintsTo PrintsTo)
	{

		defaultPrintDevice = PrintsTo;
		appendTimeStamp = true;

		/* if this directory does not exist, this should create it. */
		final File location = new File(errorlogLocation);

		try
		{

			final boolean created = location.createNewFile();
			System.out.println("Created? " + created + "!");
		}
		catch (final IOException e)
		{
			e.printStackTrace();

		}
		if (location.exists() == false)
		{
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);
			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime()
				        .exec("/bin/touch " + errorlogLocation);

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			PrintWriter newFile;
			try
			{
				newFile = new PrintWriter(errorlogLocation, "UTF-8");
				newFile.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (final UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

		}
		this.errorlogLocation = errorlogLocation;
	}

	/**
	 * Creates an Error with a message and decides to print to Drivers' Station
	 * and/or RoboRIO. <br>
	 * This constructor allows you to set the default log file location.<br>
	 * You may also set the default print device,<br>
	 * and choose whether or not to append a time stamp by default.
	 *
	 * @param errorlogLocation
	 *            the destination file of the errors.<br>
	 *            Default: /home/lvuser/errors/errorlog.txt
	 * @param PrintsTo
	 *            is the default print device(s).
	 * @param appendTimeStampByDefault
	 *            is whether or not to append a time stamp to each message by
	 *            default.
	 */
	public ErrorMessage (String errorlogLocation, PrintsTo PrintsTo,
	        boolean appendTimeStampByDefault)
	{

		defaultPrintDevice = PrintsTo;
		appendTimeStamp = appendTimeStampByDefault;

		/* if this directory does not exist, this should create it. */
		final File location = new File(errorlogLocation);

		try
		{

			final boolean created = location.createNewFile();
			System.out.println("Created? " + created + "!");
		}
		catch (final IOException e)
		{
			e.printStackTrace();

		}
		if (location.exists() == false)
		{
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);
			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime()
				        .exec("/bin/touch " + errorlogLocation);

			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			PrintWriter newFile;
			try
			{
				newFile = new PrintWriter(errorlogLocation, "UTF-8");
				newFile.close();
			}
			catch (final FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (final UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		this.errorlogLocation = errorlogLocation;
	}

	/**
	 * Appends date and time of the RIO and match time to the Error Message,
	 * spaced with newlines.
	 *
	 * @param errorMessage
	 *            original error message.
	 * @return original message plus time.
	 */
	private String appendErrorMessage (String errorMessage)
	{
		final String dsMessage = "\nRIOtime: " + rioTime // adds time
		// according to
		// the RIO
		        + "\nMatch: " + matchTime // adds match time
		        + "\n" + errorMessage;
		return dsMessage;
	}

	/** Overwrites the current log. */
	public void clearErrorlog ()
	{
		try
		{
			final PrintWriter log = new PrintWriter(
			        new BufferedWriter(new FileWriter(
			                errorlogLocation, false)));
			log.print("\n");
			log.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	/** deletes the error log from the file system. */
	public void deleteErrorLog ()
	{
		try
		{
			Runtime.getRuntime().exec("/bin/rm -f " + errorlogLocation);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the time from the RoboRIO upon construction. <br>
	 * The method therefore retains the time at which the ErrorMessage is
	 * instantiated.
	 */
	private String getDate ()
	{
		//		String retTime = "Unknown Time...";
		//		String errorMessage = null;
		//		try
		//		{
		//
		//			this.rioTime = this.getDate();
		//			this.matchTime = Hardware.driverStation.getMatchTime();
		//			final Process p = Runtime.getRuntime().exec("date");
		//
		//			if (this.appendTimeStamp == true)
		//			{
		//				errorMessage = this.appendErrorMessage(errorMessage);
		//			}
		//			final BufferedReader stdInput = new BufferedReader(
		//			        new InputStreamReader(p.getInputStream()));
		//			// if the error must print to the Drivers' Station
		//			if ((this.defaultPrintDevice == PrintsTo.driverStation) ||
		//			        (this.defaultPrintDevice == PrintsTo.driverStationAndRoboRIO))
		//			{
		//				final String dsReport =
		//				        this.appendErrorMessage(errorMessage);
		//				DriverStation.reportError(dsReport, false);
		//			}
		//			// if the error must print to the errorlog on the rio.
		//			if ((this.defaultPrintDevice == PrintsTo.roboRIO) ||
		//			        (this.defaultPrintDevice == PrintsTo.driverStationAndRoboRIO))
		//			{
		//				this.PrintsToRIO(errorMessage);
		//			}
		//			String s = null;
		//			// read the output from the command
		//			while ((s = stdInput.readLine()) != null)
		//				retTime = s;
		//		}
		//		catch (final IOException e)
		//		{
		//			String appendedErrorMessage;
		//
		//			this.rioTime = this.getDate();
		//			this.matchTime = Hardware.driverStation.getMatchTime();
		//
		//			if (this.appendTimeStamp == true)
		//			{
		//				appendedErrorMessage =
		//				        this.appendErrorMessage(errorMessage);
		//			}
		//			else
		//			{
		//				appendedErrorMessage = errorMessage;
		//			}
		//
		//			// if the error must print to the Drivers' Station
		//			if ((this.defaultPrintDevice == PrintsTo.driverStation) ||
		//			        (this.defaultPrintDevice == PrintsTo.driverStationAndRoboRIO))
		//			{
		//				final String dsReport =
		//				        this.appendErrorMessage(errorMessage);
		//				DriverStation.reportError(dsReport, false);
		//			}
		//			// if the error must print to the errorlog on the rio.
		//			if ((this.defaultPrintDevice == PrintsTo.roboRIO) ||
		//			        (this.defaultPrintDevice == PrintsTo.driverStationAndRoboRIO))
		//			{
		//				this.PrintsToRIO(appendedErrorMessage);
		//			}
		//			e.printStackTrace();
		//			System.out.println(
		//			        "Something went wrong with fetching the date.");
		//		}
		//return retTime;
		return "";
	}

	/**
	 * Gets the location of the ErrorMessage's log file in the filesystem.
	 *
	 * @return the file path of the error log.
	 */
	public String getLocation ()
	{
		return errorlogLocation;
	}

	/**
	 * Prints error to specified devices.
	 *
	 * @param errorMessage
	 *            to be printed
	 */
	public void printError (String errorMessage)
	{
		String appendedErrorMessage;
		rioTime = getDate();
		matchTime = Hardware.driverStation.getMatchTime();

		if (appendTimeStamp == true)
			appendedErrorMessage = appendErrorMessage(errorMessage);
		else
			appendedErrorMessage = errorMessage;

		// if the error must print to the Drivers' Station
		if (defaultPrintDevice == PrintsTo.driverStation ||
		        defaultPrintDevice == PrintsTo.driverStationAndRoboRIO)
		{
			final String dsReport = appendErrorMessage(errorMessage);
			DriverStation.reportError(dsReport, false);
		}
		// if the error must print to the errorlog on the rio.
		if (defaultPrintDevice == PrintsTo.roboRIO ||
		        defaultPrintDevice == PrintsTo.driverStationAndRoboRIO)
			PrintsToRIO(appendedErrorMessage);
	}

	/**
	 * Prints error to specified devices
	 *
	 * @param errorMessage
	 *            the message to be printed.
	 * @param attatchTimeStamp
	 *            whether or not to include a time stamp.
	 */
	public void printError (String errorMessage,
	        boolean attatchTimeStamp)
	{
		String appendedErrorMessage;
		rioTime = getDate();
		matchTime = Hardware.driverStation.getMatchTime();

		if (appendTimeStamp == true)
			appendedErrorMessage = appendErrorMessage(errorMessage);
		else
			appendedErrorMessage = errorMessage;

		// if the error must print to the Drivers' Station
		if (defaultPrintDevice == PrintsTo.driverStation ||
		        defaultPrintDevice == PrintsTo.driverStationAndRoboRIO)
		{
			final String dsReport = appendErrorMessage(errorMessage);
			DriverStation.reportError(dsReport, false);
		}
		// if the error must print to the errorlog on the rio.
		if (defaultPrintDevice == PrintsTo.roboRIO ||
		        defaultPrintDevice == PrintsTo.driverStationAndRoboRIO)
			PrintsToRIO(appendedErrorMessage);
	}

	/**
	 * Prints the error to the specified devices.
	 *
	 * @param errorMessage
	 *            is the message to be printed.
	 * @param PrintsTo
	 *            determines where to send the debug message to
	 */
	public void printError (String errorMessage, PrintsTo PrintsTo)
	{
		rioTime = "";//getDate();
		matchTime = Hardware.driverStation.getMatchTime();

		// if the error must print to the Drivers' Station
		if (PrintsTo == ErrorMessage.PrintsTo.driverStation ||
		        PrintsTo == ErrorMessage.PrintsTo.driverStationAndRoboRIO)
		{
			final String dsReport = appendErrorMessage(errorMessage);
			DriverStation.reportError(dsReport, false);
		}
		// if the error must print to the errorlog on the rio.
		if (PrintsTo == ErrorMessage.PrintsTo.roboRIO ||
		        PrintsTo == ErrorMessage.PrintsTo.driverStationAndRoboRIO)
			PrintsToRIO(errorMessage);
	}

	/**
	 * Prints the error to the specified devices.
	 *
	 * @param errorMessage
	 *            the error message to be printed.
	 * @param PrintsTo
	 *            the device to which the error will be printed.
	 * @param attatchTimeStamp
	 *            whether or not to append the time stamp.
	 */
	public void printError (String errorMessage, PrintsTo PrintsTo,
	        boolean attatchTimeStamp)
	{
		String appendedErrorMessage;
		rioTime = getDate();
		matchTime = Hardware.driverStation.getMatchTime();

		if (attatchTimeStamp == true)
		{
			appendedErrorMessage = appendErrorMessage(errorMessage);
		}
		else
		{
			appendedErrorMessage = errorMessage;
		}

		// if the error must print to the Drivers' Station
		if (PrintsTo == ErrorMessage.PrintsTo.driverStation ||
		        PrintsTo == ErrorMessage.PrintsTo.driverStationAndRoboRIO)
		{

			this.rioTime = this.getDate();
			this.matchTime = Hardware.driverStation.getMatchTime();
			final String dsReport = appendErrorMessage(errorMessage);
			DriverStation.reportError(dsReport, false);
		}
		// if the error must print to the errorlog on the rio.
		if (PrintsTo == ErrorMessage.PrintsTo.roboRIO ||
		        PrintsTo == ErrorMessage.PrintsTo.driverStationAndRoboRIO)
			PrintsToRIO(appendedErrorMessage);

		if (attatchTimeStamp == true)

		{
			appendedErrorMessage =
			        this.appendErrorMessage(errorMessage);
		}
		else

		{
			appendedErrorMessage = errorMessage;
		}
	}

	/**
	 * Prints the error message to the error log on the RoboRIO.
	 *
	 * @param errorMessage
	 */
	private void PrintsToRIO (String errorMessage)
	{
		//    // if the error must print to the Drivers' Station
		//    if ((defaultPrintDevice == ErrorMessage.PrintsTo.driverStation) ||
		//            (defaultPrintDevice == ErrorMessage.PrintsTo.driverStationAndRoboRIO))
		//        {
		//        final String dsReport = this.appendErrorMessage(errorMessage);
		//        DriverStation.reportError(dsReport, false);
		//        }
		//    // if the error must print to the errorlog on the rio.
		//    if ((defaultPrintDevice == ErrorMessage.PrintsTo.roboRIO) ||
		//            (defaultPrintDevice == ErrorMessage.PrintsTo.driverStationAndRoboRIO))
		//        {
		//        this.PrintsToRIO(errorMessage);
		//        }
		// final String modifiedErrorMessage =
		// appendedErrorMessage.replace('\n', ' '); // removes all
		// newlines.
		try
		{
			final PrintWriter log = new PrintWriter(
			        new BufferedWriter(new FileWriter(
			                errorlogLocation, true)));
			log.print(errorMessage + "\n");
			log.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			System.out.println("More errors with your error?");
		}

	}

	/**
	 * Sets the location of the errorlog.
	 *
	 * @param filepath
	 *            must be the file name plus the full file path.<br>
	 *            Example: /home/lvuser/errors/errorlog.txt
	 */
	public void setLocation (String filepath)
	{
		errorlogLocation = filepath;

		final File location = new File(errorlogLocation);

		if (location.exists() == false)
		{
			final int lastSlash = errorlogLocation.lastIndexOf('/');
			final String errorlogDirectory =
			        errorlogLocation.substring(0,
			                lastSlash);

			try
			{
				Runtime.getRuntime()
				        .exec("/bin/mkdir " + errorlogDirectory);
				Runtime.getRuntime().exec(
				        "/bin/touch \"" + errorlogLocation + "\"");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				location.createNewFile();
			}
			catch (final IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}

	//

	/**
	 * Prints the error message to the error log on the RoboRIO. <br>
	 * Deletes all newlines in the original error message, for formatting and to
	 * be safe.
	 *
	 * DEPRECATED DUE TO LACK OF WORKING...
	 *
	 *
	 * @param errorMessage
	 */
	// private void PrintsToRIO (String errorMessage)
	// {
	// final String modifiedErrorMessage =
	// errorMessage.replace('\n', ' '); // removes all newlines.
	// final String writeCommand =
	// this.writeCommandToBeExecuted(modifiedErrorMessage);
	// // System.out
	// //
	// .println("THE ABSOLUTE FINAL COMMAND TO BE EXECUTED IS AS FOLLOWS: \n -->"
	// // + writeCommand + "<--");
	// try
	// {
	// final Process p =
	// Runtime.getRuntime()
	// .exec(writeCommand);
	//
	// // the following lines should get the shell's output from the
	// // command.
	// final BufferedReader stdInput =
	// new BufferedReader(new
	// InputStreamReader(p.getInputStream()));
	//
	// String s =
	// null;
	// // read the output from the command
	// while ((s =
	// stdInput.readLine()) != null)
	// {
	// System.out.println(s);
	// }
	// }
	// catch (final IOException e)
	// {
	// System.out
	// .println("It seems your error... has an error.");
	// e.printStackTrace();
	// }
	// }

	/**
	 * Inserts message into command. <br>
	 * Appends date and time of the RIO and match time to the Error Message.
	 *
	 * DEPRECATED DUE TO THE USING METHOD NOT WORKING.
	 *
	 * @param message
	 *            is the original error message.
	 * @return command to write message with time to a file on the RoboRIO
	 */
	// private String writeCommandToBeExecuted (String message)
	// {
	// final String writeCommand =
	// "/usr/bin/printf \""
	// + "RIOtime: "
	// + this.rioTime// appends the RoboRIO time.
	// + " Match: "
	// + this.matchTime// appends the match time
	// + " "
	// + message
	// + "\" > " + this.errorlogLocation; // writes to
	// // errorlog.txt, or wherever ERRERLOG_LOCATION is.
	// System.out.println(writeCommand);
	// return writeCommand;

	/**
	 * Appends the date and time to each errorMessage.
	 *
	 * @param append
	 *            will append if true
	 */
	public void turnOnAppendTime (boolean append)
	{
		appendTimeStamp = append;
	}

}
