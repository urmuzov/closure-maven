package com.github.urmuzov.closurecompilermavenplugin;

import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.SourceFile;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author urmuzov
 */
public class JSFileCollector {

    private List<SourceFile> files = new ArrayList<SourceFile>();

    public JSFileCollector() {
    }

    public List<SourceFile> getFiles() {
        List<SourceFile> src = new ArrayList<SourceFile>();
        src.addAll(files);
        Collections.sort(src, new Comparator<SourceFile>() {
            @Override
            public int compare(SourceFile jsSourceFile, SourceFile jsSourceFile1) {
                return jsSourceFile.getOriginalPath().compareTo(jsSourceFile1.getOriginalPath());
            }
        });
        return src;
    }

    public void collectPaths(Collection<String> paths, Pattern excludePattern) {
        if (paths == null) {
            return;
        }
        for (String path : paths) {
            collectPath(path, excludePattern);
        }
    }

    public void collectFiles(Collection<File> files, Pattern excludePattern) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            collectFile(file, excludePattern);
        }
    }

    public void collectJSSourceFiles(Collection<SourceFile> files) {
        if (files == null) {
            return;
        }
        for (SourceFile file : files) {
            collect(file);
        }
    }

    public void collectPath(String path, Pattern excludePattern) {
        collectFile(new File(path), excludePattern);
    }

    public void collect(SourceFile jSSourceFile) {
        files.add(jSSourceFile);
    }

    public void collectFile(File file, Pattern excludePattern) {
        if (file.isFile()) {
            if (isSourceFile(file)) {
                if (excludePattern == null || !excludePattern.matcher(file.getName()).matches()) {
                    collect(JSSourceFile.fromFile(file));
                }
            }
        } else if (file.isDirectory()) {
            collectDirRecurent(file, excludePattern);
        }
    }

    public void collectDirRecurent(File dir, Pattern excludePattern) {
        if (dir.isFile()) {
            collectFile(dir, excludePattern);
        } else {
            File[] dirFiles = dir.listFiles();
            if (dirFiles != null) {
                for (File file : dirFiles) {
                    collectFile(file, excludePattern);
                }
            }
        }
    }

    protected boolean isSourceFile(File file) {
        return file.getPath().toLowerCase().endsWith(".js");
    }
}
