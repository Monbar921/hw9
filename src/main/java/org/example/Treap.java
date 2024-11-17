package org.example;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Теория<br/>
 * <a href="http://e-maxx.ru/algo/treap">http://e-maxx.ru/algo/treap</a><br/>
 * <a href="https://www.geeksforgeeks.org/treap-a-randomized-binary-search-tree/">https://www.geeksforgeeks.org/treap-a-randomized-binary-search-tree/</a><br/>
 * <a href="https://www.geeksforgeeks.org/implementation-of-search-insert-and-delete-in-treap/">https://www.geeksforgeeks.org/implementation-of-search-insert-and-delete-in-treap/</a><br/>
 * <a href="http://faculty.washington.edu/aragon/pubs/rst89.pdf">http://faculty.washington.edu/aragon/pubs/rst89.pdf</a><br/>
 * <a href="https://habr.com/ru/articles/101818/">https://habr.com/ru/articles/101818/</a><br/>
 * <a href="https://habr.com/ru/articles/102006/">https://habr.com/ru/articles/102006/</a><br/>
 * Примеение в linux kernel<br/>
 * <a href="https://www.kernel.org/doc/mirror/ols2005v2.pdf">https://www.kernel.org/doc/mirror/ols2005v2.pdf</a>
 */
public class Treap {
    public static View view = View.medium;
    Node root;

    public static View getFieldView(String cmd) {
        try {
            return View.valueOf(cmd);
        } catch (Exception e) {
            return view;
        }
    }

    public void add(Long value) {
        root = merge(root, new Node(value));
    }

    public void add(int pos, Long value) {
        Node[] split = root.split(pos);
        Node tmp = new Node(value);
        root = merge(merge(split[0], tmp), split[1]);
    }

    private void add(Node node, long value, Node increaseValue) {
        if (node == null) {
            return;
        }
        add(node.left, value, increaseValue);
        node.value += value * increaseValue.value;
        increaseValue.value += 1;
        add(node.right, value, increaseValue);
    }

    public void remove(int pos) {
        if (sizeOf(root) <= pos) {
            return;
        }
        Node[] low = root.split(pos);
        Node[] high = low[1].split(1);

        root = merge(low[0], high[1]);
    }

    public boolean search(Long value) {
        return search(root, value) != null;
    }

    public Long getStats(int a, int b) {
        if (a > b) {
            return null;
        }
        // [0 < a) [a < N)
        Node[] low = root.split(a);
        // [a < b) [b < N)
        Node[] high = low[1].split(b - a);
        return inorder(high[0]);
    }

    public void addToAll(int a, int b, long value) {
        // [0 < a) [a < N)
        Node[] low = root.split(a);
        // [a < b) [b < N)
        Node[] high = low[1].split(b - a);

        add(high[0], value, new Node((long) 1));
        high[0].recalculate();
        root = merge(merge(low[0], high[0]), high[1]);
    }

    public void setValueOnInterval(int a, int b, long value) {
        Node[] low = root.split(a);
        // [a < b) [b < N)
        Node[] high = low[1].split(b - a);

        setAll(high[0], value);
        high[0].recalculate();
        root = merge(merge(low[0], high[0]), high[1]);
    }

    private void setAll(Node node, long value) {
        if (node == null) {
            return;
        }
        setAll(node.left, value);
        node.value = value;
        setAll(node.right, value);
    }

    public void reverse(int a, int b) {
        // [0 < a) [a < N)
        Node[] low = root.split(a);
        // [a < b) [b < N)
        Node[] high = low[1].split(b - a);

        high[0].reversePromise();
        root = merge(merge(low[0], high[0]), high[1]);
    }

    public void shiftLeft(int pos) {
        Node[] split = root.split(pos);
        root = merge(split[1], split[0]);
    }

    public void shiftRight(int pos) {
        Node[] split = root.split(root.size - pos);
        root = merge(split[1], split[0]);
    }

    private Long search(Node cur, Long value) {
        if (cur == null) {
            return null;
        }
        if (cur.value == value) {
            return value;
        }
        if (search(cur.left, value) != null) {
            return value;
        }
        return search(cur.right, value);
    }

    public List<String> inorder() {
        List<String> res = new ArrayList<>();
        inorder(root, res);
        return res;
    }

    public Long inorder(Node subRoot, Long sum) {
        if (subRoot != null) {
            sum += inorder(subRoot.left, sum);
            sum += subRoot.value;
            sum += inorder(subRoot.right, sum);
            return sum;
        }
        return 0L;
    }

    private void inorder(Node subRoot, Result result) {
        if (subRoot == null) {
            return;
        }
        //cur.pushPromise();
        inorder(subRoot.left, result);
        result.sum += subRoot.value;
        inorder(subRoot.right, result);
    }

    public Long inorder(Node subRoot) {
        Result result = new Result(0L);
        inorder(subRoot, result);
        return result.sum;
    }

    public Node[] split(int pos) {
        return root.split(pos);
    }


    public java.lang.Long findKth(int k) {
        Node cur = root;
        while (cur != null) {
            int leftSize = sizeOf(cur.left);
            if (leftSize == k) {
                return cur.value;
            }
            cur.pushPromise();
            cur = leftSize > k ? cur.left : cur.right;
            if (leftSize < k) {
                k -= leftSize + 1;
            }
        }
        return null;
    }


    public Node merge(Node leftTree, Node rightTree) {
        push(leftTree);
        push(rightTree);
        if (leftTree == null) {
            return rightTree;
        }
        if (rightTree == null) {
            return leftTree;
        }
        if (leftTree.priority < rightTree.priority) {
            Node newRight = merge(leftTree.right, rightTree);
            return new Node(leftTree.value, leftTree.priority, leftTree.left, newRight);
        } else {
            Node newLeft = merge(leftTree, rightTree.left);
            return new Node(rightTree.value, rightTree.priority, newLeft, rightTree.right);
        }
    }

