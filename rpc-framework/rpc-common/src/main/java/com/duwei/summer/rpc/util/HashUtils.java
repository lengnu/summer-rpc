package com.duwei.summer.rpc.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 16:19
 * @since: 1.0
 */
public class HashUtils {
    static final MessageDigest messageDigest;
    static final int HASH_BIT = 31;
    static final int HASH_MASK = ~(-1 << HASH_BIT);

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static int hashTo4Byte(String data) {
        byte[] digest = messageDigest.digest(data.getBytes());
        int res = 0;
        int index = 0;
        for (; index < digest.length - 3; index += 4) {
            int cur = digest[index];
            cur = (cur << 8) | digest[index + 1];
            cur = (cur << 8) | digest[index + 2];
            cur = (cur << 8) | digest[index + 3];
            res ^= cur;
        }

        int cur = 0;
        while (index < digest.length){
            cur = (cur << 8) | digest[index++];
        }
        res ^= cur;
        return res & HASH_MASK;
    }
}
