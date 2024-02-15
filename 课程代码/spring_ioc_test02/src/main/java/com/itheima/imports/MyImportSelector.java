package com.itheima.imports;

import com.itheima.beans.OtherBean2;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //参数annotationMetadata叫做注解媒体数组，该对象内部封装是当前使用了@Import注解的类上的其他注解的元信息
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(ComponentScan.class.getName());
        String[] basePackages = (String[]) annotationAttributes.get("basePackages");
        System.out.println(basePackages[0]);
        //返回的数组封装是需要被注册到Spring容器中的Bean的全限定名
        return new String[]{OtherBean2.class.getName()};
    }
}
