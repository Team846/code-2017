package com.lynbrookrobotics.seventeen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementation of sending Magic Packets.
 * from http://www.jibble.org/wake-on-lan/WakeOnLan.java
 */
public class WakeOnLan {
    private static final int PORT = 9;

    public static void awaken(String mac) {
        getMacBytes(mac).ifPresent(macBytes -> {
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = null;
            try {
                address = InetAddress.getByName("10.8.46.255");
                System.out.println(address);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
                System.out.println(packet);
                DatagramSocket socket = new DatagramSocket();
                System.out.println(socket);
                socket.send(packet);
                socket.close();

                System.out.println("Wake-on-LAN packet sent.");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private static Optional<byte[]> getMacBytes(String macStr) throws IllegalArgumentException {
        String[] hex = macStr.split(Pattern.quote(":"));

        if (hex.length != 6) {
            System.out.println("Invalid MAC address, did not have 6 parts");
        } else {
            try {
                byte[] bytes = new byte[6];

                for (int i = 0; i < 6; i++) {
                    bytes[i] = (byte) Integer.parseInt(hex[i], 16);
                }

                return Optional.of(bytes);
            } catch (NumberFormatException numException) {
                System.out.println("Invalid hex digit in MAC address");
            }
        }

        return Optional.empty();
    }
}
