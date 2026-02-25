import java.util.EmptyStackException;

public class LinkedListStack<T> implements MyStack<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> top;
    private int size;

    public LinkedListStack() {
        top = null;
        size = 0;
    }

    @Override
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }

    @Override
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T item = top.data;
        top = top.next;
        size--;
        return item;
    }

    @Override
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return top.data;
    }

    @Override
    public boolean isEmpty() { return top == null; }

    @Override
    public int size() { return size; }

    @Override
    public void clear() {
        top = null; 
        size = 0;
    }
}