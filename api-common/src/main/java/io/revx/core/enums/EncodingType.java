package io.revx.core.enums;


public enum EncodingType {
	NONE(1),

	MD5(2),

	SHA256(3);

	public final Integer id;

	private EncodingType(int id) {
		this.id = id;
	}

	public static EncodingType getById(Integer id) {
		for (EncodingType type : values()) {
			if (type.id.equals(id))
				return type;
		}
		return null;
	}

}
