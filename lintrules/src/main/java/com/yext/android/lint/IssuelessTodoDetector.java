package com.yext.android.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.TextFormat;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Document;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.ast.AstVisitor;

/**
 * Lint check for the usage of to-do statements without associated issue id
 */
public class IssuelessTodoDetector extends Detector implements Detector.JavaScanner {

    private static final String ISSUELESS_TODO_PATTERN = "TODO(?! \\([A-Z]{2}-[0-9]+\\))";
    private static Pattern pattern;

    static {
        pattern = Pattern.compile(ISSUELESS_TODO_PATTERN);
    }

    /**
     * Identify segments in this file matching the issueless TODO pattern.
     *
     * @param filename Name of source file being searched.
     * @param source Source file being searched.
     * @return Locations matched in the source.
     */
    public Collection<Location> findMatches(File file, String source){
        LinkedList<Location> locations = new LinkedList<Location>();

        Matcher m = pattern.matcher(source);
        while(m.find()) {
            Location location = Location.create(file, source, m.start(), m.end());
            locations.add(location);
        }
        return locations;
    }

    @Override
    public void visitDocument(@NonNull XmlContext context, @NonNull Document document) {
        String source = context.getContents();
        // Check validity of source
        if (source == null) {
            return;
        }

        Collection<Location> matches = findMatches(context.file, source);
        for(Location location : matches){
            context.report(ISSUE, location, ISSUE.getBriefDescription(TextFormat.TEXT));
        }
    }

    @Override
    public AstVisitor createJavaVisitor(@NonNull JavaContext context) {
        String source = context.getContents();
        // Check validity of source
        if (source == null) {
            return null;
        }

        Collection<Location> matches = findMatches(context.file, source);
        for(Location location : matches){
            context.report(ISSUE, location, ISSUE.getBriefDescription(TextFormat.TEXT));
        }
        return null;
    }

    public static final Issue ISSUE = Issue.create(
            "TodoWithoutIssue",
            "TODO comment without associated issue detected",
            "TODO comments can be a useful marker for work remaining to be done, but should also be tracked. " +
                    "TODO comments should have associated issues raised, using the form '// TODO (AB-1234): Message' " +
                    "where AB-1234 is the issue raised to track this work.",
            Category.CORRECTNESS,
            8,
            Severity.WARNING,
            new Implementation(
                    IssuelessTodoDetector.class,
                    Scope.JAVA_FILE_SCOPE
            )
    );

}