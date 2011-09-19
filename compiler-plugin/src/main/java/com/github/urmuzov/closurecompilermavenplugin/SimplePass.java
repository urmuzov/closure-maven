package com.github.urmuzov.closurecompilermavenplugin;

import java.io.File;

public class SimplePass extends Pass {
    private String entryPointName;

    public SimplePass(File simplePassesEntriesDir, File simplePassesOutputDir, String entryPointName) {
        this.entryPointName = entryPointName;
        this.entryFile = new File(simplePassesEntriesDir.getPath() + "/" + entryPointName + ".entry.js");
        this.outputFile = new File(simplePassesOutputDir.getPath() + "/" + entryPointName + ".js");
    }

    @Override
    public String getTitleText() {
        return "== SimplePass (" + entryFile + " -> " + outputFile + " ==";
    }

    public String getEntryPointName() {
        return entryPointName;
    }
}
