package com.hamsoft.reservation.profiles;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(Profiles.SingleTag.class)
class SingleTagTest {

    @Test
    void test() {
    }
}
