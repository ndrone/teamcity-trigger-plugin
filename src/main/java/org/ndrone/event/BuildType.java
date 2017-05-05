package org.ndrone.event;

/**
 * @author Nicholas Drone on 5/5/17.
 */
public class BuildType
{
    private String id;

    public BuildType()
    {
    }

    public BuildType(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
