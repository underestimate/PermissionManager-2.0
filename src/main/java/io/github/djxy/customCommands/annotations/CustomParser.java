package io.github.djxy.customCommands.annotations;

import io.github.djxy.customCommands.parsers.Parser;

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
