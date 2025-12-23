package com.gzhu;

// 自定义简单哈希集合实现
class HashSet {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node[] buckets;
    private int size;

    // 内部节点类，用于处理哈希冲突（链地址法）
    private static class Node {
        String value;
        Node next;

        Node(String value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    public HashSet() {
        this(DEFAULT_CAPACITY);
    }

    public HashSet(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        buckets = new Node[initialCapacity];
        size = 0;
    }

    // 添加元素
    public boolean add(String value) {
        // 检查是否需要扩容
        if ((float)size / buckets.length >= LOAD_FACTOR) {
            resize();
        }

        int index = getIndex(value);
        Node current = buckets[index];

        // 检查是否已存在
        while (current != null) {
            if (current.value.equals(value)) {
                return false; // 已存在，不添加
            }
            current = current.next;
        }

        // 添加到链表头部
        buckets[index] = new Node(value, buckets[index]);
        size++;
        return true;
    }

    // 删除元素
    public boolean remove(String value) {
        int index = getIndex(value);
        Node current = buckets[index];
        Node prev = null;

        while (current != null) {
            if (current.value.equals(value)) {
                if (prev == null) {
                    buckets[index] = current.next; // 删除头节点
                } else {
                    prev.next = current.next; // 删除中间或尾部节点
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }

    // 检查是否包含元素
    public boolean contains(String value) {
        int index = getIndex(value);
        Node current = buckets[index];

        while (current != null) {
            if (current.value.equals(value)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    // 获取集合大小
    public int size() {
        return size;
    }

    // 检查集合是否为空
    public boolean isEmpty() {
        return size == 0;
    }

    // 清空集合
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
        size = 0;
    }

    // 转换为数组
    public String[] toArray() {
        String[] array = new String[size];
        int index = 0;

        for (int i = 0; i < buckets.length; i++) {
            Node current = buckets[i];
            while (current != null) {
                array[index++] = current.value;
                current = current.next;
            }
        }

        return array;
    }

    // 获取所有元素（已排序）
    public String[] toSortedArray() {
        String[] array = toArray();
        // 使用插入排序
        for (int i = 1; i < array.length; i++) {
            String key = array[i];
            int j = i - 1;

            // 将字符串转换为整数进行比较
            int keyValue = Integer.parseInt(key);
            while (j >= 0 && Integer.parseInt(array[j]) > keyValue) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
        return array;
    }

    // 获取元素的字符串表示（已排序）
    public String getSortedString() {
        String[] sorted = toSortedArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sorted.length; i++) {
            sb.append(sorted[i]);
            if (i < sorted.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    // 计算哈希索引
    private int getIndex(String value) {
        int hashCode = value.hashCode();
        // 确保索引为正数
        return (hashCode & 0x7fffffff) % buckets.length;
    }

    // 扩容
    private void resize() {
        Node[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        size = 0;

        // 重新哈希所有元素
        for (int i = 0; i < oldBuckets.length; i++) {
            Node current = oldBuckets[i];
            while (current != null) {
                add(current.value);
                current = current.next;
            }
        }
    }

    // 打印哈希表结构（用于调试）
    public void printStructure() {
        System.out.println("HashSet Structure (Size: " + size + ", Capacity: " + buckets.length + ")");
        for (int i = 0; i < buckets.length; i++) {
            System.out.print("Bucket " + i + ": ");
            Node current = buckets[i];
            while (current != null) {
                System.out.print(current.value);
                if (current.next != null) {
                    System.out.print(" -> ");
                }
                current = current.next;
            }
            System.out.println();
        }
    }
}