/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.reporting;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.text.MatchersPrinter;
import org.mockito.internal.util.MockUtil;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MatchableInvocation;
import org.mockito.mock.MockName;

public class PrintSettings {

    public static final int MAX_LINE_LENGTH = 45;
    private boolean multiline;
    private List<Integer> withTypeInfo = new LinkedList<>();
    private Set<String> withFullyQualifiedName = Collections.emptySet();

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public static PrintSettings verboseMatchers(Integer... indexesOfMatchers) {
        PrintSettings settings = new PrintSettings();
        settings.setMatchersToBeDescribedWithExtraTypeInfo(indexesOfMatchers);
        return settings;
    }

    public boolean extraTypeInfoFor(int argumentIndex) {
        return withTypeInfo.contains(argumentIndex);
    }

    public boolean fullyQualifiedNameFor(String simpleClassName) {
        return withFullyQualifiedName.contains(simpleClassName);
    }

    public void setMatchersToBeDescribedWithExtraTypeInfo(Integer[] indexesOfMatchers) {
        this.withTypeInfo = Arrays.asList(indexesOfMatchers);
    }

    public void setMatchersToBeDescribedWithFullName(Set<String> indexesOfMatchers) {
        this.withFullyQualifiedName = indexesOfMatchers;
    }

    public String print(List<ArgumentMatcher> matchers, Invocation invocation) {
        MatchersPrinter matchersPrinter = new MatchersPrinter();

        MockName mockName = MockUtil.getMockName(invocation.getMock());
        String methodName = invocation.getMethod().getName();
        String qualifiedName = mockName + "." + methodName;

        String invocationString = qualifiedName + matchersPrinter.getArgumentsLine(matchers, this);
        if (isMultiline() || (!matchers.isEmpty() && invocationString.length() > MAX_LINE_LENGTH)) {
            return qualifiedName + matchersPrinter.getArgumentsBlock(matchers, this);
        } else {
            return invocationString;
        }
    }

    public String print(Invocation invocation) {
        return print(invocation.getArgumentsAsMatchers(), invocation);
    }

    public String print(MatchableInvocation invocation) {
        return print(invocation.getMatchers(), invocation.getInvocation());
    }
}
