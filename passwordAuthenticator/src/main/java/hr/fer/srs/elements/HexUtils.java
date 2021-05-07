package hr.fer.srs.elements;

import static java.util.Objects.requireNonNull;

public class HexUtils {
    private static final String HEX_DIGITS = "0123456789abcdef";

    public static byte[] hextobyte(String keyText) {
        if(requireNonNull(keyText).length() % 2 != 0) throw new IllegalArgumentException("The given hex number should have an even length.");
        if(keyText.length() == 0) return new byte[0];
        if(!keyText.matches("[0-9a-fA-F]+")) throw new IllegalArgumentException("Invalid hex number given.");

        keyText = keyText.toLowerCase();

        byte[] bytearray = new byte[keyText.length() / 2];

        for (int i = 0, textLength = keyText.length(); i < textLength; i += 2)
            bytearray[i / 2] = (byte) ((HEX_DIGITS.indexOf(keyText.charAt(i)) << 4) + HEX_DIGITS.indexOf(keyText.charAt(i + 1)));

        return bytearray;
    }

    public static String bytetohex(byte[] bytearray) {
        requireNonNull(bytearray);
        StringBuilder sb = new StringBuilder();

        for (byte singleByte : bytearray)
            sb.append(HEX_DIGITS.charAt((singleByte & 0xF0) >> 4)).append((HEX_DIGITS.charAt(singleByte & 0x0F)));

        return sb.toString();
    }

}
