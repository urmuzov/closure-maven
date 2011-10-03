package com.github.urmuzov.closurecompilermavenplugin;

import com.google.javascript.jscomp.CompilerOptions;

/**
* User: urmuzov
* Date: 10/3/11
* Time: 5:05 PM
*/
enum FormattingOption {

    PRETTY_PRINT,
    PRINT_INPUT_DELIMITER,;

    public void applyToOptions(CompilerOptions options) {
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
