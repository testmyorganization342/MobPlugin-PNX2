package nukkitcoders.mobplugin.route;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import nukkitcoders.mobplugin.entities.WalkingEntity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zzz1999 @ MobPlugin
 */
public abstract class RouteFinder {
    protected ArrayList<Node> nodes = new ArrayList<>();
    protected boolean finished = false;
    protected boolean searching = false;

    protected int current = 0;

    public WalkingEntity entity;

    protected Vector3 start;
    protected Vector3 destination;

    protected Level level;

    protected boolean interrupt = false;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected boolean reachable = true;//TODO burial depth

    //public boolean arrived = false;

    RouteFinder(WalkingEntity entity){
        Objects.requireNonNull(entity,"RouteFinder: entity can not be null");
        entity = entity;
        level = entity.getLevel();
    }

    public WalkingEntity getEntity(){
        return entity;
    }

    public Vector3 getStart(){
        return start;
    }

    public void setStart(Vector3 start){
        if(!isSearching()) {
            start = start;
        }
    }

    public Vector3 getDestination(){
        return destination;
    }

    public void setDestination(Vector3 destination){
        destination = destination;
        if(isSearching()){
            interrupt = true;
            research();
        }
    }

    public boolean isFinished(){
        return finished;
    }

    public boolean isSearching(){
        return searching;
    }

    public void addNode(Node node){
        try {
            lock.writeLock().lock();
            nodes.add(node);
        }finally {
            lock.writeLock().unlock();
        }
    }

    public void addNode(ArrayList<Node> node){
        try{
            lock.writeLock().lock();
            nodes.addAll(node);
        }finally {
            lock.writeLock().unlock();
        }

    }

    public boolean isReachable(){
        return reachable;
    }

    public Node getCurrentNode(){
        try{
            lock.readLock().lock();
            if(hasCurrentNode()) {
                return nodes.get(current);
            }
            return null;
        }finally {
            lock.readLock().unlock();
        }

    }
    public boolean hasCurrentNode(){
        return current < nodes.size();
    }


    public Level getLevel(){
        return level;
    }

    public void setLevel(Level level){
        level = level;
    }

    public int getCurrent(){
        return current;
    }

    public boolean hasArrivedNode(Vector3 vec){
        try{
            lock.readLock().lock();
            if(hasNext() &&  getCurrentNode().getVector3()!=null) {
                Vector3 cur = getCurrentNode().getVector3();
                return vec.getX() == cur.getX() && vec.getZ() == cur.getZ()/* && vec.getFloorY() == cur.getFloorY()*/;
            }
            return false;
        }finally {
            lock.readLock().unlock();
        }
    }

    public void resetNodes(){
        try{
            lock.writeLock().lock();
            nodes.clear();
            current = 0;
            interrupt = false;
            destination = null;
        }finally {
            lock.writeLock().unlock();
        }
    }

    public abstract boolean search();

    public void research(){
        resetNodes();
        search();
    }

    public boolean hasNext(){
        return current + 1 < nodes.size() && nodes.get(current+1)!= null;
    }

    public Vector3 next(){
        try{
            lock.readLock().lock();
            if(hasNext()){
                return nodes.get(++current).getVector3();
            }
            return null;
        }finally {
            lock.readLock().unlock();
        }

    }

    public boolean isInterrupted(){
        return interrupt;
    }

    public boolean interrupt(){
        return interrupt ^= true;
    }
}
