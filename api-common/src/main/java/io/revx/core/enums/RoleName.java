package io.revx.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum RoleName {
	SADMIN, ADMIN, RW, RO, DEMO,
	/**
	 * adding a new role INTERNAL. This is not a hierarchical role, should see RevX
	 * cost and revenue but should not be able to create and update.
	 */
	INTERNAL;

	private static Map<String, RoleName> nameOrdinalRoleMapping = new HashMap<String, RoleName>();
	static {
		for (RoleName role : RoleName.values()) {
			nameOrdinalRoleMapping.put(role.name(), role);
			nameOrdinalRoleMapping.put(String.valueOf(role.ordinal()), role);
			// This mapping we are putting for Spring Security
			nameOrdinalRoleMapping.put("ROLE_" + role.name(), role);
		}
	}

	public static RoleName getRoleByName(String role) {
		return nameOrdinalRoleMapping.get(role);

	}

	public static RoleName getRoleByOrdinal(int ordinal) {
		return nameOrdinalRoleMapping.get(String.valueOf(ordinal));
	}

	public String getRoleNameForSpringSecurity() {
		return "ROLE_" + this.name();
	}
}
