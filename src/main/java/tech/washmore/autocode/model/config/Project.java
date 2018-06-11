package tech.washmore.autocode.model.config;

import java.util.List;

public class Project {
    private String path;
    private String javaRoot;
    private String resourcesRoot;
    private Boolean underline2Camel;
    private List<String> exclude;
    private List<String> include;

    public String getResourcesRoot() {
        return resourcesRoot;
    }

    public void setResourcesRoot(String resourcesRoot) {
        this.resourcesRoot = resourcesRoot;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getJavaRoot() {
        return javaRoot;
    }

    public void setJavaRoot(String javaRoot) {
        this.javaRoot = javaRoot;
    }

    public Boolean getUnderline2Camel() {
        return underline2Camel;
    }

    public void setUnderline2Camel(Boolean underline2Camel) {
        this.underline2Camel = underline2Camel;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }
}
