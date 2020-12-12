package com.vhbob.airimines.util;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;
    private final HashMap<E, Double> chances = new HashMap<>();

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        chances.put(result, weight);
        return this;
    }

    public E next() {
        if (map.size() == 0) {
            return null;
        }
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public HashMap<E, Double> getChances() {
        return chances;
    }

}