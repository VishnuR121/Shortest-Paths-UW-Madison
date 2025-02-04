// == CS400 Fall 2024 File Header Information ==
// Name: Vishnu Rallapalli
// Email: vrallapalli2@wisc.edu
// Group and Team: P2.3703
// Group TA: <name of your group's ta>
// Lecturer: Florian Heimerl
// Notes to Grader: <optional extra notes>

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    // Instance variables
    protected LinkedList<Pair>[] table; // Table to store key-value pairs
    private int size; // Number of key-value pairs in the hashtable
    private int capacity; // Capacity of the table

    // Inner class to represent key-value pairs
    protected class Pair {
        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    // Constructors
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
        this.capacity = capacity;
        this.table = new LinkedList[capacity];
        this.size = 0;
    }

    // Constructors
    public HashtableMap() {
        this(64); // Default capacity
    }

    // Helper method to compute the index for a given key
    private int getIndex(KeyType key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    /**
     * Adds a new key,value pair/mapping to this collection.
     * 
     * @param key   the key of the key,value pair
     * @param value the value that key maps to
     * @throws IllegalArgumentException if key already maps to a value
     * @throws NullPointerException     if key is null
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null)
            throw new NullPointerException("Key cannot be null.");

        // Get the index in the table using hashCode
        int index = getIndex(key);

        // Initialize the LinkedList at this index if it's null
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }

        // Check if the key already exists, if so throw an IllegalArgumentException
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                throw new IllegalArgumentException("Key already exists.");
            }
        }

        // Add the new key-value pair to the LinkedList
        table[index].add(new Pair(key, value));
        size++;

        // Rehash if load factor exceeds 80%
        if (getLoadFactor() >= 0.8) {
            resizeAndRehash();
        }
    }

    // Helper method to calculate load factor
    private double getLoadFactor() {
        return (double) size / capacity;
    }

    // Method to resize and rehash the table
    private void resizeAndRehash() {
        // Double the capacity
        capacity *= 2;
        LinkedList<Pair>[] newTable = new LinkedList[capacity];

        // Rehash all the existing key-value pairs to the new table
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                for (Pair pair : table[i]) {
                    int index = Math.abs(pair.key.hashCode()) % capacity;
                    if (newTable[index] == null) {
                        newTable[index] = new LinkedList<>();
                    }
                    newTable[index].add(pair);
                }
            }
        }

        // Assign the new table to the instance variable
        table = newTable;
    }

    /**
     * Checks whether a key maps to a value in this collection.
     * 
     * @param key the key to check
     * @return true if the key maps to a value, and false is the
     *         key doesn't map to a value
     */
    @Override
    public boolean containsKey(KeyType key) {
        int index = getIndex(key);

        if (table[index] == null) {
            return false;
        }

        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     * 
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this
     *                                collection
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        int index = getIndex(key);

        if (table[index] == null) {
            throw new NoSuchElementException("Key not found.");
        }

        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }
        throw new NoSuchElementException("Key not found.");
    }

    /**
     * Remove the mapping for a key from this collection.
     * 
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this
     *                                collection
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        int index = getIndex(key);

        if (table[index] == null) {
            throw new NoSuchElementException("Key not found.");
        }

        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                table[index].remove(pair);
                size--;
                return pair.value;
            }
        }

        throw new NoSuchElementException("Key not found.");
    }

    /**
     * Removes all key,value pairs from this collection.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        table = new LinkedList[capacity]; // Reset the table without changing capacity
        size = 0;
    }

    /**
     * Retrieves the number of keys stored in this collection.
     * 
     * @return the number of keys stored in this collection
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Retrieves this collection's capacity.
     * 
     * @return the size of te underlying array for this collection
     */
    @Override
    public int getCapacity() {
        return capacity;
    }

    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
     */
    @Override
    public List<KeyType> getKeys() {
        List<KeyType> keys = new ArrayList<>();

        // Traverse the table
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) { // If the current index in the table is not empty then proceed
                for (Pair pair : table[i]) { // Iterate through the LinkedList at this index
                    keys.add(pair.key); // Add the key to the list
                }
            }
        }

        return keys;
    }

    /**
     * Test for the put method.
     * Ensures that a key-value pair can be added to the hashtable.
     * Checks that duplicate keys throw an exception.
     */
    @Test
    public void testPut() {
        HashtableMap<String, Integer> map = new HashtableMap<>(10);
        map.put("one", 1);
        map.put("two", 2);

        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertThrows(IllegalArgumentException.class, () -> map.put("one", 10)); // Duplicate key
    }

    /**
     * Test for the containsKey method.
     * Ensures it correctly identifies keys that exist and doesn't falsely
     * recognize keys that do not exist.
     */
    @Test
    public void testContainsKey() {
        HashtableMap<String, Integer> map = new HashtableMap<>(10);
        map.put("one", 1);
        map.put("two", 2);

        assertTrue(map.containsKey("one"));
        assertFalse(map.containsKey("three"));
    }

    /**
     * Test for the get method.
     * Ensures values can be retrieved by valid keys and throws an exception
     * for invalid keys.
     */
    @Test
    public void testGet() {
        HashtableMap<String, Integer> map = new HashtableMap<>(10);
        map.put("one", 1);
        map.put("two", 2);

        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertThrows(NoSuchElementException.class, () -> map.get("three")); // Non-existing key
    }

    /**
     * Test for the remove method.
     * Ensures keys can be removed, their values are returned,
     * and the size is updated correctly.
     */
    @Test
    public void testRemove() {
        HashtableMap<String, Integer> map = new HashtableMap<>(10);
        map.put("one", 1);
        map.put("two", 2);

        assertEquals(1, map.remove("one"));
        assertFalse(map.containsKey("one"));
        assertEquals(1, map.getSize());
        assertThrows(NoSuchElementException.class, () -> map.remove("three")); // Non-existing key
    }

    /**
     * Test for the clear method.
     * Ensures the hashtable is cleared and size is reset to zero.
     */
    @Test
    public void testClear() {
        HashtableMap<String, Integer> map = new HashtableMap<>(10);
        map.put("one", 1);
        map.put("two", 2);
        map.clear();

        assertEquals(0, map.getSize());
        assertFalse(map.containsKey("one"));
        assertFalse(map.containsKey("two"));
    }
}
