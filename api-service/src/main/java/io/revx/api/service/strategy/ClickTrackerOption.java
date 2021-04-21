package io.revx.api.service.strategy;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;



@Entity
@Table(name = "ClickTrackerOptions")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ClickTrackerOptions")
public class ClickTrackerOption implements java.io.Serializable {

    private static final long serialVersionUID = -3288784224133903152L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_id", nullable = false)
    private Integer           id;

    @Enumerated(EnumType.STRING)
    @Column(name = "co_type", columnDefinition = "ENUM", nullable = false)
    private TrackerExpiryType trackerExpiryType;

    @Column(name = "co_alternate_url", length = 256)
    private String            url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TrackerExpiryType getTrackerExpiryType() {
        return trackerExpiryType;
    }

    public void setTrackerExpiryType(TrackerExpiryType trackerExpiryType) {
        //System.out.println("setting tracker expiry type");
        this.trackerExpiryType = trackerExpiryType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        //System.out.println("setting tracker url");
        this.url = url;
    }

}
