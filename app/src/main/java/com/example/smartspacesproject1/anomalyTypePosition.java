package com.example.smartspacesproject1;

public class anomalyTypePosition
{
    private int type;
    private int position;

    public anomalyTypePosition(int type, int position)
    {
        this.type = type;
        this.position = position;
    }

    public int getType()
    {
        return type;
    }
    public int getPosition()
    {
        return position;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    public void setPosition(int position)
    {
        this.position = position;
    }
}
