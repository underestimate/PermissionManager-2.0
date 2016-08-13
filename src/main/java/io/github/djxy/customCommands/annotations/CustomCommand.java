package io.github.djxy.customCommands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Samuel on 2016-07-29.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCommand {
    String command();
    String permission() default "";
    CustomParser[] parsers();
}
