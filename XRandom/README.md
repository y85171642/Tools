#开篇

前一阵项目需要用到服务器（Java）、客户端（C#）同步随机，经测试发现Java 和 C#的随机算法是不一样的，也导致即使相同的随机种子得到的随机结果也不同。所以本想到网上找找随机算法相关的文章和源码，但是感觉讲解的都不是很透彻，随机也比较简单达不到需要的随机效果。
#方法

由于.Net是闭源的，看不到底层的代码实现，所以只能改Java底层的随机算法了，找到Java Random类的源码，然后改成一份C#的，一份Java版的（防止之后底层更新随机算法，导致随机不一致！）。这样就达到随机同步。

这里要声明一点，由于作者水平有限，当我看到Java 代码里面有类似 java.util.concurrent.atomic.AtomicLong 这个类的时候，一脸懵逼，原子Long？一篇文章这样解释 ： “基本工作原理是使用了同步synchronized的方法实现了对一个long, 对象的增、减、赋值（更新）操作. 比如对于++运算符AtomicLong可以将它持有的long 能够atomic 地递增。” 这样看来是有些问题的。

但是由于Atomic有涉及到Number、Serializable等类的使用，重写起来会很麻烦。本文重写的随机算法去除了原子操作，所以在多线程操作情况下需注意使用同步的问题。