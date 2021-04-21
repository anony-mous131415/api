package io.revx.core.enums;

public enum AuthMethod {
	NONE(1),

	LOGIN(2),

	PRIVATE_KEY(3);

	public final Integer id;

	private AuthMethod(int id) {
		this.id = id;
	}

	public static AuthMethod getById(Integer id) {
		for (AuthMethod type : values()) {
			if (type.id.equals(id))
				return type;
		}
		return null;
	}

}
