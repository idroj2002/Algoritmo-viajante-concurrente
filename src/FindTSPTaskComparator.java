import java.util.Comparator;

public class FindTSPTaskComparator implements Comparator<Runnable> {

    @Override
    public int compare(Runnable o1, Runnable o2) {
        return Integer.compare(((FindTSPTask) o2).getNode().getCost(), ((FindTSPTask) o1).getNode().getCost());
    }
}
