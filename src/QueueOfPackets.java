import java.net.DatagramPacket;
import java.util.Arrays;

public class QueueOfPackets {
    DatagramPacket[] packets;
    private int MAX_BUF = 10;
    private int numberOfPackets;


    public QueueOfPackets() {
        numberOfPackets = 0;
        packets = new DatagramPacket[MAX_BUF];
    }

    // Returns
    public void addPacket(DatagramPacket packet) {
        if(numberOfPackets == MAX_BUF) {
            numberOfPackets--;
            for(int i = 0; i < MAX_BUF - 1; i++) {
                packets[i] = packets[i + 1];
            }
        }

        DatagramPacket copy = new DatagramPacket(packet.getData().clone(), packet.getLength(), packet.getAddress(), packet.getPort());
        packets[numberOfPackets] = copy;
        numberOfPackets++;
    }

    public DatagramPacket getPacket() {
        if(numberOfPackets == 0) {
            return null;
        }
        DatagramPacket packet = packets[0];
        for(int i = 0; i < numberOfPackets - 1; i++) {
            packets[i] = packets[i + 1];
        }
        numberOfPackets--;
        return packet;
    }

    public void printAllPackets() {
        for(int i = 0; i < numberOfPackets; i++) {
            System.out.println(Arrays.toString(packets[i].getData()));
        }
    }
}