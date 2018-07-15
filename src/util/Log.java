package util;

import visual.Login;

import java.time.LocalDate;

/**
 * A logger that prints to console with reference to Logger.
 */
public class Log {

	/**
	 * Logs to a log.txt file with reference to Logger when an object has an id.
	 *
	 * @param log A logger to log messages for a specific system or application component.
	 * @param id  an id.
	 * @param str the message to log.
	 */
	public static void logID(Object log, int id, String str) {
		Login.logger.info(String.format("LOG [%s][%s]:\t%s%s", log.toString(), id, str, System.lineSeparator()));
	}

	/**
	 * Logs to a log.txt file with reference to Logger when an object has a date associated with it.
	 * @param log A logger to log messages for a specific system or application component.
	 * @param date a date
	 * @param str the message to log
	 */
	public static void logDate(Object log, LocalDate date, String str){
		Login.logger.info(String.format("LOG [%s]:\t%s%s%s", log.toString(), str, date.toString(), System.lineSeparator()));
	}

	/**
	 * Logs to a log.txt file  with reference to Logger when an object does not have an id.
	 *
	 * @param log A logger to log messages for a specific system or application component.
	 * @param str the message to log.
	 */
	public static void log(Object log, String str) {
		Login.logger.info(String.format("LOG [%s]:\t %s%s", log.toString(), str, System.lineSeparator()));
	}


}
