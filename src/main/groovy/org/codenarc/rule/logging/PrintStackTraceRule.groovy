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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * Rule that checks for calls to <code>printStackTrace()</code>.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class PrintStackTraceRule extends AbstractAstVisitorRule {
    String name = 'PrintStackTrace'
    int priority = 2
    Class astVisitorClass = PrintStackTraceAstVisitor
}

class PrintStackTraceAstVisitor extends AbstractAstVisitor {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (isFirstVisit(methodCall) && AstUtil.isMethodNamed(methodCall, 'printStackTrace', 0)) {
            addViolation(methodCall, 'printStackTrace() should be replaced with something more robust')
        }
        super.visitMethodCallExpression(methodCall)
    }
}