package cs.tcd.ie;

import java.net.DatagramPacket;

public interface PacketContent {
	
	public static byte HEADERLENGTH = 12;
	public static final int BYTE_SIZE_OF_PORTS = 4;
	public static final int BYTE_SIZE_OF_SEQUENCE_NUMBER = 4;
	
	public String toString();
	public DatagramPacket toDatagramPacket();
}
