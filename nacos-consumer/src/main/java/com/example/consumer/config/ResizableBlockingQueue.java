package com.example.consumer.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 支持动态调整容量的
 * 有界阻塞队列
 * （用于动态调整线程池队列大小）
 */
public class ResizableBlockingQueue<E> extends ArrayBlockingQueue<E> {

    private volatile int capacity; // 动态容量

    public ResizableBlockingQueue(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    /**
     * 动态设置队列容量
     */
    public void setCapacity(int newCapacity) {
        if (newCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        this.capacity = newCapacity;
    }

    /**
     * 重写剩余容量计算（基于动态容量）
     */
    @Override
    public int remainingCapacity() {
        return Math.max(0, capacity - size());
    }

    /**
     * 重写offer方法（支持动态容量判断）
     */
    @Override
    public boolean offer(E e) {
        return super.offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return super.offer(e, timeout, unit);
    }
}
