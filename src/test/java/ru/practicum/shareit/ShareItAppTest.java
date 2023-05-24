package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ShareItAppTest {
    @Test
    void main() {
        ShareItApp.main(new String[] {});
        assertThat(true, equalTo(true));
    }
}
