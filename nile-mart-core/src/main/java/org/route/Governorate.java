package org.route;

import lombok.Getter;

@Getter
public enum Governorate {
    // GREATER_CAIRO
    CAIRO(Zone.GREATER_CAIRO),
    GIZA(Zone.GREATER_CAIRO),
    QALYUBIA(Zone.GREATER_CAIRO),

    // ALEX_DELTA
    ALEXANDRIA(Zone.ALEX_DELTA),
    BEHEIRA(Zone.ALEX_DELTA),
    KAFR_EL_SHEIKH(Zone.ALEX_DELTA),
    DAKAHLIA(Zone.ALEX_DELTA),
    DAMIETTA(Zone.ALEX_DELTA),
    GHARBIA(Zone.ALEX_DELTA),
    MONUFIA(Zone.ALEX_DELTA),
    SHARQIA(Zone.ALEX_DELTA),

    // CANAL
    PORT_SAID(Zone.CANAL),
    ISMAILIA(Zone.CANAL),
    SUEZ(Zone.CANAL),

    // UPPER_EGYPT
    FAIYUM(Zone.UPPER_EGYPT),
    BENI_SUEF(Zone.UPPER_EGYPT),
    MINYA(Zone.UPPER_EGYPT),
    ASYUT(Zone.UPPER_EGYPT),
    SOHAG(Zone.UPPER_EGYPT),
    QENA(Zone.UPPER_EGYPT),
    LUXOR(Zone.UPPER_EGYPT),
    ASWAN(Zone.UPPER_EGYPT),

    // REMOTE
    MATROUH(Zone.REMOTE),
    NORTH_SINAI(Zone.REMOTE),
    SOUTH_SINAI(Zone.REMOTE),
    RED_SEA(Zone.REMOTE),
    NEW_VALLEY(Zone.REMOTE);

    private final Zone zone;

    Governorate(Zone zone) {
        this.zone = zone;
    }
}