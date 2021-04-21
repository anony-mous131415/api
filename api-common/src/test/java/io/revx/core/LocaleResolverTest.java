/**
 * 
 */
package io.revx.core;

import java.util.List;
import java.util.Locale;
import org.junit.Test;
import mockit.Injectable;
import mockit.Tested;

/**
 * @author amaurya
 *
 */
public class LocaleResolverTest {
  @Injectable
  private Locale defaultLocale;

  @Injectable
  private List<Locale> supportedLocales;
  @Tested
  private LocaleResolver localeResolver;

  /**
   * Test method for
   * {@link io.revx.core.LocaleResolver#resolveLocale(javax.servlet.http.HttpServletRequest)}.
   */
  @Test
  public void testResolveLocale() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

}
