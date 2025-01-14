package com.ethpalser.chess.annotation;

import java.lang.annotation.Documented;

@Documented
public @interface ClassPreamble {
    String author();
    String created();
    int majorVersion() default 1; // Version 1 and 2 are no longer present
    int minorVersion() default 0;
    String lastModified() default "N/A";
    String lastModifiedBy() default "N/A";
}
