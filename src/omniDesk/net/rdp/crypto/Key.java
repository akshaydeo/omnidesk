/**
 * 
 */
package omniDesk.net.rdp.crypto;

import java.io.Serializable;

/**
 * @author akshay
 *
 */
public interface Key extends Serializable {
	static long serialVersionUID = 6603384152749567654l;

	String getAlgorithm();

	byte[] getEncoded();

	String getFormat();
}
