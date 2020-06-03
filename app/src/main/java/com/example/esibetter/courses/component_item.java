package com.example.esibetter.courses;

public class component_item {


    String moduleName;
    int modulePhoto;

    public component_item(String moduleName, int modulePhoto) {
        this.moduleName = moduleName;
        this.modulePhoto = modulePhoto;
    }


    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getModulePhoto() {
        return modulePhoto;
    }

    public void setModulePhoto(int modulePhoto) {
        this.modulePhoto = modulePhoto;
    }

}
