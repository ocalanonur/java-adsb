package org.opensky.msg;

import org.opensky.libadsb.ModeSDecoder;
import org.opensky.libadsb.exceptions.BadFormatException;
import org.opensky.libadsb.exceptions.UnspecifiedFormatError;
import org.opensky.libadsb.msgs.*;
import org.opensky.libadsb.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opensky.libadsb.msgs.ModeSReply.subtype.EXTENDED_SQUITTER;

public class ADSBDecoder implements IADSBDecoder {

    private ModeSDecoder decoder = new ModeSDecoder();
    private SWSEnum sws;

    public ADSBDecoder(SWSEnum sws){
        this.sws = sws;
    }

    @Override
    public List<ModeSReply> decode(byte[] input) {
        List<ModeSReply> messages = new ArrayList<>();
        int downlinkFormat;
        int offset = 0;

        if(sws == SWSEnum.on) {
            while (offset < input.length) {
                byte ebf = input[offset + 1];
                if (ebf == 0x31) { // Mode-AC
                    //TODO
                }
                else if (ebf == 0x32) { // Mode-S Short Frame
                    long timeStemp = 0;
                    byte[] timeStempByte = Arrays.copyOfRange(input, offset + 2, offset + 8);
                    for (int i = 0; i < timeStempByte.length; i++) {
                        timeStemp = (timeStemp << 8) + (timeStempByte[i] & 0xff);
                    }
                    try {
                        ModeSReply ms = decoder.decode(Arrays.copyOfRange(input, offset + 9, offset + 16),
                                timeStemp,
                                input[offset + 8]);
                        messages.add(ms);
                    } catch (BadFormatException e) {
                        System.out.println("Malformed message! Skipping it. Message: " + e.getMessage());
                        return null;
                    } catch (UnspecifiedFormatError e) {
                        System.out.println("Unspecified message! Skipping it...");
                        return null;
                    }
                    offset += 16;
                }
                else if (ebf == 0x33) {  // Mode-S Long Frame
                    long timeStemp = 0;
                    byte[] timeStempByte = Arrays.copyOfRange(input, offset + 2, offset + 8);
                    for (int i = 0; i < timeStempByte.length; i++) {
                        timeStemp = (timeStemp << 8) + (timeStempByte[i] & 0xff);
                    }
                    downlinkFormat = (byte) (input[offset + 9] >> 3 & 0x1F);
                    if (downlinkFormat == 17 || downlinkFormat == 18) {
                        try {
                            ModeSReply ms = decoder.decode(Arrays.copyOfRange(input, offset + 9, offset + 23),
                                    timeStemp,
                                    input[offset + 8]);
                            messages.add(ms);
                        } catch (BadFormatException e) {
                            System.out.println("Malformed message! Skipping it. Message: " + e.getMessage());
                        } catch (UnspecifiedFormatError e) {
                            System.out.println("Unspecified message! Skipping it...");
                        }
                    } else {
                        System.out.println("Unknown downlink format with DF:" +
                                downlinkFormat + ". Input data: " +
                                tools.toHexString(Arrays.copyOfRange(input, offset, offset + 23)) + ". Skipping it...");
                    }
                    offset += 23;
                }
                else {
                    System.out.println("Unspecified message! Returned NULL");
                    return null;
                }
            }
        }

        return messages;
    }

    public SWSEnum getSws() {
        return sws;
    }

    public enum SWSEnum {
        open,
        on
    }

}
