package de.kniffo80.mobplugin.route;

import cn.nukkit.block.Block;
import cn.nukkit.level.particle.RedstoneParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import de.kniffo80.mobplugin.entities.WalkingEntity;
import de.kniffo80.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * @author zzz1999 @ MobPlugin
 */
public class WalkerRouteFinder extends SimpleRouteFinder {

    public final static int DIRECT_MOVE_COST = 10;
    public final static int OBLIQUE_MOVE_COST = 14;

    private PriorityQueue<Node> openList = new PriorityQueue<>();
    private ArrayList<Node> closeList = new ArrayList<>();

    private int searchLimit = 100;

    public WalkerRouteFinder(WalkingEntity entity) {
        super(entity);
        this.level = entity.getLevel();
    }

    public WalkerRouteFinder(WalkingEntity entity,Vector3 start){
        super(entity);
        this.level = entity.getLevel();
        this.start = start;
    }

    public WalkerRouteFinder(WalkingEntity entity,Vector3 start,Vector3 destination) {
        super(entity);
        this.level = entity.getLevel();
        this.start = start;
        this.destination = destination;
    }

    /**
     * Using Manhattan Distance.
     */
    private int calHeuristic(Vector3 pos1, Vector3 pos2){
        return  10 * (Math.abs(pos1.getFloorX() - pos2.getFloorX()) + Math.abs(pos1.getFloorZ() - pos2.getFloorZ())) +
                11 * Math.abs(pos1.getFloorY() - pos2.getFloorY());
    }

    @Override
    public boolean search() {
        this.success = false;
        this.searching = true;

        if(this.start == null || this.destination == null){
            this.searching = false;
            this.success = false;
            return false;
        }
        this.resetNodes();



        Node presentNode = new Node(start);
        closeList.add(presentNode);

        while(!isPositionOverlap(presentNode.getVector3(),destination) && searchLimit-- > 0) {
            putNeighborNodeIntoOpen(presentNode);
            if (openList.peek() != null) {
                closeList.add(presentNode = openList.poll());
            }else{
                //无可用路径
                this.nodes.add(new Node(destination));
                this.reachable = false;
                return false;
            }
            if(this.isInterrupted()){
                searchLimit = 0;
                this.searching = false;
                this.success = true;
            }
        }
        if(!presentNode.getVector3().equals(destination)){
            closeList.add(new Node(destination,presentNode,0,0));
        }
        ArrayList<Node> findingPath = getPathRoute();

        findingPath.forEach(node -> level.addParticle(new WaterParticle(node.getVector3())));

        findingPath = FloydSmooth(findingPath);

        findingPath.forEach(n->level.addParticle(new RedstoneParticle(n.getVector3().add(0,0.2,0))));

        this.addNode(findingPath);

        return true;
    }


    private Block getHighestUnder(Vector3 vector3, int limit){
        if(limit > 0){
            for(int y = vector3.getFloorY() ; y >= vector3.getFloorY() - limit ; y--){
                Block block = this.level.getBlock(vector3.getFloorX(),y,vector3.getFloorZ());
                if(isWalkable(block))return block;
            }
            return null;
        }
        for(int y = vector3.getFloorY() ; y >= 0 ; y--){
            Block block = this.level.getBlock(vector3.getFloorX(),y,vector3.getFloorZ());
            if(isWalkable(block))return block;
        }
        return null;
    }

    private boolean canWalkOn(Block block){
        return !(block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA);
    }

    private boolean isWalkable(Vector3 vector3){
        Block block = level.getBlock(vector3);
        return !block.canPassThrough() && canWalkOn(block);
    }

    private double getWalkableHorizontalOffset(Vector3 vector3){
        Block block = getHighestUnder(vector3,4);
        if(block!=null){
            int height = (int) Math.ceil(entity.getBoundingBox().getMaxY() - entity.getBoundingBox().getMinY());
            for(int i = height + block.getFloorY() ; i > block.getFloorX() ; i -- ){
                if(!level.getBlock(block.getFloorX(),i,block.getFloorZ()).canPassThrough())return -256;
            }
            return (block.getY() - vector3.getY()) + 1;
        }
        return -256;
    }

    public int getSearchLimit(){
        return searchLimit;
    }

    public void setSearchLimit(int limit){
        this.searchLimit = limit;
    }

