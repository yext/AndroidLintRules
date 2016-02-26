package com.yext.android.lint;

import java.util.Arrays;
import java.util.List;

import com.android.tools.lint.detector.api.Issue;

public class CustomIssueRegistry extends com.android.tools.lint.client.api.IssueRegistry {
    public CustomIssueRegistry() {
    }

    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(IssuelessTodoDetector.ISSUE);
    }

}
