package io.revx.api.audit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.revx.api.mysql.entity.audit.AuditChange;
import io.revx.api.mysql.entity.audit.AuditLog;
import io.revx.core.exception.NotComparableException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.strategy.ConnectionType;
import io.revx.core.model.targetting.ChangeComparable;
import io.revx.core.model.targetting.Difference;
import io.revx.core.utils.StringUtils;

public abstract class AuditService {

	private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

	public AuditService() {
	}

	protected AuditChange getChange(AuditLog log, String fieldName, String o, String n) {
		if (o.equalsIgnoreCase(n)) {
			return null;
		}

		AuditChange c = new AuditChange();
		c.setFieldName(fieldName);
		c.setNewValue(n);
		c.setOldValue(o);
		return c;
	}

	protected void addChange(AuditLog log, String fieldName, Integer o, Integer n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, String o, String n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, BigDecimal o, BigDecimal n) {
		if ((o == null && n != null) || (o != null && n == null) || (o != null && o.compareTo(n) != 0)) {
			AuditChange change = getChange(log, fieldName, "" + (o == null ? null : o.setScale(9)),
					"" + (n == null ? null : n.setScale(9)));
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, Long o, Long n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, Boolean o, Boolean n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, Enum o, Enum n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(change);
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, BaseModel o, BaseModel n) {
		try {
			if ((o == null && n != null) || (o != null && !o.getId().equals(n.getId()))) {
				AuditChange change = getChange(log, fieldName, o == null ? "null" : o.getName(), n.getName());
				if (change != null) {
					log.getAuditChanges().add(change);
				}
			}
		} catch (LazyInitializationException e) {
			logger.debug("not logging change for " + fieldName, e);
		}
	}

	protected void addChange(AuditLog log, String fieldName, ChangeComparable o, ChangeComparable n) {
		Difference diff;
		if (o == null && n == null)
			return;
		try {
			diff = n.compareTo(o);
			if (diff != null && diff.different) {
				AuditChange change = getChange(log, fieldName, "" + diff.oldValue, "" + diff.newValue);
				if (change != null) {
					log.getAuditChanges().add(change);
				}
			}
		} catch (NotComparableException e) {
			logger.warn("not logging change for " + fieldName, e);
		}
	}

	protected void addChange(AuditLog log, String fieldName, Object o, Object n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(getChange(log, fieldName, "" + o, "" + n));
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, List<BaseModel> o, List<BaseModel> n) {
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, StringUtils.formatBaseModel(o),
					StringUtils.formatBaseModel(n));
			if (change != null) {
				log.getAuditChanges()
						.add(getChange(log, fieldName, StringUtils.formatBaseModel(o), StringUtils.formatBaseModel(n)));
			}
		}
	}

	protected void addChange(AuditLog log, String fieldName, Set<ConnectionType> o, Set<ConnectionType> n) {
		if (o == null || o.isEmpty()) {
			o = new HashSet<ConnectionType>(Arrays.asList(ConnectionType.values()));
		}
		if (n == null || n.isEmpty()) {
			n = new HashSet<ConnectionType>(Arrays.asList(ConnectionType.values()));
		}
		if ((o == null && n != null) || (o != null && !o.equals(n))) {
			AuditChange change = getChange(log, fieldName, "" + o, "" + n);
			if (change != null) {
				log.getAuditChanges().add(getChange(log, fieldName, "" + o, "" + n));
			}
		}
	}
}