    private void putNeighborNodeIntoOpen(Node node) {
        boolean N,E,S,W;
        /*    0 1 2  X
         * 0 NW N NE
         * 1  W O E
         * 2 WS S ES
         * Z
         */

        Vector3 vector3 = node.getVector3();

        double y;
        if(E = ((y = getWalkableHorizontalOffset(vector3.add(1,0,0))) != -256)){
            Vector3 vec = vector3.add(1,y,0);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, DIRECT_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ DIRECT_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(S = ((y = getWalkableHorizontalOffset(vector3.add(0,0,1))) != -256)){
            Vector3 vec = vector3.add(0,y,1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, DIRECT_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ DIRECT_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(W = ((y = getWalkableHorizontalOffset(vector3.add(-1,0,0))) != -256)){
            Vector3 vec = vector3.add(-1,y,0);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, DIRECT_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ DIRECT_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(N = ((y = getWalkableHorizontalOffset(vector3.add(0,0,-1))) != -256)){
            Vector3 vec = vector3.add(0,y,-1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, DIRECT_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ DIRECT_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(N && E && ((y = getWalkableHorizontalOffset(vector3.add(1,0,-1))) != -256)){
            Vector3 vec = vector3.add(1,y,-1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, OBLIQUE_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ OBLIQUE_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(E && S && ((y = getWalkableHorizontalOffset(vector3.add(1,0,1))) != -256)){
            Vector3 vec = vector3.add(1,y,1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, OBLIQUE_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ OBLIQUE_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(W && S && ((y = getWalkableHorizontalOffset(vector3.add(-1,0,1))) != -256)){
            Vector3 vec = vector3.add(-1,y,1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, OBLIQUE_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ OBLIQUE_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if(W && N && ((y = getWalkableHorizontalOffset(vector3.add(-1,0,-1))) != -256)){
            Vector3 vec = vector3.add(-1,y,-1);
            if(!isContainsInClose(vec)){
                Node nodeNear = getNodeInOpenByVector2(vec);
                if(nodeNear==null){
                    this.openList.offer(new Node(vec,node, OBLIQUE_MOVE_COST +node.getG(),calHeuristic(vec,destination)));
                }else{
                    if(node.getG()+ OBLIQUE_MOVE_COST < nodeNear.getG()){
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG()+ OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }
    }

    private Node getNodeInOpenByVector2(Vector3 vector2){
        for(Node node : this.openList){
            if(vector2.equals(node.getVector3())){
                return node;
            }
        }

        return null;
    }
    private boolean isContainsInOpen(Vector3 vector2){
        return getNodeInOpenByVector2(vector2)!=null;
    }

    private Node getNodeInCloseByVector2(Vector3 vector2){
        for(Node node : this.closeList){
            if(vector2.equals(node.getVector3())){
                return node;
            }
        }
        return null;
    }

    private boolean isContainsInClose(Vector3 vector2){
        return getNodeInCloseByVector2(vector2)!=null;
    }

    private boolean hasBarrier(Node node1, Node node2){
        return hasBarrier(node1.getVector3(),node2.getVector3());
    }

    private boolean hasBarrier(Vector3 pos1, Vector3 pos2){
        if(pos1.equals(pos2))return false;
        if(pos1.getY() != pos2.getY())return true;
        boolean traverseDirection = Math.abs(pos1.getX() - pos2.getX()) > Math.abs(pos1.getZ() - pos2.getZ());//true为横向遍历 false为纵向遍历
        if(traverseDirection){//横向遍历
            double loopStart = Math.min(pos1.getX(),pos2.getX());
            double loopEnd = Math.max(pos1.getX(),pos2.getX());
            ArrayList<Vector3> list = new ArrayList<>();
            for(double i = Math.ceil(loopStart); i <= Math.floor(loopEnd) ; i+= 1.0){
                list.add(new Vector3(i,pos1.getY(),Utils.calLinearFunction(pos1,pos2,i,Utils.ACCORDING_X_OBTAIN_Y)));
            }
            return hasBlocksAround(list);
        }else{//纵向遍历
            double loopStart = Math.min(pos1.getZ(),pos2.getZ());
            double loopEnd = Math.max(pos1.getZ(),pos2.getZ());
            ArrayList<Vector3> list = new ArrayList<>();
            for(double i = Math.ceil(loopStart); i <= Math.floor(loopEnd) ; i += 1.0){
                list.add(new Vector3(Utils.calLinearFunction(pos1,pos2,i,Utils.ACCORDING_Y_OBTAIN_X),pos1.getY(),i));
            }
            return hasBlocksAround(list);
        }

    }

    private boolean hasBlocksAround(ArrayList<Vector3> list){
        double halfX = (entity.getBoundingBox().getMaxX() - entity.getBoundingBox().getMinX()) / 2;
        double halfZ = (entity.getBoundingBox().getMaxZ() - entity.getBoundingBox().getMinZ()) / 2;
        for(Vector3 vector3 : list){
            AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - halfX,vector3.getY(),vector3.getZ() - halfZ,vector3.getX() + halfX,vector3.getY() + entity.getHeight(),vector3.getZ() + halfZ);
            if(this.level.getCollisionBlocks(bb,true).length != 0){
                return true;
            }
            if(!canWalkOn(level.getBlock(vector3.add(0,-1,0))))return true;
        }
        return false;
    }

    /**
     * 弗洛伊德路径平滑
     * @param array 路径
     * @return 平滑后的路径
     */
    private ArrayList<Node> FloydSmooth(ArrayList<Node> array){
        int index = 0;
        int current = 1;
        if(array.size() > 2){
            while(current < array.size()){
                if(!hasBarrier(array.get(index),array.get(current))){
                    current++;
                }else{
                    array.get(current-1).setParent(array.get(index));
                    index = current-1;
                    current++;
                }
            }
            current = array.size()-1;
            array.get(current).setParent(array.get(index));

            Node temp = array.get(array.size()-1);
            ArrayList<Node> tempL = new ArrayList<>();
            tempL.add(temp);
            while(temp.getParent()!=null){
                tempL.add((temp = temp.getParent()));
            }
            Collections.reverse(tempL);
            return tempL;
        }
        return array;
    }

    private ArrayList<Node> getPathRoute(){
        ArrayList<Node> nodes = new ArrayList<>();
        Node temp = closeList.get(closeList.size()-1);
        nodes.add(temp);
        while(!temp.getParent().getVector3().equals(start)){
            nodes.add(temp = temp.getParent());
        }
        nodes.add(temp.getParent());
        Collections.reverse(nodes);
        return nodes;

    }

    private boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_){
        return (int)vector2.getX() == (int)vector2_.getX()
                && (int)vector2.getZ() == (int)vector2_.getZ()
                && (int)vector2.getY() == (int)vector2_.getY();
    }

    public void resetNodes(){
        super.resetNodes();
        this.searchLimit = 100;
    }
}