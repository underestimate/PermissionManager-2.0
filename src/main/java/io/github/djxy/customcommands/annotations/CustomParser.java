package io.github.djxy.customcommands.annotations;

import io.github.djxy.customcommands.parsers.Parser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Samuel on 2016-07-29.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomParser {
    String argument();
    Class<? extends Parser> parser();
}
