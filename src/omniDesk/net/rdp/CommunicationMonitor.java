/**
 * 
 */
package omniDesk.net.rdp;

/**
 * @author akshay
 *
 */
public class CommunicationMonitor {
	public static Object locker = null;
		
	    /**
	     * Identify whether or not communications are locked
	     * @return True if locked
	     */
		public static boolean locked(){
			return locker != null;
		}
		
	    /**
	     * Wait for a lock on communications
	     * @param o Calling object should supply reference to self
	     */
		public static void lock(Object o){
			if(locker == null) locker = o;
			else{
				while(locker != null){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.err.println("InterruptedException: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		
	    /**
	     * Unlock communications, only permitted if the caller holds the current lock
	     * @param o Calling object should supply reference to self
	     * @return
	     */
		public static boolean unlock(Object o){
			if(locker == o){
				locker = null;
				return true;
			}
			return false;
		}
		
}
