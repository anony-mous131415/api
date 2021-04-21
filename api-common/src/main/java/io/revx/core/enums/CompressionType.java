package io.revx.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum CompressionType {
    UNKNOWN(0),
  
    NONE(1),

	ZIP(2),

	GZIP(3),

	BZIP2(4);

	public final Integer id;

	private CompressionType(int id) {
		this.id = id;
	}

	private static HashMap<String, CompressionType> extensionMap = new HashMap<String, CompressionType>();

	static {
		extensionMap.put(".zip", ZIP);
		extensionMap.put(".gz", GZIP);
		extensionMap.put(".bz2", BZIP2);
	}

	public static CompressionType getCompressionByExtension(String fileName) {
		if (fileName == null)
			return null;
		fileName = fileName.toLowerCase();
		for (Map.Entry<String, CompressionType> entry : extensionMap.entrySet()) {
			if (fileName.endsWith(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}

	public static CompressionType getById(Integer id) {
		for (CompressionType type : values()) {
			if (type.id.equals(id))
				return type;
		}
		return null;
	}

}
