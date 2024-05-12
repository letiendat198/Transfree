package com.transfree.utils;

public class MessageType {
    public enum MESSAGE {
        ACK, //ACKNOWLEDGE
        ATH, //AUTH
        REQ, //REQUEST
        EOF, //END OF FILE
        END, //END
        BGN, //BEGIN
        BIN, //BINARY DATA
        NSM, //NO SUCH MESSAGE
        RFS, //REFUSE
        COM, //COMPLETE
    }

    public static MESSAGE parseType(String message) {
        if (message.equalsIgnoreCase("ACK")) return MESSAGE.ACK;
        else if (message.equalsIgnoreCase("COM")) return MESSAGE.COM;
        else if (message.equalsIgnoreCase("REQ")) return MESSAGE.REQ;
        else if (message.equalsIgnoreCase("EOF")) return MESSAGE.EOF;
        else if (message.equalsIgnoreCase("END")) return MESSAGE.END;
        else if (message.equalsIgnoreCase("BGN")) return MESSAGE.BGN;
        else if (message.equalsIgnoreCase("BIN")) return MESSAGE.BIN;
        else if (message.equalsIgnoreCase("ATH")) return MESSAGE.ATH;
        else if (message.equalsIgnoreCase("RFS")) return MESSAGE.RFS;
        else return MESSAGE.NSM;
    }

    public static String toString(MESSAGE message) {
        return switch (message) {
            case ACK -> "ACK";
            case COM -> "COM";
            case REQ -> "REQ";
            case EOF -> "EOF";
            case END -> "END";
            case BGN -> "BGN";
            case BIN -> "BIN";
            case ATH -> "ATH";
            case RFS -> "RFS";
            case NSM -> "NSM";
        };
    }
}
