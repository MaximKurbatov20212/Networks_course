import java.net.DatagramPacket;
import java.util.Arrays;

public class QueueOfPackets {
    DatagramPacket[] packets;
    private int MAX_BUF = 10;
    private int numberOfPackets;
    private int start = 0;

    public QueueOfPackets() {
        numberOfPackets = 0;
        packets = new DatagramPacket[MAX_BUF];
    }

    // Returns
    public void addPacket(DatagramPacket packet) {
        DatagramPacket copy = new DatagramPacket(packet.getData().clone(), packet.getLength(), packet.getAddress(), packet.getPort());
        packets[((start + numberOfPackets) % MAX_BUF)] = copy;
        start = numberOfPackets == MAX_BUF ? start + 1 : start;
        if(numberOfPackets != MAX_BUF) {
            numberOfPackets++;
        }
    }

    public DatagramPacket getPacket() {
        if(numberOfPackets == 0) {
            return null;
        }
        numberOfPackets--;
        return packets[start];
    }

    public void printAllPackets() {
        for(int i = start; i < numberOfPackets + start; i++) {
            System.out.println(Arrays.toString(packets[i % MAX_BUF].getData()));
        }
    }
}