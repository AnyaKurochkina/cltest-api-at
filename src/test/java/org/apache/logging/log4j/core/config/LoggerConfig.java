//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.logging.log4j.core.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.impl.LocationAwareLogEventFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;
import ru.testit.utils.UniqueTest;
import tests.Tests;

@Plugin(
        name = "logger",
        category = "Core",
        printObject = true
)
public class LoggerConfig extends AbstractFilterable implements LocationAware {
    public static final String ROOT = "root";
    private static LogEventFactory LOG_EVENT_FACTORY = null;
    private List<AppenderRef> appenderRefs = new ArrayList();
    private final AppenderControlArraySet appenders = new AppenderControlArraySet();
    private final String name;
    private LogEventFactory logEventFactory;
    private Level level;
    private boolean additive = true;
    private boolean includeLocation = true;
    private LoggerConfig parent;
    private Map<Property, Boolean> propertiesMap;
    private final List<Property> properties;
    private final boolean propertiesRequireLookup;
    private final Configuration config;
    private final ReliabilityStrategy reliabilityStrategy;

    public LoggerConfig() {
        this.logEventFactory = LOG_EVENT_FACTORY;
        this.level = Level.ERROR;
        this.name = "";
        this.properties = null;
        this.propertiesRequireLookup = false;
        this.config = null;
        this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
    }

    public LoggerConfig(final String name, final Level level, final boolean additive) {
        this.logEventFactory = LOG_EVENT_FACTORY;
        this.name = name;
        this.level = level;
        this.additive = additive;
        this.properties = null;
        this.propertiesRequireLookup = false;
        this.config = null;
        this.reliabilityStrategy = new DefaultReliabilityStrategy(this);
    }

