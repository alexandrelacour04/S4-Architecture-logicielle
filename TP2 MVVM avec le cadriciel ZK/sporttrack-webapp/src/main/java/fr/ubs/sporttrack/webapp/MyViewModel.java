package fr.ubs.sporttrack.webapp;

import fr.ubs.sporttrack.model.Activity;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel {

    private List<Activity> activities;
    private List<Activity> actList;
    private Activity selectedActivity;
    private String keyword;


    public MyViewModel(){
        
    }

	@Init
	public void init() {
		this.activities = new ArrayList<>();
        this.actList = new ArrayList<>();
        this.activities = new ActivityService().findAll();
        this.keyword = "";
        this.selectedActivity = null;
        this.actList.addAll(this.activities);
	}

	@Command
	@NotifyChange("actList")
	public void search() {
        this.actList.clear();
        for(Activity act: this.activities){
            if(act.getDescription().contains(this.keyword)){
                this.actList.add(act);
            }
        }
	}

    public void setKeyword(String k){
        this.keyword = k;
    }

    public String getKeyword(){
        return this.keyword;
    }

	public List<Activity> getActList() {
		return this.actList;
	}

    public void setSelectedActivity(Activity act){
        this.selectedActivity = act;
    }

    public Activity getSelectedActivity(){
        return this.selectedActivity;
    }

    public List<Activity> search (String keyword){
        this.activities = new ActivityService().search(keyword);
        return this.activities;
    }
}
