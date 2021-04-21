package io.revx.api.service.clickdestination;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.requests.SearchRequest;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClickDestinationCacheServiceTest extends BaseTestService {
    @Mock
    private CacheService cacheService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @Mock
    private ModelConverterService modelConverter;

    @Mock
    private ClickDestinationRepository repository;

    @InjectMocks
    ClickDestinationCacheService clickDestinationCacheService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clickDestinationCacheService.cacheService = cacheService;
    }

    /**
     * Test method for {@link io.revx.api.service.clickdestination.ClickDestinationCacheService#fetchClickDestination(SearchRequest, String, boolean)}.
     */
    @Test
    public void testFetchClickDestination() throws Exception{
        SearchRequest request = new SearchRequest();
        List<ClickDestinationEntity> list = new ArrayList<>();
        list.add(MockDataGenerator.createClickDestinationEntity());
        Mockito.when(repository.findAllByLicenseeIdAndIsRefactored(Mockito.anyLong(),Mockito.anyBoolean()))
                .thenReturn(list);
        List<BaseEntity> response = clickDestinationCacheService.fetchClickDestination(request,"test",true);
        assertNotNull(response);
    }
}
