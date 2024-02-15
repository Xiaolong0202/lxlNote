package com.itheima.anno;

import com.itheima.imports.MyImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(MyImportBeanDefinitionRegistrar.class)
public @interface MyMapperScan {
}
