/**
 * (c) 2003-2021 MuleSoft, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1 a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.extensions.jms.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//https://stackoverflow.com/questions/47824761/how-would-i-add-an-annotation-to-exclude-a-method-from-a-jacoco-code-coverage-re
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExcludeFromGeneratedCoverage { }