package io.revx.api.audit;

import io.revx.api.common.BaseTestService;
import io.revx.api.mysql.repo.audit.AuditChangeRepository;
import io.revx.api.mysql.repo.audit.AuditLogRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.model.strategy.StrategyDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyAuditServiceTest extends BaseTestService {

    @InjectMocks
    private StrategyAuditService auditService;

    @Mock
    LoginUserDetailsService loginUserDetailsService;

    @Mock
    AuditLogRepository auditLogRepository;

    @Mock
    AuditChangeRepository changeRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        auditService.auditLogRepository = auditLogRepository;
        auditService.loginUserDetailsService = loginUserDetailsService;
        auditService.changeRepository = changeRepository;
    }

    @Test(expected = Test.None.class)
    public void testAudit() throws Exception {
        mockSecurityContext("akhilesh", false, false);
        StrategyDTO o = new StrategyDTO();
        o.id = 1234L;
        o.setActive(false);
        StrategyDTO n = new StrategyDTO();
        n.id = 1234L;
        n.setActive(true);
        auditService.audit(o,n);
    }
}
