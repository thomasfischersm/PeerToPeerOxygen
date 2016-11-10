package com.playposse.peertopeeroxygen.backend.util;

import com.playposse.peertopeeroxygen.backend.PeerToPeerOxygenEndPoint;

import java.util.Random;
import java.util.logging.Logger;

/**
 * A class that generates a sequence of unique letters and numbers that can be used as an invitation
 * code for a domain.
 */
public class InvitationCodeGenerator {

    private static final Logger log = Logger.getLogger(InvitationCodeGenerator.class.getName());

    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final long BASE = ALPHABET.length();

    private static final Random RANDOM = new Random();

    public static String generateCode() {
        return encode(Math.abs(RANDOM.nextLong()));
    }

    private static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while ( num > 0 ) {
            log.info("encode invitation code: " + num + " " + ((int) (num % BASE)));
            sb.append(ALPHABET.charAt((int) (num % BASE)));
            num /= BASE;
        }
        return sb.reverse().toString();
    }
}
