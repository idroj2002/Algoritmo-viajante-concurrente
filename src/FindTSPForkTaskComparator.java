import java.util.Comparator;

public class FindTSPForkTaskComparator implements Comparator<Runnable> {

    @Override
    public int compare(Runnable o1, Runnable o2) {
        return Integer.compare(((FindTSPPoolTask) o2).getNode().getCost(), ((FindTSPPoolTask) o1).getNode().getCost());
    }
}
