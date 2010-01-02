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
package org.codenarc.report

/**
 * Factory for ReportWriter objects based on the report type (name).
 * <p>
 * The passed in <code>type</code> can be either "html" or "xml".
 *
 * @author Chris Mair
 * @version $Revision: 260 $ - $Date: 2009-12-28 22:12:25 -0500 (Mon, 28 Dec 2009) $
 */
class ReportWriterFactory {

    ReportWriter getReportWriter(String type) {
        switch(type) {
            case 'html': return new HtmlReportWriter()
            case 'xml': return new XmlReportWriter()
            default: throw new IllegalArgumentException("Invalid report type [$type]")
        }
    }

    ReportWriter getReportWriter(String type, Map options) {
        def reportWriter = getReportWriter(type)
        options.each { name, value -> reportWriter[name] = value }
        return reportWriter
    }
}