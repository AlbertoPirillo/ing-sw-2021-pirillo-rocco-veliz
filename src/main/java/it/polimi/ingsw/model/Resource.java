package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.InvalidKeyException;
import it.polimi.ingsw.exceptions.NegativeResAmountException;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements a map to easily pass any type of resource between entities
 * @see ResourceType See the avaliable keys
 */
public class Resource implements Serializable, Cloneable {

    /**
     * The map containing all the associated resources
     */
    private Map<ResourceType, Integer> map;

    /**
     *  Default constructor: creates an empty map
     */
    public Resource() {
        map = new HashMap<>();
    }

    /**
     * Creates a Resource object from a given map
     * Can be used to clone a resource
     * @param map   the map to create the object from
     */
    public Resource(Map<ResourceType, Integer> map) {
        this.map = new HashMap<>(map);
    }

    /**
     * Creates a map with the four storable resources, useful e.g. for depot
     * @param stoneAmount   the amount of stone
     * @param coinAmount    the amount of coin
     * @param shieldAmount  the amount of shield
     * @param servantAmount the amount of servant
     */
    public Resource(int stoneAmount, int coinAmount, int shieldAmount, int servantAmount) {
        map = new HashMap<>();
        map.put(ResourceType.STONE, stoneAmount);
        map.put(ResourceType.COIN, coinAmount);
        map.put(ResourceType.SHIELD, shieldAmount);
        map.put(ResourceType.SERVANT, servantAmount);
    }

    /**
     * Use to get a value from a key
     * @param key   the key of the requested value
     * @return  the amount of resources of the requested key
     * @throws InvalidKeyException  if the specified key isn't present in the map
     */
    public int getValue(ResourceType key) throws InvalidKeyException {
        if (!map.containsKey(key)) throw new InvalidKeyException();
        return map.get(key);
    }

    /**
     * Check if the map contains the ALL type resource
     * @return true if the map contains resources of ALL type, false otherwise
     */
    public boolean hasAllResources(){
        return map.containsKey(ResourceType.ALL);
    }

    /**
     * Use this method only to add a new key
     * @param key   the resource you want to add
     * @param value the value associated to that key
     * @throws NegativeResAmountException   if the value specified is negative
     */
    public void addResource(ResourceType key, int value) throws NegativeResAmountException {
        if (value < 0) throw new NegativeResAmountException();
        if (map.containsKey(key)) throw new KeyAlreadyExistsException();
        map.put(key, value);
    }

    /**
     * Use this method both to add or subtract to an already existing resource
     * @param key   the resource you want the value to be modified
     * @param value the amount of that resource that you want to add or subtract
     * @throws NegativeResAmountException   if this operation will make the resource have a negative value
     */
    public void modifyValue(ResourceType key, int value) throws NegativeResAmountException {
        if (!map.containsKey(key)) {
            //we are not throwing an exception here anymore.
            //throw new InvalidKeyException();
            //instead, if the key doesn't exist we create it
            map.put(key, 0);
        }
        int newValue = map.get(key) + value;
        if (newValue < 0) {
            throw new NegativeResAmountException();
        }
        map.put(key, newValue);
    }

    /**
     * Returns a copy of this.map field (it's a new object and not a reference)
     * @return  a copy of this.map field
     */
    public Map<ResourceType, Integer> getMap() {
        return new HashMap<>(map);
    }

    /**
     * Returns true if this has more resources than that
     * @param that  the resource you want to compare to
     * @return  true if this has more or the same resources than that, false otherwise
     */
    public boolean compare (Resource that) {
        for (ResourceType key: that.map.keySet()) {
            if ((this.map.get(key) == null) || (this.map.get(key) < that.map.get(key))) return false;
        }
        return true;
    }

    /**
     * Sums two Resources and returns a new object
     * @param that  the resource you want to sum with this
     * @return  the result of the sum operation
     */
    public Resource sum(Resource that) {
        Map<ResourceType, Integer> copy = new HashMap<>(that.map);
        this.map.forEach((k,v) -> copy.merge(k, v, Integer::sum));
        return new Resource(copy);
    }

    /**
     *  Get keySet of the current Map
     */
    public Set<ResourceType> keySet() {
        return this.map.keySet();
    }

    /**
     * Returns the total amount of resources contained in the map
     * @return  the total amount of resources contained in the map
     */
    public int getTotalAmount() {
        int amount = 0;
        for(ResourceType key: map.keySet()) {
            amount += map.get(key);
        }
        return amount;
    }

    /**
     * Removes the specified resource from the map
     * @param res   the resource you want to remove
     */
    public void removeResource(ResourceType res) {
        this.map.remove(res);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return map.equals(((Resource) o).map);
    }

    /**
     * Returns the amount of different type of resource with values greater than zero
     * @return   an int representing the actual size
     */
    public int getActualSize() {
        int size = 0;
        for (ResourceType key: map.keySet()) {
            if(map.get(key) != 0) size++;
        }
        return size;
    }

    /**
     * <p>Fancy toString() method</p>
     * <p>If a resource is empty, "Empty" is printed</p>
     * <p>Resources are separated with comma</p>
     * <p>Resources with value equal to zero are not printed</p>
     * @return  a String representing a human-readable representation of the object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.getActualSize() == 0) {
            sb.append("Empty");
        }
        else {
            int i = 0;
            for (ResourceType key : this.map.keySet()) {
                if (this.map.get(key) != 0) {
                    sb.append(key).append("x").append(this.map.get(key));
                    if (i < this.getActualSize() - 1) sb.append(", ");
                    i++;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Makes a deep copy of the object
     * @return a new Resource
     */
    @Override
    public Resource clone() {
        Resource clone = null;
        try {
           clone = (Resource) super.clone();
           clone.map = new HashMap<>(this.map);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}