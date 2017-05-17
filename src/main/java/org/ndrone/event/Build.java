package org.ndrone.event;

/**
 * @author Nicholas Drone on 5/17/17.
 */
public class Build
{
    private String branchName;
    private BuildType buildType;

    public Build()
    {
    }

    public Build(String branchName, BuildType buildType)
    {
        this.branchName = branchName;
        this.buildType = buildType;
    }

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    public BuildType getBuildType()
    {
        return buildType;
    }

    public void setBuildType(BuildType buildType)
    {
        this.buildType = buildType;
    }
}
