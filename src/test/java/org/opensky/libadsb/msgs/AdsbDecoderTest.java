package org.opensky.libadsb.msgs;

import org.junit.Test;
import org.opensky.libadsb.exceptions.BadFormatException;
import org.opensky.libadsb.exceptions.UnspecifiedFormatError;
import org.opensky.libadsb.tools;
import org.opensky.msg.ADSBDecoder;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AdsbDecoderTest {

    final byte [] IS_MSG = {
            0x1a, 0x33,                                             //esb
            0x04,(byte) 0xfe,(byte) 0xb8, 0x00, 0x00, 0x00,         //timestemp
            0x1b,                                                   //signal Strength
            (byte) 0x8D,                                            //DF, CA
            0x4b,(byte) 0xcd,(byte) 0xf6,                           //ICAO
            0x23,                                                   //TC, CA
            0x4d,(byte) 0x84,(byte) 0xf1,(byte) 0x82, 0x08, 0x20,   //ME
            0x24,(byte) 0xba,(byte) 0xe2,                           //PI

            0x1a, 0x33,                                             //esb
            0x04,(byte) 0xfe,(byte) 0xb8, 0x00, 0x00, 0x00,         //timestemp
            0x1b,                                                   //signal Strength
            (byte) 0x8D,                                            //DF, CA
            0x06,(byte) 0xa0,(byte) 0xb0,                           //ICAO
            0x25,                                                   //TC, CA
            0x45,(byte) 0x44,(byte) 0xb7,(byte) 0xd0, 0x18, 0x20,   //ME
            (byte) 0xb1, 0x72,(byte) 0xa2,                          //PI

            0x1a, 0x33,                                             //esb
            0x04,(byte) 0xfe,(byte) 0xce, 0x00, 0x00, 0x00,         //timestemp
            0x1d,                                                   //signal Strength
            (byte) 0x8D,                                            //DF, CA
            0x4b,(byte) 0xa1,(byte) 0x89,                           //ICAO
            (byte) 0xf8,                                            //TC, CA
            0x03, 0x00, 0x02, 0x00, 0x40, 0x0e,                     //ME
            0x29,(byte) 0x0d,(byte) 0x24,                           //PI

            0x1a, 0x33,
            0x04,(byte) 0xfe,(byte) 0xce, 0x00, 0x00, 0x00,
            0x1d,
            (byte) 0x8D,
            0x4b,(byte) 0xa9,(byte) 0x99,
            (byte) 0x00,
            (byte) 0xb5, 0x00, 0x00, 0x00, 0x00, 0x00,
            (byte) 0xb4,(byte) 0x62,(byte) 0x94,
    };

    @Test
    public void test_adsb_decoder() throws BadFormatException, UnspecifiedFormatError {

        ADSBDecoder decoder = new ADSBDecoder(ADSBDecoder.SWSEnum.on);
        List<ModeSReply> msg = decoder.decode(IS_MSG);

        assertEquals(27, msg.get(0).getSignalStrength());
        assertEquals(5492055212032L, msg.get(0).getTimeStemp());
        assertEquals(17, msg.get(0).getDownlinkFormat());
        assertEquals("4bcdf6", tools.toHexString(msg.get(0).getIcao24()));
        assertEquals(ModeSReply.subtype.ADSB_IDENTIFICATION,msg.get(0).getType());
        assertEquals("24bae2", tools.toHexString(msg.get(0).getParity()));
        IdentificationMsg id_msg = (IdentificationMsg) msg.get(0);
        assertEquals(5, id_msg.getCapabilities());
        assertEquals("Large (75000 to 300000 lbs)",id_msg.getCategoryDescription());
        assertEquals("SXS1    ",String.valueOf(id_msg.getIdentity()));

        assertEquals(27, msg.get(1).getSignalStrength());
        assertEquals(5492055212032L, msg.get(1).getTimeStemp());
        assertEquals(17, msg.get(1).getDownlinkFormat());
        assertEquals("06a0b0", tools.toHexString(msg.get(1).getIcao24()));
        assertEquals(ModeSReply.subtype.ADSB_IDENTIFICATION,msg.get(1).getType());
        assertEquals("b172a2", tools.toHexString(msg.get(1).getParity()));
        id_msg = (IdentificationMsg) msg.get(1);
        assertEquals(5, id_msg.getCapabilities());
        assertEquals("Heavy (> 300000 lbs)",id_msg.getCategoryDescription());
        assertEquals("QTR74A  ",String.valueOf(id_msg.getIdentity()));

        assertEquals(29, msg.get(2).getSignalStrength());
        assertEquals(5492424310784L, msg.get(2).getTimeStemp());
        assertEquals(17, msg.get(2).getDownlinkFormat());
        assertEquals("4ba189", tools.toHexString(msg.get(2).getIcao24()));
        assertEquals(ModeSReply.subtype.ADSB_AIRBORN_STATUS_V2,msg.get(2).getType());
        assertEquals("290d24", tools.toHexString(msg.get(2).getParity()));


    }
}
