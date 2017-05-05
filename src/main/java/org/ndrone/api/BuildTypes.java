package org.ndrone.api;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Nicholas Drone on 5/4/17.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class BuildTypes
{
    @XmlElement
    private int             count;
    @XmlElement
    private String          href;
    @XmlElementWrapper
    private List<BuildType> buildType;

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public List<BuildType> getBuildType()
    {
        return buildType;
    }

    public void setBuildType(List<BuildType> buildType)
    {
        this.buildType = buildType;
    }
}
