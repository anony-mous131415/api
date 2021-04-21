package io.revx.api.mysql.entity;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Whitelabeling")
public class WhitelabelingEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wl_id")
	private int id;
	@Column(name = "wl_name")
	private String name;
	@Column(name = "wl_licensee_id")
	private int licenseeId;
	@Column(name = "wl_subdomain")
	private String subDomain;
	@Column(name = "wl_logo_small_url")
	private String logoSm;
	@Column(name = "wl_logo_large_url")
	private String logoLg;

	@Column(name = "wl_fav_icon_url")
	private String favIcon;

	@Column(name = "wl_page_title")
	private String pageTitle;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "wl_theme_id", referencedColumnName = "ct_id")
	private CSSThemeEntity theme;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(int licenseeId) {
		this.licenseeId = licenseeId;
	}

	public String getSubDomain() {
		return subDomain;
	}

	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}

	public String getLogoSm() {
		return logoSm;
	}

	public void setLogoSm(String logoSm) {
		this.logoSm = logoSm;
	}

	public String getLogoLg() {
		return logoLg;
	}

	public void setLogoLg(String logoLg) {
		this.logoLg = logoLg;
	}

	public CSSThemeEntity getTheme() {
		return theme;
	}

	public void setTheme(CSSThemeEntity theme) {
		this.theme = theme;
	}

	public String getFavIcon() {
		return favIcon;
	}

	public void setFavIcon(String favIcon) {
		this.favIcon = favIcon;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	@Override
	public String toString() {
		return "WhitelabelingEntity [id=" + id + ", name=" + name + ", licenseeId=" + licenseeId + ", subDomain="
				+ subDomain + ", logoSm=" + logoSm + ", logoLg=" + logoLg + ", favIcon=" + favIcon + ", pageTitle="
				+ pageTitle + ", theme=" + theme + "]";
	}



}
