package com.github.urmuzov.closurecompilermavenplugin;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * @author urmuzov
 */
public class Utils {

    private static enum FormattingOption {

        PRETTY_PRINT,
        PRINT_INPUT_DELIMITER,;

        private void applyToOptions(CompilerOptions options) {
            switch (this) {
                case PRETTY_PRINT:
                    options.prettyPrint = true;
                    break;
                case PRINT_INPUT_DELIMITER:
                    options.printInputDelimiter = true;
                    break;
                default:
                    throw new RuntimeException("Unknown formatting option: " + this);
            }
        }
    }

    public static void setCompilationLevel(String compilationLevel, boolean debug, CompilerOptions compilerOptions) throws MojoFailureException {
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

    public static void setWarningLevel(String warningLevel, CompilerOptions compilerOptions) throws MojoFailureException {
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

    public static void setFormatting(String formatting, CompilerOptions compilerOptions) throws MojoFailureException {
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

    public static void setLoggingLevel(String loggingLevel, Compiler compiler) throws MojoFailureException {
        Level loggingLvl = null;
        try {
            loggingLvl = Level.parse(loggingLevel);
            Compiler.setLoggingLevel(loggingLvl);
        } catch (IllegalArgumentException e) {
            throw new MojoFailureException("Logging level invalid (values: [ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE, WARNING])", e);
        }
    }

    public static void logSources(boolean enabled, Log log, JSFileCollector collector) {
        log(enabled, log, collector, "Sources:");
    }

    public static void logExterns(boolean enabled, Log log, JSFileCollector collector) {
        log(enabled, log, collector, "Externs:");
    }

    public static void log(boolean enabled, Log log, JSFileCollector collector, String message) {
        if (enabled) {
            log.info(message);
            for (JSSourceFile f : collector.getFiles()) {
                log.info(f.getOriginalPath());
            }
        }
    }

    public static void log(boolean enabled, Log log, String message) {
        if (enabled) {
            log.info(message);
        }
    }

    public static String wildcardToRegex(String wildcard) {
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

    public static List<SimplePass> createSimplePasses(boolean debug, Log log, String simplePasses, File entriesDir, File outputDir) throws MojoFailureException {
        List<SimplePass> out = new ArrayList<SimplePass>();

        // если проходы не задали, возвращаем пустой массив
        if ((simplePasses == null || simplePasses.isEmpty())) {
            return out;
        }

        // проверяем директории
        if (entriesDir == null) {
            throw new MojoFailureException("simplePassesEntriesDir is null");
        }
        if (!entriesDir.isDirectory()) {
            throw new MojoFailureException("simplePassesEntriesDir ("+entriesDir+") is not a directory");
        }
        if (outputDir == null) {
            throw new MojoFailureException("simplePassesOutputDir is null");
        }

        String[] passNames = simplePasses.split("[ ;,]");
        Utils.log(debug, log, "simplePasses: " + Arrays.asList(passNames).toString());
        for (String passName : passNames) {
            out.add(new SimplePass(entriesDir, outputDir, passName));
        }
        return out;
    }

//    /**
//     * Update the classpath.
//     */
//    @SuppressWarnings("unchecked")
//    protected final void extendPluginClasspath()
//            throws MojoExecutionException {
//        // this code is inspired from http://teleal.org/weblog/Extending%20the%20Maven%20plugin%20classpath.html
//        final List<String> classpathElements = new ArrayList<String>();
//        try {
//            classpathElements.addAll(mavenProject.getRuntimeClasspathElements());
//        } catch (final DependencyResolutionRequiredException e) {
//            throw new MojoExecutionException("Could not get compile classpath elements", e);
//        }
//        final ClassLoader classLoader = createClassLoader(classpathElements);
//        Thread.currentThread().setContextClassLoader(classLoader);
//    }
//
//    /**
//     * @return {@link ClassRealm} based on project dependencies.
//     */
//    private ClassLoader createClassLoader(final List<String> classpathElements) {
//        getLog().debug("Classpath elements:");
//        final List<URL> urls = new ArrayList<URL>();
//        try {
//            for (final String element : classpathElements) {
//                final File elementFile = new File(element);
//                getLog().debug("Adding element to plugin classpath: " + elementFile.getPath());
//                urls.add(elementFile.toURI().toURL());
//            }
//        } catch (final Exception e) {
//            getLog().error("Error retreiving URL for artifact", e);
//            throw new RuntimeException(e);
//        }
//        return new URLClassLoader(urls.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
//    }
}
