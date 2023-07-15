# NIO

 ![image-20230715144402597](mdPic/NIO/image-20230715144402597.png)

## 缓冲区，抽象类Buffer

![image-20230715144551373](mdPic/NIO/image-20230715144551373.png)

他有很多实现的子类，每个基本数据类型都有一个对应的Buffer实现(除了bollean)

```java
 // Invariants: mark <= position <= limit <= capacity
    private int mark = -1;
    private int position = 0;//指针
    private int limit;//限制指针的位置
    private int capacity;//最大容量
```



默认创建的是堆缓冲区

```java
 public static void main(String[] args) {
        //    IntBuffer buffer = IntBuffer.allocate(10);//创建一个能够容纳10个int的缓冲区
        int[] intArr = new int[]{1,2,3,4,5,6};
        IntBuffer buffer2 = IntBuffer.wrap(intArr);
        System.out.println(buffer2.get(0));
        buffer2.put(0,9);
        System.out.println(buffer2.get(0));
    }
}
```

使用wrap方法得到的缓冲区，实际上是在维护被wrap的那个数组



那么它的内部是本质上如何进行操作的呢？我们来看看它的源码：

```java
public static IntBuffer allocate(int capacity) {
    if (capacity < 0)   //如果申请的容量小于0，那还有啥意思
        throw new IllegalArgumentException();
    return new HeapIntBuffer(capacity, capacity);   //可以看到这里会直接创建一个新的IntBuffer实现类
      //HeapIntBuffer是在堆内存中存放数据，本质上就数组，一会我们可以在深入看一下
}
复制代码
public static IntBuffer wrap(int[] array, int offset, int length) {
    try {
          //可以看到这个也是创建了一个新的HeapIntBuffer对象，并且给了初始数组以及截取的起始位置和长度
        return new HeapIntBuffer(array, offset, length);
    } catch (IllegalArgumentException x) {
        throw new IndexOutOfBoundsException();
    }
}

public static IntBuffer wrap(int[] array) {
    return wrap(array, 0, array.length);   //调用的是上面的wrap方法
}
复制代码
```

那么这个HeapIntBuffer又是如何实现的呢，我们接着来看：

```java
HeapIntBuffer(int[] buf, int off, int len) { // 注意这个构造方法不是public，是默认的访问权限
    super(-1, off, off + len, buf.length, buf, 0);   //你会发现这怎么又去调父类的构造方法了，绕来绕去
      //mark是标记，off是当前起始下标位置，off+len是最大下标位置，buf.length是底层维护的数组真正长度，buf就是数组，最后一个0是起始偏移位置
}
复制代码
```

我们又来看看IntBuffer中的构造方法是如何定义的：

```java
final int[] hb;                  // 只有在堆缓冲区实现时才会使用
final int offset;
boolean isReadOnly;                 // 只有在堆缓冲区实现时才会使用

IntBuffer(int mark, int pos, int lim, int cap,   // 注意这个构造方法不是public，是默认的访问权限
             int[] hb, int offset)
{
    super(mark, pos, lim, cap);  //调用Buffer类的构造方法
    this.hb = hb;    //hb就是真正我们要存放数据的数组，堆缓冲区底层其实就是这么一个数组
    this.offset = offset;   //起始偏移位置
}
复制代码
```

最后我们来看看Buffer中的构造方法：

```java
Buffer(int mark, int pos, int lim, int cap) {       // 注意这个构造方法不是public，是默认的访问权限
    if (cap < 0)  //容量不能小于0，小于0还玩个锤子
        throw new IllegalArgumentException("Negative capacity: " + cap);
    this.capacity = cap;   //设定缓冲区容量
    limit(lim);    //设定最大position位置
    position(pos);   //设定起始位置
    if (mark >= 0) {  //如果起始标记大于等于0
        if (mark > pos)  //并且标记位置大于起始位置，那么就抛异常（至于为啥不能大于我们后面再说）
            throw new IllegalArgumentException("mark > position: ("
                                               + mark + " > " + pos + ")");
        this.mark = mark;   //否则设定mark位置（mark默认为-1）
    }
}
复制代码
```

通过对源码的观察，我们大致可以得到以下结构了：

![image-20230306172558001](https://fast.itbaima.net/2023/03/06/tAihmNPUVbHBJZI.png)

现在我们来总结一下上面这些结构的各自职责划分：

- Buffer：缓冲区的一些基本变量定义，比如当前的位置（position）、容量 (capacity)、最大限制 (limit)、标记 (mark)等，你肯定会疑惑这些变量有啥用，别着急，这些变量会在后面的操作中用到，我们逐步讲解。
- IntBuffer等子类：定义了存放数据的数组（只有堆缓冲区实现子类才会用到）、是否只读等，也就是说数据的存放位置、以及对于底层数组的相关操作都在这里已经定义好了，并且已经实现了Comparable接口。
- HeapIntBuffer堆缓冲区实现子类：数据存放在堆中，实际上就是用的父类的数组在保存数据，并且将父类定义的所有底层操作全部实现了。

## 缓冲区的写操作

```java
   IntBuffer buffer = IntBuffer.allocate(10);//创建一个能够容纳10个int的缓冲区
            buffer.put(1);
            buffer.put(2);//不指定索引，根据指针来确定索引
            buffer.put(6,999);//指定索引
        System.out.println(Arrays.toString(buffer.array()));
            buffer.put(new int[]{666,555,333});//将数组内容添加至buffer当中，从当前指针开始添加
        System.out.println(Arrays.toString(buffer.array()));
            buffer.put(IntBuffer.wrap(new int[]{777,666,333,555}));
        System.out.println(Arrays.toString(buffer.array()));//也可以将buffer写入
```

## 缓冲区读操作

```java
  IntBuffer intBuffer = IntBuffer.wrap(new int[]{1,2,3,4,5,6,7,3,354,3,});
            //获取元素就是直接获取数组中对应的值
        System.out.println(intBuffer.get());
        //获取指定的元素
        System.out.println(intBuffer.get(5));
        //将Buffer当中的元素放入指定的数组当中,从当前的指针开始
        int [] arr = new int[5];
        intBuffer.get(arr);
        System.out.println(Arrays.toString(arr));
        System.out.println(intBuffer.remaining());//remaining获取剩余的容量 remain = limit - position
```

flip方法:

```java
     // 将Buffer从写模式切换到读模式（必须调用这个方法）
        buffer.flip();

   //源代码
    public Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
```

mark来标记（艾克大招）

```java
   IntBuffer intBuffer = IntBuffer.wrap(new int[]{1,2,3,4,5,6,7,3,354,3,});
       //使用mark来实现控制position
            intBuffer.get();
            intBuffer.mark();//标记当前的position指针，将值赋值给mark
            intBuffer.get();
            intBuffer.reset();//将mark的值赋值给position指针
```

