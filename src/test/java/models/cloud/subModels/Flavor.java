package models.cloud.subModels;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Flavor {
    @EqualsAndHashCode.Include
    @Getter
    public String name;
    public String id;
    public Data data;

    public int getMemory(){
        return data.memory;
    }

    public int getCpus(){
        return data.cpus;
    }

    public static class Data{
        public int cpus;
        public int memory;
    }

    @Override
    public String toString() {
        return String.format("{\"cpus\": %d, \"memory\": %d, \"name\":\"%s\", \"uuid\":\"%s\"}", data.cpus, data.memory, name, id);
    }
}
