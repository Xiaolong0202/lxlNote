## redis 是 Ap

主从模式下对于 redis 主节点 与redis 从节点来说都是  非阻塞的，意思是在进行主从同步的时候，可以对主节点与从节点操作

# 数据结构

## 动态字符串SDS

使用结构体维护一个字符串

![image-20231205121139561](mdPic/redis/image-20231205121139561.png)

- 获取字符串长度时间复杂度 为O 1
- 动态扩容
- 支持预分配 减少内存申请次数
- 二进制安全

## intset

维护了一个唯一、有序的int 数组 其中enoding 表示的是 编码方式，也就是表示的每个数字占了几个字节

![image-20231205122943330](mdPic/redis/image-20231205122943330.png)

intset还支持动态的改变编码

![image-20231205123103685](mdPic/redis/image-20231205123103685.png)

## DICT

与java中的哈希表实现类似，并且，一个Dict包含两个hash表,一个存储数据，另外一个

实现键值的映射

![image-20231205124523684](mdPic/redis/image-20231205124523684.png)

## ZipList

维护一块连续的内存区域,实现了链表的功能，但是内存占用更小，但是是连续的内存空间，数据过多连续空间申请效率低

存在连锁更新，使用listpack替代

![image-20231205125517450](mdPic/redis/image-20231205125517450.png)

![image-20231205125544373](mdPic/redis/image-20231205125544373.png)

![image-20231205125931700](mdPic/redis/image-20231205125931700.png)

## QuickList

结点为ZipList的双端链表,还可以对zipList进行压缩，并控制ZipList的大小，结合了链表与连续空间的两者的优势

![image-20231205131726685](mdPic/redis/image-20231205131726685.png)

## SkipList

跳表，多级指针，元素按顺序排列，跳着查找，用空间换时间

一个结点的next指针 由多级

索引的层级最多 32 级

按照score来排序

![image-20231205134816655](mdPic/redis/image-20231205134816655.png)

## RedisObject

底层数据结构会被封装为RedisObjcet

![image-20231205135733218](mdPic/redis/image-20231205135733218.png)

## 五种基本数据类型

### String

 ![image-20231205140445779](mdPic/redis/image-20231205140445779.png)

三种编码方式：

![image-20231205140550351](mdPic/redis/image-20231205140550351.png)

### LIST

```
lpush city a
lpush city b
lpush city c
lindex  city 1
lrange city 0 -1
lset city 1 ins
linsert city before ins insssbefo
llen city
```

在之前使用的是zipList+LinkedList的方式,元素少的时候是使用的zipList，元素多的时候是使用的LinkedList

在redis 3.2 版本之后 就使用的quickList来保存数据

### SET

单列集合

```
sadd freind liu wang  yuan
srem  freind liu
srandmember freind 2
-- 求并集
sunion freind
-- 集合数量
scard  freind
-- 是否contain
sismember freind yuan
-- 求交集
sinter freind
smembers numset
```

使用的是Dict,其中只存key,Value统一为null

无序且保障数据的唯一性

- 当数据都是**整数类型并且数据数量不超过set-max-intset-extries**的时候就会使用intSet 来实现， 有序且 唯一的整数数组，使用二分查找，并且使用intset的时候，他是有序的
- 当存在不是整数的数据的时候就会使用Dict,使用hash表

### ZSET  (Sorted Set)

```
zadd zs 1 a  2 b
zscore zs b
zrem zs b
zrange zs 0 -1 withscores
zrevrange zs 0 -1 withscores
zrangebyscore zs 0  9999 withscores
zcard zs
```



**键值存储、数据唯一、可排序**

![image-20231205152659886](mdPic/redis/image-20231205152659886.png)

元素少的时候会使用ZipList

![image-20231205153203650](mdPic/redis/image-20231205153203650.png)

ZipList的Zset实现：

![image-20231205153505418](mdPic/redis/image-20231205153505418.png)

### MAP

```
hset map a stra
hsetnx map a stra
hexists map b
hget map a
hdel map a
hlen map
hkeys map
hvals map
```

底层使用的是Dict,与zipList

![image-20231205161619103](mdPic/redis/image-20231205161619103.png)

## Linux五种不同的IO模型

### 阻塞io

recvfrom

本[函数](https://baike.baidu.com/item/函数/301912?fromModule=lemma_inlink)用于从（已连接）[套接口](https://baike.baidu.com/item/套接口/10058888?fromModule=lemma_inlink)上接收数据

![image-20231205162924484](mdPic/redis/image-20231205162924484.png)

### 非阻塞IO

recvfrom

性能上没有什么提升，会轮询结果

![image-20231205163032165](mdPic/redis/image-20231205163032165.png)

### IO多路复用 

![image-20231205163949424](mdPic/redis/image-20231205163949424.png)

Linux中是先io多路复用的三个函数

- select 

  无法知道是哪个fd,并且数量最多1024

  ![image-20231205164950860](mdPic/redis/image-20231205164950860.png)

- poll  前两个不会告诉用户空间是哪个资源准备好了，还要去查找

![image-20231205165343741](mdPic/redis/image-20231205165343741.png)

- epoll  但是epoll却会告诉系统是哪一个资源准备好了，直接调用即可



### 信号驱动io

![image-20231205174825888](mdPic/redis/image-20231205174825888.png)

### 异步io

![image-20231205174940554](mdPic/redis/image-20231205174940554.png)

![image-20231205175100411](mdPic/redis/image-20231205175100411.png)