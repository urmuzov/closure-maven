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
import java.util.List;
import java.util.Properties;
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
     * @required
     */
    private List<File> externs = null;
    /**
     * @parameter
     * @required
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
        effPasses.addAll(Utils.createSimplePasses(debug, log, simplePasses, simplePassesEntriesDir, simplePassesOutputDir));
        for (Pass pass : effPasses) {
            Utils.log(true, log, pass.getTitleText());

            CompilerOptions compilerOptions = new CompilerOptions();

            String effCompilationLevel = pass.getCompilationLevel(debug, log, this.compilationLevel);
            Utils.setCompilationLevel(effCompilationLevel, debug, compilerOptions);
            String effWarningLevel = pass.getWarningLevel(debug, log, this.warningLevel);
            Utils.setWarningLevel(effWarningLevel, compilerOptions);
            String effFormatting = pass.getFormatting(debug, log, this.formatting);
            Utils.setFormatting(effFormatting, compilerOptions);
            boolean effManageClosureDependencies = pass.isManageClosureDependencies(debug, log, this.manageClosureDependencies);
            compilerOptions.setManageClosureDependencies(effManageClosureDependencies);
            boolean effGenerateExports = pass.isGenerateExports(debug, log, this.generateExports);
            compilerOptions.setGenerateExports(effGenerateExports);

            Compiler compiler = new Compiler();

            String effLoggingLevel = pass.getLoggingLevel(debug, log, this.loggingLevel);
            Utils.setLoggingLevel(effLoggingLevel, compiler);

            JSFileCollector externsCollector = createExternsCollector();
            JSFileCollector sourcesCollector = createSourcesCollector();

            boolean effAddDefaultExterns = pass.isAddDefaultExterns(debug, log, this.addDefaultExterns);
            if (effAddDefaultExterns) {
                try {
                    externsCollector.collectJSSourceFiles(CommandLineRunner.getDefaultExterns());
                } catch (IOException ex) {
                    throw new MojoFailureException("Default externs adding error");
                }
            }
            List<File> effExterns = pass.getExterns(debug, log, this.externs);
            externsCollector.collectFiles(effExterns, null);
            List<File> effSources = pass.getSources(debug, log, this.sources);
            sourcesCollector.collectFiles(effSources, Pattern.compile(Utils.wildcardToRegex(entryFileMask)));
            File entryFile = pass.getEntryFile();
            sourcesCollector.collectFile(entryFile, null);

            Utils.logExterns(logExternFiles, log, externsCollector);
            Utils.logSources(logSourceFiles, log, sourcesCollector);

            Result result = compiler.compile(externsCollector.getFiles(), sourcesCollector.getFiles(), compilerOptions);

            boolean hasWarnings = result.warnings.length > 0;
//            for (JSError warning : result.warnings) {
//                log.warn(warning.toString());
//            }

            boolean hasErrors = result.errors.length > 0;
            for (JSError error : result.errors) {
                log.error(error.toString());
            }
            if (stopOnWarnings && hasWarnings) {
                throw new MojoFailureException("Compilation faied: has warnings");
            }
            if (stopOnErrors && hasErrors) {
                throw new MojoFailureException("Compilation faied: has errors");
            }
            if (!result.success) {
                throw new MojoFailureException("Compilation failure");
            }
            File outputFile = pass.getOutputFile();
            Utils.log(debug, log, "outputFile: " + outputFile);
            try {
                Files.createParentDirs(outputFile);
                Files.touch(outputFile);
                Files.write(compiler.toSource(), outputFile, Charsets.UTF_8);
                Utils.log(true, log, "file size: " + outputFile.getName() + " -> " + outputFile.length() + " bytes");
            } catch (IOException e) {
                throw new MojoFailureException(outputFile != null ? outputFile.toString() : e.getMessage(), e);
            }

            if (pass instanceof SimplePass) {
                SimplePass simplePass = (SimplePass) pass;
                Properties props = mavenProject.getProperties();
                String prefixProperty = "output.closure.js.prefix";
                String prefix = props.getProperty(prefixProperty);
                String suffixProperty = "output.closure.js.suffix";
                String suffix = props.getProperty(suffixProperty);
                if (prefix == null) {
                    throw new MojoFailureException("Can't create simple include property for SimplePass (" + simplePass.getEntryPointName() + ") property " + prefixProperty + " not found");
                }
                if (suffix == null) {
                    throw new MojoFailureException("Can't create simple include property for SimplePass (" + simplePass.getEntryPointName() + ") property " + suffixProperty + " not found");
                }
                String propertyName = entryFile.getName();
                props.setProperty(propertyName,
                        prefix + simplePass.getEntryPointName() + suffix
                );
                Utils.log(true, log, "For simple inclusion use ${" + propertyName + "} in your HTML file");
            }
        }
    }

    protected JSFileCollector createSourcesCollector() {
        return new JSFileCollector();
    }

    protected JSFileCollector createExternsCollector() {
        return new JSFileCollector();
    }
}
