import java.util.Arrays;
import java.util.List;

public class Alist<T> implements ListInterface<T> {
    private T[] list;
    private int count; // Eleman sayısını tutar
    private int numberOfEntries;
    private boolean initialized = false;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;

    public Alist() {
        this(DEFAULT_CAPACITY);
    }

    public Alist(int initialCapacity) {
        if (initialCapacity < DEFAULT_CAPACITY)
            initialCapacity = DEFAULT_CAPACITY;
        else
            checkCapacity(initialCapacity);

        @SuppressWarnings("unchecked")
        T[] tempList = (T[]) new Object[initialCapacity + 1];
        list = tempList;
        numberOfEntries = 0;
        initialized = true;
    }
    public int size() {
        return count; // Mevcut eleman sayısını döndürür
    }

    @Override
    public void add(T newEntry) {
        checkInitialization();
        list[numberOfEntries + 1] = newEntry;
        numberOfEntries++;
        ensureCapacity();
    }

    @Override
    public void add(int newPosition, T newEntry) {
        checkInitialization();
        if ((newPosition >= 1) && (newPosition <= numberOfEntries + 1)) {
            if (newPosition <= numberOfEntries)
                makeRoom(newPosition);
            list[newPosition] = newEntry;
            numberOfEntries++;
            ensureCapacity();
        } else
            throw new IndexOutOfBoundsException(
                    "Given position of add's new entry is out of bounds.");
    }

    public void addAlphabetically(T newEntry) {
        checkInitialization();
        if (isEmpty()) {
            add(newEntry);
        } else {
            int index = 1;
            while (index <= numberOfEntries && newEntry.toString().compareTo(list[index].toString()) > 0) {
                index++;
            }
            add(index, newEntry);
        }
    }

    private void makeRoom(int newPosition) {
        int newIndex = newPosition;
        int lastIndex = numberOfEntries;
        for (int index = lastIndex; index >= newIndex; index--)
            list[index + 1] = list[index];
    }

    @Override
    public T remove(int givenPosition) {
        checkInitialization();
        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            T result = list[givenPosition];
            if (givenPosition < numberOfEntries)
                removeGap(givenPosition);
            numberOfEntries--;
            return result;
        } else
            throw new IndexOutOfBoundsException("Illegal position given to remove operation.");
    }

    private void removeGap(int givenPosition) {
        int removedIndex = givenPosition;
        int lastIndex = numberOfEntries;
        for (int index = removedIndex; index < lastIndex; index++)
            list[index] = list[index + 1];
    }

    @Override
    public void clear() {
        for (int index = 1; index <= numberOfEntries; index++) {
            list[index] = null;
        }
        numberOfEntries = 0;
    }

    @Override
    public T replace(int givenPosition, T newEntry) {
        checkInitialization();
        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            T originalEntry = list[givenPosition];
            list[givenPosition] = newEntry;
            return originalEntry;
        } else
            throw new IndexOutOfBoundsException("Illegal position given to replace operation.");
    }

    public boolean containsAll(List<String> elements) {
        for (String element : elements) {
            if (!this.contains((T)element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public T getEntry(int givenPosition) {
        checkInitialization();
        if ((givenPosition >= 1) && (givenPosition <= numberOfEntries)) {
            return list[givenPosition];
        } else
            throw new IndexOutOfBoundsException("Illegal position given to getEntry operation.");
    }

    @Override
    public T[] toArray() {
        checkInitialization();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[numberOfEntries];
        for (int i = 0; i < numberOfEntries; i++) {
            result[i] = list[i + 1];
        }
        return result;
    }

    @Override
    public boolean contains(T anEntry) {
        checkInitialization();
        boolean found = false;
        int index = 1;
        while (!found && (index <= numberOfEntries)) {
            if (anEntry.equals(list[index]))
                found = true;
            index++;
        }
        return found;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i < size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    @Override
    public T get(int index) {
        return getEntry(index); // Mevcut getEntry metodunu çağırır
    }


    @Override
    public int getLength() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    private void ensureCapacity() {
        int capacity = list.length - 1;
        if (numberOfEntries >= capacity) {
            int newCapacity = capacity * 2;
            checkCapacity(newCapacity);
            list = Arrays.copyOf(list, newCapacity + 1);
        }
    }

    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a list whose capacity exceeds maximum allowed.");
    }

    private void checkInitialization() {
        if (!initialized)
            throw new SecurityException("ArrayList object is not initialized properly.");
    }
}
