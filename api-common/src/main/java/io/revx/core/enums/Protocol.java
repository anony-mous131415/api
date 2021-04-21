package io.revx.core.enums;
/**
 * Refer: http://www.jscape.com/blog/bid/75602/Understanding-Key-Differences-Between-FTP-FTPS-and-SFTP
 * 
 * @author Ratnesh Kumar Deepak
 *
 */
public enum Protocol {
	FTP(1, 21),

	SFTP(2, 22),

	SSH(3, 22),

	HTTP(4, 80),

	HTTPS(5, 443),

	FTPS(6, 990);

	public final Integer id;

	public final Integer defaultPort;

	private Protocol(int id, int defaultPort) {
		this.id = id;
		this.defaultPort = defaultPort;
	}

	public static Protocol getById(Integer id) {
		for (Protocol type : values()) {
			if (type.id.equals(id))
				return type;
		}
		return null;
	}
}