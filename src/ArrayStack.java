import java.util.EmptyStackException;
import java.util.Arrays;

public class ArrayStack<T> implements MyStack<T> {
    private Object[] array;
    private int top;
    private static final int DEFAULT_CAPACITY = 10;

    public ArrayStack() {
        array = new Object[DEFAULT_CAPACITY];
        top = -1;
    }

    public ArrayStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        array = new Object[initialCapacity];
        top = -1;
    }

    @Override
    public void push(T item) {
        if (top == array.length - 1) {
            resize();
        }
        array[++top] = item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T item = (T) array[top];
        array[top] = null; 
        top--;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) array[top];
    }

    @Override
    public boolean isEmpty() { return top == -1; }

    @Override
    public int size() { return top + 1; }

    @Override
    public void clear() {
        Arrays.fill(array, 0, top + 1, null);
        top = -1;
    }

    private void resize() {
        Object[] newArray = new Object[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
    }
}