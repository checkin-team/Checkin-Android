package com.alcatraz.admin.project_alcatraz.Social;

/**
 * Created by TAIYAB on 14-06-2018.
 */

public class MessageUnit {
    String message="Unknown",time="00:00:00";
    int sentorget=0;
    MessageUnit(String message,String time,int sentorget)
    {
        this.message=message;
        this.time=time;
        this.sentorget=sentorget;
    }
}
