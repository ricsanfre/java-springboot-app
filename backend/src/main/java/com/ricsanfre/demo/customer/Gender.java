package com.ricsanfre.demo.customer;

import java.util.Random;

public enum Gender {
    FEMALE, MALE;

    private static final Random PRNG = new Random();

    // Random Gender Generator
    public static Gender randomGender()  {
        Gender[] genders = values();
        return genders[PRNG.nextInt(genders.length)];
    }

}
