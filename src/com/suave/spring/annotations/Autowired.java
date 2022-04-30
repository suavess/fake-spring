package com.suave.spring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author suave
 * @date 2022-04-30 22:27
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME )
public @interface Autowired {
    String value() default "";
}
