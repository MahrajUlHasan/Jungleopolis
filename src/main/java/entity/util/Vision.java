package entity.util;

import entity.Entity;

public interface Vision {

    int getVisRadius();
    Entity[] inVision();
    void markVisible();
}
