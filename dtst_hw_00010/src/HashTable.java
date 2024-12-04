import java.util.Iterator;

public class HashTable<K, V> implements DictionaryInterface<K, V> {
    private TableEntry<K, V>[] hashTable;
    private int numberOfEntries;
    private int locationsUsed;
    private static final int DEFAULT_SIZE = 7;
    private double loadFactor = 0.75;
    private boolean isSSF = false; // Simple Summation Function kullanımı
    private boolean isDoubleHash = false; // Double Hashing kullanımı
	private long collisionCount = 0;


    public HashTable(double loadFactor, boolean isSSF, boolean isDoubleHash) {
        this(DEFAULT_SIZE);
        this.loadFactor = loadFactor;
        this.isSSF = isSSF;
        this.isDoubleHash = isDoubleHash;
    }

    @SuppressWarnings("unchecked")
    public HashTable(int tableSize) {
        int primeSize = getNextPrime(tableSize);
        hashTable = new TableEntry[primeSize];
        numberOfEntries = 0;
        locationsUsed = 0;
    }

    /**
     * Hash tablosunun yük faktörünü değiştirir.
     * @param //newLoadFactor Yeni yük faktörü (0 < newLoadFactor <= 1)
     */

    public long getCollisionCount() {
        return collisionCount;
    }

    @Override
    public V add(K key, V value) {
        V oldValue;
        int index = getHashIndex(key);
        index = probe(index, key);

        if ((hashTable[index] == null) || hashTable[index].isRemoved()) {
            hashTable[index] = new TableEntry<>(key, value);
            numberOfEntries++;
            locationsUsed++;
            oldValue = null;
        } else {
            oldValue = hashTable[index].getValue();
            hashTable[index].setValue(value);
        }

        if (isHashTableTooFull()) {
            rehash();
        }
        return oldValue;
    }

    @Override
    public V remove(K key) {
        V removedValue = null;
        int index = getHashIndex(key);
        index = locate(index, key);

        if (index != -1) {
            removedValue = hashTable[index].getValue();
            hashTable[index].setToRemoved();
            numberOfEntries--;
        }
        return removedValue;
    }

    @Override
    public V getValue(K key) {
        V result = null;
        int index = getHashIndex(key);
        index = locate(index, key);
        if (index != -1)
            result = hashTable[index].getValue();
        return result;
    }

    @Override
    public boolean contains(K key) {
        int index = getHashIndex(key);
        index = locate(index, key);
        return index != -1;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    @Override
    public void clear() {
        for (int index = 0; index < hashTable.length; index++) {
            hashTable[index] = null;
        }
        numberOfEntries = 0;
        locationsUsed = 0;
    }

    @Override
    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }

    @Override
    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    }


    private int doubleHashing(K key) {
        int primaryHash = Math.abs(polyAcc(key.toString()));
        int secondaryHash = 31 * (17 - (primaryHash % 17)); // 17 rastgele bir asal sayı
        return secondaryHash;
    }


    private int probe(int index, K key) {
        int probeCount = 0;
        int length = hashTable.length;

        while (hashTable[index] != null && hashTable[index].isIn()) {
            if (key.equals(hashTable[index].getKey())) {
                return index;
            }

            if (isDoubleHash) {
                index = (index + probeCount * doubleHashing(key)) % length;
            } else {
                index = (index + 1) % length; // Linear Probing
            }
            collisionCount++;
            probeCount++;
        }
        return index;
    }


    private int locate(int index, K key) {
        int probeCount = 0;
        while (hashTable[index] != null) {
            if (hashTable[index].isIn() && key.equals(hashTable[index].getKey())) {
                return index;
            }

            if (isDoubleHash) {
                index = (index + probeCount * doubleHashing(key)) % hashTable.length;
            } else {
                index = (index + 1) % hashTable.length; // Linear Probing
            }
            probeCount++;
        }
        return -1;
    }

    private int getHashIndex(K key) {
        int hashIndex;
        if (isSSF) {
            hashIndex = simpleSum(key.toString());
        } else {
            hashIndex = polyAcc(key.toString());
        }
        return Math.abs(hashIndex) % hashTable.length;
    }

    private int simpleSum(String key) {
        int sum = 0;
        for (int i = 0; i < key.length(); i++) {
            sum += key.charAt(i);
        }
        return sum;
    }

    private int polyAcc(String key) {
        int sum = 0;
        int primeBase = 13; // Sabit bir asal sayı
        int length = key.length();

        for (int i = 0; i < length; i++) {
            sum = (sum * primeBase + key.charAt(i)) % hashTable.length;
        }

        return Math.abs(sum); // Negatif sonuçları önlemek için Math.abs
    }



    private boolean isHashTableTooFull() {
        return (double) numberOfEntries / hashTable.length >= loadFactor;
    }

    @SuppressWarnings("unchecked")
    public void rehash() {
        TableEntry<K, V>[] oldTable = hashTable;
        int newSize = getNextPrime(2 * hashTable.length);
        hashTable = new TableEntry[newSize];
        numberOfEntries = 0;
        locationsUsed = 0;

        for (TableEntry<K, V> entry : oldTable) {
            if (entry != null && entry.isIn()) {
                add(entry.getKey(), entry.getValue());
            }
        }
    }

    private int getNextPrime(int num) {
        while (!isPrime(num)) {
            num++;
        }
        return num;
    }

    private boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private class TableEntry<S, T> {
        private S key;
        private T value;
        private boolean inTable;

        private TableEntry(S key, T value) {
            this.key = key;
            this.value = value;
            this.inTable = true;
        }

        private S getKey() {
            return key;
        }

        private T getValue() {
            return value;
        }

        private void setValue(T value) {
            this.value = value;
        }

        private boolean isIn() {
            return inTable;
        }

        private void setToRemoved() {
            this.inTable = false;
        }

        private boolean isRemoved() {
            return !inTable;
        }
    }

    private class KeyIterator implements Iterator<K> {
        private int currentIndex = 0;
        private int numberLeft = numberOfEntries;

        @Override
        public boolean hasNext() {
            return numberLeft > 0;
        }

        @Override
        public K next() {
            while (hashTable[currentIndex] == null || hashTable[currentIndex].isRemoved()) {
                currentIndex++;
            }
            numberLeft--;
            return hashTable[currentIndex++].getKey();
        }
    }

    private class ValueIterator implements Iterator<V> {
        private int currentIndex = 0;
        private int numberLeft = numberOfEntries;

        @Override
        public boolean hasNext() {
            return numberLeft > 0;
        }

        @Override
        public V next() {
            while (hashTable[currentIndex] == null || hashTable[currentIndex].isRemoved()) {
                currentIndex++;
            }
            numberLeft--;
            return hashTable[currentIndex++].getValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported.");
        }
    }
}

