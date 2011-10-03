package com.github.urmuzov.closurecompilermavenplugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * @goal compile
 * @phase compile
 */
public class ClosureCompilerMojo extends AbstractMojo {
    /**
     * @parameter expression="*.entry.js"
     * @required
     */
    private String entryFileMask = "*.entry.js";
    /**
     * @parameter expression="true"
     * @required
     */
    private boolean stopOnWarnings = true;
    /**
     * @parameter expression="true"
     * @required
     */
    private boolean stopOnErrors = true;
    /**
     * @parameter expression="false"
     * @required
     */
    private boolean logSourceFiles = false;
    /**
     * @parameter expression="false"
     * @required
     */
    private boolean logExternFiles = false;
    /**
     * @parameter expression="WARNING"
     * @required
     */
    private String loggingLevel = "WARNING";
    /**
     * @parameter expression="ADVANCED_OPTIMIZATIONS"
     * @required
     */
    private String compilationLevel = "ADVANCED_OPTIMIZATIONS";
    /**
     * Additional value VERBOSE_EXTRA enables all checks that are disabled with VERBOSE
     *
     * @parameter expression="VERBOSE_EXTRA"
     * @required
     */
    private String warningLevel = "VERBOSE_EXTRA";
    /**
     * @parameter expression="false"
     * @required
     */
    private boolean debug = false;
    /**
     * @parameter expression="null"
     * @required
     */
    private String formatting = null;
    /**
     * @parameter expression="true"
     * @required
     */
    private boolean manageClosureDependencies = true;
    /**
     * @parameter expression="true"
     * @required
     */
    private boolean generateExports = true;
    /**
     * @parameter expression="false"
     * @required
     */
    private boolean addDefaultExterns = false;
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
     */
    private List<Pass> passes = null;
    /**
     * @parameter
     */
    private String simplePasses = null;
    /**
     * @parameter
     */
    private File simplePassesEntriesDir = null;
    /**
     * @parameter
     */
    private File simplePassesOutputDir = null;

    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        List<Pass> effPasses = new ArrayList<Pass>();
        if (this.passes != null) {
            effPasses.addAll(this.passes);
        }
        effPasses.addAll(createSimplePasses());
        for (Pass pass : effPasses) {
            info(pass.getTitleText());

            CompilerOptions compilerOptions = new CompilerOptions();

            setCompilationLevel(this.compilationLevel, pass.compilationLevel, compilerOptions);
            setWarningLevel(this.warningLevel, pass.warningLevel, compilerOptions);
            setFormatting(this.formatting, pass.formatting, compilerOptions);
            setManageClosureDependencies(this.manageClosureDependencies, pass.manageClosureDependencies, compilerOptions);
            setGenerateExports(this.generateExports, pass.generateExports, compilerOptions);

            Compiler compiler = new Compiler();

            setLoggingLevel(this.loggingLevel, pass.loggingLevel);

            // Externs
            JSFileCollector externsCollector = createExternsCollector();
            addDefaultExterns(this.addDefaultExterns, pass.addDefaultExterns, externsCollector);
            collectExterns(this.externs, pass.externs, externsCollector);
            infoFiles(logExternFiles, "Externs:", externsCollector);

            // Sources
            JSFileCollector sourcesCollector = createSourcesCollector();
            collectSources(this.sources, pass.sources, sourcesCollector, entryFileMask);
            addEntryFile(pass.entryFile, sourcesCollector);
            infoFiles(logSourceFiles, "Sources:", sourcesCollector);

            // Compiling
            Result result = compiler.compile(externsCollector.getFiles(), sourcesCollector.getFiles(), compilerOptions);

            // Error handling
            boolean hasWarnings = result.warnings.length > 0;
            boolean hasErrors = result.errors.length > 0;
            // Looks like compiler prints errors and warnings himself
//            for (JSError warning : result.warnings) {
//                infoIfDebug.warn(warning.toString());
//            }
//            for (JSError error : result.errors) {
//                log.error(error.toString());
//            }
            if (stopOnWarnings && hasWarnings) {
                throw new MojoFailureException("Compilation faied: has warnings");
            }
            if (stopOnErrors && hasErrors) {
                throw new MojoFailureException("Compilation faied: has errors");
            }
            if (!result.success) {
                throw new MojoFailureException("Compilation failure");
            }

            // Output
            File outputFile = pass.outputFile;
            infoIfDebug("outputFile: " + outputFile);
            try {
                Files.createParentDirs(outputFile);
                Files.touch(outputFile);
                Files.write(compiler.toSource(), outputFile, Charsets.UTF_8);
                info("file size: " + outputFile.getName() + " -> " + outputFile.length() + " bytes");
            } catch (IOException e) {
                throw new MojoFailureException(outputFile != null ? outputFile.toString() : e.getMessage(), e);
            }
        }
    }

    public void info(String message) {
        getLog().info(message);
    }

    public void infoIfDebug(String message) {
        if (debug) {
            info(message);
        }
    }

    public void infoEffectiveValuesIfDebug(String name, Object effectiveValue, Object mojoValue, Object passValue) {
        if (debug) {
            info(name + ": " + effectiveValue + " (mojo: " + mojoValue + "; pass: " + passValue + ")");
        }
    }

    public void infoEffectiveListIfDebug(String name, List effectiveValue, List mojoValue, List passValue) {
        if (debug) {
            info(name + ": count " + (effectiveValue == null ? "null" : effectiveValue.size()) + " (mojo: count " + (mojoValue == null ? "null" : mojoValue.size()) + "; pass: count " + (passValue == null ? "null" : passValue.size()) + ")");
        }
    }

    public void infoFiles(boolean enabled, String message, JSFileCollector collector) {
        if (enabled) {
            info(message);
            for (JSSourceFile f : collector.getFiles()) {
                info(f.getOriginalPath());
            }
        }
    }

    protected JSFileCollector createSourcesCollector() {
        return new JSFileCollector();
    }

    protected JSFileCollector createExternsCollector() {
        return new JSFileCollector();
    }

    public <T> T getEffectiveValue(T mojoValue, T passValue) {
        T out = mojoValue;

        if (passValue != null)
            out = passValue;

        return out;
    }

    public List<SimplePass> createSimplePasses() throws MojoFailureException {
        Log log = getLog();

        List<SimplePass> out = new ArrayList<SimplePass>();

        if ((simplePasses == null || simplePasses.isEmpty())) {
            return out;
        }

        if (simplePassesEntriesDir == null) {
            throw new MojoFailureException("simplePassesEntriesDir is null");
        }
        if (!simplePassesEntriesDir.isDirectory()) {
            throw new MojoFailureException("simplePassesEntriesDir (" + simplePassesEntriesDir + ") is not a directory");
        }
        if (simplePassesOutputDir == null) {
            throw new MojoFailureException("simplePassesOutputDir is null");
        }

        String[] passNames = simplePasses.split("[ ;,]");
        info("simplePasses: " + Arrays.asList(passNames).toString());
        for (String passName : passNames) {
            out.add(new SimplePass(simplePassesEntriesDir, simplePassesOutputDir, passName));
        }
        return out;
    }

    public void setCompilationLevel(String mojoCompilationLevel, String passCompilationLevel, CompilerOptions compilerOptions) throws MojoFailureException {
        String compilationLevel = getEffectiveValue(mojoCompilationLevel, passCompilationLevel);
        infoEffectiveValuesIfDebug("compilationLevel", compilationLevel, mojoCompilationLevel, passCompilationLevel);
        CompilationLevel compilationLvl = null;
        try {
            compilationLvl = CompilationLevel.valueOf(compilationLevel);
            compilationLvl.setOptionsForCompilationLevel(compilerOptions);
            if (debug)
                compilationLvl.setDebugOptionsForCompilationLevel(compilerOptions);
        } catch (IllegalArgumentException e) {
            throw new MojoFailureException("Compilation level invalid (values: " + Arrays.asList(CompilationLevel.values()).toString() + ")", e);
        }

    }

    public void setWarningLevel(String mojoWarningLevel, String passWarningLevel, CompilerOptions compilerOptions) throws MojoFailureException {
        String warningLevel = getEffectiveValue(mojoWarningLevel, passWarningLevel);
        infoEffectiveValuesIfDebug("warningLevel", warningLevel, mojoWarningLevel, passWarningLevel);
        WarningLevel warningLvl = null;
        try {
            if (warningLevel.equals("VERBOSE_EXTRA")) {
                WarningLevel.VERBOSE.setOptionsForWarningLevel(compilerOptions);
                compilerOptions.setWarningLevel(DiagnosticGroups.ACCESS_CONTROLS, CheckLevel.WARNING);
                compilerOptions.setWarningLevel(DiagnosticGroups.STRICT_MODULE_DEP_CHECK, CheckLevel.WARNING);
                compilerOptions.setWarningLevel(DiagnosticGroups.VISIBILITY, CheckLevel.WARNING);
            } else {
                warningLvl = WarningLevel.valueOf(warningLevel);
                warningLvl.setOptionsForWarningLevel(compilerOptions);
            }
        } catch (IllegalArgumentException e) {
            throw new MojoFailureException("Warning level invalid (values: " + Arrays.asList(WarningLevel.values()).toString() + ")", e);
        }

    }

    public void setFormatting(String mojoFormatting, String passFormatting, CompilerOptions compilerOptions) throws MojoFailureException {
        String formatting = getEffectiveValue(mojoFormatting, passFormatting);
        infoEffectiveValuesIfDebug("formatting", formatting, mojoFormatting, passFormatting);
        FormattingOption formattingOption = null;
        if (formatting != null && !formatting.equals("null")) {
            try {
                formattingOption = FormattingOption.valueOf(formatting);
                formattingOption.applyToOptions(compilerOptions);
            } catch (IllegalArgumentException e) {
                throw new MojoFailureException("Formatting invalid (values: " + Arrays.asList(FormattingOption.values()).toString() + ")", e);
            }
        }
    }

    public void setManageClosureDependencies(boolean mojoManageClosureDependencies, Boolean passManageClosureDependencies, CompilerOptions compilerOptions) {
        boolean manageClosureDependencies = getEffectiveValue(mojoManageClosureDependencies, passManageClosureDependencies);
        infoEffectiveValuesIfDebug("manageClosureDependencies", manageClosureDependencies, mojoManageClosureDependencies, passManageClosureDependencies);
        compilerOptions.setManageClosureDependencies(manageClosureDependencies);
    }

    public void setGenerateExports(boolean mojoGenerateExports, Boolean passGenerateExports, CompilerOptions compilerOptions) {
        boolean generateExports = getEffectiveValue(mojoGenerateExports, passGenerateExports);
        infoEffectiveValuesIfDebug("generateExports", generateExports, mojoGenerateExports, passGenerateExports);
        compilerOptions.setGenerateExports(generateExports);
    }

    public void setLoggingLevel(String mojoLoggingLevel, String passLoggingLevel) throws MojoFailureException {
        String loggingLevel = getEffectiveValue(mojoLoggingLevel, passLoggingLevel);
        infoEffectiveValuesIfDebug("loggingLevel", loggingLevel, mojoLoggingLevel, passLoggingLevel);
        Level loggingLvl = null;
        try {
            loggingLvl = Level.parse(loggingLevel);
            Compiler.setLoggingLevel(loggingLvl);
        } catch (IllegalArgumentException e) {
            throw new MojoFailureException("Logging level invalid (values: [ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE, WARNING])", e);
        }
    }

    public void addDefaultExterns(boolean mojoAddDefaultExterns, Boolean passAddDefaultExterns, JSFileCollector fileCollector) throws MojoFailureException {
        boolean addDefaultExterns = getEffectiveValue(mojoAddDefaultExterns, passAddDefaultExterns);
        infoEffectiveValuesIfDebug("addDefaultExterns", addDefaultExterns, mojoAddDefaultExterns, passAddDefaultExterns);
        if (addDefaultExterns) {
            try {
                fileCollector.collectJSSourceFiles(CommandLineRunner.getDefaultExterns());
            } catch (IOException ex) {
                throw new MojoFailureException("Default externs adding error");
            }
        }
    }

    public void collectExterns(List<File> mojoExterns, List<File> passExterns, JSFileCollector fileCollector) {
        List<File> effExterns = getEffectiveValue(mojoExterns, passExterns);
        infoEffectiveListIfDebug("externs", effExterns, mojoExterns, passExterns);
        fileCollector.collectFiles(effExterns, null);
    }

    public void collectSources(List<File> mojoSources, List<File> passSources, JSFileCollector fileCollector, String entryFileMask) {
        List<File> effSources = getEffectiveValue(mojoSources, passSources);
        infoEffectiveListIfDebug("sources", effSources, mojoSources, passSources);
        fileCollector.collectFiles(effSources, Pattern.compile(wildcardToRegex(entryFileMask)));
    }

    public void addEntryFile(File entryFile, JSFileCollector fileCollector) {
        if (entryFile != null && entryFile.exists() && entryFile.isFile()) {
            fileCollector.collectFile(entryFile, null);
        }
        infoIfDebug("entryFile: " + (entryFile == null ? "null" : entryFile.getAbsolutePath()));
    }

    public String wildcardToRegex(String wildcard) {
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // escape special regexp-characters
                case '(':
                case ')':
                case '[':
                case ']':
                case '$':
                case '^':
                case '.':
                case '{':
                case '}':
                case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return (s.toString());
    }
}
