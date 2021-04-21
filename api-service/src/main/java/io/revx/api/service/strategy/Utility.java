package io.revx.api.service.strategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;


@Component
public class Utility {

  @Autowired
  TargettingComponentRepository targettingComponentRepository;

  private static final Logger logger = LoggerFactory.getLogger(Utility.class);

  public String findFirstIdInExpression(String expr) {
    String firstId = "";
    int startIndex = 0, endIndex = 0;
    int length = expr.length();
    boolean transition = false;

    try {
      Integer val = Integer.parseInt(expr);
      if (val != null)
        return expr;
    } catch (NumberFormatException ex) {
      for (int i = 0; i < length; i++) {
        char ch = expr.charAt(i);
        if (Character.isDigit(ch)) {
          if (transition == false) {
            startIndex = i;
            transition = true;
          }
        }

        if (!(Character.isDigit(ch))) {
          if (transition == true) {
            endIndex = i;
            break;
          }
        }
      }
    }

    firstId = expr.substring(startIndex, endIndex);
    return firstId;
  }

  public String findExpressionType(String expr) {
    logger.debug("expr : " + expr);
    String tcIdString = "";
    int length = expr.trim().length();
    tcIdString = trimSorroundingBrackets(expr);
    if (tcIdString == null || tcIdString.length() == 0)
      return "";
    logger.debug("expr after trimming of brackets : " + tcIdString);
    long tcId = Long.parseLong(tcIdString);
    logger.debug(" targettingComponentRepository {} , ", targettingComponentRepository);
    Optional<TargetingComponent> tc = targettingComponentRepository.findById(tcId);
    if (tc.isPresent()) {
      int tfId = tc.get().getTargetingFilterId().intValue();
      logger.debug("Targeting Filter id for tcId " + tcId + " is : " + tfId);
      switch (tfId) {
        case 1:
          // DAY_OF_WEEK
          return TargetingConstants.DAYPART;
        case 2:
          // HOUR_OF_DAY
          return TargetingConstants.DAYPART;
        case 3:
          // GEO_CITY
          return TargetingConstants.GEOGRAPHIES;
        case 4:
          // GEO_REGION
          return TargetingConstants.GEOGRAPHIES;
        case 5:
          // GEO_COUNTRY
          return TargetingConstants.GEOGRAPHIES;
        case 6:
          // BROWSER
          return TargetingConstants.BROWSERS;
        case 7:
          // OS
          return TargetingConstants.OPERATINGSYSTEM;
        case 8:
          // LANGUAGE
          break;
        case 9:
          // RESOLUTION
          break;
        case 10:
          // FOLD_POSITION
          break;
        case 11:
          // USER_SEGMENT
          return TargetingConstants.SEGMENTS;
        case 12:
          // RTB_AGGREGATOR
          return TargetingConstants.RTBAGGREGATORS;
        case 13:
          // RTB_PUBLISHER
          break;
        case 14:
          // RTB_SITE
          return TargetingConstants.RTBSITES;
        case 15:
          // RTB_PUB_CATEGORY
          break;
        case 16:
          // DEV_BRAND - Mobile device brand
          return TargetingConstants.MOBILEDEVICEBRANDS;
        case 17:
          // DEV_MODEL - Mobile device model
          return TargetingConstants.MOBILEDEVICEMODELS;
        case 18:
          // PLACEMENT - Placement
          return TargetingConstants.PLACEMENTS;
        case 19:
          // DEV_TYPE - Mobile Device Type
          return TargetingConstants.MOBILEDEVICETYPES;
        case 20:
          // OS_VERSION - Mobile OS Version
          return TargetingConstants.MOBILEOSVERSIONS;
        case 21:
          // OS_VERSION - Mobile OS Version
          return TargetingConstants.CONNECTIONTYPE;
      }
    }
    return null;
  }

  public String trimSorroundingBrackets(String str) {
    String strResult = "";

    int length = str.trim().length();
    if (str.startsWith("(") && str.endsWith(")")) {
      strResult = trimSorroundingBrackets(str.trim().substring(1, length - 1).trim());
    } else
      strResult = str.trim();

    return strResult;
  }

  public String trimBrackets(String str) {
    if (str != null && str.length() > 0) {
      if (str.trim().startsWith("(") && str.trim().endsWith(")")) {
        int length = str.trim().length();
        str = str.trim().substring(1, length - 1).trim();
      } else
        str = str.trim();
    }

    return str;
  }

  public String trimBracketsAndReturnValidExpression(String str) {
    String expr = str;
    while (checkValidExpressionString(str).equals(Boolean.TRUE)) {
      expr = str;
      str = trimBrackets(str);
      if (str.equals(expr) || (!str.contains("(") && !str.contains(")")))
        break;
    }

    if (checkValidExpressionString(str).equals(Boolean.TRUE))
      return str;
    else if (checkValidExpressionString(expr).equals(Boolean.TRUE))
      return expr;
    else
      return null;
  }

  public Boolean checkValidExpressionString(String str) {
    Integer counter = 0;
    if (str != null && str.length() > 0) {
      for (int i = 0; i < str.length(); i++) {
        Character ch = str.charAt(i);
        if (ch.equals('('))
          counter++;
        else if (ch.equals(')'))
          counter--;

        if (counter < 0)
          break;
      }

      if (counter == 0)
        return Boolean.TRUE;
      else
        return Boolean.FALSE;
    }

    return Boolean.FALSE;
  }

