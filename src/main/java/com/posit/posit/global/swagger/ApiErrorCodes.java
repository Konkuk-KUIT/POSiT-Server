package com.posit.posit.global.swagger;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiErrorCodes {
    SwaggerErrorSet value();
}
