package io.revx.api.mysql.dco.entity.catalog;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.enums.CatalogVariableValueType;

@Entity
@Table(name = "AdvertiserCatalogVariablesMapping")
public class AdvertiserCatalogVariablesMappingEntity implements Serializable {
	
	private static final long serialVersionUID = 1091227001657141221L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acvm_id", nullable = false)
    private Long  id;
	
    @Column(name = "acvm_feed_id", nullable = false)
	private Long feedId;
	
    @Column(name = "acvm_atom_variable_id", nullable = false)
	private Long atomVariable;
	  
	@Column(name = "acvm_xpath", length = 128, nullable = false)
	private String xPath;
	
	@Column(name = "acvm_is_multivalued", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean isMultivalued;
	
	@Column(name = "acvm_value_type", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
    private CatalogVariableValueType valueType;
	
	@Column(name = "acvm_select_position", nullable = false)
	private Long selectPosition;
	
	@Column(name = "acvm_sort_type", columnDefinition = "ENUM", nullable = false)
	@Enumerated(EnumType.STRING)
    private SortType sortType;

    @Column(name = "acvm_value_parsing_rule", length = 128, nullable = false)
    private String   valueParsingRule;
    
	

  public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Long getFeedId() {
      return feedId;
    }

    public void setFeedId(Long feedId) {
      this.feedId = feedId;
    }

    public Long getAtomVariable() {
      return atomVariable;
    }

    public void setAtomVariable(Long atomVariable) {
      this.atomVariable = atomVariable;
    }

    public String getxPath() {
      return xPath;
    }

    public void setxPath(String xPath) {
      this.xPath = xPath;
    }

    public Boolean getIsMultivalued() {
      return isMultivalued;
    }

    public void setIsMultivalued(Boolean isMultivalued) {
      this.isMultivalued = isMultivalued;
    }

    public CatalogVariableValueType getValueType() {
      return valueType;
    }

    public void setValueType(CatalogVariableValueType valueType) {
      this.valueType = valueType;
    }

    public Long getSelectPosition() {
      return selectPosition;
    }

    public void setSelectPosition(Long selectPosition) {
      this.selectPosition = selectPosition;
    }

    public SortType getSortType() {
      return sortType;
    }

    public void setSortType(SortType sortType) {
      this.sortType = sortType;
    }

    public String getValueParsingRule() {
      return valueParsingRule;
    }

    public void setValueParsingRule(String valueParsingRule) {
      this.valueParsingRule = valueParsingRule;
    }

  @Override
    public String toString() {
      return "AdvertiserCatalogVariablesMapping [id=" + id + ", feedId=" + feedId
          + ", atomVariable=" + atomVariable + ", xPath=" + xPath + ", isMultivalued="
          + isMultivalued + ", valueType=" + valueType + ", selectPosition=" + selectPosition
          + ", sortType=" + sortType + ", valueParsingRule=" + valueParsingRule + "]";
    }

  public enum SortType {
		CATALOG(0), ASC(1), DESC(2);

	    private int value;
	    
	    private SortType(int value) {
	    	this.value = value;
	    }
	    
		public SortType get(int value) {
			switch (value) {
			case 0:
				return SortType.CATALOG;
			case 1:
				return SortType.ASC;
			case 2:
				return SortType.DESC;
			default:
				return null;
			}
		}
	    
	    public int getValue() {
	    	return this.value;
	    }
	}
}