  public List<String> splitIntoComponentStrings(String str) {
    List<String> list = new ArrayList<String>();
    str = str.trim();

    try {
      Integer val = Integer.parseInt(str);
      if (val != null)
        list.add(str);
    } catch (NumberFormatException ex) {
      if (str.startsWith("(") && str.endsWith(")")) {
        int brCount = 1, start = 0, end = 0;
        for (int i = 1; i < str.length(); i++) {
          Character ch = str.charAt(i);
          if (ch.equals('(')) {
            if (brCount == 0)
              start = i;
            brCount++;
          } else if (ch.equals(')')) {
            brCount--;
            if (brCount == 0) {
              end = i;
              if (start < end) {
                String st = str.substring(start, end + 1);
                if (st != null && st.length() > 0)
                  list.add(st);
              }
            }
          }
        }
      } else if (!str.contains("(") && !str.contains(")")) {
        list = splitStringWithoutBracketsIntoComponents(str);
      } else if (str.contains("(") && str.contains(")")) {
        list = splitStringWithBracketsIntoComponents(str);
      } else {
        // TODO Error handling
      }
    }

    return list;
  }

  public List<String> splitStringWithoutBracketsIntoComponents(String str) {
    List<String> list = new ArrayList<String>();
    int length = str.length();
    int start = 0, end = 0;
    for (int i = 0; i < str.length(); i++) {
      Character ch = str.charAt(i);
      if (ch.equals('&') || ch.equals('|')) {
        end = i - 1;
        if (start < end) {
          String st = str.substring(start, end + 1);
          if (st != null && st.length() > 0) {
            int l = list.size();
            if (l <= 0)
              list.add(st);
            else if (l > 0 && !st.equals(list.get(l - 1)))
              list.add(st);
          }
        }

        if (length > i + 1)
          start = i + 1;

      }
    }

    end = length - 1;
    if (start < end) {
      String st = str.substring(start, end + 1);
      if (st != null && st.length() > 0) {
        int l = list.size();
        if (l <= 0)
          list.add(st);
        else if (l > 0 && !st.equals(list.get(l - 1)))
          list.add(st);
      }
    }

    return list;
  }

  public List<String> splitStringWithBracketsIntoComponents(String str) {
    List<String> list = new ArrayList<String>();
    int length = str.length();
    int brCount = 0, start = 0, end = 0;
    for (int i = 0; i < str.length(); i++) {
      Character ch = str.charAt(i);
      if (ch.equals('(')) {
        if (brCount == 0)
          start = i;
        brCount++;
      } else if (ch.equals(')')) {
        brCount--;
        if (brCount == 0) {
          end = i;
          if (start < end) {
            String st = str.substring(start, end + 1);
            if (st != null && st.length() > 0) {
              int l = list.size();
              if (l <= 0)
                list.add(st);
              else if (l > 0 && !st.equals(list.get(l - 1)))
                list.add(st);
            }
          }
        }
      } else if (ch.equals('&') || ch.equals('|')) {
        if (brCount == 0) {
          end = i - 1;
          if (start < end) {
            String st = str.substring(start, end + 1);
            if (st != null && st.length() > 0) {
              int l = list.size();
              if (l <= 0)
                list.add(st);
              else if (l > 0 && !st.equals(list.get(l - 1)))
                list.add(st);
            }
          }

          if (length > i + 1)
            start = i + 1;
        }
      }
    }

    end = length - 1;
    if (start < end) {
      String st = str.substring(start, end + 1);
      if (st != null && st.length() > 0) {
        int l = list.size();
        if (l <= 0)
          list.add(st);
        else if (l > 0 && !st.equals(list.get(l - 1)))
          list.add(st);
      }
    }
    return list;
  }

  public void deleteOldTargetingComponents(List<String> tcIds) {
    // DOnt DO AnYthing
  }

  public List<String> getListOfTCIdsInExpr(String expr) {
    // (123&(124|(125&(126|127))))
    List<String> tcIds = new ArrayList<String>();
    int startIndex = 0, endIndex = 0;

    boolean isCurrentDigit = false;

    for (int i = 0; i < expr.length(); i++) {
      char ch = expr.charAt(i);
      if (Character.isDigit(ch)) {
        if (isCurrentDigit == false) {
          isCurrentDigit = true;
          startIndex = i;
        }
      } else {
        if (isCurrentDigit == true) {
          isCurrentDigit = false;
          endIndex = i;
          tcIds.add(expr.substring(startIndex, endIndex));
        }
      }
    }

    if (startIndex == endIndex && expr != null && expr.length() > 0) {
      tcIds.add(expr);
    } else if (endIndex < startIndex && startIndex < (expr.length() - 1)) {
      String subStr = expr.substring(startIndex, expr.length());
      if (subStr != null && subStr.length() > 0) {
        // logger.debug("Targeting Component id to be removed is :" + subStr);
        tcIds.add(subStr);
      }
    }

    return tcIds;
  }

  public void removeElementFromList(List<Integer> list, Integer elementToBeRemoved) {
    Iterator<Integer> it = list.iterator();
    while (it.hasNext()) {
      if (it.next().equals(elementToBeRemoved)) {
        it.remove();
        break;
      }
    }
  }
}
