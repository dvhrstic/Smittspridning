public class Individual {
    private int daysUntillImmune, x,y;

    private boolean sick, immune, dead;
    public Individual (){
        this.sick = false;
        this.immune = false;
        this.dead = false;
        this.daysUntillImmune = Integer.MAX_VALUE;
        this.x =-1;
        this.y=-1;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }

    public boolean isDead() {
        return this.dead;
    }

    public boolean isImmune() {
        return this.immune;
    }

    public boolean isSick() {
        return this.sick;
    }


    public int getDaysLeft(){
        return this.daysUntillImmune;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public void setDaysLeft(int daysLeft){
        this.daysUntillImmune = daysLeft;
    }

    public void setSick(){
        this.sick = true;
    }

    public void setImmune(){
        this.immune = true;
        this.sick = false;
    }

    public void setDead(){
        this.dead = true;
        this.sick = false;
    }

}
