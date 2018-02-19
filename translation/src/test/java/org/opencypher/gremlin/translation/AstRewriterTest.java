/*
 * Copyright (c) 2018 "Neo4j, Inc." [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencypher.gremlin.translation;

import static org.opencypher.gremlin.translation.helpers.CypherAstAssertions.assertThat;
import static org.opencypher.gremlin.translation.helpers.CypherAstHelpers.parse;

import org.junit.Test;

public class AstRewriterTest {

    @Test
    public void implicitWhere() throws Exception {
        assertThat(parse(
            "MATCH (n:Label {prop: 'value'})-[r]->(m) RETURN n"
        )).normalizedTo(
            "MATCH (n)-[r]->(m) WHERE n.prop = 'value' AND (n:Label) RETURN n"
        );
    }

    @Test
    public void implicitWhereIsNotApplied() throws Exception {
        assertThat(parse(
            "MATCH (n:Label {prop: 'value'}) RETURN n"
        )).normalizedTo(
            "MATCH (n:Label {prop: 'value'}) RETURN n"
        );
    }

    @Test
    public void whereClauseNormalizer() throws Exception {
        assertThat(parse(
            "MATCH (n) WHERE n.prop = 'value' AND (n:Label) RETURN n"
        )).normalizedTo(
            "MATCH (n:Label {prop: 'value'}) RETURN n"
        );
    }

    @Test
    public void matchFalseRewrite() throws Exception {
        assertThat(parse(
            "MATCH (n) WHERE 1 = 0 AND n.name = \"marko\" RETURN n"
        )).normalizedTo(
            "MATCH (n) WHERE 1 = 0 RETURN n"
        );
    }

    @Test
    public void matchTrueRewrite() throws Exception {
        assertThat(parse(
            "MATCH (n) WHERE 1 = 1 OR n.name = \"marko\" RETURN n"
        )).normalizedTo(
            "MATCH (n) WHERE 1 = 1 RETURN n"
        );
    }

}
