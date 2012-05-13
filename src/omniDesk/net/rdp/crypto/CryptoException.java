package omniDesk.net.rdp.crypto;

@SuppressWarnings("serial")
public class CryptoException extends Exception {
	public CryptoException() { super(); }
    /** @param reason  the reason why the exception was thrown. */
    public CryptoException(String reason) { super(reason); }   
}
