/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.generic

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.util.AstUtil

/**
 * Rule that checks for non-<code>final</code> fields on a class. The intent of this rule is
 * to check a configured set of classes that should remain "stateless" and reentrant. One
 * example might be Grails service classes, which are, by default, a singleton, and so they
 * should be reentrant. DAO classes are also often kept stateless.
 * <p/>
 * This rule ignores <code>final</code> fields (either instance or static). Fields that are
 * <code>static</code> and non-<code>final</code>, however, do cause a violation.
 * <p/>
 * This rule also ignores all classes annotated with the <code>@Immutable</code> transformation.
 * See http://groovy.codehaus.org/Immutable+transformation.
 * <p/>
 * You can configure this rule to ignore certain fields either by name or by type. This can be
 * useful to ignore fields that hold references to (static) dependencies (such as DAOs or
 * Service objects) or static configuration.
 * <p/>
 * The <code>ignoreFieldNames</code> property specifies one or more (comma-separated) field names
 * that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally
 * include wildcard characters ('*' or '?'). You can add to the field names to be ignored by setting
 * the (write-only) <code>addIgnoreFieldNames</code> property. This is a "special" property -- each
 * call to <code>setAddIgnoreFieldNames()</code> adds to the existing <code>ignoreFieldNames</code>
 * property value. 
 * <p/>
 * The <code>ignoreFieldTypes</code> property specifies one or more (comma-separated) field type names
 * that should be ignored (i.e., that should not cause a rule violation). The type name(s) may optionally
 * include wildcard characters ('*' or '?').
 * <p/>
 * Note: The <code>ignoreFieldTypes</code> property matches the field type name as indicated
 * in the field declaration, only including a full package specification IF it is included in
 * the source code. For example, the field declaration <code>BigDecimal value</code> matches
 * an <code>ignoreFieldTypes</code> value of <code>BigDecimal</code>, but not
 * <code>java.lang.BigDecimal</code>.
 * <p/>
 * There is one exception for the <code>ignoreFieldTypes</code> property: if the field is declared
 * with a modifier/type of <code>def</code>, then the type resolves to <code>java.lang.Object</code>.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class StatelessClassRule extends AbstractAstVisitorRule {
    String name = 'StatelessClass'
    int priority = 2
    String ignoreFieldNames
    String ignoreFieldTypes
    Class astVisitorClass = StatelessClassAstVisitor


    /**
     * Add more field names to the existing <code>ignoreFieldNames</code> property value.
     * @param moreFieldNames - specifies one or more (comma-separated) field names that should be
     *      ignored (i.e., that should not cause a rule violation). The name(s) may optionally
     *      include wildcard characters ('*' or '?'). Any names specified here are joined to
     *      the existing <code>ignoreFieldNames</code> property value. 
     */
    void setAddToIgnoreFieldNames(String moreFieldNames) {
        this.ignoreFieldNames = this.ignoreFieldNames ? this.ignoreFieldNames + ',' + moreFieldNames : moreFieldNames
    }
}

class StatelessClassAstVisitor extends AbstractAstVisitor  {

    private boolean immutable = false

    protected void visitClassEx(ClassNode node) {
        // TODO would prefer to just short-circuit the rule here, if immutable; but visitClass() is final
        immutable = AstUtil.getAnnotation(node, 'Immutable')
    }

    void visitFieldEx(FieldNode fieldNode) {

        if (immutable) {
            return
        }

        boolean ignore = fieldNode.modifiers & FieldNode.ACC_FINAL
        
        if (!ignore && rule.ignoreFieldNames) {
            ignore = new WildcardPattern(rule.ignoreFieldNames).matches(fieldNode.name)
        }

        if (!ignore && rule.ignoreFieldTypes) {
            ignore = new WildcardPattern(rule.ignoreFieldTypes).matches(fieldNode.type.name)
        }

        if (!ignore) {
            addViolation(fieldNode, "The class $fieldNode.owner.name is marked as stateless but contains the non-final field \"$fieldNode.name\"")
        }
        super.visitFieldEx(fieldNode)
    }

}