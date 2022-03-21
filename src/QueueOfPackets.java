import java.net.DatagramPacket;

public class QueueOfPackets {
    DatagramPacket[] packets;
    private int MAX_BUF = 10;
    private int numberOfPackets;
    private int start = 0;

    public QueueOfPackets() {
        numberOfPackets = 0;
        packets = new DatagramPacket[MAX_BUF];
    }

    public void addPacket(DatagramPacket packet) {
        if(numberOfPackets == MAX_BUF) return;
        DatagramPacket copy = new DatagramPacket(packet.getData().clone(), packet.getLength(), packet.getAddress(), packet.getPort());
        packets[((start + numberOfPackets) % MAX_BUF)] = copy;
        numberOfPackets++;
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
            System.out.println(packets[i % MAX_BUF].getData().toString());
        }
    }
}