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
package org.apache.solr.client.solrj.io.stream.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.SolrTestCase;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.eval.MovingAverageEvaluator;
import org.apache.solr.client.solrj.io.eval.StreamEvaluator;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import org.junit.Test;

public class MovingAverageEvaluatorTest extends SolrTestCase {

  StreamFactory factory;
  Map<String, Object> values;

  public MovingAverageEvaluatorTest() {
    super();

    factory = new StreamFactory().withFunctionName("movingAvg", MovingAverageEvaluator.class);
    values = new HashMap<>();
  }

  @Test
  public void doesNotFailWithEmptyList() throws Exception {
    StreamEvaluator evaluator = factory.constructEvaluator("movingAvg(a,30)");
    Object result;

    values.clear();
    values.put("a", new ArrayList<Double>());
    result = evaluator.evaluate(new Tuple(values));
    assertEquals(Collections.emptyList(), result);
  }
}
