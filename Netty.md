# Netty

导包

```xml
<dependencies>
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.76.Final</version>
    </dependency>
</dependencies>
```

缓冲区

### ByteBuf介绍

Netty并没有使用NIO中提供的ByteBuffer来进行数据装载，而是自行定义了一个ByteBuf类。

那么这个类相比NIO中的ByteBuffer有什么不同之处呢？

- 写操作完成后无需进行`flip()`翻转。
- 具有比ByteBuffer更快的响应速度。
- 动态扩容。

首先我们来看看它的内部结构：

```java
public abstract class AbstractByteBuf extends ByteBuf {
    ...
    int readerIndex;   //index被分为了读和写，是两个指针在同时工作
    int writerIndex;
    private int markedReaderIndex;    //mark操作也分两种
    private int markedWriterIndex;
    private int maxCapacity;    //最大容量，没错，这玩意能动态扩容
```

可以看到，读操作和写操作分别由两个指针在进行维护，每写入一次，`writerIndex`向后移动一位，每读取一次，也是`readerIndex`向后移动一位，当然`readerIndex`不能大于`writerIndex`，这样就不会像NIO中的ByteBuffer那样还需要进行翻转了。

![image-20230717133659215](mdPic/Netty/image-20230717133659215.png)

```java
		ByteBuf buffer = Unpooled.buffer(10);//创建一个缓冲区
        System.out.println("初始状态:"+ Arrays.toString(buffer.array()));
        buffer.writeInt(1234665321);
        System.out.println("中间状态:"+ Arrays.toString(buffer.array()));
        buffer.readChar();
        System.out.println("中间状态:"+ Arrays.toString(buffer.array()));
        buffer.discardReadBytes();//丢弃操作.将可读的部分内容丢在最前面，读写指针往前移

        buffer.clear();//读写指针都归零
        System.out.println("结束状态:"+ Arrays.toString(buffer.array()));
```

```java
ByteBuf byteBuf = Unpooled.wrappedBuffer("1231sadasad".getBytes());
        Unpooled.copiedBuffer("1231sadasad".getBytes());//copiedBuffer是会将原来的数组中的数据拷贝创建一个新的
        byteBuf.readChar();
        ByteBuf slice = byteBuf.slice();//切片
        byteBuf.arrayOffset();
        byteBuf.array();
```

动态扩容

```java
//动态扩容,也可以在创建时候指定最大容量，使得不能动态扩容
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println(byteBuf.capacity());
        byteBuf.writeCharSequence("萨加不加萨等不及萨等不及萨等不及萨比就2", StandardCharsets.UTF_8);
        System.out.println(byteBuf.capacity());
```

复合缓冲区:

![image-20230717140616360](mdPic/Netty/image-20230717140616360.png)

创建复合缓冲区

```java
CompositeByteBuf byteBufs = Unpooled.compositeBuffer();
byteBufs.addComponent(Unpooled.copiedBuffer("你好吗".getBytes()));
byteBufs.addComponent(Unpooled.copiedBuffer("我很好".getBytes()));
```

在 Netty 中，`CompositeByteBuf.array()` 方法报错的原因可能是因为 `CompositeByteBuf` 对象是一个组合缓冲区，它由多个子缓冲区组成，并不具备一个连续的字节数组来支持 `array()` 方法。

`CompositeByteBuf` 是一种特殊的 `ByteBuf` 实现，它将多个子缓冲区组合在一起，形成一个逻辑上连续但物理上分散的缓冲区。由于 `CompositeByteBuf` 不是由单个连续的字节数组支持的，因此调用 `array()` 方法时会抛出 `UnsupportedOperationException` 异常。

如果你想获取 `CompositeByteBuf` 的数据，可以考虑使用其他方法，如 `getByte(int index)`、`readByte()`、`readBytes(byte[] dst)` 等方法来逐个获取字节数据或将数据读取到字节数组中。



池化缓冲区

![image-20230717141419458](mdPic/Netty/image-20230717141419458.png)

零拷贝:

零拷贝是一种I/O操作优化技术，可以快速高效地将数据从文件系统移动到网络接口，而不需要将其从内核空间复制到用户空间

实现零拷贝我们这里演示三种方案：

1. 使用虚拟内存

   现在的操作系统基本都是支持虚拟内存的，我们可以让内核空间和用户空间的虚拟地址指向同一个物理地址，这样就相当于是直接共用了这一块区域，也就谈不上拷贝操作了：

   ![image-20230717142505212](mdPic/Netty/image-20230717142505212.png)

2. 使用mmap/write内存映射

   实际上这种方式就是将内核空间中的缓存直接映射到用户空间缓存，比如我们之前在学习NIO中使用的MappedByteBuffer，就是直接作为映射存在，当我们需要将数据发送到Socket缓冲区时，直接在内核空间中进行操作就行了：

   ![image-20230717142526165](mdPic/Netty/image-20230717142526165.png)

   不过这样还是会出现用户态和内核态的切换，我们得再优化优化。

3. 使用sendfile方式

   在Linux2.1开始，引入了sendfile方式来简化操作，我们可以直接告诉内核要把哪个文件数据拷贝拷贝到Socket上，直接在内核空间中一步到位：

   ![image-20230717142536570](mdPic/Netty/image-20230717142536570.png)

   比如我们之前在NIO中使用的`transferTo()`方法，就是利用了这种机制来实现零拷贝的。



## Netty的工作模型

![image-20230717143028445](mdPic/Netty/image-20230717143028445.png)

![image-20230717143134579](mdPic/Netty/image-20230717143134579.png)

Netty实现一个服务器

```java
 public static void main(String[] args) {
        //创建BossGroup与WorkGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //创建一个服务器的启动引导类，就是启动之前对服务器进行一些设置以后才能启动
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(bossGroup,workGroup)//指定事件循环组
                .channel(NioServerSocketChannel.class)//指定类型
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //获取流水线,一个流水线上面有很多的Handler
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){//添加一个handler,入栈
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//ctx是上下文,msg默认是ByteBuf类
                                ByteBuf byteBuf = (ByteBuf) msg;
                                System.out.println(Thread.currentThread().getName()+">>收到客户端传来的信息:"+byteBuf.toString(StandardCharsets.UTF_8));
                                //通过上下文返回一个响应,返回一个数据
                                ctx.writeAndFlush(Unpooled.wrappedBuffer("已经收到消息了".getBytes()));
                            }
                        });
                    }
                });
        serverBootstrap.bind(8080);

    }
```