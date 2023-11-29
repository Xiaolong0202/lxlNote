## 缓存扩展

PerpetualCache，是一级缓存的实现类，是mybatis中默认的缓存实现]

先实现mybatis中的cache接口，实现putObject、与readObject

可以在配置文件中配置：

```xml
<cache type = "Cache接口实现类的权限定名"/>
```

## 中的设计模式

从MyBatis的整体架构设计来分析
基础模块:
缓存模块: 装饰器模式
日志模块: 适配器模式[策略模式]代理模式
反射模块:工厂模式，装饰器模式
Mapping:代理模式
SqlSessionFactoky : SqlSessionFactoryBuilder 建造者模式