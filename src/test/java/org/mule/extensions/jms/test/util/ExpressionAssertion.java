/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.jms.test.util;

import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.el.ExpressionManager;

import java.util.function.Function;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

/**
 * Testing utility to assert to things using expression to obtain the values instead of using Java.
 *
 * @since 1.3.0
 */
public class ExpressionAssertion {

  private ExpressionManager expressionManager;
  private Object object;

  private String bindingName = "payload";

  public ExpressionAssertion(Object object, ExpressionManager expressionManager) {
    this.object = object;
    this.expressionManager = expressionManager;
  }

  public ExpressionAssertion andFrom(String expression) {
    TypedValue<?> payload =
        expressionManager.evaluate(expression, BindingContext.builder().addBinding(bindingName, getTypedValue(object)).build());
    return new ExpressionAssertion(payload, expressionManager);
  }

  public ExpressionAssertion as(String bindingName) {
    this.bindingName = bindingName;
    return this;
  }

  public void assertThat(String expression, Matcher<?> matcher) {
    TypedValue<?> payload =
        expressionManager.evaluate(expression, BindingContext.builder().addBinding(bindingName, getTypedValue(object)).build());
    Object value = payload.getValue();
    MatcherAssert.assertThat(value, (Matcher<Object>) matcher);
  }

  public ExpressionComparisionAssertionBuilder assertThat(String expression) {
    TypedValue<?> payload =
        expressionManager.evaluate(expression, BindingContext.builder().addBinding(bindingName, getTypedValue(object)).build());
    return new ExpressionComparisionAssertionBuilder(payload, expressionManager);
  }

  public static class ExpressionComparisionAssertionBuilder {

    private final TypedValue<?> actual;
    private final ExpressionManager expressionManager;

    ExpressionComparisionAssertionBuilder(TypedValue<?> actual, ExpressionManager expressionManager) {
      this.actual = actual;
      this.expressionManager = expressionManager;
    }

    public ExpressionComparisionAssertion comparedTo(Object expected) {
      return new ExpressionComparisionAssertion(actual, expected, expressionManager);
    }
  }

  public static class ExpressionComparisionAssertion {

    private final TypedValue<?> actual;
    private final Object expected;
    private final ExpressionManager expressionManager;
    private String bindingName;

    ExpressionComparisionAssertion(TypedValue<?> actual, Object expected, ExpressionManager expressionManager) {
      this.actual = actual;
      this.expected = expected;
      this.expressionManager = expressionManager;
    }

    public ExpressionComparisionAssertion as(String bindingName) {
      this.bindingName = bindingName;
      return this;
    }

    public void is(Function<Object, Matcher<?>> matcherFunction, String expression) {
      bindingName = "payload";
      TypedValue<?> expectedTypedValue =
          expressionManager.evaluate(expression,
                                     BindingContext.builder().addBinding(bindingName, getTypedValue(expected)).build());
      Matcher apply = matcherFunction.apply(expectedTypedValue.getValue());
      MatcherAssert.assertThat(actual.getValue(), apply);
    }
  }

  private static TypedValue getTypedValue(Object object) {
    if (object instanceof TypedValue) {
      return (TypedValue) object;
    } else {
      return new TypedValue(object, DataType.OBJECT);
    }
  }
}
