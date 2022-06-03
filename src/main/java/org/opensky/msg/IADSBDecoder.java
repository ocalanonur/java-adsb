package org.opensky.msg;

import org.opensky.libadsb.msgs.ModeSReply;

import java.util.List;

public interface IADSBDecoder {

    public List<ModeSReply> decode(byte[] data);

}