    private static void push(Node tree) {
        if (tree != null) {
            tree.pushPromise();
        }
    }

    private static int sizeOf(Node node) {
        return node != null ? node.size : 0;
    }

    private void inorder(Node cur, List<String> res) {
        if (cur == null) {
            return;
        }
        cur.pushPromise();
        inorder(cur.left, res);
        res.add(cur.toString());
        inorder(cur.right, res);
    }

    public static class Statistic {
        long minValue;
        long sumValue;
        long maxValue;

        @Override
        public String toString() {
            return "Statistic{" +
                    "minValue=" + minValue +
                    ", sumValue=" + sumValue +
                    ", maxValue=" + maxValue +
                    '}';
        }
    }

    @Getter
    public static class Node {
        static Random RND = new Random();
        int priority;
        Node left;
        Node right;
        int size;
        Long value;
        int addPromise;
        boolean isReversed;
        Statistic statistic = new Statistic();

        public Node(java.lang.Long value) {
            this(value, RND.nextInt());
        }

        public Node(java.lang.Long value, int priority) {
            this(value, priority, null, null);
        }

        public Node(java.lang.Long value, int priority, Node left, Node right) {
            this.priority = priority;
            this.left = left;
            this.right = right;
            this.value = value;
            recalculate();
        }

        public void recalculate() {
            size = sizeOf(left) + sizeOf(right) + 1;
            setMinValue(minOf(minOf(minValueOf(left), minValueOf(right)), value));
            setMaxValue(maxOf(maxOf(maxValueOf(left), maxValueOf(right)), value));
            setSumValue(sumValueOf(left) + sumValueOf(right) + value);
        }

        private static java.lang.Long valueOf(Node node) {
            return node != null ? node.value + node.addPromise : null;
        }

        private static java.lang.Long minValueOf(Node node) {
            return node != null ? node.statistic.minValue : null;
        }

        private static java.lang.Long maxValueOf(Node node) {
            return node != null ? node.statistic.maxValue : null;
        }

        private static java.lang.Long sumValueOf(Node node) {
            return node != null ? node.statistic.sumValue : 0;
        }

        private static long minOf(java.lang.Long a, java.lang.Long b) {
            if (a != null && b != null) {
                return Math.min(a, b);
            }
            if (a != null) {
                return a;
            }
            if (b != null) {
                return b;
            }
            return java.lang.Long.MAX_VALUE;
        }

        private static long maxOf(java.lang.Long a, java.lang.Long b) {
            if (a != null && b != null) {
                return Math.max(a, b);
            }
            if (a != null) {
                return a;
            }
            if (b != null) {
                return b;
            }
            return java.lang.Long.MIN_VALUE;
        }

        public void setLeft(Node left) {
            this.left = left;
            recalculate();
        }

        public void setRight(Node right) {
            this.right = right;

            recalculate();
        }

        public void setMinValue(long minValue) {
            statistic.minValue = minValue + addPromise;
        }

        public void setMaxValue(long maxValue) {
            statistic.maxValue = maxValue + addPromise;
        }

        public void setSumValue(long sumValue) {
            statistic.sumValue = sumValue + addPromise * size;
        }

        public Node[] split(int pos) {
            push(this);
            Node tmp = null;
            Node[] res = (Node[]) Array.newInstance(this.getClass(), 2);
            int curIndex = sizeOf(left) + 1;
            if (curIndex <= pos) {
                if (right != null) {
                    Node[] split = right.split(pos - curIndex);
                    tmp = split[0];
                    res[1] = split[1];
                }
                res[0] = new Node(value, priority, left, tmp);
            } else {
                if (left != null) {
                    Node[] split = left.split(pos);
                    tmp = split[1];
                    res[0] = split[0];
                }
                res[1] = new Node(value, priority, tmp, right);
            }
            return res;
        }

        @Override
        public String toString() {
            if (Treap.view == View.value) {
                return String.valueOf(value);
            }
            if (Treap.view == View.small) {
                return String.format("(%d/+%d %b)", valueOf(this), addPromise, isReversed);
            }
            if (Treap.view == View.medium) {
                return String.format("(%d,%d[%d]/+%d %b)", valueOf(this), priority, size, addPromise, isReversed);
            }
            return String.format("(%d[%d]/m%d s%d M%d +%d %b)", valueOf(this), size, statistic.minValue, statistic.sumValue, statistic.maxValue, addPromise, isReversed);
        }

        public void addToPromiseAdd(long add) {
            addPromise += add;
            recalculate();
        }

        public void setValue(long value) {
            this.value = value;
            recalculate();
        }

        public void pushPromise() {
            if (left != null) {
                left.addToPromiseAdd(addPromise);
                if (isReversed) {
                    left.reversePromise();
                }
            }
            if (right != null) {
                right.addToPromiseAdd(addPromise);
                if (isReversed) {
                    right.reversePromise();
                }
            }
            value += addPromise;
            addToPromiseAdd(-addPromise); //reset to zero and recalculate

            if (isReversed) {
                Node tmp = left;
                left = right;
                right = tmp;
                isReversed = false;
            }
        }

        private void reversePromise() {
            this.isReversed = !this.isReversed;
        }
    }

    public enum View {
        full,
        medium,
        small,
        value
    }

    private static class Result {
        private Long sum;

        public Result(Long sum) {
            this.sum = sum;
        }
    }
}