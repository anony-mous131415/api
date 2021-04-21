package io.revx.api.service.campaign;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;
import io.revx.api.mysql.repo.advertiser.CurrencyRepository;
import io.revx.core.model.BaseEntity;
import io.revx.core.service.CacheService;
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
public class CurrencyCacheServiceTest extends BaseTestService {
    @Mock
    private CacheService cacheService;

    @Mock
    private CurrencyRepository currency;

    @InjectMocks
    private CurrencyCacheService currencyCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CurrencyCacheService#fetchCurrencyByCode(java.lang.String)}
     */
    @Test
    public void testFetchCurrencyByCode() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCurrencyEntity());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(list);
        CurrencyEntity response = currencyCacheService.fetchCurrencyByCode("test");
        assertNotNull(response);
        assertEquals("Test",response.getCurrencyName());
    }

    @Test
    public void testFetchCurrencyByCodeNull() throws Exception{
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(null);
        CurrencyEntity response = currencyCacheService.fetchCurrencyByCode("test");
        assertNull(response);
    }

    /**
     *  Test method for {@Link io.revx.api.service.campaign.CurrencyCacheService#fetchCurrencyByName(java.lang.String)}
     */
    @Test
    public void testFetchCurrencyByName() throws Exception{
        List<BaseEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createCurrencyEntity());
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(list);
        CurrencyEntity response = currencyCacheService.fetchCurrencyByName("test");
        assertNotNull(response);
        assertEquals("Test",response.getCurrencyName());
    }

    @Test
    public void testFetchCurrencyByNameNull() throws Exception{
        Mockito.when(cacheService.fetchListCachedEntityData(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(null);
        CurrencyEntity response = currencyCacheService.fetchCurrencyByName("test");
        assertNull(response);
    }
}
