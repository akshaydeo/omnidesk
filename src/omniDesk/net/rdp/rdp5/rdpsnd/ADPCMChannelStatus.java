/* Subversion properties, do not modify!
 * 
 * $Date$
 * $Revision$
 * $Author$
 */

package omniDesk.net.rdp.rdp5.rdpsnd;

public class ADPCMChannelStatus {
	public int predictor = 0;

	public short step_index = 0;

	public int step = 0;

	/* for encoding */
	public int prev_sample;

	/* MS version */
	public short sample1;

	public short sample2;

	public int coeff1;

	public int coeff2;

	public int idelta;
}