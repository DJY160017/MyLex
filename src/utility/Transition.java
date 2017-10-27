package utility;

import java.util.HashMap;

/**
 *
 * 用于记录每条边的状态信息
 *
 * */
public class Transition {

    /**
     * 边上的标记
     */
    private String tag;

    /**
     * 该边的头顶点状态
     */
    private State head;

    /**
     * 该边的尾顶点状态
     */
    private State tail;

    public Transition(String tag, int headID, int tailID) {
        this.tag = tag;
        head = new State(headID);
        tail = new State(tailID);
        head.addNextState(tail);
        tail.addPreviousState(head);
    }

    public Transition(String tag, State head, State tail) {
        this.tag = tag;
        this.head = head;
        this.tail = tail;
        this.head.addNextState(tail);
        this.tail.addPreviousState(head);
    }

    public String getTag() {
        return tag;
    }

    public State getHead() {
        return head;
    }

    public State getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return head.toString()+" - "+tag+" - "+tail.toString();
    }

    public boolean isSame(Transition transition){
        if((this.head.getStateID() == transition.getHead().getStateID())&&
                (this.tail.getStateID() == transition.getTail().getStateID())&&
                (this.tag.equals(transition.getTag()))){
            return true;
        }
        return false;
    }
}
