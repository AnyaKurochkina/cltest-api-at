package models.interfaces;

public interface IProduct {

    public void order(String projectName);

    public void reset();

    public void stop(String method);

    public void start();

    public void delete();

}
