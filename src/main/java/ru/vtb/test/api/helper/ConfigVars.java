package ru.vtb.test.api.helper;


import org.aeonbits.owner.Config;

@Config.Sources({"classpath:application.properties"})
public interface ConfigVars extends Config {

     @Key("host")
     String host();

     @Key("dbUrl")
     String dbUrl();

     @Key("dbUser")
     String dbUser();
     @Key("dbPassword")
     String dbPassword();

     @Key("mqUser")
     String mqUser();
     @Key("mqPassword")
     String mqPassword();
}