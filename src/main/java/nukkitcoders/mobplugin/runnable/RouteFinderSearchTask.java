package nukkitcoders.mobplugin.runnable;

import nukkitcoders.mobplugin.route.RouteFinder;

/**
 * @author zzz1999 @ MobPlugin
 */
public class RouteFinderSearchTask implements Runnable {

    private RouteFinder route;
    private int retryTime = 0;

    public RouteFinderSearchTask(RouteFinder route){
        route = route;
    }

    @Override
    public void run() {
        if (route == null) return;
        while (retryTime < 50) {
            if (!route.isSearching()) {
                route.research();
                return;
            } else {
                retryTime += 10;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {

                }
            }
        }
        route.interrupt();
    }
}
