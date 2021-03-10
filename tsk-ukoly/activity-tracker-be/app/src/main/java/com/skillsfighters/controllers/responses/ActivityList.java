package com.skillsfighters.controllers.responses;

import com.skillsfighters.domain.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityList extends ArrayList<Activity> {
    public ActivityList(List<Activity> activities) {
        super(activities);
    }
}
