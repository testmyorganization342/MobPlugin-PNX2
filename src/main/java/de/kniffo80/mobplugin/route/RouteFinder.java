package de.kniffo80.mobplugin.route;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import de.kniffo80.mobplugin.entities.WalkingEntity;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author zzz1999 @ MobPlugin
 */
public abstract class RouteFinder {
    protected ArrayList<Node> nodes = new ArrayList<>();
    protected boolean success = false;
    protected boolean searching = false;

    protected int current = 0;

    public WalkingEntity entity = null;

    protected Vector3 start;
    protected Vector3 destination;

    protected Level level;

    protected boolean interrupt = false;


    public boolean reachable = true;//TODO 埋葬深度

    public boolean arrived = false;

    RouteFinder(WalkingEntity entity){
        Objects.requireNonNull(entity,"RouteSeeker: entity can not be null");
        this.entity = entity;
        this.level = entity.getLevel();
    }

    public WalkingEntity getEntity(){
        return entity;
    }

    public Vector3 getStart(){
        return this.start;
    }

    public void setStart(Vector3 start){
        if(!this.isSearching()) {
            this.start = start;
        }
    }

    public Vector3 getDestination(){
        return this.destination;
    }

    public void setDestination(Vector3 destination){
        if(!this.isSearching()) {
            this.destination = destination;
        }
    }

    public boolean isSuccess(){
        return success;
    }

    public boolean isSearching(){
        return searching;
    }

    public void addNode(Node node){
        nodes.add(node);
    }

    public void addNode(ArrayList<Node> node){
        nodes.addAll(node);
    }

    public boolean isReachable(){
        return reachable;
    }

    public Node getCurrentNode(){
        if(nodes.isEmpty())return null;
        return nodes.get(current);
    }


    public Level getLevel(){
        return this.level;
    }

    public void setLevel(Level level){
        this.level = level;
    }

    public int getCurrent(){
        return this.current;
    }

    public boolean hasArrivedNode(Vector3 vec){
        if(getCurrentNode()!=null) {
            Vector3 cur = this.getCurrentNode().getVector3();

            return vec.getX() == cur.getX() && vec.getZ() == cur.getZ() && vec.getFloorY() == cur.getFloorY();
        }
        return false;
    }

    public void arrived(){
        this.arrived = true;
    }

    public boolean isArrived(){
        return arrived;
    }

    public void resetNodes(){
        this.nodes.clear();
        this.searching = false;
        this.success = false;
        this.current = 0;
        this.interrupt = false;
        this.arrived = false;
    }

    public abstract boolean search();

    public boolean research(){
        this.resetNodes();
        this.reachable = true;
        return this.success = this.search();
    }

    public boolean hasNext(){
        return this.current + 1 < nodes.size() && this.nodes.get(this.current)!= null;
    }

    public Vector3 next(){
        if(this.hasNext()){
            return this.nodes.get(++current).getVector3();//包括了起点所以直接从1开始
        }
        return null;
    }

    public boolean isInterrupted(){
        return this.interrupt;
    }

    public boolean interrupt(){
        return this.interrupt ^= true;
    }
}