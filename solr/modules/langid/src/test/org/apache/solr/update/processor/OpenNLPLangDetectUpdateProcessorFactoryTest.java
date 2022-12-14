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

package org.apache.solr.update.processor;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.junit.Test;

@ThreadLeakLingering(linger = 0)
public class OpenNLPLangDetectUpdateProcessorFactoryTest
    extends LanguageIdentifierUpdateProcessorFactoryTestCase {
  private static final String TEST_MODEL = "opennlp-langdetect.eng-swe-spa-rus-deu.bin";

  @Override
  protected OpenNLPLangDetectUpdateProcessor createLangIdProcessor(ModifiableSolrParams parameters)
      throws Exception {
    if (parameters.get("langid.model")
        == null) { // handle superclass tests that don't provide the model filename
      parameters.set("langid.model", TEST_MODEL);
    }
    if (parameters.get("langid.threshold")
        == null) { // handle superclass tests that don't provide confidence threshold
      parameters.set("langid.threshold", "0.3");
    }
    SolrQueryRequest req = _parser.buildRequestFrom(h.getCore(), new ModifiableSolrParams(), null);
    OpenNLPLangDetectUpdateProcessorFactory factory = new OpenNLPLangDetectUpdateProcessorFactory();
    factory.init(parameters.toNamedList());
    factory.inform(h.getCore());
    return (OpenNLPLangDetectUpdateProcessor) factory.getInstance(req, resp, null);
  }

  // this one actually works better it seems with short docs
  @Override
  protected SolrInputDocument tooShortDoc() {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("text", "");
    return doc;
  }

  @Test
  @Override
  public void testLangIdGlobal() throws Exception {
    ModifiableSolrParams parameters = new ModifiableSolrParams();
    parameters.add("langid.fl", "name,subject");
    parameters.add("langid.langField", "language_s");
    parameters.add("langid.model", TEST_MODEL);
    parameters.add("langid.threshold", "0.3");
    liProcessor = createLangIdProcessor(parameters);

    assertLang(
        "en",
        "id",
        "1en",
        "name",
        "Lucene",
        "subject",
        "Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License.");
    assertLang(
        "sv",
        "id",
        "2sv",
        "name",
        "Maven",
        "subject",
        "Apache Maven ??r ett verktyg utvecklat av Apache Software Foundation och anv??nds inom systemutveckling av datorprogram i programspr??ket Java. Maven anv??nds f??r att automatiskt paketera (bygga) programfilerna till en distribuerbar enhet. Maven anv??nds inom samma omr??de som Apache Ant men dess byggfiler ??r deklarativa till skillnad ifr??n Ants skriptbaserade.");
    assertLang(
        "es",
        "id",
        "3es",
        "name",
        "Lucene",
        "subject",
        "Lucene es un API de c??digo abierto para recuperaci??n de informaci??n, originalmente implementada en Java por Doug Cutting. Est?? apoyado por el Apache Software Foundation y se distribuye bajo la Apache Software License. Lucene tiene versiones para otros lenguajes incluyendo Delphi, Perl, C#, C++, Python, Ruby y PHP.");
    assertLang(
        "ru",
        "id",
        "4ru",
        "name",
        "Lucene",
        "subject",
        "The Apache Lucene ??? ?????? ?????????????????? ???????????????????? ?????? ?????????????????????????????????? ?????????????????????????????? ????????????, ???????????????????? ???? Java. ?????????? ???????? ???????????????????????? ?????? ???????????? ?? ?????????????????? ?? ???????????? ???????????????? ???????????????????????? ?????????????????????? (?????????????????????????? ??????????????????).");
    assertLang(
        "de",
        "id",
        "5de",
        "name",
        "Lucene",
        "subject",
        "Lucene ist ein Freie-Software-Projekt der Apache Software Foundation, das eine Suchsoftware erstellt. Durch die hohe Leistungsf??higkeit und Skalierbarkeit k??nnen die Lucene-Werkzeuge f??r beliebige Projektgr????en und Anforderungen eingesetzt werden. So setzt beispielsweise Wikipedia Lucene f??r die Volltextsuche ein. Zudem verwenden die beiden Desktop-Suchprogramme Beagle und Strigi eine C#- bzw. C++- Portierung von Lucene als Indexer.");
  }
}
