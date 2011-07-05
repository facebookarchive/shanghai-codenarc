/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for InvertedIfElseRule
 *
 * @author Hamlet D'Arcy
 */
class InvertedIfElseRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'InvertedIfElse'
    }

    void testSuccessScenario() {
        final SOURCE = '''
            if (!x) {
                foo()
            }

            if (x) {
                false
            } else {
                true
            }

            if (!x) {
                1
            } else if (y) {
                2
            } else {
                3
            } '''
        assertNoViolations(SOURCE)
    }

    void testSuccessScenarioWithElseIf() {
        final SOURCE = '''
            if (x) {
                1
            } else if (!y) {
                2
            } else {
                3
            }    '''
        assertNoViolations(SOURCE)
    }

    void testSingleViolation() {
        final SOURCE = '''
            if (!x) {
                false
            } else {
                true
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (!x) {')
    }

    void testDeepNesting() {
        final SOURCE = '''
            if (x) {
                if (x) {
                    1
                } else if (!x) {
                    if (!x) {
                        5
                    } else {
                        6
                    }
                } else {
                    3
                }
            } else {
                2
            }
        '''
        assertSingleViolation(SOURCE, 6, 'if (!x) {')
    }

    protected Rule createRule() {
        new InvertedIfElseRule()
    }
}