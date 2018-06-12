package tech.washmore.autocode.model.config;

import java.util.List;

public class Config {
    private List<String> autoTypes;
    private Project project;
    private Db db;
    private Doc doc;
    private Model model;
    private DataType dataType;
    private DataFile dataFile;

    public List<String> getAutoTypes() {
        return autoTypes;
    }

    public void setAutoTypes(List<String> autoTypes) {
        this.autoTypes = autoTypes;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Db getDb() {
        return db;
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
