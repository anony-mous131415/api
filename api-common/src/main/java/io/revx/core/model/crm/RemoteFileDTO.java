package io.revx.core.model.crm;

import io.revx.core.enums.AuthMethod;
import io.revx.core.enums.Protocol;

public class RemoteFileDTO {

	protected Protocol protocol;

	protected String host;

	protected Integer port;

	protected AuthMethod authMethod;

	protected String keyPath;

	protected String username;

	protected String password;

	protected String pathTemplate;

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public AuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}

	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}

	@Override
	public String toString() {
		return "RemoteFile [protocol=" + protocol + ", host=" + host
				+ ", port=" + port + ", authMethod=" + authMethod
				+ ", keyPath=" + keyPath + ", username=" + username
				+ ", pathTemplate=" + pathTemplate
				+ "]";
	}

}
