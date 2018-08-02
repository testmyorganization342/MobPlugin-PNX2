package nukkitcoders.mobplugin.route;

import cn.nukkit.math.Vector3;

import java.util.Objects;

/**
 * @author zzz1999 @ MobPlugin
 */
public class Node implements Comparable<Node> {
    private Vector3 vector3;
    private Node parent;
    private int G;
    private int H;
    private int F;

    Node(Vector3 vector3, Node parent, int G, int H) {
        vector3 = vector3;
        parent = parent;
        G = G;
        H = H;
        F = G + H;
    }

    Node(Vector3 vector3) {
        this(vector3, null, 0, 0);
    }


    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        parent = parent;
    }

    public int getG() {
        return G;
    }

    public void setG(int g) {
        G = g;
    }

    public int getH() {
        return H;
    }

    public void setH(int h) {
        H = h;
    }

    public int getF() {
        return F;
    }

    public void setF(int f) {
        F = f;
    }

    @Override
    public int compareTo(Node o) {
        Objects.requireNonNull(o);
        if (getF() != o.getF()) {
            return getF() - o.getF();
        }
        //Breaking ties
        //0.1 = 10.0/100
        double breaking;
        if ((breaking = getG() + (getH() * 0.1) - (o.getG() + (getH() * 0.1))) > 0) {
            return 1;
        } else if (breaking < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return vector3.toString() + "| G:" + G + " H:" + H + " F" + getF() + (parent != null ? "\tparent:" + String.valueOf(parent.getVector3()) : "");
    }

    public Vector3 getVector3() {
        return vector3;
    }

    public void setVector3(Vector3 vector3) {
        vector3 = vector3;
    }
}
