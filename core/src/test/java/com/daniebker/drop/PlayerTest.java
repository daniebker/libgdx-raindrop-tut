package com.daniebker.drop;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PlayerTest {
    Player player;

    @Before
    public void before() {
        player = new Player();
    }
    @Test
    public void testSetPositionDoesntDropBelow0() {
        player.setPosition(-1, 10);
        assertEquals(0, player.getPosition().x, 0);
    }

    @Test
    public void testSetPositionDoesntGoAboveScreenWidth() {
        player.setPosition(801, 10);
        assertEquals(736, player.getPosition().x, 0);
    }
}