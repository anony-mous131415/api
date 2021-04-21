package io.revx.api.service.audience.impl;

import io.revx.api.audience.pojo.MetaRulesDto;
import io.revx.api.audience.pojo.RuleFiltersDto;
import io.revx.api.audience.pojo.RuleOperatorsDto;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.amtdb.entity.RuleFilter;
import io.revx.api.mysql.amtdb.entity.RuleOperator;
import io.revx.api.mysql.amtdb.repo.RuleFilterRepository;
import io.revx.api.mysql.amtdb.repo.RuleOperatorRepository;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class RuleServiceImplTest extends BaseTestService {
    @Mock
    RuleFilterRepository ruleFilterDao;

    @Mock
    RuleOperatorRepository ruleOperatorDao;

    @InjectMocks
    RuleServiceImpl ruleService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ruleService.ruleFilterDao = ruleFilterDao;
        ruleService.ruleOperatorDao = ruleOperatorDao;
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.RuleServiceImpl#getAllRules()}.
     */
    @Test
    public void testGetAllRules() throws Exception{
        List<RuleFilter> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleFilter());
        Mockito.when(ruleFilterDao.findAll()).thenReturn(list);
        ApiResponseObject<MetaRulesDto> response = ruleService.getAllRules();
        assertNotNull(response);
        assertEquals(1,response.getRespObject().getMetaRules().size());
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.RuleServiceImpl#getFilters()}.
     */
    @Test
    public void testGetFilters() throws Exception{
        List<RuleFilter> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleFilter());
        Mockito.when(ruleFilterDao.findAll()).thenReturn(list);
        RuleFiltersDto response = ruleService.getFilters();
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.audience.impl.RuleServiceImpl#getOperators()}.
     */
    @Test
    public void testGetOperators() throws Exception{
        List<RuleOperator> list = new ArrayList<>();
        list.add(MockDataGenerator.getRuleOperator());
        Mockito.when(ruleOperatorDao.findAll()).thenReturn(list);
        RuleOperatorsDto response = ruleService.getOperators();
        assertNotNull(response);
    }
}
