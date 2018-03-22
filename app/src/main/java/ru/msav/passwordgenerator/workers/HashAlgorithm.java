package ru.msav.passwordgenerator.workers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by msav on 30.12.2017.
 */

public enum HashAlgorithm {
    MD5(0),
    SHA256(1);

    private int value;
    private static Map map = new HashMap<>();

    private HashAlgorithm(int value) {
        this.value = value;
    }

    static {
        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
            map.put(hashAlgorithm.value, hashAlgorithm);
        }
    }

    public static HashAlgorithm valueOf(int hashAlgorithm) {
        return (HashAlgorithm)map.get(hashAlgorithm);
    }

    public int getValue() {
        return this.value;
    }
}
