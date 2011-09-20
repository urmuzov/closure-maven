package com.github.urmuzov.closurecompilermavenplugin;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Pass {
    /**
     * @parameter expression="WARNING"
     */
    private String loggingLevel = null;
    /**
     * @parameter expression="WHITESPACE_ONLY"
     */
    private String compilationLevel = null;
    /**
     * Additional value VERBOSE_EXTRA enables all checks that are disabled with VERBOSE
     *
     * @parameter expression="VERBOSE"
     */
    private String warningLevel = null;
    /**
     * @parameter expression="null"
     */
    private String formatting = null;
    /**
     * @parameter expression="false"
     */
    private Boolean manageClosureDependencies = null;
    /**
     * @parameter expression="false"
     */
    private Boolean generateExports = null;
    /**
     * @parameter expression="false"
     */
    private Boolean addDefaultExterns = null;
    /**
     * @parameter
     */
    private List<File> externs = null;
    /**
     * @parameter
     */
    private List<File> sources = null;
    /**
     * @parameter
     * @required
     */
    protected File entryFile;
    /**
     * @parameter
     * @required
     */
    protected File outputFile;

    public String getLoggingLevel(boolean debug, Log log, String parentLoggingLevel) {
        String out = parentLoggingLevel;

        if (this.loggingLevel != null)
            out = this.loggingLevel;

        Utils.log(debug, log, "loggingLevel: " + out + " (pass: " + this.loggingLevel + ", parent: " + parentLoggingLevel + ")");

        return out;
    }

    public String getCompilationLevel(boolean debug, Log log, String parentCompilationLevel) {
        String out = parentCompilationLevel;

        if (this.compilationLevel != null)
            out = this.compilationLevel;

        Utils.log(debug, log, "compilationLevel: " + out + " (pass: " + this.compilationLevel + ", parent: " + parentCompilationLevel + ")");

        return out;
    }

    public String getWarningLevel(boolean debug, Log log, String parentWarningLevel) {
        String out = parentWarningLevel;

        if (this.warningLevel != null)
            out = this.warningLevel;

        Utils.log(debug, log, "warningLevel: " + out + " (pass: " + this.warningLevel + ", parent: " + parentWarningLevel + ")");

        return out;
    }

    public String getFormatting(boolean debug, Log log, String parentFormatting) {
        String out = parentFormatting;

        if (this.formatting != null)
            out = this.formatting;

        Utils.log(debug, log, "formatting: " + out + " (pass: " + this.formatting + ", parent: " + parentFormatting + ")");

        return out;
    }

    public boolean isManageClosureDependencies(boolean debug, Log log, boolean parentManageClosureDependencies) {
        Boolean out = parentManageClosureDependencies;

        if (this.manageClosureDependencies != null)
            out = this.manageClosureDependencies;

        Utils.log(debug, log, "manageClosureDependencies: " + out + " (pass: " + this.manageClosureDependencies + ", parent: " + parentManageClosureDependencies + ")");

        return out;
    }

    public boolean isGenerateExports(boolean debug, Log log, boolean parentGenerateExports) {
        Boolean out = parentGenerateExports;

        if (this.generateExports != null)
            out = this.generateExports;

        Utils.log(debug, log, "generateExports: " + out + " (pass: " + this.generateExports + ", parent: " + parentGenerateExports + ")");

        return out;
    }

    public boolean isAddDefaultExterns(boolean debug, Log log, boolean parentAddDefaultExterns) {
        Boolean out = parentAddDefaultExterns;

        if (this.addDefaultExterns != null)
            out = this.addDefaultExterns;

        Utils.log(debug, log, "addDefaultExterns: " + out + " (pass: " + this.addDefaultExterns + ", parent: " + parentAddDefaultExterns + ")");

        return out;
    }

    public List<File> getExterns(boolean debug, Log log, List<File> parentExterns) {
        List<File> out = parentExterns;

        if (this.externs != null)
            out = this.externs;

        if (out == null) {
            out = new ArrayList<File>();
        }

        Utils.log(debug, log, "externs: " + (out == null ? null : out.size()) + " (pass: " + (this.externs == null ? null : this.externs.size()) + ", parent: " + (parentExterns == null ? null : parentExterns.size()) + ")");

        return out;
    }

    public List<File> getSources(boolean debug, Log log, List<File> parentSources) {
        List<File> out = parentSources;

        if (this.sources != null)
            out = this.sources;

        if (out == null) {
            out = new ArrayList<File>();
        }

        Utils.log(debug, log, "sources: " + (out == null ? null : out.size()) + " (pass: " + (this.sources == null ? null : this.sources.size()) + ", parent: " + (parentSources == null ? null : parentSources.size()) + ")");

        return out;
    }

    public File getEntryFile() throws MojoFailureException {
        if (entryFile == null) {
            throw new MojoFailureException("entryFile is required for pass");
        }
        if (!entryFile.isFile()) {
            throw new MojoFailureException("entryFile (" + entryFile + ") is not a file");
        }
        return entryFile;
    }

    public File getOutputFile() throws MojoFailureException {
        if (outputFile == null) {
            throw new MojoFailureException("outputFile is required for pass");
        }
        return outputFile;
    }

    public String getTitleText() {
        return "== Pass (" + entryFile + " -> " + outputFile + ") ==";
    }
}