    protected LoggerConfig(final String name, final List<AppenderRef> appenders, final Filter filter, final Level level, final boolean additive, final Property[] properties, final Configuration config, final boolean includeLocation) {
        super(filter);
        this.logEventFactory = LOG_EVENT_FACTORY;
        this.name = name;
        this.appenderRefs = appenders;
        this.level = level;
        this.additive = additive;
        this.includeLocation = includeLocation;
        this.config = config;
        if (properties != null && properties.length > 0) {
            this.properties = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(properties, properties.length)));
        } else {
            this.properties = null;
        }

        this.propertiesRequireLookup = containsPropertyRequiringLookup(properties);
        this.reliabilityStrategy = config.getReliabilityStrategy(this);
    }

    private static boolean containsPropertyRequiringLookup(final Property[] properties) {
        if (properties == null) {
            return false;
        } else {
            for(int i = 0; i < properties.length; ++i) {
                if (properties[i].isValueNeedsLookup()) {
                    return true;
                }
            }

            return false;
        }
    }

    public Filter getFilter() {
        return super.getFilter();
    }

    public String getName() {
        return this.name;
    }

    public void setParent(final LoggerConfig parent) {
        this.parent = parent;
    }

    public LoggerConfig getParent() {
        return this.parent;
    }

    public void addAppender(final Appender appender, final Level level, final Filter filter) {
        this.appenders.add(new AppenderControl(appender, level, filter));
    }

    public void removeAppender(final String name) {
        AppenderControl removed = null;

        while((removed = this.appenders.remove(name)) != null) {
            this.cleanupFilter(removed);
        }

    }

    public Map<String, Appender> getAppenders() {
        return this.appenders.asMap();
    }

    protected void clearAppenders() {
        do {
            AppenderControl[] original = this.appenders.clear();
            AppenderControl[] var2 = original;
            int var3 = original.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                AppenderControl ctl = var2[var4];
                this.cleanupFilter(ctl);
            }
        } while(!this.appenders.isEmpty());

    }

    private void cleanupFilter(final AppenderControl ctl) {
        Filter filter = ctl.getFilter();
        if (filter != null) {
            ctl.removeFilter(filter);
            filter.stop();
        }

    }

    public List<AppenderRef> getAppenderRefs() {
        return this.appenderRefs;
    }

    public void setLevel(final Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level == null ? (this.parent == null ? Level.ERROR : this.parent.getLevel()) : this.level;
    }

    public LogEventFactory getLogEventFactory() {
        return this.logEventFactory;
    }

    public void setLogEventFactory(final LogEventFactory logEventFactory) {
        this.logEventFactory = logEventFactory;
    }

    public boolean isAdditive() {
        return this.additive;
    }

    public void setAdditive(final boolean additive) {
        this.additive = additive;
    }

    public boolean isIncludeLocation() {
        return this.includeLocation;
    }

    /** @deprecated */
    @Deprecated
    public Map<Property, Boolean> getProperties() {
        if (this.properties == null) {
            return null;
        } else {
            if (this.propertiesMap == null) {
                Map<Property, Boolean> result = new HashMap(this.properties.size() * 2);

                for(int i = 0; i < this.properties.size(); ++i) {
                    result.put(this.properties.get(i), ((Property)this.properties.get(i)).isValueNeedsLookup());
                }

                this.propertiesMap = Collections.unmodifiableMap(result);
            }

            return this.propertiesMap;
        }
    }

    public List<Property> getPropertyList() {
        return this.properties;
    }

    public boolean isPropertiesRequireLookup() {
        return this.propertiesRequireLookup;
    }

    @PerformanceSensitive({"allocation"})
    public void log(final String loggerName, final String fqcn, final Marker marker, final Level level, final Message data, final Throwable t) {
        List<Property> props = null;
        if (!this.propertiesRequireLookup) {
            props = this.properties;
        } else if (this.properties != null) {
            props = new ArrayList(this.properties.size());
            LogEvent event = Log4jLogEvent.newBuilder().setMessage(data).setMarker(marker).setLevel(level).setLoggerName(loggerName).setLoggerFqcn(fqcn).setThrown(t).build();

            for(int i = 0; i < this.properties.size(); ++i) {
                Property prop = (Property)this.properties.get(i);
                String value = prop.isValueNeedsLookup() ? this.config.getStrSubstitutor().replace(event, prop.getValue()) : prop.getValue();
                ((List)props).add(Property.createProperty(prop.getName(), value));
            }
        }

        LogEvent logEvent = this.logEventFactory instanceof LocationAwareLogEventFactory ? ((LocationAwareLogEventFactory)this.logEventFactory).createEvent(loggerName, marker, fqcn, this.requiresLocation() ? StackLocatorUtil.calcLocation(fqcn) : null, level, data, (List)props, t) : this.logEventFactory.createEvent(loggerName, marker, fqcn, level, data, (List)props, t);

        try {
            this.log(logEvent, LoggerConfig.LoggerConfigPredicate.ALL);
        } finally {
            ReusableLogEventFactory.release(logEvent);
        }

    }

    @PerformanceSensitive({"allocation"})
    public void log(final String loggerName, final String fqcn, final StackTraceElement location, final Marker marker, final Level level, final Message data, final Throwable t) {
        List<Property> props = null;
        if (!this.propertiesRequireLookup) {
            props = this.properties;
        } else if (this.properties != null) {
            props = new ArrayList(this.properties.size());
            LogEvent event = Log4jLogEvent.newBuilder().setMessage(data).setMarker(marker).setLevel(level).setLoggerName(loggerName).setLoggerFqcn(fqcn).setThrown(t).build();

            for(int i = 0; i < this.properties.size(); ++i) {
                Property prop = (Property)this.properties.get(i);
                String value = prop.isValueNeedsLookup() ? this.config.getStrSubstitutor().replace(event, prop.getValue()) : prop.getValue();
                ((List)props).add(Property.createProperty(prop.getName(), value));
            }
        }

        LogEvent logEvent = this.logEventFactory instanceof LocationAwareLogEventFactory ? ((LocationAwareLogEventFactory)this.logEventFactory).createEvent(loggerName, marker, fqcn, location, level, data, (List)props, t) : this.logEventFactory.createEvent(loggerName, marker, fqcn, level, data, (List)props, t);
        if(logEvent.getMessage().getFormattedMessage().equals("toStringProductStepFunc"))
            return;
        try {
            this.log(logEvent, LoggerConfig.LoggerConfigPredicate.ALL);

            Date date = new Date(logEvent.getTimeMillis());
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            String dateFormatted = formatter.format(date);

            String s = String.format("%s %s %s:%s - %s",
                    dateFormatted,
                    logEvent.getLevel(),
                    logEvent.getSource().getClassName(), logEvent.getSource().getLineNumber(),
                    logEvent.getMessage().getFormattedMessage());
            Tests.putAttachLog(s);

        } finally {
            ReusableLogEventFactory.release(logEvent);
        }

    }

    public void log(final LogEvent event) {
        this.log(event, LoggerConfig.LoggerConfigPredicate.ALL);
    }

    protected void log(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
        if (!this.isFiltered(event)) {
            this.processLogEvent(event, predicate);
        }

    }

    public ReliabilityStrategy getReliabilityStrategy() {
        return this.reliabilityStrategy;
    }

    private void processLogEvent(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
        event.setIncludeLocation(this.isIncludeLocation());
        if (predicate.allow(this)) {
            this.callAppenders(event);
        }

        this.logParent(event, predicate);
    }

    public boolean requiresLocation() {
        if (!this.includeLocation) {
            return false;
        } else {
            AppenderControl[] controls = this.appenders.get();
            LoggerConfig loggerConfig = this;

            while(loggerConfig != null) {
                AppenderControl[] var3 = controls;
                int var4 = controls.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    AppenderControl control = var3[var5];
                    Appender appender = control.getAppender();
                    if (appender instanceof LocationAware && ((LocationAware)appender).requiresLocation()) {
                        return true;
                    }
                }

                if (!loggerConfig.additive) {
                    break;
                }

                loggerConfig = loggerConfig.parent;
                if (loggerConfig != null) {
                    controls = loggerConfig.appenders.get();
                }
            }

            return false;
        }
    }

    private void logParent(final LogEvent event, final LoggerConfig.LoggerConfigPredicate predicate) {
        if (this.additive && this.parent != null) {
            this.parent.log(event, predicate);
        }

    }

    @PerformanceSensitive({"allocation"})
    protected void callAppenders(final LogEvent event) {
        AppenderControl[] controls = this.appenders.get();

        for(int i = 0; i < controls.length; ++i) {
            controls[i].callAppender(event);
        }

    }

    public String toString() {
        return Strings.isEmpty(this.name) ? "root" : this.name;
    }

    /** @deprecated */
    @Deprecated
    public static LoggerConfig createLogger(final String additivity, final Level level, @PluginAttribute("name") final String loggerName, final String includeLocation, final AppenderRef[] refs, final Property[] properties, @PluginConfiguration final Configuration config, final Filter filter) {
        if (loggerName == null) {
            LOGGER.error("Loggers cannot be configured without a name");
            return null;
        } else {
            List<AppenderRef> appenderRefs = Arrays.asList(refs);
            String name = loggerName.equals("root") ? "" : loggerName;
            boolean additive = Booleans.parseBoolean(additivity, true);
            return new LoggerConfig(name, appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation, config));
        }
    }

    @PluginFactory
    public static LoggerConfig createLogger(@PluginAttribute(value = "additivity",defaultBoolean = true) final boolean additivity, @PluginAttribute("level") final Level level, @Required(message = "Loggers cannot be configured without a name") @PluginAttribute("name") final String loggerName, @PluginAttribute("includeLocation") final String includeLocation, @PluginElement("AppenderRef") final AppenderRef[] refs, @PluginElement("Properties") final Property[] properties, @PluginConfiguration final Configuration config, @PluginElement("Filter") final Filter filter) {
        String name = loggerName.equals("root") ? "" : loggerName;
        return new LoggerConfig(name, Arrays.asList(refs), filter, level, additivity, properties, config, includeLocation(includeLocation, config));
    }

    /** @deprecated */
    @Deprecated
    protected static boolean includeLocation(final String includeLocationConfigValue) {
        return includeLocation(includeLocationConfigValue, (Configuration)null);
    }

    protected static boolean includeLocation(final String includeLocationConfigValue, final Configuration configuration) {
        if (includeLocationConfigValue == null) {
            LoggerContext context = null;
            if (configuration != null) {
                context = configuration.getLoggerContext();
            }

            if (context != null) {
                return !(context instanceof AsyncLoggerContext);
            } else {
                return !AsyncLoggerContextSelector.isSelected();
            }
        } else {
            return Boolean.parseBoolean(includeLocationConfigValue);
        }
    }

    protected final boolean hasAppenders() {
        return !this.appenders.isEmpty();
    }

    static {
        String factory = PropertiesUtil.getProperties().getStringProperty("Log4jLogEventFactory");
        if (factory != null) {
            try {
                Class<?> clazz = Loader.loadClass(factory);
                if (clazz != null && LogEventFactory.class.isAssignableFrom(clazz)) {
                    LOG_EVENT_FACTORY = (LogEventFactory)clazz.newInstance();
                }
            } catch (Exception var2) {
                LOGGER.error("Unable to create LogEventFactory {}", factory, var2);
            }
        }

        if (LOG_EVENT_FACTORY == null) {
            LOG_EVENT_FACTORY = (LogEventFactory)(Constants.ENABLE_THREADLOCALS ? new ReusableLogEventFactory() : new DefaultLogEventFactory());
        }

    }

    protected static enum LoggerConfigPredicate {
        ALL {
            boolean allow(final LoggerConfig config) {
                return true;
            }
        },
        ASYNCHRONOUS_ONLY {
            boolean allow(final LoggerConfig config) {
                return config instanceof AsyncLoggerConfig;
            }
        },
        SYNCHRONOUS_ONLY {
            boolean allow(final LoggerConfig config) {
                return !ASYNCHRONOUS_ONLY.allow(config);
            }
        };

        private LoggerConfigPredicate() {
        }

        abstract boolean allow(LoggerConfig config);
    }

    @Plugin(
            name = "root",
            category = "Core",
            printObject = true
    )
    public static class RootLogger extends LoggerConfig {
        public RootLogger() {
        }

        @PluginFactory
        public static LoggerConfig createLogger(@PluginAttribute("additivity") final String additivity, @PluginAttribute("level") final Level level, @PluginAttribute("includeLocation") final String includeLocation, @PluginElement("AppenderRef") final AppenderRef[] refs, @PluginElement("Properties") final Property[] properties, @PluginConfiguration final Configuration config, @PluginElement("Filter") final Filter filter) {
            List<AppenderRef> appenderRefs = Arrays.asList(refs);
            Level actualLevel = level == null ? Level.ERROR : level;
            boolean additive = Booleans.parseBoolean(additivity, true);
            return new LoggerConfig("", appenderRefs, filter, actualLevel, additive, properties, config, includeLocation(includeLocation, config));
        }
    }
}
