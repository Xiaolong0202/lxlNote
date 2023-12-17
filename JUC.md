## AQS结点

```java
static final class Node {
    	//每个结点可以被分为 独占模式结点 与 共享模式结点  用于独占锁与共享锁
        /** Marker to indicate a node is waiting in shared mode */
        static final Node SHARED = new Node();
        /** Marker to indicate a node is waiting in exclusive mode */
        static final Node EXCLUSIVE = null;
		//表示该节点已经失效 ，被取消 唯一一个大于0
        /** waitStatus value to indicate thread has cancelled */
        static final int CANCELLED =  1;
    	// 表示表示该节点后面的节点 被挂起  需要被唤醒
        /** waitStatus value to indicate successor's thread needs unparking */
        static final int SIGNAL    = -1;
        /** waitStatus value to indicate thread is waiting on condition */
        static final int CONDITION = -2;
        /**
         * waitStatus value to indicate the next acquireShared should
         * unconditionally propagate
         */
        static final int PROPAGATE = -3;

        
        volatile int waitStatus;

       
        volatile Node prev;

         
        volatile Node next;

        
        volatile Thread thread;

       
        Node nextWaiter;


```

## park unpark

```java
// 暂停当前线程
LockSupport.park(); 
// 恢复某个线程的运行
LockSupport.unpark(暂停线程对象)
    //直到线程被unpark 或者发送 interrupt()中断信号 才能被恢复运行
```



AQS

```java
   
    private transient volatile Node head;

    private transient volatile Node tail;
 
    private volatile int state; //AQS状态 决定了当前的锁的状态 0表示没有被占用
```



```java
private static final Unsafe unsafe = Unsafe.getUnsafe(); //内部有unsafe常量
,private static final long stateOffset; //用于记录各个字段相对于类的便宜值 便于unsafe对象操作，直接操作内存进行cas
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;
```

## 可被重写的方法

 ReentrantLock**锁机制是自己实现**，**底层调度**是由AQS抽象类实现的,比如ReentranLOCK当中既有公平锁又有非公平锁，他们都是AQS的子类

```java
//独占的获取与释放
protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }  
protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }
//共享锁的独占与释放

 protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }
protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }
```

## ReentrantLock通过自旋锁入队

```java
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```

## ReentrantLock抢占锁 unfair 实现

```java
public final void acquire(int arg) {
        if (!tryAcquire(arg) && //先枪锁  抢到就直接退出
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) //将addWaiter入队,入队之后自旋，直到 当前加入队列的结点为头结点  就创建中断，表示抢到锁了
            selfInterrupt();
    }
```

```java
final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) { //如果前驱节点是头结点就中断
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) && //如果不是的话就将前面的结点的状态设置为SIGNAL  将中断标识设置为true 
                    parkAndCheckInterrupt())  // 并且将当前线程进行park()就是将当前线程进入等待状态 
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```

```java
protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) { //判断c是否为0也就是aqs有无被线程占用
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {//CAS抢锁
                    setExclusiveOwnerThread(current);//抢到了则设置当前的线程
                    return true; 
                }
            }
            else if (current == getExclusiveOwnerThread()) {//判断当前占有的线程是不是自己的线程  ，如果是的话就添加可重入次数
                int nextc = c + acquires;  
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;//没有抢到则返回false 
        }
```

