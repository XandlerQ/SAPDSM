package structure;

import agent.Agent;

public class Connection {
    private Agent ag1;
    private Agent ag2;

    public Connection() {
        this.ag1 = null;
        this.ag2 = null;
    }

    public Connection(Agent ag1, Agent ag2){
        this.ag1 = ag1;
        this.ag2 = ag2;
    }

    public boolean contains(Agent ag){ return this.ag1 == ag || this.ag2 == ag; }

    public Agent pairOf(Agent ag){
        if(contains(ag)){
            if(this.ag1 == ag)
                return this.ag2;
            else
                return this.ag1;
        }
        return null;
    }

    public Agent getFirst(){ return this.ag1; }

    public Agent getSecond(){ return this.ag2; }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        Connection arg = (Connection) obj;
        return (arg.getFirst() == this.ag1 && arg.getSecond() == this.ag2)
                ||(arg.getFirst() == this.ag2 && arg.getSecond() == this.ag1);
    }
}