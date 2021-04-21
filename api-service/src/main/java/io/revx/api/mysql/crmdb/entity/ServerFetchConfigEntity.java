package io.revx.api.mysql.crmdb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import io.revx.core.enums.AuthMethod;
import io.revx.core.enums.CompressionType;
import io.revx.core.enums.EncodingType;
import io.revx.core.enums.Protocol;
import io.revx.core.model.audience.UserDataType;

@Entity
@Table(name = "ServerFetchConfig")
public class ServerFetchConfigEntity implements IDto, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588364472353701L;

	@Id
	@Column(name = "sfc_id", unique = true, nullable = false)
	@GeneratedValue(generator = "gen")
	@GenericGenerator(name = "gen", strategy = "foreign", parameters = @Parameter(name = "property", value = "coordinator"))
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "sfc_protocol", nullable = false)
	private Protocol protocol;

	@Column(name = "sfc_server_host", nullable = false)
	private String host;

	@Column(name = "sfc_file_path", nullable = false)
	private String pathTemplate;

	@Column(name = "sfc_server_port", nullable = true)
	private Integer port;

	@Column(name = "sfc_auth_method", nullable = true)
	@Enumerated(EnumType.STRING)
	private AuthMethod authMethod;

	@Column(name = "sfc_username", nullable = true)
	private String username;

	@Column(name = "sfc_password", nullable = true)
	private String password;

	@Column(name = "sfc_key_path", nullable = true)
	private String keyPath;

	@Column(name = "sfc_pixel_id", nullable = false)
	private Long pixelId;

	@Column(name = "sfc_licensee_id", nullable = false)
	private Long licenseeId;

	@Column(name = "sfc_encoding_type", nullable = true)
	@Enumerated(EnumType.STRING)
	private EncodingType encodingType;

	@Column(name = "sfc_user_data_type", nullable = true)
	@Enumerated(EnumType.STRING)
	private UserDataType userDataType;

	@Column(name = "sfc_file_compression_type", nullable = true)
	@Enumerated(EnumType.STRING)
	private CompressionType compressionType;

	@OneToOne
	@PrimaryKeyJoinColumn
	private ServerSyncCoordinatorEntity coordinator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}

	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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

	public Long getPixelId() {
		return pixelId;
	}

	public void setPixelId(Long pixelId) {
		this.pixelId = pixelId;
	}

	public ServerSyncCoordinatorEntity getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(ServerSyncCoordinatorEntity coordinator) {
		this.coordinator = coordinator;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public AuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}

	public EncodingType getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(EncodingType encodingType) {
		this.encodingType = encodingType;
	}

	public UserDataType getUserDataType() {
		return userDataType;
	}

	public void setUserDataType(UserDataType userDataType) {
		this.userDataType = userDataType;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public ServerFetchConfigEntity setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
		return this;
	}

	public CompressionType getCompressionType() {
		return compressionType;
	}

	public void setCompressionType(CompressionType compressionType) {
		this.compressionType = compressionType;
	}

}