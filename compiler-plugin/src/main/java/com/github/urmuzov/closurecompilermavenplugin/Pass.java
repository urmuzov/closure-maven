package com.github.urmuzov.closurecompilermavenplugin;

import java.io.File;
import java.util.List;

public class Pass {
    /**
     * @parameter expression="WARNING"
     */
    protected String loggingLevel = null;
    /**
     * @parameter expression="WHITESPACE_ONLY"
     */
    protected String compilationLevel = null;
    /**
     * Additional value VERBOSE_EXTRA enables all checks that are disabled with VERBOSE
     *
     * @parameter expression="VERBOSE"
     */
    protected String warningLevel = null;
    /**
     * @parameter expression="null"
     */
    protected String formatting = null;
    /**
     * @parameter expression="false"
     */
    protected Boolean manageClosureDependencies = null;
    /**
     * @parameter expression="false"
     */
    protected Boolean generateExports = null;
    /**
     * @parameter expression="false"
     */
    protected Boolean addDefaultExterns = null;
    /**
     * @parameter
     */
    protected List<File> externs = null;
    /**
     * @parameter
     */
    protected List<File> sources = null;
    /**
     * @parameter
     */
    protected File entryFile;
    /**
     * @parameter
     * @required
     */
    protected File outputFile;

    public String getTitleText() {
        return "== Pass (" + entryFile + " -> " + outputFile + ") ==";
    }
}
