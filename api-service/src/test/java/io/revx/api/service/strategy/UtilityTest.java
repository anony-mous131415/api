package io.revx.api.service.strategy;

import io.revx.api.common.BaseTestService;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.core.model.strategy.StrategyDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class UtilityTest extends BaseTestService {
    @Mock
    TargettingComponentRepository targettingComponentRepository;

    @InjectMocks
    Utility utility;

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
     * Test method for {@link io.revx.api.service.strategy.Utility#findExpressionType(java.lang.String)}.
     */
    @Test
    public void testFindExpressionType() throws Exception{
        String response = utility.findExpressionType("");
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#trimSorroundingBrackets(java.lang.String)}.
     */
    @Test
    public void testTrimSorroundingBrackets() throws Exception{
        String response = utility.trimSorroundingBrackets("(abc)");
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#trimBrackets(java.lang.String)}.
     */
    @Test
    public void testTrimBrackets() throws Exception{
        String response = utility.trimBrackets("(abc)");
        assertNotNull(response);
    }

    @Test
    public void testTrimBracketsWithoutBrackets() throws Exception{
        String response = utility.trimBrackets("abc");
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#trimBracketsAndReturnValidExpression(java.lang.String)}.
     */
    @Test
    public void testTrimBracketsAndReturnValidExpression() throws Exception{
        String response = utility.trimBracketsAndReturnValidExpression("()");
        assertNotNull(response);
    }

    @Test
    public void testTrimBracketsAndReturn() throws Exception{
        String response = utility.trimBracketsAndReturnValidExpression("((abc))");
        assertNotNull(response);
    }

    @Test
    public void testTrimBracketsAndReturnValidExpressionNull() throws Exception{
        String response = utility.trimBracketsAndReturnValidExpression("())");
        assertNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#splitIntoComponentStrings(java.lang.String)}.
     */
    @Test
    public void testSplitIntoComponentStrings() throws Exception{
        List<Integer> list = new ArrayList<>();
        list.add(2);
        List<String> response = utility.splitIntoComponentStrings("23");
        assertNotNull(response);
        List<String> resp = utility.splitIntoComponentStrings("(23a)");
        assertNotNull(resp);
        utility.removeElementFromList(list,2);
        List<String> res = utility.splitIntoComponentStrings("((23a)");
        assertNotNull(res);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#splitStringWithoutBracketsIntoComponents(java.lang.String)}.
     */
    @Test
    public void testSplitStringWithoutBracketsIntoComponents() throws Exception{
        List<String> response = utility.splitStringWithoutBracketsIntoComponents("ads&ads");
        assertNotNull(response);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#splitStringWithBracketsIntoComponents(java.lang.String)}.
     */
    @Test
    public void testSplitStringWithBracketsIntoComponents() throws Exception{
        List<String> response = utility.splitStringWithBracketsIntoComponents("ads");
        assertNotNull(response);
        List<String> resp = utility.splitStringWithBracketsIntoComponents("(ads)");
        assertNotNull(resp);
        List<String> r = utility.splitStringWithBracketsIntoComponents("aa&|ds)");
        assertNotNull(r);
    }

    /**
     * Test method for {@link io.revx.api.service.strategy.Utility#getListOfTCIdsInExpr(java.lang.String)}.
     */
    @Test
    public void testGetListOfTCIdsInExpr() throws Exception{
        List<String> respone = utility.getListOfTCIdsInExpr("1a2");
        assertNotNull(respone);
        List<String> resp = utility.getListOfTCIdsInExpr("1234");
        assertNotNull(resp);
        List<String> r = utility.getListOfTCIdsInExpr("aa1");
        assertNotNull(r);
    }
}
