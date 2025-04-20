package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task;

public class JobResult {
    public enum Status {
        START, COMPLETE;
    };

    public String id;
    public String executor_id;
    public int tag;
    public boolean redelivered;
    public Status status;
    public Double reward;
    public String error;
    public Integer return_code;
}