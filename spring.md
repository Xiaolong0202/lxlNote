## 构造方法

默认使用无参的构造方法

如果只有一个构造方法的话就会使用这一个构造方法

如果有多个构造方法则需要使用@Autowired注解指定构造函数（没有无参的情况下）

如果构造方法中的参数，在spring对象池当中没有对应的bean,就会报错

构造方法注入有循环依赖，则使用@Lazy让一方懒加载

## 单例bean

单例bean指的一个spring容器内，同一个名字内部，只能有同一个类型的bean

```java
@Autowired(required = false) //设置为不强制依赖	
```

## AOP类的实现

一个aop代理类会继承原有类或者接口，但是aop代理类中的父类中的属性字段是为null,的而是这些代理类，都持有了原有**父类的对象实例target**,调用aop的时候会调用target的对应方法来实现原有逻辑

![image-20231215152329727](mdPic/spring/image-20231215152329727.png)

```java'
joinPoint.getTarget()获取普通对象
joinPoint.getThis()获取代理对象
```

想要开启jdk动态代理需要在配置文件里面设置

```properties
spring.aop.proxy-target-class=false
```

并且还要满足被动态代理的类有实现的接口

jdk动态代理与cglib

```java
@Override
public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
    // 判断是否需要使用CGLIB代理
    if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
        // 获取目标类
        Class<?> targetClass = config.getTargetClass();
        if (targetClass == null) {
            throw new AopConfigException("TargetSource cannot determine target class: " +
                                         "Either an interface or a target is required for proxy creation.");
        }
        // 判断是否需要使用JDK动态代理
        if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
            return new JdkDynamicAopProxy(config);
        }
        // 使用CGLIB代理
        return new ObjenesisCglibAopProxy(config);
    } else {
        // 使用JDK动态代理
        return new JdkDynamicAopProxy(config);
    }
}

```

