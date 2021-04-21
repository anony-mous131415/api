package io.revx.core;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Component
public class LocaleResolver extends AcceptHeaderLocaleResolver {

  private static final List<Locale> LOCALES =
      Arrays.asList(new Locale("en"), new Locale("fr"), new Locale("id"));

  private static Logger log = LogManager.getLogger(LocaleResolver.class);

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String language = request.getHeader("Accept-Language");
    if (language == null || language.isEmpty()) {
      return Locale.getDefault();
    }
    List<Locale.LanguageRange> list = Locale.LanguageRange.parse(language);
    Locale locale = Locale.lookup(list, LOCALES);
    log.debug(" Returning locale " + locale.toString() + " for " + language);
    return locale;
  }
}
