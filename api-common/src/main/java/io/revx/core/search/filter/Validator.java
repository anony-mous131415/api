package io.revx.core.search.filter;

import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.DashboardData;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.utils.BeanUtils;
import io.revx.querybuilder.enums.Filter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class Validator {
  private static final Logger logger = LogManager.getLogger(Validator.class);

  /**
   * This method generates complex expression object, which will be used by DAO layer to fetch the
   * data. <br/>
   * 
   * @param filterString - the filter string supplied <br/>
   * @param entity - an entity name, this is required for us to get the corresponding dtoTOdoMap
   *        while constructing complex expression.<br/>
   * @return
   * @throws ValidationException
   */

  public static ComplexExpression getComplexExpression(Set<DashboardFilters> filters)
      throws ValidationException {
    ComplexExpression complexExpression = null;
    try {
      Map<String, Set<String>> flMap = new HashMap<String, Set<String>>();
      for (DashboardFilters df : filters) {
        Set<String> values = flMap.get(df.getColumn());
        if (values == null) {
          values = new HashSet<String>();
          flMap.put(df.getColumn(), values);
        }
        if (StringUtils.isNotBlank(df.getValue())) {
          values.add(df.getValue());
        }
      }
      if (flMap.size() > 0) {
        complexExpression = new ComplexExpression();
        complexExpression.setLogicalOperator("AND");
        complexExpression.setOperands(new ArrayList<>());
        for (Entry<String, Set<String>> flMapGroup : flMap.entrySet()) {
          ComplexExpression ceGroupWise = new ComplexExpression();
          ceGroupWise.setLogicalOperator("OR");
          ceGroupWise.setOperands(new ArrayList<>());
          for (String values : flMapGroup.getValue()) {
            Expression exp = getExpression(flMapGroup.getKey(), values);
            ceGroupWise.getOperands().add(exp);
            if ("id".equalsIgnoreCase(flMapGroup.getKey())) {
              Expression expNAme = getExpression("name", values);
              ceGroupWise.getOperands().add(expNAme);
              logger.error("Adding the name also in search group :" + expNAme);
            }
          }
          complexExpression.getOperands().add(ceGroupWise);
        }
      }

    } catch (Exception ex) {
      complexExpression = null;
      logger.error("ERROR :: " + ex.getMessage());
      throw ex;
    }
    return complexExpression;
  }

  private static Expression getExpression(String key, String values) {
    Expression exp = new Expression();
    exp.setField(key);
    exp.setDtoField(key);
    exp.setOperator(getOperatorForDashBoardFilter(key));
    exp.setValue("name".equalsIgnoreCase(key) ? values : getActualValueType(key, values));
    return exp;
  }

  private static Object getActualValueType(String key, String valueString) {
    Filter filter = Filter.fromString(key);
    Object value = valueString;
    if ("true".equalsIgnoreCase(valueString) || "false".equalsIgnoreCase(valueString)) {
      return Boolean.parseBoolean(valueString);
    } else {
      try {
        if(Long.class.isAssignableFrom(filter.getValueType())) {
          logger.debug("inside getActualValueType method. Its long type");
          value = Long.parseLong(valueString);
        }
        else if(Integer.class.isAssignableFrom(filter.getValueType())) {
          logger.debug("inside getActualValueType method. Its Integer type");
          value = Integer.parseInt(valueString);
        }
        else if(Boolean.class.isAssignableFrom(filter.getValueType())) {
          logger.debug("inside getActualValueType method. Its Boolean type");
          value = Integer.parseInt(valueString);
        }
        else if(CreativeStatus.class.isAssignableFrom(filter.getValueType())) {
          logger.debug("inside getActualValueType method. Its CreativeStatus type");
          value = valueString.equals(CreativeStatus.active.toString())?CreativeStatus.active : CreativeStatus.inactive;
        }
        else {
          logger.debug("inside getActualValueType method. Its String type");
        }
        return value;
      } catch (Exception e) {
      }
    }
    return value;
  }

  private static Operator getOperatorForDashBoardFilter(String key) {
    // TODO; DOnt Hardcode change later
    if ("name".equalsIgnoreCase(key)) {
      return Operator.like;
    }
    return Operator.eq;
  }

  public static ComplexExpression getComplexExpression(String filterString, String entity)
      throws ValidationException {
    ComplexExpression complexExpression = null;
    try {
      if (filterString != null && checkBrackets(filterString)) {
        // Build expression tree
        Tree expressionTree = getExpressionTree(filterString);

        if (expressionTree != null) {
          // Good !! we are able to construct expression tree.
          complexExpression = new ComplexExpression();
          complexExpression.setLogicalOperator(expressionTree.getData());
          complexExpression.setOperands(new ArrayList<Object>());

          ArrayList<Node> children = expressionTree.getRoot().getChildren();

          // Generate ComplexExpression object using expression tree
          complexExpression = buildComplexExpression(complexExpression, children, entity);

          // memory clean up
          children = null;
          expressionTree = null;
        } else {
          // This is bad :-(
          logger.error("ERROR ::  Unable to generate expression tree from filter string.");
          throw new ValidationException(ErrorCode.BAD_CONFIGURATION);
        }
      }
    } catch (ValidationException ex) {
      complexExpression = null;
      logger.error("ERROR :: " + ex.getMessage());
      throw ex;
    }
    return complexExpression;
  }

  private static ComplexExpression buildComplexExpression(ComplexExpression expression,
      ArrayList<Node> children, String entity) throws ValidationException {
    for (Node eachNode : children) {
      if (eachNode.noOfChildren() == 0) {
        // TODO : Validate each condition before adding to expression
        try {
          Expression exp = validateCondition(eachNode.getData(), entity);
          if (exp != null) {
            expression.getOperands().add(exp);
          }
          // else {
          // // What to do here??
          // logger.error("Unable to validate the filter condition : " + eachNode.getData());
          // throw new
          // ValidationException(Constants.EC_INVALID_SEARCH_FILTER_UNABLE_TO_VALIDATE_CONDITION,
          // new Object[] {" '" + eachNode.getData() + "' "});
          // }
        } catch (ValidationException ex) {
          // memory clean up
          children = null;
          expression = null;
          throw ex;
        }
      } else {
        ComplexExpression ce = new ComplexExpression();
        ce.setLogicalOperator(eachNode.getData());
        ce.setOperands(new ArrayList<Object>());

        ce = buildComplexExpression(ce, eachNode.getChildren(), entity);

        // Add this sub expression into main complex expression
        expression.getOperands().add(ce);
      }
    }
    return expression;
  }

  private static Expression validateCondition(String conditionStr, String entity)
      throws ValidationException {

    Expression exp = null;
    Map<String, String> dtoToDoMap = BeanUtils.getPropertyAndMethodMap(DashboardData.class);

    // check whether dtoToDoMap is initialized or not. If not, then we cannot
    // validate the condition so, throw exception.
    if (dtoToDoMap == null || (dtoToDoMap != null && dtoToDoMap.size() == 0)) {
      logger.error("dtoToDoMap is not initialized for entity : " + entity);
      throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER);
    } else if (StringUtils.isBlank(conditionStr)) {
      conditionStr = conditionStr.trim();
      String[] strArr = conditionStr.split(SearchFilterConstants.COLON);

      if (strArr.length != 3) {
        logger.error("Invalid condition : " + conditionStr);
        throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
            new Object[] {" '" + conditionStr + "' "});
      }
      String column = strArr[0].trim();
      String optr = strArr[1].trim();
      String value = strArr[2].trim();

      /** COLUMN VALIDATION **/
      if (dtoToDoMap.containsKey(column)) {
        /** VALUE VALIDATION **/
        if ((value.contains("(") || value.contains(")"))
            && !optr.equals(Operator.in.getOperatorName())) {
          logger.error("The value : '" + value + "' is invalid.");
          throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
              new Object[] {" '" + value + "' ", " '" + column + "' "});
        }

        Object val = null;
        try {
          // TODO: Validate Passed Values
          // val = validateValue(value, "String");
        } catch (Exception ex) {
          logger.error(ex.getMessage());
          throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
              new Object[] {" '" + value + "' ", " '" + column + "' "});
        }

        /** HOPE EVERYTHING IS ALRIGHT !! **/
        exp = new Expression(column, column, optr, val);
      }
    }
    return exp;
  }

  private static Tree getExpressionTree(String expression) throws ValidationException {
    String operand = SearchFilterConstants.AND;

    if (expression.startsWith(SearchFilterConstants.AND))
      operand = SearchFilterConstants.AND;
    else if (expression.startsWith(SearchFilterConstants.OR))
      operand = SearchFilterConstants.OR;

    Tree expressionTree = new Tree();
    Node rootNode = new Node(operand);
    expressionTree.setRoot(rootNode);

    expressionTree = buildExpressionTree(expressionTree, rootNode, expression);
    return expressionTree;
  }

  private static Tree buildExpressionTree(Tree tree, Node parent, String expression)
      throws ValidationException {
    Node child = null;
    try {
      ArrayList<String> listOfOperands = getListOfOperandsForThisExpression(expression);

      for (String expr : listOfOperands) {
        if (!expr.startsWith(SearchFilterConstants.AND)
            && !expr.startsWith(SearchFilterConstants.OR)) {
          // LEAF NODE
          child = new Node(expr);
          tree.insert(parent, child);
        } else {
          // SUB NODE
          String operand = SearchFilterConstants.AND;

          if (expr.startsWith(SearchFilterConstants.AND))
            operand = SearchFilterConstants.AND;
          else if (expr.startsWith(SearchFilterConstants.OR))
            operand = SearchFilterConstants.OR;

          child = new Node(operand);
          tree.insert(parent, child);

          tree = buildExpressionTree(tree, child, expr);
        }
      }
    } catch (ValidationException ex) {
      tree = null;
      throw ex;
    } finally {
      // memory clean up
      child = null;
      parent = null;
    }
    return tree;
  }

  private static ArrayList<String> getListOfOperandsForThisExpression(String filter)
      throws ValidationException {
    Stack<Character> BRACKET_STACK = new Stack<Character>();
    ArrayList<String> branches = new ArrayList<String>();

    if (filter != null) {
      filter = filter.trim();

      int startIndex = 0;
      int operandEndIndex = 0;

      if (filter.startsWith(SearchFilterConstants.AND)) {
        startIndex += 4;
      } else if (filter.startsWith(SearchFilterConstants.OR)) {
        startIndex += 3;
      } else if (filter.startsWith("[")) {
        startIndex += 1;
      }

      while (startIndex < filter.length()) {
        filter = filter.substring(startIndex).trim();
        int i = 0;

        /** CHECK FOR INVALID LOGICAL OPERATORS **/
        if (!filter.startsWith(SearchFilterConstants.AND)
            && !filter.startsWith(SearchFilterConstants.OR) && !filter.startsWith("[")
            && filter.contains("[")) {
          String invalidLogicalOperator = filter.substring(0, filter.indexOf("[")).trim();

          if (!invalidLogicalOperator.contains(SearchFilterConstants.COLON)
              && !invalidLogicalOperator.endsWith(SearchFilterConstants.COMMA)) {
            // memory clean up
            BRACKET_STACK = null;
            branches = null;
            filter = null;
            throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                new Object[] {" '" + invalidLogicalOperator + "' "});
          }
        }

        if (filter.startsWith(SearchFilterConstants.AND)
            || filter.startsWith(SearchFilterConstants.OR)) {
          boolean operandFound = false;

          while (i < filter.length() && !operandFound) {
            char ch = filter.charAt(i);

            switch (ch) {
              case ' ':
                i++;
                continue;
              case '{':
                // memory clean up
                BRACKET_STACK = null;
                branches = null;
                throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                    new Object[] {" '" + ch + "' ", i});
                // case '(': i++; continue;
              case '[':
                BRACKET_STACK.push(ch);
                break;

              case '}':
                // memory clean up
                BRACKET_STACK = null;
                branches = null;
                throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                    new Object[] {" '" + ch + "' ", i});
                // case ')': i++; continue;
              case ']':
                if (!BRACKET_STACK.isEmpty()) {
                  char chx = BRACKET_STACK.pop();
                  if ((ch == ']' && chx != '[') || (ch == ')' && chx != '(')) {
                    // memory clean up
                    BRACKET_STACK = null;
                    branches = null;
                    throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                        new Object[] {" '" + chx + "' "});
                  } else {
                    BRACKET_STACK.push(chx);
                    if (BRACKET_STACK.size() == 1) {
                      operandEndIndex = i + 1;
                      operandFound = true;
                    }
                    // pop out anyway
                    BRACKET_STACK.pop();

                  }
                }
                break;
              default:
                break;
            }

            i++;
          }

          // Extract operand
          String operand = filter.substring(0, operandEndIndex).trim();

          if (!StringUtils.isEmpty(operand))
            branches.add(operand);

          startIndex = operandEndIndex;

          // TODO : check for delimiter(,) after operand
          String subFilter = filter.substring(startIndex).trim();
          if ((filter.length() - startIndex) > 1 && subFilter.matches("^.*[a-zA-Z0-9].*$")
              && !subFilter.startsWith(SearchFilterConstants.COMMA)) {
            // memory clean up
            BRACKET_STACK = null;
            branches = null;
            throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER);
          } else if (subFilter.startsWith(SearchFilterConstants.COMMA)) {
            // Okay, found delimiter(,) so lets move forward one step.
            startIndex += 1;
          }

          // TODO : check if we have reached end of filter string
          if (!subFilter.matches("^.*[a-zA-Z0-9].*$")) {
            // moving startIndex to the end as we have processed all operands
            startIndex = filter.length();
          }

        } else if (filter.matches("^.*[a-zA-Z0-9].*$")) {
          // if the subFilter does'nt start with any logical operator
          // then we should treat an operand will be till we encounter
          // a delimiter(,) or closing bracket(])
          String operand = null;

          if (filter.indexOf(SearchFilterConstants.COMMA) != -1) {
            operandEndIndex = filter.indexOf(SearchFilterConstants.COMMA);
            operand = filter.substring(0, operandEndIndex).trim();

            if (operand.contains("(")) {
              // may be operator is 'IN', so will be having multiple values enclosed in
              // parenthesis
              operandEndIndex = (filter.indexOf(")")) + 1;
              operand = filter.substring(0, operandEndIndex).trim();
            }
          } else if (filter.indexOf("]") != -1) {
            operandEndIndex = filter.indexOf("]");
            operand = filter.substring(0, operandEndIndex).trim();

            if (operand.startsWith("["))
              operand = operand.substring(1);
          }

          if (!StringUtils.isEmpty(operand))
            branches.add(operand);

          startIndex = operandEndIndex;

          // TODO : check for delimiter(,) after operand
          String subFilter = filter.substring(startIndex).trim();
          if ((filter.length() - startIndex) > 1 && subFilter.matches("^.*[a-zA-Z0-9].*$")
              && !subFilter.startsWith(SearchFilterConstants.COMMA)) {
            // memory clean up
            BRACKET_STACK = null;
            branches = null;
            throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER);
          } else if (subFilter.startsWith(SearchFilterConstants.COMMA)) {
            // Okay, found delimiter(,) so lets move forward one step.
            startIndex += 1;
          }

          // TODO : check if we have reached end of filter string
          if (!subFilter.matches("^.*[a-zA-Z0-9].*$")) {
            // moving startIndex to the end as we have processed all operands
            startIndex = filter.length();
          }
        }

      }
    }

    return branches;
  }

  private static boolean checkBrackets(String filter) throws ValidationException {
    if (filter != null) {
      Stack<Character> theStack = new Stack<Character>();

      for (int j = 0; j < filter.length(); j++) {
        char ch = filter.charAt(j);
        switch (ch) {
          case '{':
            // memory clean up
            theStack = null;
            throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                new Object[] {" '" + ch + "' ", j});
          case '[':
          case '(':
            theStack.push(ch);
            break;

          case '}':
            // memory clean up
            theStack = null;
            throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                new Object[] {" '" + ch + "' ", j});
          case ']':
          case ')':
            if (!theStack.isEmpty()) {
              char chx = theStack.pop();
              if ((ch == '}' && chx != '{') || (ch == ']' && chx != '[')
                  || (ch == ')' && chx != '(')) {
                // memory clean up
                theStack = null;
                throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                    new Object[] {" '" + chx + "' ", j});
              }
            } else // prematurely empty
            {
              // memory clean up
              theStack = null;
              throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
                  new Object[] {" '" + ch + "' "});
            }
            break;
          default: // no action on other characters
            break;
        }
      }

      // at this point, all characters have been processed
      if (!theStack.isEmpty()) {
        char ch = theStack.pop();
        // memory clean up
        theStack = null;
        throw new ValidationException(ErrorCode.INVALID_SEARCH_FILTER,
            new Object[] {" '" + ch + "' "});
      }
    }

    return true;
  }
}
