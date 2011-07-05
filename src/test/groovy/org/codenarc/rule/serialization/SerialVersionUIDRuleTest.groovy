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
package org.codenarc.rule.serialization

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for SerialVersionUIDRule
 *
 * @author Hamlet D'Arcy
  */
class SerialVersionUIDRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SerialVersionUID'
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
        	class MyClass {
                private static final long serialVersionUID = 13241234134 as long
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_2_Violations() {
        final SOURCE = '''
            class MyClass1 {
                private final long serialVersionUID = 13241234134
            }
            class MyClass2 {
                private static long serialVersionUID = 665544
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private final long serialVersionUID = 13241234134',
                6, 'private static long serialVersionUID = 665544')
    }

    void testApplyTo_WrongType() {
        final SOURCE = '''
            class MyClass1 {
                public static final int serialVersionUID = 13241234134
            }
            class MyClass2 {
                private static final Long serialVersionUID = 665544
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'static final int serialVersionUID = 13241234134',
                6, 'static final Long serialVersionUID = 665544')
    }

    void testApplyTo_Property() {
        final SOURCE = '''
            class MyClass1 {
                static final long serialVersionUID = 13241234134 as long
            }
        '''
        assertSingleViolation(SOURCE,
                3, 'static final long serialVersionUID = 13241234134 as long')
    }

    protected Rule createRule() {
        new SerialVersionUIDRule()
    }

}
