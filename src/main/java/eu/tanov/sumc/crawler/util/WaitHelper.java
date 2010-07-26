package eu.tanov.sumc.crawler.util;

import org.apache.log4j.Logger;

public class WaitHelper {

	private static final Logger log = Logger.getLogger(WaitHelper.class.getName());
	
	private static final int WAIT_FOR_POOLING_INTERVAL = 100; // 1/10 sec 

	//helper, without instance
	private WaitHelper() {}

	public static interface Condition {
		public boolean completed();
	}

	/**
	 * @param condition
	 * @param timeout max time to wait, -1 - infinite wait
	 * @return true if condition is reached, or false if condition.completed() always returns false
	 */
	public static boolean waitForCondition(Condition condition, final int timeout) {
		final long endDate = System.currentTimeMillis() + timeout;
		long remaining;

		do {
			if (condition.completed()) {
				return true;
			}

			// prevent negative milliseconds in Thread.sleep()
			remaining = Math.max(endDate - System.currentTimeMillis(), 0);
			try {
				Thread.sleep(Math.min(remaining, WAIT_FOR_POOLING_INTERVAL));
			} catch (InterruptedException e) {
				log.info("in wait for " + condition, e);
			}
		} while (timeout == -1 || remaining > 0);
		
		log.debug("condition not satisfied: "+condition);
		return false;
	}

	
}
