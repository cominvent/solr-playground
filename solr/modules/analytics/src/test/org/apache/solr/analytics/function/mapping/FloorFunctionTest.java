/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.analytics.function.mapping;

import java.util.Arrays;
import java.util.Iterator;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.analytics.function.mapping.DecimalNumericConversionFunction.FloorFunction;
import org.apache.solr.analytics.value.AnalyticsValueStream;
import org.apache.solr.analytics.value.FillableTestValue.TestDoubleValue;
import org.apache.solr.analytics.value.FillableTestValue.TestDoubleValueStream;
import org.apache.solr.analytics.value.FillableTestValue.TestFloatValue;
import org.apache.solr.analytics.value.FillableTestValue.TestFloatValueStream;
import org.apache.solr.analytics.value.FillableTestValue.TestIntValue;
import org.apache.solr.analytics.value.FillableTestValue.TestIntValueStream;
import org.apache.solr.analytics.value.FillableTestValue.TestLongValue;
import org.apache.solr.analytics.value.FillableTestValue.TestLongValueStream;
import org.apache.solr.analytics.value.IntValue;
import org.apache.solr.analytics.value.IntValueStream;
import org.apache.solr.analytics.value.LongValue;
import org.apache.solr.analytics.value.LongValueStream;
import org.junit.Test;

public class FloorFunctionTest extends SolrTestCaseJ4 {

  @Test
  public void singleValueFloatParameterTest() {
    TestFloatValue val = new TestFloatValue();

    AnalyticsValueStream uncasted =
        FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {val});
    assertTrue(uncasted instanceof IntValue);
    IntValue func = (IntValue) uncasted;

    // Value doesn't exist
    val.setExists(false);
    func.getInt();
    assertFalse(func.exists());

    // Value exists
    val.setValue(21.56F).setExists(true);
    assertEquals(21, func.getInt());
    assertTrue(func.exists());

    val.setValue(-100.3F).setExists(true);
    assertEquals(-101, func.getInt());
    assertTrue(func.exists());
  }

  @Test
  public void singleValueDoubleParameterTest() {
    TestDoubleValue val = new TestDoubleValue();

    AnalyticsValueStream uncasted =
        FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {val});
    assertTrue(uncasted instanceof LongValue);
    LongValue func = (LongValue) uncasted;

    // Value doesn't exist
    val.setExists(false);
    func.getLong();
    assertFalse(func.exists());

    // Value exists
    val.setValue(21.56).setExists(true);
    assertEquals(21L, func.getLong());
    assertTrue(func.exists());

    val.setValue(-100.3).setExists(true);
    assertEquals(-101L, func.getLong());
    assertTrue(func.exists());
  }

  @Test
  public void multiValueFloatParameterTest() {
    TestFloatValueStream val = new TestFloatValueStream();

    AnalyticsValueStream uncasted =
        FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {val});
    assertTrue(uncasted instanceof IntValueStream);
    IntValueStream func = (IntValueStream) uncasted;

    // No values
    val.setValues();
    func.streamInts(
        value -> {
          fail("There should be no values to stream");
        });

    // One value
    val.setValues(-4F);
    Iterator<Integer> values1 = Arrays.asList(-4).iterator();
    func.streamInts(
        value -> {
          assertTrue(values1.hasNext());
          assertEquals(values1.next().intValue(), value);
        });
    assertFalse(values1.hasNext());

    // Multiple values
    val.setValues(4F, -10.9999F, 50.00001F, 74.99999F);
    Iterator<Integer> values2 = Arrays.asList(4, -11, 50, 74).iterator();
    func.streamInts(
        value -> {
          assertTrue(values2.hasNext());
          assertEquals(values2.next().intValue(), value);
        });
    assertFalse(values2.hasNext());
  }

  @Test
  public void multiValueDoubleParameterTest() {
    TestDoubleValueStream val = new TestDoubleValueStream();

    AnalyticsValueStream uncasted =
        FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {val});
    assertTrue(uncasted instanceof LongValueStream);
    LongValueStream func = (LongValueStream) uncasted;

    // No values
    val.setValues();
    func.streamLongs(
        value -> {
          fail("There should be no values to stream");
        });

    // One value
    val.setValues(-4);
    Iterator<Long> values1 = Arrays.asList(-4L).iterator();
    func.streamLongs(
        value -> {
          assertTrue(values1.hasNext());
          assertEquals(values1.next().longValue(), value);
        });
    assertFalse(values1.hasNext());

    // Multiple values
    val.setValues(4, -10.9999, 50.000001, 74.99999);
    Iterator<Long> values2 = Arrays.asList(4L, -11L, 50L, 74L).iterator();
    func.streamLongs(
        value -> {
          assertTrue(values2.hasNext());
          assertEquals(values2.next().longValue(), value);
        });
    assertFalse(values2.hasNext());
  }

  @Test
  public void nonDecimalParameterTest() {
    AnalyticsValueStream result;
    AnalyticsValueStream param;

    param = new TestIntValue();
    result = FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {param});
    assertTrue(result instanceof IntValue);
    assertEquals(param, result);

    param = new TestIntValueStream();
    result = FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {param});
    assertTrue(result instanceof IntValueStream);
    assertEquals(param, result);

    param = new TestLongValue();
    result = FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {param});
    assertTrue(result instanceof LongValue);
    assertEquals(param, result);

    param = new TestLongValueStream();
    result = FloorFunction.creatorFunction.apply(new AnalyticsValueStream[] {param});
    assertTrue(result instanceof LongValueStream);
    assertEquals(param, result);
  }
}
