package io.revx.api.service.strategy;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author praveen
 */
@Embeddable
public class AdvertiserBudgetStatsPK implements Serializable {
    private static final long serialVersionUID = -6758163240065746149L;

    @Basic(optional = false)
    @Column(name = "abs_adv_li_id", nullable = false)
    private Integer           advertiserLineItemId;

    @Basic(optional = false)
    @Column(name = "abs_hour_start_time", nullable = false)
    private Long              intervalStartTime;

    @Basic(optional = true)
    @Column(name = "abs_is_learning", columnDefinition = "BIT", length = 1)
    private boolean           isLearning;

    public AdvertiserBudgetStatsPK() {
    }

    public AdvertiserBudgetStatsPK(Integer advertiserLineItemId, Long intervalStartTime,
            Boolean isLearning) {
        this.advertiserLineItemId = advertiserLineItemId;
        this.intervalStartTime = intervalStartTime;
        this.isLearning = isLearning;
    }

    public Integer getAdvertiserLineItemId() {
        return advertiserLineItemId;
    }

    public void setAdvertiserLineItemId(Integer advertiserLineItemId) {
        this.advertiserLineItemId = advertiserLineItemId;
    }

    public Long getIntervalStartTime() {
        return intervalStartTime;
    }

    public void setIntervalStartTime(Long intervalStartTime) {
        this.intervalStartTime = intervalStartTime;
    }

    public boolean isLearning() {
        return isLearning;
    }

    public void setLearning(boolean isLearning) {
        this.isLearning = isLearning;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += advertiserLineItemId;
        hash += intervalStartTime;
        hash += (isLearning ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AdvertiserBudgetStatsPK)) {
            return false;
        }
        AdvertiserBudgetStatsPK other = (AdvertiserBudgetStatsPK) object;
        if (this.advertiserLineItemId != other.advertiserLineItemId) {
            return false;
        }
        if (this.intervalStartTime != other.intervalStartTime) {
            return false;
        }
        if (this.isLearning != other.isLearning) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AdvertiserBudgetStatsPK[advertiserLineItemId=" + advertiserLineItemId
                + ", intervalStartTime=" + intervalStartTime + ", isLearning=" + isLearning + "]";
    }

}
